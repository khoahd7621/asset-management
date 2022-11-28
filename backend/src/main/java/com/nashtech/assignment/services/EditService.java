package com.nashtech.assignment.services;

import java.text.ParseException;

import com.nashtech.assignment.dto.request.asset.EditAssetInformationRequest;
import com.nashtech.assignment.dto.response.asset.AssetResponse;

public interface EditService {
    AssetResponse editAssetInformation(Long idAsset,
            EditAssetInformationRequest editAssetInformationRequest)
            throws ParseException;
}
