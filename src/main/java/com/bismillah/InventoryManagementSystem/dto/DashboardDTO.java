package com.bismillah.InventoryManagementSystem.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DashboardDTO {
    private long totalProducts;
    private BigDecimal totalSales;
    private long lowStockCount;
    private long totalSuppliers;
}
