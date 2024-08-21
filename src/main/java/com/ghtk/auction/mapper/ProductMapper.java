package com.ghtk.auction.mapper;

import com.ghtk.auction.dto.response.auction.AuctionCreationResponse;
import com.ghtk.auction.dto.response.auction.AuctionResponse;
import com.ghtk.auction.dto.response.product.ProductResponse;
import com.ghtk.auction.entity.Auction;
import com.ghtk.auction.entity.Product;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ProductMapper {
  
    @Mapping(source = "id", target = "productId")
    @Mapping(source = "ownerId", target = "owner")
    ProductResponse toProductResponse(Product product);
}
