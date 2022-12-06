package com.nashtech.assignment.services.create;

import com.nashtech.assignment.dto.request.category.CreateNewCategoryRequest;
import com.nashtech.assignment.dto.response.category.CategoryResponse;

public interface CreateCategoryService {
    CategoryResponse createNewCategory(CreateNewCategoryRequest createNewCategoryRequest);
}
