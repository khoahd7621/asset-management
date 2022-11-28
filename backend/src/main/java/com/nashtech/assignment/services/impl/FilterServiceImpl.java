package com.nashtech.assignment.services.impl;

import com.nashtech.assignment.data.entities.Asset;
import com.nashtech.assignment.data.entities.User;
import com.nashtech.assignment.data.repositories.AssetRepository;
import com.nashtech.assignment.dto.request.asset.SearchFilterAssetRequest;
import com.nashtech.assignment.dto.response.PaginationResponse;
import com.nashtech.assignment.dto.response.asset.AssetResponse;
import com.nashtech.assignment.mappers.AssetMapper;
import com.nashtech.assignment.services.FilterService;
import com.nashtech.assignment.services.SecurityContextService;
import com.nashtech.assignment.utils.PageableUtil;
import lombok.Builder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Builder
public class FilterServiceImpl implements FilterService {

    @Autowired
    private AssetRepository assetRepository;
    @Autowired
    private AssetMapper assetMapper;
    @Autowired
    private PageableUtil pageableUtil;
    @Autowired
    private SecurityContextService securityContextService;

    @Override
    public PaginationResponse<List<AssetResponse>> filterAllAssetsByLocationAndKeyWordInStatusesAndCategoriesWithPagination(
            SearchFilterAssetRequest searchFilterAssetRequest
    ) {
        User user = securityContextService.getCurrentUser();
        Pageable pageable = pageableUtil.getPageable(
                searchFilterAssetRequest.getPage(),
                searchFilterAssetRequest.getLimit(),
                searchFilterAssetRequest.getSortField(),
                searchFilterAssetRequest.getSortType());
        Page<Asset> assetList = assetRepository.findAllAssetsByQueryAndStatusesAndCategoryIds(
                searchFilterAssetRequest.getKeyword(),
                searchFilterAssetRequest.getStatuses(),
                searchFilterAssetRequest.getCategoryIds(),
                user.getLocation(), pageable);
        List<AssetResponse> assetResponseList = assetMapper.toListAssetsResponse(assetList.toList());
        return PaginationResponse.<List<AssetResponse>>builder()
                .data(assetResponseList)
                .totalPage(assetList.getTotalPages())
                .totalRow(assetList.getTotalElements()).build();
    }
}
