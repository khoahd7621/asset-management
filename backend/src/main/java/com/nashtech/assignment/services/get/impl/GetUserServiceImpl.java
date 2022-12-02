package com.nashtech.assignment.services.get.impl;

import com.nashtech.assignment.data.entities.User;
import com.nashtech.assignment.data.repositories.UserRepository;
import com.nashtech.assignment.dto.response.user.UserResponse;
import com.nashtech.assignment.mappers.UserMapper;
import com.nashtech.assignment.services.SecurityContextService;
import com.nashtech.assignment.services.get.GetUserService;
import lombok.Builder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Builder
public class GetUserServiceImpl implements GetUserService {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private SecurityContextService securityContextService;

    @Override
    public List<UserResponse> getAllUsers() {
        User user = securityContextService.getCurrentUser();
        List<User> userList = userRepository.findAllByLocationAndIsDeletedFalse(user.getLocation());
        return userMapper.mapListEntityUserResponses(userList);
    }
}
