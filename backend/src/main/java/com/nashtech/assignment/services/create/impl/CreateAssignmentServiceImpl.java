package com.nashtech.assignment.services.create.impl;

import com.nashtech.assignment.data.constants.EAssetStatus;
import com.nashtech.assignment.data.constants.EAssignStatus;
import com.nashtech.assignment.data.entities.Asset;
import com.nashtech.assignment.data.entities.AssignAsset;
import com.nashtech.assignment.data.entities.User;
import com.nashtech.assignment.data.repositories.AssetRepository;
import com.nashtech.assignment.data.repositories.AssignAssetRepository;
import com.nashtech.assignment.data.repositories.UserRepository;
import com.nashtech.assignment.dto.request.assignment.CreateNewAssignmentRequest;
import com.nashtech.assignment.dto.response.assignment.AssignAssetResponse;
import com.nashtech.assignment.exceptions.BadRequestException;
import com.nashtech.assignment.exceptions.NotFoundException;
import com.nashtech.assignment.mappers.AssignAssetMapper;
import com.nashtech.assignment.services.SecurityContextService;
import com.nashtech.assignment.services.create.CreateAssignmentService;
import lombok.Builder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Optional;

@Service
@Builder
public class CreateAssignmentServiceImpl implements CreateAssignmentService {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private AssetRepository assetRepository;
    @Autowired
    private AssignAssetRepository assignAssetRepository;
    @Autowired
    private AssignAssetMapper assignAssetMapper;
    @Autowired
    private SecurityContextService securityContextService;

    @Override
    public AssignAssetResponse createNewAssignment(CreateNewAssignmentRequest createNewAssignmentRequest) {
        LocalDate today = LocalDate.now();
        LocalDate assignedDate = LocalDate.ofInstant(
                createNewAssignmentRequest.getAssignedDate().toInstant(), ZoneId.systemDefault());
        if (today.isAfter(assignedDate)) {
            throw new BadRequestException("Assign date is before today.");
        }
        Optional<Asset> assetOpt = assetRepository.findByIdAndIsDeletedFalse(createNewAssignmentRequest.getAssetId());
        if (assetOpt.isEmpty()) {
            throw new NotFoundException("Not exist asset with this assetId.");
        }
        if (assetOpt.get().getStatus() != EAssetStatus.AVAILABLE) {
            throw new BadRequestException("Status of this asset is not available.");
        }
        Optional<User> userOpt = userRepository.findByIdAndIsDeletedFalse(createNewAssignmentRequest.getUserId());
        if (userOpt.isEmpty()) {
            throw new NotFoundException("Not exist user with this userId.");
        }
        LocalDate joinedDate = LocalDate.ofInstant(
                userOpt.get().getJoinedDate().toInstant(), ZoneId.systemDefault());
        if (assignedDate.isBefore(joinedDate)) {
            throw new BadRequestException("Assigned date cannot before joined date of user.");
        }
        User currentUser = securityContextService.getCurrentUser();
        Asset asset = assetOpt.get();
        asset.setStatus(EAssetStatus.ASSIGNED);
        AssignAsset assignAsset = AssignAsset.builder()
                .asset(asset)
                .assignedDate(createNewAssignmentRequest.getAssignedDate())
                .userAssignedBy(currentUser)
                .userAssignedTo(userOpt.get())
                .status(EAssignStatus.WAITING_FOR_ACCEPTANCE)
                .note(createNewAssignmentRequest.getNote())
                .isDeleted(false).build();
        assignAsset = assignAssetRepository.save(assignAsset);
        return assignAssetMapper.toAssignAssetResponse(assignAsset);
    }
}
