package com.nashtech.assignment.services.get.impl;

import com.nashtech.assignment.data.repositories.AssetRepository;
import com.nashtech.assignment.dto.response.report.AssetReportResponse;
import com.nashtech.assignment.mappers.AssetReportMapper;
import com.nashtech.assignment.services.get.GetAssetReportService;
import lombok.Builder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Builder
public class GetAssetReportServiceImpl implements GetAssetReportService {

    @Autowired
    private AssetRepository assetRepository;
    @Autowired
    private AssetReportMapper assetReportMapper;

    @Override
    public List<AssetReportResponse> getAssetReport() {
        return assetReportMapper.toListAssetReportResponses(assetRepository.getAssetReport());
    }
}
