package com.nashtech.assignment.services.returned.impl;

import com.nashtech.assignment.data.constants.EAssetStatus;
import com.nashtech.assignment.data.constants.EAssignStatus;
import com.nashtech.assignment.data.constants.EReturnStatus;
import com.nashtech.assignment.data.constants.EUserType;
import com.nashtech.assignment.data.entities.Asset;
import com.nashtech.assignment.data.entities.AssignAsset;
import com.nashtech.assignment.data.entities.ReturnAsset;
import com.nashtech.assignment.data.entities.User;
import com.nashtech.assignment.data.repositories.AssetRepository;
import com.nashtech.assignment.data.repositories.AssignAssetRepository;
import com.nashtech.assignment.data.repositories.ReturnAssetRepository;
import com.nashtech.assignment.dto.response.returned.ReturnAssetResponse;
import com.nashtech.assignment.exceptions.BadRequestException;
import com.nashtech.assignment.exceptions.ForbiddenException;
import com.nashtech.assignment.exceptions.NotFoundException;
import com.nashtech.assignment.mappers.ReturnAssetMapper;
import com.nashtech.assignment.services.auth.SecurityContextService;
import com.nashtech.assignment.services.returned.ReturnedService;
import lombok.Builder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.Optional;

@Service
@Builder
public class ReturnedServiceImpl implements ReturnedService {
    @Autowired
    private ReturnAssetRepository returnAssetRepository;
    @Autowired
    private AssignAssetRepository assignAssetRepository;
    @Autowired
    private ReturnAssetMapper returnAssetMapper;
    @Autowired
    private SecurityContextService securityContextService;
    @Autowired
    private AssetRepository assetRepository;

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

        if (EUserType.ADMIN.equals(currentUser.getType())) {
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

    @Override
    public void deleteReturnAsset(long id) {
        Optional<ReturnAsset> returnAssetOtp = returnAssetRepository.findById(id);

        if (returnAssetOtp.isEmpty()) {
            throw new NotFoundException("Cannot find return asset with id " + id);
        }

        ReturnAsset returnAsset = returnAssetOtp.get();

        if (!EReturnStatus.WAITING_FOR_RETURNING.equals(returnAsset.getStatus())) {
            throw new BadRequestException("Cannot cancel this return asset");
        }

        returnAsset.setAsset(null);
        returnAsset.setAssignAsset(null);
        returnAsset.setUserAcceptedReturn(null);
        returnAsset.setUserRequestedReturn(null);
        returnAssetRepository.save(returnAsset);
        returnAssetRepository.delete(returnAsset);
    }

    @Override
    public void completeReturnRequest(long id) {
        LocalDate localDate = LocalDate.now();
        Date today = Date.from(localDate.atStartOfDay(ZoneId.systemDefault()).toInstant());
        User currentUser = securityContextService.getCurrentUser();
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
