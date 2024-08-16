package com.ghtk.auction.repository.Custom.impl;

import com.ghtk.auction.entity.Product;
import com.ghtk.auction.repository.Custom.ProductRepositoryCustom;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import org.springframework.context.annotation.Primary;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@Primary
public class ProductRepositoryCustomImpl implements ProductRepositoryCustom {
    @PersistenceContext
    private EntityManager entityManager;
    @Override
    public List<Product> findProductByName(String key, Pageable pageable) {
        StringBuilder sql = new StringBuilder("SELECT p.* FROM product p ");
        StringBuilder where = new StringBuilder("WHERE 1=1 ");
        if(key != null && !key.equals("")) {
            where.append("AND p.name like " + "'" + key + "%' ");
        }
        where.append("GROUP BY p.id ");
        sql.append(where);
        sql.append(" LIMIT ").append(pageable.getPageSize()).append("\n");
        sql.append(" OFFSET ").append(pageable.getOffset());
        Query query = entityManager.createNativeQuery(sql.toString(), Product.class);
        return query.getResultList();
    }
}
