package com.nashtech.assignment.services.search;

import com.nashtech.assignment.dto.request.asset.SearchAssetRequest;
import com.nashtech.assignment.dto.response.PaginationResponse;
import com.nashtech.assignment.dto.response.asset.AssetResponse;

import java.util.List;

public interface SearchAssetService {
    PaginationResponse<List<AssetResponse>> searchAllAssetsByKeyWordInStatusesAndCategoriesWithPagination(
            SearchAssetRequest searchAssetRequest);
}
