package com.nashtech.assignment.services.get;

import com.nashtech.assignment.dto.response.category.CategoryResponse;

import java.util.List;

public interface GetCategoryService {
    List<CategoryResponse> getAllCategories();

}
