package com.ghtk.auction.controller.comment;

import java.util.List;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import com.ghtk.auction.dto.request.comment.CommentFilter;
import com.ghtk.auction.dto.response.ApiResponse;
import com.ghtk.auction.dto.stomp.CommentMessage;
import com.ghtk.auction.service.CommentService;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
@RequestMapping("/v1/auctions/{auctionId}/comments")
public class CommentController {
  private final CommentService commentService;

  @GetMapping("/")
  @PreAuthorize("auctionComponent.canParticipateAuction(#auctionId, principal)")
  public ApiResponse<List<CommentMessage>> getComments(
        @PathVariable("auctionId") Long auctionId, 
        CommentFilter filter,
        @AuthenticationPrincipal Jwt principal) {
    return ApiResponse.success(commentService.getComments(principal, auctionId, filter));
  }
}
