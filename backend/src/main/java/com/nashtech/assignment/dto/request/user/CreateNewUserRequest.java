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
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

@Getter
@Setter
@Builder
public class CreateNewUserRequest {
    @NotBlank(message = "First name is required")
    @Pattern(regexp = Constants.ONLY_ALPHABET_REGEX)
    @Size(max = Constants.MAX_LENGTH_INPUT)
    private String firstName;

    @NotBlank(message = "Last name is required")
    @Pattern(regexp = Constants.ONLY_ALPHABET_AND_SPACE_REGEX)
    @Size(max = Constants.MAX_LENGTH_INPUT)
    private String lastName;

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

    @Size(max = Constants.MAX_LENGTH_INPUT)
    private String location;
}
