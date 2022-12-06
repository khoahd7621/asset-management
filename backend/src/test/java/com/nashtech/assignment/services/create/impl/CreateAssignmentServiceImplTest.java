package com.nashtech.assignment.services.create.impl;

import com.nashtech.assignment.data.constants.EAssetStatus;
import com.nashtech.assignment.data.entities.Asset;
import com.nashtech.assignment.data.entities.AssignAsset;
import com.nashtech.assignment.data.entities.User;
import com.nashtech.assignment.data.repositories.AssignAssetRepository;
import com.nashtech.assignment.dto.request.assignment.CreateNewAssignmentRequest;
import com.nashtech.assignment.dto.response.assignment.AssignAssetResponse;
import com.nashtech.assignment.exceptions.BadRequestException;
import com.nashtech.assignment.mappers.AssignAssetMapper;
import com.nashtech.assignment.services.auth.SecurityContextService;
import com.nashtech.assignment.services.validation.ValidationAssetService;
import com.nashtech.assignment.services.validation.ValidationUserService;
import com.nashtech.assignment.utils.CompareDateUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

class CreateAssignmentServiceImplTest {

    private CreateAssignmentServiceImpl createAssignmentServiceImpl;
    private AssignAssetRepository assignAssetRepository;
    private AssignAssetMapper assignAssetMapper;
    private SecurityContextService securityContextService;
    private CompareDateUtil compareDateUtil;
    private ValidationAssetService validationAssetService;
    private ValidationUserService validationUserService;

    private Date today;
    private User user;
    private Asset asset;
    private DateFormat dateFormat;

    @BeforeEach
    void beforeEach() {
        securityContextService = mock(SecurityContextService.class);
        assignAssetRepository = mock(AssignAssetRepository.class);
        assignAssetMapper = mock(AssignAssetMapper.class);
        compareDateUtil = mock(CompareDateUtil.class);
        validationAssetService = mock(ValidationAssetService.class);
        validationUserService = mock(ValidationUserService.class);
        createAssignmentServiceImpl = CreateAssignmentServiceImpl.builder()
                .securityContextService(securityContextService)
                .assignAssetRepository(assignAssetRepository)
                .assignAssetMapper(assignAssetMapper)
                .compareDateUtil(compareDateUtil)
                .validationAssetService(validationAssetService)
                .validationUserService(validationUserService).build();
        today = new Date();
        user = mock(User.class);
        asset = mock(Asset.class);
        dateFormat = new SimpleDateFormat("dd/MM/yyyy");
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

        BadRequestException actual = assertThrows(BadRequestException.class, () -> {
            createAssignmentServiceImpl.createNewAssignment(requestData);
        });

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

        AssignAssetResponse actual = createAssignmentServiceImpl.createNewAssignment(requestData);

        assertThat(dateFormat.format(assignedDateCaptor.getValue()).equals(dateFormat.format(today)), is(true));
        assertThat(assetIdCaptor.getValue(), is(1L));
        assertThat(userIdCaptor.getValue(), is(1L));
        verify(asset).setStatus(EAssetStatus.ASSIGNED);
        verify(assignAssetRepository).save(assignAssetCaptor.getValue());
        assertThat(actual, is(expected));
    }
}