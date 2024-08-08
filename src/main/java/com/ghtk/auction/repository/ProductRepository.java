package com.ghtk.auction.repository;

import com.ghtk.auction.entity.Product;
import com.ghtk.auction.enums.ProductCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product,Long> {
	
	@Query(value =
			"""
  		  SELECT
           u.full_name AS OWNER,
           p.name AS NAME,
           p.category AS category,
           p.description AS description,
           p.image AS image,
           u2.full_name AS buyer
       FROM
           product p
       JOIN USER u ON
           p.owner_id = u.id
       LEFT JOIN USER u2 ON
           p.buyer_id = u2.id
       WHERE
           u.id = :o 	""", nativeQuery = true)
	List<Object[]> findByOwnerId(@Param("o") Long ownerId);
	
	List<Product> findAllByOwnerIdAndCategory(Long userId, ProductCategory category);
	
	List<Product> findAllByCategory(ProductCategory productCategory);
}
