package com.nashtech.assignment.mappers;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.nashtech.assignment.data.entities.Category;
import com.nashtech.assignment.dto.response.category.CategoryResponse;

@Component
public class CategoryMapper {

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
