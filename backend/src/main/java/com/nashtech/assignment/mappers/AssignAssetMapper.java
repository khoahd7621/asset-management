package com.nashtech.assignment.mappers;

import com.nashtech.assignment.data.entities.Asset;
import com.nashtech.assignment.data.entities.AssignAsset;
import com.nashtech.assignment.data.entities.User;
import com.nashtech.assignment.dto.response.assignment.AssignAssetResponse;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class AssignAssetMapper {
    public AssignAssetResponse toAssignAssetResponse(AssignAsset assignAsset) {
        Asset asset = assignAsset.getAsset();
        User userAssignedTo = assignAsset.getUserAssignedTo();
        return AssignAssetResponse.builder()
                .id(assignAsset.getId())
                .assetId(asset.getId())
                .assetCode(asset.getAssetCode())
                .assetName(asset.getName())
                .userAssignedToId(userAssignedTo.getId())
                .userAssignedTo(userAssignedTo.getUsername())
                .userAssignedToFullName(userAssignedTo.getFirstName() + " " + userAssignedTo.getLastName())
                .userAssignedBy(assignAsset.getUserAssignedBy().getUsername())
                .assignedDate(assignAsset.getAssignedDate())
                .category(asset.getCategory().getName())
                .note(assignAsset.getNote())
                .specification(asset.getSpecification())
                .returnAsset(assignAsset.getReturnAsset())
                .status(assignAsset.getStatus()).build();
    }

    public List<AssignAssetResponse> toListAssignAssetResponses(List<AssignAsset> assignAssets) {
        return assignAssets.stream().map(this::toAssignAssetResponse).collect(Collectors.toList());
    }
}
