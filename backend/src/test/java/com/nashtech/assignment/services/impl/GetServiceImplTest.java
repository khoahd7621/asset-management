package com.nashtech.assignment.services.impl;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.mock;

import com.nashtech.assignment.data.entities.Asset;
import com.nashtech.assignment.data.entities.Category;
import com.nashtech.assignment.data.repositories.AssetRepository;
import com.nashtech.assignment.data.repositories.CategoryRepository;
import com.nashtech.assignment.dto.response.asset.AssetAndHistoriesResponse;
import com.nashtech.assignment.dto.response.category.CategoryResponse;
import com.nashtech.assignment.exceptions.NotFoundException;
import com.nashtech.assignment.mappers.AssetMapper;
import com.nashtech.assignment.mappers.CategoryMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Sort;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

class GetServiceImplTest {

    private GetServiceImpl getServiceImpl;
    private CategoryRepository categoryRepository;
    private CategoryMapper categoryMapper;
    private AssetRepository assetRepository;
    private AssetMapper assetMapper;
    private Category category;
    private Asset asset;

    @BeforeEach
    void setup() {
        categoryRepository = mock(CategoryRepository.class);
        categoryMapper = mock(CategoryMapper.class);
        assetRepository = mock(AssetRepository.class);
        assetMapper = mock(AssetMapper.class);
        getServiceImpl = GetServiceImpl.builder()
                .categoryRepository(categoryRepository)
                .categoryMapper(categoryMapper)
                .assetRepository(assetRepository)
                .assetMapper(assetMapper).build();
        category = mock(Category.class);
        asset = mock(Asset.class);
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

        List<CategoryResponse> actual = getServiceImpl.getAllCategories();

        assertThat(actual, is(expected));
    }

    @Test
    void getAssetAndItsHistoriesByAssetId_WhenAssetIdNotExist_ShouldThrowNotFoundException() {
        when(assetRepository.findByIdAndIsDeletedFalse(1L)).thenReturn(Optional.empty());

        NotFoundException actual = assertThrows(NotFoundException.class, () -> {
            getServiceImpl.getAssetAndItsHistoriesByAssetId(1L);
        });

        assertThat(actual.getMessage(), is("Don't exist asset with this assetId."));
    }

    @Test
    void getAssetAndItsHistoriesByAssetId_WhenAssetIdExist_ShouldReturnData() {
        Optional<Asset> assetOptional = Optional.of(asset);
        AssetAndHistoriesResponse expected = mock(AssetAndHistoriesResponse.class);

        when(assetRepository.findByIdAndIsDeletedFalse(1L)).thenReturn(assetOptional);
        when(assetMapper.toAssetAndHistoriesResponse(assetOptional.get())).thenReturn(expected);

        AssetAndHistoriesResponse actual = getServiceImpl.getAssetAndItsHistoriesByAssetId(1L);

        assertThat(actual, is(expected));
    }
}