package com.nashtech.assignment.services;

import com.nashtech.assignment.dto.request.CreateNewUserRequest;

import java.text.ParseException;

public interface CreateService {
    public void createNewUser(CreateNewUserRequest createNewUserRequest) throws ParseException;
}
