package com.ghtk.auction.event;

import lombok.Getter;
import lombok.AllArgsConstructor;

@Getter
@AllArgsConstructor
public class AuctionEndEvent {
  private final Long auctionId;
}
