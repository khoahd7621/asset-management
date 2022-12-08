package com.nashtech.assignment.services.edit.impl;

import com.nashtech.assignment.data.constants.EAssetStatus;
import com.nashtech.assignment.data.constants.EReturnStatus;
import com.nashtech.assignment.data.entities.Asset;
import com.nashtech.assignment.data.entities.AssignAsset;
import com.nashtech.assignment.data.entities.ReturnAsset;
import com.nashtech.assignment.data.entities.User;
import com.nashtech.assignment.data.repositories.AssetRepository;
import com.nashtech.assignment.data.repositories.AssignAssetRepository;
import com.nashtech.assignment.data.repositories.ReturnAssetRepository;
import com.nashtech.assignment.exceptions.BadRequestException;
import com.nashtech.assignment.exceptions.NotFoundException;
import com.nashtech.assignment.services.auth.SecurityContextService;
import com.nashtech.assignment.services.edit.EditReturnAssetService;
import lombok.Builder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Optional;

@Service
@Builder
public class EditReturnAssetServiceImpl implements EditReturnAssetService {

    @Autowired
    private ReturnAssetRepository returnAssetRepository;
    @Autowired
    private AssignAssetRepository assignAssetRepository;
    @Autowired
    private AssetRepository assetRepository;
    @Autowired
    private SecurityContextService securityContext;

    @Override
    public void completeReturnRequest(long id) {
        Date today = new Date();
        User currentUser = securityContext.getCurrentUser();
        Optional<ReturnAsset> returnAssetOtp = returnAssetRepository.findById(id);

        if (returnAssetOtp.isEmpty()) {
            throw new NotFoundException("Cannot found return asset with id " + id);
        }

        ReturnAsset returnAsset = returnAssetOtp.get();

        if (!EReturnStatus.WAITING_FOR_RETURNING.equals(returnAsset.getStatus())) {
            throw new BadRequestException("Cannot completed this request");
        }

        returnAsset.setStatus(EReturnStatus.COMPLETED);
        returnAsset.setReturnedDate(today);
        returnAsset.setUserAcceptedReturn(currentUser);
        returnAssetRepository.save(returnAsset);

        AssignAsset assignAsset = returnAsset.getAssignAsset();
        Asset asset = returnAsset.getAsset();

        asset.setStatus(EAssetStatus.AVAILABLE);
        assignAsset.setDeleted(true);

        assetRepository.save(asset);
        assignAssetRepository.save(assignAsset);
    }

}
