package com.nashtech.assignment.services.get;

import com.nashtech.assignment.dto.response.asset.AssetAndHistoriesResponse;

public interface GetAssetService {
    AssetAndHistoriesResponse getAssetAndItsHistoriesByAssetId(long assetId);

    void checkAssetIsValidForDeleteOrNot(Long assetId);
}
