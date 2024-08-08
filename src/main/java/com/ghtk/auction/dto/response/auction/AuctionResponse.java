package com.ghtk.auction.dto.response.auction;


import com.ghtk.auction.enums.AuctionStatus;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.sql.Timestamp;
import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AuctionResponse {
	
	Long auction_id;
	Long product_id;
	String title;
	String description;
	Timestamp created_at;
	LocalDateTime confirm_date;
	LocalDateTime end_registration;
	LocalDateTime start_time;
	LocalDateTime end_time;
	Long start_bid;
	Long price_per_step;
	Long end_bid;
	
	@Enumerated(EnumType.STRING)
	AuctionStatus status;
}
