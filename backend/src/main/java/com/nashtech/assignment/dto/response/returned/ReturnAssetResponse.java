package com.nashtech.assignment.dto.response.returned;

import com.nashtech.assignment.data.constants.EReturnStatus;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Builder
@Getter
@Setter
public class ReturnAssetResponse {
    private long id;
    private Date returnedDate;
    private Date assignedDate;
    private EReturnStatus status;
    private boolean isDeleted;
    private String assetCode;
    private String assetName;
    private String acceptByUser;
    private String requestedByUser;
}
