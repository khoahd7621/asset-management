package com.nashtech.assignment.services.delete.impl;

import com.nashtech.assignment.data.constants.EAssignStatus;
import com.nashtech.assignment.data.entities.Asset;
import com.nashtech.assignment.data.repositories.AssetRepository;
import com.nashtech.assignment.data.repositories.AssignAssetRepository;
import com.nashtech.assignment.exceptions.BadRequestException;
import com.nashtech.assignment.exceptions.NotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

class DeleteAssetServiceImplTest {

    private DeleteAssetServiceImpl deleteAssetServiceImpl;
    private AssignAssetRepository assignAssetRepository;
    private AssetRepository assetRepository;
    private Asset asset;

    @BeforeEach
    void setUpTest() {
        assignAssetRepository = mock(AssignAssetRepository.class);
        assetRepository = mock(AssetRepository.class);
        deleteAssetServiceImpl = DeleteAssetServiceImpl.builder()
                .assignAssetRepository(assignAssetRepository)
                .assetRepository(assetRepository).build();
        asset = mock(Asset.class);
    }


    @Test
    void deleteAssetByAssetId_WhenAssetIdNotExist_ShouldThrowNotFoundException() {
        long assetId = 1L;

        when(assetRepository.findByIdAndIsDeletedFalse(assetId)).thenReturn(Optional.empty());

        NotFoundException actual = assertThrows(NotFoundException.class,
                () -> deleteAssetServiceImpl.deleteAssetByAssetId(assetId));

        assertThat(actual.getMessage(), is("Don't exist asset with this assetId."));
    }

    @Test
    void deleteAssetByAssetId_WhenAssetIdExistButNotValidForDelete_ShouldThrowBadRequestException() {
        long assetId = 1L;

        when(assetRepository.findByIdAndIsDeletedFalse(assetId)).thenReturn(Optional.of(asset));
        when(assignAssetRepository.existsByAssetIdAndStatusAndIsDeletedFalse(assetId, EAssignStatus.ACCEPTED))
                .thenReturn(true);

        BadRequestException actual = assertThrows(BadRequestException.class,
                () -> deleteAssetServiceImpl.deleteAssetByAssetId(assetId));

        assertThat(actual.getMessage(), is("Asset already assigned. Invalid for delete."));
    }

    @Test
    void deleteAssetByAssetId_WhenAssetIdExistAndValidForDelete_ShouldDeleteSuccess() {
        long assetId = 1L;

        when(assetRepository.findByIdAndIsDeletedFalse(assetId)).thenReturn(Optional.of(asset));
        when(assignAssetRepository.existsByAssetIdAndStatusAndIsDeletedFalse(assetId, EAssignStatus.ACCEPTED))
                .thenReturn(false);

        deleteAssetServiceImpl.deleteAssetByAssetId(assetId);

        verify(asset).setDeleted(true);
        verify(assetRepository).save(asset);
    }
}