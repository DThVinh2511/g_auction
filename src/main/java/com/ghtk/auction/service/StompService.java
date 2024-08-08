package com.ghtk.auction.service;

import org.springframework.security.oauth2.jwt.Jwt;

import com.ghtk.auction.dto.stomp.BidMessage;
import com.ghtk.auction.dto.stomp.CommentMessage;

public interface StompService {
  public void notifyJoinableAuction(long userId, long auctionId);

  public void broadcastStartAuction(long auctionId);

  public void broadcastEndAuction(long auctionId);

  public void broadcastBid(long auctionId, BidMessage bidResponse);

  public void broadcastComment(long auctionId, CommentMessage bidResponse);

  public void broadcastNotification(long auctionId, String message);

  public void sendAuctionLastPrice(Jwt principal, long auctionId, long lastPrice);

  public void broadcastError(String message);
}
