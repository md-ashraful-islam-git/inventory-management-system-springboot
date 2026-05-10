package com.bismillah.InventoryManagementSystem.service.impl;

import com.bismillah.InventoryManagementSystem.dto.Response;
import com.bismillah.InventoryManagementSystem.dto.TransactionDTO;
import com.bismillah.InventoryManagementSystem.dto.TransactionRequest;
import com.bismillah.InventoryManagementSystem.entity.Product;
import com.bismillah.InventoryManagementSystem.entity.Supplier;
import com.bismillah.InventoryManagementSystem.entity.Transaction;
import com.bismillah.InventoryManagementSystem.entity.User;
import com.bismillah.InventoryManagementSystem.enums.TransactionStatus;
import com.bismillah.InventoryManagementSystem.enums.TransactionType;
import com.bismillah.InventoryManagementSystem.exception.NameValueRequiredException;
import com.bismillah.InventoryManagementSystem.exception.NotFoundException;
import com.bismillah.InventoryManagementSystem.repository.ProductRepository;
import com.bismillah.InventoryManagementSystem.repository.SupplierRepository;
import com.bismillah.InventoryManagementSystem.repository.TransactionRepository;
import com.bismillah.InventoryManagementSystem.service.TransactionService;
import com.bismillah.InventoryManagementSystem.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
@org.springframework.transaction.annotation.Transactional
public class TransactionServiceImpl implements TransactionService {

        private final TransactionRepository transactionRepository;
        private final ModelMapper modelMapper;
        private final SupplierRepository supplierRepository;
        private final UserService userService;

        private final ProductRepository productRepository;

        @Override
        public Response restockInventory(TransactionRequest transactionRequest) {

                Long productId = transactionRequest.getProductId();

                Long supplierId = transactionRequest.getSupplierId();

                Integer quantity = transactionRequest.getQuantity();

                if (supplierId == null)
                        throw new NameValueRequiredException("Supplier Id id Required");

                Product product = productRepository.findById(productId)
                                .orElseThrow(() -> new NotFoundException("Product Not Found"));

                Supplier supplier = supplierRepository.findById(supplierId)
                                .orElseThrow(() -> new NotFoundException("Supplier Not Found"));

                User user = userService.getCurrentLoggedInUser();


                product.setStockQuantity((product.getStockQuantity() == null ? 0 : product.getStockQuantity()) + quantity);


                if (transactionRequest.getNewSellingPrice() != null
                        && transactionRequest.getNewSellingPrice().compareTo(BigDecimal.ZERO) > 0) {
                    product.setPrice(transactionRequest.getNewSellingPrice());
                }


                if (transactionRequest.getPurchasePrice() != null
                        && transactionRequest.getPurchasePrice().compareTo(BigDecimal.ZERO) > 0) {
                    product.setPurchasePrice(transactionRequest.getPurchasePrice());
                }

                productRepository.save(product);


                BigDecimal unitCost = transactionRequest.getPurchasePrice() != null ? transactionRequest.getPurchasePrice() 
                                    : (product.getPurchasePrice() != null ? product.getPurchasePrice() : BigDecimal.ZERO);
                
                Transaction transaction = Transaction.builder()
                                .transactionType(TransactionType.PURCHASE)
                                .status(TransactionStatus.COMPLETED)
                                .product(product)
                                .user(user)
                                .supplier(supplier)
                                .totalProducts(quantity)
                                .purchasePrice(unitCost)
                                .totalPrice(unitCost.multiply(BigDecimal.valueOf(quantity)))
                                .description(transactionRequest.getDescription())
                                .build();

                transactionRepository.save(transaction);

                return Response.builder()
                                .status(200)
                                .message("Transaction Made Successfully")
                                .build();

        }

        @Override
        public Response sell(TransactionRequest transactionRequest) {

                Long productId = transactionRequest.getProductId();

                Integer quantity = transactionRequest.getQuantity();

                Product product = productRepository.findById(productId)
                                .orElseThrow(() -> new NotFoundException("Product Not Found"));

                User user = userService.getCurrentLoggedInUser();

                if (product.getStockQuantity() == null || product.getStockQuantity() < quantity) {
                        throw new IllegalArgumentException("Insufficient stock for product: " + product.getName());
                }

                if (product.getPrice() == null || product.getPrice().compareTo(BigDecimal.ZERO) == 0) {
                        throw new IllegalArgumentException(
                                "Product '" + product.getName() + "' has no price set. Please complete a purchase transaction first.");
                }



                product.setStockQuantity(product.getStockQuantity() - quantity);
                productRepository.save(product);



                Transaction transaction = Transaction.builder()
                                .transactionType(TransactionType.SALE)
                                .status(TransactionStatus.COMPLETED)
                                .product(product)
                                .user(user)
                                .totalProducts(quantity)
                                .totalPrice(product.getPrice().multiply(BigDecimal.valueOf(quantity)))
                                .description(transactionRequest.getDescription())
                                .build();

                transactionRepository.save(transaction);

                return Response.builder()
                                .status(200)
                                .message("Transaction Made Successfully")
                                .build();

        }

        @Override
        public Response returnToSupplier(TransactionRequest transactionRequest) {

                Long productId = transactionRequest.getProductId();
                Long supplierId = transactionRequest.getSupplierId();
                Integer quantity = transactionRequest.getQuantity();

                if (supplierId == null)
                        throw new NameValueRequiredException("Supplier Id id Required");

                Product product = productRepository.findById(productId)
                                .orElseThrow(() -> new NotFoundException("Product Not Found"));

                Supplier supplier = supplierRepository.findById(supplierId)
                                .orElseThrow(() -> new NotFoundException("Supplier Not Found"));

                User user = userService.getCurrentLoggedInUser();

                if (product.getStockQuantity() < quantity) {
                        throw new IllegalArgumentException(
                                        "Insufficient stock to return for product: " + product.getName());
                }



                product.setStockQuantity(product.getStockQuantity() - quantity);
                productRepository.save(product);



                Transaction transaction = Transaction.builder()
                                .transactionType(TransactionType.RETURN_TO_SUPPLIER)
                                .status(TransactionStatus.PROCESSING)
                                .product(product)
                                .user(user)
                                .supplier(supplier)
                                .totalProducts(quantity)
                                .totalPrice(BigDecimal.ZERO)
                                .description(transactionRequest.getDescription())
                                .build();

                transactionRepository.save(transaction);

                return Response.builder()
                                .status(200)
                                .message("Transaction Returned Successfully Initialized")
                                .build();

        }

        @Override
        public Response getAllTransactions(int page, int size, String searchText) {

                Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "id"));
                Page<Transaction> transactionPage = transactionRepository.searchTransactions(searchText, pageable);

                log.info("Found {} transactions for search text: {}", transactionPage.getNumberOfElements(), searchText);

                List<TransactionDTO> transactionDTOS = modelMapper
                                .map(transactionPage.getContent(), new TypeToken<List<TransactionDTO>>() {
                                }.getType());



                return Response.builder()
                                .status(200)
                                .message("success")
                                .transactions(transactionDTOS)
                                .totalPages(transactionPage.getTotalPages())
                                .totalElement(transactionPage.getTotalElements())
                                .build();
        }

        @Override
        public Response getTransactionById(Long id) {
                Transaction transaction = transactionRepository.findById(id)
                                .orElseThrow(() -> new NotFoundException("Transaction Not Found"));

                TransactionDTO transactionDTO = modelMapper.map(transaction, TransactionDTO.class);

                if (transactionDTO.getUser() != null) {
                    transactionDTO.getUser().setTransactions(null); // removing the user trnasaction list
                }

                return Response.builder()
                                .status(200)
                                .message("success")
                                .transaction(transactionDTO)
                                .build();
        }

        @Override
        public Response getAllTransactionByMonthAndYear(int month, int year) {

                List<Transaction> transactions = transactionRepository.findAllByMonthAndYear(month, year);

                List<TransactionDTO> transactionDTOS = modelMapper
                                .map(transactions, new TypeToken<List<TransactionDTO>>() {
                                }.getType());



                return Response.builder()
                                .status(200)
                                .message("success")
                                .transactions(transactionDTOS)
                                .build();
        }

        @Override
        public Response updateTransactionStatus(Long transactionId, TransactionStatus transactionStatus) {
                Transaction existingTransaction = transactionRepository.findById(transactionId)

                                .orElseThrow(() -> new NotFoundException("Transaction Not Found"));

                existingTransaction.setStatus(transactionStatus);

                existingTransaction.setUpdatedAt(LocalDateTime.now());

                transactionRepository.save(existingTransaction);

                return Response.builder()
                                .status(200)
                                .message("Transaction Status Successfully Updated")
                                .build();
        }
}
