package com.nashtech.assignment.services.user;

import com.nashtech.assignment.dto.request.user.ChangePasswordFirstRequest;
import com.nashtech.assignment.dto.request.user.ChangePasswordRequest;
import com.nashtech.assignment.dto.request.user.CreateNewUserRequest;
import com.nashtech.assignment.dto.request.user.EditUserRequest;
import com.nashtech.assignment.dto.response.user.UserResponse;

import java.text.ParseException;

public interface UserService {
    UserResponse createNewUser(CreateNewUserRequest createNewUserRequest) throws ParseException;

    void deleteUser(String staffCode);

    UserResponse editUserInformation(EditUserRequest userRequest) throws ParseException;

    UserResponse changePasswordFirst(ChangePasswordFirstRequest changePasswordFirstRequest);

    UserResponse changePassword(ChangePasswordRequest changePasswordRequest);

    UserResponse viewUserDetails(String staffCode);

    UserResponse getCurrentUserLoggedInInformation(String username);
}
