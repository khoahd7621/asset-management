package com.nashtech.assignment.services.validation.impl;

import com.nashtech.assignment.data.entities.User;
import com.nashtech.assignment.data.repositories.AssignAssetRepository;
import com.nashtech.assignment.data.repositories.UserRepository;
import com.nashtech.assignment.exceptions.BadRequestException;
import com.nashtech.assignment.exceptions.NotFoundException;
import com.nashtech.assignment.services.auth.SecurityContextService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class ValidationUserServiceImplTest {

    private ValidationUserServiceImpl validationUserServiceImpl;
    private UserRepository userRepository;
    private SecurityContextService securityContextService;
    private AssignAssetRepository assignAssetRepository;

    private User user;

    @BeforeEach
    void setup() {
        userRepository = mock(UserRepository.class);
        securityContextService = mock(SecurityContextService.class);
        assignAssetRepository = mock(AssignAssetRepository.class);
        validationUserServiceImpl = ValidationUserServiceImpl.builder()
                .userRepository(userRepository)
                .securityContextService(securityContextService)
                .assignAssetRepository(assignAssetRepository).build();
        user = mock(User.class);
    }

    @Test
    void validationUserAssignedToAssignment_WhenUserNotExist_ThrowNotFoundException() {
        when(userRepository.findByIdAndIsDeletedFalse(1L)).thenReturn(Optional.empty());

        NotFoundException actual = assertThrows(NotFoundException.class, () -> {
            validationUserServiceImpl.validationUserAssignedToAssignment(1L);
        });

        assertThat(actual.getMessage(), is("Not exist user with this user id."));
    }

    @Test
    void validationUserAssignedToAssignment_WhenAllDataValid_ShouldReturnData() {
        Optional<User> userOpt = Optional.of(user);

        when(userRepository.findByIdAndIsDeletedFalse(1L)).thenReturn(userOpt);

        User actual = validationUserServiceImpl.validationUserAssignedToAssignment(1L);

        assertThat(actual, is(user));
    }

    @Test
    void checkValidUserForDelete_WhenDataValid_ShouldReturnTrue() {
        when(securityContextService.getCurrentUser()).thenReturn(user);
        when(securityContextService.getCurrentUser().getStaffCode()).thenReturn("test2");
        when(userRepository.findByStaffCode("test")).thenReturn(user);
        when(assignAssetRepository.existsByUserAssignedToAndIsDeletedFalse(user)).thenReturn(false);

        Boolean actual = validationUserServiceImpl.checkValidUserForDelete("test");

        assertThat(actual, is(true));
    }

    @Test
    void checkValidUserForDelete_WhenUserIsCurrentUser_ShouldThrowBadRequestException() {
        when(securityContextService.getCurrentUser()).thenReturn(user);
        when(securityContextService.getCurrentUser().getStaffCode()).thenReturn("currentUser");
        when(userRepository.findByStaffCode("currentUser")).thenReturn(user);

        BadRequestException actual = assertThrows(BadRequestException.class,
                () -> validationUserServiceImpl.checkValidUserForDelete("currentUser"));

        assertThat(actual.getMessage(), is("Cannot disable yourself"));
    }

    @Test
    void checkValidUserForDelete_WhenUserNotExist_ShouldThrowNotFoundException() {
        when(securityContextService.getCurrentUser()).thenReturn(user);
        when(securityContextService.getCurrentUser().getStaffCode()).thenReturn("test2");
        when(userRepository.findByStaffCode("test")).thenReturn(null);

        NotFoundException actual = assertThrows(NotFoundException.class, () ->
                validationUserServiceImpl.checkValidUserForDelete("test"));

        assertThat(actual.getMessage(), is("Cannot found user with staff code test"));
    }

    @Test
    void checkValidUserForDelete_WhenUserHaveAssign_ShouldThrowBadRequestException() {
        User user = mock(User.class);

        when(securityContextService.getCurrentUser()).thenReturn(user);
        when(securityContextService.getCurrentUser().getStaffCode()).thenReturn("test2");
        when(userRepository.findByStaffCode("test")).thenReturn(user);
        when(assignAssetRepository.existsByUserAssignedToAndIsDeletedFalse(user)).thenReturn(true);

        BadRequestException actual = assertThrows(BadRequestException.class,
                () -> validationUserServiceImpl.checkValidUserForDelete("test"));

        assertThat(actual.getMessage(), is(
                "There are valid assignments belonging to this user. Please close all assignments before disabling user."));
    }

    @Test
    void checkValidUserForDelete_WhenUserHaveAssignBy_ShouldThrowBadRequestException() {
        when(securityContextService.getCurrentUser()).thenReturn(user);
        when(securityContextService.getCurrentUser().getStaffCode()).thenReturn("test2");
        when(userRepository.findByStaffCode("test")).thenReturn(user);
        when(assignAssetRepository.existsByUserAssignedToAndIsDeletedFalse(user)).thenReturn(false);
        when(assignAssetRepository.existsByUserAssignedByAndIsDeletedFalse(user)).thenReturn(true);

        BadRequestException actual = assertThrows(BadRequestException.class,
                () -> validationUserServiceImpl.checkValidUserForDelete("test"));

        assertThat(actual.getMessage(), is(
                "There are valid assignments belonging to this user. Please close all assignments before disabling user."));
    }
}