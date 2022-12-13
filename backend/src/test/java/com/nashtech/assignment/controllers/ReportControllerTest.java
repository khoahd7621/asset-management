package com.nashtech.assignment.controllers;

import com.nashtech.assignment.AssignmentApplication;
import com.nashtech.assignment.config.CORSConfig;
import com.nashtech.assignment.config.SecurityConfig;
import com.nashtech.assignment.dto.response.report.AssetReportResponse;
import com.nashtech.assignment.services.asset.AssetService;
import com.nashtech.assignment.services.auth.SecurityContextService;
import com.nashtech.assignment.utils.JwtTokenUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.when;

@WebMvcTest(value = ReportController.class)
@ContextConfiguration(classes = {AssignmentApplication.class, SecurityConfig.class, CORSConfig.class})
@AutoConfigureMockMvc(addFilters = false)
class ReportControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private AssetService assetService;
    @MockBean
    private JwtTokenUtil jwtTokenUtil;
    @MockBean
    private SecurityContextService securityContextService;

    private AssetReportResponse assetReportResponse;

    @BeforeEach
    void setUpTest() {
        assetReportResponse = AssetReportResponse.builder()
                .assigned(1)
                .available(1)
                .name("test")
                .waitingForRecycling(1)
                .recycling(1)
                .notAvailable(1)
                .count(12)
                .build();
    }

    @Test
    void getReport_WhenDataValid_ShouldReturnListOfAssetReportResponse() throws Exception {
        List<AssetReportResponse> expected = new ArrayList<>();
        expected.add(assetReportResponse);

        when(assetService.getAssetReport()).thenReturn(expected);

        RequestBuilder request = MockMvcRequestBuilders.get("/api/report");
        MvcResult result = mockMvc.perform(request).andReturn();

        assertThat(result.getResponse().getContentAsString(), is(
                "[{\"name\":\"test\",\"count\":12,\"assigned\":1,\"available\":1,\"notAvailable\":1," +
                        "\"waitingForRecycling\":1,\"recycling\":1}]"));
    }
}
