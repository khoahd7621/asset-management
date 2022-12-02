package com.nashtech.assignment.services.get.impl;

import com.nashtech.assignment.data.constants.EAssetStatus;
import com.nashtech.assignment.data.constants.EAssignStatus;
import com.nashtech.assignment.data.entities.Asset;
import com.nashtech.assignment.data.entities.User;
import com.nashtech.assignment.data.repositories.AssetRepository;
import com.nashtech.assignment.data.repositories.AssignAssetRepository;
import com.nashtech.assignment.dto.response.PaginationResponse;
import com.nashtech.assignment.dto.response.asset.AssetAndHistoriesResponse;
import com.nashtech.assignment.dto.response.asset.AssetResponse;
import com.nashtech.assignment.exceptions.BadRequestException;
import com.nashtech.assignment.exceptions.NotFoundException;
import com.nashtech.assignment.mappers.AssetMapper;
import com.nashtech.assignment.services.auth.SecurityContextService;
import com.nashtech.assignment.utils.PageableUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.data.domain.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class GetAssetServiceImplTest {

    private GetAssetServiceImpl getAssetServiceImpl;
    private AssetRepository assetRepository;
    private AssetMapper assetMapper;
    private SecurityContextService securityContextService;
    private AssignAssetRepository assignAssetRepository;
    private PageableUtil pageableUtil;
    private Asset asset;
    private AssetResponse assetResponse;

    @BeforeEach
    void setup() {
        assetRepository = mock(AssetRepository.class);
        assetMapper = mock(AssetMapper.class);
        securityContextService = mock(SecurityContextService.class);
        pageableUtil = mock(PageableUtil.class);
        assignAssetRepository = mock(AssignAssetRepository.class);
        getAssetServiceImpl = GetAssetServiceImpl.builder()
                .assetRepository(assetRepository)
                .assetMapper(assetMapper)
                .securityContextService(securityContextService)
                .pageableUtil(pageableUtil)
                .assignAssetRepository(assignAssetRepository).build();
        asset = mock(Asset.class);
        assetResponse = mock(AssetResponse.class);
    }

    @Test
    void getAllAssetByAssetStatus_ShouldReturnData() {
        EAssetStatus eAssetStatus = EAssetStatus.AVAILABLE;
        User userTest = User.builder().location("location").build();
        List<Asset> assetList = new ArrayList<>();
        assetList.add(asset);
        List<AssetResponse> expected = new ArrayList<>();
        expected.add(assetResponse);

        ArgumentCaptor<EAssetStatus> eAssetStatusCaptor = ArgumentCaptor.forClass(EAssetStatus.class);
        ArgumentCaptor<String> locationCaptor = ArgumentCaptor.forClass(String.class);

        when(securityContextService.getCurrentUser()).thenReturn(userTest);
        when(assetRepository
                .findAllByStatusAndLocationAndIsDeletedFalse(eAssetStatusCaptor.capture(), locationCaptor.capture()))
                .thenReturn(assetList);
        when(assetMapper.toListAssetsResponse(assetList)).thenReturn(expected);

        List<AssetResponse> actual = getAssetServiceImpl.getAllAssetByAssetStatus(eAssetStatus);

        EAssetStatus assetStatusActual = eAssetStatusCaptor.getValue();
        String locationActual = locationCaptor.getValue();
        assertThat(assetStatusActual, is(EAssetStatus.AVAILABLE));
        assertThat(locationActual, is("location"));
        assertThat(actual, is(expected));
    }

    @Test
    void getAllAssetByAssetStatusWithPagination_ShouldReturnData() {
        EAssetStatus status = EAssetStatus.AVAILABLE;
        int page = 0;
        int limit = 20;
        String sortField = "name";
        String sortType = "ASC";

        User userLoggedIn = User.builder().location("location").build();
        Pageable pageableCreate = PageRequest.of(0, 20, Sort.by("name").ascending());
        List<Asset> assetList = new ArrayList<>();
        assetList.add(asset);
        List<AssetResponse> assetResponseList = new ArrayList<>();
        assetResponseList.add(assetResponse);
        Page<Asset> assetPage = new PageImpl<>(assetList);
        PaginationResponse<List<AssetResponse>> expected = PaginationResponse.<List<AssetResponse>>builder()
                .data(assetResponseList)
                .totalRow(1)
                .totalPage(0).build();

        ArgumentCaptor<EAssetStatus> eAssetStatusCaptor = ArgumentCaptor.forClass(EAssetStatus.class);
        ArgumentCaptor<String> locationCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<Pageable> pageableCaptor = ArgumentCaptor.forClass(Pageable.class);

        when(pageableUtil.getPageable(page, limit, sortField, sortType)).thenReturn(pageableCreate);
        when(securityContextService.getCurrentUser()).thenReturn(userLoggedIn);
        when(assetRepository.findAllByStatusAndLocationAndIsDeletedFalse(
                eAssetStatusCaptor.capture(),
                locationCaptor.capture(),
                pageableCaptor.capture())).thenReturn(assetPage);
        when(assetMapper.toListAssetsResponse(assetList)).thenReturn(assetResponseList);

        PaginationResponse<List<AssetResponse>> actual = getAssetServiceImpl
                .getAllAssetByAssetStatusWithPagination(status, page, limit, sortField, sortType);

        Pageable pageableActual = pageableCaptor.getValue();
        assertThat(pageableActual, is(pageableCreate));
        EAssetStatus assetStatusActual = eAssetStatusCaptor.getValue();
        assertThat(assetStatusActual, is(EAssetStatus.AVAILABLE));
        String locationActual = locationCaptor.getValue();
        assertThat(locationActual, is("location"));
        assertThat(actual.getData(), is(expected.getData()));
    }

    @Test
    void getAssetAndItsHistoriesByAssetId_WhenAssetIdNotExist_ShouldThrowNotFoundException() {
        when(assetRepository.findByIdAndIsDeletedFalse(1L)).thenReturn(Optional.empty());

        NotFoundException actual = assertThrows(NotFoundException.class, () -> {
            getAssetServiceImpl.getAssetAndItsHistoriesByAssetId(1L);
        });

        assertThat(actual.getMessage(), is("Don't exist asset with this assetId."));
    }

    @Test
    void getAssetAndItsHistoriesByAssetId_WhenAssetIdExist_ShouldReturnData() {
        Optional<Asset> assetOptional = Optional.of(asset);
        AssetAndHistoriesResponse expected = mock(AssetAndHistoriesResponse.class);

        when(assetRepository.findByIdAndIsDeletedFalse(1L)).thenReturn(assetOptional);
        when(assetMapper.toAssetAndHistoriesResponse(assetOptional.get())).thenReturn(expected);

        AssetAndHistoriesResponse actual = getAssetServiceImpl.getAssetAndItsHistoriesByAssetId(1L);

        assertThat(actual, is(expected));
    }

    @Test
    void checkAssetIsValidForDeleteOrNot_WhenAssetIdNotExist_ShouldThrowNotFoundException() {
        long assetId = 1L;

        when(assetRepository.findByIdAndIsDeletedFalse(assetId)).thenReturn(Optional.empty());

        NotFoundException actual = assertThrows(NotFoundException.class, () -> {
            getAssetServiceImpl.checkAssetIsValidForDeleteOrNot(assetId);
        });

        assertThat(actual.getMessage(), is("Don't exist asset with this assetId."));
    }

    @Test
    void checkAssetIsValidForDeleteOrNot_WhenAssetIdExistButNotValidForDelete_ShouldThrowBadRequestException() {
        long assetId = 1L;

        when(assetRepository.findByIdAndIsDeletedFalse(assetId)).thenReturn(Optional.of(asset));
        when(assignAssetRepository.existsByAssetIdAndStatusAndIsDeletedFalse(assetId, EAssignStatus.ACCEPTED))
                .thenReturn(true);

        BadRequestException actual = assertThrows(BadRequestException.class, () -> {
            getAssetServiceImpl.checkAssetIsValidForDeleteOrNot(assetId);
        });

        assertThat(actual.getMessage(), is("Asset already assigned. Invalid for delete."));
    }
}