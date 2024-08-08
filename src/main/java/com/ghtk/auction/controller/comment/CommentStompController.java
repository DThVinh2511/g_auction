package com.ghtk.auction.controller.comment;

import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Controller;

import com.ghtk.auction.service.CommentService;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class CommentStompController {
  private final CommentService commentService;

  @MessageMapping("/auctions/{id}/comment")
  public void sendComment(
      @DestinationVariable Long auctionId, 
      @Payload String message, 
      @AuthenticationPrincipal Jwt principal) {
    commentService.addComment(principal, auctionId, message);
  }
}
