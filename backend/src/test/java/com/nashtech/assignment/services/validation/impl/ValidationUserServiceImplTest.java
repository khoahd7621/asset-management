package com.nashtech.assignment.services.validation.impl;

import com.nashtech.assignment.data.entities.User;
import com.nashtech.assignment.data.repositories.UserRepository;
import com.nashtech.assignment.exceptions.NotFoundException;
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

    private User user;

    @BeforeEach
    void setup() {
        userRepository = mock(UserRepository.class);
        validationUserServiceImpl = ValidationUserServiceImpl.builder()
                .userRepository(userRepository).build();
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
}