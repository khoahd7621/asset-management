package com.nashtech.assignment.services.get.impl;

import com.nashtech.assignment.data.entities.User;
import com.nashtech.assignment.data.repositories.UserRepository;
import com.nashtech.assignment.dto.response.user.UserResponse;
import com.nashtech.assignment.exceptions.NotFoundException;
import com.nashtech.assignment.mappers.UserMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class GetUserServiceImplTest {

    private GetUserServiceImpl getUserServiceImpl;
    private UserRepository userRepository;
    private UserMapper userMapper;

    private User user;
    private UserResponse userResponse;

    @BeforeEach
    void setup() {
        userRepository = mock(UserRepository.class);
        userMapper = mock(UserMapper.class);
        getUserServiceImpl = GetUserServiceImpl.builder()
                .userRepository(userRepository)
                .userMapper(userMapper).build();
        user = mock(User.class);
        userResponse = mock(UserResponse.class);
    }

    @Test
    void viewUserDetails_WhenDataValid_ShouldReturnUserDetails() {
        when(userRepository.findByStaffCode("test")).thenReturn(user);
        when(userMapper.toUserResponse(user)).thenReturn(userResponse);

        UserResponse actual = getUserServiceImpl.viewUserDetails("test");

        assertThat(actual, is(userResponse));
    }

    @Test
    void viewUserDetails_WhenNotFound_ShouldReturnNull() {
        when(userRepository.findByStaffCode("test")).thenReturn(null);
        when(userMapper.toUserResponse(user)).thenReturn(userResponse);

        NotFoundException actual = assertThrows(NotFoundException.class, () ->
                getUserServiceImpl.viewUserDetails("test"));

        assertThat(actual.getMessage(), is("Cannot Found Staff With Code: test"));
    }
}