package com.nashtech.assignment.services;

import java.text.ParseException;
import java.util.List;

import org.springframework.data.domain.Pageable;

import com.nashtech.assignment.data.constants.EAssignStatus;
import com.nashtech.assignment.data.entities.AssignAsset;
import com.nashtech.assignment.dto.request.asset.SearchFilterAssetRequest;
import com.nashtech.assignment.dto.response.PaginationResponse;
import com.nashtech.assignment.dto.response.asset.AssetResponse;
import com.nashtech.assignment.dto.response.assignment.AssignAssetResponse;

public interface FilterService {
    PaginationResponse<List<AssetResponse>> filterAllAssetsByLocationAndKeyWordInStatusesAndCategoriesWithPagination(
            SearchFilterAssetRequest searchFilterAssetRequest);

}
