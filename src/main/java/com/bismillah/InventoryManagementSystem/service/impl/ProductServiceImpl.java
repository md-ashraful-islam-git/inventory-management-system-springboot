package com.bismillah.InventoryManagementSystem.service.impl;

import com.bismillah.InventoryManagementSystem.dto.ProductDTO;
import com.bismillah.InventoryManagementSystem.dto.Response;
import com.bismillah.InventoryManagementSystem.entity.Category;
import com.bismillah.InventoryManagementSystem.entity.Product;
import com.bismillah.InventoryManagementSystem.exception.DeleteConstraintException;
import com.bismillah.InventoryManagementSystem.exception.NotFoundException;
import com.bismillah.InventoryManagementSystem.repository.CategoryRepository;
import com.bismillah.InventoryManagementSystem.repository.ProductRepository;
import com.bismillah.InventoryManagementSystem.repository.TransactionRepository;
import com.bismillah.InventoryManagementSystem.service.ProductService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
@org.springframework.transaction.annotation.Transactional
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;

    private final ModelMapper modelMapper;

    private final CategoryRepository categoryRepository;

    private final TransactionRepository transactionRepository;

    private static final String IMAGE_DIRECTORY = "uploads" + File.separator + "products" + File.separator;

    @Override
    public Response saveProduct(ProductDTO productDTO, MultipartFile imageFile) {
        Category category = categoryRepository.findById(productDTO.getCategoryId())
                .orElseThrow(() -> new NotFoundException("Category Not Found"));


        Product productToSave = Product.builder()
                .name(productDTO.getName())
                .sku(productDTO.getSku())
                .price(BigDecimal.ZERO)
                .stockQuantity(0)
                .description(productDTO.getDescription())
                .expiryDate(productDTO.getExpiryDate())
                .category(category)
                .build();

        if (imageFile != null && !imageFile.isEmpty()) {
            String imagePath = saveImage(imageFile);
            productToSave.setImageUrl(imagePath);
        } else if (productDTO.getImageUrl() != null) {
            productToSave.setImageUrl(productDTO.getImageUrl());
        }

        // save the product to our database
        productRepository.save(productToSave);
        return Response.builder()
                .status(200)
                .message("Product successfully saved")
                .build();
    }

    @Override
    public Response updateProduct(ProductDTO productDTO, MultipartFile imageFile) {
        Long idToUse = productDTO.getProductId() != null ? productDTO.getProductId() : productDTO.getId();
        Product existingProduct = productRepository.findById(idToUse)
                .orElseThrow(() -> new NotFoundException("Product Not Found"));


        if (imageFile != null && !imageFile.isEmpty()) {
            String imagePath = saveImage(imageFile);
            existingProduct.setImageUrl(imagePath);
        } else if (productDTO.getImageUrl() != null && !productDTO.getImageUrl().isBlank()) {
            existingProduct.setImageUrl(productDTO.getImageUrl());
        }


        if (productDTO.getCategoryId() != null && productDTO.getCategoryId() > 0) {
            Category category = categoryRepository.findById(productDTO.getCategoryId())
                    .orElseThrow(() -> new NotFoundException("Category Not Found"));
            existingProduct.setCategory(category);
        }


        if (productDTO.getName() != null && !productDTO.getName().isBlank()) {
            existingProduct.setName(productDTO.getName());
        }

        if (productDTO.getSku() != null && !productDTO.getSku().isBlank()) {
            existingProduct.setSku(productDTO.getSku());
        }

        if (productDTO.getDescription() != null && !productDTO.getDescription().isBlank()) {
            existingProduct.setDescription(productDTO.getDescription());
        }

        if (productDTO.getExpiryDate() != null) {
            existingProduct.setExpiryDate(productDTO.getExpiryDate());
        }





        productRepository.save(existingProduct);
        return Response.builder()
                .status(200)
                .message("product successfully Updated")
                .build();
    }

    @Override
    public Response getAllProducts(String searchText) {
        List<Product> products;
        if (searchText != null && !searchText.isBlank()) {
            products = productRepository.findByNameContainingIgnoreCaseOrSkuContainingIgnoreCase(searchText,
                    searchText);
        } else {
            products = productRepository.findAll(Sort.by(Sort.Direction.DESC, "id"));
        }
        List<ProductDTO> productDTOS = modelMapper.map(products, new TypeToken<List<ProductDTO>>() {
        }.getType());

        return Response.builder()
                .status(200)
                .message("success")
                .products(productDTOS)
                .build();
    }

    @Override
    public Response getProductById(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Product Not Found"));

        return Response.builder()
                .status(200)
                .message("success")
                .product(modelMapper.map(product, ProductDTO.class))
                .build();
    }

    @Override
    public Response deleteProduct(Long id) {
        log.info("Attempting to delete product with ID: {}", id);
        productRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Product Not Found"));

        if (transactionRepository.countByProductId(id) > 0) {
            log.warn("Deletion blocked: Product ID {} is linked to transactions", id);
            throw new DeleteConstraintException("Cannot delete product. It is linked to existing transactions.");
        }

        productRepository.deleteById(id);
        log.info("Product ID {} successfully deleted", id);

        return Response.builder()
                .status(200)
                .message("Product successfully deleted")
                .build();
    }

    private String saveImage(MultipartFile imageFile) {

        if (imageFile.getContentType() == null || !imageFile.getContentType().startsWith("image/")) {
            throw new IllegalArgumentException("Only image files are allowed");
        }


        File directory = new File(IMAGE_DIRECTORY);

        if (!directory.exists()) {
            directory.mkdir();
            log.info("Directory was created: {}", IMAGE_DIRECTORY);
        }


        String uniqueFileName = UUID.randomUUID() + "_" + imageFile.getOriginalFilename();


        String imagePath = IMAGE_DIRECTORY + uniqueFileName;

        try {
            File destinationFile = new File(imagePath);
            imageFile.transferTo(destinationFile);
        } catch (Exception e) {
            throw new IllegalArgumentException("Error occurred while saving image: " + e.getMessage());
        }

        return "uploads/products/" + uniqueFileName;
    }
}
