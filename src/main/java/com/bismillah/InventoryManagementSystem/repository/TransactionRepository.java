package com.bismillah.InventoryManagementSystem.repository;

import com.bismillah.InventoryManagementSystem.entity.Transaction;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;


import java.util.List;

public interface TransactionRepository extends JpaRepository<Transaction,Long> {



    @Query("SELECT t FROM Transaction t " +

            "WHERE YEAR(t.createdAt) = :year AND MONTH(t.createdAt) = :month")
    List<Transaction> findAllByMonthAndYear (@Param("month") int month, @Param("year") int year);




    @Query("SELECT t FROM Transaction t " +
            "WHERE YEAR(t.createdAt) = :year AND MONTH(t.createdAt) = :month")
    List<Transaction> findAllByMonthYear(@Param("month") int month, @Param("year") int year);

    @Query("SELECT t FROM Transaction t " +
            "LEFT JOIN t.product p " +
            "WHERE (:searchText IS NULL OR :searchText = '' OR " +
            "LOWER(t.description) LIKE LOWER(CONCAT('%', :searchText, '%')) OR " +
            "LOWER(t.status) LIKE LOWER(CONCAT('%', :searchText, '%')) OR " +
            "LOWER(p.name) LIKE LOWER(CONCAT('%', :searchText, '%')) OR " +
            "LOWER(p.sku) LIKE LOWER(CONCAT('%', :searchText, '%')))")
    Page<Transaction> searchTransactions (@Param("searchText") String searchText, Pageable pageable);

    @Query("SELECT SUM(t.totalPrice) FROM Transaction t WHERE t.transactionType = :type")
    java.math.BigDecimal sumTotalPriceByType(@Param("type") com.bismillah.InventoryManagementSystem.enums.TransactionType type);

    long countByProductId(Long productId);

    long countBySupplierId(Long supplierId);
}
