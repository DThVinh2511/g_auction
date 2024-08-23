package com.ghtk.auction.event;

import java.util.List;

import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import com.ghtk.auction.dto.response.ApiResponse;
import com.ghtk.auction.dto.stomp.BidMessage;
import com.ghtk.auction.dto.stomp.CommentMessage;
import com.ghtk.auction.entity.Auction;
import com.ghtk.auction.entity.UserAuction;
import com.ghtk.auction.repository.AuctionRepository;
import com.ghtk.auction.repository.UserAuctionRepository;
import com.ghtk.auction.service.StompService;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class AuctionEventStompListener {
  private final StompService stompService;
  private final AuctionRepository auctionRepository;
  private final UserAuctionRepository userAuctionRepository;

  @Async
  @EventListener
  public void handleSubscribeEvent(SubscribeEvent event) {
    System.out.println("listen subscribe event");
    StompHeaderAccessor headers = StompHeaderAccessor.wrap(event.getMessage());
    long userId = (Long) headers.getSessionAttributes().get("userId");
    stompService.sendMessageReceipt(
        userId, event.getMessage(), ApiResponse.success("ok"));
  }

  @Async
  @EventListener
  public void handleBidEvent(BidEvent event) {
    System.out.println("listen bid event");
    stompService.broadcastBid(
        event.getAuctionId(), 
        new BidMessage(event.getUserId(), event.getPrice(), event.getTime()));
  }

  @Async
  @EventListener
  public void handleCommentEvent(CommentEvent event) {
    stompService.broadcastComment(event.getAuctionId(),
        new CommentMessage(event.getCommentId(), event.getUserId(), 
            event.getContent(), event.getTime()));
  }

  @Async
  @EventListener
  public void handleAuctionRoomOpenEvent(AuctionRoomOpenEvent event) {
    System.out.println("listen auction room open event");
    Auction auction = auctionRepository.findById(event.getAuctionId()).get();
    List<UserAuction> uas = userAuctionRepository.findAllByAuction(auction);
    uas.stream().forEach(ua -> {
      long userId = ua.getUser().getId();
      System.out.println(userId);
      stompService.notifyJoinableAuction(userId, event.getAuctionId());
    });
  }

  @Async
  @EventListener
  public void handleAuctionStartEvent(AuctionStartEvent event) {
    stompService.broadcastStartAuction(event.getAuctionId());
  }

  @Async
  @EventListener
  public void handleAuctionEndEvent(AuctionEndEvent event) {
    stompService.broadcastEndAuction(event.getAuctionId());
  }
}
