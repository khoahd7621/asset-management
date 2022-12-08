package com.nashtech.assignment.dto.response.report;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Getter
@Setter
public class AssetReportResponse {
    private String name;
    private Integer count;
    private Integer assigned;
    private Integer available;
    private Integer notAvailable;
    private Integer waitingForRecycling;
    private Integer recycling;
}
