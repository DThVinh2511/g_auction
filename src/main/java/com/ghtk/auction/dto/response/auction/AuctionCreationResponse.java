package com.ghtk.auction.dto.response.auction;


import com.ghtk.auction.entity.Product;
import com.ghtk.auction.enums.AuctionStatus;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AuctionCreationResponse {
	
	Product product;
	
	String title;
	
	String description;
	
	Long startBid;
	
	Long pricePerStep;
	
	LocalDateTime createdAt;
	
	@Enumerated(EnumType.STRING)
	AuctionStatus status;
	
}
