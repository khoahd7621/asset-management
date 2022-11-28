package com.nashtech.assignment.services;

import com.nashtech.assignment.dto.request.asset.SearchFilterAssetRequest;
import com.nashtech.assignment.dto.response.PaginationResponse;
import com.nashtech.assignment.dto.response.asset.AssetResponse;

import java.util.List;

public interface FilterService {
    PaginationResponse<List<AssetResponse>> filterAllAssetsByLocationAndKeyWordInStatusesAndCategoriesWithPagination(
            SearchFilterAssetRequest searchFilterAssetRequest
    );
}
