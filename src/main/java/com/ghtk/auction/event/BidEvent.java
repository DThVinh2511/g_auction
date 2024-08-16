package com.ghtk.auction.event;

import java.time.LocalDateTime;

import lombok.Getter;
import lombok.AllArgsConstructor;

@Getter
@AllArgsConstructor
public class BidEvent {
  private final Long auctionId;
  private final Long userId;
  private final Long price;
  private final LocalDateTime time;
}