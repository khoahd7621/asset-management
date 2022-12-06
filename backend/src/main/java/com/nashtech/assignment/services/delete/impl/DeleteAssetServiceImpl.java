package com.nashtech.assignment.services.delete.impl;

import com.nashtech.assignment.data.constants.EAssignStatus;
import com.nashtech.assignment.data.entities.Asset;
import com.nashtech.assignment.data.repositories.AssetRepository;
import com.nashtech.assignment.data.repositories.AssignAssetRepository;
import com.nashtech.assignment.exceptions.BadRequestException;
import com.nashtech.assignment.exceptions.NotFoundException;
import com.nashtech.assignment.services.delete.DeleteAssetService;
import lombok.Builder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@Builder
public class DeleteAssetServiceImpl implements DeleteAssetService {

    @Autowired
    private AssignAssetRepository assignAssetRepository;
    @Autowired
    private AssetRepository assetRepository;

    @Override
    public void deleteAssetByAssetId(Long assetId) {
        Optional<Asset> assetOptional = assetRepository.findByIdAndIsDeletedFalse(assetId);
        if (assetOptional.isEmpty()) {
            throw new NotFoundException("Don't exist asset with this assetId.");
        }
        boolean isAssigned = assignAssetRepository
                .existsByAssetIdAndStatusAndIsDeletedFalse(assetId, EAssignStatus.ACCEPTED);
        if (isAssigned) {
            throw new BadRequestException("Asset already assigned. Invalid for delete.");
        }
        Asset asset = assetOptional.get();
        asset.setDeleted(true);
        assetRepository.save(asset);
    }
}
