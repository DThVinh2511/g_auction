package com.ghtk.auction.repository;

import com.ghtk.auction.entity.User;
import com.ghtk.auction.enums.UserRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User,Long> {
	
	Optional<User> findByRole(UserRole role);
	boolean existsByEmail(String email);
	boolean existsByPhone(String phone);
	User findByEmail(String email);
	
	
	
}
