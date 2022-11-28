package com.nashtech.assignment.dto.request.user;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.nashtech.assignment.data.constants.EGender;
import com.nashtech.assignment.data.constants.EUserType;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class CreateNewUserRequest {
    @NotBlank(message = "First name is required")
    @Pattern(regexp = "^.*[A-Za-z].*$")
    @Size(max = 100)
    private String firstName;

    @NotBlank(message = "Last name is required")
    @Pattern(regexp = "^.*[A-Za-z].*[\\s]?+.*$")
    @Size(max = 100)
    private String lastName;

    @NotBlank(message = "Date of birth is required")
    @JsonFormat(pattern = "dd/MM/yyyy")
    private String dateOfBirth;

    @NotNull(message = "Gender is required")
    private EGender gender;

    @NotBlank(message = "Joined date is required")
    @JsonFormat(pattern = "dd/MM/yyyy")
    private String joinedDate;

    @NotNull(message = "User Type is required")
    private EUserType type;

    @Size(max = 100)
    private String location;
}
