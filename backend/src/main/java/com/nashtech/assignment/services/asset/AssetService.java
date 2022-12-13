package com.nashtech.assignment.services.asset;

import com.nashtech.assignment.dto.request.asset.CreateNewAssetRequest;
import com.nashtech.assignment.dto.request.asset.EditAssetInformationRequest;
import com.nashtech.assignment.dto.response.asset.AssetAndHistoriesResponse;
import com.nashtech.assignment.dto.response.asset.AssetResponse;
import com.nashtech.assignment.dto.response.report.AssetReportResponse;

import java.text.ParseException;
import java.util.List;

public interface AssetService {
    AssetResponse createAssetResponse(CreateNewAssetRequest createNewAssetRequest) throws ParseException;

    void deleteAssetByAssetId(Long assetId);

    AssetResponse editAssetInformation(Long idAsset, EditAssetInformationRequest editAssetInformationRequest) throws ParseException;

    AssetAndHistoriesResponse getAssetAndItsHistoriesByAssetId(long assetId);

    void checkAssetIsValidForDeleteOrNot(Long assetId);

    List<AssetReportResponse> getAssetReport();
}
