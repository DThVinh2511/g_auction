package com.ghtk.auction.event;

import lombok.Getter;
import lombok.AllArgsConstructor;

@Getter
@AllArgsConstructor
public class AuctionRoomOpenEvent {
  private final Long auctionId;
}
