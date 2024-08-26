package com.ghtk.auction.utils;

import com.ghtk.auction.repository.ProductRepository;
import com.github.javafaker.Faker;
import com.ghtk.auction.entity.Product;
import com.ghtk.auction.enums.ProductCategory;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

@RequiredArgsConstructor
@Component
public class ProductDataGenerator {

    private final ProductRepository productRepository;

    private static final Faker faker = new Faker();
    private static final Random random = new Random();

    // Define the range for owner IDs
    private static final int ID_MIN = 200028;
    private static final int ID_MAX = 200127;

    // Define province codes and names
    private static final Map<String, String> PROVINCE_CODES = new HashMap<>();

    static {
        PROVINCE_CODES.put("Hà Nội", "29");
        PROVINCE_CODES.put("Hồ Chí Minh", "79");
        PROVINCE_CODES.put("Đà Nẵng", "43");
        PROVINCE_CODES.put("Hải Phòng", "15");
        PROVINCE_CODES.put("Cần Thơ", "92");
        PROVINCE_CODES.put("Hải Dương", "34");
        PROVINCE_CODES.put("Nghệ An", "37");
        PROVINCE_CODES.put("Thanh Hóa", "36");
        PROVINCE_CODES.put("Quảng Nam", "92");
        PROVINCE_CODES.put("Đồng Nai", "60");
        // Add more provinces if needed
    }
    private static final String[] ART_PRODUCTS = {
            "Tranh sơn dầu phong cảnh", "Tượng đồng cổ", "Tranh thêu tay", "Bức tranh phong thủy", "Tượng gỗ chạm khắc"
    };
    private static final String[] OTHER_PRODUCTS = {
            "Máy xay sinh tố", "Tủ lạnh mini", "Lò vi sóng", "Đèn LED", "Máy hút bụi"
    };
    // Define some Vietnamese vehicle types
    private static final String[] VEHICLE_MODELS = {
            "Toyota Camry", "Honda Civic", "Mazda 3", "Ford Ranger", "Hyundai Accent",
            "VinFast Lux A2.0", "Kia Seltos", "Toyota Vios", "Honda CR-V", "Mazda CX-5"
    };

    // Define some Vietnamese antiques and art-related terms
    private static final String[] ANTIQUES = {
            "Gốm sứ", "Đồng hồ cổ", "Đèn dầu cổ", "Tranh cổ", "Bình gốm"
    };

    @Bean
    public void fakeData() {
        List<Product> products = generateFakeProducts(100);

        // Print or save products to the database
        products.forEach(System.out::println);
    }

    public List<Product> generateFakeProducts(int count) {
        List<Product> products = new ArrayList<>();

        for (int i = 0; i < count; i++) {
            ProductCategory category = ProductCategory.values()[random.nextInt(ProductCategory.values().length)];
            Product product = Product.builder()
                    .ownerId(generateRandomOwnerId()) // Generate a random owner ID within the specified range
                    .name(generateProductNameByCategory(category)) // Name based on category
                    .category(category) // Random category
                    .description(generateProductDescriptionByCategory(category)) // Description based on category
                    .image(generateProductImageUrl()) // Random image URL
                    .build();
            productRepository.save(product);
            products.add(product);
        }

        return products;
    }

    private static Long generateRandomOwnerId() {
        // Generate a random owner ID within the range [ID_MIN, ID_MAX]
        return (long) (ID_MIN + random.nextInt(ID_MAX - ID_MIN + 1));
    }

    private static String generateProductNameByCategory(ProductCategory category) {
        switch (category) {
            case ART:
                return ART_PRODUCTS[random.nextInt(ART_PRODUCTS.length)] + " (Tranh ảnh)"; // Vietnamese art names
            case LICENSE_PLATE:
                return generateLicensePlate() + " (Biển số)"; // Generate license plate with province code and type
            case VEHICLES:
                return VEHICLE_MODELS[random.nextInt(VEHICLE_MODELS.length)] + " (Xe)"; // Vehicle model names
            case ANTIQUES:
                return ANTIQUES[random.nextInt(ANTIQUES.length)] + " (Đồ cổ)"; // Antique names
            case OTHER:
                return OTHER_PRODUCTS[random.nextInt(OTHER_PRODUCTS.length)]; // General product names
            default:
                return "Sản phẩm"; // Default fallback
        }
    }

    private static String generateProductDescriptionByCategory(ProductCategory category) {
        switch (category) {
            case ART:
                return "Tác phẩm nghệ thuật đẹp mắt, mang đậm phong cách Việt.";
            case LICENSE_PLATE:
                return "Biển số xe hợp pháp với tỉnh và loại xe rõ ràng.";
            case VEHICLES:
                return "Xe mới, hiệu suất tốt, đáng tin cậy cho nhu cầu của bạn.";
            case ANTIQUES:
                return "Đồ cổ quý giá, mang giá trị lịch sử và văn hóa.";
            case OTHER:
                return "Sản phẩm chất lượng cao với các tính năng đặc biệt.";
            default:
                return "Mô tả sản phẩm không xác định.";
        }
    }

    private static String generateProductImageUrl() {
        // Generate a URL pointing to random images
        return "v1723197222/product-1.jpg, v1724125911/qjxhxu2qbkrjemvuyx7d.jpg, v1724125911/zffx0zb6i0fwhl4it8dr.jpg";
    }

    private static String generateLicensePlate() {
        // Randomly select a province code and vehicle type
        String provinceCode = new ArrayList<>(PROVINCE_CODES.values()).get(random.nextInt(PROVINCE_CODES.size()));
        String vehicleType = "XX"; // Placeholder for vehicle type

        // Create a random license plate number
        String plateNumber = faker.number().digits(5);

        return provinceCode + "A-" + plateNumber;
    }
}

