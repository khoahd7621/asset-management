package com.nashtech.assignment.services.impl;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.mockito.Mockito.*;

import com.nashtech.assignment.data.constants.EAssetStatus;
import com.nashtech.assignment.data.entities.Asset;
import com.nashtech.assignment.data.entities.User;
import com.nashtech.assignment.data.repositories.AssetRepository;
import com.nashtech.assignment.dto.request.asset.SearchFilterAssetRequest;
import com.nashtech.assignment.dto.response.PaginationResponse;
import com.nashtech.assignment.dto.response.asset.AssetResponse;
import com.nashtech.assignment.mappers.AssetMapper;
import com.nashtech.assignment.services.SecurityContextService;
import com.nashtech.assignment.utils.PageableUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.data.domain.*;

import java.util.ArrayList;
import java.util.List;

class FilterServiceImplTest {

    private FilterServiceImpl filterServiceImpl;
    private AssetRepository assetRepository;
    private AssetMapper assetMapper;
    private PageableUtil pageableUtil;
    private SecurityContextService securityContextService;
    private Asset asset;
    private AssetResponse assetResponse;
    private User user;

    @BeforeEach
    void setup() {
        assetRepository = mock(AssetRepository.class);
        assetMapper = mock(AssetMapper.class);
        pageableUtil = mock(PageableUtil.class);
        securityContextService = mock(SecurityContextService.class);
        filterServiceImpl = FilterServiceImpl.builder()
                .assetRepository(assetRepository)
                .assetMapper(assetMapper)
                .pageableUtil(pageableUtil)
                .securityContextService(securityContextService).build();
        asset = mock(Asset.class);
        assetResponse = mock(AssetResponse.class);
        user = mock(User.class);
    }

    @Test
    void filterAllAssetsByLocationAndKeyWordInStatusesAndCategoriesWithPagination_WhenKeyWordAndStatusesAndCategoryIdsNotNull_ShouldReturnData() {
        List<EAssetStatus> statuses = new ArrayList<>();
        statuses.add(EAssetStatus.AVAILABLE);
        List<Integer> categoryIds = new ArrayList<>();
        categoryIds.add(1);
        SearchFilterAssetRequest searchFilterAssetRequest = SearchFilterAssetRequest.builder()
                .keyword("keyword")
                .statuses(statuses)
                .categoryIds(categoryIds)
                .limit(20)
                .page(0)
                .sortField("name")
                .sortType("ASC").build();

        Pageable pageable = PageRequest.of(
                searchFilterAssetRequest.getPage(),
                searchFilterAssetRequest.getLimit(),
                Sort.by(searchFilterAssetRequest.getSortField()).ascending());
        ArgumentCaptor<String> keywordCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<List<EAssetStatus>> statusesCaptor = ArgumentCaptor.forClass(List.class);
        ArgumentCaptor<List<Integer>> categoryIdsCaptor = ArgumentCaptor.forClass(List.class);
        ArgumentCaptor<String> locationCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<Pageable> pageableCaptor = ArgumentCaptor.forClass(Pageable.class);
        List<Asset> assetList = new ArrayList<>();
        assetList.add(asset);
        Page<Asset> assetPage = new PageImpl<>(assetList);
        List<AssetResponse> assetResponseList = new ArrayList<>();
        assetResponseList.add(assetResponse);
        PaginationResponse<List<AssetResponse>> expectedResponse = PaginationResponse.<List<AssetResponse>>builder()
                .data(assetResponseList)
                .totalRow(1)
                .totalPage(1).build();

        when(securityContextService.getCurrentUser()).thenReturn(user);
        when(pageableUtil.getPageable(
                searchFilterAssetRequest.getPage(),
                searchFilterAssetRequest.getLimit(),
                searchFilterAssetRequest.getSortField(),
                searchFilterAssetRequest.getSortType())).thenReturn(pageable);
        when(assetRepository.findAllAssetsByQueryAndStatusesAndCategoryIds(
                keywordCaptor.capture(),
                statusesCaptor.capture(),
                categoryIdsCaptor.capture(),
                locationCaptor.capture(),
                pageableCaptor.capture())).thenReturn(assetPage);
        when(assetMapper.toListAssetsResponse(assetPage.toList())).thenReturn(assetResponseList);

        PaginationResponse<List<AssetResponse>> actual = filterServiceImpl.
                filterAllAssetsByLocationAndKeyWordInStatusesAndCategoriesWithPagination(searchFilterAssetRequest);

        Pageable actualPageable = pageableCaptor.getValue();
        assertThat(actualPageable, is(pageable));
        assertThat(keywordCaptor.getValue(), is("keyword"));
        assertThat(statusesCaptor.getValue(), is(statuses));
        assertThat(categoryIdsCaptor.getValue(), is(categoryIds));
        assertThat(locationCaptor.getValue(), is(user.getLocation()));
        assertThat(actual.getData(), is(expectedResponse.getData()));
        assertThat(actual.getTotalPage(), is(expectedResponse.getTotalPage()));
        assertThat(actual.getTotalRow(), is(expectedResponse.getTotalRow()));
    }

    @Test
    void filterAllAssetsByLocationAndKeyWordInStatusesAndCategoriesWithPagination_WhenKeyWordOrStatusesOrCategoryIdsIsNull_ShouldReturnData() {
        SearchFilterAssetRequest searchFilterAssetRequest = SearchFilterAssetRequest.builder()
                .keyword(null)
                .statuses(null)
                .categoryIds(null)
                .limit(20)
                .page(0)
                .sortField("name")
                .sortType("ASC").build();

        Pageable pageable = PageRequest.of(
                searchFilterAssetRequest.getPage(),
                searchFilterAssetRequest.getLimit(),
                Sort.by(searchFilterAssetRequest.getSortField()).ascending());
        ArgumentCaptor<String> keywordCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<List<EAssetStatus>> statusesCaptor = ArgumentCaptor.forClass(List.class);
        ArgumentCaptor<List<Integer>> categoryIdsCaptor = ArgumentCaptor.forClass(List.class);
        ArgumentCaptor<String> locationCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<Pageable> pageableCaptor = ArgumentCaptor.forClass(Pageable.class);
        List<Asset> assetList = new ArrayList<>();
        assetList.add(asset);
        Page<Asset> assetPage = new PageImpl<>(assetList);
        List<AssetResponse> assetResponseList = new ArrayList<>();
        assetResponseList.add(assetResponse);
        PaginationResponse<List<AssetResponse>> expectedResponse = PaginationResponse.<List<AssetResponse>>builder()
                .data(assetResponseList)
                .totalRow(1)
                .totalPage(1).build();

        when(securityContextService.getCurrentUser()).thenReturn(user);
        when(pageableUtil.getPageable(
                searchFilterAssetRequest.getPage(),
                searchFilterAssetRequest.getLimit(),
                searchFilterAssetRequest.getSortField(),
                searchFilterAssetRequest.getSortType())).thenReturn(pageable);
        when(assetRepository.findAllAssetsByQueryAndStatusesAndCategoryIds(
                keywordCaptor.capture(),
                statusesCaptor.capture(),
                categoryIdsCaptor.capture(),
                locationCaptor.capture(),
                pageableCaptor.capture())).thenReturn(assetPage);
        when(assetMapper.toListAssetsResponse(assetPage.toList())).thenReturn(assetResponseList);

        PaginationResponse<List<AssetResponse>> actual = filterServiceImpl.
                filterAllAssetsByLocationAndKeyWordInStatusesAndCategoriesWithPagination(searchFilterAssetRequest);

        keywordCaptor.getValue();

        Pageable actualPageable = pageableCaptor.getValue();
        assertThat(actualPageable, is(pageable));
        assertThat(keywordCaptor.getValue(), is(nullValue()));
        assertThat(statusesCaptor.getValue(), is(nullValue()));
        assertThat(categoryIdsCaptor.getValue(), is(nullValue()));
        assertThat(locationCaptor.getValue(), is(user.getLocation()));
        assertThat(actual.getData(), is(expectedResponse.getData()));
        assertThat(actual.getTotalPage(), is(expectedResponse.getTotalPage()));
        assertThat(actual.getTotalRow(), is(expectedResponse.getTotalRow()));
    }
}