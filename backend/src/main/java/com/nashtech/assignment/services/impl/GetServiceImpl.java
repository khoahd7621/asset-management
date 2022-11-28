package com.nashtech.assignment.services.impl;

import com.nashtech.assignment.data.entities.Asset;
import com.nashtech.assignment.data.entities.Category;
import com.nashtech.assignment.data.repositories.AssetRepository;
import com.nashtech.assignment.data.repositories.CategoryRepository;
import com.nashtech.assignment.dto.response.asset.AssetAndHistoriesResponse;
import com.nashtech.assignment.dto.response.category.CategoryResponse;
import com.nashtech.assignment.exceptions.NotFoundException;
import com.nashtech.assignment.mappers.AssetMapper;
import com.nashtech.assignment.mappers.CategoryMapper;
import com.nashtech.assignment.services.GetService;
import lombok.Builder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@Builder
public class GetServiceImpl implements GetService {

    @Autowired
    private CategoryRepository categoryRepository;
    @Autowired
    private CategoryMapper categoryMapper;
    @Autowired
    private AssetRepository assetRepository;
    @Autowired
    private AssetMapper assetMapper;

    @Override
    public List<CategoryResponse> getAllCategories() {
        List<Category> categoryList = categoryRepository.findAll();
        return categoryMapper.toListCategoriesResponse(categoryList);
    }

    @Override
    public AssetAndHistoriesResponse getAssetAndItsHistoriesByAssetId(long assetId) {
        Optional<Asset> assetOptional = assetRepository.findByIdAndIsDeletedFalse(assetId);
        if (assetOptional.isEmpty()) {
            throw new NotFoundException("Don't exist asset with this assetId.");
        }
        return assetMapper.toAssetAndHistoriesResponse(assetOptional.get());
    }
}
