package com.ghtk.auction.repository;

import com.ghtk.auction.entity.Auction;
import com.ghtk.auction.entity.TimeHistory;
import com.ghtk.auction.entity.UserAuction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TimeHistoryRepository extends JpaRepository<TimeHistory,Long> {
	
	
	boolean existsByUserAuctionId(UserAuction userAuction);
	
	
}
