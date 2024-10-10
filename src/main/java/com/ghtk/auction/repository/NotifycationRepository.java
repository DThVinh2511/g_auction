package com.ghtk.auction.repository;

import com.ghtk.auction.entity.Notifycation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface NotifycationRepository extends JpaRepository<Notifycation, Long> {

    List<Notifycation> findAllByAuctionId(Long auctionId);
}
