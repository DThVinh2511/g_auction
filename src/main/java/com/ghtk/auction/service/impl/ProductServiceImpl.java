package com.ghtk.auction.service.impl;

import com.ghtk.auction.dto.request.product.ProductCreationRequest;
import com.ghtk.auction.dto.request.product.ProductFilterRequest;
import com.ghtk.auction.dto.response.product.ProductResponse;
import com.ghtk.auction.dto.response.product.ProductSearchResponse;
import com.ghtk.auction.dto.response.user.PageResponse;
import com.ghtk.auction.entity.Product;
import com.ghtk.auction.entity.User;
import com.ghtk.auction.entity.UserProduct;
import com.ghtk.auction.enums.ProductCategory;
import com.ghtk.auction.exception.NotFoundException;
import com.ghtk.auction.mapper.ProductMapper;
import com.ghtk.auction.repository.ProductRepository;
import com.ghtk.auction.repository.UserProductRepository;
import com.ghtk.auction.repository.UserRepository;
import com.ghtk.auction.service.ImageService;
import com.ghtk.auction.service.ProductService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@FieldDefaults(level = AccessLevel.PRIVATE)
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {
	final ProductRepository productRepository;
	final UserRepository userRepository;
	final UserProductRepository userProductRepository;
	final ImageService imageService;
	final ProductMapper productMapper;

	@Override
	public Product createProduct(ProductCreationRequest request) {
		var context = SecurityContextHolder.getContext();
		String email = context.getAuthentication().getName();
		
		User user = userRepository.findByEmail(email);
		
		Product product = new Product();
		product.setName(request.getName());
		product.setOwnerId(user.getId());
		product.setCategory(request.getCategory());
		product.setDescription(request.getDescription());
		product.setImage(request.getImage());
		
		return productRepository.save(product);
		
	}
	
	
	
	@Override
	public List<ProductResponse> getAllMyProduct() {
		String currentUser = SecurityContextHolder.getContext().getAuthentication().getName();
		User user = userRepository.findByEmail(currentUser);
		Long userId = user.getId();
		
		List<Object[]> products = productRepository.findByOwnerId(userId);
		return products.stream()
				.map(product -> new ProductResponse(
						(String) product[0],
						(String) product[1],
						(ProductCategory.valueOf((String) product[2])),
						(String) product[3],
						(String) product[4],
						(Long) product[6],
						null
				)).collect(Collectors.toList());
	}
	
	@Override
	public Product getById(Long id) {
		return productRepository.findById(id).orElseThrow(
				() -> new NotFoundException("Khong tim thay san pham")
		);
	}
	
	
	@Override
	public List<ProductResponse> getMyByCategory(Jwt principal , ProductFilterRequest request) {
		
		Long userId = (Long)principal.getClaims().get("id");
		
		List<Product> products = productRepository.findAllByOwnerIdAndCategory(userId,request.getProductCategory());
		
		Map<Long,String> buyerMap = new HashMap<>();
		buyerMap.put(null, null);
		products.forEach(product -> {
			if (!buyerMap.containsKey(product.getBuyerId())) {
				buyerMap.put(product.getBuyerId()
						,userRepository.findById(product.getBuyerId()).get().getFullName());
			}
		});
		
		return products.stream().map(
				product -> ProductResponse.builder()
						.name(product.getName())
						.category(product.getCategory())
						.description(product.getDescription())
						.image(product.getImage())
//						.buyer(buyerMap.get(product.getBuyerId()))
						.build()
		).collect(Collectors.toList());
		
	}
	
	@PreAuthorize("@productComponent.isProductOwner(#id, principal)")
	@Override
	public ProductResponse deleteProduct(Jwt principal, Long id) {
		Product product = productRepository.findById(id).get();
		User user = userRepository.findByEmail(principal.getClaims().get("sub").toString());
		
		productRepository.delete(product);
		return ProductResponse.builder()
				.owner(user.getFullName())
				.name(product.getName())
				.category(product.getCategory())
				.description(product.getDescription())
				.image(product.getImage())
				.build();
	}
	
	@Override
	public void interestProduct(Jwt principal, Long id) {
		
		Long userId = (Long)principal.getClaims().get("id");
		Product productId = productRepository.findById(id).orElseThrow(
				() -> new  NotFoundException("Product not found")
		);
		
		userProductRepository.save(UserProduct.builder()
						.userID(userRepository.findById(userId).get())
						.productID(productId)
						.build());
	}
	
	@Override
	public Long getInterestProduct(Long id) {
		return userProductRepository.countByProductID(productRepository.findById(id).get());
	}
	
	@Override
	public List<ProductResponse> getMyInterestProduct(Jwt principal) {
		
		Long userId = (Long)principal.getClaims().get("id");
		
		List<Object[]> products = userProductRepository.findMyInterestByUserID(userId);
		return products.stream()
				.map(product ->new ProductResponse(
						(String) product[0],
						(String) product[1],
						(ProductCategory.valueOf((String) product[2])),
						(String) product[3],
						(String) product[4],
						(Long) product[5],
						null
				)).collect(Collectors.toList());
		
	}
	
	@Override
	public List<ProductResponse> searchProductbyCategory(ProductFilterRequest request) {
		
		List<Product> products = productRepository.findAllByCategory(request.getProductCategory());
		
		products = products.stream().filter(product ->
			product.getBuyerId() == null
		).toList();
		
		Map<Long,String> ownerMap = new HashMap<>();
		products.forEach(product -> {
			if (!ownerMap.containsKey(product.getOwnerId())) {
				ownerMap.put(product.getOwnerId()
						,userRepository.findById(product.getOwnerId()).get().getFullName());
			}
		});
		
		return products.stream().map(
				product -> ProductResponse.builder()
						.owner(ownerMap.get(product.getOwnerId()))
						.name(product.getName())
						.category(product.getCategory())
						.description(product.getDescription())
						.image(product.getImage())
						.build()
		).collect(Collectors.toList());
		
	}

	@Override
	public PageResponse<ProductSearchResponse> searchProduct(String key, int pageNo, int pageSize) {
		Pageable pageable= PageRequest.of(pageNo,pageSize);
		List<ProductSearchResponse> products = productRepository.findProductByName(key, pageable);
		PageResponse<ProductSearchResponse> pageAuctionResponse = new PageResponse<>();
		pageAuctionResponse.setPageNo(pageNo);
		pageAuctionResponse.setPageSize(pageSize);
		pageAuctionResponse.setLast(true);
		pageAuctionResponse.setContent(products);
		return pageAuctionResponse;
	}

	@Override
	public List<ProductResponse> getTop5MostPopularProducts() {
		List<ProductResponse> topProducts = new ArrayList<>();
		List<Object[]> products = userProductRepository.findTop5MostPopularProducts();

		for (Object[] result : products) {
			Long productId = (Long) result[0];
			Long userCount = (Long) result[1];
			Product product = productRepository.findById(productId).orElseThrow(
					() -> new NotFoundException("Product not found")
			);
			String owner = userRepository.findById(product.getOwnerId()).orElseThrow(
					() -> new NotFoundException("Owner not found")
			).getFullName();
			ProductResponse productResponse = ProductResponse.builder()
					.owner(owner)
					.name(product.getName())
					.category(product.getCategory())
					.description(product.getDescription())
					.image(product.getImage())
					.productId(product.getId())
					.quantity(userCount)
					.build();
			topProducts.add(productResponse);
	}
		return topProducts;
	}
	
	@Override
	public PageResponse<ProductResponse> getAllProductByCategory(ProductCategory category, int pageNo, int pageSize, String sortBy, String sortDir) {
		Sort sort =sortDir.equalsIgnoreCase(Sort.Direction.ASC.name())
				? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
		
		Pageable pageable= PageRequest.of(pageNo,pageSize,sort);
		
		Page<Product> products =productRepository.findAllByCategory(category, pageable);
		
		List<Product> listOfProducts =products.getContent();
		
		List<ProductResponse> content =listOfProducts.stream().map(productMapper::toProductResponse).toList();
		
		PageResponse<ProductResponse> pageProductResponse = new PageResponse<>();
		pageProductResponse.setPageNo(pageNo);
		pageProductResponse.setPageSize(pageSize);
		pageProductResponse.setTotalPages(products.getTotalPages());
		pageProductResponse.setTotalElements(products.getTotalElements());
		pageProductResponse.setLast(products.isLast());
		pageProductResponse.setContent(content);
		return pageProductResponse;
	}
	
	@Override
	public PageResponse<ProductResponse> getAllProduct(int pageNo, int pageSize, String sortBy, String sortDir) {
		Sort sort =sortDir.equalsIgnoreCase(Sort.Direction.ASC.name())
				? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
		
		Pageable pageable= PageRequest.of(pageNo,pageSize,sort);
		
		Page<Product> products =productRepository.findAll(pageable);
		
		List<Product> listOfAuction =products.getContent();
		
		List<ProductResponse> content =listOfAuction.stream().map(productMapper::toProductResponse).toList();
		
		
		PageResponse<ProductResponse> pageProductResponse = new PageResponse<>();
		pageProductResponse.setPageNo(pageNo);
		pageProductResponse.setPageSize(pageSize);
		pageProductResponse.setTotalPages(products.getTotalPages());
		pageProductResponse.setTotalElements(products.getTotalElements());
		pageProductResponse.setLast(products.isLast());
		pageProductResponse.setContent(content);
		return pageProductResponse;
	}
}
