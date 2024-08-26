package com.ghtk.auction.service;

import org.springframework.messaging.Message;

import com.ghtk.auction.dto.response.ApiResponse;
import com.ghtk.auction.dto.stomp.BidMessage;
import com.ghtk.auction.dto.stomp.CommentMessage;
import com.ghtk.auction.dto.stomp.NotifyMessage;

public interface StompService {
  public void sendGlobalNotification(NotifyMessage message);

  public void sendMessageReceipt(long userId, Message<?> message, ApiResponse<?> response);

  public void notifyJoinableAuction(long userId, long auctionId);

  public void broadcastStartAuction(long auctionId);

  public void broadcastEndAuction(long auctionId, Long winnerId);

  public void broadcastBid(long auctionId, BidMessage bidResponse);

  public void broadcastComment(long auctionId, CommentMessage bidResponse);

  public void broadcastAuctionNotification(long auctionId, NotifyMessage message);

  public void sendAuctionLastPrice(Long userId, long auctionId, long lastPrice);

  public void broadcastError(String message);
}
