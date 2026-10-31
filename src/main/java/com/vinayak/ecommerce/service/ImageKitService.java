package com.vinayak.ecommerce.service;

import com.vinayak.ecommerce.model.UploadedImage;
import io.imagekit.client.ImageKitClient;
import io.imagekit.models.files.FileUploadParams;
import io.imagekit.models.files.FileUploadResponse;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Objects;

@Service
public class ImageKitService {

    private final ImageKitClient imageKitClient;

    public ImageKitService(ImageKitClient imageKitClient) {
        this.imageKitClient = imageKitClient;
    }

    public UploadedImage uploadImage(MultipartFile file) throws IOException {

        if(file.isEmpty()) {
            throw new IllegalArgumentException("Image File cannot be empty");
        }

        String contentType = file.getContentType();

        if(contentType == null || !contentType.startsWith("image/")) {
            throw new IllegalArgumentException("Image File must be of type image");
        }

        FileUploadParams params = FileUploadParams.builder()
                .file(file.getBytes())
                .fileName(Objects.requireNonNull(file.getOriginalFilename()))
                .folder("/products")
                .build();

        FileUploadResponse response = imageKitClient.files().upload(params);

        return new UploadedImage(response.url().get(), response.fileId().get());
    }

    public void deleteImage(String imageId) throws IOException {

        imageKitClient.files().delete(imageId);

    }

}
