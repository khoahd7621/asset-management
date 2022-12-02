package com.nashtech.assignment.services.create;

import com.nashtech.assignment.dto.request.user.CreateNewUserRequest;
import com.nashtech.assignment.dto.response.user.UserResponse;

import java.text.ParseException;

public interface CreateUserService {
    UserResponse createNewUser(CreateNewUserRequest createNewUserRequest) throws ParseException;
}
