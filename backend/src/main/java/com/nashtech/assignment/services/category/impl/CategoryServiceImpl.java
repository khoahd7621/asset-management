package com.nashtech.assignment.services.category.impl;

import com.nashtech.assignment.data.entities.Category;
import com.nashtech.assignment.data.repositories.CategoryRepository;
import com.nashtech.assignment.dto.request.category.CreateNewCategoryRequest;
import com.nashtech.assignment.dto.response.category.CategoryResponse;
import com.nashtech.assignment.exceptions.BadRequestException;
import com.nashtech.assignment.mappers.CategoryMapper;
import com.nashtech.assignment.services.category.CategoryService;
import lombok.Builder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Builder
public class CategoryServiceImpl implements CategoryService {

    @Autowired
    private CategoryRepository categoryRepository;
    @Autowired
    private CategoryMapper categoryMapper;

    @Override
    public CategoryResponse createNewCategory(CreateNewCategoryRequest createNewCategoryRequest) {
        if (!categoryRepository.findByName(createNewCategoryRequest.getCategoryName()).isEmpty()) {
            throw new BadRequestException("Category is already existed. Please enter a different category");
        }
        if (!categoryRepository.findByPrefixAssetCode(createNewCategoryRequest.getPrefixAssetCode()).isEmpty()) {
            throw new BadRequestException("Prefix is already existed. Please enter a different prefix");
        }
        Category category = categoryMapper.toCategory(createNewCategoryRequest);
        categoryRepository.save(category);
        return categoryMapper.toCategoryResponse(category);
    }

    @Override
    public List<CategoryResponse> getAllCategories() {
        List<Category> categoryList = categoryRepository.findAll(Sort.by("name").ascending());
        return categoryMapper.toListCategoriesResponse(categoryList);
    }
}
