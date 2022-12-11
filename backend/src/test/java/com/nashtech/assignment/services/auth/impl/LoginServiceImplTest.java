package com.nashtech.assignment.services.auth.impl;

import com.nashtech.assignment.data.entities.User;
import com.nashtech.assignment.data.repositories.UserRepository;
import com.nashtech.assignment.dto.request.UserLoginRequest;
import com.nashtech.assignment.dto.response.UserLoginResponse;
import com.nashtech.assignment.exceptions.BadRequestException;
import com.nashtech.assignment.utils.GeneratePassword;
import com.nashtech.assignment.utils.JwtTokenUtil;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class LoginServiceImplTest {

    LoginServiceImpl loginServiceImpl;
    UserRepository userRepository;
    JwtTokenUtil jwtTokenUtil;
    PasswordEncoder passwordEncoder;
    GeneratePassword generatePassword;

    User user;
    UserLoginResponse userLoginResponse;

    @BeforeEach
    void setUpBeforeTest() {
        user = mock(User.class);
        userLoginResponse = mock(UserLoginResponse.class);
        userRepository = mock(UserRepository.class);
        jwtTokenUtil = mock(JwtTokenUtil.class);
        passwordEncoder = mock(PasswordEncoder.class);
        generatePassword = mock(GeneratePassword.class);
        loginServiceImpl = LoginServiceImpl.builder()
                .generatePassword(generatePassword)
                .jwtTokenUtil(jwtTokenUtil)
                .passwordEncoder(passwordEncoder)
                .userRepository(userRepository).build();
    }

    @Test
    void testLogin_WhenUserFindNull_ShouldReturnBadRequestException() throws BadRequestException {
        UserLoginRequest userLoginRequest = UserLoginRequest.builder().username("username").build();

        when(userRepository.findByUsername(userLoginRequest.getUsername())).thenReturn(Optional.empty());

        BadRequestException actualException = Assertions.assertThrows(BadRequestException.class,
                () -> loginServiceImpl.login(userLoginRequest));

        Assertions.assertEquals("Username or password is incorrect. Please try again", actualException.getMessage());
    }

    @Test
    void testLogin_WhenUserFindNotActive_ShouldReturnBadRequestException() throws BadRequestException {
        UserLoginRequest userLoginRequest = UserLoginRequest.builder().username("username").build();

        when(userRepository.findByUsername(userLoginRequest.getUsername())).thenReturn(Optional.of(user));
        when(user.isDeleted()).thenReturn(true);

        BadRequestException actualException = Assertions.assertThrows(BadRequestException.class,
                () -> loginServiceImpl.login(userLoginRequest));

        Assertions.assertEquals("This account has been disabled", actualException.getMessage());
    }

    @Test
    void testLogin_WhenPasswordInvalid_ShouldReturnBadRequestException() throws BadRequestException {
        UserLoginRequest userLoginRequest = UserLoginRequest.builder().username("username").password("123456").build();

        when(userRepository.findByUsername(userLoginRequest.getUsername())).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(userLoginRequest.getPassword(), user.getPassword())).thenReturn(false);

        BadRequestException actualException = Assertions.assertThrows(BadRequestException.class,
                () -> loginServiceImpl.login(userLoginRequest));

        Assertions.assertEquals("Username or password is incorrect. Please try again", actualException.getMessage());
    }

    @Test
    void testLogin_WhenPasswordFirstLogin_ShouldReturnTrue() throws BadRequestException {
        UserLoginRequest userLoginRequest = UserLoginRequest.builder().username("username").password("123456").build();

        when(userRepository.findByUsername(userLoginRequest.getUsername())).thenReturn(Optional.of(user));
        when(user.isDeleted()).thenReturn(false);
        when(passwordEncoder.matches(userLoginRequest.getPassword(), user.getPassword())).thenReturn(true);
        when(passwordEncoder.matches(userLoginRequest.getPassword(),
                passwordEncoder.encode(generatePassword.generatePassword(user))))
                .thenReturn(true);

        UserLoginResponse actual = loginServiceImpl.login(userLoginRequest);

        Assertions.assertEquals(true, actual.getIsFirstLogin());
    }

    @Test
    void testLogin_WhenPasswordValid_ShouldLoginSuccess() throws BadRequestException {
        UserLoginRequest userLoginRequest = UserLoginRequest.builder().username("username").password("123456").build();

        when(userRepository.findByUsername(userLoginRequest.getUsername())).thenReturn(Optional.of(user));
        when(user.isDeleted()).thenReturn(false);
        when(passwordEncoder.matches(userLoginRequest.getPassword(), user.getPassword())).thenReturn(true);
        when(jwtTokenUtil.generateJwtToken(user)).thenReturn("accessToken");

        UserLoginResponse actual = loginServiceImpl.login(userLoginRequest);
        assertThat(actual.getAccessToken(), is("accessToken"));
    }
}