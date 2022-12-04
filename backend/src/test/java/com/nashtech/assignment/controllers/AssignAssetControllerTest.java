package com.nashtech.assignment.controllers;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.when;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

import com.nashtech.assignment.dto.request.assignment.CreateNewAssignmentRequest;
import com.nashtech.assignment.dto.request.assignment.EditAssignmentRequest;
import com.nashtech.assignment.exceptions.BadRequestException;
import com.nashtech.assignment.services.create.CreateAssignmentService;
import com.nashtech.assignment.services.edit.EditAssignAssetService;
import com.nashtech.assignment.services.get.GetAssignAssetService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
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
import com.nashtech.assignment.services.auth.SecurityContextService;
import com.nashtech.assignment.services.search.SearchAssignAssetService;
import com.nashtech.assignment.utils.JwtTokenUtil;

@WebMvcTest(value = AssignAssetController.class)
@ContextConfiguration(classes = {AssignmentApplication.class, SecurityConfig.class,
        CORSConfig.class})
@AutoConfigureMockMvc(addFilters = false)
public class AssignAssetControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @MockBean
    private SearchAssignAssetService searchAssignAssetService;
    @MockBean
    private GetAssignAssetService getAssignAssetService;
    @MockBean
    private CreateAssignmentService createAssignmentService;
    @MockBean
    private EditAssignAssetService editAssignAssetService;
    @MockBean
    private JwtTokenUtil jwtTokenUtil;
    @MockBean
    private SecurityContextService securityContextService;

    private AssignAssetResponse assetResponse;
    private Date date;
    private BadRequestException badRequestException;
    private NotFoundException notFoundException;

    @BeforeEach()
    void setUp() throws ParseException {

        assetResponse = AssignAssetResponse.builder().assetCode("LP0101")
                .assetName("Laptop Lenovo 1009").category("Laptop")
                .note("note").build();

        SimpleDateFormat formatterDate = new SimpleDateFormat("dd/MM/yyyy");
        formatterDate.setTimeZone(TimeZone.getTimeZone("GMT"));
        date = formatterDate.parse("01/01/2001");
        badRequestException = new BadRequestException("Error message");
        notFoundException = new NotFoundException("Error message");
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
                "{\"data\":[{\"id\":0,\"assetId\":0,\"assetCode\":\"LP0101\",\"assetName\":\"Laptop Lenovo 1009\"," +
                        "\"userAssignedToId\":0,\"userAssignedTo\":null,\"userAssignedToFullName\":null," +
                        "\"userAssignedBy\":null,\"assignedDate\":null,\"category\":\"Laptop\",\"note\":\"note\"," +
                        "\"specification\":null,\"status\":null}],\"totalPage\":0,\"totalRow\":0}"));

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
        when(getAssignAssetService.getAssignAssetDetails(1l)).thenReturn(expected);

        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .get("/api/assignment/details").param("id", "1");
        MvcResult result = mockMvc.perform(requestBuilder).andReturn();

        assertThat(result.getResponse().getStatus(), is(HttpStatus.OK.value()));
        assertThat(result.getResponse().getContentAsString(), is(
                "{\"id\":0,\"assetId\":1,\"assetCode\":\"test\",\"assetName\":null,\"userAssignedToId\":0," +
                        "\"userAssignedTo\":null,\"userAssignedToFullName\":null,\"userAssignedBy\":null," +
                        "\"assignedDate\":null,\"category\":null,\"note\":null,\"specification\":null,\"status\":null}"
        ));

    }

    @Test
    void getAssignmentDetails_WhenAssignNotFound_ShouldThrowNotFoundException() throws Exception {
        NotFoundException exception = new NotFoundException("Mess");
        when(getAssignAssetService.getAssignAssetDetails(1l)).thenThrow(exception);

        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .get("/api/assignment/details").param("id", "1");
        MvcResult result = mockMvc.perform(requestBuilder).andReturn();

        assertThat(result.getResponse().getStatus(), is(HttpStatus.NOT_FOUND.value()));
        assertThat(result.getResponse().getContentAsString(), is("{\"message\":\"Mess\"}"));

    }

    @Test
    void createNewAssignment_WhenDataRequestValid_ShouldReturnData() throws Exception {
        CreateNewAssignmentRequest requestData = CreateNewAssignmentRequest.builder()
                .assetId(1L)
                .userId(1L)
                .assignedDate(date)
                .note("note").build();
        ArgumentCaptor<CreateNewAssignmentRequest> requestDataCaptor =
                ArgumentCaptor.forClass(CreateNewAssignmentRequest.class);
        AssignAssetResponse assetResponse = AssignAssetResponse.builder()
                .id(1)
                .assetCode("assetCode")
                .assetName("assetName")
                .userAssignedTo("userAssignedTo")
                .userAssignedBy("userAssignedBy")
                .assignedDate(date)
                .category("category")
                .note("note")
                .specification("specification")
                .status(EAssignStatus.WAITING_FOR_ACCEPTANCE).build();

        when(createAssignmentService.createNewAssignment(requestDataCaptor.capture())).thenReturn(assetResponse);

        RequestBuilder requestBuilder = MockMvcRequestBuilders.post("/api/assignment")
                .content(objectMapper.writeValueAsString(requestData))
                .contentType(MediaType.APPLICATION_JSON);
        MockHttpServletResponse actual = mockMvc.perform(requestBuilder).andReturn().getResponse();

        assertThat(actual.getStatus(), is(HttpStatus.OK.value()));
        assertThat(actual.getContentAsString(), is(
                "{\"id\":1,\"assetId\":0,\"assetCode\":\"assetCode\",\"assetName\":\"assetName\",\"userAssignedToId\":0," +
                        "\"userAssignedTo\":\"userAssignedTo\",\"userAssignedToFullName\":null,\"userAssignedBy\":\"userAssignedBy\"," +
                        "\"assignedDate\":\"2001-01-01T00:00:00.000+00:00\",\"category\":\"category\",\"note\":\"note\"," +
                        "\"specification\":\"specification\",\"status\":\"WAITING_FOR_ACCEPTANCE\"}"));
    }

    @Test
    void createNewAssignment_WhenStatusOfAssetIsNotTypeOfAvailableOrAssignDateIsBeforeToDay_ThrowBadRequestException() throws Exception {
        CreateNewAssignmentRequest requestData = CreateNewAssignmentRequest.builder()
                .assetId(1L)
                .userId(1L)
                .assignedDate(date)
                .note("note").build();
        ArgumentCaptor<CreateNewAssignmentRequest> requestDataCaptor =
                ArgumentCaptor.forClass(CreateNewAssignmentRequest.class);

        when(createAssignmentService.createNewAssignment(requestDataCaptor.capture())).thenThrow(badRequestException);

        RequestBuilder requestBuilder = MockMvcRequestBuilders.post("/api/assignment")
                .content(objectMapper.writeValueAsString(requestData))
                .contentType(MediaType.APPLICATION_JSON);
        MockHttpServletResponse actual = mockMvc.perform(requestBuilder).andReturn().getResponse();

        assertThat(actual.getStatus(), is(HttpStatus.BAD_REQUEST.value()));
        assertThat(actual.getContentAsString(), is("{\"message\":\"Error message\"}"));
    }

    @Test
    void createNewAssignment_WhenAssetOrUserNotExist_ThrowBadRequestException() throws Exception {
        CreateNewAssignmentRequest requestData = CreateNewAssignmentRequest.builder()
                .assetId(1L)
                .userId(1L)
                .assignedDate(date)
                .note("note").build();
        ArgumentCaptor<CreateNewAssignmentRequest> requestDataCaptor =
                ArgumentCaptor.forClass(CreateNewAssignmentRequest.class);

        when(createAssignmentService.createNewAssignment(requestDataCaptor.capture())).thenThrow(notFoundException);

        RequestBuilder requestBuilder = MockMvcRequestBuilders.post("/api/assignment")
                .content(objectMapper.writeValueAsString(requestData))
                .contentType(MediaType.APPLICATION_JSON);
        MockHttpServletResponse actual = mockMvc.perform(requestBuilder).andReturn().getResponse();

        assertThat(actual.getStatus(), is(HttpStatus.NOT_FOUND.value()));
        assertThat(actual.getContentAsString(), is("{\"message\":\"Error message\"}"));
    }

    @Test
    void editAssignment_WhenAllDataRequestValid_ShouldReturnData() throws Exception {
        long assignmentId = 1L;
        EditAssignmentRequest requestData = EditAssignmentRequest.builder()
                .assetId(1L)
                .userId(1L)
                .assignedDate(date)
                .note("note").build();
        ArgumentCaptor<Long> assignmentIdCaptor = ArgumentCaptor.forClass(Long.class);
        ArgumentCaptor<EditAssignmentRequest> requestDataCaptor = ArgumentCaptor.forClass(EditAssignmentRequest.class);
        AssignAssetResponse response = AssignAssetResponse.builder()
                .id(1L)
                .assetId(1L)
                .assetCode("assetCode")
                .assetName("assetName")
                .userAssignedToId(1L)
                .userAssignedTo("userAssignedTo")
                .userAssignedToFullName("fullName")
                .userAssignedBy("userAssignedBy")
                .assignedDate(date)
                .category("categoryName")
                .note("note")
                .specification("specification")
                .status(EAssignStatus.WAITING_FOR_ACCEPTANCE).build();

        when(editAssignAssetService.editAssignment(assignmentIdCaptor.capture(), requestDataCaptor.capture()))
                .thenReturn(response);

        RequestBuilder requestBuilder = MockMvcRequestBuilders.put("/api/assignment/{assignmentId}", assignmentId)
                .content(objectMapper.writeValueAsString(requestData))
                .contentType(MediaType.APPLICATION_JSON);
        MockHttpServletResponse actual = mockMvc.perform(requestBuilder).andReturn().getResponse();

        assertThat(actual.getStatus(), is(HttpStatus.OK.value()));
        assertThat(actual.getContentAsString(), is(
                "{\"id\":1,\"assetId\":1,\"assetCode\":\"assetCode\",\"assetName\":\"assetName\"," +
                        "\"userAssignedToId\":1,\"userAssignedTo\":\"userAssignedTo\",\"userAssignedToFullName\":\"fullName\"," +
                        "\"userAssignedBy\":\"userAssignedBy\",\"assignedDate\":\"2001-01-01T00:00:00.000+00:00\"," +
                        "\"category\":\"categoryName\",\"note\":\"note\",\"specification\":\"specification\"," +
                        "\"status\":\"WAITING_FOR_ACCEPTANCE\"}"));
    }

    @Test
    void editAssignment_WhenAnyDataRequestInValid_ThrowBadRequestException() throws Exception {
        long assignmentId = 1L;
        EditAssignmentRequest requestData = EditAssignmentRequest.builder()
                .assetId(1L)
                .userId(1L)
                .assignedDate(date)
                .note("note").build();
        ArgumentCaptor<Long> assignmentIdCaptor = ArgumentCaptor.forClass(Long.class);
        ArgumentCaptor<EditAssignmentRequest> requestDataCaptor = ArgumentCaptor.forClass(EditAssignmentRequest.class);

        when(editAssignAssetService.editAssignment(assignmentIdCaptor.capture(), requestDataCaptor.capture()))
                .thenThrow(badRequestException);

        RequestBuilder requestBuilder = MockMvcRequestBuilders.put("/api/assignment/{assignmentId}", assignmentId)
                .content(objectMapper.writeValueAsString(requestData))
                .contentType(MediaType.APPLICATION_JSON);
        MockHttpServletResponse actual = mockMvc.perform(requestBuilder).andReturn().getResponse();

        assertThat(actual.getStatus(), is(HttpStatus.BAD_REQUEST.value()));
        assertThat(actual.getContentAsString(), is("{\"message\":\"Error message\"}"));
    }

    @Test
    void editAssignment_WhenAnyDataRequestInValid_ThrowNotFoundException() throws Exception {
        long assignmentId = 1L;
        EditAssignmentRequest requestData = EditAssignmentRequest.builder()
                .assetId(1L)
                .userId(1L)
                .assignedDate(date)
                .note("note").build();
        ArgumentCaptor<Long> assignmentIdCaptor = ArgumentCaptor.forClass(Long.class);
        ArgumentCaptor<EditAssignmentRequest> requestDataCaptor = ArgumentCaptor.forClass(EditAssignmentRequest.class);

        when(editAssignAssetService.editAssignment(assignmentIdCaptor.capture(), requestDataCaptor.capture()))
                .thenThrow(notFoundException);

        RequestBuilder requestBuilder = MockMvcRequestBuilders.put("/api/assignment/{assignmentId}", assignmentId)
                .content(objectMapper.writeValueAsString(requestData))
                .contentType(MediaType.APPLICATION_JSON);
        MockHttpServletResponse actual = mockMvc.perform(requestBuilder).andReturn().getResponse();

        assertThat(actual.getStatus(), is(HttpStatus.NOT_FOUND.value()));
        assertThat(actual.getContentAsString(), is("{\"message\":\"Error message\"}"));
    }

}
