package com.nashtech.assignment.services.edit.impl;

import com.nashtech.assignment.data.constants.EGender;
import com.nashtech.assignment.data.constants.EUserType;
import com.nashtech.assignment.data.entities.User;
import com.nashtech.assignment.data.repositories.UserRepository;
import com.nashtech.assignment.dto.request.user.ChangePasswordFirstRequest;
import com.nashtech.assignment.dto.request.user.ChangePasswordRequest;
import com.nashtech.assignment.dto.request.user.EditUserRequest;
import com.nashtech.assignment.dto.response.user.UserResponse;
import com.nashtech.assignment.exceptions.BadRequestException;
import com.nashtech.assignment.exceptions.NotFoundException;
import com.nashtech.assignment.mappers.UserMapper;
import com.nashtech.assignment.services.auth.SecurityContextService;
import com.nashtech.assignment.utils.GeneratePassword;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

class EditUserServiceImplTest {

    private EditUserServiceImpl editUserServiceImpl;
    private UserRepository userRepository;
    private UserMapper userMapper;
    private User userRepo;
    private UserResponse userResponse;
    private User user;
    private SecurityContextService securityContextService;
    private PasswordEncoder passwordEncoder;
    private GeneratePassword generatePassword;

    @BeforeEach
    void setUp() {
        userRepository = mock(UserRepository.class);
        passwordEncoder = mock(PasswordEncoder.class);
        securityContextService = mock(SecurityContextService.class);
        userMapper = mock(UserMapper.class);
        generatePassword = mock(GeneratePassword.class);
        editUserServiceImpl = EditUserServiceImpl.builder()
                .generatePassword(generatePassword)
                .userRepository(userRepository)
                .passwordEncoder(passwordEncoder)
                .securityContextService(securityContextService)
                .userMapper(userMapper).build();
        userRepo = mock(User.class);
        userResponse = mock(UserResponse.class);
        user = mock(User.class);
    }

    @Test
    void editUserInformation_WhenDataValid_ShouldReturnUserResponse() throws ParseException {
        EditUserRequest user = EditUserRequest.builder()
                .dateOfBirth("21/9/2001")
                .joinedDate("25/11/2022")
                .staffCode("test")
                .gender(EGender.FEMALE)
                .type(EUserType.STAFF)
                .build();

        SimpleDateFormat formatterDate = new SimpleDateFormat("dd/MM/yyyy");
        formatterDate.setLenient(false);
        Date dateOfBirth = formatterDate.parse(user.getDateOfBirth());
        Date joinedDate = formatterDate.parse(user.getJoinedDate());

        when(userRepository.findByStaffCode("test")).thenReturn(userRepo);
        when(userRepository.save(userRepo)).thenReturn(userRepo);
        when(userMapper.mapEntityToResponseDto(userRepo)).thenReturn(userResponse);

        UserResponse actual = editUserServiceImpl.editUserInformation(user);

        verify(userRepo).setDateOfBirth(dateOfBirth);
        verify(userRepo).setJoinedDate(joinedDate);
        verify(userRepo).setGender(user.getGender());
        verify(userRepo).setType(user.getType());
        verify(userRepository).save(userRepo);

        assertThat(actual, is(userResponse));
    }

    @Test
    void editUserInformation_WhenUserNotFound_ShouldThrowBadRequestException() throws ParseException {
        EditUserRequest user = EditUserRequest.builder()
                .dateOfBirth("21/9/2022")
                .joinedDate("25/11/2022")
                .staffCode("test")
                .gender(EGender.FEMALE)
                .type(EUserType.STAFF)
                .build();
        when(userRepository.findByStaffCode("test")).thenReturn(null);

        NotFoundException actual = assertThrows(NotFoundException.class,
                () -> editUserServiceImpl.editUserInformation(user));

        assertThat(actual.getMessage(), is("Cannot found staff with Id " + "test"));

    }

    @Test
    void editUserInformation_WhenDateOfBirthNotValid_ShouldThrowBadRequestException() throws ParseException {
        EditUserRequest user = EditUserRequest.builder()
                .dateOfBirth("21/9/2022")
                .joinedDate("25/11/2022")
                .gender(EGender.FEMALE)
                .staffCode("test")
                .type(EUserType.STAFF)
                .build();
        when(userRepository.findByStaffCode("test")).thenReturn(userRepo);

        BadRequestException actual = assertThrows(BadRequestException.class,
                () -> editUserServiceImpl.editUserInformation(user));

        assertThat(actual.getMessage(), is("Age cannot below 18."));

    }

    @Test
    void editUserInformation_WhenJoinDateIsSundayOrSaturday_ShouldThrowBadRequestException() throws ParseException {
        EditUserRequest user = EditUserRequest.builder()
                .dateOfBirth("21/9/2001")
                .joinedDate("26/11/2022")
                .staffCode("test")
                .build();
        when(userRepository.findByStaffCode("test")).thenReturn(userRepo);

        BadRequestException actual = assertThrows(BadRequestException.class,
                () -> editUserServiceImpl.editUserInformation(user));

        assertThat(actual.getMessage(), is("Joined date cannot be Saturday or Sunday."));

    }

    @Test
    void editUserInformation_WhenJoinDateIsAfterDateOfBirth_ShouldThrowBadRequestException() throws ParseException {
        EditUserRequest user = EditUserRequest.builder()
                .dateOfBirth("21/9/2001")
                .joinedDate("25/11/2000")
                .staffCode("test")
                .build();
        when(userRepository.findByStaffCode("test")).thenReturn(userRepo);

        BadRequestException actual = assertThrows(BadRequestException.class,
                () -> editUserServiceImpl.editUserInformation(user));

        assertThat(actual.getMessage(), is("Joined date must lager or equal 18 years."));

    }

    @Test
    void editUserInformation_WhenJoinDateIsLagerThan100Years_ShouldThrowBadRequestException() throws ParseException {
        EditUserRequest user = EditUserRequest.builder()
                .dateOfBirth("21/9/2001")
                .joinedDate("29/11/2900")
                .staffCode("test")
                .build();
        when(userRepository.findByStaffCode("test")).thenReturn(userRepo);

        BadRequestException actual = assertThrows(BadRequestException.class,
                () -> editUserServiceImpl.editUserInformation(user));

        assertThat(actual.getMessage(), is("Joined date cannot lager than 100 years."));

    }

    @Test
    void testChangePasswordFirst_WhenPasswordNoChange_ShouldReturnException() throws Exception {
        ChangePasswordFirstRequest changePasswordFirstRequest = ChangePasswordFirstRequest.builder().build();

        when(securityContextService.getCurrentUser()).thenReturn(user);
        when(passwordEncoder.matches(changePasswordFirstRequest.getNewPassword(), user.getPassword()))
                .thenReturn(true);

        BadRequestException actual = assertThrows(BadRequestException.class, () -> {
            editUserServiceImpl.changePasswordFirst(changePasswordFirstRequest);
        });

        assertThat(actual.getMessage(), is("Password no change"));
    }

    @Test
    void testChangePasswordFirst_WhenDataValid_ShouldReturnData() throws Exception {
        ChangePasswordFirstRequest changePasswordFirstRequest = ChangePasswordFirstRequest.builder()
                .newPassword("123456").build();
        ArgumentCaptor<User> userArgumentCaptor = ArgumentCaptor.forClass(User.class);

        when(securityContextService.getCurrentUser()).thenReturn(user);
        when(passwordEncoder.matches(changePasswordFirstRequest.getNewPassword(), user.getPassword()))
                .thenReturn(false);
        when(userMapper.mapEntityToResponseDto(userArgumentCaptor.capture())).thenReturn(userResponse);

        UserResponse actual = editUserServiceImpl.changePasswordFirst(changePasswordFirstRequest);

        verify(user).setPassword(passwordEncoder.encode(changePasswordFirstRequest.getNewPassword()));
        verify(userRepository).save(user);

        assertThat(actual, is(userResponse));
    }

    @Test
    void testChangePassword_WhenPasswordIncorrect_ShouldReturnException() throws Exception {
        ChangePasswordRequest changePasswordRequest = ChangePasswordRequest.builder().build();

        when(securityContextService.getCurrentUser()).thenReturn(user);
        when(passwordEncoder.matches(changePasswordRequest.getOldPassword(), user.getPassword()))
                .thenReturn(false);

        BadRequestException actual = assertThrows(BadRequestException.class, () -> {
            editUserServiceImpl.changePassword(changePasswordRequest);
        });

        assertThat(actual.getMessage(), is("Password is incorrect"));
    }

    @Test
    void testChangePassword_WhenPasswordNoChange_ShouldReturnException() throws Exception {
        ChangePasswordRequest changePasswordRequest = ChangePasswordRequest
                .builder()
                .oldPassword("123456")
                .newPassword("123456").build();

        when(securityContextService.getCurrentUser()).thenReturn(user);
        when(passwordEncoder.matches(changePasswordRequest.getOldPassword(), user.getPassword())).thenReturn(true);

        BadRequestException actual = assertThrows(BadRequestException.class, () -> {
            editUserServiceImpl.changePassword(changePasswordRequest);
        });

        assertThat(actual.getMessage(), is("Password no change"));
    }

    @Test
    void testChangePassword_WhenPasswordSameGenerated_ShouldReturnException() throws Exception {
        ChangePasswordRequest changePasswordRequest = ChangePasswordRequest
                .builder()
                .oldPassword("123456")
                .newPassword("anh@01012001").build();

        when(securityContextService.getCurrentUser()).thenReturn(user);
        when(passwordEncoder.matches(changePasswordRequest.getOldPassword(), user.getPassword()))
                .thenReturn(true);
        when(passwordEncoder.matches(changePasswordRequest.getNewPassword(), generatePassword.firstPassword(user)))
                .thenReturn(true);

        BadRequestException actual = assertThrows(BadRequestException.class, () -> {
            editUserServiceImpl.changePassword(changePasswordRequest);
        });

        assertThat(actual.getMessage(), is("Password same password generated"));
    }

    @Test
    void testChangePassword_WhenDataVaid_ShouldReturnData() throws Exception {
        ChangePasswordRequest changePasswordRequest = ChangePasswordRequest
                .builder()
                .oldPassword("123456")
                .newPassword("654321").build();

        ArgumentCaptor<User> userArgumentCaptor = ArgumentCaptor.forClass(User.class);

        when(securityContextService.getCurrentUser()).thenReturn(user);
        when(passwordEncoder.matches(changePasswordRequest.getOldPassword(), user.getPassword())).thenReturn(true);
        when(passwordEncoder.matches(changePasswordRequest.getNewPassword(), generatePassword.firstPassword(user)))
                .thenReturn(false);
        when(userMapper.mapEntityToResponseDto(userArgumentCaptor.capture())).thenReturn(userResponse);

        UserResponse actual = editUserServiceImpl.changePassword(changePasswordRequest);

        verify(user).setPassword(passwordEncoder.encode(changePasswordRequest.getNewPassword()));
        verify(userRepository).save(user);

        assertThat(actual, is(userResponse));
    }
}