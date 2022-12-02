package com.nashtech.assignment.services.get.impl;

import com.nashtech.assignment.data.constants.EAssetStatus;
import com.nashtech.assignment.data.entities.Asset;
import com.nashtech.assignment.data.entities.User;
import com.nashtech.assignment.data.repositories.AssetRepository;
import com.nashtech.assignment.dto.response.PaginationResponse;
import com.nashtech.assignment.dto.response.asset.AssetResponse;
import com.nashtech.assignment.mappers.AssetMapper;
import com.nashtech.assignment.services.SecurityContextService;
import com.nashtech.assignment.services.get.GetAssetService;
import com.nashtech.assignment.utils.PageableUtil;
import lombok.Builder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Builder
public class GetAssetServiceImpl implements GetAssetService {

    @Autowired
    private AssetRepository assetRepository;
    @Autowired
    private AssetMapper assetMapper;
    @Autowired
    private SecurityContextService securityContextService;
    @Autowired
    private PageableUtil pageableUtil;

    @Override
    public List<AssetResponse> getAllAssetByAssetStatus(EAssetStatus assetStatus) {
        User user = securityContextService.getCurrentUser();
        List<Asset> assetList = assetRepository.findAllByStatusAndLocationAndIsDeletedFalse(assetStatus, user.getLocation());
        return assetMapper.toListAssetsResponse(assetList);
    }

    @Override
    public PaginationResponse<List<AssetResponse>> getAllAssetByAssetStatusWithPagination(
            EAssetStatus assetStatus,
            Integer page,
            Integer limit,
            String sortField,
            String sortType) {
        Pageable pageable = pageableUtil.getPageable(page, limit, sortField, sortType);
        User user = securityContextService.getCurrentUser();
        Page<Asset> assetPage = assetRepository.findAllByStatusAndLocationAndIsDeletedFalse(assetStatus, user.getLocation(), pageable);
        List<Asset> assetList = assetPage.toList();
        return PaginationResponse.<List<AssetResponse>>builder()
                .data(assetMapper.toListAssetsResponse(assetList))
                .totalRow(assetPage.getTotalElements())
                .totalPage(assetPage.getTotalPages()).build();
    }
}
