package com.ghtk.auction.repository;

import com.ghtk.auction.entity.*;
import com.ghtk.auction.enums.AuctionStatus;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserAuctionRepository extends JpaRepository<UserAuction, Long> {
  UserAuction findByUserIdAndAuctionId(Long userId, Long auctionId);
	boolean existsByUserAndAuction(User user, Auction auction);
	List<UserAuction> findAllByUser(User user);
  List<UserAuction> findAllByUserAndAuctionStatus(User user, AuctionStatus auctionStatus);
  List<UserAuction> findAllByAuction(Auction auction);
}
