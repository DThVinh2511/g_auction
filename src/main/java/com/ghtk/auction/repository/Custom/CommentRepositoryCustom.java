package com.ghtk.auction.repository.Custom;

import com.ghtk.auction.dto.response.auction.AuctionCommentResponse;

import java.util.List;

public interface CommentRepositoryCustom {
    List<AuctionCommentResponse> getCommentByAuctionId(Long auctionId);
}
