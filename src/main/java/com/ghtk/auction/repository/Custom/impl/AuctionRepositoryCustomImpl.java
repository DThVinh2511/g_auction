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

@Repository
@Primary
@RequiredArgsConstructor
public class AuctionRepositoryCustomImpl implements AuctionRepositoryCustom {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public List<AuctionListResponse> getAllAuctionListResponse(Pageable pageable, AuctionStatus status) {
        StringBuilder sql = new StringBuilder("SELECT a.id, a.product_id, a.title, a.description, p.image, a.created_at," +
                " a.confirm_date, a.end_registration, a.start_time, a.end_time, a.start_bid, a.price_per_step, " +
                "a.end_bid, a.status FROM auction a ");
        StringBuilder where = new StringBuilder("WHERE 1=1 ");
        sql.append("join product p on p.id = a.product_id ");
        if(status != null) {
            where.append("AND a.status = " + "'" + status.toString() + "' ");
        }
        where.append("ORDER BY a.id DESC ");
        sql.append(where);
        sql.append(" LIMIT ").append(pageable.getPageSize()).append("\n");
        sql.append(" OFFSET ").append(pageable.getOffset());
        Query query = entityManager.createNativeQuery(sql.toString());
        List<Object[]> results = query.getResultList();
        List<AuctionListResponse> responses = new ArrayList<>();

        for (Object[] result : results) {
            AuctionListResponse response = new AuctionListResponse();
            response.setId((Long) result[0]);
            response.setProduct_id((Long) result[1]);
            response.setTitle((String) result[2]);
            response.setDescription((String) result[3]);
            response.setImage((String) result[4]);
            response.setCreated_at(result[5] != null ? ((java.sql.Timestamp) result[5]) : null);
            response.setConfirm_date(result[6] != null ? ((java.sql.Timestamp) result[6]).toLocalDateTime() : null);
            response.setEnd_registration(result[7] != null ? ((java.sql.Timestamp) result[7]).toLocalDateTime() : null);
            response.setStart_time(result[8] != null ? ((java.sql.Timestamp) result[8]).toLocalDateTime() : null);
            response.setStart_time(result[9] != null ? ((java.sql.Timestamp) result[9]).toLocalDateTime() : null);
            response.setStart_bid((Long) result[10]);
            response.setPrice_per_step((Long) result[11]);
            response.setEnd_bid(result[12] != null ? ((Long) result[12]) : null);
            response.setStatus(AuctionStatus.valueOf((String) result[13]));
            responses.add(response);
        }
        return responses;
    }
}
