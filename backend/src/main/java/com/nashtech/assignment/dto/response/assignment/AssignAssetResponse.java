package com.nashtech.assignment.dto.response.assignment;

import com.nashtech.assignment.data.constants.EAssignStatus;
import com.nashtech.assignment.data.entities.ReturnAsset;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
@Builder
public class AssignAssetResponse {
    private long id;
    private long assetId;
    private String assetCode;
    private String assetName;
    private long userAssignedToId;
    private String userAssignedTo;
    private String userAssignedToFullName;
    private String userAssignedBy;
    private Date assignedDate;
    private String category;
    private String note;
    private String specification;
    private EAssignStatus status;
    private ReturnAsset returnAsset;
}
