package com.nashtech.assignment.services.get.impl;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.nashtech.assignment.data.repositories.AssetRepository;
import com.nashtech.assignment.dto.response.report.AssetReportResponse;

public class GetAssetReportServiceImplTest {

    private GetAssetReportServiceImpl getAssetReportServiceImpl;

    private AssetRepository assetRepository;

    @BeforeEach
    void setUpTest() {
        assetRepository = mock(AssetRepository.class);
        getAssetReportServiceImpl = GetAssetReportServiceImpl
                .builder()
                .assetRepository(assetRepository)
                .build();
    }

    @Test
    void getAssetReport_WhenDataValid_ShouldReturnListOfAssetResponse() {
        List<AssetReportResponse> expected = mock(List.class);
        when(assetRepository.getAssetReport()).thenReturn(expected);
        List<AssetReportResponse> actual = getAssetReportServiceImpl.getAssetReport();
        assertThat(actual, is(expected));
    }
}
