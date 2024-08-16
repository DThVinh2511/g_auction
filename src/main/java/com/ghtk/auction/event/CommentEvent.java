package com.ghtk.auction.event;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class CommentEvent {
  private final Long commentId;
  private final Long auctionId;
  private final Long userId;
  private final String content;
  private final LocalDateTime time;
}
