package com.nashtech.assignment.services.category.impl;

import com.nashtech.assignment.data.entities.Category;
import com.nashtech.assignment.data.repositories.CategoryRepository;
import com.nashtech.assignment.dto.request.category.CreateNewCategoryRequest;
import com.nashtech.assignment.dto.response.category.CategoryResponse;
import com.nashtech.assignment.exceptions.BadRequestException;
import com.nashtech.assignment.mappers.CategoryMapper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Sort;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class CategoryServiceImplTest {

    private CategoryServiceImpl categoryServiceImpl;
    private CategoryRepository categoryRepository;
    private CategoryMapper categoryMapper;
    private Category category;

    @BeforeEach
    void beforeEach() {
        categoryRepository = mock(CategoryRepository.class);
        categoryMapper = mock(CategoryMapper.class);
        categoryServiceImpl = CategoryServiceImpl.builder()
                .categoryRepository(categoryRepository)
                .categoryMapper(categoryMapper).build();
        category = mock(Category.class);
    }

    @Test
    void createNewCategory_WhenCategoryNameExisted_ShouldReturnException() {
        CreateNewCategoryRequest createNewCategoryRequest = CreateNewCategoryRequest.builder()
                .categoryName("categoryName")
                .prefixAssetCode("CN")
                .build();

        when(categoryRepository.findByName(createNewCategoryRequest.getCategoryName()))
                .thenReturn(Optional.of(category));

        BadRequestException actualException = Assertions.assertThrows(BadRequestException.class,
                () -> categoryServiceImpl.createNewCategory(createNewCategoryRequest));

        Assertions.assertEquals("Category is already existed. Please enter a different category", actualException.getMessage());
    }

    @Test
    void createNewCategory_WhenAssetCodeExisted_ShouldReturnException() {
        CreateNewCategoryRequest createNewCategoryRequest = CreateNewCategoryRequest.builder()
                .categoryName("categoryName")
                .prefixAssetCode("CN")
                .build();

        when(categoryRepository.findByName(createNewCategoryRequest.getCategoryName())).thenReturn(Optional.empty());
        when(categoryRepository.findByPrefixAssetCode(createNewCategoryRequest.getPrefixAssetCode()))
                .thenReturn(Optional.of(category));

        BadRequestException actualException = Assertions.assertThrows(BadRequestException.class,
                () -> categoryServiceImpl.createNewCategory(createNewCategoryRequest));

        Assertions.assertEquals("Prefix is already existed. Please enter a different prefix", actualException.getMessage());
    }

    @Test
    void createNewCategory_WhenDataValid_ShouldReturnData() throws BadRequestException {
        CreateNewCategoryRequest createNewCategoryRequest = CreateNewCategoryRequest.builder()
                .categoryName("categoryName")
                .prefixAssetCode("CN")
                .build();

        CategoryResponse expected = mock(CategoryResponse.class);

        when(categoryRepository.findByName(createNewCategoryRequest.getCategoryName())).thenReturn(Optional.empty());
        when(categoryRepository.findByPrefixAssetCode(createNewCategoryRequest.getPrefixAssetCode()))
                .thenReturn(Optional.empty());
        when(categoryMapper.toCategory(createNewCategoryRequest)).thenReturn(category);
        when(categoryRepository.save(category)).thenReturn(category);
        when(categoryMapper.toCategoryResponse(category)).thenReturn(expected);

        CategoryResponse actual = categoryServiceImpl.createNewCategory(createNewCategoryRequest);

        assertThat(actual, is(expected));
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

        List<CategoryResponse> actual = categoryServiceImpl.getAllCategories();

        assertThat(actual, is(expected));
    }
}