package com.nashtech.assignment.services.delete.impl;

import com.nashtech.assignment.data.constants.EReturnStatus;
import com.nashtech.assignment.data.entities.ReturnAsset;
import com.nashtech.assignment.data.repositories.ReturnAssetRepository;
import com.nashtech.assignment.exceptions.BadRequestException;
import com.nashtech.assignment.exceptions.NotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

public class DeleteReturnAssetServiceImplTest {

    private DeleteReturnAssetServiceImpl deleteReturnAssetServiceImpl;
    private ReturnAssetRepository returnAssetRepository;
    private ReturnAsset returnAsset;

    @BeforeEach
    void setUpTest() {
        returnAssetRepository = mock(ReturnAssetRepository.class);
        deleteReturnAssetServiceImpl = DeleteReturnAssetServiceImpl.builder()
                .returnAssetRepository(returnAssetRepository)
                .build();
        returnAsset = mock(ReturnAsset.class);
    }

    @Test
    void deleteReturnAsset_WhenDataValid_ShouldReturnVoid() {
        when(returnAssetRepository.findById(1L)).thenReturn(Optional.of(returnAsset));
        when(returnAsset.getStatus()).thenReturn(EReturnStatus.WAITING_FOR_RETURNING);

        deleteReturnAssetServiceImpl.deleteReturnAsset(1L);

        verify(returnAsset).setAsset(null);
        verify(returnAsset).setAssignAsset(null);
        verify(returnAsset).setUserAcceptedReturn(null);
        verify(returnAsset).setUserRequestedReturn(null);
        verify(returnAssetRepository).save(returnAsset);
        verify(returnAssetRepository).delete(returnAsset);
    }

    @Test
    void deleteReturnAsset_WhenReturnIdNotExist_ShouldThrowNotFoundException() {
        when(returnAssetRepository.findById(1L)).thenReturn(Optional.empty());

        NotFoundException actual = assertThrows(NotFoundException.class, () ->
                deleteReturnAssetServiceImpl.deleteReturnAsset(1L));

        assertThat(actual.getMessage(), is("Cannot find return asset with id 1"));
    }

    @Test
    void deleteReturnAsset_WhenReturnStatusNotValid_ShouldThrowBadRequestException() {
        when(returnAssetRepository.findById(1L)).thenReturn(Optional.of(returnAsset));
        when(returnAsset.getStatus()).thenReturn(EReturnStatus.COMPLETED);

        BadRequestException actual = assertThrows(BadRequestException.class, () ->
                deleteReturnAssetServiceImpl.deleteReturnAsset(1L));

        assertThat(actual.getMessage(), is("Cannot cancel this return asset"));
    }
}
