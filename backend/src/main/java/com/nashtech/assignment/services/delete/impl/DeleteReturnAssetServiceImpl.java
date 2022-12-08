package com.nashtech.assignment.services.delete.impl;

import com.nashtech.assignment.data.constants.EReturnStatus;
import com.nashtech.assignment.data.entities.ReturnAsset;
import com.nashtech.assignment.data.repositories.ReturnAssetRepository;
import com.nashtech.assignment.exceptions.BadRequestException;
import com.nashtech.assignment.exceptions.NotFoundException;
import com.nashtech.assignment.services.delete.DeleteReturnAssetService;
import lombok.Builder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@Builder
public class DeleteReturnAssetServiceImpl implements DeleteReturnAssetService {

    @Autowired
    private ReturnAssetRepository returnAssetRepository;

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

}
