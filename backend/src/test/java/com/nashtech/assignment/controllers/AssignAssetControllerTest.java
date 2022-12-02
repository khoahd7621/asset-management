package com.nashtech.assignment.controllers;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nashtech.assignment.AssignmentApplication;
import com.nashtech.assignment.config.CORSConfig;
import com.nashtech.assignment.config.SecurityConfig;
import com.nashtech.assignment.data.constants.EAssignStatus;
import com.nashtech.assignment.dto.response.PaginationResponse;
import com.nashtech.assignment.dto.response.assignment.AssignAssetResponse;
import com.nashtech.assignment.exceptions.NotFoundException;
import com.nashtech.assignment.services.FilterService;
import com.nashtech.assignment.services.GetService;
import com.nashtech.assignment.services.SecurityContextService;
import com.nashtech.assignment.services.search.SearchAssignAssetService;
import com.nashtech.assignment.utils.JwtTokenUtil;

@WebMvcTest(value = AssignAssetController.class)
@ContextConfiguration(classes = { AssignmentApplication.class, SecurityConfig.class,
                CORSConfig.class })
@AutoConfigureMockMvc(addFilters = false)
public class AssignAssetControllerTest {
        @Autowired
        private ObjectMapper objectMapper;
        @Autowired
        private MockMvc mockMvc;
        @MockBean
        private SearchAssignAssetService searchAssignAssetService;
        @MockBean
        private GetService getService;
        @MockBean
        private JwtTokenUtil jwtTokenUtil;
        @MockBean
        private SecurityContextService securityContextService;

        private AssignAssetResponse assetResponse;

        @BeforeEach()
        void setUp() {

                assetResponse = AssignAssetResponse.builder().assetCode("LP0101")
                                .assetName("Laptop Lenovo 1009").category("Laptop")
                                .note("note").build();

        }

        @Test
        void searchAssignAsset_WhenValid_ShouldReturnPaginationRespponseOfAssignAsset()
                        throws Exception {
                List<EAssignStatus> status = new ArrayList<>();
                status = new ArrayList<>();
                status.add(EAssignStatus.ACCEPTED);
                List<AssignAssetResponse> assignAssetPage = new ArrayList<>();
                assignAssetPage.add(assetResponse);

                PaginationResponse<List<AssignAssetResponse>> paginationResponse = PaginationResponse
                                .<List<AssignAssetResponse>>builder()
                                .data(assignAssetPage).build();

                when(searchAssignAssetService.filterAndSearchAssignAsset("test", status,
                                "11/30/2022", 0)).thenReturn(paginationResponse);

                RequestBuilder requestBuilder = MockMvcRequestBuilders
                                .get("/api/assignment").param("name", "test")
                                .param("status", "ACCEPTED")
                                .param("date", "11/30/2022").param("page", "0");
                MvcResult result = mockMvc.perform(requestBuilder).andReturn();

                assertThat(result.getResponse().getStatus(),
                                is(HttpStatus.OK.value()));
                assertThat(result.getResponse().getContentAsString(), is(
                                "{\"data\":[{\"id\":0,\"assetId\":0,\"assetCode\":\"LP0101\",\"assetName\":\"Laptop Lenovo 1009\",\"userAssignedToId\":0,\"userAssignedTo\":null,\"userAssignedBy\":null,\"assignedDate\":null,\"category\":\"Laptop\",\"note\":\"note\",\"specification\":null,\"status\":null}],\"totalPage\":0,\"totalRow\":0}"));

        }

        @Test
        void searchAssignAsset_WhenDataEmpty_ShouldReturnPaginationResponseWithDataEmpty()
                        throws Exception {
                List<EAssignStatus> status = new ArrayList<>();
                status = new ArrayList<>();
                status.add(EAssignStatus.ACCEPTED);
                List<AssignAssetResponse> assignAssetPage = new ArrayList<>();
                assignAssetPage.add(assetResponse);

                PaginationResponse<List<AssignAssetResponse>> paginationResponse = PaginationResponse
                                .<List<AssignAssetResponse>>builder()
                                .data(Collections.emptyList()).build();

                when(searchAssignAssetService.filterAndSearchAssignAsset("test", status,
                                "11/30/2022", 0)).thenReturn(paginationResponse);

                RequestBuilder requestBuilder = MockMvcRequestBuilders
                                .get("/api/assignment").param("name", "test")
                                .param("status", "ACCEPTED")
                                .param("date", "11/30/2022").param("page", "0");
                MvcResult result = mockMvc.perform(requestBuilder).andReturn();

                assertThat(result.getResponse().getStatus(),
                                is(HttpStatus.OK.value()));
                assertThat(result.getResponse().getContentAsString(),
                                is("{\"data\":[],\"totalPage\":0,\"totalRow\":0}"));

        }

        @Test
        void getAssignmentDetails_WhenDataValid_ShouldReturnAssignAssetRespone() throws Exception {
                AssignAssetResponse expected = AssignAssetResponse.builder()
                                .assetCode("test").assetId(1l).build();
                when(getService.getAssignAssetDetails(1l)).thenReturn(expected);

                RequestBuilder requestBuilder = MockMvcRequestBuilders
                                .get("/api/assignment/details").param("id", "1");
                MvcResult result = mockMvc.perform(requestBuilder).andReturn();

                assertThat(result.getResponse().getStatus(), is(HttpStatus.OK.value()));
                assertThat(result.getResponse().getContentAsString(), is("{\"id\":0,\"assetId\":1,\"assetCode\":\"test\",\"assetName\":null,\"userAssignedToId\":0,\"userAssignedTo\":null,\"userAssignedBy\":null,\"assignedDate\":null,\"category\":null,\"note\":null,\"specification\":null,\"status\":null}"));

        }
        @Test
        void getAssignmentDetails_WhenAssignNotFound_ShouldThrowNotFoundException() throws Exception {
                NotFoundException exception = new NotFoundException("Mess");
                when(getService.getAssignAssetDetails(1l)).thenThrow(exception);

                RequestBuilder requestBuilder = MockMvcRequestBuilders
                                .get("/api/assignment/details").param("id", "1");
                MvcResult result = mockMvc.perform(requestBuilder).andReturn();

                assertThat(result.getResponse().getStatus(), is(HttpStatus.NOT_FOUND.value()));
                assertThat(result.getResponse().getContentAsString(), is("{\"message\":\"Mess\"}"));

        }

}
