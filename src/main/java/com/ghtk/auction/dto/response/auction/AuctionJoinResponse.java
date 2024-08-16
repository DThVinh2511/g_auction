package com.ghtk.auction.dto.response.auction;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AuctionJoinResponse {
	boolean isStarted;
}
