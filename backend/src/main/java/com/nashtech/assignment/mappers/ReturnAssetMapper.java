package com.nashtech.assignment.mappers;

import org.springframework.stereotype.Component;

import com.nashtech.assignment.data.constants.EReturnStatus;
import com.nashtech.assignment.data.entities.ReturnAsset;
import com.nashtech.assignment.dto.response.return_asset.ReturnAssetResponse;

@Component
public class ReturnAssetMapper {
    public ReturnAssetResponse toReturnAssetResponse(ReturnAsset returnAsset) {
        return ReturnAssetResponse.builder()
                .id(returnAsset.getId())
                .returnedDate(returnAsset.getStatus() == EReturnStatus.COMPLETED ? returnAsset.getReturnedDate() : null)
                .status(returnAsset.getStatus())
                .isDeleted(returnAsset.isDeleted())
                .assetCode(returnAsset.getAsset().getAssetCode())
                .assetName(returnAsset.getAsset().getName())
                .acceptByUser(returnAsset.getStatus() == EReturnStatus.COMPLETED
                        ? returnAsset.getUserAcceptedReturn().getUsername()
                        : null)
                .requestedByUser(returnAsset.getUserRequestedReturn().getUsername())
                .build();
    }
}
