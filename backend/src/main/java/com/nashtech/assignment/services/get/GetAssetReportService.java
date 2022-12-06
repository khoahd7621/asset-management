package com.nashtech.assignment.services.get;

import java.util.List;

import com.nashtech.assignment.dto.response.report.AssetReportResponse;

public interface GetAssetReportService {
    List<AssetReportResponse> getAssetReport();
}
