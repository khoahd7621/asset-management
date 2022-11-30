package com.nashtech.assignment.services.impl;

import com.nashtech.assignment.data.constants.EAssignStatus;
import com.nashtech.assignment.data.entities.Asset;
import com.nashtech.assignment.data.repositories.AssetRepository;
import lombok.Builder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.nashtech.assignment.data.entities.User;
import com.nashtech.assignment.data.repositories.AssignAssetRepository;
import com.nashtech.assignment.data.repositories.UserRepository;
import com.nashtech.assignment.exceptions.BadRequestException;
import com.nashtech.assignment.exceptions.NotFoundException;
import com.nashtech.assignment.services.DeleteService;
import com.nashtech.assignment.services.SecurityContextService;

import java.util.Optional;

@Service
@Builder
public class DeleteServiceImpl implements DeleteService {
    @Autowired
    AssignAssetRepository assignAssetRepository;
    @Autowired
    UserRepository userRepository;
    @Autowired
    SecurityContextService securityContextService;
    @Autowired
    private AssetRepository assetRepository;

    @Override
    public void deleteUser(String staffCode) {
        if (securityContextService.getCurrentUser().getStaffCode().equals(staffCode)) {
            throw new BadRequestException("Cannot disable yourselft");
        }
        User user = userRepository.findByStaffCode(staffCode);

        if (user == null) {
            throw new NotFoundException("Cannot found user with staff code " + staffCode);
        }

        if (Boolean.TRUE.equals(assignAssetRepository.existsByUserAssignedToAndIsDeletedFalse(user))
                || Boolean.TRUE.equals(assignAssetRepository.existsByUserAssignedByAndIsDeletedFalse(user))) {
            throw new BadRequestException(
                    "There are valid assignments belonging to this user. Please close all assignments before disabling user.");
        }

        user.setDeleted(true);
        userRepository.save(user);

    }

    @Override
    public boolean checkValidUser(String staffCode) {
        if (securityContextService.getCurrentUser().getStaffCode().equals(staffCode)) {
            throw new BadRequestException("Cannot disable yourselft");
        }
        User user = userRepository.findByStaffCode(staffCode);

        if (user == null) {
            throw new NotFoundException("Cannot found user with staff code " + staffCode);
        }

        if (Boolean.TRUE.equals(assignAssetRepository.existsByUserAssignedToAndIsDeletedFalse(user))
                || Boolean.TRUE.equals(assignAssetRepository.existsByUserAssignedByAndIsDeletedFalse(user))) {
            throw new BadRequestException(
                    "There are valid assignments belonging to this user. Please close all assignments before disabling user.");
        }
        return true;
    }

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
