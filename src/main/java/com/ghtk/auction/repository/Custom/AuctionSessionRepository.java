package com.ghtk.auction.repository.Custom;

import com.ghtk.auction.dto.redis.AuctionRoom;
import com.ghtk.auction.dto.request.comment.CommentFilter;
import com.ghtk.auction.dto.stomp.BidMessage;
import com.ghtk.auction.dto.stomp.CommentMessage;

import java.util.List;
import java.util.Optional;

import org.springframework.data.util.Pair;

import java.time.LocalDateTime;

public interface AuctionSessionRepository {
    boolean existsJoinable(Long auctionId, Long userId);
    List<Long> getJoinableByUser(Long userId);
    List<Long> getJoinableByAuction(Long auctionId);
    void addJoinable(Long auctionId, Long... userId);
    void deleteAllJoinableByAuction(Long auctionId);

    Optional<AuctionRoom> getAuctionRoom(Long auctionId);
    void setAuctionRoom(Long auctionId, AuctionRoom auctionRoom);
    void deleteAuctionRoom(Long auctionId);

    Optional<LocalDateTime> getJoin(Long auctionId, Long userId);
    List<Pair<Long, LocalDateTime>> getJoinsByAuction(Long auctionId);
    List<Pair<Long, LocalDateTime>> getJoinsByUser(Long userId);
    void addJoin(Long auctionId, Long userId);
    void deleteJoin(Long auctionId, Long userId);
    void deleteAllJoinByUser(Long auctionId);
    void deleteAllJoinByAuction(Long auctionId);
    
    Optional<BidMessage> getLastBid(Long auctionId);
    List<BidMessage> getBids(Long auctionId);
    void addBid(Long auctionId, BidMessage auctionBid);
    void deleteAllBids(Long auctionId);

    List<CommentMessage> getComments(Long auctionId);
    List<CommentMessage> getComments(Long auctionId, CommentFilter filter);
    void addComment(Long auctionId, CommentMessage comment);
    void deleteAllComments(Long auctionId);
}
