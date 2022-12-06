package com.nashtech.assignment.services.get.impl;

import java.util.Collections;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.nashtech.assignment.data.entities.Category;
import com.nashtech.assignment.data.repositories.AssetRepository;
import com.nashtech.assignment.data.repositories.CategoryRepository;
import com.nashtech.assignment.dto.response.report.AssetReportResponse;
import com.nashtech.assignment.services.get.GetAssetReportService;

import lombok.Builder;

@Service
@Builder
public class GetAssetReportServiceImpl implements GetAssetReportService {

    @Autowired AssetRepository assetRepository;

    @Override
    public List<AssetReportResponse> getAssetReport() {
        return assetRepository.getAssetReport();
    }
    
}
