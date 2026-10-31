package com.vinayak.ecommerce.repository;

import com.vinayak.ecommerce.model.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    Page<Product> findByProductNameContainingIgnoreCase(String name, Pageable pageable);

    Page<Product> findByProductNameContainingIgnoreCaseAndCategoryIgnoreCase(String name, String category , Pageable pageable);

}
