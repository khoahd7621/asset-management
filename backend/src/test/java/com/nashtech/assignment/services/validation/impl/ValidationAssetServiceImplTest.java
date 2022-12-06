package com.nashtech.assignment.services.validation.impl;

import com.nashtech.assignment.data.constants.EAssetStatus;
import com.nashtech.assignment.data.entities.Asset;
import com.nashtech.assignment.data.repositories.AssetRepository;
import com.nashtech.assignment.exceptions.BadRequestException;
import com.nashtech.assignment.exceptions.NotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class ValidationAssetServiceImplTest {

    private ValidationAssetServiceImpl validationAssetServiceImpl;
    private AssetRepository assetRepository;

    private Asset asset;

    @BeforeEach
    void setup() {
        assetRepository = mock(AssetRepository.class);
        validationAssetServiceImpl = ValidationAssetServiceImpl.builder()
                .assetRepository(assetRepository).build();
        asset = mock(Asset.class);
    }

    @Test
    void validationAssetAssignedToAssignment_WhenAssetNotExist_ThrowNotFoundException() {
        when(assetRepository.findByIdAndIsDeletedFalse(1L)).thenReturn(Optional.empty());

        NotFoundException actual = assertThrows(NotFoundException.class, () -> {
            validationAssetServiceImpl.validationAssetAssignedToAssignment(1L);
        });

        assertThat(actual.getMessage(), is("Not exist asset with this asset id."));
    }

    @Test
    void validationAssetAssignedToAssignment_WhenAssetExistButStatusIsNotTypeAvailable_ThrowBadRequestException() {
        Optional<Asset> assetOpt = Optional.of(asset);

        when(assetRepository.findByIdAndIsDeletedFalse(1L)).thenReturn(assetOpt);
        when(assetOpt.get().getStatus()).thenReturn(EAssetStatus.ASSIGNED);

        BadRequestException actual = assertThrows(BadRequestException.class, () -> {
            validationAssetServiceImpl.validationAssetAssignedToAssignment(1L);
        });

        assertThat(actual.getMessage(), is("Can only assign asset with status available."));
    }

    @Test
    void validationAssetAssignedToAssignment_WhenAllDataValid_ShouldReturnData() {
        Optional<Asset> assetOpt = Optional.of(asset);

        when(assetRepository.findByIdAndIsDeletedFalse(1L)).thenReturn(assetOpt);
        when(assetOpt.get().getStatus()).thenReturn(EAssetStatus.AVAILABLE);

        Asset actual = validationAssetServiceImpl.validationAssetAssignedToAssignment(1L);

        assertThat(actual, is(asset));
    }
}