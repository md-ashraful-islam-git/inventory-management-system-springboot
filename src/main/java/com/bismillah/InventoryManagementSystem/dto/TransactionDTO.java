package com.bismillah.InventoryManagementSystem.dto;


import com.bismillah.InventoryManagementSystem.enums.TransactionStatus;
import com.bismillah.InventoryManagementSystem.enums.TransactionType;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class TransactionDTO {


    private Long id;

    private Integer totalProducts;

    private BigDecimal totalPrice;

    private BigDecimal purchasePrice;


    private TransactionType transactionType;


    private TransactionStatus status;

    private String description;

    @com.fasterxml.jackson.annotation.JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updatedAt;


    @com.fasterxml.jackson.annotation.JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;


    @JsonIgnoreProperties({"transactions"})
    private UserDTO user;


    @JsonIgnoreProperties({"transactions"})
    private ProductDTO product;


    @JsonIgnoreProperties({"transactions"})
    private SupplierDTO supplier;


}
