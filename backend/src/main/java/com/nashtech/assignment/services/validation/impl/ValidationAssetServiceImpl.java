package com.nashtech.assignment.services.validation.impl;

import com.nashtech.assignment.data.constants.EAssetStatus;
import com.nashtech.assignment.data.entities.Asset;
import com.nashtech.assignment.data.repositories.AssetRepository;
import com.nashtech.assignment.exceptions.BadRequestException;
import com.nashtech.assignment.exceptions.NotFoundException;
import com.nashtech.assignment.services.validation.ValidationAssetService;
import lombok.Builder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@Builder
public class ValidationAssetServiceImpl implements ValidationAssetService {

    @Autowired
    private AssetRepository assetRepository;

    @Override
    public Asset validationAssetAssignedToAssignment(long assetId) {
        Optional<Asset> assetOpt = assetRepository.findByIdAndIsDeletedFalse(assetId);
        if (assetOpt.isEmpty()) {
            throw new NotFoundException("Not exist asset with this asset id.");
        }
        if (assetOpt.get().getStatus() != EAssetStatus.AVAILABLE) {
            throw new BadRequestException("Can only assign asset with status available.");
        }
        return assetOpt.get();
    }
}
