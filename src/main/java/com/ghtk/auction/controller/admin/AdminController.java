package com.ghtk.auction.controller.admin;


import com.ghtk.auction.dto.request.auction.AuctionUpdateStatusRequest;
import com.ghtk.auction.dto.response.ApiResponse;
import com.ghtk.auction.dto.stomp.NotifyMessage;
import com.ghtk.auction.enums.AuctionStatus;
import com.ghtk.auction.service.StompService;
import com.ghtk.auction.service.AuctionRealtimeService;
import com.ghtk.auction.service.AuctionService;

import java.time.LocalDateTime;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v1")
@FieldDefaults(level = AccessLevel.PRIVATE)
@RequiredArgsConstructor
public class AdminController {
  final AuctionService auctionService;
  final AuctionRealtimeService auctionRealtimeService;
  final StompService stompService;
	
  @PostMapping("/error")
  @PreAuthorize("hasRole('ADMIN')")
  public ApiResponse<Void> error(@RequestBody String content) {
    stompService.broadcastError(content);
    return ApiResponse.success(null);
  }

  @PostMapping("/auctions/{auctionId}/open")
  @PreAuthorize("hasRole('ADMIN')")
  public ApiResponse<Void> openAuction(
      @PathVariable Long auctionId
  ) {
    auctionService.updateStatus(new AuctionUpdateStatusRequest(auctionId, AuctionStatus.CLOSED));
    auctionRealtimeService.openAuctionRoom(auctionId);
    return ApiResponse.success(null);
  }

  @PostMapping("/auctions/{auctionId}/start")
  @PreAuthorize("hasRole('ADMIN')")
  public ApiResponse<Void> startAuction(
      @PathVariable Long auctionId
  ) {
    auctionService.updateStatus(new AuctionUpdateStatusRequest(auctionId, AuctionStatus.IN_PROGRESS));
    auctionRealtimeService.startAuction(auctionId);
    return ApiResponse.success(null);
  }

  @PostMapping("/auctions/{auctionId}/end")
  @PreAuthorize("hasRole('ADMIN')")
  public ApiResponse<Void> endAuction(
      @PathVariable Long auctionId
  ) {
    auctionService.updateStatus(new AuctionUpdateStatusRequest(auctionId, AuctionStatus.FINISHED));
    auctionRealtimeService.endAuction(auctionId);
    return ApiResponse.success(null);
  }

  @PostMapping("/notify")
  public ApiResponse<Void> publishNotification(@RequestBody String message) {
    stompService.sendGlobalNotification(new NotifyMessage(message, LocalDateTime.now()));
    return ApiResponse.ok("ok");
  }

  @PostMapping("/auctions/{auctionId}/notify")
  public ApiResponse<Void> publishNotification(
    @RequestBody String message,
    @PathVariable Long auctionId) {
    stompService.broadcastAuctionNotification(auctionId, new NotifyMessage(message, LocalDateTime.now()));
    return ApiResponse.ok("ok");
  }
}
