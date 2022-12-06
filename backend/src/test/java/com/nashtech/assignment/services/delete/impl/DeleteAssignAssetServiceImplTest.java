package com.nashtech.assignment.services.delete.impl;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.nashtech.assignment.data.constants.EAssetStatus;
import com.nashtech.assignment.data.constants.EAssignStatus;
import com.nashtech.assignment.data.entities.Asset;
import com.nashtech.assignment.data.entities.AssignAsset;
import com.nashtech.assignment.data.repositories.AssetRepository;
import com.nashtech.assignment.data.repositories.AssignAssetRepository;

public class DeleteAssignAssetServiceImplTest {

    private DeleteAssignAssetServiceImpl deleteAsignAssetServiceImpl;
    private AssignAssetRepository assignAssetRepository;
    private AssetRepository assetRepository;

    @BeforeEach
    void setUpTest() {
        assignAssetRepository = mock(AssignAssetRepository.class);
        assetRepository = mock(AssetRepository.class);
        deleteAsignAssetServiceImpl = DeleteAssignAssetServiceImpl.builder()
                .assignAssetRepository(assignAssetRepository)
                .assetRepository(assetRepository)
                .build();
    }

    @Test
    void deleteAssignAsset_WhenDataValid_ShouldReturnVoid() {

        AssignAsset assignAsset = mock(AssignAsset.class);
        Asset asset = mock(Asset.class);
        Optional<AssignAsset> assignAssetOtp = Optional.of(assignAsset);

        when(assignAssetRepository.findById(1l)).thenReturn(assignAssetOtp);
        when(assignAsset.getStatus()).thenReturn(EAssignStatus.DECLINED);
        when(assignAsset.getAsset()).thenReturn(asset);

        deleteAsignAssetServiceImpl.deleteAssignAsset(1l);

        verify(asset).setStatus(EAssetStatus.AVAILABLE);
        verify(assignAsset).setDeleted(true);
        verify(assetRepository).save(asset);
        verify(assignAssetRepository).save(assignAsset);

    }
}
