package com.ghtk.auction.service.impl;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import com.ghtk.auction.component.RedisTemplateFactory;
import com.ghtk.auction.dto.redis.AuctionBid;
import com.ghtk.auction.dto.redis.AuctionRedisResponse;
import com.ghtk.auction.dto.request.comment.CommentFilter;
import com.ghtk.auction.dto.response.auction.AuctionJoinResponse;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import com.ghtk.auction.dto.stomp.BidMessage;
import com.ghtk.auction.dto.stomp.CommentMessage;
import com.ghtk.auction.dto.stomp.NotifyMessage;
import com.ghtk.auction.entity.Auction;
import com.ghtk.auction.entity.Comment;
import com.ghtk.auction.entity.Product;
import com.ghtk.auction.entity.TimeHistory;
import com.ghtk.auction.entity.UserAuction;
import com.ghtk.auction.entity.UserAuctionHistory;
import com.ghtk.auction.event.AuctionEndEvent;
import com.ghtk.auction.event.AuctionRoomOpenEvent;
import com.ghtk.auction.event.AuctionStartEvent;
import com.ghtk.auction.event.BidEvent;
import com.ghtk.auction.event.CommentEvent;
import com.ghtk.auction.exception.AlreadyExistsException;
import com.ghtk.auction.exception.ForbiddenException;
import com.ghtk.auction.exception.NotFoundException;
import com.ghtk.auction.repository.AuctionRepository;
import com.ghtk.auction.repository.CommentRepository;
import com.ghtk.auction.repository.ProductRepository;
import com.ghtk.auction.repository.TimeHistoryRepository;
import com.ghtk.auction.repository.UserAuctionHistoryRepository;
import com.ghtk.auction.repository.UserAuctionRepository;
import com.ghtk.auction.repository.UserRepository;
import com.ghtk.auction.service.AuctionRealtimeService;

import jakarta.validation.ValidationException;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import lombok.AccessLevel;

@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AuctionRealtimeServiceImpl implements AuctionRealtimeService {
  final UserRepository userRepository;
  final UserAuctionRepository userAuctionRepository;
  final TimeHistoryRepository timeHistoryRepository;
  final AuctionRepository auctionRepository;
  final ProductRepository productRepository;
  final UserAuctionHistoryRepository userAuctionHistoryRepository;
  final CommentRepository commentRepository;

  final RedisTemplate<String, String> redisTemplate;
  final RedisTemplateFactory redisTemplateFactory;

  final ApplicationEventPublisher eventPublisher;
  
  @Override
  public List<Auction> getJoinableNotis(Long userId) {
    return getUserAuctions(userId).stream()
        .map(auctionId -> auctionRepository.findById(auctionId).orElseThrow(
            () -> new NotFoundException("Khong tim thay phien dau gia nao trung voi Id")
        ))
        .toList();
  }

  @Override
  public void checkControlJoin(Long userId, Long auctionId) {
    if (!isAuctionRoomOpen(auctionId)) {
      throw new ForbiddenException("phong dau gia chua mo");
    }
    if (!canUserJoinAuction(userId, auctionId)) {
      throw new ForbiddenException("Khong co quyen tham gia dau gia");
    }
  }

  @Override
  public void checkNotifJoin(Long userId, Long auctionId) {
    if (!isAuctionRoomOpen(auctionId)) {
      throw new ForbiddenException("phong dau gia chua mo");
    }
    if (!canUserJoinAuction(userId, auctionId)) {
      throw new ForbiddenException("Khong co quyen tham gia dau gia");
    }
  }
  
  @Override
  public void checkBidJoin(Long userId, Long auctionId) {
    if (!isAuctionActive(auctionId)) {
      throw new ForbiddenException("dau gia chua bat dau");
    }
    if (!canUserJoinAuction(userId, auctionId)) {
      throw new ForbiddenException("Khong co quyen tham gia dau gia");
    }
  }
  
  @Override
  public void checkCommentJoin(Long userId, Long auctionId) {
    if (!isAuctionActive(auctionId)) {
      throw new ForbiddenException("dau gia chua bat dau");
    }
    if (!canUserJoinAuction(userId, auctionId)) {
      throw new ForbiddenException("Khong co quyen tham gia dau gia");
    }
  }

	@Override
	public AuctionJoinResponse joinAuction(Long userId, Long auctionId) {
    if (!isAuctionRoomOpen(auctionId)) {
      throw new ForbiddenException("phong dau gia chua mo");
    }
    if (!canUserJoinAuction(userId, auctionId)) {
      throw new ForbiddenException("Khong co quyen tham gia dau gia");
    }
    if (!getUserLastJoinAuction(userId, auctionId).isEmpty()) {
      throw new AlreadyExistsException("da tham gia dau gia");
    }
    LocalDateTime joinTime = LocalDateTime.now();
    setUserLastJoinAuction(userId, auctionId, joinTime);
    return new AuctionJoinResponse(isAuctionActive(auctionId));
	}

  @Override
  public void leaveAuction(Long userId, Long auctionId) {
    UserAuction ua = userAuctionRepository.findByUserIdAndAuctionId(userId, auctionId);
    LocalDateTime joinTime = getUserLastJoinAuction(userId, auctionId).orElseThrow(
        () -> new ForbiddenException("Chua tham gia dau gia")
    );
    LocalDateTime leaveTime = LocalDateTime.now();
    TimeHistory entry = TimeHistory.builder()
        .userAuction(ua)
        .joinTime(joinTime)
        .outTime(leaveTime)
        .build();
    timeHistoryRepository.save(entry);
    deleteUserLastJoinAuction(userId, auctionId);
  }

  @Override
  public void checkBidding(Long userId, Long auctionId) {
    if (!isAuctionActive(auctionId)) {
      throw new ForbiddenException("Phien dau gia chua bat dau");
    }
    getUserLastJoinAuction(userId, auctionId).orElseThrow(() ->
        new ForbiddenException("Chua tham gia dau gia")
    );
  }

  @Override
  public void checkCommenting(Long userId, Long auctionId) {
    if (!isAuctionActive(auctionId)) {
      throw new ForbiddenException("Phien dau gia chua bat dau");
    }
    getUserLastJoinAuction(userId, auctionId).orElseThrow(() ->
        new ForbiddenException("Chua tham gia dau gia")
    );
  }

  @Override
  public void checkNotifying(Long userId, Long auctionId) {
    AuctionRedisResponse info = getAuctionRoom(auctionId).orElseThrow(
        () -> new NotFoundException("Khong tim thay phien dau gia")
    );
    if (info.getOwnerId() != userId) {
      throw new ForbiddenException("Khong co quyen thong bao");
    }
  }

  @Override
  public BidMessage getCurrentPrice(Long userId, Long auctionId) {
    if (!isAuctionActive(auctionId)) {
      throw new ForbiddenException("Phien dau gia chua bat dau");
    }
    BidMessage result = getAuctionLastPrice(auctionId).get();
    return result;
  }

  @Override
  public List<CommentMessage> getComments(Long userId, Long auctionId, CommentFilter filter) {
    if (!isAuctionActive(auctionId)) {
      throw new ForbiddenException("Phien dau gia chua bat dau");
    }
    var queryResult = (filter.getFrom() == null) 
        ? commentRepository.findAllByAuctionId(auctionId)
        : commentRepository.findAllByAuctionIdAndCreatedAtAfter(auctionId, filter.getFrom());
    return queryResult.stream()
        .map(comment -> new CommentMessage(
            comment.getId(),
            comment.getUserId(),
            comment.getContent(), 
            comment.getCreatedAt() 
        ))
        .toList();
  }
	
	@Override
	public synchronized BidMessage bid(Long userId, Long auctionId, Long bid) {
    if (!isAuctionActive(auctionId)) {
      throw new ForbiddenException("Phien dau gia chua bat dau");
    }
    if (bid == null || bid <= 0) {
      throw new ValidationException("Gia dau gia khong hop le");
    }
    AuctionRedisResponse info = getAuctionRoom(auctionId).orElseThrow(
        () -> new NotFoundException("Khong tim thay phien dau gia")
    );

    long lastPrice = getAuctionLastPrice(auctionId).get().getBid();

    boolean valid = bid >= info.getStartBid() 
        && bid >= lastPrice + info.getPricePerStep();

    if (!valid) {
      throw new ForbiddenException("Gia dau gia khong hop le");
    }
    
    LocalDateTime time = LocalDateTime.now();

    AuctionBid auctionBid = new AuctionBid(
      userId,
      bid,
      time
    );
    addBid(auctionId, auctionBid);
    BidMessage message = new BidMessage(userId, auctionBid.getBid(), auctionBid.getCreatedAt());
    setAuctionLastPrice(auctionId, message); 
    eventPublisher.publishEvent(new BidEvent(auctionId, userId, bid, time));
    return message;
	}

  @Override
  public CommentMessage comment(Long userId, Long auctionId, String message) {
    LocalDateTime time = LocalDateTime.now();

    Comment comment = Comment.builder()
        .auctionId(auctionId)
        .userId(userId)
        .content(message)
        .createdAt(LocalDateTime.now())
        .build();
    var saved = commentRepository.save(comment);
    
    eventPublisher.publishEvent(new CommentEvent(
        saved.getId(), auctionId, userId, message, time));

    return null;
  }

  @Override
  public NotifyMessage makeNotification(Long userId, Long auctionId, String message) {
    return new NotifyMessage(message, LocalDateTime.now());
  }

  // @Override
  // public List<BidResponse> getBids(Long userId, Long auctionId, BidFilter filter) {
  //   
  //   return null;
  // }

  @Override
  public void openAuctionRoom(Long auctionId) {
    if (isAuctionRoomOpen(auctionId)) {
      throw new ForbiddenException("Phong dau gia da mo");
    }

    setAuctionRoomOpen(auctionId);

    Auction auction = auctionRepository.findById(auctionId).orElseThrow(
        () -> new NotFoundException("Khong tim thay phien dau gia nao trung voi Id")
    );
    List<UserAuction> userAuctions = userAuctionRepository.findAllByAuction(auction);
    userAuctions.stream()
        .forEach(userAuction 
            -> addUserToAuction(userAuction.getUser().getId(), auctionId)); 

    eventPublisher.publishEvent(new AuctionRoomOpenEvent(auctionId));
  }

  @Override
  public void startAuction(Long auctionId) {
    if (isAuctionActive(auctionId)) {
      throw new ForbiddenException("Phien dau gia da bat dau");
    }
    setAuctionActive(auctionId);
    setAuctionLastPrice(auctionId, new BidMessage(null, 0L, LocalDateTime.now()));
    eventPublisher.publishEvent(new AuctionStartEvent(auctionId));
  }

  @Override
  public void endAuction(Long auctionId) {
    if (!isAuctionRoomOpen(auctionId)) {
      throw new ForbiddenException("Phien dau gia chua bat dau");
    }

    Auction auction = auctionRepository.findById(auctionId).orElseThrow(
        () -> new NotFoundException("Khong tim thay phien dau gia nao trung voi Id")
    );

    if (isAuctionActive(auctionId)) {
      deleteAuctionActive(auctionId);

      Product product = auction.getProduct();
      long lastPrice = getAuctionLastPrice(auctionId).get().getBid();
      List<AuctionBid> bids = getBids(auctionId);

      if (!bids.isEmpty()) {
        AuctionBid lastBid = bids.get(0);
        if (lastBid.getBid() != lastPrice) {
          log.info("Gia cuoi cung khong khop");
        }
        auction.setEndBid(lastPrice);
        product.setBuyerId(lastBid.getUserId());
        productRepository.save(product);
        auctionRepository.save(auction);
      }

      deleteAuctionLastPrice(auctionId);

      bids.forEach(auctionBid -> {
        UserAuction ua = userAuctionRepository.findByUserIdAndAuctionId(auctionBid.getUserId(), auctionId);
        userAuctionHistoryRepository.save(
            UserAuctionHistory.builder()
                .userAuction(ua)
                .bid(auctionBid.getBid())
                .time(auctionBid.getCreatedAt())
                .build()
        );
      });

      deleteBids(auctionId);
    }
    setAuctionRoomClosed(auctionId);

    List<UserAuction> userAuctions = userAuctionRepository.findAllByAuction(auction);
    userAuctions.stream()
        .forEach(userAuction 
            -> removeUserFromAuction(userAuction.getUser().getId(), auctionId));

    eventPublisher.publishEvent(new AuctionEndEvent(auctionId));
  }


  private String getAuctionRoomKey(Long auctionId) {
    return String.format("auction:%d", auctionId);
  }

  private boolean isAuctionRoomOpen(Long auctionId) {
    String key = getAuctionRoomKey(auctionId);
    return redisTemplate.hasKey(key);
  }

  private Optional<AuctionRedisResponse> getAuctionRoom(Long auctionId) {
    String key = getAuctionRoomKey(auctionId);
    var template = redisTemplateFactory.get(AuctionRedisResponse.class);
    return Optional.ofNullable(template.opsForValue().get(key));
  }

  private void setAuctionRoomOpen(Long auctionId) {
    String key = getAuctionRoomKey(auctionId);
    var template = redisTemplateFactory.get(AuctionRedisResponse.class);
    Auction auction = auctionRepository.findById(auctionId).orElseThrow(
          () -> new NotFoundException("Khong tim thay auction hop le voi Id")
    );
    Product product = auction.getProduct();
    AuctionRedisResponse value = AuctionRedisResponse.builder()
          .ownerId(product.getOwnerId())
          .startBid(auction.getStartBid())
          .pricePerStep(auction.getPricePerStep())
          .build();
    template.opsForValue().set(key, value);
  }

  private void setAuctionRoomClosed(Long auctionId) {
    String key = getAuctionRoomKey(auctionId);
    var template = redisTemplateFactory.get(AuctionRedisResponse.class);
    template.delete(key);
  }
  

  private String getAuctionActiveKey(Long auctionId) {
    return String.format("auction:%d:active", auctionId);
  }

  private void setAuctionActive(Long auctionId) {
    String value = "ok";
    String key = getAuctionActiveKey(auctionId);
    redisTemplate.opsForValue().set(key,value);
  }

  private boolean isAuctionActive(Long auctionId) {
    String key = getAuctionActiveKey(auctionId);
    return redisTemplate.hasKey(key);
  }

  private void deleteAuctionActive(Long auctionId) {
    String key = getAuctionActiveKey(auctionId);
    redisTemplate.delete(key);
  }
  

  private String getUserAuctionKey(Long userId) {
    return String.format("user:%d:auction", userId);
  }

  private List<Long> getUserAuctions(Long userId) {
    String key = getUserAuctionKey(userId);
    return redisTemplate.opsForSet().members(key).stream()
        .map(Long::parseLong)
        .toList();
  }

  private boolean canUserJoinAuction(Long userId, Long auctionId) {
    String key = getUserAuctionKey(userId);
    return redisTemplate.opsForSet().isMember(key, auctionId.toString());
  }

  private void addUserToAuction(Long userId, Long auctionId) {
    String key = getUserAuctionKey(userId);
    redisTemplate.opsForSet().add(key, auctionId.toString());
  }

  private void removeUserFromAuction(Long userId, Long auctionId) {
    String key = getUserAuctionKey(userId);
    redisTemplate.opsForSet().remove(key, auctionId.toString());
  }

  private String getLastJoinKey(Long userId, Long auctionId) {
    return String.format("user:%d:auction:%d:last_join", userId, auctionId);
  }

  private Optional<LocalDateTime> getUserLastJoinAuction(Long userId, Long auctionId) {
    String key = getLastJoinKey(userId, auctionId);
    return Optional.ofNullable(redisTemplate.opsForValue().get(key))
        .map(LocalDateTime::parse);
  }

  private void setUserLastJoinAuction(Long userId, Long auctionId, LocalDateTime time) {
    String key = getLastJoinKey(userId, auctionId);
    redisTemplate.opsForValue().set(key, time.toString());
  }

  private void deleteUserLastJoinAuction(Long userId, Long auctionId) {
    String key = getLastJoinKey(userId, auctionId);
    redisTemplate.delete(key);
  }


  private String getLastPriceKey(Long auctionId) {
    return String.format("auction:%d:last_price", auctionId);
  }

  private Optional<BidMessage> getAuctionLastPrice(long auctionId) {
    String key = getLastPriceKey(auctionId);
    var template = redisTemplateFactory.get(BidMessage.class);
    return Optional.ofNullable(template.opsForValue().get(key));
  }

  private void setAuctionLastPrice(long auctionId, BidMessage bid) {
    String key = getLastPriceKey(auctionId);
    redisTemplateFactory.get(BidMessage.class).opsForValue().set(key, bid);
  }

  private void deleteAuctionLastPrice(Long auctionId) {
    String key = getLastPriceKey(auctionId);
    redisTemplate.delete(key);
  }


  private String getAuctionBidsKey(Long auctionId) {
    return String.format("auction:%d:bids", auctionId);
  }

  private void addBid(Long auctionId, AuctionBid bid) {
    String key = getAuctionBidsKey(auctionId);
    var template = redisTemplateFactory.get(AuctionBid.class);
    template.opsForList().leftPush(key, bid);
  }

  private List<AuctionBid> getBids(Long auctionId) {
    String key = getAuctionBidsKey(auctionId);
    var template = redisTemplateFactory.get(AuctionBid.class);
    return template.opsForList().range(key, 0, -1);
  }

  private boolean deleteBids(Long auctionId) {
    String key = String.format("auction:%d:bids", auctionId);
    return redisTemplate.delete(key);
  }
}
