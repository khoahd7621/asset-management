package com.nashtech.assignment.services.validation.impl;

import com.nashtech.assignment.data.entities.User;
import com.nashtech.assignment.data.repositories.UserRepository;
import com.nashtech.assignment.exceptions.BadRequestException;
import com.nashtech.assignment.exceptions.NotFoundException;
import com.nashtech.assignment.utils.CompareDateUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Date;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class ValidationUserServiceImplTest {

    private ValidationUserServiceImpl validationUserServiceImpl;
    private UserRepository userRepository;
    private CompareDateUtil compareDateUtil;

    private User user;
    private Date today;

    @BeforeEach
    void setup() {
        userRepository = mock(UserRepository.class);
        compareDateUtil = mock(CompareDateUtil.class);
        validationUserServiceImpl = ValidationUserServiceImpl.builder()
                .userRepository(userRepository)
                .compareDateUtil(compareDateUtil).build();
        user = mock(User.class);
        today = new Date();
    }

    @Test
    void validationUserAssignedToAssignment_WhenUserNotExist_ThrowNotFoundException() {
        when(userRepository.findByIdAndIsDeletedFalse(1L)).thenReturn(Optional.empty());

        NotFoundException actual = assertThrows(NotFoundException.class, () -> {
            validationUserServiceImpl.validationUserAssignedToAssignment(1L, today);
        });

        assertThat(actual.getMessage(), is("Not exist user with this user id."));
    }

    @Test
    void validationUserAssignedToAssignment_WhenUserExistButAssignedDateBeforeJoinedDate_ThrowBadRequestException() {
        Date assignedDate = new Date(today.getTime() - (1000 * 60 * 60 * 24));
        Optional<User> userOpt = Optional.of(user);

        when(userRepository.findByIdAndIsDeletedFalse(1L)).thenReturn(userOpt);
        when(userOpt.get().getJoinedDate()).thenReturn(today);
        when(compareDateUtil.isBefore(assignedDate, userOpt.get().getJoinedDate())).thenReturn(true);

        BadRequestException actual = assertThrows(BadRequestException.class, () -> {
            validationUserServiceImpl.validationUserAssignedToAssignment(1L, assignedDate);
        });

        assertThat(actual.getMessage(), is("Assigned date cannot before joined date of user."));
    }

    @Test
    void validationUserAssignedToAssignment_WhenAllDataValid_ShouldReturnData() {
        Date assignedDate = today;
        Optional<User> userOpt = Optional.of(user);

        when(userRepository.findByIdAndIsDeletedFalse(1L)).thenReturn(userOpt);
        when(userOpt.get().getJoinedDate()).thenReturn(today);
        when(compareDateUtil.isBefore(assignedDate, userOpt.get().getJoinedDate())).thenReturn(false);

        User actual = validationUserServiceImpl.validationUserAssignedToAssignment(1L, assignedDate);

        assertThat(actual, is(user));
    }
}