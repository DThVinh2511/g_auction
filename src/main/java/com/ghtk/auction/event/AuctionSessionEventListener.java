package com.ghtk.auction.event;

import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import com.ghtk.auction.service.AuctionRealtimeService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class AuctionSessionEventListener {
  private final AuctionRealtimeService auctionRealtimeService;

  @EventListener
  public void handleSessionClosedEvent(final SessionClosedEvent event) {
    var session = event.getSession();
    Long userId = (Long) session.getAttributes().get("userId");
    auctionRealtimeService.leaveAllAuctions(userId);
  }
}
