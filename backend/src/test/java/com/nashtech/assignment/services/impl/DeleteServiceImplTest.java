package com.nashtech.assignment.services.impl;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.nashtech.assignment.data.constants.EAssignStatus;
import com.nashtech.assignment.data.entities.Asset;
import com.nashtech.assignment.data.repositories.AssetRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.nashtech.assignment.data.entities.User;
import com.nashtech.assignment.data.repositories.AssignAssetRepository;
import com.nashtech.assignment.data.repositories.UserRepository;
import com.nashtech.assignment.exceptions.BadRequestException;
import com.nashtech.assignment.exceptions.NotFoundException;
import com.nashtech.assignment.services.SecurityContextService;

import java.util.Optional;

public class DeleteServiceImplTest {

    private UserRepository userRepository;
    private AssignAssetRepository assignAssetRepository;
    private DeleteServiceImpl deleteServiceImpl;
    private SecurityContextService securityContextService;
    private User currentUser;
    private AssetRepository assetRepository;
    private Asset asset;

    @BeforeEach
    void setUpTest() {
        userRepository = mock(UserRepository.class);
        securityContextService = mock(SecurityContextService.class);
        assignAssetRepository = mock(AssignAssetRepository.class);
        assetRepository = mock(AssetRepository.class);
        deleteServiceImpl = DeleteServiceImpl.builder()
                .assignAssetRepository(assignAssetRepository)
                .userRepository(userRepository)
                .securityContextService(securityContextService)
                .assetRepository(assetRepository).build();

        currentUser = User.builder().staffCode("currentUser").build();
        asset = mock(Asset.class);
    }

    @Test
    void deleteUser_WhenDataValid_ShouldReturnVoid() {
        User user = mock(User.class);

        when(securityContextService.getCurrentUser()).thenReturn(currentUser);
        when(userRepository.findByStaffCode("test")).thenReturn(user);
        when(assignAssetRepository.existsByUserAssignedToAndIsDeletedFalse(user)).thenReturn(false);

        deleteServiceImpl.deleteUser("test");

        verify(user).setDeleted(true);
        verify(userRepository).save(user);
    }

    @Test
    void deleteUser_WhenUserNotExist_ShouldThrowNotFoundException() {

        when(securityContextService.getCurrentUser()).thenReturn(currentUser);
        when(userRepository.findByStaffCode("test")).thenReturn(null);

        NotFoundException actual = assertThrows(NotFoundException.class, ()->deleteServiceImpl.deleteUser("test"));
        
        assertThat(actual.getMessage(), is("Cannot found user with staff code test"));   
    }

    @Test
    void deleteUser_WhenUserHaveAssign_ShouldThrowBadRequestException() {
        User user = mock(User.class);

        when(securityContextService.getCurrentUser()).thenReturn(currentUser);
        when(userRepository.findByStaffCode("test")).thenReturn(user);
        when(assignAssetRepository.existsByUserAssignedToAndIsDeletedFalse(user)).thenReturn(true);

        BadRequestException actual = assertThrows(BadRequestException.class,
                () -> deleteServiceImpl.deleteUser("test"));

        assertThat(actual.getMessage(), is(
                "There are valid assignments belonging to this user. Please close all assignments before disabling user."));

    }
    @Test
    void deleteUser_WhenUserHaveAssignBy_ShouldThrowBadRequestException() {
        User user = mock(User.class);

        when(securityContextService.getCurrentUser()).thenReturn(currentUser);
        when(userRepository.findByStaffCode("test")).thenReturn(user);
        when(assignAssetRepository.existsByUserAssignedToAndIsDeletedFalse(user)).thenReturn(true);
        when(assignAssetRepository.existsByUserAssignedByAndIsDeletedFalse(user)).thenReturn(true);

        BadRequestException actual = assertThrows(BadRequestException.class,
                () -> deleteServiceImpl.deleteUser("test"));

        assertThat(actual.getMessage(), is(
                "There are valid assignments belonging to this user. Please close all assignments before disabling user."));

    }

    @Test
    void deleteUser_WhenUserDeleteIsCurrentUser_ShouldThrowBadRequestException() {
        User user = User.builder().staffCode("currentUser").build();

        when(securityContextService.getCurrentUser()).thenReturn(currentUser);
        when(userRepository.findByStaffCode("currentUser")).thenReturn(user);

        BadRequestException actual = assertThrows(BadRequestException.class,
                () -> deleteServiceImpl.deleteUser("currentUser"));

        assertThat(actual.getMessage(), is("Cannot disable yourselft"));

    }

    @Test
    void checkValidUser_WhenDataValid_ShouldReturnTrue() {
        User user = mock(User.class);

        when(securityContextService.getCurrentUser()).thenReturn(currentUser);
        when(userRepository.findByStaffCode("test")).thenReturn(user);
        when(assignAssetRepository.existsByUserAssignedToAndIsDeletedFalse(user)).thenReturn(false);

        Boolean actual = deleteServiceImpl.checkValidUser("test");

        assertThat(actual, is(true));
    }

    @Test
    void checkValidUser_WhenUserIsCurrentUser_ShouldThrowBadRequestException() {
        User user = User.builder().staffCode("currentUser").build();

        when(securityContextService.getCurrentUser()).thenReturn(currentUser);
        when(userRepository.findByStaffCode("currentUser")).thenReturn(user);

        BadRequestException actual = assertThrows(BadRequestException.class,
                () -> deleteServiceImpl.checkValidUser("currentUser"));

        assertThat(actual.getMessage(), is("Cannot disable yourselft"));
    }

    @Test
    void checkValidUser_WhenUserNotExist_ShouldThrowNotFoundException() {

        when(securityContextService.getCurrentUser()).thenReturn(currentUser);
        when(userRepository.findByStaffCode("test")).thenReturn(null);

        NotFoundException actual = assertThrows(NotFoundException.class, ()->deleteServiceImpl.checkValidUser("test"));
        
        assertThat(actual.getMessage(), is("Cannot found user with staff code test"));

        
    }

    @Test
    void checkValidUser_WhenUserHaveAssign_ShouldThrowBadRequestException() {
        User user = mock(User.class);

        when(securityContextService.getCurrentUser()).thenReturn(currentUser);
        when(userRepository.findByStaffCode("test")).thenReturn(user);
        when(assignAssetRepository.existsByUserAssignedToAndIsDeletedFalse(user)).thenReturn(true);

        BadRequestException actual = assertThrows(BadRequestException.class,
                () -> deleteServiceImpl.checkValidUser("test"));

        assertThat(actual.getMessage(), is(
                "There are valid assignments belonging to this user. Please close all assignments before disabling user."));

    }
    @Test
    void checkValidUser_WhenUserHaveAssignBy_ShouldThrowBadRequestException() {
        User user = mock(User.class);

        when(securityContextService.getCurrentUser()).thenReturn(currentUser);
        when(userRepository.findByStaffCode("test")).thenReturn(user);
        when(assignAssetRepository.existsByUserAssignedToAndIsDeletedFalse(user)).thenReturn(false);
        when(assignAssetRepository.existsByUserAssignedByAndIsDeletedFalse(user)).thenReturn(true);

        BadRequestException actual = assertThrows(BadRequestException.class,
                () -> deleteServiceImpl.checkValidUser("test"));

        assertThat(actual.getMessage(), is(
                "There are valid assignments belonging to this user. Please close all assignments before disabling user."));

    }

    @Test
    void deleteAssetByAssetId_WhenAssetIdNotExist_ShouldThrowNotFoundException() {
        long assetId = 1L;

        when(assetRepository.findByIdAndIsDeletedFalse(assetId)).thenReturn(Optional.empty());

        NotFoundException actual = assertThrows(NotFoundException.class,
                () -> deleteServiceImpl.deleteAssetByAssetId(assetId));

        assertThat(actual.getMessage(), is("Don't exist asset with this assetId."));
    }

    @Test
    void deleteAssetByAssetId_WhenAssetIdExistButNotValidForDelete_ShouldThrowBadRequestException() {
        long assetId = 1L;

        when(assetRepository.findByIdAndIsDeletedFalse(assetId)).thenReturn(Optional.of(asset));
        when(assignAssetRepository.existsByAssetIdAndStatusAndIsDeletedFalse(assetId, EAssignStatus.ACCEPTED))
                .thenReturn(true);

        BadRequestException actual = assertThrows(BadRequestException.class,
                () -> deleteServiceImpl.deleteAssetByAssetId(assetId));

        assertThat(actual.getMessage(), is("Asset already assigned. Invalid for delete."));
    }

    @Test
    void deleteAssetByAssetId_WhenAssetIdExistAndValidForDelete_ShouldDeleteSuccess() {
        long assetId = 1L;

        when(assetRepository.findByIdAndIsDeletedFalse(assetId)).thenReturn(Optional.of(asset));
        when(assignAssetRepository.existsByAssetIdAndStatusAndIsDeletedFalse(assetId, EAssignStatus.ACCEPTED))
                .thenReturn(false);

        deleteServiceImpl.deleteAssetByAssetId(assetId);

        verify(asset).setDeleted(true);
        verify(assetRepository).save(asset);
    }
}
