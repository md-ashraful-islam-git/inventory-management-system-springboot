package com.bismillah.InventoryManagementSystem.service;

import com.bismillah.InventoryManagementSystem.dto.Response;
import com.bismillah.InventoryManagementSystem.dto.TransactionRequest;
import com.bismillah.InventoryManagementSystem.enums.TransactionStatus;

public interface TransactionService {

    Response restockInventory(TransactionRequest transactionRequest);

    Response sell(TransactionRequest transactionRequest);

    Response returnToSupplier(TransactionRequest transactionRequest);

    Response getAllTransactions(int page, int size, String searchText);

    Response getTransactionById(Long id);

    Response getAllTransactionByMonthAndYear(int month, int year);


    Response updateTransactionStatus(Long transactionId, TransactionStatus transactionStatus);
}
