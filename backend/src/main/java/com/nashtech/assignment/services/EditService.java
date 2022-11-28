package com.nashtech.assignment.services;

import java.text.ParseException;

import com.nashtech.assignment.dto.request.asset.EditAssetInformationRequest;
import com.nashtech.assignment.dto.request.user.EditUserRequest;
import com.nashtech.assignment.dto.response.asset.AssetResponse;
import com.nashtech.assignment.dto.response.user.UserResponse;

public interface EditService {
    UserResponse editUserInformation(EditUserRequest userRequest) throws ParseException;

    AssetResponse editAssetInformation(Long idAsset, EditAssetInformationRequest editAssetInformationRequest)
            throws ParseException;
}
