package com.nashtech.assignment.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nashtech.assignment.AssignmentApplication;
import com.nashtech.assignment.config.CORSConfig;
import com.nashtech.assignment.config.SecurityConfig;
import com.nashtech.assignment.data.constants.EGender;
import com.nashtech.assignment.data.constants.EUserType;
import com.nashtech.assignment.dto.request.user.ChangePasswordFirstRequest;
import com.nashtech.assignment.dto.request.user.ChangePasswordRequest;
import com.nashtech.assignment.dto.request.user.CreateNewUserRequest;
import com.nashtech.assignment.dto.request.user.EditUserRequest;
import com.nashtech.assignment.dto.response.user.UserResponse;
import com.nashtech.assignment.exceptions.BadRequestException;
import com.nashtech.assignment.exceptions.NotFoundException;
import com.nashtech.assignment.services.auth.SecurityContextService;
import com.nashtech.assignment.services.create.CreateUserService;
import com.nashtech.assignment.services.delete.DeleteUserService;
import com.nashtech.assignment.services.edit.EditUserService;
import com.nashtech.assignment.services.get.GetUserService;
import com.nashtech.assignment.utils.JwtTokenUtil;
import org.junit.jupiter.api.BeforeEach;
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
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.*;

@WebMvcTest(value = UserController.class)
@ContextConfiguration(classes = {AssignmentApplication.class, SecurityConfig.class, CORSConfig.class})
@AutoConfigureMockMvc(addFilters = false)
public class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @MockBean
    private EditUserService editUserService;
    @MockBean
    private DeleteUserService deleteUserService;
    @MockBean
    private GetUserService getUserService;
    @MockBean
    private CreateUserService createUserService;
    @MockBean
    private JwtTokenUtil jwtTokenUtil;
    @MockBean
    private SecurityContextService securityContextService;

    private BadRequestException badRequestException;
    private Date dateOfBirth;
    private Date joinedDate;
    private Date date;

    @BeforeEach
    void setup() throws Exception {
        badRequestException = new BadRequestException("error message");
        SimpleDateFormat formatterDate = new SimpleDateFormat("dd/MM/yyyy");
        formatterDate.setTimeZone(TimeZone.getTimeZone("GMT"));
        dateOfBirth = formatterDate.parse("21/12/2001");
        ;
        joinedDate = formatterDate.parse("17/11/2022");
        date = formatterDate.parse("01/01/2001");
    }

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
                .dateOfBirth(dateOfBirth)
                .joinedDate(joinedDate)
                .gender(EGender.MALE)
                .type(EUserType.ADMIN)
                .location("hehe")
                .build();

        when(createUserService.createNewUser(createNewUserRequestCaptor.capture())).thenReturn(response);

        RequestBuilder request = MockMvcRequestBuilders
                .post("/api/user")
                .content(objectMapper.writeValueAsString(createNewUserRequest))
                .characterEncoding(StandardCharsets.UTF_8)
                .contentType(MediaType.APPLICATION_JSON);

        MvcResult result = mockMvc.perform(request).andReturn();

        assertThat(result.getResponse().getStatus(), is(HttpStatus.OK.value()));
        assertThat(result.getResponse().getContentAsString(), is(
                "{\"id\":0,\"username\":null,\"staffCode\":null,\"firstName\":\"hau\",\"lastName\":\"doan\"," +
                        "\"gender\":\"MALE\",\"joinedDate\":\"2022-11-17T00:00:00.000+00:00\"," +
                        "\"dateOfBirth\":\"2001-12-21T00:00:00.000+00:00\",\"type\":\"ADMIN\"," +
                        "\"location\":\"hehe\",\"fullName\":null}"
        ));
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
                .dateOfBirth(dateOfBirth)
                .joinedDate(joinedDate)
                .gender(EGender.FEMALE)
                .staffCode("test")
                .fullName("Linh Ngoc Dam")
                .firstName("Linh")
                .lastName("Ngoc Dam")
                .location("HCM")
                .type(EUserType.STAFF)
                .build();

        when(editUserService.editUserInformation(userRequestCaptor.capture())).thenReturn(userResponse);

        RequestBuilder request = MockMvcRequestBuilders
                .put("/api/user/edit")
                .content(objectMapper.writeValueAsString(userRequest))
                .characterEncoding(StandardCharsets.UTF_8)
                .contentType(MediaType.APPLICATION_JSON);

        MvcResult result = mockMvc.perform(request).andReturn();

        assertThat(result.getResponse().getStatus(), is(HttpStatus.OK.value()));
        assertThat(result.getResponse().getContentAsString(), is(
                "{\"id\":0,\"username\":null,\"staffCode\":\"test\",\"firstName\":\"Linh\",\"lastName\":\"Ngoc Dam\"," +
                        "\"gender\":\"FEMALE\",\"joinedDate\":\"2022-11-17T00:00:00.000+00:00\"," +
                        "\"dateOfBirth\":\"2001-12-21T00:00:00.000+00:00\",\"type\":\"STAFF\",\"location\":\"HCM\"," +
                        "\"fullName\":\"Linh Ngoc Dam\"}"
        ));

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
        when(editUserService.editUserInformation(userRequestCaptor.capture())).thenThrow(notFoundException);

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
        when(editUserService.editUserInformation(userRequestCaptor.capture())).thenThrow(badRequestException);

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
    void deleteUser_WhenSuccessfully_ShouldReturnStatusCode201() throws Exception {

        doNothing().when(deleteUserService).deleteUser("test");
        RequestBuilder request = MockMvcRequestBuilders
                .delete("/api/user")
                .param("staffCode", "test");

        MvcResult result = mockMvc.perform(request).andReturn();

        assertThat(result.getResponse().getStatus(), is(HttpStatus.NO_CONTENT.value()));
    }

    @Test
    void deleteUser_WhenUserNotFound_ShouldThrowNotFoundException() throws Exception {

        doThrow(NotFoundException.class).when(deleteUserService).deleteUser("test");
        RequestBuilder request = MockMvcRequestBuilders
                .delete("/api/user")
                .param("staffCode", "test");

        MvcResult result = mockMvc.perform(request).andReturn();

        assertThat(result.getResponse().getStatus(), is(HttpStatus.NOT_FOUND.value()));
    }

    @Test
    void deleteUser_WhenUserHaveValidAssign_ShouldThrowBadRequestException() throws Exception {

        doThrow(BadRequestException.class).when(deleteUserService).deleteUser("test");
        RequestBuilder request = MockMvcRequestBuilders
                .delete("/api/user")
                .param("staffCode", "test");

        MvcResult result = mockMvc.perform(request).andReturn();

        assertThat(result.getResponse().getStatus(), is(HttpStatus.BAD_REQUEST.value()));
    }

    @Test
    void checkValidUserForDelete_ShouldReturnTrue_WhenDataValid() throws Exception {
        when(deleteUserService.checkValidUser("test")).thenReturn(true);
        RequestBuilder request = MockMvcRequestBuilders
                .get("/api/user/check-user")
                .param("staffCode", "test");

        MvcResult result = mockMvc.perform(request).andReturn();

        assertThat(result.getResponse().getStatus(), is(HttpStatus.OK.value()));
        assertThat(result.getResponse().getContentAsString(), is("true"));
    }

    @Test
    void checkValidUserForDelete_WhenUserHaveValidAssign_ShouldThrowBadRequestException() throws Exception {

        doThrow(BadRequestException.class).when(deleteUserService).checkValidUser("test");
        RequestBuilder request = MockMvcRequestBuilders
                .get("/api/user/check-user")
                .param("staffCode", "test");

        MvcResult result = mockMvc.perform(request).andReturn();

        assertThat(result.getResponse().getStatus(), is(HttpStatus.BAD_REQUEST.value()));
    }

    @Test
    void checkValidUser_WhenUserNotFound_ShouldThrowNotFoundException() throws Exception {

        doThrow(NotFoundException.class).when(deleteUserService).checkValidUser("test");
        RequestBuilder request = MockMvcRequestBuilders
                .get("/api/user/check-user")
                .param("staffCode", "test");

        MvcResult result = mockMvc.perform(request).andReturn();

        assertThat(result.getResponse().getStatus(), is(HttpStatus.NOT_FOUND.value()));
    }

    @Test
    void testChangePassword_WhenPasswordNoChangeOrIncorrect_ShouldReturnException() throws Exception {
        ChangePasswordRequest changePasswordRequest = ChangePasswordRequest.builder()
                .oldPassword("123456")
                .newPassword("123456").build();

        ArgumentCaptor<ChangePasswordRequest> changePasswordRequestCaptor = ArgumentCaptor
                .forClass(ChangePasswordRequest.class);

        when(editUserService.changePassword(changePasswordRequestCaptor.capture())).thenThrow(badRequestException);

        RequestBuilder requestBuilder = MockMvcRequestBuilders.put("/api/user/change-password")
                .content(objectMapper.writeValueAsString(changePasswordRequest))
                .contentType(MediaType.APPLICATION_JSON);

        MockHttpServletResponse actual = mockMvc.perform(requestBuilder).andReturn().getResponse();

        assertThat(actual.getStatus(), is(HttpStatus.BAD_REQUEST.value()));
        assertThat(actual.getContentAsString(), is("{\"message\":\"error message\"}"));
    }

    @Test
    void testChangePassword_WhenDataValid_ShouldReturnData() throws Exception {
        ChangePasswordRequest changePasswordRequest = ChangePasswordRequest.builder()
                .oldPassword("123456")
                .newPassword("654321").build();

        ArgumentCaptor<ChangePasswordRequest> changePasswordRequestCaptor = ArgumentCaptor
                .forClass(ChangePasswordRequest.class);

        UserResponse userResponse = UserResponse.builder().fullName("Test").build();

        when(editUserService.changePassword(changePasswordRequestCaptor.capture()))
                .thenReturn(userResponse);

        RequestBuilder requestBuilder = MockMvcRequestBuilders.put("/api/user/change-password")
                .content(objectMapper.writeValueAsString(changePasswordRequest))
                .contentType(MediaType.APPLICATION_JSON);

        MockHttpServletResponse actual = mockMvc.perform(requestBuilder).andReturn().getResponse();

        assertThat(actual.getContentAsString(), is(
                "{\"id\":0,\"username\":null,\"staffCode\":null,\"firstName\":null,\"lastName\":null," +
                        "\"gender\":null,\"joinedDate\":null,\"dateOfBirth\":null,\"type\":null,\"location\":null," +
                        "\"fullName\":\"Test\"}"
        ));
    }

    @Test
    void testChangePasswordFirst_WhenPasswordNoChange_ShouldReturnException() throws Exception {
        ChangePasswordFirstRequest changeFirstRequest = ChangePasswordFirstRequest.builder().newPassword("123456")
                .build();

        ArgumentCaptor<ChangePasswordFirstRequest> changeFirstCaptor = ArgumentCaptor
                .forClass(ChangePasswordFirstRequest.class);

        when(editUserService.changePasswordFirst(changeFirstCaptor.capture())).thenThrow(badRequestException);

        RequestBuilder requestBuilder = MockMvcRequestBuilders.put("/api/user/change-password/first")
                .content(objectMapper.writeValueAsString(changeFirstRequest))
                .contentType(MediaType.APPLICATION_JSON);

        MockHttpServletResponse actual = mockMvc.perform(requestBuilder).andReturn().getResponse();

        assertThat(actual.getStatus(), is(HttpStatus.BAD_REQUEST.value()));
        assertThat(actual.getContentAsString(), is("{\"message\":\"error message\"}"));
    }

    @Test
    void testChangePasswordFirst_WhenDataValid_ShouldReturnData() throws Exception {
        ChangePasswordFirstRequest changeFirstRequest = ChangePasswordFirstRequest.builder()
                .newPassword("654f321").build();

        ArgumentCaptor<ChangePasswordFirstRequest> changeFirstCaptor = ArgumentCaptor
                .forClass(ChangePasswordFirstRequest.class);

        UserResponse userResponse = UserResponse.builder().fullName("Test").build();

        when(editUserService.changePasswordFirst(changeFirstCaptor.capture())).thenReturn(userResponse);

        RequestBuilder requestBuilder = MockMvcRequestBuilders.put("/api/user/change-password/first")
                .content(objectMapper.writeValueAsString(changeFirstRequest))
                .contentType(MediaType.APPLICATION_JSON);

        MockHttpServletResponse actual = mockMvc.perform(requestBuilder).andReturn().getResponse();

        assertThat(actual.getContentAsString(), is(
                "{\"id\":0,\"username\":null,\"staffCode\":null,\"firstName\":null,\"lastName\":null,\"gender\":null," +
                        "\"joinedDate\":null,\"dateOfBirth\":null,\"type\":null,\"location\":null,\"fullName\":\"Test\"}"
        ));
    }

    @Test
    void testGetAllUsers_ShouldReturnData() throws Exception {
        UserResponse userResponse = UserResponse.builder()
                .username("username")
                .staffCode("staffCode")
                .firstName("firstName")
                .lastName("lastName")
                .gender(EGender.MALE)
                .joinedDate(date)
                .dateOfBirth(date)
                .type(EUserType.ADMIN)
                .location("location")
                .fullName("fullName").build();
        List<UserResponse> response = new ArrayList<>();
        response.add(userResponse);

        when(getUserService.getAllUsers()).thenReturn(response);

        RequestBuilder requestBuilder = MockMvcRequestBuilders.get("/api/user");

        MockHttpServletResponse actual = mockMvc.perform(requestBuilder).andReturn().getResponse();

        assertThat(actual.getStatus(), is(HttpStatus.OK.value()));
        assertThat(actual.getContentAsString(), is(
                "[{\"id\":0,\"username\":\"username\",\"staffCode\":\"staffCode\",\"firstName\":\"firstName\"," +
                        "\"lastName\":\"lastName\",\"gender\":\"MALE\",\"joinedDate\":\"2001-01-01T00:00:00.000+00:00\"," +
                        "\"dateOfBirth\":\"2001-01-01T00:00:00.000+00:00\",\"type\":\"ADMIN\",\"location\":\"location\"," +
                        "\"fullName\":\"fullName\"}]"
        ));
    }
}