package com.nashtech.assignment.services.auth.impl;

import com.nashtech.assignment.data.entities.User;
import com.nashtech.assignment.data.repositories.UserRepository;
import com.nashtech.assignment.dto.request.UserLoginRequest;
import com.nashtech.assignment.dto.response.UserLoginResponse;
import com.nashtech.assignment.exceptions.BadRequestException;
import com.nashtech.assignment.services.auth.LoginService;
import com.nashtech.assignment.utils.JwtTokenUtil;
import lombok.Builder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@Builder
public class LoginServiceImpl implements LoginService {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private JwtTokenUtil jwtTokenUtil;
    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public UserLoginResponse login(UserLoginRequest userLoginRequest) {
        String username = userLoginRequest.getUsername();
        Optional<User> userOpt = userRepository.findByUsername(username);
        if (userOpt.isEmpty()) {
            throw new BadRequestException("Username or password is incorrect. Please try again");
        }
        if (!passwordEncoder.matches(userLoginRequest.getPassword(), userOpt.get().getPassword())) {
            throw new BadRequestException("Username or password is incorrect. Please try again");
        }
        if (userOpt.get().isDeleted()) {
            throw new BadRequestException("This account has been disabled");
        }
        String token = jwtTokenUtil.generateJwtToken(userOpt.get());
        return UserLoginResponse.builder().accessToken(token).isFirstLogin(userOpt.get().isFirstLogin()).build();
    }

}
