package com.nashtech.assignment.services.edit.impl;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Date;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import com.nashtech.assignment.data.constants.EAssetStatus;
import com.nashtech.assignment.data.constants.EReturnStatus;
import com.nashtech.assignment.data.entities.Asset;
import com.nashtech.assignment.data.entities.AssignAsset;
import com.nashtech.assignment.data.entities.ReturnAsset;
import com.nashtech.assignment.data.entities.User;
import com.nashtech.assignment.data.repositories.AssetRepository;
import com.nashtech.assignment.data.repositories.AssignAssetRepository;
import com.nashtech.assignment.data.repositories.ReturnAssetRepository;
import com.nashtech.assignment.exceptions.BadRequestException;
import com.nashtech.assignment.exceptions.NotFoundException;
import com.nashtech.assignment.services.auth.SecurityContextService;

public class EditReturnAssetServiceImplTest {

    private EditReturnAssetServiceImpl editReturnAssetServiceImpl;
    private ReturnAssetRepository returnAssetRepository;
    private AssignAssetRepository assignAssetRepository;
    private AssetRepository assetRepository;
    private SecurityContextService securityContextService;

    private ReturnAsset returnAsset;
    private AssignAsset assignAsset;
    private Asset asset;

    @BeforeEach
    void setUpTest() {
        returnAssetRepository = mock(ReturnAssetRepository.class);
        assignAssetRepository = mock(AssignAssetRepository.class);
        assetRepository = mock(AssetRepository.class);
        securityContextService = mock(SecurityContextService.class);
        editReturnAssetServiceImpl = EditReturnAssetServiceImpl.builder()
                .assignAssetRepository(assignAssetRepository)
                .securityContext(securityContextService)
                .assetRepository(assetRepository)
                .returnAssetRepository(returnAssetRepository).build();
        returnAsset = mock(ReturnAsset.class);
        assignAsset = mock(AssignAsset.class);
        asset = mock(Asset.class);
    }

    @Test
    void completeReturnRequest_WhenDataValid_ShouldReturnVoid() {
        ArgumentCaptor<Date> today = ArgumentCaptor.forClass(Date.class);
        User user = mock(User.class);

        when(returnAssetRepository.findById(1L)).thenReturn(Optional.of(returnAsset));
        when(returnAsset.getStatus()).thenReturn(EReturnStatus.WAITING_FOR_RETURNING);
        when(securityContextService.getCurrentUser()).thenReturn(user);
        when(returnAsset.getAssignAsset()).thenReturn(assignAsset);
        when(returnAsset.getAsset()).thenReturn(asset);

        editReturnAssetServiceImpl.completeReturnRequest(1L);

        verify(returnAsset).setStatus(EReturnStatus.COMPLETED);
        verify(returnAsset).setReturnedDate(today.capture());
        verify(returnAsset).setUserAcceptedReturn(user);
        verify(asset).setStatus(EAssetStatus.AVAILABLE);
        verify(assignAsset).setDeleted(true);
        verify(assetRepository).save(asset);
        verify(assignAssetRepository).save(assignAsset);
        verify(returnAssetRepository).save(returnAsset);
    }

    @Test
    void completeReturnRequest_WhenReturnAssetNotExist_ShouldThrowNotFoundException() {
        when(returnAssetRepository.findById(1L)).thenReturn(Optional.empty());

        NotFoundException actual = assertThrows(NotFoundException.class, () ->
                editReturnAssetServiceImpl.completeReturnRequest(1L));

        assertThat(actual.getMessage(), is("Cannot found return asset with id 1"));
    }

    @Test
    void completeReturnRequest_WhenStatusNotWaitingForReturn_ShouldThrowBadRequestException() {
        when(returnAssetRepository.findById(1L)).thenReturn(Optional.of(returnAsset));
        when(returnAsset.getStatus()).thenReturn(EReturnStatus.COMPLETED);

        BadRequestException actual = assertThrows(BadRequestException.class, () ->
                editReturnAssetServiceImpl.completeReturnRequest(1L));

        assertThat(actual.getMessage(), is("Cannot completed this request"));
    }
}
