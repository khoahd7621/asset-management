package com.nashtech.assignment.services.impl;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

import com.nashtech.assignment.services.SecurityContextService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.nashtech.assignment.data.constants.EGender;
import com.nashtech.assignment.data.constants.EUserType;
import com.nashtech.assignment.data.entities.User;
import com.nashtech.assignment.data.repositories.UserRepository;
import com.nashtech.assignment.dto.request.CreateNewUserRequest;
import com.nashtech.assignment.exceptions.BadRequestException;
import com.nashtech.assignment.mappers.UserMapper;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

class CreateServiceImplTest {

    private CreateServiceImpl createService;
    private UserRepository userRepository;
    private UserMapper userMapper;
    private PasswordEncoder passwordEncoder;
    private SecurityContextService securityContextService;
    private User user; 

    @BeforeEach
    void beforeEach() {
        userRepository = mock(UserRepository.class);
        userMapper = mock(UserMapper.class);
        passwordEncoder = mock(PasswordEncoder.class);
        securityContextService = mock(SecurityContextService.class);
        createService = CreateServiceImpl.builder()
                .userRepository(userRepository)
                .userMapper(userMapper)
                .passwordEncoder(passwordEncoder)
                .securityContextService(securityContextService)
                .build();
        user = mock(User.class);
    }

    @Test
    void createNewUser_WhenAgeLessThan18_ShouldThrowBadRequestException() {
        CreateNewUserRequest createNewUserRequest = CreateNewUserRequest.builder()
                .dateOfBirth("20/12/2004")
                .build();

        BadRequestException actual = assertThrows(BadRequestException.class, () -> {
            createService.createNewUser(createNewUserRequest);
        });

        assertThat(actual.getMessage(), is("Age cannot below 18."));
    }

    @Test
    void createNewUser_WhenBirthAfterJoinDate_ShouldThrowBadRequestException() {
        CreateNewUserRequest createNewUserRequest = CreateNewUserRequest.builder()
                .dateOfBirth("21/12/2001")
                .joinedDate("20/12/2001")
                .build();

        BadRequestException actual = assertThrows(BadRequestException.class, () -> {
            createService.createNewUser(createNewUserRequest);
        });

        assertThat(actual.getMessage(), is("Joined date cannot be after birth date."));
    }

    @Test
    void createNewUser_WhenJoinDateIsSunday_ShouldThrowBadRequestException() {
        CreateNewUserRequest createNewUserRequest = CreateNewUserRequest.builder()
                .dateOfBirth("21/12/2001")
                .joinedDate("20/11/2022")
                .build();

        BadRequestException actual = assertThrows(BadRequestException.class, () -> {
            createService.createNewUser(createNewUserRequest);
        });

        assertThat(actual.getMessage(), is("Joined date cannot be Saturday or Sunday."));
    }

    @Test
    void createNewUser_WhenJoinDateIsSaturday_ShouldThrowBadRequestException() {
        CreateNewUserRequest createNewUserRequest = CreateNewUserRequest.builder()
                .dateOfBirth("21/12/2001")
                .joinedDate("19/11/2022")
                .build();

        BadRequestException actual = assertThrows(BadRequestException.class, () -> {
            createService.createNewUser(createNewUserRequest);
        });

        assertThat(actual.getMessage(), is("Joined date cannot be Saturday or Sunday."));
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

        BadRequestException actual = assertThrows(BadRequestException.class, () -> {
            createService.createNewUser(createNewUserRequest);
        });

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
                .location("hehe")
                .build();
        ArgumentCaptor<String> staffCodeCaptor = ArgumentCaptor.forClass(String.class);
        SimpleDateFormat formatterDate = new SimpleDateFormat("dd/MM/yyyy");
        Date dateOfBirth = formatterDate.parse(createNewUserRequest.getDateOfBirth());
        Date joinedDate = formatterDate.parse(createNewUserRequest.getJoinedDate());
        
        when(userRepository.save(user)).thenReturn(user);
        when(user.getFirstName()).thenReturn(createNewUserRequest.getFirstName());
        when(userMapper.toUser(createNewUserRequest)).thenReturn(user);
        when(user.getUsername()).thenReturn("haud");

        createService.createNewUser(createNewUserRequest);

        verify(user).setStaffCode(staffCodeCaptor.capture());
        verify(user).setUsername("haud");
        verify(user).setDateOfBirth(dateOfBirth);
        verify(user).setJoinedDate(joinedDate);
        verify(user).setPassword(passwordEncoder.encode("haud@21122001"));
        assertThat(staffCodeCaptor.getValue(), is("SD0000"));
    }
}