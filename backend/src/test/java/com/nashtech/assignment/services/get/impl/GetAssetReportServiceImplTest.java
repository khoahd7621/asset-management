package com.nashtech.assignment.services.get.impl;

import com.nashtech.assignment.data.repositories.AssetRepository;
import com.nashtech.assignment.dto.response.report.AssetReportResponse;
import com.nashtech.assignment.dto.response.report.AssetReportResponseInterface;
import com.nashtech.assignment.mappers.AssetReportMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class GetAssetReportServiceImplTest {

    private GetAssetReportServiceImpl getAssetReportServiceImpl;
    private AssetRepository assetRepository;
    private AssetReportMapper assetReportMapper;

    @BeforeEach
    void setUpTest() {
        assetRepository = mock(AssetRepository.class);
        assetReportMapper = mock(AssetReportMapper.class);
        getAssetReportServiceImpl = GetAssetReportServiceImpl
                .builder()
                .assetRepository(assetRepository)
                .assetReportMapper(assetReportMapper)
                .build();
    }

    @Test
    void getAssetReport_WhenDataValid_ShouldReturnListOfAssetResponse() {
        List<AssetReportResponseInterface> assetReportResponseInterfaces = mock(List.class);
        List<AssetReportResponse> expected = mock(List.class);

        when(assetRepository.getAssetReport()).thenReturn(assetReportResponseInterfaces);
        when(assetReportMapper.toListAssetReportResponses(assetReportResponseInterfaces)).thenReturn(expected);

        List<AssetReportResponse> actual = getAssetReportServiceImpl.getAssetReport();
        assertThat(actual, is(expected));
    }
}
