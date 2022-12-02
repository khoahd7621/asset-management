package com.nashtech.assignment.services.get;

import com.nashtech.assignment.dto.response.user.UserResponse;

import java.util.List;

public interface GetUserService {
    List<UserResponse> getAllUsers();
}
