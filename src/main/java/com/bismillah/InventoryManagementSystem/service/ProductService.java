package com.bismillah.InventoryManagementSystem.service;

import com.bismillah.InventoryManagementSystem.dto.ProductDTO;
import com.bismillah.InventoryManagementSystem.dto.Response;
import com.bismillah.InventoryManagementSystem.dto.SupplierDTO;
import org.springframework.web.multipart.MultipartFile;

public interface ProductService {

    Response saveProduct(ProductDTO productDTO, MultipartFile imageFile);

    Response updateProduct(ProductDTO productDTO, MultipartFile imageFile);

    Response getAllProducts(String searchText);

    Response getProductById(Long id);

    Response deleteProduct(Long id);
}
