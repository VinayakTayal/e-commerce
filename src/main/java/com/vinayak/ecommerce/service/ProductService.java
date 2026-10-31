package com.vinayak.ecommerce.service;

import com.vinayak.ecommerce.exception.ResourceNotFoundException;
import com.vinayak.ecommerce.model.Product;
import com.vinayak.ecommerce.model.UploadedImage;
import com.vinayak.ecommerce.repository.ProductRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

@Service
public class ProductService {

    private final ProductRepository productRepository;
    private final ImageKitService imageKitService;


    public ProductService(ProductRepository productRepository, ImageKitService imageKitService) {
        this.imageKitService = imageKitService;
        this.productRepository = productRepository;
    }

    public Product createProduct(Product product, MultipartFile image) throws IOException {
        UploadedImage imageObject;
        imageObject = imageKitService.uploadImage(image);
        product.setImageUrl(imageObject.getImageUrl());
        product.setImageKitFileId(imageObject.getImageKitFileId());

        return productRepository.save(product);
    }

    public Page<Product> getAllProducts(int pageNumber, int pageSize, String sortBy, String direction) {

        Sort sort = direction.equals("desc") ? Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(pageNumber, pageSize, sort);
        return productRepository.findAll(pageable);
    }

    public Product getProductById(Long id) {
        return productRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Product not found"));
    }

    public Product updateProduct(Long id, Product product, MultipartFile image) throws IOException {
        Product existingProduct = productRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Product not found"));

        existingProduct.setProductName(product.getProductName());
        existingProduct.setDescription(product.getDescription());
        existingProduct.setPrice(product.getPrice());
        existingProduct.setCategory(product.getCategory());
        existingProduct.setQuantity(product.getQuantity());

        if(image!= null && !image.isEmpty()) {

            String oldFileId = existingProduct.getImageKitFileId();

            UploadedImage uploadedImage = imageKitService.uploadImage(image);


            existingProduct.setImageUrl(uploadedImage.getImageUrl());
            existingProduct.setImageKitFileId(uploadedImage.getImageKitFileId());


            Product updatedProduct = productRepository.save(existingProduct);

            imageKitService.deleteImage(oldFileId);

            return updatedProduct;
        }
        return  productRepository.save(existingProduct);
    }

    public void deleteProduct(Long id) throws IOException {
        Product product = productRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Product not found"));

        productRepository.delete(product);

        imageKitService.deleteImage(product.getImageKitFileId());
    }

    public Page<Product> searchProduct(String keyword, String category, int pageNumber, int pageSize, String sortBy, String direction) {

        Sort sort = direction.equalsIgnoreCase("desc") ? Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();

        Pageable pageable = PageRequest.of(pageNumber, pageSize, sort);

        if(category != null && !category.isEmpty())
            return productRepository.findByProductNameContainingIgnoreCaseAndCategoryIgnoreCase(keyword, category ,pageable);

        return productRepository.findByProductNameContainingIgnoreCase(keyword,pageable);
    }


    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, String>> handleGeneralException(
            Exception e) {

        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", "An unexpected error occurred"));
    }
}
