package com.bismillah.InventoryManagementSystem.repository;

import com.bismillah.InventoryManagementSystem.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProductRepository extends JpaRepository<Product, Long> {
    List<Product> findByNameContainingIgnoreCaseOrSkuContainingIgnoreCase(String name, String sku);
    long countByStockQuantityLessThan(int threshold);
    long countByCategoryId(Long categoryId);
}
