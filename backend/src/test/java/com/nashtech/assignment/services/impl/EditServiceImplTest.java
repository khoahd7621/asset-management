package com.nashtech.assignment.services.impl;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Optional;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.nashtech.assignment.data.constants.EAssetStatus;
import com.nashtech.assignment.data.constants.EGender;
import com.nashtech.assignment.data.constants.EUserType;
import com.nashtech.assignment.data.entities.Asset;
import com.nashtech.assignment.data.entities.User;
import com.nashtech.assignment.data.repositories.AssetRepository;
import com.nashtech.assignment.data.repositories.UserRepository;
import com.nashtech.assignment.dto.request.asset.EditAssetInformationRequest;
import com.nashtech.assignment.dto.request.user.EditUserRequest;
import com.nashtech.assignment.dto.response.asset.AssetResponse;
import com.nashtech.assignment.dto.response.user.UserResponse;
import com.nashtech.assignment.exceptions.BadRequestException;
import com.nashtech.assignment.exceptions.NotFoundException;
import com.nashtech.assignment.mappers.AssetMapper;
import com.nashtech.assignment.mappers.UserMapper;

public class EditServiceImplTest {

    private UserRepository userRepository;
    private UserMapper userMapper;
    private EditServiceImpl editServiceImpl;
    private User userRepo;
    private UserResponse userResponse;
    private AssetRepository assetRepository;
    private Asset asset;
    private AssetMapper assetMapper;

    @BeforeEach
    void setUp() {
        userRepository = mock(UserRepository.class);
        assetRepository = mock(AssetRepository.class);
        assetMapper = mock(AssetMapper.class);
        asset = mock(Asset.class);
        userMapper = mock(UserMapper.class);
        editServiceImpl = new EditServiceImpl(userRepository, userMapper, assetRepository, assetMapper);
        userRepo = mock(User.class);
        userResponse = mock(UserResponse.class);
    }

    @Test
    void editUserInformation_WhenDataValid_ShouldReturnUserResponse() throws ParseException {
        EditUserRequest user = EditUserRequest.builder()
                .dateOfBirth("21/9/2001")
                .joinedDate("25/11/2022")
                .staffCode("test")
                .gender(EGender.FEMALE)
                .type(EUserType.STAFF)
                .build();

        SimpleDateFormat formatterDate = new SimpleDateFormat("dd/MM/yyyy");
        formatterDate.setLenient(false);
        Date dateOfBirth = formatterDate.parse(user.getDateOfBirth());
        Date joinedDate = formatterDate.parse(user.getJoinedDate());

        when(userRepository.findByStaffCode("test")).thenReturn(userRepo);
        when(userRepository.save(userRepo)).thenReturn(userRepo);
        when(userMapper.mapEntityToResponseDto(userRepo)).thenReturn(userResponse);

        UserResponse actual = editServiceImpl.editUserInformation(user);

        verify(userRepo).setDateOfBirth(dateOfBirth);
        verify(userRepo).setJoinedDate(joinedDate);
        verify(userRepo).setGender(user.getGender());
        verify(userRepo).setType(user.getType());
        verify(userRepository).save(userRepo);

        assertThat(actual, is(userResponse));
    }

    @Test
    void editUserInformation_WhenUserNotFound_ShouldThrowBadRequestException() throws ParseException {
        EditUserRequest user = EditUserRequest.builder()
                .dateOfBirth("21/9/2022")
                .joinedDate("25/11/2022")
                .staffCode("test")
                .gender(EGender.FEMALE)
                .type(EUserType.STAFF)
                .build();
        when(userRepository.findByStaffCode("test")).thenReturn(null);

        NotFoundException actual = assertThrows(NotFoundException.class,
                () -> editServiceImpl.editUserInformation(user));

        assertThat(actual.getMessage(), is("Cannot found staff with Id " + "test"));

    }

    @Test
    void editUserInformation_WhenDateOfBirthNotValid_ShouldThrowBadRequestException() throws ParseException {
        EditUserRequest user = EditUserRequest.builder()
                .dateOfBirth("21/9/2022")
                .joinedDate("25/11/2022")
                .gender(EGender.FEMALE)
                .staffCode("test")
                .type(EUserType.STAFF)
                .build();
        when(userRepository.findByStaffCode("test")).thenReturn(userRepo);

        BadRequestException actual = assertThrows(BadRequestException.class,
                () -> editServiceImpl.editUserInformation(user));

        assertThat(actual.getMessage(), is("Age cannot below 18."));

    }

    @Test
    void editUserInformation_WhenJoinDateIsSundayOrSaturday_ShouldThrowBadRequestException() throws ParseException {
        EditUserRequest user = EditUserRequest.builder()
                .dateOfBirth("21/9/2001")
                .joinedDate("26/11/2022")
                .staffCode("test")
                .build();
        when(userRepository.findByStaffCode("test")).thenReturn(userRepo);

        BadRequestException actual = assertThrows(BadRequestException.class,
                () -> editServiceImpl.editUserInformation(user));

        assertThat(actual.getMessage(), is("Joined date cannot be Saturday or Sunday."));

    }

    @Test
    void editUserInformation_WhenJoinDateIsAfterDateOfBirth_ShouldThrowBadRequestException() throws ParseException {
        EditUserRequest user = EditUserRequest.builder()
                .dateOfBirth("21/9/2001")
                .joinedDate("25/11/2000")
                .staffCode("test")
                .build();
        when(userRepository.findByStaffCode("test")).thenReturn(userRepo);

        BadRequestException actual = assertThrows(BadRequestException.class,
                () -> editServiceImpl.editUserInformation(user));

        assertThat(actual.getMessage(), is("Joined date must lager or equal 18 years."));

    }

    @Test
    void testEditAssetInformation_WhenFindAssetNull_ShouldReturnException() throws NotFoundException {
        long idAsset = 1L;
        EditAssetInformationRequest editAssetInformationRequest = EditAssetInformationRequest.builder().build();

        when(assetRepository.findById(idAsset)).thenReturn(Optional.empty());
        NotFoundException actualException = Assertions.assertThrows(NotFoundException.class,
                () -> editServiceImpl.editAssetInformation(idAsset,
                        editAssetInformationRequest));

        Assertions.assertEquals("Asset not found", actualException.getMessage());
    }

    @Test
    void testEditAssetInformation_WhenAssetStatusAssigned_ShouldReturnException() {
        long idAsset = 1L;
        EditAssetInformationRequest editAssetInformationRequest = EditAssetInformationRequest.builder().build();

        Optional<Asset> assetOpt = Optional.of(asset);
        when(assetRepository.findById(idAsset)).thenReturn(assetOpt);
        when(assetOpt.get().getStatus())
                .thenReturn(EAssetStatus.ASSIGNED);

        BadRequestException actualException = Assertions.assertThrows(BadRequestException.class,
                () -> editServiceImpl.editAssetInformation(idAsset,
                        editAssetInformationRequest));

        Assertions.assertEquals("Asset have state is assigned", actualException.getMessage());
    }

    @Test
    void testEditAssetInformation_WhenDataValid_ShouldReturnData() throws ParseException {
        long idAsset = 1L;
        EditAssetInformationRequest editAssetInformationRequest = EditAssetInformationRequest.builder()
                .assetName("assetName")
                .specification("assetSpecification")
                .assetStatus(EAssetStatus.AVAILABLE)
                .installedDate("01/01/2022")
                .build();

        AssetResponse expected = mock(AssetResponse.class);

        SimpleDateFormat sourceFormat = new SimpleDateFormat("dd/MM/yyyy");
        Date installedDate = sourceFormat.parse(editAssetInformationRequest.getInstalledDate());

        when(assetRepository.findById(idAsset)).thenReturn(Optional.of(asset));
        when(asset.getStatus())
                .thenReturn(EAssetStatus.NOT_AVAILABLE);
        when(assetMapper.mapEntityToEditAssetInformationResponse(asset)).thenReturn(expected);

        AssetResponse actual = editServiceImpl.editAssetInformation(idAsset,
                editAssetInformationRequest);

        verify(asset).setName("assetName");
        verify(asset).setSpecification("assetSpecification");
        verify(asset).setStatus(EAssetStatus.AVAILABLE);
        verify(asset).setInstalledDate(installedDate);
        verify(assetRepository).save(asset);

        assertThat(actual, is(expected));
    }
}
