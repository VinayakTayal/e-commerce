package com.vinayak.ecommerce.controller;

import com.vinayak.ecommerce.model.UploadedImage;
import com.vinayak.ecommerce.service.ImageKitService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/api/images")
public class ImageController {

    private final ImageKitService imageKitService;

    public ImageController(ImageKitService imageKitService) {
        this.imageKitService = imageKitService;
    }

    @PostMapping("/upload")
    public UploadedImage uploadImage(@RequestParam("file") MultipartFile file) throws IOException {
        return imageKitService.uploadImage(file);
    }

}
