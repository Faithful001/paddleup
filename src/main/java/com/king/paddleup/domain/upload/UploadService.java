package com.king.paddleup.domain.upload;

import com.cloudinary.Cloudinary;
import com.king.paddleup.domain.media.enums.MediaType;
import com.king.paddleup.domain.upload.dto.UploadResponse;
import com.king.paddleup.shared.exception.FileUploadException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class UploadService {

    private static final long MAX_IMAGE_SIZE = 10 * 1024 * 1024L;  // 10MB
    private static final long MAX_VIDEO_SIZE = 100 * 1024 * 1024L; // 100MB

    private final Cloudinary cloudinary;

    public UploadResponse upload(MultipartFile file) {
        validateFile(file);

        try {
            Map<String, Object> params = Map.of(
                    "resource_type", "auto",
                    "folder", "paddleup"
            );

            Map<?, ?> result = cloudinary.uploader().upload(file.getBytes(), params);

            String secureUrl = (String) result.get("secure_url");
            String publicId = (String) result.get("public_id");
            String resourceType = (String) result.get("resource_type");
            String format = (String) result.get("format");
            long bytes = result.get("bytes") instanceof Number n ? n.longValue() : file.getSize();

            MediaType type = "video".equalsIgnoreCase(resourceType)
                    ? MediaType.VIDEO
                    : MediaType.IMAGE;

            return new UploadResponse(secureUrl, publicId, type, format, bytes);
        } catch (IOException e) {
            log.error("Cloudinary upload failed: {}", e.getMessage(), e);
            throw new FileUploadException("Failed to upload file to Cloudinary: " + e.getMessage(), e);
        }
    }

    public List<UploadResponse> uploadMultiple(List<MultipartFile> files) {
        if (files == null || files.isEmpty()) {
            throw new FileUploadException("No files provided for upload");
        }

        List<UploadResponse> responses = new ArrayList<>();
        for (MultipartFile file : files) {
            responses.add(upload(file));
        }
        return responses;
    }

    private void validateFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new FileUploadException("File cannot be empty");
        }

        String contentType = file.getContentType();
        if (contentType == null || (!contentType.startsWith("image/") && !contentType.startsWith("video/"))) {
            throw new FileUploadException("Only image and video files are supported");
        }

        if (contentType.startsWith("image/") && file.getSize() > MAX_IMAGE_SIZE) {
            throw new FileUploadException("Image size must not exceed 10MB");
        }

        if (contentType.startsWith("video/") && file.getSize() > MAX_VIDEO_SIZE) {
            throw new FileUploadException("Video size must not exceed 100MB");
        }
    }
}
