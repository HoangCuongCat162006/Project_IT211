package com.example.project_it211.service;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

@Service
public class CloudinaryService {

    private final Cloudinary cloudinary;

    public CloudinaryService(
            @Value("${cloudinary.cloud-name}") String cloudName,
            @Value("${cloudinary.api-key}") String apiKey,
            @Value("${cloudinary.api-secret}") String apiSecret) {
        this.cloudinary = new Cloudinary(ObjectUtils.asMap(
                "cloud_name", cloudName,
                "api_key", apiKey,
                "api_secret", apiSecret,
                "secure", true
        ));
    }

    public String uploadFile(MultipartFile file) {
        if ("your_cloud_name".equals(cloudinary.config.cloudName) ||
                "your_api_key".equals(cloudinary.config.apiKey) ||
                "your_api_secret".equals(cloudinary.config.apiSecret) ||
                cloudinary.config.cloudName == null ||
                cloudinary.config.cloudName.trim().isEmpty()) {
            System.out.println("[WARNING] Cloudinary credentials are not configured. Returning dummy file URL.");
            return "https://res.cloudinary.com/dummy-cloud/image/upload/dummy-file-url-" + System.currentTimeMillis();
        }
        try {
            Map<?, ?> uploadResult = cloudinary.uploader().upload(file.getBytes(), ObjectUtils.emptyMap());
            return uploadResult.get("secure_url").toString();
        } catch (Exception e) {
            System.err.println("[ERROR] Failed to upload file to Cloudinary: " + e.getMessage() + ". Returning fallback dummy URL.");
            return "https://res.cloudinary.com/dummy-cloud/image/upload/fallback-dummy-file-url-" + System.currentTimeMillis();
        }
    }
}
