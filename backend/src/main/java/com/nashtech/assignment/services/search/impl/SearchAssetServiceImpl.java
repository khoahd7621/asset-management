package com.nashtech.assignment.services.search.impl;

import com.nashtech.assignment.data.entities.Asset;
import com.nashtech.assignment.data.entities.User;
import com.nashtech.assignment.data.repositories.AssetRepository;
import com.nashtech.assignment.dto.request.asset.SearchAssetRequest;
import com.nashtech.assignment.dto.response.PaginationResponse;
import com.nashtech.assignment.dto.response.asset.AssetResponse;
import com.nashtech.assignment.mappers.AssetMapper;
import com.nashtech.assignment.services.auth.SecurityContextService;
import com.nashtech.assignment.services.search.SearchAssetService;
import com.nashtech.assignment.utils.PageableUtil;
import lombok.Builder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Builder
public class SearchAssetServiceImpl implements SearchAssetService {

    @Autowired
    private AssetRepository assetRepository;
    @Autowired
    private AssetMapper assetMapper;
    @Autowired
    private PageableUtil pageableUtil;
    @Autowired
    private SecurityContextService securityContextService;

    @Override
    public PaginationResponse<List<AssetResponse>> searchAllAssetsByKeyWordInStatusesAndCategoriesWithPagination(
            SearchAssetRequest searchAssetRequest) {
        User user = securityContextService.getCurrentUser();
        Pageable pageable = pageableUtil.getPageable(searchAssetRequest.getPage(),
                searchAssetRequest.getLimit(), searchAssetRequest.getSortField(),
                searchAssetRequest.getSortType());
        Page<Asset> assetList = assetRepository.findAllAssetsByQueryAndStatusesAndCategoryIds(
                searchAssetRequest.getKeyword(), searchAssetRequest.getStatuses(),
                searchAssetRequest.getCategoryIds(), user.getLocation(), pageable);
        List<AssetResponse> assetResponseList = assetMapper.toListAssetsResponse(assetList.toList());
        return PaginationResponse.<List<AssetResponse>>builder().data(assetResponseList)
                .totalPage(assetList.getTotalPages()).totalRow(assetList.getTotalElements()).build();
    }
}
