package com.ghtk.auction.repository.Custom;

import com.ghtk.auction.dto.response.auction.AuctionBidResponse;
import com.ghtk.auction.dto.stomp.BidMessage;
import com.ghtk.auction.enums.AuctionStatus;

import java.util.List;

public interface UserAuctionHistoryRepositoryCustom {
    List<AuctionBidResponse> getBidAuction(Long auctionId, AuctionStatus status);
}
