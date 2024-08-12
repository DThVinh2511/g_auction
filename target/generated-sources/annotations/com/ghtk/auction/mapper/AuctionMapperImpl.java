package com.ghtk.auction.mapper;

import com.ghtk.auction.dto.response.auction.AuctionCreationResponse;
import com.ghtk.auction.dto.response.auction.AuctionResponse;
import com.ghtk.auction.entity.Auction;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2024-08-12T10:57:21+0700",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 21.0.1 (Oracle Corporation)"
)
@Component
public class AuctionMapperImpl implements AuctionMapper {

    @Override
    public AuctionCreationResponse toAuctionCreationResponse(Auction auction) {
        if ( auction == null ) {
            return null;
        }

        AuctionCreationResponse.AuctionCreationResponseBuilder auctionCreationResponse = AuctionCreationResponse.builder();

        auctionCreationResponse.product( auction.getProduct() );
        auctionCreationResponse.title( auction.getTitle() );
        auctionCreationResponse.description( auction.getDescription() );
        auctionCreationResponse.startBid( auction.getStartBid() );
        auctionCreationResponse.pricePerStep( auction.getPricePerStep() );
        auctionCreationResponse.createdAt( auction.getCreatedAt() );
        auctionCreationResponse.status( auction.getStatus() );

        return auctionCreationResponse.build();
    }

    @Override
    public AuctionResponse toAuctionResponse(Auction auction) {
        if ( auction == null ) {
            return null;
        }

        AuctionResponse.AuctionResponseBuilder auctionResponse = AuctionResponse.builder();

        auctionResponse.title( auction.getTitle() );
        auctionResponse.description( auction.getDescription() );
        auctionResponse.status( auction.getStatus() );

        return auctionResponse.build();
    }
}
