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
import com.nashtech.assignment.exceptions.ForbiddenException;
import com.nashtech.assignment.exceptions.NotFoundException;
import com.nashtech.assignment.mappers.AssignAssetMapper;
import com.nashtech.assignment.services.auth.SecurityContextService;
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
    @Autowired
    private SecurityContextService securityContextService;

    @Override
    public AssignAssetResponse acceptAssignAsset(Long idAssignAsset) {
        Optional<AssignAsset> assignAssetOpt = assignAssetRepository.findById(idAssignAsset);
        if (assignAssetOpt.isEmpty()) {
            throw new NotFoundException("Assignment not found");
        }
        if (!assignAssetOpt.get().getStatus().equals(EAssignStatus.WAITING_FOR_ACCEPTANCE)) {
            throw new BadRequestException("Assignment is not waiting for acceptance");
        }

        Date today = new Date();
        Date assignedDate = assignAssetOpt.get().getAssignedDate();
        if (compareDateUtil.isBefore(today, assignedDate)) {
            throw new BadRequestException("Assign date is after today.");
        }

        User currentUser = securityContextService.getCurrentUser();
        if (currentUser.getId() != assignAssetOpt.get().getUserAssignedTo().getId())
            throw new ForbiddenException("Current user is not match to this assignment.");

        assignAssetOpt.get().setStatus(EAssignStatus.ACCEPTED);
        assignAssetRepository.save(assignAssetOpt.get());
        return assignAssetMapper.toAssignAssetResponse(assignAssetOpt.get());
    }

    @Override
    public AssignAssetResponse declineAssignAsset(Long idAssignAsset) {
        Optional<AssignAsset> assignAssetOpt = assignAssetRepository.findById(idAssignAsset);
        if (assignAssetOpt.isEmpty()) {
            throw new NotFoundException("Assignment not found");
        }
        if (!assignAssetOpt.get().getStatus().equals(EAssignStatus.WAITING_FOR_ACCEPTANCE)) {
            throw new BadRequestException("Assignment is not waiting for acceptance");
        }

        Date today = new Date();
        Date assignedDate = assignAssetOpt.get().getAssignedDate();
        if (compareDateUtil.isBefore(today, assignedDate)) {
            throw new BadRequestException("Assign date is after today.");
        }

        User currentUser = securityContextService.getCurrentUser();
        if (currentUser.getId() != assignAssetOpt.get().getUserAssignedTo().getId())
            throw new ForbiddenException("Current user is not match to this assignment.");

        assignAssetOpt.get().setStatus(EAssignStatus.DECLINED);
        assignAssetRepository.save(assignAssetOpt.get());
        return assignAssetMapper.toAssignAssetResponse(assignAssetOpt.get());
    }

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
        Date today = new Date();
        if (!compareDateUtil.isEquals(oldAssignedDate, newAssignedDate)
                && compareDateUtil.isAfter(today, newAssignedDate)) {
            throw new BadRequestException("Assign date is before today.");
        }
        User currentUserAssignTo = assignAsset.getUserAssignedTo();
        if (currentUserAssignTo.getId() != editAssignmentRequest.getUserId()) {
            currentUserAssignTo = validationUserService.validationUserAssignedToAssignment(
                    editAssignmentRequest.getUserId());
        }
        Asset currentAssetAssignedTo = assignAsset.getAsset();
        if (currentAssetAssignedTo.getId() != editAssignmentRequest.getAssetId()) {
            Asset newAsset = validationAssetService
                    .validationAssetAssignedToAssignment(editAssignmentRequest.getAssetId());
            newAsset.setStatus(EAssetStatus.ASSIGNED);
            currentAssetAssignedTo.setStatus(EAssetStatus.AVAILABLE);
            assetRepository.save(currentAssetAssignedTo);
            assignAsset.setAsset(newAsset);
        }
        assignAsset.setUserAssignedTo(currentUserAssignTo);
        assignAsset.setAssignedDate(editAssignmentRequest.getAssignedDate());
        assignAsset.setNote(editAssignmentRequest.getNote());
        assignAsset = assignAssetRepository.save(assignAsset);
        return assignAssetMapper.toAssignAssetResponse(assignAsset);
    }

}
