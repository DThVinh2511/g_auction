package com.ghtk.auction.dto.redis;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.io.Serializable;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AuctionRoom implements Serializable {
  Long ownerId;
  Long startBid;
  Long pricePerStep;
  boolean isStarted;
}
