package com.nashtech.assignment.controllers;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;

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

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nashtech.assignment.AssignmentApplication;
import com.nashtech.assignment.config.CORSConfig;
import com.nashtech.assignment.config.SecurityConfig;
import com.nashtech.assignment.dto.response.report.AssetReportResponse;
import com.nashtech.assignment.services.auth.SecurityContextService;
import com.nashtech.assignment.services.get.GetAssetReportService;
import com.nashtech.assignment.utils.JwtTokenUtil;

@WebMvcTest(value = ReportController.class)
@ContextConfiguration(classes = { AssignmentApplication.class, SecurityConfig.class, CORSConfig.class })
@AutoConfigureMockMvc(addFilters = false)
public class ReportControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @MockBean
    private GetAssetReportService getAssetReportService;
    @MockBean
    private JwtTokenUtil jwtTokenUtil;
    @MockBean
    private SecurityContextService securityContextService;

    private AssetReportResponse assetReportResponse;

    @BeforeEach
    void setUpTest() {
        assetReportResponse = new AssetReportResponse() {

            @Override
            public Integer getCount() {
                return 1;
            }

            @Override
            public String getName() {
                return "name";
            }

            @Override
            public Integer getAssigned() {
                return 1;
            }

            @Override
            public Integer getAvailable() {
                return 1;
            }

            @Override
            public Integer getNotAvailable() {
                return 1;
            }

            @Override
            public Integer getWaitingForRecycling() {
                return 1;
            }

            @Override
            public Integer getRecycling() {
                return 1;
            }

        };
    }

    @Test
    void getReport_WhenDataValid_ShouldReturnListOfAssetReportResponse() throws Exception {

        List<AssetReportResponse> expected = new ArrayList<>();
        expected.add(assetReportResponse);

        when(getAssetReportService.getAssetReport()).thenReturn(expected);
        RequestBuilder request = MockMvcRequestBuilders
                .get("/api/report");

        MvcResult result = mockMvc.perform(request).andReturn();

        assertThat(result.getResponse().getContentAsString(), is(objectMapper.writeValueAsString(expected)));
    }
}
