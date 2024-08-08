package com.ghtk.auction.repository;

import com.ghtk.auction.dto.response.auction.AuctionResponse;
import com.ghtk.auction.entity.Auction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AuctionRepository extends JpaRepository<Auction,Long> {
	
	@Query(value =
			"""
			SELECT
	       a.id AS auction_id,
	       p.id AS product_id,
	       a.title AS title,
	       a.description AS description,
	       a.created_at AS created_at,
	       a.confirm_date AS confirm_date,
	       a.end_registration AS end_registration,
	       a.start_time AS start_time,
	       a.end_time AS end_time,
	       a.start_bid AS start_bid,
	       a.price_per_step AS price_per_step,
	       a.end_bid AS end_bid,
	       a.status AS status
	   FROM
	       product p
	   RIGHT JOIN auction a ON p.id = a.product_id
	   WHERE
	       p.owner_id = :ownerId""", nativeQuery = true)
	List<Object[]> findMyByProductOwnerId(@Param("ownerId") Long ownerId);
	
}
