package com.bismillah.InventoryManagementSystem.controller;

import com.bismillah.InventoryManagementSystem.dto.DashboardDTO;
import com.bismillah.InventoryManagementSystem.dto.Response;
import com.bismillah.InventoryManagementSystem.enums.TransactionType;
import com.bismillah.InventoryManagementSystem.repository.ProductRepository;
import com.bismillah.InventoryManagementSystem.repository.SupplierRepository;
import com.bismillah.InventoryManagementSystem.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;

@RestController
@RequestMapping("/api/dashboard")
@RequiredArgsConstructor
public class DashboardController {

    private final ProductRepository productRepository;
    private final SupplierRepository supplierRepository;
    private final TransactionRepository transactionRepository;

    @GetMapping("/stats")
    public ResponseEntity<Response> getDashboardStats() {
        long totalProducts = productRepository.count();
        long totalSuppliers = supplierRepository.count();
        long lowStockCount = productRepository.countByStockQuantityLessThan(5);
        
        BigDecimal totalSales = transactionRepository.sumTotalPriceByType(TransactionType.SALE);
        if (totalSales == null) totalSales = BigDecimal.ZERO;

        DashboardDTO stats = DashboardDTO.builder()
                .totalProducts(totalProducts)
                .totalSuppliers(totalSuppliers)
                .lowStockCount(lowStockCount)
                .totalSales(totalSales)
                .build();

        return ResponseEntity.ok(Response.builder()
                .status(200)
                .message("success")
                .dashboard(stats)
                .build());
    }
}
