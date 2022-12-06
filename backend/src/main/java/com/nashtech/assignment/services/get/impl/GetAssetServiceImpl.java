package com.nashtech.assignment.services.get.impl;

import com.nashtech.assignment.data.constants.EAssignStatus;
import com.nashtech.assignment.data.entities.Asset;
import com.nashtech.assignment.data.repositories.AssetRepository;
import com.nashtech.assignment.data.repositories.AssignAssetRepository;
import com.nashtech.assignment.dto.response.asset.AssetAndHistoriesResponse;
import com.nashtech.assignment.exceptions.BadRequestException;
import com.nashtech.assignment.exceptions.NotFoundException;
import com.nashtech.assignment.mappers.AssetMapper;
import com.nashtech.assignment.services.get.GetAssetService;
import lombok.Builder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@Builder
public class GetAssetServiceImpl implements GetAssetService {

    @Autowired
    private AssetRepository assetRepository;
    @Autowired
    private AssetMapper assetMapper;
    @Autowired
    private AssignAssetRepository assignAssetRepository;

    @Override
    public AssetAndHistoriesResponse getAssetAndItsHistoriesByAssetId(long assetId) {
        Optional<Asset> assetOptional = assetRepository.findByIdAndIsDeletedFalse(assetId);
        if (assetOptional.isEmpty()) {
            throw new NotFoundException("Don't exist asset with this assetId.");
        }
        return assetMapper.toAssetAndHistoriesResponse(assetOptional.get());
    }

    @Override
    public void checkAssetIsValidForDeleteOrNot(Long assetId) {
        Optional<Asset> assetOptional = assetRepository.findByIdAndIsDeletedFalse(assetId);
        if (assetOptional.isEmpty()) {
            throw new NotFoundException("Don't exist asset with this assetId.");
        }
        boolean isAssigned = assignAssetRepository
                .existsByAssetIdAndStatusAndIsDeletedFalse(assetId, EAssignStatus.ACCEPTED);
        if (isAssigned) {
            throw new BadRequestException("Asset already assigned. Invalid for delete.");
        }
    }
}
