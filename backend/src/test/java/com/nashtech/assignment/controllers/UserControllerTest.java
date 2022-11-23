package com.nashtech.assignment.controllers;

import static org.mockito.Mockito.when;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

import com.nashtech.assignment.services.SecurityContextService;
import com.nashtech.assignment.utils.JwtTokenUtil;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nashtech.assignment.AssignmentApplication;
import com.nashtech.assignment.config.CORSConfig;
import com.nashtech.assignment.config.SecurityConfig;
import com.nashtech.assignment.data.constants.EGender;
import com.nashtech.assignment.data.constants.EUserType;
import com.nashtech.assignment.dto.request.CreateNewUserRequest;
import com.nashtech.assignment.dto.response.user.UserResponse;
import com.nashtech.assignment.services.CreateService;

import java.nio.charset.StandardCharsets;

@WebMvcTest(value = UserController.class)
@ContextConfiguration(classes = { AssignmentApplication.class, SecurityConfig.class, CORSConfig.class })
@AutoConfigureMockMvc(addFilters = false)
public class UserControllerTest {
    @Autowired
	private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
	@MockBean
	private CreateService createService;
    @MockBean
    private JwtTokenUtil jwtTokenUtil;
    @MockBean
    private SecurityContextService securityContextService;

    @Test
    void createNewUser_ShouldReturnResponseEntity_WhenDataValidRequest() throws Exception {
        CreateNewUserRequest createNewUserRequest = CreateNewUserRequest.builder()
                .firstName("hau")
                .lastName("doan")
                .dateOfBirth("21/12/2001")
                .joinedDate("17/11/2022")
                .gender(EGender.MALE)
                .type(EUserType.ADMIN)
                .location("hehe").build();
        ArgumentCaptor<CreateNewUserRequest> createNewUserRequestCaptor =
                ArgumentCaptor.forClass(CreateNewUserRequest.class);

        UserResponse response = UserResponse.builder()
                .firstName("hau")
                .lastName("doan")
                .dateOfBirth("21/12/2001")
                .joinedDate("17/11/2022")
                .gender(EGender.MALE)
                .type(EUserType.ADMIN)
                .location("hehe")
                .build();

        when(createService.createNewUser(createNewUserRequestCaptor.capture())).thenReturn(response);

        RequestBuilder request = MockMvcRequestBuilders
                .post("/api/user")
                .content(objectMapper.writeValueAsString(createNewUserRequest))
                .characterEncoding(StandardCharsets.UTF_8)
                .contentType(MediaType.APPLICATION_JSON);

        MvcResult result = mockMvc.perform(request).andReturn();

        assertThat(result.getResponse().getStatus(), is(HttpStatus.OK.value()));
        assertThat(result.getResponse().getContentAsString(), 
            is("{\"username\":null,\"staffCode\":null,\"firstName\":\"hau\",\"lastName\":\"doan\",\"gender\":\"MALE\",\"joinedDate\":\"17/11/2022\",\"dateOfBirth\":\"21/12/2001\",\"type\":\"ADMIN\",\"location\":\"hehe\",\"fullName\":null}"
        ));
    }
}