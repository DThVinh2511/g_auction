package com.ghtk.auction.service;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface ImageService {
    String uploadImage(String name, MultipartFile file) throws IOException;
}
