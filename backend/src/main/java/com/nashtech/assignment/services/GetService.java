package com.nashtech.assignment.services;

import com.nashtech.assignment.dto.response.asset.AssetAndHistoriesResponse;
import com.nashtech.assignment.dto.response.category.CategoryResponse;

import java.util.List;

public interface GetService {
    List<CategoryResponse> getAllCategories();

    AssetAndHistoriesResponse getAssetAndItsHistoriesByAssetId(long assetId);

}
