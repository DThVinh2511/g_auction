package com.ghtk.auction.event;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class NotifycationEvent {
    private final Long commentId;
    private final Long auctionId;
    private final String content;
    private final LocalDateTime time;
}
