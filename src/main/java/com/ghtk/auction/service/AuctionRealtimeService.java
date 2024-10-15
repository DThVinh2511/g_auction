package com.ghtk.auction.service;

import java.util.List;

import com.ghtk.auction.dto.request.comment.CommentFilter;
import com.ghtk.auction.dto.response.auction.AuctionJoinResponse;
import com.ghtk.auction.dto.stomp.BidMessage;
import com.ghtk.auction.dto.stomp.BidMessageResponse;
import com.ghtk.auction.dto.stomp.CommentMessage;
import com.ghtk.auction.dto.stomp.NotifyMessage;
import com.ghtk.auction.entity.Auction;
import org.springframework.security.oauth2.jwt.Jwt;

public interface AuctionRealtimeService {
    List<Auction> getJoinableNotis(Long userId);

    void checkControlJoin(Long userId, Long auctionId);
    void checkNotifJoin(Long userId, Long auctionId);
    void checkBidJoin(Long userId, Long auctionId);
    void checkCommentJoin(Long userId, Long auctionId);

    AuctionJoinResponse joinAuction(Long userId, Long auctionId, String role);
    void leaveAuction(Long userId, Long auctionId);
    
    void leaveAllAuctions(Long userId);

    void checkBidding(Long userId, Long auctionId);
    void checkCommenting(Long userId, Long auctionId);
    void checkNotifying(Long userId, Long auctionId);

    BidMessage getCurrentPrice(Long userId, Long auctionId);
    List<CommentMessage> getComments(Long auctionId, CommentFilter filter, String role);

    BidMessage bid(Long userId, Long auctionId, Long bid);
    CommentMessage comment(Long userId, Long auctionId, String comment);
    NotifyMessage makeNotification(Long userId, Long auctionId, String message);



    void openAuctionRoom(Long auctionId);
    void startAuction(Long auctionId);
    void endAuction(Long auctionId);

    List<NotifyMessage> getNotifies(Long auctionI, String role);

    List<BidMessageResponse> getBids(Long auctionId, String role);
}
