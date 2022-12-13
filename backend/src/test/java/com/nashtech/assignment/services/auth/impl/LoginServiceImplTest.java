package com.nashtech.assignment.services.auth.impl;

import com.nashtech.assignment.data.entities.User;
import com.nashtech.assignment.data.repositories.UserRepository;
import com.nashtech.assignment.dto.request.UserLoginRequest;
import com.nashtech.assignment.dto.response.UserLoginResponse;
import com.nashtech.assignment.exceptions.BadRequestException;
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

    private LoginServiceImpl loginServiceImpl;
    private UserRepository userRepository;
    private JwtTokenUtil jwtTokenUtil;
    private PasswordEncoder passwordEncoder;

    private User user;

    @BeforeEach
    void setUpBeforeTest() {
        user = mock(User.class);
        userRepository = mock(UserRepository.class);
        jwtTokenUtil = mock(JwtTokenUtil.class);
        passwordEncoder = mock(PasswordEncoder.class);
        loginServiceImpl = LoginServiceImpl.builder()
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
    void testLogin_WhenPasswordInvalid_ShouldReturnBadRequestException() throws BadRequestException {
        UserLoginRequest userLoginRequest = UserLoginRequest.builder().username("username").password("123456").build();

        when(userRepository.findByUsername(userLoginRequest.getUsername())).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(userLoginRequest.getPassword(), user.getPassword())).thenReturn(false);

        BadRequestException actualException = Assertions.assertThrows(BadRequestException.class,
                () -> loginServiceImpl.login(userLoginRequest));

        Assertions.assertEquals("Username or password is incorrect. Please try again", actualException.getMessage());
    }

    @Test
    void testLogin_WhenUserFindNotActive_ShouldReturnBadRequestException() throws BadRequestException {
        UserLoginRequest userLoginRequest = UserLoginRequest.builder().username("username").build();

        when(userRepository.findByUsername(userLoginRequest.getUsername())).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(userLoginRequest.getPassword(), user.getPassword())).thenReturn(true);
        when(user.isDeleted()).thenReturn(true);

        BadRequestException actualException = Assertions.assertThrows(BadRequestException.class,
                () -> loginServiceImpl.login(userLoginRequest));

        Assertions.assertEquals("This account has been disabled", actualException.getMessage());
    }

    @Test
    void testLogin_WhenPasswordValid_ShouldLoginSuccess() throws BadRequestException {
        UserLoginRequest userLoginRequest = UserLoginRequest.builder().username("username").password("123456").build();

        when(userRepository.findByUsername(userLoginRequest.getUsername())).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(userLoginRequest.getPassword(), user.getPassword())).thenReturn(true);
        when(user.isDeleted()).thenReturn(false);
        when(jwtTokenUtil.generateJwtToken(user)).thenReturn("accessToken");

        UserLoginResponse actual = loginServiceImpl.login(userLoginRequest);
        assertThat(actual.getAccessToken(), is("accessToken"));
    }
}