package com.ghtk.auction.entity;

import com.ghtk.auction.enums.UserGender;
import com.ghtk.auction.enums.UserRole;
import com.ghtk.auction.enums.UserStatus;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
@Table(name = "user")
public class User {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	Long id;

	@Column(nullable = false, unique = true, name = "email")
	String email;
	
	@Column(nullable = false, name = "password")
	String password;
	
	@Column(nullable = false, name = "full_name")
	String fullName;
	
	@Column(nullable = false, name = "birthday")
	LocalDate dateOfBirth;
	
	@Column(name = "gender")
	@Enumerated(EnumType.STRING)
	UserGender gender;
	
	@Column(name = "address")
	String address;
	
	@Column(nullable = false, name = "status")
	@Enumerated(EnumType.STRING)
	UserStatus statusAccount;
	
	@Column(name = "avatar_url")
	String avatar;
	
	@Column(nullable = false, name = "created_at")
	LocalDateTime createdAt;
	
	@Column(nullable = false, name = "role")
	@Enumerated(EnumType.STRING)
	UserRole role;
	
	@Column(nullable = false, name = "is_verified")
	Boolean isVerified;

	@Column(name = "phone")
	String phone;
}
