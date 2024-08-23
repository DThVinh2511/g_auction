package com.ghtk.auction.repository.Custom.impl;

import com.ghtk.auction.dto.response.product.ProductListResponse;
import com.ghtk.auction.enums.ProductCategory;
import com.ghtk.auction.repository.Custom.ProductRepositoryCustom;
import com.ghtk.auction.repository.UserProductRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

@Repository
@Primary
@RequiredArgsConstructor
public class ProductRepositoryCustomImpl implements ProductRepositoryCustom {
    @PersistenceContext
    private EntityManager entityManager;


    @Override
    public List<ProductListResponse> findProduct(String key, Pageable pageable, ProductCategory category) {
        StringBuilder sql = new StringBuilder("SELECT p.id, u.full_name, p.name, p.category, p.description, p.image, COUNT(up.user_id) AS quantity FROM product p ");
        StringBuilder where = new StringBuilder("WHERE 1=1 ");
        sql.append("JOIN user u on u.id = p.owner_id ");
        sql.append("LEFT JOIN user_product up on up.product_id = p.id ");
        if(key != null && !key.equals("")) {
            where.append("AND p.name like " + "'" + key + "%' ");
        }
        if(category != null) {
            where.append("AND p.category = " + "'" + category.toString() + "' ");
        }
        where.append("GROUP BY p.id ");
        where.append("ORDER BY p.id DESC ");
        sql.append(where);
        sql.append(" LIMIT ").append(pageable.getPageSize()).append("\n");
        sql.append(" OFFSET ").append(pageable.getOffset());
        Query query = entityManager.createNativeQuery(sql.toString());
        List<Object[]> results = query.getResultList();
        List<ProductListResponse> responses = new ArrayList<>();

        for (Object[] result : results) {
            ProductListResponse response = new ProductListResponse();
            response.setId((Long) result[0]);
            response.setOwner((String) result[1]);
            response.setName((String) result[2]);
            response.setCategory(ProductCategory.valueOf((String) result[3]));
            response.setDescription((String) result[4]);
            response.setImage((String) result[5]);
            response.setQuantity((Long) result[6]);
            responses.add(response);
        }

        return responses;
    }
    
    @Override
    public long countTotalProducts(String key, ProductCategory category) {
        StringBuilder sql = new StringBuilder("SELECT COUNT(p.id) FROM product p ");
        StringBuilder where = new StringBuilder("WHERE 1=1 ");
        sql.append("JOIN user u on u.id = p.owner_id ");
        sql.append("LEFT JOIN user_product up on up.product_id = p.id ");
        if(key != null && !key.equals("")) {
            where.append("AND p.name like " + "'" + key + "%' ");
        }
        if(category != null) {
            where.append("AND p.category = " + "'" + category.toString() + "' ");
        }
        sql.append(where);
        Query query = entityManager.createNativeQuery(sql.toString());
        return ((Number) query.getSingleResult()).longValue();
    }
    
    @Override
    public List<ProductListResponse> getInterestProductTop(Long limit) {
        StringBuilder sql = new StringBuilder("SELECT p.id, u.full_name, p.name, p.category, p.description, p.image, COUNT(up.user_id) AS quantity FROM product p ");
        StringBuilder where = new StringBuilder(" ");
        sql.append("JOIN user u on u.id = p.owner_id ");
        sql.append("JOIN user_product up on up.product_id = p.id ");
        where.append("GROUP BY p.id ");
        where.append("ORDER BY quantity DESC ");
        sql.append(where);
        sql.append(" LIMIT ").append(limit).append(";");
        Query query = entityManager.createNativeQuery(sql.toString());
        List<Object[]> results = query.getResultList();
        List<ProductListResponse> responses = new ArrayList<>();

        for (Object[] result : results) {
            ProductListResponse response = new ProductListResponse();
            response.setId((Long) result[0]);
            response.setOwner((String) result[1]);
            response.setName((String) result[2]);
            response.setCategory(ProductCategory.valueOf((String) result[3]));
            response.setDescription((String) result[4]);
            response.setImage((String) result[5]);
            response.setQuantity((Long) result[6]);
            responses.add(response);
        }

        return responses;
    }


}
