package com.nashtech.assignment.services.create.impl;

import com.nashtech.assignment.data.entities.Category;
import com.nashtech.assignment.data.repositories.CategoryRepository;
import com.nashtech.assignment.dto.request.category.CreateNewCategoryRequest;
import com.nashtech.assignment.dto.response.category.CategoryResponse;
import com.nashtech.assignment.exceptions.BadRequestException;
import com.nashtech.assignment.mappers.CategoryMapper;
import com.nashtech.assignment.services.create.CreateCategoryService;
import lombok.Builder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Builder
public class CreateCategoryServiceImpl implements CreateCategoryService {

    @Autowired
    private CategoryRepository categoryRepository;
    @Autowired
    private CategoryMapper categoryMapper;

    @Override
    public CategoryResponse createNewCategory(CreateNewCategoryRequest createNewCategoryRequest) {
        if (!categoryRepository.findByName(createNewCategoryRequest.getCategoryName()).isEmpty()) {
            throw new BadRequestException("Category is already existed. Please enter a different category");
        }
        if (!categoryRepository.findByPrefixAssetCode(createNewCategoryRequest.getPrefixAssetCode()).isEmpty()){
            throw new BadRequestException("Prefix is already existed. Please enter a different prefix");
        }
        Category category = categoryMapper.mapCategoryRequestToEntity(createNewCategoryRequest);
        categoryRepository.save(category);
        return categoryMapper.toCategoryResponse(category);
    }
}
