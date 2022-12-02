package com.nashtech.assignment.services.create.impl;

import com.nashtech.assignment.data.constants.EAssetStatus;
import com.nashtech.assignment.data.entities.Asset;
import com.nashtech.assignment.data.entities.AssignAsset;
import com.nashtech.assignment.data.entities.User;
import com.nashtech.assignment.data.repositories.AssetRepository;
import com.nashtech.assignment.data.repositories.AssignAssetRepository;
import com.nashtech.assignment.data.repositories.UserRepository;
import com.nashtech.assignment.dto.request.assignment.CreateNewAssignmentRequest;
import com.nashtech.assignment.dto.response.assignment.AssignAssetResponse;
import com.nashtech.assignment.exceptions.BadRequestException;
import com.nashtech.assignment.exceptions.NotFoundException;
import com.nashtech.assignment.mappers.AssignAssetMapper;
import com.nashtech.assignment.services.SecurityContextService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.Date;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

class CreateAssignmentServiceImplTest {

    private CreateAssignmentServiceImpl createAssignmentServiceImpl;
    private UserRepository userRepository;
    private AssetRepository assetRepository;
    private AssignAssetRepository assignAssetRepository;
    private AssignAssetMapper assignAssetMapper;
    private SecurityContextService securityContextService;
    private User user;
    private Asset asset;

    @BeforeEach
    void beforeEach() {
        userRepository = mock(UserRepository.class);
        assetRepository = mock(AssetRepository.class);
        securityContextService = mock(SecurityContextService.class);
        assignAssetRepository = mock(AssignAssetRepository.class);
        assignAssetMapper = mock(AssignAssetMapper.class);
        createAssignmentServiceImpl = CreateAssignmentServiceImpl.builder()
                .userRepository(userRepository)
                .securityContextService(securityContextService)
                .assetRepository(assetRepository)
                .assignAssetRepository(assignAssetRepository)
                .assignAssetMapper(assignAssetMapper).build();
        user = mock(User.class);
        asset = mock(Asset.class);
    }

    @Test
    void createNewAssignment_WhenAssignDateIsBeforeToDay_ThrowBadRequestException() throws ParseException {
        CreateNewAssignmentRequest requestData = CreateNewAssignmentRequest.builder()
                .assignedDate(new SimpleDateFormat("yyyy-MM-dd").parse("2022-01-01")).build();

        BadRequestException actual = assertThrows(BadRequestException.class, () -> {
            createAssignmentServiceImpl.createNewAssignment(requestData);
        });

        assertThat(actual.getMessage(), is("Assign date is before today."));
    }

    @Test
    void createNewAssignment_WhenAssetNotExist_ThrowNotFoundException() {
        CreateNewAssignmentRequest requestData = CreateNewAssignmentRequest.builder()
                .assignedDate(new Date())
                .assetId(1L).build();

        when(assetRepository.findByIdAndIsDeletedFalse(requestData.getAssetId())).thenReturn(Optional.empty());

        NotFoundException actual = assertThrows(NotFoundException.class, () -> {
            createAssignmentServiceImpl.createNewAssignment(requestData);
        });

        assertThat(actual.getMessage(), is("Not exist asset with this assetId."));
    }

    @Test
    void createNewAssignment_WhenAssetExistButStatusIsNotAvailable_ThrowBadRequestException() {
        CreateNewAssignmentRequest requestData = CreateNewAssignmentRequest.builder()
                .assignedDate(new Date())
                .assetId(1L).build();
        Optional<Asset> assetOptional = Optional.of(asset);

        when(assetRepository.findByIdAndIsDeletedFalse(requestData.getAssetId())).thenReturn(assetOptional);
        when(assetOptional.get().getStatus()).thenReturn(EAssetStatus.ASSIGNED);

        BadRequestException actual = assertThrows(BadRequestException.class, () -> {
            createAssignmentServiceImpl.createNewAssignment(requestData);
        });

        assertThat(actual.getMessage(), is("Status of this asset is not available."));
    }

    @Test
    void createNewAssignment_WhenUserNotExist_ThrowNotFoundException() {
        CreateNewAssignmentRequest requestData = CreateNewAssignmentRequest.builder()
                .assignedDate(new Date())
                .assetId(1L)
                .userId(1L).build();
        Optional<Asset> assetOptional = Optional.of(asset);

        when(assetRepository.findByIdAndIsDeletedFalse(requestData.getAssetId())).thenReturn(assetOptional);
        when(assetOptional.get().getStatus()).thenReturn(EAssetStatus.AVAILABLE);
        when(userRepository.findByIdAndIsDeletedFalse(requestData.getUserId())).thenReturn(Optional.empty());

        NotFoundException actual = assertThrows(NotFoundException.class, () -> {
            createAssignmentServiceImpl.createNewAssignment(requestData);
        });

        assertThat(actual.getMessage(), is("Not exist user with this userId."));
    }

    @Test
    void createNewAssignment_WhenAssignDateIsBeforeJoinedDateOfUser_ThrowBadRequestException() throws ParseException {
        CreateNewAssignmentRequest requestData = CreateNewAssignmentRequest.builder()
                .assignedDate(new Date())
                .assetId(1L)
                .userId(1L).build();
        Optional<Asset> assetOptional = Optional.of(asset);
        Optional<User> userOptional = Optional.of(user);
        Date today = new Date();
        Date tomorrow = new Date(today.getTime() + (1000 * 60 * 60 * 24));

        when(assetRepository.findByIdAndIsDeletedFalse(requestData.getAssetId())).thenReturn(assetOptional);
        when(assetOptional.get().getStatus()).thenReturn(EAssetStatus.AVAILABLE);
        when(userRepository.findByIdAndIsDeletedFalse(requestData.getUserId())).thenReturn(userOptional);
        when(userOptional.get().getJoinedDate()).thenReturn(tomorrow);

        BadRequestException actual = assertThrows(BadRequestException.class, () -> {
            createAssignmentServiceImpl.createNewAssignment(requestData);
        });

        assertThat(actual.getMessage(), is("Assigned date cannot before joined date of user."));
    }

    @Test
    void createNewAssignment_WhenDataValid_ShouldReturnData() {
        CreateNewAssignmentRequest requestData = CreateNewAssignmentRequest.builder()
                .assignedDate(new Date())
                .assetId(1L)
                .userId(1L).build();
        Optional<Asset> assetOptional = Optional.of(asset);
        Optional<User> userOptional = Optional.of(user);
        AssignAsset assignAsset = mock(AssignAsset.class);
        ArgumentCaptor<AssignAsset> assignAssetCaptor = ArgumentCaptor.forClass(AssignAsset.class);
        AssignAssetResponse expected = mock(AssignAssetResponse.class);

        when(assetRepository.findByIdAndIsDeletedFalse(requestData.getAssetId())).thenReturn(assetOptional);
        when(assetOptional.get().getStatus()).thenReturn(EAssetStatus.AVAILABLE);
        when(userRepository.findByIdAndIsDeletedFalse(requestData.getUserId())).thenReturn(userOptional);
        when(userOptional.get().getJoinedDate()).thenReturn(new Date());
        when(securityContextService.getCurrentUser()).thenReturn(user);
        when(assignAssetRepository.save(assignAssetCaptor.capture())).thenReturn(assignAsset);
        when(assignAssetMapper.toAssignAssetResponse(assignAsset)).thenReturn(expected);

        AssignAssetResponse actual = createAssignmentServiceImpl.createNewAssignment(requestData);
        verify(asset).setStatus(EAssetStatus.ASSIGNED);
        verify(assignAssetRepository).save(assignAssetCaptor.getValue());
        assertThat(actual, is(expected));
    }
}