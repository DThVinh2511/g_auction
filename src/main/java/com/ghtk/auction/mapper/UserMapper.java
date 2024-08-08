package com.ghtk.auction.mapper;

import com.ghtk.auction.dto.request.user.UserCreationRequest;
import com.ghtk.auction.dto.response.user.UserResponse;
import com.ghtk.auction.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface UserMapper {
    User toUser(UserCreationRequest request);
    @Mapping(target = "password",ignore = true)
    @Mapping(target = "isVerified",ignore = true)
    UserResponse toUserResponse(User user);
}
