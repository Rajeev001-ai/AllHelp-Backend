package com.project.AllHelp.service;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.project.AllHelp.dto.UploadResultDto;
import com.project.AllHelp.exception.ApiException;
import java.io.IOException;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class MediaUploadService {
    private final Cloudinary cloudinary;

    public MediaUploadService(Cloudinary cloudinary) {
        this.cloudinary = cloudinary;
    }

    public UploadResultDto uploadImage(MultipartFile file, String folder) {
        if (file == null || file.isEmpty()) {
            throw new ApiException("Image file is required", HttpStatus.BAD_REQUEST);
        }
        if (file.getContentType() == null || !file.getContentType().startsWith("image/")) {
            throw new ApiException("Only image uploads are allowed", HttpStatus.BAD_REQUEST);
        }

        try {
            Map<?, ?> result = cloudinary.uploader().upload(file.getBytes(), ObjectUtils.asMap("folder", folder, "resource_type", "image"));
            return new UploadResultDto(String.valueOf(result.get("secure_url")), String.valueOf(result.get("public_id")));
        } catch (IOException exception) {
            throw new ApiException("Unable to upload image", HttpStatus.BAD_GATEWAY);
        }
    }

    public void deleteImage(String publicId) {
        if (publicId == null || publicId.isBlank()) {
            return;
        }
        try {
            cloudinary.uploader().destroy(publicId, ObjectUtils.emptyMap());
        } catch (IOException ignored) {
            // The database state is more important than a best-effort remote cleanup.
        }
    }
}
