package com.nashtech.assignment.services.get.impl;

import com.nashtech.assignment.data.entities.User;
import com.nashtech.assignment.data.repositories.UserRepository;
import com.nashtech.assignment.dto.response.user.UserResponse;
import com.nashtech.assignment.exceptions.NotFoundException;
import com.nashtech.assignment.mappers.UserMapper;
import com.nashtech.assignment.services.get.GetUserService;
import lombok.Builder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Builder
public class GetUserServiceImpl implements GetUserService {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private UserMapper userMapper;

    @Override
    public UserResponse viewUserDetails(String staffCode) {
        User user = userRepository.findByStaffCode(staffCode);
        if (user == null) {
            throw new NotFoundException("Cannot Found Staff With Code: " + staffCode);
        }
        return userMapper.mapEntityToResponseDto(user);
    }
}
