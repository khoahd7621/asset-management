package com.nashtech.assignment.controllers;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.when;

import com.nashtech.assignment.AssignmentApplication;
import com.nashtech.assignment.config.CORSConfig;
import com.nashtech.assignment.config.SecurityConfig;
import com.nashtech.assignment.data.constants.EAssetStatus;
import com.nashtech.assignment.dto.request.asset.SearchFilterAssetRequest;
import com.nashtech.assignment.dto.response.PaginationResponse;
import com.nashtech.assignment.dto.response.asset.AssetAndHistoriesResponse;
import com.nashtech.assignment.dto.response.asset.AssetHistory;
import com.nashtech.assignment.dto.response.asset.AssetResponse;
import com.nashtech.assignment.dto.response.category.CategoryResponse;
import com.nashtech.assignment.exceptions.NotFoundException;
import com.nashtech.assignment.services.FilterService;
import com.nashtech.assignment.services.GetService;
import com.nashtech.assignment.services.SecurityContextService;
import com.nashtech.assignment.utils.JwtTokenUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
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

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

@WebMvcTest(value = AssetController.class)
@ContextConfiguration(classes = { AssignmentApplication.class, SecurityConfig.class, CORSConfig.class })
@AutoConfigureMockMvc(addFilters = false)
class AssetControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private FilterService filterService;
    @MockBean
    private GetService getService;
    @MockBean
    private JwtTokenUtil jwtTokenUtil;
    @MockBean
    private SecurityContextService securityContextService;

    private Date date;
    private AssetResponse assetResponse;
    private NotFoundException notFoundException;

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
                        "\"id\":1,\"assetName\":\"name\",\"assetCode\":\"code\",\"installedDate\":\"2001-01-01T00:00:00.000+00:00\"," +
                        "\"specification\":\"specification\",\"status\":\"AVAILABLE\",\"location\":\"location\"," +
                        "\"category\":{\"id\":1,\"name\":\"name\",\"prefixAssetCode\":\"code\"},\"deleted\":false}," +
                        "\"histories\":[{\"assignedDate\":null,\"assignedTo\":\"username\",\"assignedBy\":\"username\"," +
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
        ArgumentCaptor<SearchFilterAssetRequest> searchFilterAssetRequestCaptor =
                ArgumentCaptor.forClass(SearchFilterAssetRequest.class);

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
                        "{\"id\":1,\"assetName\":\"name\",\"assetCode\":\"code\",\"installedDate\":\"2001-01-01T00:00:00.000+00:00\"," +
                        "\"specification\":\"specification\",\"status\":\"AVAILABLE\",\"location\":\"location\"," +
                        "\"category\":{\"id\":1,\"name\":\"name\",\"prefixAssetCode\":\"code\"},\"deleted\":false}" +
                        "],\"totalPage\":1,\"totalRow\":1}"));
    }
}