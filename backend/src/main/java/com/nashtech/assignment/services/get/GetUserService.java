package com.nashtech.assignment.services.get;

import com.nashtech.assignment.dto.response.user.UserResponse;

public interface GetUserService {
    UserResponse viewUserDetails(String staffCode);
}
