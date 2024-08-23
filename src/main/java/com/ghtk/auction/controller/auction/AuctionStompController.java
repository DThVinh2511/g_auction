package com.ghtk.auction.controller.auction;

import org.springframework.messaging.Message;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Headers;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.annotation.SubscribeMapping;
import org.springframework.stereotype.Controller;

import com.ghtk.auction.dto.request.auction.BidRequest;
import com.ghtk.auction.dto.response.ApiResponse;
import com.ghtk.auction.dto.stomp.BidMessage;
import com.ghtk.auction.service.AuctionRealtimeService;
import com.ghtk.auction.service.StompService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
@RequiredArgsConstructor
public class AuctionStompController {
  private final AuctionRealtimeService auctionRealtimeService;
  private final StompService stompService;

  @MessageMapping("/echo")
  public Object echo(@Payload long bid, 
      @Headers MessageHeaders headers,
      SimpMessageHeaderAccessor accessor) {
    return accessor.getSessionAttributes();
    // return headers;//.<String, Object> map = new HashMap<>();
  }

  @SubscribeMapping("/topic/auction/{auctionId}/control")
  public void subscribeControlChannel(
      @DestinationVariable Long auctionId,
      @Header("userId") Long userId,
      Message<?> message) {
    System.out.println("subscribing to control channel");
    stompService.sendMessageReceipt(userId, message, ApiResponse.success("ok"));
  }
  @SubscribeMapping("/topic/auction/{auctionId}/notifications")
  public void subscribeNotificationChannel(
      @DestinationVariable Long auctionId,
      @Header("userId") Long userId,
      Message<?> message) {
    System.out.println("subscribing to notification channel");
    stompService.sendMessageReceipt(userId, message, ApiResponse.success("ok"));
  }
  
  @SubscribeMapping("/topic/auction/{auctionId}/bids")
  public void subscribeBidChannel(
      @DestinationVariable Long auctionId,
      @Header("userId") Long userId) {
    System.out.println("subscribing to bid channel");
    // Long lastPrice = auctionRealtimeService.getCurrentPrice(userId, auctionId).getBid();
    // stompService.sendAuctionLastPrice(userId, auctionId, lastPrice);
  }
  
  @SubscribeMapping("/topic/auction/{auctionId}/comments")
  public void subscribeCommentChannel(
      @DestinationVariable Long auctionId,
      @Header("userId") Long userId,
      Message<?> message) {
    System.out.println("subscribing to comment channel");
    stompService.sendMessageReceipt(userId, message, ApiResponse.success("ok"));
  }

  @MessageMapping("/auction/{id}/last-price")
  public void getLastPrice(
      @DestinationVariable("id") Long auctionId, 
      @Header("userId") Long userId,
      Message<?> message) {
    BidMessage lastPrice = auctionRealtimeService.getCurrentPrice(userId, auctionId);
    stompService.sendMessageReceipt(userId, message, ApiResponse.success(lastPrice));
  }

  @MessageMapping("/auction/{id}/bid")
  // @SendTo("/topic/auction/{id}/bids")
  public void placeBid(
      @DestinationVariable("id") Long auctionId, 
      @Header("userId") Long userId,
      @Payload @Valid BidRequest bid,
      Message<?> message) {
    auctionRealtimeService.bid(userId, auctionId, bid.getBid());  
    stompService.sendMessageReceipt(userId, message, ApiResponse.success("ok"));
  }

  @MessageMapping("/auction/{id}/comment")
  public void sendComment(
      @Header("userId") Long userId,
      @DestinationVariable("id") Long auctionId, 
      @Payload String content,
      Message<?> message) {
    auctionRealtimeService.comment(userId, auctionId, content);
    stompService.sendMessageReceipt(userId, message, ApiResponse.success("ok"));
  }
}
