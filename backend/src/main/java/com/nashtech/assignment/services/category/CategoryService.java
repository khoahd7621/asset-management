package com.nashtech.assignment.services.category;

import com.nashtech.assignment.dto.request.category.CreateNewCategoryRequest;
import com.nashtech.assignment.dto.response.category.CategoryResponse;

import java.util.List;

public interface CategoryService {
    CategoryResponse createNewCategory(CreateNewCategoryRequest createNewCategoryRequest);

    List<CategoryResponse> getAllCategories();
}
