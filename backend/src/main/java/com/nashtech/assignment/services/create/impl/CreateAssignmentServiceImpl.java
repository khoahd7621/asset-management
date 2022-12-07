package com.nashtech.assignment.services.create.impl;

import com.nashtech.assignment.data.constants.EAssetStatus;
import com.nashtech.assignment.data.constants.EAssignStatus;
import com.nashtech.assignment.data.entities.Asset;
import com.nashtech.assignment.data.entities.AssignAsset;
import com.nashtech.assignment.data.entities.User;
import com.nashtech.assignment.data.repositories.AssignAssetRepository;
import com.nashtech.assignment.dto.request.assignment.CreateNewAssignmentRequest;
import com.nashtech.assignment.dto.response.assignment.AssignAssetResponse;
import com.nashtech.assignment.exceptions.BadRequestException;
import com.nashtech.assignment.mappers.AssignAssetMapper;
import com.nashtech.assignment.services.auth.SecurityContextService;
import com.nashtech.assignment.services.create.CreateAssignmentService;
import com.nashtech.assignment.services.validation.ValidationAssetService;
import com.nashtech.assignment.services.validation.ValidationUserService;
import com.nashtech.assignment.utils.CompareDateUtil;
import lombok.Builder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

@Service
@Builder
public class CreateAssignmentServiceImpl implements CreateAssignmentService {

    @Autowired
    private AssignAssetRepository assignAssetRepository;
    @Autowired
    private AssignAssetMapper assignAssetMapper;
    @Autowired
    private SecurityContextService securityContextService;
    @Autowired
    private CompareDateUtil compareDateUtil;
    @Autowired
    private ValidationAssetService validationAssetService;
    @Autowired
    private ValidationUserService validationUserService;

    @Override
    public AssignAssetResponse createNewAssignment(CreateNewAssignmentRequest createNewAssignmentRequest) throws ParseException {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        Date today = new Date();
        Date assignedDate = dateFormat.parse(createNewAssignmentRequest.getAssignedDate());
        if (compareDateUtil.isAfter(today, assignedDate)) {
            throw new BadRequestException("Assign date is before today.");
        }
        Asset asset = validationAssetService.validationAssetAssignedToAssignment(
                createNewAssignmentRequest.getAssetId());
        User userAssignedTo = validationUserService.validationUserAssignedToAssignment(
                createNewAssignmentRequest.getUserId());
        User currentUser = securityContextService.getCurrentUser();
        asset.setStatus(EAssetStatus.ASSIGNED);
        AssignAsset assignAsset = AssignAsset.builder()
                .asset(asset)
                .assignedDate(assignedDate)
                .userAssignedBy(currentUser)
                .userAssignedTo(userAssignedTo)
                .status(EAssignStatus.WAITING_FOR_ACCEPTANCE)
                .note(createNewAssignmentRequest.getNote())
                .isDeleted(false).build();
        assignAsset = assignAssetRepository.save(assignAsset);
        return assignAssetMapper.toAssignAssetResponse(assignAsset);
    }
}
