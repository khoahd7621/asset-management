package com.nashtech.assignment.services.validation.impl;

import com.nashtech.assignment.data.entities.User;
import com.nashtech.assignment.data.repositories.UserRepository;
import com.nashtech.assignment.exceptions.NotFoundException;
import com.nashtech.assignment.services.validation.ValidationUserService;
import lombok.Builder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@Builder
public class ValidationUserServiceImpl implements ValidationUserService {

    @Autowired
    private UserRepository userRepository;

    @Override
    public User validationUserAssignedToAssignment(long userId) {
        Optional<User> userOpt = userRepository.findByIdAndIsDeletedFalse(userId);
        if (userOpt.isEmpty()) {
            throw new NotFoundException("Not exist user with this user id.");
        }
        return userOpt.get();
    }
}
