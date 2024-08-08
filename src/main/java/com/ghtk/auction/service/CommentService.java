package com.ghtk.auction.service;


import java.util.List;

import org.springframework.security.oauth2.jwt.Jwt;

import com.ghtk.auction.dto.request.comment.CommentFilter;
import com.ghtk.auction.dto.stomp.CommentMessage;

public interface CommentService {
    CommentMessage addComment(Jwt principal, Long auctionId, String message);
    // TODO: co nen xoa ko
    void deleteComment();

    List<CommentMessage> getComments(Jwt principal, Long auctionId, CommentFilter filter);
}
