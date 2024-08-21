package com.ghtk.auction.mapper;

import com.ghtk.auction.dto.request.user.UserCreationRequest;
import com.ghtk.auction.dto.response.auction.AuctionCreationResponse;
import com.ghtk.auction.dto.response.auction.AuctionResponse;
import com.ghtk.auction.dto.response.user.UserResponse;
import com.ghtk.auction.entity.Auction;
import com.ghtk.auction.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface AuctionMapper {
    AuctionCreationResponse toAuctionCreationResponse(Auction auction);
    
    @Mapping(source = "id", target = "auction_id")
    @Mapping(source = "product.id", target = "product_id")
    @Mapping(source = "createdAt", target = "created_at")
    @Mapping(source = "confirmDate", target = "confirm_date")
    @Mapping(source = "endRegistration", target = "end_registration")
    @Mapping(source = "startTime", target = "start_time")
    @Mapping(source = "endTime", target = "end_time")
    @Mapping(source = "startBid", target = "start_bid")
    @Mapping(source = "pricePerStep", target = "price_per_step")
    @Mapping(source = "endBid", target = "end_bid")
    @Mapping(source = "status", target = "status")
    AuctionResponse toAuctionResponse(Auction auction);
}
