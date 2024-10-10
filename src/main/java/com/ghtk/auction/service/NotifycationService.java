package com.ghtk.auction.service;

import com.ghtk.auction.dto.stomp.NotifyMessage;

public interface NotifycationService {
    void addNotifycation(Long auctionId, NotifyMessage notify);
}
