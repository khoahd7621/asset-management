package com.nashtech.assignment.services.edit;

import com.nashtech.assignment.dto.request.asset.EditAssetInformationRequest;
import com.nashtech.assignment.dto.response.asset.AssetResponse;

import java.text.ParseException;

public interface EditAssetService {
    AssetResponse editAssetInformation(Long idAsset, EditAssetInformationRequest editAssetInformationRequest) throws ParseException;
}
