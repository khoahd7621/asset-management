package com.nashtech.assignment.services.get.impl;

import com.nashtech.assignment.data.entities.User;
import com.nashtech.assignment.data.repositories.UserRepository;
import com.nashtech.assignment.dto.response.user.UserResponse;
import com.nashtech.assignment.exceptions.NotFoundException;
import com.nashtech.assignment.mappers.UserMapper;
import com.nashtech.assignment.services.auth.SecurityContextService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class GetUserServiceImplTest {

    private GetUserServiceImpl getUserServiceImpl;
    private UserRepository userRepository;
    private UserMapper userMapper;
    private SecurityContextService securityContextService;
    private User user;
    private UserResponse userResponse;

    @BeforeEach
    void setup() {
        userRepository = mock(UserRepository.class);
        userMapper = mock(UserMapper.class);
        securityContextService = mock(SecurityContextService.class);
        getUserServiceImpl = GetUserServiceImpl.builder()
                .userRepository(userRepository)
                .userMapper(userMapper)
                .securityContextService(securityContextService).build();
        user = mock(User.class);
        userResponse= mock(UserResponse.class);
    }

    @Test
    void getAllUser_ShouldReturnData() {
        List<User> userList = new ArrayList<>();
        userList.add(user);
        List<UserResponse> expected = new ArrayList<>();
        expected.add(userResponse);

        when(securityContextService.getCurrentUser()).thenReturn(user);
        when(userRepository.findAllByLocationAndIsDeletedFalse(user.getLocation())).thenReturn(userList);
        when(userMapper.mapListEntityUserResponses(userList)).thenReturn(expected);

        List<UserResponse> actual = getUserServiceImpl.getAllUsers();

        assertThat(actual, is(expected));
    }

    @Test
    void viewUserDetails_WhenDataValid_ShouldReturnUserDetails() {
        when(userRepository.findByStaffCode("test")).thenReturn(user);
        when(userMapper.mapEntityToResponseDto(user)).thenReturn(userResponse);

        UserResponse actual = getUserServiceImpl.viewUserDetails("test");

        assertThat(actual, is(userResponse));
    }

    @Test
    void viewUserDetails_WhenNotFound_ShouldReturnNull() {
        when(userRepository.findByStaffCode("test")).thenReturn(null);
        when(userMapper.mapEntityToResponseDto(user)).thenReturn(userResponse);

        NotFoundException actual = assertThrows(NotFoundException.class, () ->
                getUserServiceImpl.viewUserDetails("test"));

        assertThat(actual.getMessage(), is("Cannot Found Staff With Code: " + "test"));
    }
}