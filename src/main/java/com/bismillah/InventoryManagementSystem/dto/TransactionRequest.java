package com.bismillah.InventoryManagementSystem.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class TransactionRequest {

    @Positive(message = "Product id is required")
    private Long productId;

    private Long supplierId;

    @Positive(message = "Quantity Id is Required")
    private Integer quantity;

    private String description;


    private BigDecimal newSellingPrice;

    private BigDecimal purchasePrice;
}
