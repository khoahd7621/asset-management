package com.nashtech.assignment.services.assignment.impl;

import com.nashtech.assignment.data.constants.Constants;
import com.nashtech.assignment.data.constants.EAssetStatus;
import com.nashtech.assignment.data.constants.EAssignStatus;
import com.nashtech.assignment.data.entities.Asset;
import com.nashtech.assignment.data.entities.AssignAsset;
import com.nashtech.assignment.data.entities.User;
import com.nashtech.assignment.data.repositories.AssetRepository;
import com.nashtech.assignment.data.repositories.AssignAssetRepository;
import com.nashtech.assignment.dto.request.assignment.CreateNewAssignmentRequest;
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
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

class AssignmentServiceImplTest {

    private AssignmentServiceImpl assignmentServiceImpl;
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
    private DateFormat dateFormat;
    private User user;
    private Asset asset;

    @BeforeEach
    void setup() {
        securityContextService = mock(SecurityContextService.class);
        assignAssetRepository = mock(AssignAssetRepository.class);
        assetRepository = mock(AssetRepository.class);
        assignAssetMapper = mock(AssignAssetMapper.class);
        validationAssetService = mock(ValidationAssetService.class);
        validationUserService = mock(ValidationUserService.class);
        compareDateUtil = mock(CompareDateUtil.class);
        assignmentServiceImpl = AssignmentServiceImpl.builder()
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
        dateFormat = new SimpleDateFormat(Constants.DATE_FORMAT);
        user = mock(User.class);
        asset = mock(Asset.class);
    }

    @Test
    void editAssignment_WhenNotExistAssignment_ThrowNotFoundException() {
        long assignmentId = 1L;
        EditAssignmentRequest editAssignmentRequest = EditAssignmentRequest.builder().build();

        when(assignAssetRepository.findByIdAndIsDeletedFalse(assignmentId)).thenReturn(Optional.empty());

        NotFoundException actual = assertThrows(NotFoundException.class, () -> {
            assignmentServiceImpl.editAssignment(assignmentId, editAssignmentRequest);
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
            assignmentServiceImpl.editAssignment(assignmentId, editAssignmentRequest);
        });

        assertThat(actual.getMessage(), is("Can only edit assignment with status waiting for acceptance."));
    }

    @Test
    void editAssignment_WhenChangeAssignDateButItBeforeToday_ThrowBadRequestException() {
        long assignmentId = 1L;
        Date oldAssignedDate = new Date(today.getTime() + (1000 * 60 * 60 * 48));
        Date newAssignedDate = new Date(today.getTime() + (1000 * 60 * 60 * 24));
        EditAssignmentRequest editAssignmentRequest = EditAssignmentRequest.builder()
                .assignedDate(dateFormat.format(newAssignedDate)).build();
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
            assignmentServiceImpl.editAssignment(assignmentId, editAssignmentRequest);
        });

        assertThat(dateFormat.format(oldAssignedDateCaptor.getValue()).equals(dateFormat.format(oldAssignedDate)), is(true));
        assertThat(dateFormat.format(newAssignedDateCaptor.getValue()).equals(dateFormat.format(newAssignedDate)), is(true));
        assertThat(actual.getMessage(), is("Assign date is before today."));
    }

    @Test
    void editAssignment_WhenAllDataValid_ShouldReturnData() throws ParseException {
        long assignmentId = 1L;
        Date assignDate = dateFormat.parse("01/01/2022");
        EditAssignmentRequest editAssignmentRequest = EditAssignmentRequest.builder()
                .assignedDate(dateFormat.format(assignDate))
                .assetId(1L)
                .userId(1L)
                .note("note").build();
        ArgumentCaptor<Long> newAssetIdCaptor = ArgumentCaptor.forClass(Long.class);
        ArgumentCaptor<Long> newUserIdCaptor = ArgumentCaptor.forClass(Long.class);
        Asset asset = mock(Asset.class);
        User user = mock(User.class);

        when(assignAssetRepository.findByIdAndIsDeletedFalse(assignmentId)).thenReturn(Optional.of(assignAsset));
        when(assignAsset.getStatus()).thenReturn(EAssignStatus.WAITING_FOR_ACCEPTANCE);
        when(assignAsset.getAssignedDate()).thenReturn(assignDate);
        when(assignAsset.getUserAssignedTo()).thenReturn(user);
        when(user.getId()).thenReturn(2L);
        when(validationUserService.validationUserAssignedToAssignment(newUserIdCaptor.capture())).thenReturn(user);
        when(assignAsset.getAsset()).thenReturn(asset);
        when(asset.getId()).thenReturn(2L);
        when(validationAssetService.validationAssetAssignedToAssignment(newAssetIdCaptor.capture())).thenReturn(asset);
        when(assignAssetRepository.save(assignAsset)).thenReturn(assignAsset);
        when(assignAssetMapper.toAssignAssetResponse(assignAsset)).thenReturn(assignAssetResponse);

        AssignAssetResponse actual = assignmentServiceImpl.editAssignment(assignmentId, editAssignmentRequest);

        assertThat(newAssetIdCaptor.getValue(), is(1L));
        assertThat(newUserIdCaptor.getValue(), is(1L));
        verify(asset).setStatus(EAssetStatus.ASSIGNED);
        verify(asset).setStatus(EAssetStatus.AVAILABLE);
        verify(assetRepository).save(asset);
        verify(assignAsset).setAsset(asset);
        verify(assignAsset).setUserAssignedTo(user);
        verify(assignAsset).setAssignedDate(dateFormat.parse("01/01/2022"));
        verify(assignAsset).setNote("note");
        verify(assignAssetRepository).save(assignAsset);
        assertThat(actual, is(assignAssetResponse));
    }

    @Test
    void acceptAssignAsset_WhenNotFindEntity_WhenReturnException() throws Exception {
        when(assignAssetRepository.findById(1L)).thenReturn(Optional.empty());

        NotFoundException actual = assertThrows(NotFoundException.class, () -> {
            assignmentServiceImpl.acceptAssignAsset(1L);
        });

        assertThat(actual.getMessage(), is("Assignment not found"));
    }

    @Test
    void acceptAssignAsset_WhenStatusWaiting_WhenReturnException() throws Exception {
        when(assignAssetRepository.findById(1L)).thenReturn(Optional.of(assignAsset));
        when(assignAsset.getStatus()).thenReturn(EAssignStatus.ACCEPTED);

        BadRequestException actual = assertThrows(BadRequestException.class, () -> {
            assignmentServiceImpl.acceptAssignAsset(1L);
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
            assignmentServiceImpl.acceptAssignAsset(1L);
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
            assignmentServiceImpl.acceptAssignAsset(1L);
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

        AssignAssetResponse actual = assignmentServiceImpl.acceptAssignAsset(1L);
        assertThat(actual, is(assignAssetResponse));
    }

    @Test
    void declineAssignAsset_WhenNotFindEntity_WhenReturnException() throws Exception {
        when(assignAssetRepository.findById(1L)).thenReturn(Optional.empty());

        NotFoundException actual = assertThrows(NotFoundException.class, () -> {
            assignmentServiceImpl.declineAssignAsset(1L);
        });

        assertThat(actual.getMessage(), is("Assignment not found"));
    }

    @Test
    void declineAssignAsset_WhenStatusWaiting_WhenReturnException() throws Exception {
        when(assignAssetRepository.findById(1L)).thenReturn(Optional.of(assignAsset));
        when(assignAsset.getStatus()).thenReturn(EAssignStatus.ACCEPTED);

        BadRequestException actual = assertThrows(BadRequestException.class, () -> {
            assignmentServiceImpl.declineAssignAsset(1L);
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
            assignmentServiceImpl.declineAssignAsset(1L);
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
            assignmentServiceImpl.declineAssignAsset(1L);
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

        AssignAssetResponse actual = assignmentServiceImpl.acceptAssignAsset(1L);
        assertThat(actual, is(assignAssetResponse));
    }

    @Test
    void createNewAssignment_WhenAssignDateIsBeforeToDay_ThrowBadRequestException() throws ParseException {
        Date assignedDate = new Date(today.getTime() - (1000 * 60 * 60 * 24));
        CreateNewAssignmentRequest requestData = CreateNewAssignmentRequest.builder()
                .assignedDate(dateFormat.format(assignedDate)).build();

        ArgumentCaptor<Date> todayCaptor = ArgumentCaptor.forClass(Date.class);
        ArgumentCaptor<Date> assignedDateCaptor = ArgumentCaptor.forClass(Date.class);

        when(compareDateUtil.isAfter(todayCaptor.capture(), assignedDateCaptor.capture()))
                .thenReturn(true);

        BadRequestException actual = assertThrows(BadRequestException.class, () ->
                assignmentServiceImpl.createNewAssignment(requestData)
        );

        assertThat(dateFormat.format(assignedDateCaptor.getValue()).equals(dateFormat.format(assignedDate)), is(true));
        assertThat(actual.getMessage(), is("Assign date is before today."));
    }

    @Test
    void createNewAssignment_WhenDataValid_ShouldReturnData() throws ParseException {
        CreateNewAssignmentRequest requestData = CreateNewAssignmentRequest.builder()
                .assignedDate(dateFormat.format(today))
                .assetId(1L)
                .userId(1L).build();
        AssignAsset assignAsset = mock(AssignAsset.class);
        ArgumentCaptor<Date> todayCaptor = ArgumentCaptor.forClass(Date.class);
        ArgumentCaptor<Date> assignedDateCaptor = ArgumentCaptor.forClass(Date.class);
        ArgumentCaptor<Long> assetIdCaptor = ArgumentCaptor.forClass(Long.class);
        ArgumentCaptor<Long> userIdCaptor = ArgumentCaptor.forClass(Long.class);
        ArgumentCaptor<AssignAsset> assignAssetCaptor = ArgumentCaptor.forClass(AssignAsset.class);
        AssignAssetResponse expected = mock(AssignAssetResponse.class);

        when(compareDateUtil.isAfter(todayCaptor.capture(), assignedDateCaptor.capture()))
                .thenReturn(false);
        when(validationAssetService.validationAssetAssignedToAssignment(
                assetIdCaptor.capture())).thenReturn(asset);
        when(validationUserService.validationUserAssignedToAssignment(
                userIdCaptor.capture())).thenReturn(user);
        when(securityContextService.getCurrentUser()).thenReturn(user);
        when(assignAssetRepository.save(assignAssetCaptor.capture())).thenReturn(assignAsset);
        when(assignAssetMapper.toAssignAssetResponse(assignAsset)).thenReturn(expected);

        AssignAssetResponse actual = assignmentServiceImpl.createNewAssignment(requestData);

        assertThat(dateFormat.format(assignedDateCaptor.getValue()).equals(dateFormat.format(today)), is(true));
        assertThat(assetIdCaptor.getValue(), is(1L));
        assertThat(userIdCaptor.getValue(), is(1L));
        verify(asset).setStatus(EAssetStatus.ASSIGNED);
        verify(assignAssetRepository).save(assignAssetCaptor.getValue());
        assertThat(actual, is(expected));
    }

    @Test
    void deleteAssignAsset_WhenDataValid_ShouldReturnVoid() {
        AssignAsset assignAsset = mock(AssignAsset.class);
        Asset asset = mock(Asset.class);
        Optional<AssignAsset> assignAssetOtp = Optional.of(assignAsset);

        when(assignAssetRepository.findById(1L)).thenReturn(assignAssetOtp);
        when(assignAsset.getStatus()).thenReturn(EAssignStatus.DECLINED);
        when(assignAsset.getAsset()).thenReturn(asset);

        assignmentServiceImpl.deleteAssignAsset(1L);

        verify(asset).setStatus(EAssetStatus.AVAILABLE);
        verify(assignAsset).setDeleted(true);
        verify(assetRepository).save(asset);
        verify(assignAssetRepository).save(assignAsset);
    }

    @Test
    void detailAssignAsset_WhenAssignAssetEmpty_ShouldReturnException() {
        when(securityContextService.getCurrentUser()).thenReturn(user);
        when(assignAssetRepository.findByIdAndUser(1L, user.getId())).thenReturn(Optional.empty());
        NotFoundException actualException = Assertions.assertThrows(NotFoundException.class,
                () -> assignmentServiceImpl.getDetailAssignAssetOfUser(1L));

        Assertions.assertEquals("Cannot Found Assign Asset", actualException.getMessage());
    }

    @Test
    void detailAssignAsset_WhenAssignAssetNotEmpty_ShouldReturnException() {
        AssignAssetResponse expected = mock(AssignAssetResponse.class);

        when(securityContextService.getCurrentUser()).thenReturn(user);
        when(assignAssetRepository.findByIdAndUser(1L, user.getId())).thenReturn(Optional.of(assignAsset));
        when(assignAssetMapper.toAssignAssetResponse(assignAsset)).thenReturn(expected);

        AssignAssetResponse actual = assignmentServiceImpl.getDetailAssignAssetOfUser(1L);

        assertThat(actual, is(expected));
    }

    @Test
    void findAssignAssetByUser_WhenFindNull_ShouldReturnException() {
        when(securityContextService.getCurrentUser()).thenReturn(user);
        when(assignAssetRepository.findAllAssignAssetByUser(user.getId(), false, EAssignStatus.DECLINED, ""))
                .thenReturn(Collections.emptyList());

        NotFoundException actualException = Assertions.assertThrows(NotFoundException.class,
                () -> assignmentServiceImpl.getAssignAssetOfUser());

        Assertions.assertEquals("User doesn't assign asset", actualException.getMessage());

    }

    @Test
    void findAssignAssetByUser_WhenFindNotNull_ShouldReturnData() {
        List<AssignAssetResponse> expected = mock(List.class);
        List<AssignAsset> assignAssets = mock(List.class);
        LocalDate date = LocalDate.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(Constants.DATE_FORMAT);

        when(securityContextService.getCurrentUser()).thenReturn(user);
        when(assignAssetRepository.findAllAssignAssetByUser(user.getId(), false, EAssignStatus.DECLINED,
                formatter.format(date)))
                .thenReturn(assignAssets);
        when(assignAssetMapper.toListAssignAssetResponses(assignAssets)).thenReturn(expected);

        List<AssignAssetResponse> actual = assignmentServiceImpl.getAssignAssetOfUser();

        assertThat(actual, is(expected));
    }

    @Test
    void getAssignAssetDetails_WhenDataValid_ShouldReturnAssignAssetResponse() {
        Optional<AssignAsset> assignAssetOtp = Optional.of(assignAsset);
        AssignAssetResponse expected = mock(AssignAssetResponse.class);

        when(assignAssetRepository.findById(1L)).thenReturn(assignAssetOtp);
        when(assignAssetMapper.toAssignAssetResponse(assignAssetOtp.get())).thenReturn(expected);

        AssignAssetResponse actual = assignmentServiceImpl.getAssignAssetDetails(1L);

        assertThat(actual, is(expected));
    }

    @Test
    void getAssignAssetDetails_WhenUserNotExist_ShouldThrowNotFoundException() {
        when(assignAssetRepository.findById(1L)).thenReturn(Optional.empty());

        NotFoundException actual = assertThrows(NotFoundException.class,
                () -> assignmentServiceImpl.getAssignAssetDetails(1L));

        assertThat(actual.getMessage(), is("Cannot find assignment with id: 1"));
    }
}