package com.ghtk.auction.repository;

import com.ghtk.auction.entity.Comment;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CommentRepository extends JpaRepository<Comment,Long> {
  List<Comment> findAllByAuctionId(Long auctionId);
  List<Comment> findAllByAuctionIdAndCreatedAtBetween(Long auctionId, LocalDateTime start, LocalDateTime end);
  List<Comment> findAllByAuctionIdAndCreatedAtAfter(Long auctionId, LocalDateTime start);
}
