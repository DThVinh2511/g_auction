package com.ghtk.auction.repository;

import com.ghtk.auction.entity.Product;
import com.ghtk.auction.enums.ProductCategory;
import com.ghtk.auction.repository.Custom.ProductRepositoryCustom;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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
           p.id AS productId
       FROM
           product p
       JOIN `user` u ON
           p.owner_id = u.id
       LEFT JOIN `user` u2 ON
           p.buyer_id = u2.id
       WHERE
           u.id = :o 	""", nativeQuery = true)
	List<Object[]> findByOwnerId(@Param("o") Long ownerId);
	
	List<Product> findAllByOwnerIdAndCategory(Long userId, ProductCategory category);
	
	List<Product> findAllByCategory(ProductCategory productCategory);

	Page<Product> findAllByNameStartingWith(Pageable pageable, String name);
	@Query("SELECT p FROM Product p WHERE p.name LIKE %:name%")
	Page<Product> searchProduct(Pageable pageable, String name);
	
	Page<Product> findAllByCategory(ProductCategory category,Pageable pageable);
}
