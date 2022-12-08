package com.nashtech.assignment.services.get.impl;

import com.nashtech.assignment.data.entities.Category;
import com.nashtech.assignment.data.repositories.CategoryRepository;
import com.nashtech.assignment.dto.response.category.CategoryResponse;
import com.nashtech.assignment.mappers.CategoryMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Sort;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class GetCategoryServiceImplTest {

    private GetCategoryServiceImpl getCategoryServiceImpl;
    private CategoryRepository categoryRepository;
    private CategoryMapper categoryMapper;

    private Category category;

    @BeforeEach
    void setup() {
        categoryRepository = mock(CategoryRepository.class);
        categoryMapper = mock(CategoryMapper.class);
        getCategoryServiceImpl = GetCategoryServiceImpl.builder()
                .categoryRepository(categoryRepository)
                .categoryMapper(categoryMapper).build();
        category = mock(Category.class);
    }

    @Test
    void getAllCategories_ShouldReturnData() {
        List<Category> categoryList = new ArrayList<>();
        categoryList.add(category);
        CategoryResponse categoryResponse = mock(CategoryResponse.class);
        List<CategoryResponse> expected = new ArrayList<>();
        expected.add(categoryResponse);

        when(categoryRepository.findAll(Sort.by("name").ascending())).thenReturn(categoryList);
        when(categoryMapper.toListCategoriesResponse(categoryList)).thenReturn(expected);

        List<CategoryResponse> actual = getCategoryServiceImpl.getAllCategories();

        assertThat(actual, is(expected));
    }
}