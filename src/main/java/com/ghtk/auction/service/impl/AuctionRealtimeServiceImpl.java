package com.ghtk.auction.service.impl;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import com.ghtk.auction.dto.redis.AuctionBid;
import com.ghtk.auction.dto.redis.AuctionRedisResponse;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.MethodArgumentNotValidException;

import com.ghtk.auction.dto.response.auction.AuctionResponse;
import com.ghtk.auction.dto.stomp.BidMessage;
import com.ghtk.auction.entity.Auction;
import com.ghtk.auction.entity.Product;
import com.ghtk.auction.entity.TimeHistory;
import com.ghtk.auction.entity.User;
import com.ghtk.auction.entity.UserAuction;
import com.ghtk.auction.entity.UserAuctionHistory;
import com.ghtk.auction.enums.AuctionStatus;
import com.ghtk.auction.exception.AlreadyExistsException;
import com.ghtk.auction.exception.NotFoundException;
import com.ghtk.auction.repository.AuctionRepository;
import com.ghtk.auction.repository.ProductRepository;
import com.ghtk.auction.repository.TimeHistoryRepository;
import com.ghtk.auction.repository.UserAuctionHistoryRepository;
import com.ghtk.auction.repository.UserAuctionRepository;
import com.ghtk.auction.repository.UserRepository;
import com.ghtk.auction.service.AuctionRealtimeService;
import com.ghtk.auction.service.StompService;

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

  final StompService stompService;

  final RedisTemplate<String, String> redisTemplate;
  final RedisTemplate<String, Object> redisObjectTemplate;

  
  @Override
  public List<Auction> getJoinableNotis(Jwt principal) {
    Long userId = (Long)principal.getClaims().get("id");
    User user = userRepository.findById(userId).orElseThrow(
        () -> new NotFoundException("Khong tim thay nguoi dung")
    );
    List<UserAuction> userAuctions
        = userAuctionRepository.findAllByUserAndAuctionStatus(user, AuctionStatus.IN_PROGRESS);
    List<UserAuction> userAuctions2 
        = userAuctionRepository.findAllByUserAndAuctionStatus(user, AuctionStatus.CLOSED);
    userAuctions.addAll(userAuctions2);
    return userAuctions.stream()
        .map(UserAuction::getAuction)
        .filter(auction -> isAuctionRoomOpen(auction.getId()))
        .toList();
  }

  @Override
  public void checkNotifJoin(Jwt principal, Long auctionId) {
    if (!isAuctionRoomOpen(auctionId)) {
      // TODO: throw hop le
      throw new RuntimeException("phong dau gia chua mo");
    }
  }
  
  @Override
  public void checkBidJoin(Jwt principal, Long auctionId) {
    if (!isAuctionActive(auctionId)) {
      // TODO: throw hop le
      throw new RuntimeException("dau gia chua bat dau");
    }
  }
  
  @Override
  public void checkCommentJoin(Jwt principal, Long auctionId) {
    if (!isAuctionActive(auctionId)) {
      // TODO: throw hop le
      throw new RuntimeException("dau gia chua bat dau");
    }
  }

	@Override
	public void joinAuction(Jwt principal, Long auctionId) {
    Long userId = (Long)principal.getClaims().get("id");
    if (!isAuctionRoomOpen(auctionId)) {
      // TODO: throw hop le
      throw new RuntimeException("phong dau gia chua mo");
    }
    if (!getUserLastJoinAuction(userId, auctionId).isEmpty()) {
      throw new AlreadyExistsException("da tham gia dau gia");
    }
    LocalDateTime joinTime = LocalDateTime.now();
    setUserLastJoinAuction(userId, auctionId, joinTime);
	}

  @Override
  public void leaveAuction(Jwt principal, Long auctionId) {
    Long userId = (Long)principal.getClaims().get("id");
    UserAuction ua = userAuctionRepository.findByUserIdAndAuctionId(userId, auctionId);
    LocalDateTime joinTime = getUserLastJoinAuction(userId, auctionId).orElseThrow(
        () -> new RuntimeException("Chua tham gia dau gia")
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
  public Long getCurrentPrice(Jwt principal, Long auctionId) {
    if (!isAuctionActive(auctionId)) {
      // TODO: nem loi hop le
      throw new RuntimeException("Phien dau gia chua bat dau");
    }
    Long result = getAuctionLastPrice(auctionId).orElse(0L);
    return result;
  }
	
	@Override
	public synchronized BidMessage bid(Jwt principal, Long auctionId, Long bid) {
    if (bid == null || bid <= 0) {
      // tODO: throw hop le
      throw new RuntimeException("Gia dau gia khong hop le");
    }
    if (!isAuctionActive(auctionId)) {
      // TODO: nem loi hop le
      throw new RuntimeException("Phien dau gia chua bat dau");
    }
    AuctionRedisResponse info = getAuctionActive(auctionId).orElseThrow(
        () -> new RuntimeException("Khong tim thay phien dau gia")
    );

    long lastPrice = getAuctionLastPrice(auctionId).get();

    boolean valid = bid >= info.getStartBid() 
        && bid > lastPrice + info.getPricePerStep();

    if (!valid) {
      throw new RuntimeException("Gia dau gia khong hop le");
    }
    
    AuctionBid auctionBid = new AuctionBid(
      (Long)principal.getClaims().get("id"),
      bid,
      LocalDateTime.now()
    );
    addBid(auctionId, auctionBid);
    setAuctionLastPrice(auctionId, bid); 
    BidMessage message = new BidMessage(auctionBid.getBid(), auctionBid.getCreatedAt());
    stompService.broadcastBid(auctionId, message);
    return message;
	}

  // @Override
  // public List<BidResponse> getBids(Jwt principal, Long auctionId, BidFilter filter) {
  //   
  //   return null;
  // }

  @Override
  public void openAuctionRoom(Long auctionId) {
    if (isAuctionRoomOpen(auctionId)) {
      throw new RuntimeException("Phong dau gia da mo");
    }
    setAuctionRoomOpen(auctionId);
    Auction auction = auctionRepository.findById(auctionId).orElseThrow(
        () -> new NotFoundException("Khong tim thay phien dau gia nao trung voi Id")
    );
    List<UserAuction> userAuctions = userAuctionRepository.findAllByAuction(auction);
    userAuctions.stream()
        .forEach(userAuction 
            -> stompService.notifyJoinableAuction(userAuction.getUser().getId(), auctionId));
            // -> addUserToAuction(userAuction.getUser().getId(), auctionId));
  }

  @Override
  public void startAuction(Long auctionId) {
    if (isAuctionActive(auctionId)) {
      throw new RuntimeException("Phien dau gia da bat dau");
    }
    setAuctionActive(auctionId);
    setAuctionLastPrice(auctionId, 0L);
    stompService.broadcastStartAuction(auctionId);
  }

  @Override
  public void endAuction(Long auctionId) {
    if (!isAuctionRoomOpen(auctionId)) {
      throw new RuntimeException("Phong dau gia chua mo");
    }
    if (isAuctionActive(auctionId)) {
      stompService.broadcastEndAuction(auctionId);
      deleteAuctionActive(auctionId);
      Auction auction = auctionRepository.findById(auctionId).orElseThrow(
          () -> new NotFoundException("Khong tim thay phien dau gia nao trung voi Id")
      );
      Product product = auction.getProduct();
      long lastPrice = getAuctionLastPrice(auctionId).get();
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
    } else {
      stompService.broadcastEndAuction(auctionId);
    }
    setAuctionRoomClosed(auctionId);
    // Auction auction = auctionRepository.findById(auctionId).orElseThrow(
    //     () -> new NotFoundException("Khong tim thay phien dau gia nao trung voi Id")
    // );
    // List<UserAuction> userAuctions = userAuctionRepository.findAllByAuction(auction);
    // userAuctions.stream()
    //     .forEach(userAuction 
    //         -> removeUserFromAuction(userAuction.getUser().getId(), auctionId));
  }


  private String getAuctionRoomKey(Long auctionId) {
    return String.format("auction:%d", auctionId);
  }

  private boolean isAuctionRoomOpen(Long auctionId) {
    String key = getAuctionRoomKey(auctionId);
    return redisTemplate.hasKey(key);
  }

  private void setAuctionRoomOpen(Long auctionId) {
    String key = getAuctionRoomKey(auctionId);
    redisTemplate.opsForValue().set(key, "not-started");
  }

  private void setAuctionRoomClosed(Long auctionId) {
    String key = getAuctionRoomKey(auctionId);
    redisTemplate.delete(key);
  }
  

  private String getAuctionActiveKey(Long auctionId) {
    return String.format("auction:%d:active", auctionId);
  }

  private void setAuctionActive(Long auctionId) {
    Auction auction = auctionRepository.findById(auctionId).orElseThrow(
          () -> new NotFoundException("Khong tim thay auction hop le voi Id")
    );
    AuctionRedisResponse value = AuctionRedisResponse.builder()
          .startBid(auction.getStartBid())
          .pricePerStep(auction.getPricePerStep())
          .build();
    String key = getAuctionActiveKey(auctionId);
    redisObjectTemplate.opsForValue().set(key,value);
  }

  private Optional<AuctionRedisResponse> getAuctionActive(Long auctionId) {
    String key = getAuctionActiveKey(auctionId);
    return Optional.ofNullable((AuctionRedisResponse) redisObjectTemplate.opsForValue().get(key));
  }

  private boolean isAuctionActive(Long auctionId) {
    String key = getAuctionActiveKey(auctionId);
    return redisObjectTemplate.hasKey(key);
  }

  private void deleteAuctionActive(Long auctionId) {
    String key = getAuctionActiveKey(auctionId);
    redisObjectTemplate.delete(key);
  }
  

  // private String getUserAuctionKey(Long userId, Long auctionId) {
  //   return String.format("user:%d:auction:%d", userId, auctionId);
  // }

  // private boolean canUserJoinAuction(Long userId, Long auctionId) {
  //   String key = getUserAuctionKey(userId, auctionId);
  //   return redisTemplate.hasKey(key);
  // }

  // private void addUserToAuction(Long userId, Long auctionId) {
  //   String key = getUserAuctionKey(userId, auctionId);
  //   redisTemplate.opsForValue().set(key, "");
  // }
  // private void removeUserFromAuction(Long userId, Long auctionId) {
  //   String key = getUserAuctionKey(userId, auctionId);
  //   redisTemplate.delete(key);
  // }

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

  private Optional<Long> getAuctionLastPrice(long auctionId) {
    String key = getLastPriceKey(auctionId);
    return Optional.ofNullable(redisTemplate.opsForValue().get(key))
        .map(Long::parseLong);
  }

  private void setAuctionLastPrice(long auctionId, Long bid) {
    String key = getLastPriceKey(auctionId);
    redisTemplate.opsForValue().set(key, bid.toString());
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
    redisObjectTemplate.opsForList().leftPush(key, bid);
  }

  private List<AuctionBid> getBids(Long auctionId) {
    String key = getAuctionBidsKey(auctionId);
    return redisObjectTemplate.opsForList().range(key, 0, -1).stream().map(
        obj -> (AuctionBid) obj
    ).toList();
  }

  private boolean deleteBids(Long auctionId) {
    String key = String.format("auction:%d:bids", auctionId);
    return redisObjectTemplate.delete(key);
  }
}
