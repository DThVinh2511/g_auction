package com.ghtk.auction.service;

import java.util.List;

import org.springframework.security.oauth2.jwt.Jwt;

import com.ghtk.auction.dto.stomp.BidMessage;
import com.ghtk.auction.entity.Auction;

public interface AuctionRealtimeService {
    List<Auction> getJoinableNotis(Jwt principal);

    void checkNotifJoin(Jwt principal, Long auctionId);
    void checkBidJoin(Jwt principal, Long auctionId);
    void checkCommentJoin(Jwt principal, Long auctionId);

    void joinAuction(Jwt principal, Long auctionId);
    void leaveAuction(Jwt principal, Long auctionId);

    Long getCurrentPrice(Jwt principal, Long auctionId);
    BidMessage bid(Jwt principal, Long auctionId, Long bid);

    void openAuctionRoom(Long auctionId); // TODO: quartz 10p truoc khi bat dau
    void startAuction(Long auctionId);
    void endAuction(Long auctionId);
}
