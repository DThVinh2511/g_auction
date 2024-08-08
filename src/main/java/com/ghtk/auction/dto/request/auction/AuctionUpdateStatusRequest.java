package com.ghtk.auction.dto.request.auction;


import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.ghtk.auction.enums.AuctionStatus;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AuctionUpdateStatusRequest {
	
	@NotNull
	Long auctionId;
	
	@Enumerated(EnumType.STRING)
	AuctionStatus auctionStatus;
	
}
