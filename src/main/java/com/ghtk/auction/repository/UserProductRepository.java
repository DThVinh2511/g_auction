package com.ghtk.auction.repository;

import com.ghtk.auction.dto.response.product.ProductResponse;
import com.ghtk.auction.entity.Product;
import com.ghtk.auction.entity.UserProduct;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface UserProductRepository extends JpaRepository<UserProduct, Long> {
	
	@Query(value = "SELECT\n" +
			"    u.full_name AS owner,\n" +
			"    p.name AS name,\n" +
			"    p.category AS category,\n" +
			"    p.description AS description,\n" +
			"    p.image AS image\n" +
			"FROM\n" +
			"    user_product up\n" +
			"JOIN product p ON\n" +
			"    up.product_id = p.id\n" +
			"JOIN USER u ON\n" +
			"    p.owner_id = u.id\n" +
			"WHERE\n" +
			"    up.user_id = :userID", nativeQuery = true)
	List<Object[]> findMyInterestByUserID(@Param("userID") Long userId);
	
	Long countByProductID(Product product);
	
}
