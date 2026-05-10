package com.bismillah.InventoryManagementSystem.dto;

import com.bismillah.InventoryManagementSystem.enums.UserRole;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;


@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Response {


    private int status;

    private String message;



    private String token;

    private UserRole role;

    private String expirationTime;



    private Integer totalPages;

    private Long totalElement;



    private UserDTO user;
    private List<UserDTO> users;

    private SupplierDTO supplier;
    private List<SupplierDTO> suppliers;

    private CategoryDTO category;
    private List<CategoryDTO> categories;

    private ProductDTO product;
    private List<ProductDTO> products;



    private TransactionDTO transaction;
    private List<TransactionDTO> transactions;

    private DashboardDTO dashboard;

    private final LocalDateTime timestamp = LocalDateTime.now();
}
