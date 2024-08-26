package com.ghtk.auction.repository;

import com.ghtk.auction.entity.Product;
import com.ghtk.auction.enums.ProductCategory;
import com.ghtk.auction.repository.Custom.ProductRepositoryCustom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product,Long>, ProductRepositoryCustom {
	
	@Query(value =
			"""
  		  SELECT
           u.full_name AS OWNER,
           p.name AS NAME,
           p.category AS category,
           p.description AS description,
           p.image AS image,
           u2.full_name AS buyer,
           p.id AS productId,
           COUNT(up.user_id) AS quantity
       FROM
           product p
       JOIN `user` u ON
           p.owner_id = u.id
       LEFT JOIN `user` u2 ON
           p.buyer_id = u2.id
       LEFT JOIN user_product up on up.product_id = p.id
       WHERE
           u.id = :o 
       GROUP BY p.name, u.full_name, p.category, p.description, p.image, u2.full_name, p.id
       ORDER BY p.id desc
       LIMIT :pageSize OFFSET :offsetPage
       """, nativeQuery = true)
	List<Object[]> findByOwnerId(@Param("o") Long ownerId, @Param("pageSize") int pageSize, @Param("offsetPage") int offset);
	
	List<Product> findAllByOwnerIdAndCategory(Long userId, ProductCategory category);
	
	List<Product> findAllByCategory(ProductCategory productCategory);
	
	@Query(value =
			"""
			SELECT
         	COUNT(*)
   		FROM
       		`product`
				""", nativeQuery = true
	)
	Long countAll();
	
	Long countByCategory(ProductCategory category);

	@Query(value =
			"""
			SELECT product_id
		   FROM `user_product`
		   WHERE user_id = :userId
		""", nativeQuery = true
	)
	List<Integer> checkFavoriteProduct(Long userId);
}
