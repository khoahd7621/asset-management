package com.nashtech.assignment.services.returned.impl;

import com.nashtech.assignment.data.constants.EAssetStatus;
import com.nashtech.assignment.data.constants.EAssignStatus;
import com.nashtech.assignment.data.constants.EReturnStatus;
import com.nashtech.assignment.data.constants.EUserType;
import com.nashtech.assignment.data.entities.Asset;
import com.nashtech.assignment.data.entities.AssignAsset;
import com.nashtech.assignment.data.entities.ReturnAsset;
import com.nashtech.assignment.data.entities.User;
import com.nashtech.assignment.data.repositories.AssetRepository;
import com.nashtech.assignment.data.repositories.AssignAssetRepository;
import com.nashtech.assignment.data.repositories.ReturnAssetRepository;
import com.nashtech.assignment.dto.response.returned.ReturnAssetResponse;
import com.nashtech.assignment.exceptions.BadRequestException;
import com.nashtech.assignment.exceptions.ForbiddenException;
import com.nashtech.assignment.exceptions.NotFoundException;
import com.nashtech.assignment.mappers.ReturnAssetMapper;
import com.nashtech.assignment.services.auth.SecurityContextService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.util.Date;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

class ReturnedServiceImplTest {

    private ReturnedServiceImpl returnedServiceImpl;
    private ReturnAssetRepository returnAssetRepository;
    private AssignAssetRepository assignAssetRepository;
    private AssetRepository assetRepository;
    private SecurityContextService securityContextService;
    private ReturnAssetMapper returnAssetMapper;

    private ReturnAsset returnAsset;
    private AssignAsset assignAsset;
    private Asset asset;

    @BeforeEach
    void setUpTest() {
        returnAssetRepository = mock(ReturnAssetRepository.class);
        assignAssetRepository = mock(AssignAssetRepository.class);
        assetRepository = mock(AssetRepository.class);
        securityContextService = mock(SecurityContextService.class);
        returnAssetMapper = mock(ReturnAssetMapper.class);
        returnedServiceImpl = ReturnedServiceImpl.builder()
                .assignAssetRepository(assignAssetRepository)
                .securityContextService(securityContextService)
                .assetRepository(assetRepository)
                .returnAssetRepository(returnAssetRepository)
                .returnAssetMapper(returnAssetMapper).build();
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

        returnedServiceImpl.completeReturnRequest(1L);

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
                returnedServiceImpl.completeReturnRequest(1L));

        assertThat(actual.getMessage(), is("Cannot found return asset with id 1"));
    }

    @Test
    void completeReturnRequest_WhenStatusNotWaitingForReturn_ShouldThrowBadRequestException() {
        when(returnAssetRepository.findById(1L)).thenReturn(Optional.of(returnAsset));
        when(returnAsset.getStatus()).thenReturn(EReturnStatus.COMPLETED);

        BadRequestException actual = assertThrows(BadRequestException.class, () ->
                returnedServiceImpl.completeReturnRequest(1L));

        assertThat(actual.getMessage(), is("Cannot completed this request"));
    }

    @Test
    void deleteReturnAsset_WhenDataValid_ShouldReturnVoid() {
        when(returnAssetRepository.findById(1L)).thenReturn(Optional.of(returnAsset));
        when(returnAsset.getStatus()).thenReturn(EReturnStatus.WAITING_FOR_RETURNING);

        returnedServiceImpl.deleteReturnAsset(1L);

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
                returnedServiceImpl.deleteReturnAsset(1L));

        assertThat(actual.getMessage(), is("Cannot find return asset with id 1"));
    }

    @Test
    void deleteReturnAsset_WhenReturnStatusNotValid_ShouldThrowBadRequestException() {
        when(returnAssetRepository.findById(1L)).thenReturn(Optional.of(returnAsset));
        when(returnAsset.getStatus()).thenReturn(EReturnStatus.COMPLETED);

        BadRequestException actual = assertThrows(BadRequestException.class, () ->
                returnedServiceImpl.deleteReturnAsset(1L));

        assertThat(actual.getMessage(), is("Cannot cancel this return asset"));
    }

    @Test
    void createReturnAsset_WhenAssignNotFound_ShouldReturnNotFoundException() {
        when(assignAssetRepository.findById(1L)).thenReturn(Optional.empty());

        NotFoundException actual = assertThrows(NotFoundException.class, () ->
                returnedServiceImpl.createReturnAsset(1L));

        assertThat(actual.getMessage(), is("Assignment not found"));
    }

    @Test
    void createReturnAsset_WhenAssignNotFound_ShouldReturnForbiddenException() {
        User userAssignedTo = User.builder().id(1L).build();
        User userCurrent = User.builder().id(2L).type(EUserType.STAFF).build();

        when(assignAssetRepository.findById(1L)).thenReturn(Optional.of(assignAsset));
        when(securityContextService.getCurrentUser()).thenReturn(userCurrent);
        when(assignAsset.getUserAssignedTo()).thenReturn(userAssignedTo);

        ForbiddenException actual = assertThrows(ForbiddenException.class, () ->
                returnedServiceImpl.createReturnAsset(1L));

        assertThat(actual.getMessage(), is("Current user is not match to this assignment or current user is not admin."));
    }

    @Test
    void createReturnAsset_WhenAssignNotFound_ShouldReturnBadRequestException() {
        User userAssignedTo = User.builder().id(1L).build();
        User userCurrent = User.builder().id(2L).type(EUserType.ADMIN).build();

        when(assignAssetRepository.findById(1L)).thenReturn(Optional.of(assignAsset));
        when(securityContextService.getCurrentUser()).thenReturn(userCurrent);
        when(assignAsset.getUserAssignedTo()).thenReturn(userAssignedTo);
        when(assignAsset.getStatus()).thenReturn(EAssignStatus.WAITING_FOR_ACCEPTANCE);

        BadRequestException actual = assertThrows(BadRequestException.class, () ->
                returnedServiceImpl.createReturnAsset(1L));

        assertThat(actual.getMessage(), is("Assignment is not accepted or Assignment already exist in return list"));
    }

    @Test
    void createReturnAsset_WhenDataValid_ShouldReturnData() {
        ReturnAssetResponse expected = mock(ReturnAssetResponse.class);
        ArgumentCaptor<ReturnAsset> returnAssetCaptor = ArgumentCaptor.forClass(ReturnAsset.class);
        User userAssignedTo = User.builder().id(1L).build();
        User userCurrent = User.builder().id(1L).type(EUserType.STAFF).build();

        when(assignAssetRepository.findById(1L)).thenReturn(Optional.of(assignAsset));
        when(securityContextService.getCurrentUser()).thenReturn(userCurrent);
        when(assignAsset.getUserAssignedTo()).thenReturn(userAssignedTo);
        when(assignAsset.getStatus()).thenReturn(EAssignStatus.ACCEPTED);
        when(returnAssetRepository.findByAssignAssetId(1L)).thenReturn(Optional.empty());
        when(returnAssetRepository.save(returnAssetCaptor.capture())).thenReturn(returnAsset);
        when(returnAssetMapper.toReturnAssetResponse(returnAsset)).thenReturn(expected);

        ReturnAssetResponse actual = returnedServiceImpl.createReturnAsset(1L);

        assertThat(actual, is(expected));
    }

    @Test
    void createReturnAsset_WhenUserIsAdmin_ShouldReturnData() {
        ReturnAssetResponse expected = mock(ReturnAssetResponse.class);
        ArgumentCaptor<ReturnAsset> returnAssetCaptor = ArgumentCaptor.forClass(ReturnAsset.class);
        User userAssignedTo = User.builder().id(1L).build();
        User userCurrent = User.builder().id(1L).type(EUserType.ADMIN).build();

        when(assignAssetRepository.findById(1L)).thenReturn(Optional.of(assignAsset));
        when(securityContextService.getCurrentUser()).thenReturn(userCurrent);
        when(assignAsset.getUserAssignedTo()).thenReturn(userAssignedTo);
        when(assignAsset.getStatus()).thenReturn(EAssignStatus.ACCEPTED);
        when(returnAssetRepository.findByAssignAssetId(1L)).thenReturn(Optional.empty());
        when(returnAssetRepository.save(returnAssetCaptor.capture())).thenReturn(returnAsset);
        when(returnAssetMapper.toReturnAssetResponse(returnAsset)).thenReturn(expected);

        ReturnAssetResponse actual = returnedServiceImpl.createReturnAsset(1L);

        assertThat(actual, is(expected));
    }
}