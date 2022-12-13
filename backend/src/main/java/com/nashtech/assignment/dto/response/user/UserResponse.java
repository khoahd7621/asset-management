package com.nashtech.assignment.dto.response.user;

import com.nashtech.assignment.data.constants.EGender;
import com.nashtech.assignment.data.constants.EUserType;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Builder
@Getter
@Setter
public class UserResponse {
    private long id;
    private String username;
    private String staffCode;
    private String firstName;
    private String lastName;
    private EGender gender;
    private Date joinedDate;
    private Date dateOfBirth;
    private EUserType type;
    private String location;
    private String fullName;
    private boolean isFirstLogin;
}
