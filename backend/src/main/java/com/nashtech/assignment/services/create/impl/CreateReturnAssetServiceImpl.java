package com.nashtech.assignment.services.create.impl;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.nashtech.assignment.data.constants.EAssignStatus;
import com.nashtech.assignment.data.constants.EReturnStatus;
import com.nashtech.assignment.data.constants.EUserType;
import com.nashtech.assignment.data.entities.AssignAsset;
import com.nashtech.assignment.data.entities.ReturnAsset;
import com.nashtech.assignment.data.entities.User;
import com.nashtech.assignment.data.repositories.AssignAssetRepository;
import com.nashtech.assignment.data.repositories.ReturnAssetRepository;
import com.nashtech.assignment.dto.response.returned.ReturnAssetResponse;
import com.nashtech.assignment.exceptions.BadRequestException;
import com.nashtech.assignment.exceptions.ForbiddenException;
import com.nashtech.assignment.exceptions.NotFoundException;
import com.nashtech.assignment.mappers.ReturnAssetMapper;
import com.nashtech.assignment.services.auth.SecurityContextService;
import com.nashtech.assignment.services.create.CreateReturnAssetService;

import lombok.Builder;

@Service
@Builder
public class CreateReturnAssetServiceImpl implements CreateReturnAssetService {
    @Autowired
    private ReturnAssetRepository returnAssetRepository;
    @Autowired
    private AssignAssetRepository assignAssetRepository;
    @Autowired
    private ReturnAssetMapper returnAssetMapper;
    @Autowired
    private SecurityContextService securityContextService;

    @Override
    public ReturnAssetResponse createReturnAsset(Long id) {
        Optional<AssignAsset> assignAsset = assignAssetRepository.findById(id);

        if (assignAsset.isEmpty()) {
            throw new NotFoundException("Assignment not found");
        }

        User currentUser = securityContextService.getCurrentUser();
        User userRequestedReturn = assignAsset.get().getUserAssignedTo();

        if (currentUser.getId() != assignAsset.get().getUserAssignedTo().getId()
                && currentUser.getType().equals(EUserType.STAFF)) {
            throw new ForbiddenException("Current user is not match to this assignment or current user is not admin.");
        }

        if(EUserType.ADMIN.equals(currentUser.getType())){
            userRequestedReturn = currentUser;
        }

        Optional<ReturnAsset> assetReturn = returnAssetRepository.findByAssignAssetId(id);
        
        if (!assignAsset.get().getStatus().equals(EAssignStatus.ACCEPTED)
                || !assetReturn.isEmpty()) {
            throw new BadRequestException("Assignment is not accepted or Assignment already exist in return list");
        }

        ReturnAsset returnAsset = ReturnAsset.builder()
                .status(EReturnStatus.WAITING_FOR_RETURNING)
                .isDeleted(false)
                .asset(assignAsset.get().getAsset())
                .assignAsset(assignAsset.get())
                .userRequestedReturn(userRequestedReturn)
                .build();

        returnAsset = returnAssetRepository.save(returnAsset);

        return returnAssetMapper.toReturnAssetResponse(returnAsset);
    }
}
