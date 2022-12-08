package com.nashtech.assignment.mappers;

import com.nashtech.assignment.data.entities.Category;
import com.nashtech.assignment.dto.request.category.CreateNewCategoryRequest;
import com.nashtech.assignment.dto.response.category.CategoryResponse;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class CategoryMapper {

    public Category toCategory(CreateNewCategoryRequest createNewCategoryRequest) {
        return Category.builder()
                .name(createNewCategoryRequest.getCategoryName())
                .prefixAssetCode(createNewCategoryRequest.getPrefixAssetCode())
                .build();
    }

    public CategoryResponse toCategoryResponse(Category category) {
        return CategoryResponse.builder()
                .id(category.getId())
                .name(category.getName())
                .prefixAssetCode(category.getPrefixAssetCode()).build();
    }

    public List<CategoryResponse> toListCategoriesResponse(List<Category> categoryList) {
        return categoryList.stream().map(this::toCategoryResponse).collect(Collectors.toList());
    }
}
