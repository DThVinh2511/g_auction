package com.ghtk.auction.service.impl;

import java.util.Map;


import org.springframework.messaging.Message;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Service;

import com.ghtk.auction.dto.response.ApiResponse;
import com.ghtk.auction.dto.stomp.AuctionLastPrice;
import com.ghtk.auction.dto.stomp.BidMessage;
import com.ghtk.auction.dto.stomp.CommentMessage;
import com.ghtk.auction.dto.stomp.ControlMessage;
import com.ghtk.auction.dto.stomp.NotifyMessage;
import com.ghtk.auction.service.StompService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class StompServiceImpl implements StompService {
  private final SimpMessagingTemplate messagingTemplate;

  @Override
  public void sendGlobalNotification(NotifyMessage message) {
    messagingTemplate.convertAndSend("/topic/notifications", message);
  }

  @Override
  public void sendMessageReceipt(long userId, Message<?> message, ApiResponse<?> response) {
    StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);
    var ms = accessor.getNativeHeader("receipt");
    String receipt = accessor.getCommand() + ":" + accessor.getDestination();
    if (ms != null && !ms.isEmpty()) {
      receipt = ms.get(0);
    }
    messagingTemplate.convertAndSend("/user/" + userId + "/queue/receipts", 
        response, Map.of("receipt-id", receipt));
  }

  @Override
  public void notifyJoinableAuction(long userId, long auctionId) {
    System.out.println("destination: /user/" + userId + "/queue/control");
    messagingTemplate.convertAndSend(
        "/user/" + userId + "/queue/control",
        new ControlMessage<>("joinable", auctionId));
  }

  @Override
  public void broadcastAuctionNotification(long auctionId, NotifyMessage message) {
    messagingTemplate.convertAndSend("/topic/auction/" + auctionId + "/notifications", message);
  }

  @Override
  public void broadcastStartAuction(long auctionId) {
    messagingTemplate.convertAndSend(
        "/topic/auction/" + auctionId + "/control",
        new ControlMessage<>("start", (Void) null));
  }

  @Override
  public void broadcastEndAuction(long auctionId, Long winnerId) {
    messagingTemplate.convertAndSend(
        "/topic/auction/" + auctionId + "/control",
        new ControlMessage<>("end", winnerId));
  }

  @Override
  public void broadcastBid(long auctionId, BidMessage bid) {
    messagingTemplate.convertAndSend("/topic/auction/" + auctionId + "/bids", bid);
  }

  @Override
  public void broadcastComment(long auctionId, CommentMessage comment) {
    messagingTemplate.convertAndSend("/topic/auction/" + auctionId + "/comments", comment);
  }

  @Override
  public void sendAuctionLastPrice(Long userId, long auctionId, long lastPrice) {
    messagingTemplate.convertAndSend(
        "/user/" + userId + "/queue/control",
        new ControlMessage<>("auction_last_price", new AuctionLastPrice(auctionId, lastPrice)));
  }

  @Override
  public void broadcastError(String message) {
    messagingTemplate.convertAndSend("/topic/error", message);
  }
}
