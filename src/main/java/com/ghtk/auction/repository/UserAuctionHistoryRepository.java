package com.ghtk.auction.repository;

import com.ghtk.auction.entity.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserAuctionHistoryRepository extends JpaRepository<UserAuctionHistory, Long> {
  List<UserAuctionHistory> findAllByUserAuction(UserAuction userAuction);
}
