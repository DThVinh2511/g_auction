package com.ghtk.auction.service.impl;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import com.ghtk.auction.dto.redis.AuctionBid;
import com.ghtk.auction.dto.redis.AuctionRoom;
import com.ghtk.auction.dto.request.comment.CommentFilter;
import com.ghtk.auction.dto.response.auction.AuctionJoinResponse;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.util.Pair;
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
import com.ghtk.auction.repository.Custom.AuctionSessionRepository;
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

  final AuctionSessionRepository auctionSessionRepository;

  final ApplicationEventPublisher eventPublisher;
  
  @Override
  public List<Auction> getJoinableNotis(Long userId) {
    return auctionSessionRepository.getJoinableByUser(userId).stream()
        .map(auctionId -> auctionRepository.findById(auctionId).orElseThrow(
            () -> new NotFoundException("Khong tim thay phien dau gia nao trung voi Id")
        ))
        .toList();
  }

  @Override
  public void checkControlJoin(Long userId, Long auctionId) {
    if (auctionSessionRepository.getAuctionRoom(auctionId).isEmpty()) {
      throw new ForbiddenException("phong dau gia chua mo");
    }
    if (!auctionSessionRepository.existsJoinable(auctionId, userId)) {
      throw new ForbiddenException("Khong co quyen tham gia dau gia");
    }
  }

  @Override
  public void checkNotifJoin(Long userId, Long auctionId) {
    if (auctionSessionRepository.getAuctionRoom(auctionId).isEmpty()) {
      throw new ForbiddenException("phong dau gia chua mo");
    }
    if (!auctionSessionRepository.existsJoinable(auctionId, userId)) {
      throw new ForbiddenException("Khong co quyen tham gia dau gia");
    }
  }
  
  @Override
  public void checkBidJoin(Long userId, Long auctionId) {
    if (!isAuctionStarted(auctionId)) {
      throw new ForbiddenException("dau gia chua bat dau");
    }
    if (!auctionSessionRepository.existsJoinable(auctionId, userId)) {
      throw new ForbiddenException("Khong co quyen tham gia dau gia");
    }
  }
  
  @Override
  public void checkCommentJoin(Long userId, Long auctionId) {
    if (!isAuctionStarted(auctionId)) {
      throw new ForbiddenException("dau gia chua bat day");
    }
    if (!auctionSessionRepository.existsJoinable(auctionId, userId)) {
      throw new ForbiddenException("Khong co quyen tham gia dau gia");
    }
  }

	@Override
	public AuctionJoinResponse joinAuction(Long userId, Long auctionId) {
    AuctionRoom room = auctionSessionRepository.getAuctionRoom(auctionId).orElseThrow(
        () -> new NotFoundException("phong dau gia chua mo")
    );
    if (!auctionSessionRepository.existsJoinable(auctionId, userId)) {
      throw new ForbiddenException("Khong co quyen tham gia dau gia");
    }
    if (auctionSessionRepository.getJoin(auctionId, userId).isPresent()) {
      throw new AlreadyExistsException("da tham gia dau gia");
    }
    auctionSessionRepository.addJoin(auctionId, userId);
    return new AuctionJoinResponse(room.isStarted());
	}

  @Override
  public void leaveAuction(Long userId, Long auctionId) {
    UserAuction ua = userAuctionRepository.findByUserIdAndAuctionId(userId, auctionId);
    LocalDateTime joinTime = auctionSessionRepository.getJoin(auctionId, userId).orElseThrow(
        () -> new ForbiddenException("Chua tham gia dau gia")
    );
    LocalDateTime leaveTime = LocalDateTime.now();
    auctionSessionRepository.deleteJoin(auctionId, userId);
    TimeHistory entry = TimeHistory.builder()
        .userAuction(ua)
        .joinTime(joinTime)
        .outTime(leaveTime)
        .build();
    timeHistoryRepository.save(entry);
  }

  @Override
  public void leaveAllAuctions(Long userId) {
    LocalDateTime leaveTime = LocalDateTime.now();

    List<Pair<Long, LocalDateTime>> joins = auctionSessionRepository.getJoinsByUser(userId);
    auctionSessionRepository.deleteAllJoinByUser(userId);

    List<TimeHistory> entries = new ArrayList<>();
    joins.forEach(pair -> {
      try {
        Long auctionId = pair.getFirst();
        LocalDateTime joinTime = pair.getSecond();
        UserAuction ua = userAuctionRepository.findByUserIdAndAuctionId(userId, auctionId);
        TimeHistory entry = TimeHistory.builder()
            .userAuction(ua)
            .joinTime(joinTime)
            .outTime(leaveTime)
            .build();
        entries.add(entry);
      } catch (Exception e) {
        log.error("Loi khi roi phien dau gia", e);
      }
    });
    timeHistoryRepository.saveAll(entries);
  }

  @Override
  public void checkBidding(Long userId, Long auctionId) {
    if (!isAuctionStarted(auctionId)) {
      throw new ForbiddenException("Phien dau gia chua bat dau");
    }
    auctionSessionRepository.getJoin(auctionId, userId).orElseThrow(() ->
        new ForbiddenException("Chua tham gia dau gia")
    );
  }

  @Override
  public void checkCommenting(Long userId, Long auctionId) {
    if (!isAuctionStarted(auctionId)) {
        throw new ForbiddenException("Phien dau gia chua bat dau");
    }
    auctionSessionRepository.getJoin(auctionId, userId).orElseThrow(() ->
        new ForbiddenException("Chua tham gia dau gia")
    );
  }

  @Override
  public void checkNotifying(Long userId, Long auctionId) {
    
  }

  @Override
  public BidMessage getCurrentPrice(Long userId, Long auctionId) {
    if (!isAuctionStarted(auctionId)) {
      throw new ForbiddenException("Phien dau gia chua bat dau");
    }
    BidMessage result = auctionSessionRepository.getLastBid(auctionId)
        .orElseGet(() -> {
            return new BidMessage(0L, 0L, null);
        });
    return result;
  }

  @Override
  public List<CommentMessage> getComments(Long userId, Long auctionId, CommentFilter filter) {
    if (!isAuctionStarted(auctionId)) {
      throw new ForbiddenException("Phien dau gia chua bat dau");
    }
    return auctionSessionRepository.getComments(auctionId, filter);
  }
	
	@Override
	public synchronized BidMessage bid(Long userId, Long auctionId, Long bid) {
    AuctionRoom info = auctionSessionRepository.getAuctionRoom(auctionId).orElseThrow(
        () -> new ForbiddenException("Phien dau gia chua bat dau")
    );
    if (!isAuctionStarted(auctionId)) {
      throw new ForbiddenException("Phien dau gia chua bat dau");
    }
    if (bid == null || bid <= 0) {
      throw new ValidationException("Gia dau gia khong hop le");
    }

    long lastPrice = auctionSessionRepository.getLastBid(auctionId)
        .map(BidMessage::getBid).orElse(0L);

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
    BidMessage message = new BidMessage(userId, auctionBid.getBid(), auctionBid.getCreatedAt());
    auctionSessionRepository.addBid(auctionId, message);
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
    auctionSessionRepository.getAuctionRoom(auctionId).ifPresent(room -> {
      throw new ForbiddenException("Phong dau gia da mo");
    });

    Auction auction = auctionRepository.findById(auctionId).orElseThrow(
        () -> new NotFoundException("Khong tim thay phien dau gia nao trung voi Id")
    );
    Product product = productRepository.findById(auction.getProduct().getId()).orElseThrow(
        () -> new NotFoundException("Khong tim thay san pham nao trung voi Id")
    );
    AuctionRoom room = AuctionRoom.builder()
        .ownerId(product.getOwnerId())
        .startBid(auction.getStartBid())
        .pricePerStep(auction.getPricePerStep())
        .isStarted(false)
        .build();
    auctionSessionRepository.setAuctionRoom(auctionId, room);

    Long[] userIds = userAuctionRepository.findUserIdsByAuctionId(auctionId).toArray(Long[]::new);
    auctionSessionRepository.addJoinable(auctionId, userIds); 

    eventPublisher.publishEvent(new AuctionRoomOpenEvent(auctionId));
  }

  @Override
  public void startAuction(Long auctionId) {
    AuctionRoom room = auctionSessionRepository.getAuctionRoom(auctionId).orElseThrow(
        () -> new NotFoundException("Phong dau gia chua mo")
    );
    if (room.isStarted()) {
      throw new ForbiddenException("Phien dau gia da bat dau");
    }
    room.setStarted(true);
    auctionSessionRepository.setAuctionRoom(auctionId, room);
    eventPublisher.publishEvent(new AuctionStartEvent(auctionId));
  }

  @Override
  public synchronized void endAuction(Long auctionId) {
    AuctionRoom room = auctionSessionRepository.getAuctionRoom(auctionId).orElseThrow(
        () -> new NotFoundException("Phong dau gia chua mo")
    );
    Auction auction = auctionRepository.findById(auctionId).orElseThrow(
        () -> new NotFoundException("Khong tim thay phien dau gia nao trung voi Id")
    );

    Long winner = auctionSessionRepository.getLastBid(auctionId)
        .map(BidMessage::getUserId).orElse(null);
    
    eventPublisher.publishEvent(new AuctionEndEvent(auctionId, winner));
    leaveAllAuctionUser(auctionId);
    auctionSessionRepository.deleteAllJoinableByAuction(auctionId);

    if (room.isStarted()) {
      Product product = auction.getProduct();
      long lastPrice = auctionSessionRepository.getLastBid(auctionId)
          .map(BidMessage::getBid).orElse(0L);
      List<BidMessage> bids = auctionSessionRepository.getBids(auctionId);

      if (!bids.isEmpty()) {
        BidMessage lastBid = bids.get(bids.size() - 1);
        if (lastBid.getBid() != lastPrice) {
          log.error("Gia cuoi cung khong khop");
        }

        product.setBuyerId(lastBid.getUserId());
        productRepository.save(product);

        auction.setEndBid(lastPrice);
        auctionRepository.save(auction);

        saveBids(auctionId, bids);
      } 
      
      auctionSessionRepository.deleteAllBids(auctionId);
      auctionSessionRepository.deleteAllComments(auctionId);
    } // if (room.isStarted())

    auctionSessionRepository.deleteAuctionRoom(auctionId);
  }

  private boolean isAuctionStarted(Long auctionId) {
    return auctionSessionRepository.getAuctionRoom(auctionId).filter(AuctionRoom::isStarted).isPresent();
  }

  private void saveBids(Long auctionId, List<BidMessage> bids) {
    userAuctionHistoryRepository.saveAll(
        bids.reversed().stream().map(auctionBid -> {
            UserAuction ua = userAuctionRepository.findByUserIdAndAuctionId(auctionBid.getUserId(), auctionId);
            return UserAuctionHistory.builder()
                .userAuction(ua)
                .bid(auctionBid.getBid())
                .time(auctionBid.getCreatedAt())
                .build();
        }).toList()
    );
  }

  private void leaveAllAuctionUser(Long auctionId) {
    LocalDateTime leaveTime = LocalDateTime.now();

    List<Pair<Long, LocalDateTime>> joins = auctionSessionRepository.getJoinsByAuction(auctionId);
    auctionSessionRepository.deleteAllJoinByAuction(auctionId);

    List<TimeHistory> entries = new ArrayList<>();
    joins.forEach(pair -> {
      try {
        Long userId = pair.getFirst();
        LocalDateTime joinTime = pair.getSecond();
        UserAuction ua = userAuctionRepository.findByUserIdAndAuctionId(userId, auctionId);
        TimeHistory entry = TimeHistory.builder()
            .userAuction(ua)
            .joinTime(joinTime)
            .outTime(leaveTime)
            .build();
        entries.add(entry);
      } catch (Exception e) {
        log.error("Loi khi roi phien dau gia", e);
      }
    });
    timeHistoryRepository.saveAll(entries);
  }
}
