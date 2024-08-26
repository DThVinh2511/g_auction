package com.ghtk.auction.repository.Custom.impl;

import com.ghtk.auction.dto.response.auction.AuctionListResponse;
import com.ghtk.auction.enums.AuctionStatus;
import com.ghtk.auction.repository.Custom.AuctionRepositoryCustom;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Repository
@Primary
@RequiredArgsConstructor
public class AuctionRepositoryCustomImpl implements AuctionRepositoryCustom {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public List<AuctionListResponse> getAllAuctionListResponse(Pageable pageable, AuctionStatus status) {
        StringBuilder sql = new StringBuilder("SELECT a.id AS id, a.product_id AS productId, a.title AS title, " +
                "a.description AS description, p.image AS image, a.created_at AS createdAt, a.confirm_date AS confirmDate, " +
                "a.end_registration AS endRegistration, a.start_time AS startTime, a.end_time AS endTime, " +
                "a.start_bid AS startBid, a.price_per_step AS pricePerStep, a.end_bid AS endBid, a.status AS status " +
                "FROM auction a ");
        StringBuilder where = new StringBuilder("WHERE 1=1 ");
        sql.append("join product p on p.id = a.product_id ");
        if(status != null) {
            where.append("AND a.status = " + "'" + status.toString() + "' ");
        }
        where.append("ORDER BY a.id DESC ");
        sql.append(where);
        sql.append(" LIMIT ").append(pageable.getPageSize()).append("\n");
        sql.append(" OFFSET ").append(pageable.getOffset());
        Query query = entityManager.createNativeQuery(sql.toString(), "AuctionListProjectionMapping");
        List<AuctionListResponse> projections = query.getResultList();
        return projections;
    }

}
