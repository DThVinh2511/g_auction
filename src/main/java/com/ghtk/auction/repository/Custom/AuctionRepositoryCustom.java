package com.ghtk.auction.repository.Custom;

import com.ghtk.auction.dto.response.auction.AuctionListResponse;
import com.ghtk.auction.enums.AuctionStatus;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface AuctionRepositoryCustom {

    List<AuctionListResponse> getAllAuctionListResponse(Pageable pageable, AuctionStatus status);
}
