package com.nashtech.assignment.dto.response.report;

public interface AssetReportResponse {
    Integer getCount();
    String getName();
    Integer getAssigned();
    Integer getAvailable();
    Integer getNotAvailable();
    Integer getWaitingForRecycling();
    Integer getRecycling();
}
