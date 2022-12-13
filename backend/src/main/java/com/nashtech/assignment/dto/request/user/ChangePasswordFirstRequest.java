package com.nashtech.assignment.dto.request.user;

import com.nashtech.assignment.data.constants.Constants;
import lombok.*;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChangePasswordFirstRequest {
    @NotBlank(message = "New password is required")
    @Size(min = Constants.MIN_LENGTH_PASSWORD, max = Constants.MAX_LENGTH_PASSWORD, message = "The password must contain at least 6 characters and be up to 24 characters")
    private String newPassword;
}
