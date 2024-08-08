package com.ghtk.auction.service;


import com.ghtk.auction.dto.request.auction.AuctionCreationRequest;
import com.ghtk.auction.dto.request.auction.AuctionUpdateStatusRequest;
import com.ghtk.auction.dto.request.auction.BidFilter;
import com.ghtk.auction.dto.response.auction.AuctionCreationResponse;
import com.ghtk.auction.dto.response.auction.AuctionResponse;
import com.ghtk.auction.dto.stomp.BidMessage;
import com.ghtk.auction.entity.Auction;
import com.ghtk.auction.entity.UserAuction;
import org.quartz.SchedulerException;
import org.springframework.security.oauth2.jwt.Jwt;

import java.util.List;

public interface AuctionService {

    AuctionCreationResponse addAuction(Jwt principal, AuctionCreationRequest request);
    Auction deleteAuction(Jwt principal, Long auctionId);
    List<AuctionResponse> getMyCreatedAuction(Jwt principal);

    Auction getAuctionById(Long auctionId);

    List<Auction> getMyJoinedAuction(Jwt principal);
    UserAuction registerJoinAuction(Jwt principal, Long auctionId);

    List<AuctionResponse> getRegisActiveAuctions(Jwt principal);
    // List<BidResponse> getBids(Jwt principal, Long auctionId, BidFilter filter);//
    
    // ADMIN
    List<Auction> getAllList();
    
    // thay doi trang thai PENDING -> OPENING,
    // them fiels confirm_date, end_regis, start_time, end_time
    // neu tu choi thi xoa auction.
    Auction confirmAuction(Long auctionId) throws SchedulerException;
    void updateStatus(AuctionUpdateStatusRequest request);
    void rejectAuction(Long auctionId);
}
