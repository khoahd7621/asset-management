package com.nashtech.assignment.services;

import com.nashtech.assignment.dto.response.asset.AssetAndHistoriesResponse;
import com.nashtech.assignment.dto.response.assignment.AssignAssetResponse;
import com.nashtech.assignment.dto.response.category.CategoryResponse;
import com.nashtech.assignment.dto.response.user.UserResponse;

import java.util.List;

public interface GetService {
    List<CategoryResponse> getAllCategories();

    AssetAndHistoriesResponse getAssetAndItsHistoriesByAssetId(long assetId);

    AssignAssetResponse getAssignAssetDetails(Long id);

    void checkAssetIsValidForDeleteOrNot(Long assetId);
}
