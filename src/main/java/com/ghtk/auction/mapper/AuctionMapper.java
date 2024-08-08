package com.ghtk.auction.mapper;

import com.ghtk.auction.dto.request.user.UserCreationRequest;
import com.ghtk.auction.dto.response.auction.AuctionCreationResponse;
import com.ghtk.auction.dto.response.auction.AuctionResponse;
import com.ghtk.auction.dto.response.user.UserResponse;
import com.ghtk.auction.entity.Auction;
import com.ghtk.auction.entity.User;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface AuctionMapper {
    AuctionCreationResponse toAuctionCreationResponse(Auction auction);
    AuctionResponse toAuctionResponse(Auction auction);
}
