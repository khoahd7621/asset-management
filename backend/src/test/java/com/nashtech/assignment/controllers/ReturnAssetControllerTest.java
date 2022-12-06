package com.nashtech.assignment.controllers;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.when;

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
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import com.nashtech.assignment.AssignmentApplication;
import com.nashtech.assignment.config.CORSConfig;
import com.nashtech.assignment.config.SecurityConfig;
import com.nashtech.assignment.data.constants.EReturnStatus;
import com.nashtech.assignment.dto.response.return_asset.ReturnAssetResponse;
import com.nashtech.assignment.exceptions.BadRequestException;
import com.nashtech.assignment.exceptions.ForbiddenException;
import com.nashtech.assignment.exceptions.NotFoundException;
import com.nashtech.assignment.services.auth.SecurityContextService;
import com.nashtech.assignment.services.create.CreateReturnAssetService;
import com.nashtech.assignment.utils.JwtTokenUtil;

@WebMvcTest(value = ReturnAssetController.class)
@ContextConfiguration(classes = { AssignmentApplication.class, SecurityConfig.class, CORSConfig.class })
@AutoConfigureMockMvc(addFilters = false)
public class ReturnAssetControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private JwtTokenUtil jwtTokenUtil;
    @MockBean
    private SecurityContextService securityContextService;
    @MockBean
    private CreateReturnAssetService createReturnAssetService;
    private NotFoundException notFoundException;
    private BadRequestException badRequestException;
    private ForbiddenException forbiddenException;

    @BeforeEach
    void setUp() throws Exception {
        notFoundException = new NotFoundException("error message");
        badRequestException = new BadRequestException("error message");
        forbiddenException = new ForbiddenException("error message");
    }

    @Test
    void createReturnAsset_WhenReturnNotFoundException() throws Exception {
        when(createReturnAssetService.createReturnAsset(1L)).thenThrow(notFoundException);

        RequestBuilder requestBuilder = MockMvcRequestBuilders.post("/api/return-asset").param("id",
                String.valueOf(1L));
        MockHttpServletResponse actual = mockMvc.perform(requestBuilder).andReturn().getResponse();

        assertThat(actual.getStatus(), is(HttpStatus.NOT_FOUND.value()));
        assertThat(actual.getContentAsString(), is("{\"message\":\"error message\"}"));
    }

    @Test
    void createReturnAsset_WhenReturnBadRequestException() throws Exception {
        when(createReturnAssetService.createReturnAsset(1L)).thenThrow(badRequestException);

        RequestBuilder requestBuilder = MockMvcRequestBuilders.post("/api/return-asset").param("id",
                String.valueOf(1L));
        MockHttpServletResponse actual = mockMvc.perform(requestBuilder).andReturn().getResponse();

        assertThat(actual.getStatus(), is(HttpStatus.BAD_REQUEST.value()));
        assertThat(actual.getContentAsString(), is("{\"message\":\"error message\"}"));
    }

    @Test
    void createReturnAsset_WhenReturnForbiddenException() throws Exception {
        when(createReturnAssetService.createReturnAsset(1L)).thenThrow(forbiddenException);

        RequestBuilder requestBuilder = MockMvcRequestBuilders.post("/api/return-asset").param("id",
                String.valueOf(1L));
        MockHttpServletResponse actual = mockMvc.perform(requestBuilder).andReturn().getResponse();

        assertThat(actual.getStatus(), is(HttpStatus.FORBIDDEN.value()));
        assertThat(actual.getContentAsString(), is("{\"message\":\"error message\"}"));
    }

    @Test
    void createReturnAsset_WhenReturnData() throws Exception {
        ReturnAssetResponse expected = ReturnAssetResponse.builder()
                .status(EReturnStatus.WAITING_FOR_RETURNING)
                .isDeleted(false)
                .build();
        when(createReturnAssetService.createReturnAsset(1L)).thenReturn(expected);

        RequestBuilder requestBuilder = MockMvcRequestBuilders.post("/api/return-asset").param("id",
                String.valueOf(1L));
        MockHttpServletResponse actual = mockMvc.perform(requestBuilder).andReturn().getResponse();

        assertThat(actual.getStatus(), is(HttpStatus.OK.value()));
        assertThat(actual.getContentAsString(), is(
                "{\"id\":0,\"returnedDate\":null,\"status\":\"WAITING_FOR_RETURNING\",\"assetCode\":null,\"assetName\":null,\"acceptByUser\":null,\"requestedByUser\":null,\"deleted\":false}"));

    }
}
