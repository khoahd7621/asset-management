package com.nashtech.assignment.services.create;

import com.nashtech.assignment.dto.request.asset.CreateNewAssetRequest;
import com.nashtech.assignment.dto.response.asset.AssetResponse;

import java.text.ParseException;

public interface CreateAssetService {
    AssetResponse createAssetResponse(CreateNewAssetRequest createNewAssetRequest) throws ParseException;
}
