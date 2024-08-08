package com.ghtk.auction.entity;

import com.ghtk.auction.enums.ProductCategory;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
@Table(name = "product")
//@SqlResultSetMapping(
//      name = "ProductResponseMapping",
//      classes = @ConstructorResult(
//            targetClass = ProductResponse.class,
//            columns = {
//                  @ColumnResult(name = "name", type = String.class),
//                  @ColumnResult(name = "category", type = String.class),
//                  @ColumnResult(name = "owner", type = String.class),
//                  @ColumnResult(name = "description", type = String.class),
//                  @ColumnResult(name = "image", type = String.class)
//            }
//      )
//)
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @Column(name = "owner_id", nullable = false)
    Long ownerId;
    
    @Column(name = "name", nullable = false)
    String name;

    @Column(name = "category", nullable = false)
    @Enumerated(EnumType.STRING)
    ProductCategory category;

    @Column(name = "description",nullable = false, length = 100000)
    String description;

    @Column(name = "image", nullable = false)
    String image;
    
    @Column(name = "buyer_id")
    Long buyerId;
}
