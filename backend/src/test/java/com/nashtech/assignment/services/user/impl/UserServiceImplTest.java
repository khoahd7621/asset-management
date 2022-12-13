package com.nashtech.assignment.services.user.impl;

import com.nashtech.assignment.data.constants.Constants;
import com.nashtech.assignment.data.constants.EGender;
import com.nashtech.assignment.data.constants.EUserType;
import com.nashtech.assignment.data.entities.User;
import com.nashtech.assignment.data.repositories.AssignAssetRepository;
import com.nashtech.assignment.data.repositories.UserRepository;
import com.nashtech.assignment.dto.request.user.ChangePasswordFirstRequest;
import com.nashtech.assignment.dto.request.user.ChangePasswordRequest;
import com.nashtech.assignment.dto.request.user.CreateNewUserRequest;
import com.nashtech.assignment.dto.request.user.EditUserRequest;
import com.nashtech.assignment.dto.response.user.UserResponse;
import com.nashtech.assignment.exceptions.BadRequestException;
import com.nashtech.assignment.exceptions.ForbiddenException;
import com.nashtech.assignment.exceptions.NotFoundException;
import com.nashtech.assignment.mappers.UserMapper;
import com.nashtech.assignment.services.auth.SecurityContextService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.ArgumentCaptor;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

class UserServiceImplTest {

    private UserServiceImpl userServiceImpl;
    private UserRepository userRepository;
    private UserMapper userMapper;
    private PasswordEncoder passwordEncoder;
    private AssignAssetRepository assignAssetRepository;
    private SecurityContextService securityContextService;

    private User user;
    private UserResponse userResponse;

    @BeforeEach
    void beforeEach() {
        userRepository = mock(UserRepository.class);
        userMapper = mock(UserMapper.class);
        passwordEncoder = mock(PasswordEncoder.class);
        securityContextService = mock(SecurityContextService.class);
        assignAssetRepository = mock(AssignAssetRepository.class);
        userServiceImpl = UserServiceImpl.builder()
                .userRepository(userRepository)
                .userMapper(userMapper)
                .passwordEncoder(passwordEncoder)
                .assignAssetRepository(assignAssetRepository)
                .securityContextService(securityContextService).build();
        user = mock(User.class);
        userResponse = mock(UserResponse.class);
    }

    @Test
    void createNewUser_WhenAgeLessThan18_ShouldThrowBadRequestException() {
        CreateNewUserRequest createNewUserRequest = CreateNewUserRequest.builder()
                .dateOfBirth("20/12/2004")
                .build();

        BadRequestException actual = assertThrows(BadRequestException.class,
                () -> userServiceImpl.createNewUser(createNewUserRequest));

        assertThat(actual.getMessage(), is("Age cannot below 18."));
    }

    @ParameterizedTest
    @CsvSource(value = {
            "26/11/2022:Joined date cannot be Saturday or Sunday.",
            "25/11/2000:Joined date cannot be after birth date.",
    }, delimiter = ':')
    void createNewUser_WhenJoinDateNotValid_ShouldThrowBadRequestException(String joinedDate, String message) {
        CreateNewUserRequest createNewUserRequest = CreateNewUserRequest.builder()
                .dateOfBirth("21/12/2001")
                .joinedDate(joinedDate)
                .build();

        BadRequestException actual = assertThrows(BadRequestException.class,
                () -> userServiceImpl.createNewUser(createNewUserRequest));

        assertThat(actual.getMessage(), is(message));
    }

    @Test
    void createNewUser_WhenCreateAdminButLocationIsNull_ShouldThrowBadRequestException() {
        CreateNewUserRequest createNewUserRequest = CreateNewUserRequest.builder()
                .dateOfBirth("21/12/2001")
                .joinedDate("17/11/2022")
                .type(EUserType.ADMIN)
                .location(null)
                .build();

        when(userMapper.toUser(createNewUserRequest)).thenReturn(user);

        BadRequestException actual = assertThrows(BadRequestException.class,
                () -> userServiceImpl.createNewUser(createNewUserRequest));

        assertThat(actual.getMessage(), is("User type of ADMIN so location cannot be blank."));
    }

    @Test
    void createNewUser_WhenDataValid_ShouldReturnData() throws ParseException {
        CreateNewUserRequest createNewUserRequest = CreateNewUserRequest.builder()
                .firstName("hau")
                .lastName("doan")
                .dateOfBirth("21/12/2001")
                .joinedDate("17/11/2022")
                .gender(EGender.MALE)
                .type(EUserType.ADMIN)
                .location("location")
                .build();
        ArgumentCaptor<String> staffCodeCaptor = ArgumentCaptor.forClass(String.class);
        SimpleDateFormat formatterDate = new SimpleDateFormat(Constants.DATE_FORMAT);
        Date dateOfBirth = formatterDate.parse(createNewUserRequest.getDateOfBirth());
        Date joinedDate = formatterDate.parse(createNewUserRequest.getJoinedDate());
        UserResponse expected = mock(UserResponse.class);

        when(userRepository.save(user)).thenReturn(user);
        when(user.getFirstName()).thenReturn(createNewUserRequest.getFirstName());
        when(userMapper.toUser(createNewUserRequest)).thenReturn(user);
        when(user.getUsername()).thenReturn("haud");
        when(userMapper.toUserResponse(user)).thenReturn(expected);

        UserResponse actual = userServiceImpl.createNewUser(createNewUserRequest);

        verify(user).setStaffCode(staffCodeCaptor.capture());
        verify(user).setUsername("haud");
        verify(user).setDateOfBirth(dateOfBirth);
        verify(user).setJoinedDate(joinedDate);
        verify(user).setPassword(passwordEncoder.encode("haud@21122001"));
        assertThat(staffCodeCaptor.getValue(), is("SD0000"));
        assertThat(actual, is(expected));
    }

    @Test
    void deleteUser_WhenDataValid_ShouldReturnVoid() {
        when(securityContextService.getCurrentUser()).thenReturn(user);
        when(securityContextService.getCurrentUser().getStaffCode()).thenReturn("test2");
        when(userRepository.findByStaffCode("test")).thenReturn(user);
        when(assignAssetRepository.existsByUserAssignedToAndIsDeletedFalse(user)).thenReturn(false);

        userServiceImpl.deleteUser("test");

        verify(user).setDeleted(true);
        verify(userRepository).save(user);
    }

    @Test
    void deleteUser_WhenUserNotExist_ShouldThrowNotFoundException() {
        when(securityContextService.getCurrentUser()).thenReturn(user);
        when(securityContextService.getCurrentUser().getStaffCode()).thenReturn("test2");
        when(userRepository.findByStaffCode("test")).thenReturn(null);

        NotFoundException actual = assertThrows(NotFoundException.class, () ->
                userServiceImpl.deleteUser("test")
        );

        assertThat(actual.getMessage(), is("Cannot found user with staff code test"));
    }

    @Test
    void deleteUser_WhenUserHaveAssign_ShouldThrowBadRequestException() {
        when(securityContextService.getCurrentUser()).thenReturn(user);
        when(securityContextService.getCurrentUser().getStaffCode()).thenReturn("test2");
        when(userRepository.findByStaffCode("test")).thenReturn(user);
        when(assignAssetRepository.existsByUserAssignedToAndIsDeletedFalse(user)).thenReturn(true);

        BadRequestException actual = assertThrows(BadRequestException.class,
                () -> userServiceImpl.deleteUser("test"));

        assertThat(actual.getMessage(), is(
                "There are valid assignments belonging to this user. Please close all assignments before disabling user."));
    }

    @Test
    void deleteUser_WhenUserHaveAssignBy_ShouldThrowBadRequestException() {
        when(securityContextService.getCurrentUser()).thenReturn(user);
        when(securityContextService.getCurrentUser().getStaffCode()).thenReturn("test2");
        when(userRepository.findByStaffCode("test")).thenReturn(user);
        when(assignAssetRepository.existsByUserAssignedToAndIsDeletedFalse(user)).thenReturn(true);
        when(assignAssetRepository.existsByUserAssignedByAndIsDeletedFalse(user)).thenReturn(true);

        BadRequestException actual = assertThrows(BadRequestException.class,
                () -> userServiceImpl.deleteUser("test"));

        assertThat(actual.getMessage(), is(
                "There are valid assignments belonging to this user. Please close all assignments before disabling user."));
    }

    @Test
    void deleteUser_WhenUserDeleteIsCurrentUser_ShouldThrowBadRequestException() {
        when(securityContextService.getCurrentUser()).thenReturn(user);
        when(securityContextService.getCurrentUser().getStaffCode()).thenReturn("currentUser");

        BadRequestException actual = assertThrows(BadRequestException.class,
                () -> userServiceImpl.deleteUser("currentUser"));

        assertThat(actual.getMessage(), is("Cannot disable yourself"));
    }

    @Test
    void editUserInformation_WhenDataValid_ShouldReturnUserResponse() throws ParseException {
        DateFormat formatterDate = new SimpleDateFormat(Constants.DATE_FORMAT);
        Date dateOfBirth = formatterDate.parse("21/9/2001");
        Date joinedDate = formatterDate.parse("25/11/2022");
        EditUserRequest editUserRequest = EditUserRequest.builder()
                .dateOfBirth(formatterDate.format(dateOfBirth))
                .joinedDate(formatterDate.format(joinedDate))
                .staffCode("test")
                .gender(EGender.FEMALE)
                .type(EUserType.STAFF)
                .build();

        when(userRepository.findByStaffCode("test")).thenReturn(user);
        when(userRepository.save(user)).thenReturn(user);
        when(userMapper.toUserResponse(user)).thenReturn(userResponse);

        UserResponse actual = userServiceImpl.editUserInformation(editUserRequest);

        verify(user).setDateOfBirth(dateOfBirth);
        verify(user).setJoinedDate(joinedDate);
        verify(user).setGender(editUserRequest.getGender());
        verify(user).setType(editUserRequest.getType());
        verify(userRepository).save(user);

        assertThat(actual, is(userResponse));
    }

    @Test
    void editUserInformation_WhenUserNotFound_ShouldThrowBadRequestException() {
        EditUserRequest user = EditUserRequest.builder()
                .dateOfBirth("21/9/2022")
                .joinedDate("25/11/2022")
                .staffCode("test")
                .gender(EGender.FEMALE)
                .type(EUserType.STAFF)
                .build();
        when(userRepository.findByStaffCode("test")).thenReturn(null);

        NotFoundException actual = assertThrows(NotFoundException.class,
                () -> userServiceImpl.editUserInformation(user));

        assertThat(actual.getMessage(), is("Cannot found staff with Id " + "test"));
    }

    @Test
    void editUserInformation_WhenDateOfBirthNotValid_ShouldThrowBadRequestException() {
        EditUserRequest editUserRequest = EditUserRequest.builder()
                .dateOfBirth("21/9/2022")
                .joinedDate("25/11/2022")
                .gender(EGender.FEMALE)
                .staffCode("test")
                .type(EUserType.STAFF)
                .build();
        when(userRepository.findByStaffCode("test")).thenReturn(user);

        BadRequestException actual = assertThrows(BadRequestException.class,
                () -> userServiceImpl.editUserInformation(editUserRequest));

        assertThat(actual.getMessage(), is("Age cannot below 18."));
    }

    @ParameterizedTest
    @CsvSource(value = {
            "26/11/2022:Joined date cannot be Saturday or Sunday.",
            "25/11/2000:Joined date must lager or equal 18 years.",
            "29/11/2900:Joined date cannot lager than 100 years."
    }, delimiter = ':')
    void editUserInformation_WhenJoinDateNotValid_ShouldThrowBadRequestException(String joinedDate, String message) {
        EditUserRequest editUserRequest = EditUserRequest.builder()
                .dateOfBirth("21/9/2001")
                .joinedDate(joinedDate)
                .staffCode("test")
                .build();
        when(userRepository.findByStaffCode("test")).thenReturn(user);

        BadRequestException actual = assertThrows(BadRequestException.class,
                () -> userServiceImpl.editUserInformation(editUserRequest));

        assertThat(actual.getMessage(), is(message));
    }

    @Test
    void changePasswordFirst_WhenNotFirstLogin_ShouldReturnException() {
        ChangePasswordFirstRequest changePasswordFirstRequest = ChangePasswordFirstRequest.builder().build();

        when(securityContextService.getCurrentUser()).thenReturn(user);
        when(user.isFirstLogin()).thenReturn(false);

        BadRequestException actual = assertThrows(BadRequestException.class, () ->
                userServiceImpl.changePasswordFirst(changePasswordFirstRequest)
        );

        assertThat(actual.getMessage(), is("Is not first login"));
    }

    @Test
    void changePasswordFirst_WhenPasswordNoChange_ShouldReturnException() {
        ChangePasswordFirstRequest changePasswordFirstRequest = ChangePasswordFirstRequest.builder()
                .newPassword("123456").build();

        when(securityContextService.getCurrentUser()).thenReturn(user);
        when(user.isFirstLogin()).thenReturn(true);
        when(passwordEncoder.matches(changePasswordFirstRequest.getNewPassword(), user.getPassword()))
                .thenReturn(true);

        BadRequestException actual = assertThrows(BadRequestException.class, () ->
                userServiceImpl.changePasswordFirst(changePasswordFirstRequest)
        );

        assertThat(actual.getMessage(), is("Password no change"));
    }

    @Test
    void changePasswordFirst_WhenDataValid_ShouldReturnData() {
        ChangePasswordFirstRequest changePasswordFirstRequest = ChangePasswordFirstRequest.builder()
                .newPassword("123456").build();
        ArgumentCaptor<User> userArgumentCaptor = ArgumentCaptor.forClass(User.class);

        when(securityContextService.getCurrentUser()).thenReturn(user);
        when(user.isFirstLogin()).thenReturn(true);
        when(passwordEncoder.matches(changePasswordFirstRequest.getNewPassword(), user.getPassword()))
                .thenReturn(false);
        when(userMapper.toUserResponse(userArgumentCaptor.capture())).thenReturn(userResponse);

        UserResponse actual = userServiceImpl.changePasswordFirst(changePasswordFirstRequest);

        verify(user).setPassword(passwordEncoder.encode(changePasswordFirstRequest.getNewPassword()));
        verify(userRepository).save(user);
        assertThat(actual, is(userResponse));
    }

    @Test
    void changePassword_WhenPasswordIncorrect_ShouldReturnException() {
        ChangePasswordRequest changePasswordRequest = ChangePasswordRequest.builder().build();

        when(securityContextService.getCurrentUser()).thenReturn(user);
        when(passwordEncoder.matches(changePasswordRequest.getOldPassword(), user.getPassword()))
                .thenReturn(false);

        BadRequestException actual = assertThrows(BadRequestException.class, () ->
                userServiceImpl.changePassword(changePasswordRequest)
        );

        assertThat(actual.getMessage(), is("Password is incorrect"));
    }

    @Test
    void changePassword_WhenPasswordNoChange_ShouldReturnException() {
        ChangePasswordRequest changePasswordRequest = ChangePasswordRequest
                .builder()
                .oldPassword("123456")
                .newPassword("123456").build();

        when(securityContextService.getCurrentUser()).thenReturn(user);
        when(passwordEncoder.matches(changePasswordRequest.getOldPassword(), user.getPassword())).thenReturn(true);

        BadRequestException actual = assertThrows(BadRequestException.class, () ->
                userServiceImpl.changePassword(changePasswordRequest)
        );

        assertThat(actual.getMessage(), is("Password no change"));
    }

    @Test
    void changePassword_WhenDataValid_ShouldReturnData() {
        ChangePasswordRequest changePasswordRequest = ChangePasswordRequest
                .builder()
                .oldPassword("123456")
                .newPassword("654321").build();

        ArgumentCaptor<User> userArgumentCaptor = ArgumentCaptor.forClass(User.class);

        when(securityContextService.getCurrentUser()).thenReturn(user);
        when(passwordEncoder.matches(changePasswordRequest.getOldPassword(), user.getPassword())).thenReturn(true);
        when(userMapper.toUserResponse(userArgumentCaptor.capture())).thenReturn(userResponse);

        UserResponse actual = userServiceImpl.changePassword(changePasswordRequest);

        verify(user).setPassword(passwordEncoder.encode(changePasswordRequest.getNewPassword()));
        verify(userRepository).save(user);

        assertThat(actual, is(userResponse));
    }

    @Test
    void viewUserDetails_WhenDataValid_ShouldReturnUserDetails() {
        when(userRepository.findByStaffCode("test")).thenReturn(user);
        when(userMapper.toUserResponse(user)).thenReturn(userResponse);

        UserResponse actual = userServiceImpl.viewUserDetails("test");

        assertThat(actual, is(userResponse));
    }

    @Test
    void viewUserDetails_WhenNotFound_ShouldReturnNull() {
        when(userRepository.findByStaffCode("test")).thenReturn(null);
        when(userMapper.toUserResponse(user)).thenReturn(userResponse);

        NotFoundException actual = assertThrows(NotFoundException.class, () ->
                userServiceImpl.viewUserDetails("test"));

        assertThat(actual.getMessage(), is("Cannot Found Staff With Code: test"));
    }

    @Test
    void getCurrentUserLoggedInInformation_WhenUsernameNotMatchWithUsernameOfLoggedInUser_ThrowForbiddenException() {
        when(securityContextService.getCurrentUser()).thenReturn(user);
        when(user.getUsername()).thenReturn("differentUserName");

        ForbiddenException actual = assertThrows(ForbiddenException.class, () ->
                userServiceImpl.getCurrentUserLoggedInInformation("username"));

        assertThat(actual.getMessage(), is("You don't have permission to get other user information."));
    }

    @Test
    void getCurrentUserLoggedInInformation_WhenValidUsername_ShouldReturnData() {
        when(securityContextService.getCurrentUser()).thenReturn(user);
        when(user.getUsername()).thenReturn("username");
        when(userMapper.toUserResponse(user)).thenReturn(userResponse);

        UserResponse actual = userServiceImpl.getCurrentUserLoggedInInformation("username");

        assertThat(actual, is(userResponse));
    }
}