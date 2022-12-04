package com.nashtech.assignment.services.validation.impl;

import com.nashtech.assignment.data.constants.EAssetStatus;
import com.nashtech.assignment.data.entities.Asset;
import com.nashtech.assignment.data.repositories.AssetRepository;
import com.nashtech.assignment.exceptions.BadRequestException;
import com.nashtech.assignment.exceptions.NotFoundException;
import com.nashtech.assignment.utils.CompareDateUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Date;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class ValidationAssetServiceImplTest {

    private ValidationAssetServiceImpl validationAssetServiceImpl;
    private AssetRepository assetRepository;
    private CompareDateUtil compareDateUtil;

    private Asset asset;
    private Date today;

    @BeforeEach
    void setup() {
        assetRepository = mock(AssetRepository.class);
        compareDateUtil = mock(CompareDateUtil.class);
        validationAssetServiceImpl = ValidationAssetServiceImpl.builder()
                .assetRepository(assetRepository)
                .compareDateUtil(compareDateUtil).build();
        asset = mock(Asset.class);
        today = new Date();
    }

    @Test
    void validationAssetAssignedToAssignment_WhenAssetNotExist_ThrowNotFoundException() {
        when(assetRepository.findByIdAndIsDeletedFalse(1L)).thenReturn(Optional.empty());

        NotFoundException actual = assertThrows(NotFoundException.class, () -> {
            validationAssetServiceImpl.validationAssetAssignedToAssignment(1L, today);
        });

        assertThat(actual.getMessage(), is("Not exist asset with this asset id."));
    }

    @Test
    void validationAssetAssignedToAssignment_WhenAssetExistButStatusIsNotTypeAvailable_ThrowBadRequestException() {
        Optional<Asset> assetOpt = Optional.of(asset);

        when(assetRepository.findByIdAndIsDeletedFalse(1L)).thenReturn(assetOpt);
        when(assetOpt.get().getStatus()).thenReturn(EAssetStatus.ASSIGNED);

        BadRequestException actual = assertThrows(BadRequestException.class, () -> {
            validationAssetServiceImpl.validationAssetAssignedToAssignment(1L, today);
        });

        assertThat(actual.getMessage(), is("Can only assign asset with status available."));
    }

    @Test
    void validationAssetAssignedToAssignment_WhenAssetExistButAssignedDateBeforeInstalledDate_ThrowBadRequestException() {
        Date assignedDate = new Date(today.getTime() - (1000 * 60 * 60 * 24));
        Optional<Asset> assetOpt = Optional.of(asset);

        when(assetRepository.findByIdAndIsDeletedFalse(1L)).thenReturn(assetOpt);
        when(assetOpt.get().getStatus()).thenReturn(EAssetStatus.AVAILABLE);
        when(assetOpt.get().getInstalledDate()).thenReturn(today);
        when(compareDateUtil.isBefore(assignedDate, assetOpt.get().getInstalledDate())).thenReturn(true);

        BadRequestException actual = assertThrows(BadRequestException.class, () -> {
            validationAssetServiceImpl.validationAssetAssignedToAssignment(1L, assignedDate);
        });

        assertThat(actual.getMessage(), is("Assigned date cannot before installed date of asset."));
    }

    @Test
    void validationAssetAssignedToAssignment_WhenAllDateValid_ShouldReturnData() {
        Date assignedDate = today;
        Optional<Asset> assetOpt = Optional.of(asset);

        when(assetRepository.findByIdAndIsDeletedFalse(1L)).thenReturn(assetOpt);
        when(assetOpt.get().getStatus()).thenReturn(EAssetStatus.AVAILABLE);
        when(assetOpt.get().getInstalledDate()).thenReturn(today);
        when(compareDateUtil.isBefore(assignedDate, assetOpt.get().getInstalledDate())).thenReturn(false);

        Asset actual = validationAssetServiceImpl.validationAssetAssignedToAssignment(1L, assignedDate);

        assertThat(actual, is(asset));
    }
}