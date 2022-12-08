package com.nashtech.assignment.mappers;

import com.nashtech.assignment.data.constants.EReturnStatus;
import com.nashtech.assignment.data.entities.ReturnAsset;
import com.nashtech.assignment.dto.response.returned.ReturnAssetResponse;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class ReturnAssetMapper {
    public ReturnAssetResponse toReturnAssetResponse(ReturnAsset returnAsset) {
        return ReturnAssetResponse.builder()
                .id(returnAsset.getId())
                .returnedDate(returnAsset.getStatus() == EReturnStatus.COMPLETED ? returnAsset.getReturnedDate() : null)
                .assignedDate(returnAsset.getAssignAsset().getAssignedDate())
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

    public List<ReturnAssetResponse> mapListEntityReturnAssetResponses(List<ReturnAsset> users) {
        return users.stream()
                .map(this::toReturnAssetResponse)
                .collect(Collectors.toList());
    }
}
