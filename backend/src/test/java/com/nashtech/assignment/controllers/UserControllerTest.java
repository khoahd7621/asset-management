package com.nashtech.assignment.controllers;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;

import java.nio.charset.StandardCharsets;

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
import com.nashtech.assignment.dto.request.user.CreateNewUserRequest;
import com.nashtech.assignment.dto.request.user.EditUserRequest;
import com.nashtech.assignment.dto.response.user.UserResponse;
import com.nashtech.assignment.exceptions.BadRequestException;
import com.nashtech.assignment.exceptions.NotFoundException;
import com.nashtech.assignment.services.CreateService;
import com.nashtech.assignment.services.DeleteService;
import com.nashtech.assignment.services.EditService;
import com.nashtech.assignment.services.SecurityContextService;
import com.nashtech.assignment.utils.JwtTokenUtil;

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
    private EditService editService;
    @MockBean 
    private DeleteService deleteService;
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
        ArgumentCaptor<CreateNewUserRequest> createNewUserRequestCaptor = ArgumentCaptor
                .forClass(CreateNewUserRequest.class);

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
                is("{\"username\":null,\"staffCode\":null,\"firstName\":\"hau\",\"lastName\":\"doan\",\"gender\":\"MALE\",\"joinedDate\":\"17/11/2022\",\"dateOfBirth\":\"21/12/2001\",\"type\":\"ADMIN\",\"location\":\"hehe\",\"fullName\":null}"));
    }

    @Test
    void editUser_ShouldReturnUserResponseObject_WhenDataValid() throws Exception {
        EditUserRequest userRequest = EditUserRequest.builder()
                .dateOfBirth("11/26/2001")
                .joinedDate("28/11/2022")
                .gender(EGender.FEMALE)
                .type(EUserType.STAFF)
                .staffCode("test")
                .build();

        ArgumentCaptor<EditUserRequest> userRequestCaptor = ArgumentCaptor.forClass(EditUserRequest.class);

        UserResponse userResponse = UserResponse.builder()
                .dateOfBirth("11/26/2001")
                .joinedDate("28/11/2022")
                .gender(EGender.FEMALE)
                .staffCode("test")
                .fullName("Linh Ngoc Dam")
                .firstName("Linh")
                .lastName("Ngoc Dam")
                .location("HCM")
                .type(EUserType.STAFF)
                .build();

        when(editService.editUserInformation(userRequestCaptor.capture())).thenReturn(userResponse);

        RequestBuilder request = MockMvcRequestBuilders
                .put("/api/user/edit")
                .content(objectMapper.writeValueAsString(userRequest))
                .characterEncoding(StandardCharsets.UTF_8)
                .contentType(MediaType.APPLICATION_JSON);

        MvcResult result = mockMvc.perform(request).andReturn();

        assertThat(result.getResponse().getStatus(), is(HttpStatus.OK.value()));
        assertThat(result.getResponse().getContentAsString(),
                is("{\"username\":null,\"staffCode\":\"test\",\"firstName\":\"Linh\",\"lastName\":\"Ngoc Dam\",\"gender\":\"FEMALE\",\"joinedDate\":\"28/11/2022\",\"dateOfBirth\":\"11/26/2001\",\"type\":\"STAFF\",\"location\":\"HCM\",\"fullName\":\"Linh Ngoc Dam\"}"));

    }

    @Test
    void editUser_ShouldThrowNotFoundException_WhenStaffCodeNotExist() throws Exception {
        EditUserRequest userRequest = EditUserRequest.builder()
                .dateOfBirth("11/26/2001")
                .joinedDate("28/11/2022")
                .gender(EGender.FEMALE)
                .type(EUserType.STAFF)
                .staffCode("test")
                .build();

        ArgumentCaptor<EditUserRequest> userRequestCaptor = ArgumentCaptor.forClass(EditUserRequest.class);

        NotFoundException notFoundException = new NotFoundException("Message");
        when(editService.editUserInformation(userRequestCaptor.capture())).thenThrow(notFoundException);

        RequestBuilder request = MockMvcRequestBuilders
                .put("/api/user/edit")
                .content(objectMapper.writeValueAsString(userRequest))
                .characterEncoding(StandardCharsets.UTF_8)
                .contentType(MediaType.APPLICATION_JSON);

        MvcResult result = mockMvc.perform(request).andReturn();

        assertThat(result.getResponse().getStatus(), is(HttpStatus.NOT_FOUND.value()));
        assertThat(result.getResponse().getContentAsString(),
                is("{\"message\":\"Message\"}"));

    }

    @Test
    void editUser_ShouldBadRequestException_WhenDateNotValid() throws Exception {
        EditUserRequest userRequest = EditUserRequest.builder()
                .dateOfBirth("11/26/2001")
                .joinedDate("28/11/2022")
                .gender(EGender.FEMALE)
                .type(EUserType.STAFF)
                .staffCode("test")
                .build();

        ArgumentCaptor<EditUserRequest> userRequestCaptor = ArgumentCaptor.forClass(EditUserRequest.class);

        BadRequestException badRequestException = new BadRequestException("Message");
        when(editService.editUserInformation(userRequestCaptor.capture())).thenThrow(badRequestException);

        RequestBuilder request = MockMvcRequestBuilders
                .put("/api/user/edit")
                .content(objectMapper.writeValueAsString(userRequest))
                .characterEncoding(StandardCharsets.UTF_8)
                .contentType(MediaType.APPLICATION_JSON);

        MvcResult result = mockMvc.perform(request).andReturn();

        assertThat(result.getResponse().getStatus(), is(HttpStatus.BAD_REQUEST.value()));
        assertThat(result.getResponse().getContentAsString(),
                is("{\"message\":\"Message\"}"));

    }

    @Test
    void deleteUser_WhenSuccesfull_ShouldReturnStatusCode201() throws Exception {

        doNothing().when(deleteService).deleteUser("test");
        RequestBuilder request = MockMvcRequestBuilders
                .delete("/api/user")
                .param("staffCode", "test");

        MvcResult result = mockMvc.perform(request).andReturn();

        assertThat(result.getResponse().getStatus(), is(HttpStatus.NO_CONTENT.value()));
    }
    @Test
    void deleteUser_WhenUserNotFound_ShouldThrowNotFoundException() throws Exception {

        doThrow(NotFoundException.class).when(deleteService).deleteUser("test");
        RequestBuilder request = MockMvcRequestBuilders
                .delete("/api/user")
                .param("staffCode", "test");

        MvcResult result = mockMvc.perform(request).andReturn();

        assertThat(result.getResponse().getStatus(), is(HttpStatus.NOT_FOUND.value()));
    }
    @Test
    void deleteUser_WhenUserHaveValidAssign_ShouldThrowBadRequestException() throws Exception {

        doThrow(BadRequestException.class).when(deleteService).deleteUser("test");
        RequestBuilder request = MockMvcRequestBuilders
                .delete("/api/user")
                .param("staffCode", "test");

        MvcResult result = mockMvc.perform(request).andReturn();

        assertThat(result.getResponse().getStatus(), is(HttpStatus.BAD_REQUEST.value()));
    }

    @Test
    void checkValidUserForDelete_ShouldReturnTrue_WhenDataValid() throws Exception {
        when(deleteService.checkValidUser("test")).thenReturn(true);
        RequestBuilder request = MockMvcRequestBuilders
                .get("/api/user/check-user")
                .param("staffCode", "test");

        MvcResult result = mockMvc.perform(request).andReturn();

        assertThat(result.getResponse().getStatus(), is(HttpStatus.OK.value()));
        assertThat(result.getResponse().getContentAsString(),is("true"));
    }

    @Test
    void checkValidUserForDelete_WhenUserHaveValidAssign_ShouldThrowBadRequestException() throws Exception {

        doThrow(BadRequestException.class).when(deleteService).checkValidUser("test");
        RequestBuilder request = MockMvcRequestBuilders
                .get("/api/user/check-user")
                .param("staffCode", "test");

        MvcResult result = mockMvc.perform(request).andReturn();

        assertThat(result.getResponse().getStatus(), is(HttpStatus.BAD_REQUEST.value()));
    }

    @Test
    void checkValidUser_WhenUserNotFound_ShouldThrowNotFoundException() throws Exception {

        doThrow(NotFoundException.class).when(deleteService).checkValidUser("test");
        RequestBuilder request = MockMvcRequestBuilders
                .get("/api/user/check-user")
                .param("staffCode", "test");

        MvcResult result = mockMvc.perform(request).andReturn();

        assertThat(result.getResponse().getStatus(), is(HttpStatus.NOT_FOUND.value()));
    }
}