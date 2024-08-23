package com.ghtk.auction.repository.Custom;

import com.ghtk.auction.dto.response.user.UserResponse;
import com.ghtk.auction.enums.UserStatus;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface UserRepositoryCustom {

    List<UserResponse> findUsersAll(Pageable pageable, UserStatus status);
}
