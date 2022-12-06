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
import com.nashtech.assignment.exceptions.ForbiddenException;
import com.nashtech.assignment.exceptions.NotFoundException;
import com.nashtech.assignment.mappers.AssignAssetMapper;
import com.nashtech.assignment.services.auth.SecurityContextService;
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
    private SecurityContextService securityContextService;
    private AssignAsset assignAsset;
    private AssignAssetResponse assignAssetResponse;
    private Date today;

    @BeforeEach
    void setup() {
        securityContextService = mock(SecurityContextService.class);
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
                .compareDateUtil(compareDateUtil)
                .securityContextService(securityContextService).build();
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

        assertThat(oldAssignedDateCaptor.getValue().equals(oldAssignedDate), is(true));
        assertThat(newAssignedDateCaptor.getValue().equals(newAssignedDate), is(true));
        assertThat(actual.getMessage(), is("Assign date is before today."));
    }

    @Test
    void editAssignment_WhenAllDataValid_ShouldReturnData() {
        long assignmentId = 1L;
        EditAssignmentRequest editAssignmentRequest = EditAssignmentRequest.builder()
                .assignedDate(today)
                .assetId(1L)
                .userId(1L)
                .note("note").build();
        ArgumentCaptor<Long> newAssetIdCaptor = ArgumentCaptor.forClass(Long.class);
        ArgumentCaptor<Long> newUserIdCaptor = ArgumentCaptor.forClass(Long.class);
        Asset asset = mock(Asset.class);
        User user = mock(User.class);

        when(assignAssetRepository.findByIdAndIsDeletedFalse(assignmentId)).thenReturn(Optional.of(assignAsset));
        when(assignAsset.getStatus()).thenReturn(EAssignStatus.WAITING_FOR_ACCEPTANCE);
        when(assignAsset.getAssignedDate()).thenReturn(today);
        when(assignAsset.getUserAssignedTo()).thenReturn(user);
        when(user.getId()).thenReturn(2L);
        when(validationUserService.validationUserAssignedToAssignment(newUserIdCaptor.capture())).thenReturn(user);
        when(assignAsset.getAsset()).thenReturn(asset);
        when(asset.getId()).thenReturn(2L);
        when(validationAssetService.validationAssetAssignedToAssignment(newAssetIdCaptor.capture())).thenReturn(asset);
        when(assignAssetRepository.save(assignAsset)).thenReturn(assignAsset);
        when(assignAssetMapper.toAssignAssetResponse(assignAsset)).thenReturn(assignAssetResponse);

        AssignAssetResponse actual = editAssignAssetServiceImpl.editAssignment(assignmentId, editAssignmentRequest);

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

    @Test
    void acceptAssignAsset_WhenNotFindEntity_WhenReturnException() throws Exception{
        when(assignAssetRepository.findById(1L)).thenReturn(Optional.empty());

        NotFoundException actual = assertThrows(NotFoundException.class, () -> {
            editAssignAssetServiceImpl.acceptAssignAsset(1L);
        });

        assertThat(actual.getMessage(), is("Assignment not found"));
    }

    @Test
    void acceptAssignAsset_WhenStatusWaiting_WhenReturnException() throws Exception{
        when(assignAssetRepository.findById(1L)).thenReturn(Optional.of(assignAsset));
        when(assignAsset.getStatus()).thenReturn(EAssignStatus.ACCEPTED);

        BadRequestException actual = assertThrows(BadRequestException.class, () -> {
            editAssignAssetServiceImpl.acceptAssignAsset(1L);
        });

        assertThat(actual.getMessage(), is("Assignment is not waiting for acceptance"));
    }

    @Test
    void acceptAssignAsset_WhenAssignedDateIsAfterToday_WhenReturnException() throws Exception {
        ArgumentCaptor<Date> todayCaptor = ArgumentCaptor.forClass(Date.class);
        ArgumentCaptor<Date> assignedDateCaptor = ArgumentCaptor.forClass(Date.class);
        when(assignAssetRepository.findById(1L)).thenReturn(Optional.of(assignAsset));
        when(assignAsset.getStatus()).thenReturn(EAssignStatus.WAITING_FOR_ACCEPTANCE);
        when(compareDateUtil.isBefore(todayCaptor.capture(), assignedDateCaptor.capture())).thenReturn(true);

        BadRequestException actual = assertThrows(BadRequestException.class, () -> {
            editAssignAssetServiceImpl.acceptAssignAsset(1L);
        });

        assertThat(actual.getMessage(), is("Assign date is after today."));
    }

    @Test
    void acceptAssignAsset_WhenCurrentUserIsNotMatchAssignToUser_WhenReturnException() throws Exception {
        ArgumentCaptor<Date> todayCaptor = ArgumentCaptor.forClass(Date.class);
        ArgumentCaptor<Date> assignedDateCaptor = ArgumentCaptor.forClass(Date.class);
        User userAssignedTo = User.builder().id(1L).build();
        User userCurrent = User.builder().id(2L).build();

        when(assignAssetRepository.findById(1L)).thenReturn(Optional.of(assignAsset));
        when(assignAsset.getStatus()).thenReturn(EAssignStatus.WAITING_FOR_ACCEPTANCE);
        when(compareDateUtil.isBefore(todayCaptor.capture(), assignedDateCaptor.capture())).thenReturn(false);
        when(assignAsset.getUserAssignedTo()).thenReturn(userAssignedTo);
        when(securityContextService.getCurrentUser()).thenReturn(userCurrent);

        ForbiddenException actual = assertThrows(ForbiddenException.class, () -> {
            editAssignAssetServiceImpl.acceptAssignAsset(1L);
        });

        assertThat(actual.getMessage(), is("Current user is not match to this assignment."));
    }

    @Test
    void acceptAssignAsset_WhenFindEntity_WhenReturnData() throws Exception {
        ArgumentCaptor<Date> todayCaptor = ArgumentCaptor.forClass(Date.class);
        ArgumentCaptor<Date> assignedDateCaptor = ArgumentCaptor.forClass(Date.class);
        AssignAssetResponse assignAssetResponse = AssignAssetResponse.builder().status(EAssignStatus.ACCEPTED).build();
        User userAssignedTo = User.builder().id(1L).build();
        User userCurrent = User.builder().id(1L).build();

        when(assignAssetRepository.findById(1L)).thenReturn(Optional.of(assignAsset));
        when(assignAsset.getStatus()).thenReturn(EAssignStatus.WAITING_FOR_ACCEPTANCE);
        when(compareDateUtil.isBefore(todayCaptor.capture(), assignedDateCaptor.capture())).thenReturn(false);
        when(assignAsset.getUserAssignedTo()).thenReturn(userAssignedTo);
        when(securityContextService.getCurrentUser()).thenReturn(userCurrent);

        ArgumentCaptor<AssignAsset> assignAssetArgumentCaptor = ArgumentCaptor.forClass(AssignAsset.class);
        when(assignAssetMapper.toAssignAssetResponse(assignAssetArgumentCaptor.capture()))
                .thenReturn(assignAssetResponse);

        AssignAssetResponse actual = editAssignAssetServiceImpl.acceptAssignAsset(1L);
        assertThat(actual, is(assignAssetResponse));
    }

    @Test
    void declineAssignAsset_WhenNotFindEntity_WhenReturnException() throws Exception{
        when(assignAssetRepository.findById(1L)).thenReturn(Optional.empty());

        NotFoundException actual = assertThrows(NotFoundException.class, () -> {
            editAssignAssetServiceImpl.declineAssignAsset(1L);
        });

        assertThat(actual.getMessage(), is("Assignment not found"));
    }

    @Test
    void declineAssignAsset_WhenStatusWaiting_WhenReturnException() throws Exception{
        when(assignAssetRepository.findById(1L)).thenReturn(Optional.of(assignAsset));
        when(assignAsset.getStatus()).thenReturn(EAssignStatus.ACCEPTED);

        BadRequestException actual = assertThrows(BadRequestException.class, () -> {
            editAssignAssetServiceImpl.declineAssignAsset(1L);
        });

        assertThat(actual.getMessage(), is("Assignment is not waiting for acceptance"));
    }

    @Test
    void declineAssignAsset_WhenAssignedDateIsAfterToday_WhenReturnException() throws Exception {
        ArgumentCaptor<Date> todayCaptor = ArgumentCaptor.forClass(Date.class);
        ArgumentCaptor<Date> assignedDateCaptor = ArgumentCaptor.forClass(Date.class);
        when(assignAssetRepository.findById(1L)).thenReturn(Optional.of(assignAsset));
        when(assignAsset.getStatus()).thenReturn(EAssignStatus.WAITING_FOR_ACCEPTANCE);
        when(compareDateUtil.isBefore(todayCaptor.capture(), assignedDateCaptor.capture())).thenReturn(true);

        BadRequestException actual = assertThrows(BadRequestException.class, () -> {
            editAssignAssetServiceImpl.declineAssignAsset(1L);
        });

        assertThat(actual.getMessage(), is("Assign date is after today."));
    }

    @Test
    void declineAssignAsset_WhenCurrentUserIsNotMatchAssignToUser_WhenReturnException() throws Exception {
        ArgumentCaptor<Date> todayCaptor = ArgumentCaptor.forClass(Date.class);
        ArgumentCaptor<Date> assignedDateCaptor = ArgumentCaptor.forClass(Date.class);
        User userAssignedTo = User.builder().id(1L).build();
        User userCurrent = User.builder().id(2L).build();

        when(assignAssetRepository.findById(1L)).thenReturn(Optional.of(assignAsset));
        when(assignAsset.getStatus()).thenReturn(EAssignStatus.WAITING_FOR_ACCEPTANCE);
        when(compareDateUtil.isBefore(todayCaptor.capture(), assignedDateCaptor.capture())).thenReturn(false);
        when(assignAsset.getUserAssignedTo()).thenReturn(userAssignedTo);
        when(securityContextService.getCurrentUser()).thenReturn(userCurrent);

        ForbiddenException actual = assertThrows(ForbiddenException.class, () -> {
            editAssignAssetServiceImpl.declineAssignAsset(1L);
        });

        assertThat(actual.getMessage(), is("Current user is not match to this assignment."));
    }

    @Test
    void declineAssignAsset_WhenFindEntity_WhenReturnData() throws Exception {
        ArgumentCaptor<Date> todayCaptor = ArgumentCaptor.forClass(Date.class);
        ArgumentCaptor<Date> assignedDateCaptor = ArgumentCaptor.forClass(Date.class);
        AssignAssetResponse assignAssetResponse = AssignAssetResponse.builder().status(EAssignStatus.DECLINED).build();
        User userAssignedTo = User.builder().id(1L).build();
        User userCurrent = User.builder().id(1L).build();

        when(assignAssetRepository.findById(1L)).thenReturn(Optional.of(assignAsset));
        when(assignAsset.getStatus()).thenReturn(EAssignStatus.WAITING_FOR_ACCEPTANCE);
        when(compareDateUtil.isBefore(todayCaptor.capture(), assignedDateCaptor.capture())).thenReturn(false);
        when(assignAsset.getUserAssignedTo()).thenReturn(userAssignedTo);
        when(securityContextService.getCurrentUser()).thenReturn(userCurrent);

        ArgumentCaptor<AssignAsset> assignAssetArgumentCaptor = ArgumentCaptor.forClass(AssignAsset.class);
        when(assignAssetMapper.toAssignAssetResponse(assignAssetArgumentCaptor.capture()))
                .thenReturn(assignAssetResponse);

        AssignAssetResponse actual = editAssignAssetServiceImpl.acceptAssignAsset(1L);
        assertThat(actual, is(assignAssetResponse));
    }
}