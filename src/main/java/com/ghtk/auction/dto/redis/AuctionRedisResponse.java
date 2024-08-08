package com.ghtk.auction.dto.redis;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AuctionRedisResponse {
 
  Long startBid;
  
  Long pricePerStep;
}
