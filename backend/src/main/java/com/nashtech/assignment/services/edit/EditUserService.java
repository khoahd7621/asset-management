package com.nashtech.assignment.services.edit;

import com.nashtech.assignment.dto.request.user.ChangePasswordFirstRequest;
import com.nashtech.assignment.dto.request.user.ChangePasswordRequest;
import com.nashtech.assignment.dto.request.user.EditUserRequest;
import com.nashtech.assignment.dto.response.user.UserResponse;

import java.text.ParseException;

public interface EditUserService {
    UserResponse editUserInformation(EditUserRequest userRequest) throws ParseException;

    UserResponse changePasswordFirst(ChangePasswordFirstRequest changePasswordFirstRequest);

    UserResponse changePassword(ChangePasswordRequest changePasswordRequest);
}
