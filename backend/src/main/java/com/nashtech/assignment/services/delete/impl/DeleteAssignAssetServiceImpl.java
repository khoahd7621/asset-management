package com.nashtech.assignment.services.delete.impl;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.nashtech.assignment.data.constants.EAssetStatus;
import com.nashtech.assignment.data.constants.EAssignStatus;
import com.nashtech.assignment.data.entities.Asset;
import com.nashtech.assignment.data.entities.AssignAsset;
import com.nashtech.assignment.data.repositories.AssetRepository;
import com.nashtech.assignment.data.repositories.AssignAssetRepository;
import com.nashtech.assignment.exceptions.BadRequestException;
import com.nashtech.assignment.exceptions.NotFoundException;
import com.nashtech.assignment.services.delete.DeleteAssignAssetService;

import lombok.Builder;

@Service
@Builder
public class DeleteAssignAssetServiceImpl implements DeleteAssignAssetService {

    @Autowired
    private AssignAssetRepository assignAssetRepository;
    @Autowired
    private AssetRepository assetRepository;

    @Override
    public void deleteAssignAsset(Long assignAssetId) {
        Optional<AssignAsset> assignAssetOtp = assignAssetRepository.findById(assignAssetId);

        if (assignAssetOtp.isEmpty()) {
            throw new NotFoundException("Cannot found assign asset with id " + assignAssetId);
        }

        AssignAsset assignAsset = assignAssetOtp.get();

        if (EAssignStatus.ACCEPTED == assignAsset.getStatus()) {
            throw new BadRequestException("Cannot delete this assignment");
        }

        Asset asset = assignAsset.getAsset();

        asset.setStatus(EAssetStatus.AVAILABLE);
        assignAsset.setDeleted(true);
        assetRepository.save(asset);
        assignAssetRepository.save(assignAsset);

    }

}
