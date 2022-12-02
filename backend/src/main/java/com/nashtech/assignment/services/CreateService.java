package com.nashtech.assignment.services;

import com.nashtech.assignment.dto.request.asset.CreateNewAssetRequest;
import com.nashtech.assignment.dto.request.category.CreateNewCategoryRequest;
import com.nashtech.assignment.dto.request.user.CreateNewUserRequest;
import com.nashtech.assignment.dto.response.asset.AssetResponse;
import com.nashtech.assignment.dto.response.category.CategoryResponse;
import com.nashtech.assignment.dto.response.user.UserResponse;

import java.text.ParseException;

public interface CreateService {
    UserResponse createNewUser(CreateNewUserRequest createNewUserRequest) throws ParseException;

    AssetResponse createAssetResponse(CreateNewAssetRequest createNewAssetRequest) throws ParseException;

    CategoryResponse createNewCategory(CreateNewCategoryRequest createNewCategoryRequest);
}
