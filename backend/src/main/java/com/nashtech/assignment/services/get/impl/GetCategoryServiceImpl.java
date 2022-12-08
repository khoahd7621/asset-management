package com.nashtech.assignment.services.get.impl;

import com.nashtech.assignment.data.entities.Category;
import com.nashtech.assignment.data.repositories.CategoryRepository;
import com.nashtech.assignment.dto.response.category.CategoryResponse;
import com.nashtech.assignment.mappers.CategoryMapper;
import com.nashtech.assignment.services.get.GetCategoryService;
import lombok.Builder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Builder
public class GetCategoryServiceImpl implements GetCategoryService {

    @Autowired
    private CategoryRepository categoryRepository;
    @Autowired
    private CategoryMapper categoryMapper;

    @Override
    public List<CategoryResponse> getAllCategories() {
        List<Category> categoryList = categoryRepository.findAll(Sort.by("name").ascending());
        return categoryMapper.toListCategoriesResponse(categoryList);
    }
}
