package com.ghtk.auction.service.impl;

import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;

import com.ghtk.auction.dto.stomp.AuctionLastPrice;
import com.ghtk.auction.dto.stomp.BidMessage;
import com.ghtk.auction.dto.stomp.CommentMessage;
import com.ghtk.auction.dto.stomp.ControlMessage;
import com.ghtk.auction.service.StompService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class StompServiceImpl implements StompService {
  final SimpMessagingTemplate messagingTemplate;

  @Override
  public void notifyJoinableAuction(long userId, long auctionId) {
    messagingTemplate.convertAndSend(
        "/user/" + userId + "/queue/control", 
        new ControlMessage<>("joinable", auctionId));
  }

  @Override
  public void broadcastNotification(long auctionId, String message) {
    messagingTemplate.convertAndSend("/topic/auction/" + auctionId + "/notification", message);
  }

  @Override
  public void broadcastStartAuction(long auctionId) {
    messagingTemplate.convertAndSend(
        "/topic/auction/" + auctionId + "/control",
        new ControlMessage<>("start", (Void) null));
  }

  @Override
  public void broadcastEndAuction(long auctionId) {
    messagingTemplate.convertAndSend(
        "/topic/auction/" + auctionId + "/control",
        new ControlMessage<>("end", (Void) null));
  }

  @Override
  public void broadcastBid(long auctionId, BidMessage bid) {
    messagingTemplate.convertAndSend("/topic/auction/" + auctionId + "/bid", bid);
  }

  @Override
  public void broadcastComment(long auctionId, CommentMessage comment) {
    messagingTemplate.convertAndSend("/topic/auction/" + auctionId + "/comment", comment);
  }

  @Override
  public void sendAuctionLastPrice(Jwt principal, long auctionId, long lastPrice) {
    long userId = (Long) principal.getClaim("id");
    messagingTemplate.convertAndSend(
        "/user/" + userId + "/queue/control",
        new ControlMessage<>("auction_last_price", new AuctionLastPrice(auctionId, lastPrice)));
  }

  @Override
  public void broadcastError(String message) {
    messagingTemplate.convertAndSend("/topic/error", message);
  }
}
