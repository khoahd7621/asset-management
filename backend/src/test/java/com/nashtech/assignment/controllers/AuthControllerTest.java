package com.nashtech.assignment.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nashtech.assignment.AssignmentApplication;
import com.nashtech.assignment.config.CORSConfig;
import com.nashtech.assignment.config.SecurityConfig;
import com.nashtech.assignment.dto.request.UserLoginRequest;
import com.nashtech.assignment.dto.response.UserLoginResponse;
import com.nashtech.assignment.exceptions.BadRequestException;
import com.nashtech.assignment.services.auth.LoginService;
import com.nashtech.assignment.services.auth.SecurityContextService;
import com.nashtech.assignment.utils.JwtTokenUtil;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.when;

@WebMvcTest(value = AuthController.class)
@ContextConfiguration(classes = {AssignmentApplication.class, SecurityConfig.class, CORSConfig.class})
@AutoConfigureMockMvc(addFilters = false)
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @MockBean
    private LoginService loginService;
    @MockBean
    private JwtTokenUtil jwtTokenUtil;
    @MockBean
    private SecurityContextService securityContextService;

    @Test
    void login_WhenDataValid_ShouldLoginSuccess() throws Exception {
        UserLoginRequest userLoginRequest = UserLoginRequest.builder()
                .username("username")
                .password("123456").build();
        UserLoginResponse userLoginResponse = UserLoginResponse.builder()
                .accessToken("").isFirstLogin(true).build();
        ArgumentCaptor<UserLoginRequest> userLoginRequestCaptor = ArgumentCaptor.forClass(UserLoginRequest.class);

        when(loginService.login(userLoginRequestCaptor.capture())).thenReturn(userLoginResponse);

        RequestBuilder requestBuilder = MockMvcRequestBuilders.post("/api/login")
                .content(objectMapper.writeValueAsString(userLoginRequest))
                .contentType(MediaType.APPLICATION_JSON);

        MockHttpServletResponse actual = mockMvc.perform(requestBuilder).andReturn().getResponse();
        assertThat(actual.getContentAsString(), is("{\"accessToken\":\"\",\"isFirstLogin\":true}"));
    }

    @Test
    void login_WhenDataInvalid_ShouldReturnException() throws Exception {
        UserLoginRequest userLoginRequest = UserLoginRequest.builder()
                .username("username")
                .password("123456").build();
        ArgumentCaptor<UserLoginRequest> userLoginRequestCaptor = ArgumentCaptor
                .forClass(UserLoginRequest.class);
        BadRequestException expected = new BadRequestException("Error message");

        when(loginService.login(userLoginRequestCaptor.capture())).thenThrow(expected);

        RequestBuilder requestBuilder = MockMvcRequestBuilders.post("/api/login")
                .content(objectMapper.writeValueAsString(userLoginRequest))
                .contentType(MediaType.APPLICATION_JSON);

        MockHttpServletResponse actual = mockMvc.perform(requestBuilder).andReturn()
                .getResponse();
        assertThat(actual.getStatus(), is(HttpStatus.BAD_REQUEST.value()));
        assertThat(actual.getContentAsString(), is("{\"message\":\"Error message\"}"));
    }

}
