package com.nashtech.assignment.services.impl;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.nashtech.assignment.data.entities.User;
import com.nashtech.assignment.data.repositories.UserRepository;
import com.nashtech.assignment.dto.request.UserLoginRequest;
import com.nashtech.assignment.dto.response.UserLoginResponse;
import com.nashtech.assignment.exceptions.BadRequestException;
import com.nashtech.assignment.services.LoginService;
import com.nashtech.assignment.utils.GeneratePassword;
import com.nashtech.assignment.utils.JwtTokenUtil;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class LoginServiceImpl implements LoginService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private GeneratePassword generatePassword;

    @Override
    public UserLoginResponse login(UserLoginRequest userLoginRequest) {
        Boolean isFirstLogin = false;
        String username = userLoginRequest.getUsername();
        Optional<User> userOpt = userRepository.findByUsername(username);
        if (userOpt.isEmpty()) {
            throw new BadRequestException("Username not found");
        }
        if (userOpt.get().isDeleted() == true) {
            throw new BadRequestException("This account has been disabled");
        }
        if (!passwordEncoder.matches(userLoginRequest.getPassword(), userOpt.get().getPassword())) {
            throw new BadRequestException("Username or password is incorrect. Please try again");
        }
        if (passwordEncoder.matches(userLoginRequest.getPassword(), generatePassword.firstPassword(userOpt.get()))) {
            isFirstLogin = true;
        }
        String token = jwtTokenUtil.generateJwtToken(userOpt.get());
        UserLoginResponse userLoginResponse = UserLoginResponse.builder().accessToken(token).isFirstLogin(isFirstLogin)
                .build();
        return userLoginResponse;
    }

}
