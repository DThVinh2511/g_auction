package com.ghtk.auction.component;

import com.cloudinary.Cloudinary;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;
import java.util.UUID;

@RequiredArgsConstructor
@Component
@Getter
public class CloudinaryComponent {
    private final Cloudinary cloudinary;

    public String uploadFile(String name, MultipartFile multipartFile) throws IOException {
        return cloudinary.uploader()
                .upload(multipartFile.getBytes(),
                        Map.of("public_id", toSlug(name)))
                .get("url")
                .toString();
    }

    private String toSlug(String name) {
        // Chuyển về chữ thường
        String slug = name.toLowerCase();

        // Xóa các ký tự không phải chữ cái và số, thay thế bằng dấu cách
        slug = slug.replaceAll("[^a-z0-9\\s-]", "");

        // Thay thế các khoảng trắng và dấu gạch ngang bằng dấu gạch ngang
        slug = slug.replaceAll("\\s+", "-");

        // Loại bỏ các dấu gạch ngang thừa
        slug = slug.replaceAll("-+", "-");

        // Loại bỏ dấu gạch ngang ở đầu và cuối
        slug = slug.replaceAll("^-|-$", "");

        return slug;
    }
}
