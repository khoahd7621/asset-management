package com.nashtech.assignment.services.edit.impl;

import com.nashtech.assignment.data.constants.EAssetStatus;
import com.nashtech.assignment.data.constants.EAssignStatus;
import com.nashtech.assignment.data.entities.Asset;
import com.nashtech.assignment.data.entities.AssignAsset;
import com.nashtech.assignment.data.entities.User;
import com.nashtech.assignment.data.repositories.AssetRepository;
import com.nashtech.assignment.data.repositories.AssignAssetRepository;
import com.nashtech.assignment.dto.request.assignment.EditAssignmentRequest;
import com.nashtech.assignment.dto.response.assignment.AssignAssetResponse;
import com.nashtech.assignment.exceptions.BadRequestException;
import com.nashtech.assignment.exceptions.NotFoundException;
import com.nashtech.assignment.mappers.AssignAssetMapper;
import com.nashtech.assignment.services.validation.ValidationAssetService;
import com.nashtech.assignment.services.validation.ValidationUserService;
import com.nashtech.assignment.utils.CompareDateUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.util.Date;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

class EditAssignAssetServiceImplTest {

    private EditAssignAssetServiceImpl editAssignAssetServiceImpl;
    private AssignAssetRepository assignAssetRepository;
    private AssetRepository assetRepository;
    private AssignAssetMapper assignAssetMapper;
    private ValidationAssetService validationAssetService;
    private ValidationUserService validationUserService;
    private CompareDateUtil compareDateUtil;

    private AssignAsset assignAsset;
    private AssignAssetResponse assignAssetResponse;
    private Date today;

    @BeforeEach
    void setup() {
        assignAssetRepository = mock(AssignAssetRepository.class);
        assetRepository = mock(AssetRepository.class);
        assignAssetMapper = mock(AssignAssetMapper.class);
        validationAssetService = mock(ValidationAssetService.class);
        validationUserService = mock(ValidationUserService.class);
        compareDateUtil = mock(CompareDateUtil.class);
        editAssignAssetServiceImpl = EditAssignAssetServiceImpl.builder()
                .assignAssetRepository(assignAssetRepository)
                .assetRepository(assetRepository)
                .assignAssetMapper(assignAssetMapper)
                .validationAssetService(validationAssetService)
                .validationUserService(validationUserService)
                .compareDateUtil(compareDateUtil).build();
        assignAsset = mock(AssignAsset.class);
        assignAssetResponse = mock(AssignAssetResponse.class);
        today = new Date();
    }

    @Test
    void editAssignment_WhenNotExistAssignment_ThrowNotFoundException() {
        long assignmentId = 1L;
        EditAssignmentRequest editAssignmentRequest = EditAssignmentRequest.builder().build();

        when(assignAssetRepository.findByIdAndIsDeletedFalse(assignmentId)).thenReturn(Optional.empty());

        NotFoundException actual = assertThrows(NotFoundException.class, () -> {
            editAssignAssetServiceImpl.editAssignment(assignmentId, editAssignmentRequest);
        });

        assertThat(actual.getMessage(), is("Not exist assignment with this assignment id."));
    }

    @Test
    void editAssignment_WhenExistAssignmentButStatusIsNotWaitingForAcceptance_ThrowBadRequestException() {
        long assignmentId = 1L;
        EditAssignmentRequest editAssignmentRequest = EditAssignmentRequest.builder().build();

        when(assignAssetRepository.findByIdAndIsDeletedFalse(assignmentId)).thenReturn(Optional.of(assignAsset));
        when(assignAsset.getStatus()).thenReturn(EAssignStatus.ACCEPTED);

        BadRequestException actual = assertThrows(BadRequestException.class, () -> {
            editAssignAssetServiceImpl.editAssignment(assignmentId, editAssignmentRequest);
        });

        assertThat(actual.getMessage(), is("Can only edit assignment with status waiting for acceptance."));
    }

    @Test
    void editAssignment_WhenChangeAssignDateButItBeforeToday_ThrowBadRequestException() {
        long assignmentId = 1L;
        Date oldAssignedDate = new Date(today.getTime() + (1000 * 60 * 60 * 48));
        Date newAssignedDate = new Date(today.getTime() + (1000 * 60 * 60 * 24));
        EditAssignmentRequest editAssignmentRequest = EditAssignmentRequest.builder()
                .assignedDate(newAssignedDate).build();
        ArgumentCaptor<Date> todayCaptor = ArgumentCaptor.forClass(Date.class);
        ArgumentCaptor<Date> oldAssignedDateCaptor = ArgumentCaptor.forClass(Date.class);
        ArgumentCaptor<Date> newAssignedDateCaptor = ArgumentCaptor.forClass(Date.class);

        when(assignAssetRepository.findByIdAndIsDeletedFalse(assignmentId)).thenReturn(Optional.of(assignAsset));
        when(assignAsset.getStatus()).thenReturn(EAssignStatus.WAITING_FOR_ACCEPTANCE);
        when(assignAsset.getAssignedDate()).thenReturn(oldAssignedDate);
        when(compareDateUtil.isEquals(oldAssignedDateCaptor.capture(), newAssignedDateCaptor.capture()))
                .thenReturn(false);
        when(compareDateUtil.isAfter(todayCaptor.capture(), newAssignedDateCaptor.capture())).thenReturn(true);

        BadRequestException actual = assertThrows(BadRequestException.class, () -> {
            editAssignAssetServiceImpl.editAssignment(assignmentId, editAssignmentRequest);
        });

        assertThat(todayCaptor.getValue(), is(new Date()));
        assertThat(oldAssignedDateCaptor.getValue(), is(oldAssignedDate));
        assertThat(newAssignedDateCaptor.getValue(), is(newAssignedDate));
        assertThat(actual.getMessage(), is("Assign date is before today."));
    }

    @Test
    void editAssignment_WhenNotChangeAssetButAssignedDateBeforeInstalledDateOfOldAsset_ThrowBadRequestException() {
        long assignmentId = 1L;
        EditAssignmentRequest editAssignmentRequest = EditAssignmentRequest.builder()
                .assignedDate(today)
                .assetId(1L).build();
        Date installedDate = new Date(today.getTime() + (1000 * 60 * 60 * 48));
        ArgumentCaptor<Date> oldAssignedDateCaptor = ArgumentCaptor.forClass(Date.class);
        ArgumentCaptor<Date> newAssignedDateCaptor = ArgumentCaptor.forClass(Date.class);
        ArgumentCaptor<Date> installedDateCaptor = ArgumentCaptor.forClass(Date.class);
        Asset asset = mock(Asset.class);

        when(assignAssetRepository.findByIdAndIsDeletedFalse(assignmentId)).thenReturn(Optional.of(assignAsset));
        when(assignAsset.getStatus()).thenReturn(EAssignStatus.WAITING_FOR_ACCEPTANCE);
        when(assignAsset.getAssignedDate()).thenReturn(today);
        when(compareDateUtil.isEquals(oldAssignedDateCaptor.capture(), newAssignedDateCaptor.capture()))
                .thenReturn(true);
        when(assignAsset.getAsset()).thenReturn(asset);
        when(asset.getId()).thenReturn(1L);
        when(asset.getInstalledDate()).thenReturn(installedDate);
        when(compareDateUtil.isBefore(newAssignedDateCaptor.capture(), installedDateCaptor.capture()))
                .thenReturn(true);

        BadRequestException actual = assertThrows(BadRequestException.class, () -> {
            editAssignAssetServiceImpl.editAssignment(assignmentId, editAssignmentRequest);
        });

        assertThat(oldAssignedDateCaptor.getValue(), is(today));
        assertThat(newAssignedDateCaptor.getValue(), is(today));
        assertThat(installedDateCaptor.getValue(), is(installedDate));
        assertThat(actual.getMessage(), is("Assigned date cannot before installed date of asset."));
    }

    @Test
    void editAssignment_WhenNotChangeUserButAssignedDateIsBeforeJoinedDate_ThrowBadRequestException() {
        long assignmentId = 1L;
        EditAssignmentRequest editAssignmentRequest = EditAssignmentRequest.builder()
                .assignedDate(today)
                .assetId(1L)
                .userId(1L).build();
        Date joinedDate = new Date(today.getTime() + (1000 * 60 * 60 * 24));
        ArgumentCaptor<Date> oldAssignedDateCaptor = ArgumentCaptor.forClass(Date.class);
        ArgumentCaptor<Date> newAssignedDateCaptor = ArgumentCaptor.forClass(Date.class);
        ArgumentCaptor<Long> newAssetId = ArgumentCaptor.forClass(Long.class);
        ArgumentCaptor<Date> joinedDateCaptor = ArgumentCaptor.forClass(Date.class);
        Asset asset = mock(Asset.class);
        User user = mock(User.class);

        when(assignAssetRepository.findByIdAndIsDeletedFalse(assignmentId)).thenReturn(Optional.of(assignAsset));
        when(assignAsset.getStatus()).thenReturn(EAssignStatus.WAITING_FOR_ACCEPTANCE);
        when(assignAsset.getAssignedDate()).thenReturn(today);
        when(compareDateUtil.isEquals(oldAssignedDateCaptor.capture(), newAssignedDateCaptor.capture()))
                .thenReturn(true);
        when(assignAsset.getAsset()).thenReturn(asset);
        when(asset.getId()).thenReturn(2L);
        when(validationAssetService.validationAssetAssignedToAssignment(
                newAssetId.capture(), newAssignedDateCaptor.capture())).thenReturn(asset);
        when(assignAsset.getUserAssignedTo()).thenReturn(user);
        when(user.getId()).thenReturn(1L);
        when(user.getJoinedDate()).thenReturn(joinedDate);
        when(compareDateUtil.isBefore(newAssignedDateCaptor.capture(), joinedDateCaptor.capture()))
                .thenReturn(true);

        BadRequestException actual = assertThrows(BadRequestException.class, () -> {
            editAssignAssetServiceImpl.editAssignment(assignmentId, editAssignmentRequest);
        });

        assertThat(oldAssignedDateCaptor.getValue(), is(today));
        assertThat(newAssignedDateCaptor.getValue(), is(today));
        assertThat(joinedDateCaptor.getValue(), is(joinedDate));
        assertThat(actual.getMessage(), is("Assigned date cannot before joined date of user."));
    }

    @Test
    void editAssignment_WhenAllDataValid_ShouldReturnData() {
        long assignmentId = 1L;
        EditAssignmentRequest editAssignmentRequest = EditAssignmentRequest.builder()
                .assignedDate(today)
                .assetId(1L)
                .userId(1L)
                .note("note").build();
        ArgumentCaptor<Date> newAssignedDateCaptor = ArgumentCaptor.forClass(Date.class);
        ArgumentCaptor<Long> newAssetIdCaptor = ArgumentCaptor.forClass(Long.class);
        ArgumentCaptor<Long> newUserIdCaptor = ArgumentCaptor.forClass(Long.class);
        Asset asset = mock(Asset.class);
        User user = mock(User.class);

        when(assignAssetRepository.findByIdAndIsDeletedFalse(assignmentId)).thenReturn(Optional.of(assignAsset));
        when(assignAsset.getStatus()).thenReturn(EAssignStatus.WAITING_FOR_ACCEPTANCE);
        when(assignAsset.getAssignedDate()).thenReturn(today);
        when(assignAsset.getAsset()).thenReturn(asset);
        when(asset.getId()).thenReturn(2L);
        when(validationAssetService.validationAssetAssignedToAssignment(
                newAssetIdCaptor.capture(), newAssignedDateCaptor.capture())).thenReturn(asset);
        when(assignAsset.getUserAssignedTo()).thenReturn(user);
        when(user.getId()).thenReturn(2L);
        when(validationUserService.validationUserAssignedToAssignment(
                newUserIdCaptor.capture(), newAssignedDateCaptor.capture())).thenReturn(user);
        when(assignAssetRepository.save(assignAsset)).thenReturn(assignAsset);
        when(assignAssetMapper.toAssignAssetResponse(assignAsset)).thenReturn(assignAssetResponse);

        AssignAssetResponse actual = editAssignAssetServiceImpl.editAssignment(assignmentId, editAssignmentRequest);

        assertThat(newAssignedDateCaptor.getValue(), is(today));
        assertThat(newAssetIdCaptor.getValue(), is(1L));
        assertThat(newUserIdCaptor.getValue(), is(1L));
        verify(asset).setStatus(EAssetStatus.ASSIGNED);
        verify(asset).setStatus(EAssetStatus.AVAILABLE);
        verify(assetRepository).save(asset);
        verify(assignAsset).setAsset(asset);
        verify(assignAsset).setUserAssignedTo(user);
        verify(assignAsset).setAssignedDate(today);
        verify(assignAsset).setNote("note");
        verify(assignAssetRepository).save(assignAsset);
        assertThat(actual, is(assignAssetResponse));
    }
}