package com.nashtech.assignment.services.edit.impl;

import com.nashtech.assignment.data.constants.EAssetStatus;
import com.nashtech.assignment.data.entities.Asset;
import com.nashtech.assignment.data.repositories.AssetRepository;
import com.nashtech.assignment.dto.request.asset.EditAssetInformationRequest;
import com.nashtech.assignment.dto.response.asset.AssetResponse;
import com.nashtech.assignment.exceptions.BadRequestException;
import com.nashtech.assignment.exceptions.NotFoundException;
import com.nashtech.assignment.mappers.AssetMapper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.*;

class EditAssetServiceImplTest {

    private EditAssetServiceImpl editAssetServiceImpl;
    private AssetRepository assetRepository;
    private AssetMapper assetMapper;
    private Asset asset;

    @BeforeEach
    void setUp() {
        assetRepository = mock(AssetRepository.class);
        assetMapper = mock(AssetMapper.class);
        editAssetServiceImpl = EditAssetServiceImpl.builder()
                .assetRepository(assetRepository)
                .assetMapper(assetMapper).build();
        asset = mock(Asset.class);
    }


    @Test
    void editAssetInformation_WhenFindAssetNull_ShouldReturnException() throws NotFoundException {
        long idAsset = 1L;
        EditAssetInformationRequest editAssetInformationRequest = EditAssetInformationRequest.builder().build();

        when(assetRepository.findById(idAsset)).thenReturn(Optional.empty());
        NotFoundException actualException = Assertions.assertThrows(NotFoundException.class,
                () -> editAssetServiceImpl.editAssetInformation(idAsset,
                        editAssetInformationRequest));

        Assertions.assertEquals("Asset not found", actualException.getMessage());
    }

    @Test
    void editAssetInformation_WhenAssetStatusAssigned_ShouldReturnException() {
        long idAsset = 1L;
        EditAssetInformationRequest editAssetInformationRequest = EditAssetInformationRequest.builder().build();

        Optional<Asset> assetOpt = Optional.of(asset);
        when(assetRepository.findById(idAsset)).thenReturn(assetOpt);
        when(assetOpt.get().getStatus())
                .thenReturn(EAssetStatus.ASSIGNED);

        BadRequestException actualException = Assertions.assertThrows(BadRequestException.class,
                () -> editAssetServiceImpl.editAssetInformation(idAsset,
                        editAssetInformationRequest));

        Assertions.assertEquals("Asset have state is assigned", actualException.getMessage());
    }

    @Test
    void editAssetInformation_WhenDataValid_ShouldReturnData() throws ParseException {
        long idAsset = 1L;
        EditAssetInformationRequest editAssetInformationRequest = EditAssetInformationRequest.builder()
                .assetName("assetName")
                .specification("assetSpecification")
                .assetStatus(EAssetStatus.AVAILABLE)
                .installedDate("01/01/2022")
                .build();

        AssetResponse expected = mock(AssetResponse.class);

        SimpleDateFormat sourceFormat = new SimpleDateFormat("dd/MM/yyyy");
        Date installedDate = sourceFormat.parse(editAssetInformationRequest.getInstalledDate());

        when(assetRepository.findById(idAsset)).thenReturn(Optional.of(asset));
        when(asset.getStatus())
                .thenReturn(EAssetStatus.NOT_AVAILABLE);
        when(assetMapper.mapEntityToEditAssetInformationResponse(asset)).thenReturn(expected);

        AssetResponse actual = editAssetServiceImpl.editAssetInformation(idAsset,
                editAssetInformationRequest);

        verify(asset).setName("assetName");
        verify(asset).setSpecification("assetSpecification");
        verify(asset).setStatus(EAssetStatus.AVAILABLE);
        verify(asset).setInstalledDate(installedDate);
        verify(assetRepository).save(asset);

        assertThat(actual, is(expected));
    }


}