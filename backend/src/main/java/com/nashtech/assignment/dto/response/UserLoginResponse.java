package com.nashtech.assignment.dto.response;

import lombok.*;

@Getter
@Setter
@Builder
public class UserLoginResponse {
    private String accessToken;
    private Boolean isFirstLogin;
}
