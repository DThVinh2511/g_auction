package com.ghtk.auction.repository;

import com.ghtk.auction.entity.*;
import com.ghtk.auction.enums.AuctionStatus;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserAuctionRepository extends JpaRepository<UserAuction, Long> {
    UserAuction findByUserIdAndAuctionId(Long userId, Long auctionId);
    boolean existsByUserAndAuction(User user, Auction auction);
    List<UserAuction> findAllByUser(User user);
    List<UserAuction> findAllByUserAndAuctionStatus(User user, AuctionStatus auctionStatus);
    List<UserAuction> findAllByAuction(Auction auction);

    @Query(
      value = """
          SELECT ua.user_id
          FROM user_auction ua
          WHERE ua.auction_id = :auctionId
      """,
      nativeQuery = true
    )
    List<Long> findUserIdsByAuctionId(@Param("auctionId") Long auctionId);

    @Query(
      value = """
          SELECT u.auction_id
          FROM user_auction ua
          WHERE ua.user_id = :userId
      """,
      nativeQuery = true
    )
    List<Long> findAuctionIdsByUserId(@Param("userId") Long userId);
}
