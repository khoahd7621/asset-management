package com.nashtech.assignment.dto.response.asset;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
@Builder
public class AssetHistory {
    private Date assignedDate;
    private String assignedTo;
    private String assignedBy;
    private Date returnedDate;
}
