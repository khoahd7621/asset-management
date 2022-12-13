package com.nashtech.assignment.controllers;

import com.nashtech.assignment.AssignmentApplication;
import com.nashtech.assignment.config.CORSConfig;
import com.nashtech.assignment.config.SecurityConfig;
import com.nashtech.assignment.data.constants.EReturnStatus;
import com.nashtech.assignment.dto.response.PaginationResponse;
import com.nashtech.assignment.dto.response.returned.ReturnAssetResponse;
import com.nashtech.assignment.exceptions.BadRequestException;
import com.nashtech.assignment.exceptions.ForbiddenException;
import com.nashtech.assignment.exceptions.NotFoundException;
import com.nashtech.assignment.services.auth.SecurityContextService;
import com.nashtech.assignment.services.returned.ReturnedService;
import com.nashtech.assignment.services.search.SearchReturnAssetService;
import com.nashtech.assignment.utils.JwtTokenUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.*;

@WebMvcTest(value = ReturnAssetController.class)
@ContextConfiguration(classes = {AssignmentApplication.class, SecurityConfig.class, CORSConfig.class})
@AutoConfigureMockMvc(addFilters = false)
class ReturnAssetControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private SearchReturnAssetService searchReturnAssetService;
    @MockBean
    private ReturnedService returnedService;
    @MockBean
    private JwtTokenUtil jwtTokenUtil;
    @MockBean
    private SecurityContextService securityContextService;

    private NotFoundException notFoundException;
    private BadRequestException badRequestException;
    private ForbiddenException forbiddenException;
    private ReturnAssetResponse returnAssetResponse;

    @BeforeEach
    void setUp() {
        notFoundException = new NotFoundException("error message");
        badRequestException = new BadRequestException("error message");
        forbiddenException = new ForbiddenException("error message");
        returnAssetResponse = ReturnAssetResponse.builder()
                .assetCode("LP0101")
                .assetName("Laptop Lenovo 1009").build();
    }

    @Test
    void createReturnAsset_WhenAssignNotFound_ShouldReturnNotFoundException() throws Exception {
        when(returnedService.createReturnAsset(1L)).thenThrow(notFoundException);

        RequestBuilder requestBuilder = MockMvcRequestBuilders.post("/api/return-asset")
                .param("id", String.valueOf(1L));
        MockHttpServletResponse actual = mockMvc.perform(requestBuilder).andReturn().getResponse();

        assertThat(actual.getStatus(), is(HttpStatus.NOT_FOUND.value()));
        assertThat(actual.getContentAsString(), is("{\"message\":\"error message\"}"));
    }

    @Test
    void createReturnAsset_WhenAssignInvalid_ShouldReturnBadRequestException() throws Exception {
        when(returnedService.createReturnAsset(1L)).thenThrow(badRequestException);

        RequestBuilder requestBuilder = MockMvcRequestBuilders.post("/api/return-asset")
                .param("id", String.valueOf(1L));
        MockHttpServletResponse actual = mockMvc.perform(requestBuilder).andReturn().getResponse();

        assertThat(actual.getStatus(), is(HttpStatus.BAD_REQUEST.value()));
        assertThat(actual.getContentAsString(), is("{\"message\":\"error message\"}"));
    }

    @Test
    void createReturnAsset_WhenUserNotMatch_ShouldReturnForbiddenException() throws Exception {
        when(returnedService.createReturnAsset(1L)).thenThrow(forbiddenException);

        RequestBuilder requestBuilder = MockMvcRequestBuilders.post("/api/return-asset")
                .param("id", String.valueOf(1L));
        MockHttpServletResponse actual = mockMvc.perform(requestBuilder).andReturn().getResponse();

        assertThat(actual.getStatus(), is(HttpStatus.FORBIDDEN.value()));
        assertThat(actual.getContentAsString(), is("{\"message\":\"error message\"}"));
    }

    @Test
    void createReturnAsset_WhenDataValid_ShouldReturnData() throws Exception {
        ReturnAssetResponse expected = ReturnAssetResponse.builder()
                .status(EReturnStatus.WAITING_FOR_RETURNING)
                .isDeleted(false)
                .build();
        when(returnedService.createReturnAsset(1L)).thenReturn(expected);

        RequestBuilder requestBuilder = MockMvcRequestBuilders.post("/api/return-asset")
                .param("id", String.valueOf(1L));
        MockHttpServletResponse actual = mockMvc.perform(requestBuilder).andReturn().getResponse();

        assertThat(actual.getStatus(), is(HttpStatus.OK.value()));
        assertThat(actual.getContentAsString(), is(
                "{\"id\":0,\"returnedDate\":null,\"assignedDate\":null,\"status\":\"WAITING_FOR_RETURNING\"," +
                        "\"assetCode\":null,\"assetName\":null,\"acceptByUser\":null,\"requestedByUser\":null,\"deleted\":false}"));
    }

    @Test
    void searchReturnAsset_WhenValid_ShouldReturnPaginationResponseOfAssignAsset() throws Exception {
        List<EReturnStatus> status = new ArrayList<>();
        status.add(EReturnStatus.COMPLETED);
        List<ReturnAssetResponse> returnAssetPage = new ArrayList<>();
        returnAssetPage.add(returnAssetResponse);

        PaginationResponse<List<ReturnAssetResponse>> paginationResponse = PaginationResponse
                .<List<ReturnAssetResponse>>builder()
                .data(returnAssetPage).build();

        when(searchReturnAssetService.searchAllReturnAssetsByStateAndReturnedDateAndSearchWithPagination(
                "test", status, "11/30/2022", 0)).thenReturn(paginationResponse);

        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .get("/api/return-asset").param("query", "test")
                .param("statuses", "COMPLETED")
                .param("date", "11/30/2022").param("page", "0");
        MvcResult result = mockMvc.perform(requestBuilder).andReturn();

        assertThat(result.getResponse().getStatus(), is(HttpStatus.OK.value()));
        assertThat(result.getResponse().getContentAsString(), is(
                "{\"data\":[{\"id\":0,\"returnedDate\":null,\"assignedDate\":null,\"status\":null," +
                        "\"assetCode\":\"LP0101\",\"assetName\":\"Laptop Lenovo 1009\",\"acceptByUser\":null," +
                        "\"requestedByUser\":null,\"deleted\":false}],\"totalPage\":0,\"totalRow\":0}"));
    }

    @Test
    void searchReturnAsset_WhenDataEmpty_ShouldReturnPaginationResponseWithDataEmpty()
            throws Exception {
        List<EReturnStatus> status = new ArrayList<>();
        status.add(EReturnStatus.COMPLETED);

        PaginationResponse<List<ReturnAssetResponse>> paginationResponse = PaginationResponse
                .<List<ReturnAssetResponse>>builder()
                .data(Collections.emptyList()).build();

        when(searchReturnAssetService.searchAllReturnAssetsByStateAndReturnedDateAndSearchWithPagination("test",
                status,
                "11/30/2022", 0)).thenReturn(paginationResponse);

        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .get("/api/return-asset").param("query", "test")
                .param("statuses", "COMPLETED")
                .param("date", "11/30/2022").param("page", "0");
        MvcResult result = mockMvc.perform(requestBuilder).andReturn();

        assertThat(result.getResponse().getStatus(), is(HttpStatus.OK.value()));
        assertThat(result.getResponse().getContentAsString(), is("{\"data\":[],\"totalPage\":0,\"totalRow\":0}"));
    }

    @Test
    void cancelReturnAsset_WhenDataValid_ShouldReturn204() throws Exception {
        doNothing().when(returnedService).deleteReturnAsset(1L);

        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .delete("/api/return-asset")
                .param("id", "1");
        MvcResult result = mockMvc.perform(requestBuilder).andReturn();

        assertThat(result.getResponse().getStatus(), is(HttpStatus.NO_CONTENT.value()));
    }

    @Test
    void cancelReturnAsset_WhenReturnAssetNotExist_ShouldReturn404() throws Exception {
        doThrow(NotFoundException.class).when(returnedService).deleteReturnAsset(1L);

        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .delete("/api/return-asset")
                .param("id", "1");
        MvcResult result = mockMvc.perform(requestBuilder).andReturn();

        assertThat(result.getResponse().getStatus(), is(HttpStatus.NOT_FOUND.value()));
    }

    @Test
    void completeReturnRequest_WhenDataValid_ShouldReturn204() throws Exception {
        doNothing().when(returnedService).completeReturnRequest(1L);

        RequestBuilder requestBuilder = MockMvcRequestBuilders.patch("/api/return-asset")
                .param("id", "1");
        MvcResult result = mockMvc.perform(requestBuilder).andReturn();

        assertThat(result.getResponse().getStatus(), is(HttpStatus.NO_CONTENT.value()));
    }

    @Test
    void completeReturnRequest_WhenIdNotExist_ShouldThrow404() throws Exception {
        doThrow(NotFoundException.class).when(returnedService).completeReturnRequest(1L);

        RequestBuilder requestBuilder = MockMvcRequestBuilders.patch("/api/return-asset")
                .param("id", "1");
        MvcResult result = mockMvc.perform(requestBuilder).andReturn();

        assertThat(result.getResponse().getStatus(), is(HttpStatus.NOT_FOUND.value()));
    }

    @Test
    void completeReturnRequest_WhenReturnAssetNotValid_ShouldThrow400() throws Exception {
        doThrow(BadRequestException.class).when(returnedService).completeReturnRequest(1L);

        RequestBuilder requestBuilder = MockMvcRequestBuilders.patch("/api/return-asset")
                .param("id", "1");
        MvcResult result = mockMvc.perform(requestBuilder).andReturn();

        assertThat(result.getResponse().getStatus(), is(HttpStatus.BAD_REQUEST.value()));
    }

    @Test
    void cancelReturnAsset_WhenReturnAssetNotValid_ShouldReturn400() throws Exception {
        doThrow(BadRequestException.class).when(returnedService).deleteReturnAsset(1L);

        RequestBuilder requestBuilder = MockMvcRequestBuilders.delete("/api/return-asset")
                .param("id", "1");
        MvcResult result = mockMvc.perform(requestBuilder).andReturn();

        assertThat(result.getResponse().getStatus(), is(HttpStatus.BAD_REQUEST.value()));
    }
}
