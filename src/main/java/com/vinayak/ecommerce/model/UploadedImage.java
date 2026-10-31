package com.vinayak.ecommerce.model;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UploadedImage {

    private String imageUrl;
    private String imageKitFileId;

}
