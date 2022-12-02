package com.nashtech.assignment.dto.response.assignment;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.nashtech.assignment.data.constants.EAssignStatus;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

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
    private String userAssignedBy;
    private Date assignedDate;
    private String category;
    private String note;
    private String specification;
    private EAssignStatus status;
}
