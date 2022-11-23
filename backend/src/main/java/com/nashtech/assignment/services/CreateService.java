package com.nashtech.assignment.services;

import com.nashtech.assignment.dto.request.CreateNewUserRequest;
import com.nashtech.assignment.dto.response.user.UserResponse;

import java.text.ParseException;

public interface CreateService {
    public UserResponse createNewUser(CreateNewUserRequest createNewUserRequest) throws ParseException;
}
