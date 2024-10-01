package com.ghtk.auction.repository.Custom.impl;

import com.ghtk.auction.dto.response.auction.AuctionBidResponse;
import com.ghtk.auction.dto.response.auction.AuctionCommentResponse;
import com.ghtk.auction.repository.Custom.CommentRepositoryCustom;
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
public class CommentRepositoryCustomImpl implements CommentRepositoryCustom {
    @PersistenceContext
    private EntityManager entityManager;
    @Override
    public List<AuctionCommentResponse> getCommentByAuctionId(Long auctionId) {
        StringBuilder sql = new StringBuilder("SELECT c.id as commentId, u.full_name as userName, c.content as content, c.created_at as createdAt" +
                " FROM comment c ");
        StringBuilder where = new StringBuilder("WHERE 1=1 ");
        sql.append("join user u on u.id = c.user_id ");
        if(auctionId != null) {
            where.append("AND c.auction_id = " + "'" + auctionId + "' ");
        }
        where.append("ORDER BY c.created_at ASC ");
        sql.append(where);
        Query query = entityManager.createNativeQuery(sql.toString(), "CommentListProjectionMapping");
        List<AuctionCommentResponse> projections = query.getResultList();
        return projections;
    }
}
