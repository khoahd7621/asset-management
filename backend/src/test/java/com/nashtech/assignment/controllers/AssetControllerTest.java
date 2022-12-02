package com.nashtech.assignment.controllers;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.doThrow;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nashtech.assignment.AssignmentApplication;
import com.nashtech.assignment.config.CORSConfig;
import com.nashtech.assignment.config.SecurityConfig;
import com.nashtech.assignment.data.constants.EAssetStatus;
import com.nashtech.assignment.dto.request.asset.CreateNewAssetRequest;
import com.nashtech.assignment.dto.request.asset.EditAssetInformationRequest;
import com.nashtech.assignment.dto.request.asset.SearchFilterAssetRequest;
import com.nashtech.assignment.dto.response.PaginationResponse;
import com.nashtech.assignment.dto.response.asset.AssetAndHistoriesResponse;
import com.nashtech.assignment.dto.response.asset.AssetHistory;
import com.nashtech.assignment.dto.response.asset.AssetResponse;
import com.nashtech.assignment.dto.response.category.CategoryResponse;
import com.nashtech.assignment.exceptions.BadRequestException;
import com.nashtech.assignment.exceptions.NotFoundException;
import com.nashtech.assignment.services.*;
import com.nashtech.assignment.services.get.GetAssetService;
import com.nashtech.assignment.utils.JwtTokenUtil;
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
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

@WebMvcTest(value = AssetController.class)
@ContextConfiguration(classes = { AssignmentApplication.class, SecurityConfig.class, CORSConfig.class })
@AutoConfigureMockMvc(addFilters = false)
class AssetControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @MockBean
    private FilterService filterService;
    @MockBean
    private GetService getService;
    @MockBean
    private JwtTokenUtil jwtTokenUtil;
    @MockBean
    private SecurityContextService securityContextService;
    @MockBean
    private EditService editService;
    @MockBean
    private CreateService createService;
    @MockBean
    private DeleteService deleteService;
    @MockBean
    private GetAssetService getAssetService;

    private Date date;
    private AssetResponse assetResponse;
    private NotFoundException notFoundException;
    private BadRequestException badRequestException;

    @BeforeEach
    void setup() throws ParseException {
        SimpleDateFormat formatterDate = new SimpleDateFormat("dd/MM/yyyy");
        formatterDate.setTimeZone(TimeZone.getTimeZone("GMT"));
        date = formatterDate.parse("01/01/2001");
        assetResponse = AssetResponse.builder()
                .id(1)
                .assetName("name")
                .assetCode("code")
                .installedDate(date)
                .specification("specification")
                .status(EAssetStatus.AVAILABLE)
                .location("location")
                .isDeleted(false)
                .category(CategoryResponse.builder()
                        .id(1)
                        .name("name")
                        .prefixAssetCode("code").build())
                .build();
        notFoundException = new NotFoundException("error message");
        badRequestException = new BadRequestException("error message");
    }

    @Test
    void getAssetAndItsHistoriesByAssetId_WhenAssetIdExist_ShouldReturnData() throws Exception {
        List<AssetHistory> assetHistories = new ArrayList<>();
        assetHistories.add(AssetHistory.builder()
                .returnedDate(date)
                .assignedBy("username")
                .assignedTo("username")
                .returnedDate(date).build());
        AssetAndHistoriesResponse expected = AssetAndHistoriesResponse.builder()
                .asset(assetResponse)
                .histories(assetHistories).build();

        when(getService.getAssetAndItsHistoriesByAssetId(1L)).thenReturn(expected);

        RequestBuilder requestBuilder = MockMvcRequestBuilders.get("/api/asset/{assetId}", 1L);
        MockHttpServletResponse actual = mockMvc.perform(requestBuilder).andReturn().getResponse();

        assertThat(actual.getStatus(), is(HttpStatus.OK.value()));
        assertThat(actual.getContentAsString(), is(
                "{\"asset\":{" +
                        "\"id\":1,\"assetName\":\"name\",\"assetCode\":\"code\",\"installedDate\":\"2001-01-01T00:00:00.000+00:00\","
                        +
                        "\"specification\":\"specification\",\"status\":\"AVAILABLE\",\"location\":\"location\"," +
                        "\"category\":{\"id\":1,\"name\":\"name\",\"prefixAssetCode\":\"code\"},\"deleted\":false}," +
                        "\"histories\":[{\"assignedDate\":null,\"assignedTo\":\"username\",\"assignedBy\":\"username\","
                        +
                        "\"returnedDate\":\"2001-01-01T00:00:00.000+00:00\"}]}"));
    }

  @Test
    void getAssetAndItsHistoriesByAssetId_WhenAssetIdNotExist_ShouldThrowNotFoundException() throws Exception {
        when(getService.getAssetAndItsHistoriesByAssetId(1L)).thenThrow(notFoundException);

        RequestBuilder requestBuilder = MockMvcRequestBuilders.get("/api/asset/{assetId}", 1L);
        MockHttpServletResponse actual = mockMvc.perform(requestBuilder).andReturn().getResponse();

        assertThat(actual.getStatus(), is(HttpStatus.NOT_FOUND.value()));
        assertThat(actual.getContentAsString(), is("{\"message\":\"error message\"}"));
    }

    @Test
    void filterAllAssetsByLocationAndKeyWordInStatusesAndCategoriesWithPagination_WhenValidDataRequest_ShouldReturnData()
            throws Exception {
        List<EAssetStatus> statuses = new ArrayList<>();
        statuses.add(EAssetStatus.AVAILABLE);
        List<Integer> categoriesId = new ArrayList<>();
        categoriesId.add(1);
        List<AssetResponse> data = new ArrayList<>();
        data.add(assetResponse);
        PaginationResponse<List<AssetResponse>> response = PaginationResponse.<List<AssetResponse>>builder()
                .data(data)
                .totalRow(1)
                .totalPage(1).build();
        ArgumentCaptor<SearchFilterAssetRequest> searchFilterAssetRequestCaptor = ArgumentCaptor
                .forClass(SearchFilterAssetRequest.class);

        when(filterService.filterAllAssetsByLocationAndKeyWordInStatusesAndCategoriesWithPagination(
                searchFilterAssetRequestCaptor.capture())).thenReturn(response);

        RequestBuilder requestBuilder = MockMvcRequestBuilders.get("/api/asset")
                .param("key-word", "keyword")
                .param("statuses", EAssetStatus.AVAILABLE.toString())
                .param("categories", "1")
                .param("limit", "20")
                .param("page", "0")
                .param("sort-field", "name")
                .param("sort-type", "ASC");
        MockHttpServletResponse actual = mockMvc.perform(requestBuilder).andReturn().getResponse();

        SearchFilterAssetRequest searchFilterAssetRequestActual = searchFilterAssetRequestCaptor.getValue();
        assertThat(searchFilterAssetRequestActual.getKeyword(), is("keyword"));
        assertThat(searchFilterAssetRequestActual.getStatuses(), is(statuses));
        assertThat(searchFilterAssetRequestActual.getCategoryIds(), is(categoriesId));
        assertThat(searchFilterAssetRequestActual.getLimit(), is(20));
        assertThat(searchFilterAssetRequestActual.getPage(), is(0));
        assertThat(searchFilterAssetRequestActual.getSortField(), is("name"));
        assertThat(searchFilterAssetRequestActual.getSortType(), is("ASC"));
        assertThat(actual.getStatus(), is(HttpStatus.OK.value()));
        assertThat(actual.getContentAsString(), is(
                "{\"data\":[" +
                        "{\"id\":1,\"assetName\":\"name\",\"assetCode\":\"code\",\"installedDate\":\"2001-01-01T00:00:00.000+00:00\","
                        +
                        "\"specification\":\"specification\",\"status\":\"AVAILABLE\",\"location\":\"location\"," +
                        "\"category\":{\"id\":1,\"name\":\"name\",\"prefixAssetCode\":\"code\"},\"deleted\":false}" +
                        "],\"totalPage\":1,\"totalRow\":1}"));
    }

    @Test
    void testEditAssetInformation_WhenDataValid_ShouldReturnReponse() throws Exception {
        EditAssetInformationRequest editAssetInformationRequest = EditAssetInformationRequest.builder()
                .assetName("assetName")
                .specification("assetSpecification")
                .assetStatus(EAssetStatus.AVAILABLE)
                .installedDate("01/01/2001")
                .build();

        ArgumentCaptor<EditAssetInformationRequest> editAssetInformationRequestCaptor = ArgumentCaptor
                .forClass(EditAssetInformationRequest.class);

        ArgumentCaptor<Long> idCaptor = ArgumentCaptor.forClass(Long.class);

        AssetResponse editAssetInformationResponse = AssetResponse.builder()
                .assetName("assetName")
                .specification("assetSpecification")
                .status(EAssetStatus.AVAILABLE)
                .installedDate(date)
                .build();

        when(editService.editAssetInformation(idCaptor.capture(), editAssetInformationRequestCaptor.capture()))
                .thenReturn(editAssetInformationResponse);

        RequestBuilder requestBuilder = MockMvcRequestBuilders.put("/api/asset/{assetId}", 1L)
                .content(objectMapper.writeValueAsString(editAssetInformationRequest))
                .contentType(MediaType.APPLICATION_JSON);

        MockHttpServletResponse actual = mockMvc.perform(requestBuilder).andReturn().getResponse();
        assertThat(actual.getContentAsString(),
                is("{\"id\":0,\"assetName\":\"assetName\",\"assetCode\":null,\"installedDate\":\"2001-01-01T00:00:00.000+00:00\",\"specification\":\"assetSpecification\",\"status\":\"AVAILABLE\",\"location\":null,\"category\":null,\"deleted\":false}"));
    }

    @Test
    void testEditAssetInformation_WhenAssetNotFound_ShouldReturnException() throws Exception {
        EditAssetInformationRequest editAssetInformationRequest = EditAssetInformationRequest.builder()
                .assetName("assetName")
                .specification("assetSpecification")
                .assetStatus(EAssetStatus.AVAILABLE)
                .installedDate("01/01/2001")
                .build();

        ArgumentCaptor<EditAssetInformationRequest> editAssetInformationRequestCaptor = ArgumentCaptor
                .forClass(EditAssetInformationRequest.class);

        ArgumentCaptor<Long> idCaptor = ArgumentCaptor.forClass(Long.class);

        when(editService.editAssetInformation(idCaptor.capture(), editAssetInformationRequestCaptor.capture()))
                .thenThrow(notFoundException);

        RequestBuilder requestBuilder = MockMvcRequestBuilders.put("/api/asset/{assetId}", 1L)
                .content(objectMapper.writeValueAsString(editAssetInformationRequest))
                .contentType(MediaType.APPLICATION_JSON);

        MockHttpServletResponse actual = mockMvc.perform(requestBuilder).andReturn().getResponse();

        assertThat(actual.getStatus(), is(HttpStatus.NOT_FOUND.value()));
        assertThat(actual.getContentAsString(), is("{\"message\":\"error message\"}"));
    }

    @Test
    void testEditAssetInformation_WhenAssetHaveStatusAssigned_ShouldReturnException() throws Exception {
        EditAssetInformationRequest editAssetInformationRequest = EditAssetInformationRequest.builder()
                .assetName("assetName")
                .specification("assetSpecification")
                .assetStatus(EAssetStatus.ASSIGNED)
                .installedDate("01/01/2001")
                .build();

        ArgumentCaptor<EditAssetInformationRequest> editAssetInformationRequestCaptor = ArgumentCaptor
                .forClass(EditAssetInformationRequest.class);

        ArgumentCaptor<Long> idCaptor = ArgumentCaptor.forClass(Long.class);

        when(editService.editAssetInformation(idCaptor.capture(), editAssetInformationRequestCaptor.capture()))
                .thenThrow(badRequestException);

        RequestBuilder requestBuilder = MockMvcRequestBuilders.put("/api/asset/{assetId}", 1L)
                .content(objectMapper.writeValueAsString(editAssetInformationRequest))
                .contentType(MediaType.APPLICATION_JSON);

        MockHttpServletResponse actual = mockMvc.perform(requestBuilder).andReturn().getResponse();

        assertThat(actual.getStatus(), is(HttpStatus.BAD_REQUEST.value()));
        assertThat(actual.getContentAsString(), is("{\"message\":\"error message\"}"));
    }

    @Test
    void testCreateAssetResponse_WhenDataValid_ShouldReturnData() throws Exception {
    CreateNewAssetRequest request = CreateNewAssetRequest
        .builder()
        .assetName("assetName")
        .categoryName("categoryName")
        .specification("specification")
        .assetStatus(EAssetStatus.AVAILABLE)
        .installedDate("01/01/2001")
        .build();

    ArgumentCaptor<CreateNewAssetRequest> assetCaptor = ArgumentCaptor
        .forClass(CreateNewAssetRequest.class);

    AssetResponse response = AssetResponse
        .builder()
        .assetName("assetName")
        .installedDate(date)
        .specification("specification")
        .status(EAssetStatus.AVAILABLE)
        .location("location")
        .build();

    when(createService.createAssetResponse(assetCaptor.capture())).thenReturn(response);

    RequestBuilder requestBuilder = MockMvcRequestBuilders.post("/api/asset")
        .content(objectMapper.writeValueAsString(request))
        .contentType(MediaType.APPLICATION_JSON);

    MockHttpServletResponse actual = mockMvc.perform(requestBuilder).andReturn().getResponse();
    assertThat(actual.getContentAsString(),
        is("{\"id\":0,\"assetName\":\"assetName\",\"assetCode\":null,\"installedDate\":\"2001-01-01T00:00:00.000+00:00\",\"specification\":\"specification\",\"status\":\"AVAILABLE\",\"location\":\"location\",\"category\":null,\"deleted\":false}"));
  }

    @Test
    void testCheckAssetIsValidForDeleteOrNot_WhenAssetIdExistAndValidForDelete_ShouldReturnNoContent() throws Exception {
        long assetId = 1L;

        doNothing().when(getService).checkAssetIsValidForDeleteOrNot(assetId);

        RequestBuilder requestBuilder = MockMvcRequestBuilders.get("/api/asset/check-asset/{assetId}", assetId);
        MockHttpServletResponse actual = mockMvc.perform(requestBuilder).andReturn().getResponse();

        assertThat(actual.getStatus(), is(HttpStatus.NO_CONTENT.value()));
    }

    @Test
    void testCheckAssetIsValidForDeleteOrNot_WhenAssetIdNotExist_ShouldThrowNotFoundException() throws Exception {
        long assetId = 1L;

        doThrow(notFoundException).when(getService).checkAssetIsValidForDeleteOrNot(assetId);

        RequestBuilder requestBuilder = MockMvcRequestBuilders.get("/api/asset/check-asset/{assetId}", assetId);
        MockHttpServletResponse actual = mockMvc.perform(requestBuilder).andReturn().getResponse();

        assertThat(actual.getStatus(), is(HttpStatus.NOT_FOUND.value()));
        assertThat(actual.getContentAsString(), is("{\"message\":\"error message\"}"));
    }

    @Test
    void testCheckAssetIsValidForDeleteOrNot_WhenAssetIdExistButNotValidForDelete_ShouldThrowBadRequestException() throws Exception {
        long assetId = 1L;

        doThrow(badRequestException).when(getService).checkAssetIsValidForDeleteOrNot(assetId);

        RequestBuilder requestBuilder = MockMvcRequestBuilders.get("/api/asset/check-asset/{assetId}", assetId);
        MockHttpServletResponse actual = mockMvc.perform(requestBuilder).andReturn().getResponse();

        assertThat(actual.getStatus(), is(HttpStatus.BAD_REQUEST.value()));
        assertThat(actual.getContentAsString(), is("{\"message\":\"error message\"}"));
    }

    @Test
    void deleteAssetByAssetId_WhenAssetIdExistAndValidForDelete_ShouldReturnNoContent() throws Exception {
        long assetId = 1L;

        doNothing().when(deleteService).deleteAssetByAssetId(assetId);

        RequestBuilder requestBuilder = MockMvcRequestBuilders.delete("/api/asset/{assetId}", assetId);
        MockHttpServletResponse actual = mockMvc.perform(requestBuilder).andReturn().getResponse();

        assertThat(actual.getStatus(), is(HttpStatus.NO_CONTENT.value()));
    }

    @Test
    void deleteAssetByAssetId_WhenAssetIdNotExist_ShouldThrowNotFoundException() throws Exception {
        long assetId = 1L;

        doThrow(notFoundException).when(deleteService).deleteAssetByAssetId(assetId);

        RequestBuilder requestBuilder = MockMvcRequestBuilders.delete("/api/asset/{assetId}", assetId);
        MockHttpServletResponse actual = mockMvc.perform(requestBuilder).andReturn().getResponse();

        assertThat(actual.getStatus(), is(HttpStatus.NOT_FOUND.value()));
        assertThat(actual.getContentAsString(), is("{\"message\":\"error message\"}"));
    }

    @Test
    void deleteAssetByAssetId_WhenAssetIdExistButNotValidForDelete_ShouldThrowBadRequestException() throws Exception {
        long assetId = 1L;

        doThrow(badRequestException).when(deleteService).deleteAssetByAssetId(assetId);

        RequestBuilder requestBuilder = MockMvcRequestBuilders.delete("/api/asset/{assetId}", assetId);
        MockHttpServletResponse actual = mockMvc.perform(requestBuilder).andReturn().getResponse();

        assertThat(actual.getStatus(), is(HttpStatus.BAD_REQUEST.value()));
        assertThat(actual.getContentAsString(), is("{\"message\":\"error message\"}"));
    }

    @Test
    void getAllAssetByAssetStatus_ShouldReturnData() throws Exception {
        EAssetStatus assetStatus = EAssetStatus.AVAILABLE;
        List<AssetResponse> response = new ArrayList<>();
        response.add(assetResponse);

        when(getAssetService.getAllAssetByAssetStatus(assetStatus)).thenReturn(response);

        RequestBuilder requestBuilder = MockMvcRequestBuilders.get("/api/asset/status/{assetStatus}", assetStatus);
        MockHttpServletResponse actual = mockMvc.perform(requestBuilder).andReturn().getResponse();

        assertThat(actual.getStatus(), is(HttpStatus.OK.value()));
        assertThat(actual.getContentAsString(), is(
                "[{\"id\":1,\"assetName\":\"name\",\"assetCode\":\"code\",\"installedDate\":\"2001-01-01T00:00:00.000+00:00\"," +
                        "\"specification\":\"specification\",\"status\":\"AVAILABLE\",\"location\":\"location\"," +
                        "\"category\":{\"id\":1,\"name\":\"name\",\"prefixAssetCode\":\"code\"},\"deleted\":false}]"));
    }

    @Test
    void getAllAssetByAssetStatusWithPagination_ShouldReturnData() throws Exception {
        EAssetStatus status = EAssetStatus.AVAILABLE;
        int page = 0;
        int limit = 20;
        String sortField = "name";
        String sortType = "ASC";
        List<AssetResponse> assetResponseList = new ArrayList<>();
        assetResponseList.add(assetResponse);
        PaginationResponse<List<AssetResponse>> response = PaginationResponse.<List<AssetResponse>>builder()
                .data(assetResponseList)
                .totalPage(1)
                .totalRow(1).build();

        when(getAssetService.getAllAssetByAssetStatusWithPagination(status, page, limit, sortField, sortType)).thenReturn(response);

        RequestBuilder requestBuilder = MockMvcRequestBuilders.get("/api/asset/status/{assetStatus}/pagination", status)
                .param("page", String.valueOf(page))
                .param("limit", String.valueOf(limit))
                .param("sort-field", sortField)
                .param("sort-type", sortType);
        MockHttpServletResponse actual = mockMvc.perform(requestBuilder).andReturn().getResponse();

        assertThat(actual.getStatus(), is(HttpStatus.OK.value()));
        assertThat(actual.getContentAsString(), is(
                "{\"data\":[{\"id\":1,\"assetName\":\"name\",\"assetCode\":\"code\"," +
                        "\"installedDate\":\"2001-01-01T00:00:00.000+00:00\"," +
                        "\"specification\":\"specification\",\"status\":\"AVAILABLE\",\"location\":\"location\"," +
                        "\"category\":{\"id\":1,\"name\":\"name\",\"prefixAssetCode\":\"code\"},\"deleted\":false}]," +
                        "\"totalPage\":1,\"totalRow\":1}"
        ));
    }

}
