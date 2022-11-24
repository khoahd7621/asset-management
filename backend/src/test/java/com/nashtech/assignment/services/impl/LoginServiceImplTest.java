package com.nashtech.assignment.services.impl;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.nashtech.assignment.data.entities.User;
import com.nashtech.assignment.data.repositories.UserRepository;
import com.nashtech.assignment.dto.request.UserLoginRequest;
import com.nashtech.assignment.dto.response.UserLoginResponse;
import com.nashtech.assignment.exceptions.BadRequestException;
import com.nashtech.assignment.utils.GeneratePassword;
import com.nashtech.assignment.utils.JwtTokenUtil;

public class LoginServiceImplTest {
    UserRepository userRepository;

    JwtTokenUtil jwtTokenUtil;

    PasswordEncoder passwordEncoder;

    User user;

    UserLoginResponse userLoginResponse;

    LoginServiceImpl loginServiceImpl;

    GeneratePassword generatePassword;

    @BeforeEach
    void setUpBeforeTest() {
        user = mock(User.class);
        userLoginResponse = mock(UserLoginResponse.class);
        userRepository = mock(UserRepository.class);
        jwtTokenUtil = mock(JwtTokenUtil.class);
        passwordEncoder = mock(PasswordEncoder.class);
        generatePassword = mock(GeneratePassword.class);
        loginServiceImpl = new LoginServiceImpl(userRepository, jwtTokenUtil, passwordEncoder, generatePassword);
    }

    @Test
    void testLogin_WhenUserFindNull_ShouldReturnBadRequestException() throws BadRequestException {
        UserLoginRequest userLoginRequest = UserLoginRequest.builder().username("username").build();

        when(userRepository.findByUsernameAndIsDeletedFalse(userLoginRequest.getUsername())).thenReturn(Optional.empty());

        BadRequestException actualException = Assertions.assertThrows(BadRequestException.class,
                () -> loginServiceImpl.login(userLoginRequest));

        Assertions.assertEquals("Username not found", actualException.getMessage());
    }

    @Test
    void testLogin_WhenUserFindNotActive_ShouldReturnBadRequestException() throws BadRequestException {
        UserLoginRequest userLoginRequest = UserLoginRequest.builder().username("username").build();

        when(userRepository.findByUsernameAndIsDeletedFalse(userLoginRequest.getUsername())).thenReturn(Optional.of(user));
        when(user.isDeleted()).thenReturn(true);

        BadRequestException actualException = Assertions.assertThrows(BadRequestException.class,
                () -> loginServiceImpl.login(userLoginRequest));

        Assertions.assertEquals("User is not active", actualException.getMessage());
    }

    @Test
    void testLogin_WhenPasswordInvalid_ShouldReturnBadRequestException() throws BadRequestException {
        UserLoginRequest userLoginRequest = UserLoginRequest.builder().username("username").password("123456").build();

        when(userRepository.findByUsernameAndIsDeletedFalse(userLoginRequest.getUsername())).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(userLoginRequest.getPassword(), user.getPassword())).thenReturn(false);

        BadRequestException actualException = Assertions.assertThrows(BadRequestException.class,
                () -> loginServiceImpl.login(userLoginRequest));

        Assertions.assertEquals("Username or password is incorrect. Please try again", actualException.getMessage());
    }

    @Test
    void testLogin_WhenPasswordFirstLogin_ShouldReturnTrue() throws BadRequestException {
        UserLoginRequest userLoginRequest = UserLoginRequest.builder().username("username").password("123456").build();

        when(userRepository.findByUsernameAndIsDeletedFalse(userLoginRequest.getUsername())).thenReturn(Optional.of(user));
        when(user.isDeleted()).thenReturn(false);
        when(passwordEncoder.matches(userLoginRequest.getPassword(), user.getPassword())).thenReturn(true);
        when(passwordEncoder.matches(userLoginRequest.getPassword(), generatePassword.firstPassword(user)))
                .thenReturn(true);

        UserLoginResponse actual = loginServiceImpl.login(userLoginRequest);

        Assertions.assertEquals(true, actual.getIsFirstLogin());
    }

    @Test
    void testLogin_WhenPasswordValid_ShouldLoginSuccess() throws BadRequestException {
        UserLoginRequest userLoginRequest = UserLoginRequest.builder().username("username").password("123456").build();

        when(userRepository.findByUsernameAndIsDeletedFalse(userLoginRequest.getUsername())).thenReturn(Optional.of(user));
        when(user.isDeleted()).thenReturn(false);
        when(passwordEncoder.matches(userLoginRequest.getPassword(), user.getPassword())).thenReturn(true);
        when(jwtTokenUtil.generateJwtToken(user)).thenReturn("accessToken");

        UserLoginResponse actual = loginServiceImpl.login(userLoginRequest);
        assertThat(actual.getAccessToken(), is("accessToken"));
    }
}
