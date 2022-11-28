package com.nashtech.assignment.mappers;

import com.nashtech.assignment.data.constants.EReturnStatus;
import com.nashtech.assignment.data.entities.Asset;
import com.nashtech.assignment.data.entities.AssignAsset;
import com.nashtech.assignment.data.entities.ReturnAsset;
import com.nashtech.assignment.dto.request.asset.CreateNewAssetRequest;
import com.nashtech.assignment.dto.response.asset.AssetAndHistoriesResponse;
import com.nashtech.assignment.dto.response.asset.AssetHistory;
import com.nashtech.assignment.dto.response.asset.AssetResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

import com.nashtech.assignment.data.repositories.CategoryRepository;
import com.nashtech.assignment.dto.response.category.CategoryResponse;

@Component
public class AssetMapper {
    @Autowired
    private CategoryMapper categoryMapper;

    @Autowired 
    private CategoryRepository categoryRepository;

    public AssetResponse toAssetResponse(Asset asset) {
        return AssetResponse.builder()
                .id(asset.getId())
                .assetName(asset.getName())
                .assetCode(asset.getAssetCode())
                .installedDate(asset.getInstalledDate())
                .specification(asset.getSpecification())
                .status(asset.getStatus())
                .location(asset.getLocation())
                .isDeleted(asset.isDeleted())
                .category(categoryMapper.toCategoryResponse(asset.getCategory())).build();
    }

    public List<AssetResponse> toListAssetsResponse(List<Asset> listAssets) {
        return listAssets.stream().map(this::toAssetResponse).collect(Collectors.toList());
    }

    public AssetHistory toAssetHistory(ReturnAsset returnAsset) {
        AssignAsset assignAsset = returnAsset.getAssignAsset();
        return AssetHistory.builder()
                .assignedDate(assignAsset.getAssignedDate())
                .assignedTo(assignAsset.getUserAssignedTo().getUsername())
                .assignedBy(assignAsset.getUserAssignedBy().getUsername())
                .returnedDate(returnAsset.getReturnedDate()).build();
    }

    public AssetAndHistoriesResponse toAssetAndHistoriesResponse(Asset asset) {
        List<AssetHistory> assetHistories = asset.getReturnAssets().stream()
                .filter(item -> item.getStatus() == EReturnStatus.COMPLETED)
                .map(this::toAssetHistory).collect(Collectors.toList());
        return AssetAndHistoriesResponse.builder()
                .asset(toAssetResponse(asset))
                .histories(assetHistories)
                .build();
    }

    public Asset mapAssetRequestToEntity(CreateNewAssetRequest createNewAssetRequest) {
        Asset asset = Asset.builder()
                .name(createNewAssetRequest.getAssetName())
                .category(categoryRepository.findByName(createNewAssetRequest.getCategoryName()).get())
                .specification(createNewAssetRequest.getSpecification())
                .status(createNewAssetRequest.getAssetStatus())
                .isDeleted(false)
                .build();
        return asset;
    }

    public AssetResponse mapEntityToEditAssetInformationResponse(Asset asset) {
        CategoryResponse category = categoryMapper.toCategoryResponse(asset.getCategory());
        AssetResponse assetResponse = AssetResponse.builder()
                .id(asset.getId())
                .assetCode(asset.getAssetCode())
                .assetName(asset.getName())
                .installedDate(asset.getInstalledDate())
                .specification(asset.getSpecification())
                .status(asset.getStatus())
                .location(asset.getLocation())
                .category(category)
                .build();
        return assetResponse;
    }
}
