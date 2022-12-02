package com.nashtech.assignment.services.get;

import com.nashtech.assignment.data.constants.EAssetStatus;
import com.nashtech.assignment.dto.response.PaginationResponse;
import com.nashtech.assignment.dto.response.asset.AssetAndHistoriesResponse;
import com.nashtech.assignment.dto.response.asset.AssetResponse;

import java.util.List;

public interface GetAssetService {
    List<AssetResponse> getAllAssetByAssetStatus(EAssetStatus assetStatus);

    PaginationResponse<List<AssetResponse>> getAllAssetByAssetStatusWithPagination(EAssetStatus assetStatus, Integer page, Integer limit, String sortField, String sortType);

    AssetAndHistoriesResponse getAssetAndItsHistoriesByAssetId(long assetId);

    void checkAssetIsValidForDeleteOrNot(Long assetId);
}
