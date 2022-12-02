package com.nashtech.assignment.mappers;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.nashtech.assignment.data.entities.Asset;
import com.nashtech.assignment.data.entities.AssignAsset;
import com.nashtech.assignment.data.entities.User;
import com.nashtech.assignment.dto.response.assignment.AssignAssetResponse;

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
                .userAssignedBy(assignAsset.getUserAssignedBy().getUsername())
                .assignedDate(assignAsset.getAssignedDate())
                .category(asset.getCategory().getName())
                .note(assignAsset.getNote())
                .specification(asset.getSpecification())
                .status(assignAsset.getStatus()).build();
    }

    public List<AssignAssetResponse> mapListEntityToDto(
            List<AssignAsset> assignAssets) {
        return assignAssets.stream().map(assignAsset -> toAssignAssetResponse(assignAsset))
                .collect(Collectors.toList());
    }
}
