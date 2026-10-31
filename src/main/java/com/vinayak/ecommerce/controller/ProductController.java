package com.vinayak.ecommerce.controller;

import com.vinayak.ecommerce.model.Product;
import com.vinayak.ecommerce.service.ProductService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/api/products")
public class ProductController {

    private final ProductService productService;


    public ProductController(ProductService productService) {
        this.productService = productService;

    }

    @PostMapping
    public Product createProduct(@Valid @RequestPart("product") Product product,
                                 @RequestPart("image") MultipartFile image) throws IOException {
        return productService.createProduct(product, image);
    }

    @GetMapping
    public Page<Product> findAll(@RequestParam(defaultValue = "0") @Min(value = 0 , message = "Page must be 0 or greater") int page,
                                 @RequestParam(defaultValue = "10") @Min(value = 1, message = "Page size must be at least 1") @Max(value = 100, message = "Page size cannot exceed 100") int size,
                                 @RequestParam(defaultValue = "id") String sortBy,
                                 @RequestParam(defaultValue = "asc") String direction) {
        return productService.getAllProducts(page, size, sortBy, direction);
    }

    @GetMapping("/{id}")
    public Product getProductById(@Valid @PathVariable Long id) {
        return productService.getProductById(id);
    }

    @PutMapping("/{id}")
    public Product updateProduct(@Valid @PathVariable Long id,
                                 @RequestPart("product") Product product,
                                 @RequestPart(value = "image", required = false) MultipartFile image) throws IOException {
        return productService.updateProduct(id, product, image);
    }

    @DeleteMapping("/{id}")
    public void deleteProductById(@PathVariable Long id) throws IOException {
        productService.deleteProduct(id);
    }

    @GetMapping("/search")
    public Page<Product> searchProducts(@RequestParam String keyword,
                                        @RequestParam(required = false) String category,
                                        @RequestParam(defaultValue = "0") @Min(value = 0, message = "Page must be 0 or greater") int page,
                                        @RequestParam(defaultValue = "10") @Min(value = 1, message = "Page size must be at least 1") @Max(value = 100, message = "Page size cannot exceed 100") int size,
                                        @RequestParam(defaultValue = "id") String sortBy,
                                        @RequestParam(defaultValue = "asc") String direction) {
        return productService.searchProduct(keyword, category, page, size, sortBy, direction);
    }

}
