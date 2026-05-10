package com.bismillah.InventoryManagementSystem.service.impl;

import com.bismillah.InventoryManagementSystem.dto.CategoryDTO;
import com.bismillah.InventoryManagementSystem.dto.Response;
import com.bismillah.InventoryManagementSystem.dto.UserDTO;
import com.bismillah.InventoryManagementSystem.entity.Category;
import com.bismillah.InventoryManagementSystem.entity.User;
import com.bismillah.InventoryManagementSystem.exception.DeleteConstraintException;
import com.bismillah.InventoryManagementSystem.exception.NotFoundException;
import com.bismillah.InventoryManagementSystem.repository.CategoryRepository;
import com.bismillah.InventoryManagementSystem.repository.ProductRepository;
import com.bismillah.InventoryManagementSystem.service.CategoryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class CategoryServiceImpl implements CategoryService {
    private final CategoryRepository categoryRepository;
    private final ProductRepository productRepository;
    private final ModelMapper modelMapper;

    @Override
    public Response createCategory(CategoryDTO categoryDTO) {
        Category categoryToSave = modelMapper.map(categoryDTO, Category.class);
        categoryRepository.save(categoryToSave);

        return Response.builder()
                .status(200)
                .message("Category Created Successfully")
                .build();
    }

    @Override
    public Response getAllCategories() {
        List<Category> categories = categoryRepository.findAll(Sort.by(Sort.Direction.DESC, "id"));

        List<CategoryDTO> categoryDTOS = modelMapper.map(categories, new TypeToken<List<CategoryDTO>>() {}.getType());



        return Response.builder()
                .status(200)
                .message("success")
                .categories(categoryDTOS)
                .build();
    }

    @Override
    public Response getCategoryById(Long id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Category Not Found"));
        CategoryDTO categoryDTO = modelMapper.map(category, CategoryDTO.class);
        return Response.builder()
                .status(200)
                .message("success")
                .category(categoryDTO)
                .build();

    }

    @Override
    public Response updateCategory(Long id, CategoryDTO categoryDTO) {
        Category existingCategory = categoryRepository.findById(id)
                .orElseThrow(()-> new NotFoundException("Category not found"));

        existingCategory.setName(categoryDTO.getName());
        categoryRepository.save(existingCategory);

        return Response.builder()
                .status(200)
                .message("Category successfully updated")
                .build();

    }

    @Override
    public Response deleteCategory(Long id) {
        log.info("Attempting to delete category with ID: {}", id);
        categoryRepository.findById(id)
                .orElseThrow(()-> new NotFoundException("Category not found"));

        if (productRepository.countByCategoryId(id) > 0) {
            log.warn("Deletion blocked: Category ID {} is linked to products", id);
            throw new DeleteConstraintException("Cannot delete category. It is linked to existing products.");
        }

        categoryRepository.deleteById(id);
        log.info("Category ID {} successfully deleted", id);

        return Response.builder()
                .status(200)
                .message("Category successfully deleted")
                .build();
    }
}
