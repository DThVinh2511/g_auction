package com.ghtk.auction.repository.Custom.impl;

import com.ghtk.auction.dto.response.user.UserResponse;
import com.ghtk.auction.enums.UserGender;
import com.ghtk.auction.enums.UserStatus;
import com.ghtk.auction.repository.Custom.UserRepositoryCustom;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import org.springframework.context.annotation.Primary;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

@Repository
@Primary
public class UserRepositoryCustomImpl implements UserRepositoryCustom {

    @PersistenceContext
    private EntityManager entityManager;
    @Override
    public List<UserResponse> findUsersAll(Pageable pageable, UserStatus status) {
        StringBuilder sql = new StringBuilder("SELECT u.id, u.email, u.full_name, u.birthday, u.gender, u.address, u.status, u.avatar_url, u.phone  FROM `user` u ");
        StringBuilder where = new StringBuilder("WHERE 1=1 ");
        if(status != null) {
            where.append("AND u.status = " + "'" + status.toString() + "' ");
        }
        where.append("ORDER BY u.id DESC ");
        sql.append(where);
        sql.append(" LIMIT ").append(pageable.getPageSize()).append("\n");
        sql.append(" OFFSET ").append(pageable.getOffset());
        Query query = entityManager.createNativeQuery(sql.toString());
        List<Object[]> results = query.getResultList();
        List<UserResponse> responses = new ArrayList<>();

        for (Object[] result : results) {
            UserResponse response = new UserResponse();
            response.setId((Long) result[0]);
            response.setEmail((String) result[1]);
            response.setFullName((String) result[2]);
            response.setDateOfBirth(result[3] != null ? ((java.sql.Date) result[3]).toLocalDate() : null);
            response.setGender(result[4] != null ? (UserGender.valueOf((String) result[4])) : null);
            response.setAddress(result[5] != null ?((String) result[5]) : null);
            response.setStatusAccount(UserStatus.valueOf((String)result[6]));
            response.setAvatar(result[7] != null ?((String) result[7]) : null);
            response.setPhone((String) result[8]);
            responses.add(response);
        }
        return  responses;
    }
}

