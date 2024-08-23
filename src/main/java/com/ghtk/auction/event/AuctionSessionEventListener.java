package com.ghtk.auction.event;

import java.util.List;

import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import com.ghtk.auction.entity.User;
import com.ghtk.auction.entity.UserAuction;
import com.ghtk.auction.repository.UserAuctionRepository;
import com.ghtk.auction.repository.UserRepository;
import com.ghtk.auction.service.AuctionRealtimeService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class AuctionSessionEventListener {
  private final AuctionRealtimeService auctionRealtimeService;
  private final UserAuctionRepository userAuctionRepository;
  private final UserRepository userRepository;

  @EventListener
  public void handleSessionClosedEvent(final SessionClosedEvent event) {
    var session = event.getSession();
    Long userId = (Long) session.getAttributes().get("userId");
    User user = userRepository.findById(userId).get();
    List<UserAuction> userAuctions = userAuctionRepository.findAllByUser(user);
    userAuctions.forEach(userAuction -> {
      try {
        auctionRealtimeService.leaveAuction(userId, userAuction.getAuction().getId());
      } catch (Exception e) {
        log.debug(e.getMessage());
      }
    });
  }
}
