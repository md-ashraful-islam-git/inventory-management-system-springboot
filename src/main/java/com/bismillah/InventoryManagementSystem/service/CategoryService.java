package com.bismillah.InventoryManagementSystem.service;

import com.bismillah.InventoryManagementSystem.dto.CategoryDTO;
import com.bismillah.InventoryManagementSystem.dto.Response;

public interface CategoryService {

    Response createCategory(CategoryDTO categoryDTO);

    Response getAllCategories();

    Response getCategoryById(Long id);

    Response updateCategory(Long id, CategoryDTO categoryDTO);

    Response deleteCategory(Long id);
}
