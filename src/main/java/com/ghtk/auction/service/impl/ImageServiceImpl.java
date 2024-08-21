package com.ghtk.auction.service.impl;

import com.ghtk.auction.component.CloudinaryComponent;
import com.ghtk.auction.exception.UploadException;
import com.ghtk.auction.service.ImageService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class ImageServiceImpl implements ImageService {

    private final CloudinaryComponent cloudinaryComponent;
    @Override
    public String uploadImage(String name, MultipartFile file){
        try {
            return cloudinaryComponent.uploadFile(name, file);
        } catch (IOException e) {
            throw new UploadException(e.getMessage());
        }
    }

    @Override
    public String normalizeImageUrls(String iamges) {
        String marker = "image/upload/"; // Phần cố định để bắt đầu lấy chuỗi
        return Arrays.stream(iamges.split(","))
                .map(String::trim)
                .map(url -> {
                    int startIndex = url.indexOf(marker);
                    return startIndex != -1 ? url.substring(startIndex + marker.length()) : url;
                })
                .collect(Collectors.joining(", "));
    }

    @Override
    public String restoreImageUrls(String images) {
        String baseUrl = "https://res.cloudinary.com/ddqaug6dy/image/upload/";

        return Arrays.stream(images.split(","))
                .map(String::trim)
                .map(url -> baseUrl + url)
                .collect(Collectors.joining(", "));
    }
}
