package com.ghtk.auction.repository;

import com.ghtk.auction.entity.BlackListToken;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BlackListTokenRepository extends JpaRepository<BlackListToken,Long> {
	boolean existsByToken(String token);
}
