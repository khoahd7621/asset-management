package com.nashtech.assignment.services.auth;

import com.nashtech.assignment.dto.request.UserLoginRequest;
import com.nashtech.assignment.dto.response.UserLoginResponse;

public interface LoginService {
    UserLoginResponse login(UserLoginRequest userLoginRequest);
}
