package com.nashtech.assignment.services.delete.impl;

import com.nashtech.assignment.data.entities.User;
import com.nashtech.assignment.data.repositories.AssignAssetRepository;
import com.nashtech.assignment.data.repositories.UserRepository;
import com.nashtech.assignment.exceptions.BadRequestException;
import com.nashtech.assignment.exceptions.NotFoundException;
import com.nashtech.assignment.services.auth.SecurityContextService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

class DeleteUserServiceImplTest {

    private DeleteUserServiceImpl deleteUserServiceImpl;
    private UserRepository userRepository;
    private AssignAssetRepository assignAssetRepository;
    private SecurityContextService securityContextService;
    private User currentUser;
    private User user;

    @BeforeEach
    void setUpTest() {
        userRepository = mock(UserRepository.class);
        securityContextService = mock(SecurityContextService.class);
        assignAssetRepository = mock(AssignAssetRepository.class);
        deleteUserServiceImpl = DeleteUserServiceImpl.builder()
                .assignAssetRepository(assignAssetRepository)
                .userRepository(userRepository)
                .securityContextService(securityContextService).build();

        currentUser = User.builder().staffCode("currentUser").build();
        user = mock(User.class);
    }

    @Test
    void deleteUser_WhenDataValid_ShouldReturnVoid() {
        when(securityContextService.getCurrentUser()).thenReturn(currentUser);
        when(userRepository.findByStaffCode("test")).thenReturn(user);
        when(assignAssetRepository.existsByUserAssignedToAndIsDeletedFalse(user)).thenReturn(false);

        deleteUserServiceImpl.deleteUser("test");

        verify(user).setDeleted(true);
        verify(userRepository).save(user);
    }

    @Test
    void deleteUser_WhenUserNotExist_ShouldThrowNotFoundException() {
        when(securityContextService.getCurrentUser()).thenReturn(currentUser);
        when(userRepository.findByStaffCode("test")).thenReturn(null);

        NotFoundException actual = assertThrows(NotFoundException.class, () ->
                deleteUserServiceImpl.deleteUser("test")
        );

        assertThat(actual.getMessage(), is("Cannot found user with staff code test"));
    }

    @Test
    void deleteUser_WhenUserHaveAssign_ShouldThrowBadRequestException() {
        when(securityContextService.getCurrentUser()).thenReturn(currentUser);
        when(userRepository.findByStaffCode("test")).thenReturn(user);
        when(assignAssetRepository.existsByUserAssignedToAndIsDeletedFalse(user)).thenReturn(true);

        BadRequestException actual = assertThrows(BadRequestException.class,
                () -> deleteUserServiceImpl.deleteUser("test"));

        assertThat(actual.getMessage(), is(
                "There are valid assignments belonging to this user. Please close all assignments before disabling user."));
    }

    @Test
    void deleteUser_WhenUserHaveAssignBy_ShouldThrowBadRequestException() {
        when(securityContextService.getCurrentUser()).thenReturn(currentUser);
        when(userRepository.findByStaffCode("test")).thenReturn(user);
        when(assignAssetRepository.existsByUserAssignedToAndIsDeletedFalse(user)).thenReturn(true);
        when(assignAssetRepository.existsByUserAssignedByAndIsDeletedFalse(user)).thenReturn(true);

        BadRequestException actual = assertThrows(BadRequestException.class,
                () -> deleteUserServiceImpl.deleteUser("test"));

        assertThat(actual.getMessage(), is(
                "There are valid assignments belonging to this user. Please close all assignments before disabling user."));
    }

    @Test
    void deleteUser_WhenUserDeleteIsCurrentUser_ShouldThrowBadRequestException() {
        when(securityContextService.getCurrentUser()).thenReturn(currentUser);
        when(userRepository.findByStaffCode("currentUser")).thenReturn(currentUser);

        BadRequestException actual = assertThrows(BadRequestException.class,
                () -> deleteUserServiceImpl.deleteUser("currentUser"));

        assertThat(actual.getMessage(), is("Cannot disable yourself"));
    }

    @Test
    void checkValidUser_WhenDataValid_ShouldReturnTrue() {
        when(securityContextService.getCurrentUser()).thenReturn(currentUser);
        when(userRepository.findByStaffCode("test")).thenReturn(user);
        when(assignAssetRepository.existsByUserAssignedToAndIsDeletedFalse(user)).thenReturn(false);

        Boolean actual = deleteUserServiceImpl.checkValidUser("test");

        assertThat(actual, is(true));
    }

    @Test
    void checkValidUser_WhenUserIsCurrentUser_ShouldThrowBadRequestException() {
        when(securityContextService.getCurrentUser()).thenReturn(currentUser);
        when(userRepository.findByStaffCode("currentUser")).thenReturn(currentUser);

        BadRequestException actual = assertThrows(BadRequestException.class,
                () -> deleteUserServiceImpl.checkValidUser("currentUser"));

        assertThat(actual.getMessage(), is("Cannot disable yourself"));
    }

    @Test
    void checkValidUser_WhenUserNotExist_ShouldThrowNotFoundException() {
        when(securityContextService.getCurrentUser()).thenReturn(currentUser);
        when(userRepository.findByStaffCode("test")).thenReturn(null);

        NotFoundException actual = assertThrows(NotFoundException.class, () ->
                deleteUserServiceImpl.checkValidUser("test"));

        assertThat(actual.getMessage(), is("Cannot found user with staff code test"));
    }

    @Test
    void checkValidUser_WhenUserHaveAssign_ShouldThrowBadRequestException() {
        User user = mock(User.class);

        when(securityContextService.getCurrentUser()).thenReturn(currentUser);
        when(userRepository.findByStaffCode("test")).thenReturn(user);
        when(assignAssetRepository.existsByUserAssignedToAndIsDeletedFalse(user)).thenReturn(true);

        BadRequestException actual = assertThrows(BadRequestException.class,
                () -> deleteUserServiceImpl.checkValidUser("test"));

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
                () -> deleteUserServiceImpl.checkValidUser("test"));

        assertThat(actual.getMessage(), is(
                "There are valid assignments belonging to this user. Please close all assignments before disabling user."));
    }

}