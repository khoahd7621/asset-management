package com.nashtech.assignment.dto.request.user;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.nashtech.assignment.data.constants.Constants;
import com.nashtech.assignment.data.constants.EGender;
import com.nashtech.assignment.data.constants.EUserType;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Builder
@Getter
@Setter
public class EditUserRequest {
    @NotBlank(message = "Date of birth is required")
    @JsonFormat(pattern = Constants.DATE_FORMAT)
    private String dateOfBirth;

    @NotNull(message = "Gender is required")
    private EGender gender;

    @NotBlank(message = "Joined date is required")
    @JsonFormat(pattern = Constants.DATE_FORMAT)
    private String joinedDate;

    @NotNull(message = "User Type is required")
    private EUserType type;

    @NotNull(message = "Staff code is required")
    private String staffCode;

}
