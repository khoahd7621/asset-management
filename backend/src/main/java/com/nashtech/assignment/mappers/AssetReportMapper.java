package com.nashtech.assignment.mappers;

import com.nashtech.assignment.dto.response.report.AssetReportResponse;
import com.nashtech.assignment.dto.response.report.AssetReportResponseInterface;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class AssetReportMapper {
    public AssetReportResponse toAssetReportResponse(AssetReportResponseInterface assetReportResponseInterface) {
        return AssetReportResponse.builder()
                .count(assetReportResponseInterface.getCount())
                .name(assetReportResponseInterface.getName())
                .assigned(assetReportResponseInterface.getAssigned())
                .available(assetReportResponseInterface.getAvailable())
                .notAvailable(assetReportResponseInterface.getNotAvailable())
                .recycling(assetReportResponseInterface.getRecycling())
                .waitingForRecycling(assetReportResponseInterface.getWaitingForRecycling())
                .build();
    }

    public List<AssetReportResponse> toListAssetReportResponses(List<AssetReportResponseInterface> assetReportResponseInterfaces) {
        return assetReportResponseInterfaces.stream()
                .map(this::toAssetReportResponse)
                .collect(Collectors.toList());
    }
}
