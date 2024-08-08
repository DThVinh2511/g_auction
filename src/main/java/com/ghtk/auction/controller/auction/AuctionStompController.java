package com.ghtk.auction.controller.auction;

import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.annotation.SubscribeMapping;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Controller;

import com.ghtk.auction.dto.request.auction.BidRequest;
import com.ghtk.auction.dto.stomp.BidMessage;
import com.ghtk.auction.service.AuctionRealtimeService;
import com.ghtk.auction.service.StompService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class AuctionStompController {
  private final AuctionRealtimeService auctionRealtimeService;
  private final StompService stompService;

  // @SubscribeMapping("/auctions/{id}/comment")
  // @PreAuthorize("@auctionComponent.canParticipateAuction(#auctionId, principal)")
  // public void joinAuctionComment(
  //     @DestinationVariable Long auctionId, 
  //     @AuthenticationPrincipal Jwt principal) {
  //   auctionService.joinAuction(principal, auctionId);  
  // }

  @SubscribeMapping("/auctions/{id}/notification")
  @PreAuthorize("@auctionComponent.isRegisteredAuction(#auctionId, principal)")
  public void subscribeNotificationChannel(
      @DestinationVariable Long auctionId,
      @AuthenticationPrincipal Jwt principal) {
    auctionRealtimeService.checkNotifJoin(principal, auctionId);
  }
  
  @SubscribeMapping("/auctions/{id}/bid")
  @PreAuthorize("@auctionComponent.isRegisteredAuction(#auctionId, principal)")
  public void subscribeBidChannel(
      @DestinationVariable Long auctionId,
      @AuthenticationPrincipal Jwt principal) {
    auctionRealtimeService.checkBidJoin(principal, auctionId);
    long lastPrice = auctionRealtimeService.getCurrentPrice(principal, auctionId);
    stompService.sendAuctionLastPrice(principal, auctionId, lastPrice);
  }
  
  @SubscribeMapping("/auctions/{id}/comment")
  @PreAuthorize("@auctionComponent.isRegisteredAuction(#auctionId, principal)")
  public void subscribeCommentChannel(
      @DestinationVariable Long auctionId,
      @AuthenticationPrincipal Jwt principal) {
    auctionRealtimeService.checkCommentJoin(principal, auctionId);
  }

  @MessageMapping("/auctions/{id}/bid")
  @PreAuthorize("@auctionComponent.canParticipateAuction(#auctionId, principal)")
  public void placeBid(
      @DestinationVariable Long auctionId, 
      @Payload @Valid BidRequest bid, 
      @AuthenticationPrincipal Jwt principal) {
    BidMessage bidinfo = auctionRealtimeService.bid(principal, auctionId, bid.getBid());  
  }
}
