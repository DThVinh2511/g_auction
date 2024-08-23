package com.ghtk.auction.repository;

import com.ghtk.auction.entity.User;
import com.ghtk.auction.enums.UserRole;
import com.ghtk.auction.enums.UserStatus;
import com.ghtk.auction.repository.Custom.UserRepositoryCustom;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User,Long>, UserRepositoryCustom {
	
	Optional<User> findByRole(UserRole role);
	boolean existsByEmail(String email);
	boolean existsByPhone(String phone);
	User findByEmail(String email);
	Page<User> findAllByStatusAccount(Pageable pageable, UserStatus statusAccount);
	
	
	Long countByStatusAccount(UserStatus statusAccount);
}
