package com.nashtech.assignment.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nashtech.assignment.AssignmentApplication;
import com.nashtech.assignment.config.CORSConfig;
import com.nashtech.assignment.config.SecurityConfig;
import com.nashtech.assignment.data.constants.Constants;
import com.nashtech.assignment.data.constants.EGender;
import com.nashtech.assignment.data.constants.EUserType;
import com.nashtech.assignment.dto.request.user.*;
import com.nashtech.assignment.dto.response.PaginationResponse;
import com.nashtech.assignment.dto.response.user.UserResponse;
import com.nashtech.assignment.exceptions.BadRequestException;
import com.nashtech.assignment.exceptions.ForbiddenException;
import com.nashtech.assignment.exceptions.NotFoundException;
import com.nashtech.assignment.services.auth.SecurityContextService;
import com.nashtech.assignment.services.search.SearchUserService;
import com.nashtech.assignment.services.user.UserService;
import com.nashtech.assignment.services.validation.ValidationUserService;
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
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @MockBean
    private SearchUserService searchUserService;
    @MockBean
    private UserService userService;
    @MockBean
    private ValidationUserService validationUserService;
    @MockBean
    private JwtTokenUtil jwtTokenUtil;
    @MockBean
    private SecurityContextService securityContextService;

    private BadRequestException badRequestException;
    private Date dateOfBirth;
    private Date joinedDate;
    private UserResponse userResponse;

    @BeforeEach
    void setup() throws Exception {
        badRequestException = new BadRequestException("error message");
        SimpleDateFormat formatterDate = new SimpleDateFormat(Constants.DATE_FORMAT);
        formatterDate.setTimeZone(TimeZone.getTimeZone("GMT"));
        dateOfBirth = formatterDate.parse("21/12/2001");
        joinedDate = formatterDate.parse("17/11/2022");
        Date date = formatterDate.parse("01/01/2001");
        userResponse = UserResponse.builder()
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
                .location("location").build();
        ArgumentCaptor<CreateNewUserRequest> createNewUserRequestCaptor = ArgumentCaptor
                .forClass(CreateNewUserRequest.class);

        UserResponse response = UserResponse.builder()
                .firstName("hau")
                .lastName("doan")
                .dateOfBirth(dateOfBirth)
                .joinedDate(joinedDate)
                .gender(EGender.MALE)
                .type(EUserType.ADMIN)
                .location("location")
                .build();

        when(userService.createNewUser(createNewUserRequestCaptor.capture())).thenReturn(response);

        RequestBuilder request = MockMvcRequestBuilders.post("/api/user")
                .content(objectMapper.writeValueAsString(createNewUserRequest))
                .characterEncoding(StandardCharsets.UTF_8)
                .contentType(MediaType.APPLICATION_JSON);

        MvcResult result = mockMvc.perform(request).andReturn();

        assertThat(result.getResponse().getStatus(), is(HttpStatus.OK.value()));
        assertThat(result.getResponse().getContentAsString(), is(
                "{\"id\":0,\"username\":null,\"staffCode\":null,\"firstName\":\"hau\",\"lastName\":\"doan\"," +
                        "\"gender\":\"MALE\",\"joinedDate\":\"2022-11-17T00:00:00.000+00:00\"," +
                        "\"dateOfBirth\":\"2001-12-21T00:00:00.000+00:00\",\"type\":\"ADMIN\"," +
                        "\"location\":\"location\",\"fullName\":null,\"firstLogin\":false}"
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
                .fullName("Doan Ngoc Dam")
                .firstName("Doan")
                .lastName("Ngoc Dam")
                .location("HCM")
                .type(EUserType.STAFF)
                .build();

        when(userService.editUserInformation(userRequestCaptor.capture())).thenReturn(userResponse);

        RequestBuilder request = MockMvcRequestBuilders.put("/api/user/edit")
                .content(objectMapper.writeValueAsString(userRequest))
                .characterEncoding(StandardCharsets.UTF_8)
                .contentType(MediaType.APPLICATION_JSON);
        MvcResult result = mockMvc.perform(request).andReturn();

        assertThat(result.getResponse().getStatus(), is(HttpStatus.OK.value()));
        assertThat(result.getResponse().getContentAsString(), is(
                "{\"id\":0,\"username\":null,\"staffCode\":\"test\",\"firstName\":\"Doan\",\"lastName\":\"Ngoc Dam\"," +
                        "\"gender\":\"FEMALE\",\"joinedDate\":\"2022-11-17T00:00:00.000+00:00\"," +
                        "\"dateOfBirth\":\"2001-12-21T00:00:00.000+00:00\",\"type\":\"STAFF\",\"location\":\"HCM\"," +
                        "\"fullName\":\"Doan Ngoc Dam\",\"firstLogin\":false}"
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

        when(userService.editUserInformation(userRequestCaptor.capture())).thenThrow(notFoundException);

        RequestBuilder request = MockMvcRequestBuilders.put("/api/user/edit")
                .content(objectMapper.writeValueAsString(userRequest))
                .characterEncoding(StandardCharsets.UTF_8)
                .contentType(MediaType.APPLICATION_JSON);
        MvcResult result = mockMvc.perform(request).andReturn();

        assertThat(result.getResponse().getStatus(), is(HttpStatus.NOT_FOUND.value()));
        assertThat(result.getResponse().getContentAsString(), is("{\"message\":\"Message\"}"));
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

        when(userService.editUserInformation(userRequestCaptor.capture())).thenThrow(badRequestException);

        RequestBuilder request = MockMvcRequestBuilders.put("/api/user/edit")
                .content(objectMapper.writeValueAsString(userRequest))
                .characterEncoding(StandardCharsets.UTF_8)
                .contentType(MediaType.APPLICATION_JSON);
        MvcResult result = mockMvc.perform(request).andReturn();

        assertThat(result.getResponse().getStatus(), is(HttpStatus.BAD_REQUEST.value()));
        assertThat(result.getResponse().getContentAsString(),
                is("{\"message\":\"Message\"}"));

    }

    @Test
    void deleteUser_WhenSuccessfully_ShouldReturnStatusCode204() throws Exception {
        doNothing().when(userService).deleteUser("test");

        RequestBuilder request = MockMvcRequestBuilders.delete("/api/user")
                .param("staffCode", "test");
        MvcResult result = mockMvc.perform(request).andReturn();

        assertThat(result.getResponse().getStatus(), is(HttpStatus.NO_CONTENT.value()));
    }

    @Test
    void deleteUser_WhenUserNotFound_ShouldThrowNotFoundException() throws Exception {
        doThrow(NotFoundException.class).when(userService).deleteUser("test");

        RequestBuilder request = MockMvcRequestBuilders.delete("/api/user")
                .param("staffCode", "test");
        MvcResult result = mockMvc.perform(request).andReturn();

        assertThat(result.getResponse().getStatus(), is(HttpStatus.NOT_FOUND.value()));
    }

    @Test
    void deleteUser_WhenUserHaveValidAssign_ShouldThrowBadRequestException() throws Exception {
        doThrow(BadRequestException.class).when(userService).deleteUser("test");

        RequestBuilder request = MockMvcRequestBuilders.delete("/api/user")
                .param("staffCode", "test");
        MvcResult result = mockMvc.perform(request).andReturn();

        assertThat(result.getResponse().getStatus(), is(HttpStatus.BAD_REQUEST.value()));
    }

    @Test
    void checkValidUserForDelete_ShouldReturnTrue_WhenDataValid() throws Exception {
        when(validationUserService.checkValidUserForDelete("test")).thenReturn(true);

        RequestBuilder request = MockMvcRequestBuilders.get("/api/user/check-user")
                .param("staffCode", "test");
        MvcResult result = mockMvc.perform(request).andReturn();

        assertThat(result.getResponse().getStatus(), is(HttpStatus.OK.value()));
        assertThat(result.getResponse().getContentAsString(), is("true"));
    }

    @Test
    void checkValidUserForDelete_WhenUserHaveValidAssign_ShouldThrowBadRequestException() throws Exception {
        doThrow(BadRequestException.class).when(validationUserService).checkValidUserForDelete("test");

        RequestBuilder request = MockMvcRequestBuilders.get("/api/user/check-user")
                .param("staffCode", "test");
        MvcResult result = mockMvc.perform(request).andReturn();

        assertThat(result.getResponse().getStatus(), is(HttpStatus.BAD_REQUEST.value()));
    }

    @Test
    void checkValidUser_WhenUserNotFound_ShouldThrowNotFoundException() throws Exception {
        doThrow(NotFoundException.class).when(validationUserService).checkValidUserForDelete("test");

        RequestBuilder request = MockMvcRequestBuilders.get("/api/user/check-user")
                .param("staffCode", "test");
        MvcResult result = mockMvc.perform(request).andReturn();

        assertThat(result.getResponse().getStatus(), is(HttpStatus.NOT_FOUND.value()));
    }

    @Test
    void changePassword_WhenPasswordNoChangeOrIncorrect_ShouldReturnException() throws Exception {
        ChangePasswordRequest changePasswordRequest = ChangePasswordRequest.builder()
                .oldPassword("123456")
                .newPassword("123456").build();
        ArgumentCaptor<ChangePasswordRequest> changePasswordRequestCaptor = ArgumentCaptor
                .forClass(ChangePasswordRequest.class);

        when(userService.changePassword(changePasswordRequestCaptor.capture())).thenThrow(badRequestException);

        RequestBuilder requestBuilder = MockMvcRequestBuilders.put("/api/user/change-password")
                .content(objectMapper.writeValueAsString(changePasswordRequest))
                .contentType(MediaType.APPLICATION_JSON);
        MockHttpServletResponse actual = mockMvc.perform(requestBuilder).andReturn().getResponse();

        assertThat(actual.getStatus(), is(HttpStatus.BAD_REQUEST.value()));
        assertThat(actual.getContentAsString(), is("{\"message\":\"error message\"}"));
    }

    @Test
    void changePassword_WhenDataValid_ShouldReturnData() throws Exception {
        ChangePasswordRequest changePasswordRequest = ChangePasswordRequest.builder()
                .oldPassword("123456")
                .newPassword("654321").build();
        ArgumentCaptor<ChangePasswordRequest> changePasswordRequestCaptor = ArgumentCaptor
                .forClass(ChangePasswordRequest.class);
        UserResponse userResponse = UserResponse.builder().fullName("Test").build();

        when(userService.changePassword(changePasswordRequestCaptor.capture()))
                .thenReturn(userResponse);

        RequestBuilder requestBuilder = MockMvcRequestBuilders.put("/api/user/change-password")
                .content(objectMapper.writeValueAsString(changePasswordRequest))
                .contentType(MediaType.APPLICATION_JSON);

        MockHttpServletResponse actual = mockMvc.perform(requestBuilder).andReturn().getResponse();

        assertThat(actual.getContentAsString(), is(
                "{\"id\":0,\"username\":null,\"staffCode\":null,\"firstName\":null,\"lastName\":null," +
                        "\"gender\":null,\"joinedDate\":null,\"dateOfBirth\":null,\"type\":null,\"location\":null," +
                        "\"fullName\":\"Test\",\"firstLogin\":false}"
        ));
    }

    @Test
    void changePasswordFirst_WhenPasswordNoChange_ShouldReturnException() throws Exception {
        ChangePasswordFirstRequest changeFirstRequest = ChangePasswordFirstRequest.builder()
                .newPassword("123456").build();
        ArgumentCaptor<ChangePasswordFirstRequest> changeFirstCaptor = ArgumentCaptor
                .forClass(ChangePasswordFirstRequest.class);

        when(userService.changePasswordFirst(changeFirstCaptor.capture())).thenThrow(badRequestException);

        RequestBuilder requestBuilder = MockMvcRequestBuilders.put("/api/user/change-password/first")
                .content(objectMapper.writeValueAsString(changeFirstRequest))
                .contentType(MediaType.APPLICATION_JSON);
        MockHttpServletResponse actual = mockMvc.perform(requestBuilder).andReturn().getResponse();

        assertThat(actual.getStatus(), is(HttpStatus.BAD_REQUEST.value()));
        assertThat(actual.getContentAsString(), is("{\"message\":\"error message\"}"));
    }

    @Test
    void changePasswordFirst_WhenDataValid_ShouldReturnData() throws Exception {
        ChangePasswordFirstRequest changeFirstRequest = ChangePasswordFirstRequest.builder()
                .newPassword("654f321").build();
        ArgumentCaptor<ChangePasswordFirstRequest> changeFirstCaptor = ArgumentCaptor
                .forClass(ChangePasswordFirstRequest.class);
        UserResponse userResponse = UserResponse.builder().fullName("Test").build();

        when(userService.changePasswordFirst(changeFirstCaptor.capture())).thenReturn(userResponse);

        RequestBuilder requestBuilder = MockMvcRequestBuilders.put("/api/user/change-password/first")
                .content(objectMapper.writeValueAsString(changeFirstRequest))
                .contentType(MediaType.APPLICATION_JSON);
        MockHttpServletResponse actual = mockMvc.perform(requestBuilder).andReturn().getResponse();

        assertThat(actual.getContentAsString(), is(
                "{\"id\":0,\"username\":null,\"staffCode\":null,\"firstName\":null,\"lastName\":null,\"gender\":null," +
                        "\"joinedDate\":null,\"dateOfBirth\":null,\"type\":null,\"location\":null,\"fullName\":\"Test\",\"firstLogin\":false}"
        ));
    }

    @Test
    void searchAllUsersByKeyWordInTypesWithPagination_WhenValidDataRequest_ShouldReturnData() throws Exception {
        List<EUserType> types = new ArrayList<>();
        types.add(EUserType.ADMIN);
        List<UserResponse> userResponseList = new ArrayList<>();
        userResponseList.add(userResponse);
        PaginationResponse<List<UserResponse>> response = PaginationResponse.<List<UserResponse>>builder()
                .data(userResponseList)
                .totalPage(1)
                .totalRow(1).build();
        ArgumentCaptor<SearchUserRequest> searchUserRequestCaptor = ArgumentCaptor.forClass(SearchUserRequest.class);

        when(searchUserService.searchAllUsersByKeyWordInTypesWithPagination(searchUserRequestCaptor.capture()))
                .thenReturn(response);

        RequestBuilder requestBuilder = MockMvcRequestBuilders.get("/api/user/search")
                .param("key-word", "keyword")
                .param("types", EUserType.ADMIN.toString())
                .param("limit", "20")
                .param("page", "0")
                .param("sort-field", "firstName")
                .param("sort-type", "ASC");
        MockHttpServletResponse actual = mockMvc.perform(requestBuilder).andReturn().getResponse();

        SearchUserRequest searchUserRequestActual = searchUserRequestCaptor.getValue();
        assertThat(searchUserRequestActual.getKeyword(), is("keyword"));
        assertThat(searchUserRequestActual.getTypes(), is(types));
        assertThat(searchUserRequestActual.getLimit(), is(20));
        assertThat(searchUserRequestActual.getPage(), is(0));
        assertThat(searchUserRequestActual.getSortField(), is("firstName"));
        assertThat(searchUserRequestActual.getSortType(), is("ASC"));
        assertThat(actual.getStatus(), is(HttpStatus.OK.value()));
        assertThat(actual.getContentAsString(), is(
                "{\"data\":[{\"id\":0,\"username\":\"username\",\"staffCode\":\"staffCode\"," +
                        "\"firstName\":\"firstName\",\"lastName\":\"lastName\",\"gender\":\"MALE\"," +
                        "\"joinedDate\":\"2001-01-01T00:00:00.000+00:00\",\"dateOfBirth\":\"2001-01-01T00:00:00.000+00:00\"," +
                        "\"type\":\"ADMIN\",\"location\":\"location\",\"fullName\":\"fullName\",\"firstLogin\":false}]," +
                        "\"totalPage\":1,\"totalRow\":1}"));
    }

    @Test
    void viewUserDetails_ShouldReturnUserResponse() throws Exception {
        when(userService.viewUserDetails("test")).thenReturn(userResponse);

        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .get("/api/user/get/{staffCode}", "test");
        MvcResult result = mockMvc.perform(requestBuilder).andReturn();

        assertThat(result.getResponse().getContentAsString(), is(
                "{\"id\":0,\"username\":\"username\",\"staffCode\":\"staffCode\"," +
                        "\"firstName\":\"firstName\",\"lastName\":\"lastName\",\"gender\":\"MALE\"," +
                        "\"joinedDate\":\"2001-01-01T00:00:00.000+00:00\",\"dateOfBirth\":\"2001-01-01T00:00:00.000+00:00\"," +
                        "\"type\":\"ADMIN\",\"location\":\"location\",\"fullName\":\"fullName\",\"firstLogin\":false}"
        ));
    }

    @Test
    void getCurrentUserLoggedInInformation_WhenUserNameNotMatch_ThrowForbiddenException() throws Exception {
        doThrow(ForbiddenException.class).when(userService).getCurrentUserLoggedInInformation("username");

        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .get("/api/user/{username}/current", "username");
        MvcResult result = mockMvc.perform(requestBuilder).andReturn();

        assertThat(result.getResponse().getStatus(), is(HttpStatus.FORBIDDEN.value()));
        assertThat(result.getResponse().getContentAsString(), is("{\"message\":null}"));
    }

    @Test
    void getCurrentUserLoggedInInformation_WhenDataValid_ShouldReturnUserResponse() throws Exception {
        when(userService.getCurrentUserLoggedInInformation("username")).thenReturn(userResponse);

        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .get("/api/user/{username}/current", "username");
        MvcResult result = mockMvc.perform(requestBuilder).andReturn();

        assertThat(result.getResponse().getStatus(), is(HttpStatus.OK.value()));
        assertThat(result.getResponse().getContentAsString(), is(
                "{\"id\":0,\"username\":\"username\",\"staffCode\":\"staffCode\"," +
                        "\"firstName\":\"firstName\",\"lastName\":\"lastName\",\"gender\":\"MALE\"," +
                        "\"joinedDate\":\"2001-01-01T00:00:00.000+00:00\",\"dateOfBirth\":\"2001-01-01T00:00:00.000+00:00\"," +
                        "\"type\":\"ADMIN\",\"location\":\"location\",\"fullName\":\"fullName\",\"firstLogin\":false}"
        ));
    }

}