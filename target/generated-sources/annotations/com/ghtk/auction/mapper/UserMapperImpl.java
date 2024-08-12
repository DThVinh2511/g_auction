package com.ghtk.auction.mapper;

import com.ghtk.auction.dto.request.user.UserCreationRequest;
import com.ghtk.auction.dto.response.user.UserResponse;
import com.ghtk.auction.entity.User;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2024-08-12T10:57:21+0700",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 21.0.1 (Oracle Corporation)"
)
@Component
public class UserMapperImpl implements UserMapper {

    @Override
    public User toUser(UserCreationRequest request) {
        if ( request == null ) {
            return null;
        }

        User.UserBuilder user = User.builder();

        user.email( request.getEmail() );
        user.password( request.getPassword() );
        user.fullName( request.getFullName() );
        user.dateOfBirth( request.getDateOfBirth() );
        user.phone( request.getPhone() );

        return user.build();
    }

    @Override
    public UserResponse toUserResponse(User user) {
        if ( user == null ) {
            return null;
        }

        UserResponse.UserResponseBuilder userResponse = UserResponse.builder();

        userResponse.email( user.getEmail() );
        userResponse.fullName( user.getFullName() );
        userResponse.dateOfBirth( user.getDateOfBirth() );
        userResponse.gender( user.getGender() );
        userResponse.address( user.getAddress() );
        userResponse.avatar( user.getAvatar() );
        userResponse.phone( user.getPhone() );

        return userResponse.build();
    }
}
