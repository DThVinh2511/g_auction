package com.ghtk.auction.repository.Custom.impl;

import com.ghtk.auction.dto.response.auction.AuctionBidResponse;
import com.ghtk.auction.enums.AuctionStatus;
import com.ghtk.auction.repository.Custom.UserAuctionHistoryRepositoryCustom;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@Primary
@RequiredArgsConstructor
public class UserAuctionHistoryRepositoryCustomImpl implements UserAuctionHistoryRepositoryCustom {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public List<AuctionBidResponse> getBidAuction(Long auctionId, AuctionStatus status) {
        StringBuilder sql = new StringBuilder("SELECT full_name as userName, auction_id as auctionId, user_id as userId, " +
                "bid as bid, time as createdAt FROM user_auction_history uah ");
        StringBuilder where = new StringBuilder("WHERE 1=1 ");
        sql.append("join user_auction ua on ua.id = uah.user_auction_id ");
        sql.append("join user u on u.id = ua.user_id ");
        sql.append("join auction a on a.id = ua.auction_id ");
        if(auctionId != null) {
            where.append("AND ua.auction_id = " + "'" + auctionId + "' ");
        }
        if (status != null) {
            where.append("AND a.status = " + "'" + status.toString() + "' ");
        }
        where.append("ORDER BY uah.time ASC ");
        sql.append(where);
        Query query = entityManager.createNativeQuery(sql.toString(), "BidListProjectionMapping");
        List<AuctionBidResponse> projections = query.getResultList();
        return projections;
    }
}
