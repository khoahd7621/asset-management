package com.nashtech.assignment.services.edit.impl;

import com.nashtech.assignment.data.constants.EAssetStatus;
import com.nashtech.assignment.data.constants.EAssignStatus;
import com.nashtech.assignment.data.entities.Asset;
import com.nashtech.assignment.data.entities.AssignAsset;
import com.nashtech.assignment.data.entities.User;
import com.nashtech.assignment.data.repositories.AssetRepository;
import com.nashtech.assignment.data.repositories.AssignAssetRepository;
import com.nashtech.assignment.dto.request.assignment.EditAssignmentRequest;
import com.nashtech.assignment.dto.response.assignment.AssignAssetResponse;
import com.nashtech.assignment.exceptions.BadRequestException;
import com.nashtech.assignment.exceptions.NotFoundException;
import com.nashtech.assignment.mappers.AssignAssetMapper;
import com.nashtech.assignment.services.edit.EditAssignAssetService;
import com.nashtech.assignment.services.validation.ValidationAssetService;
import com.nashtech.assignment.services.validation.ValidationUserService;
import com.nashtech.assignment.utils.CompareDateUtil;
import lombok.Builder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Optional;

@Service
@Builder
public class EditAssignAssetServiceImpl implements EditAssignAssetService {

    @Autowired
    private AssignAssetRepository assignAssetRepository;
    @Autowired
    private AssetRepository assetRepository;
    @Autowired
    private AssignAssetMapper assignAssetMapper;
    @Autowired
    private ValidationAssetService validationAssetService;
    @Autowired
    private ValidationUserService validationUserService;
    @Autowired
    private CompareDateUtil compareDateUtil;

    @Override
    public AssignAssetResponse editAssignment(Long assignmentId, EditAssignmentRequest editAssignmentRequest) {
        Optional<AssignAsset> assignAssetOpt = assignAssetRepository.findByIdAndIsDeletedFalse(assignmentId);
        if (assignAssetOpt.isEmpty()) {
            throw new NotFoundException("Not exist assignment with this assignment id.");
        }
        AssignAsset assignAsset = assignAssetOpt.get();
        if (assignAsset.getStatus() != EAssignStatus.WAITING_FOR_ACCEPTANCE) {
            throw new BadRequestException("Can only edit assignment with status waiting for acceptance.");
        }
        Date oldAssignedDate = assignAsset.getAssignedDate();
        Date newAssignedDate = editAssignmentRequest.getAssignedDate();
        if (!compareDateUtil.isEquals(oldAssignedDate, newAssignedDate)
                && compareDateUtil.isAfter(new Date(), newAssignedDate)) {
            throw new BadRequestException("Assign date is before today.");
        }
        Asset oldAsset = assignAsset.getAsset();
        Asset newAsset = null;
        if (oldAsset.getId() != editAssignmentRequest.getAssetId()) {
            newAsset = validationAssetService.validationAssetAssignedToAssignment(
                    editAssignmentRequest.getAssetId(), newAssignedDate);
        } else {
            if (compareDateUtil.isBefore(editAssignmentRequest.getAssignedDate(), oldAsset.getInstalledDate())) {
                throw new BadRequestException("Assigned date cannot before installed date of asset.");
            }
        }
        User currentUserAssignTo = assignAsset.getUserAssignedTo();
        if (currentUserAssignTo.getId() != editAssignmentRequest.getUserId()) {
            currentUserAssignTo = validationUserService.validationUserAssignedToAssignment(
                    editAssignmentRequest.getUserId(), newAssignedDate);
        } else {
            if (compareDateUtil.isBefore(newAssignedDate, currentUserAssignTo.getJoinedDate())) {
                throw new BadRequestException("Assigned date cannot before joined date of user.");
            }
        }
        if (newAsset != null) {
            newAsset.setStatus(EAssetStatus.ASSIGNED);
            oldAsset.setStatus(EAssetStatus.AVAILABLE);
            assetRepository.save(oldAsset);
            assignAsset.setAsset(newAsset);
        }
        assignAsset.setUserAssignedTo(currentUserAssignTo);
        assignAsset.setAssignedDate(editAssignmentRequest.getAssignedDate());
        assignAsset.setNote(editAssignmentRequest.getNote());
        assignAsset = assignAssetRepository.save(assignAsset);
        return assignAssetMapper.toAssignAssetResponse(assignAsset);
    }

}
