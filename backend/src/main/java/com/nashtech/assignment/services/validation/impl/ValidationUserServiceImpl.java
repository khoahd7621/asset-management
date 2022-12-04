package com.nashtech.assignment.services.validation.impl;

import com.nashtech.assignment.data.entities.User;
import com.nashtech.assignment.data.repositories.UserRepository;
import com.nashtech.assignment.exceptions.BadRequestException;
import com.nashtech.assignment.exceptions.NotFoundException;
import com.nashtech.assignment.services.validation.ValidationUserService;
import com.nashtech.assignment.utils.CompareDateUtil;
import lombok.Builder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Optional;

@Service
@Builder
public class ValidationUserServiceImpl implements ValidationUserService {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private CompareDateUtil compareDateUtil;

    @Override
    public User validationUserAssignedToAssignment(long userId, Date assignedDate) {
        Optional<User> userOpt = userRepository.findByIdAndIsDeletedFalse(userId);
        if (userOpt.isEmpty()) {
            throw new NotFoundException("Not exist user with this user id.");
        }
        if (compareDateUtil.isBefore(assignedDate, userOpt.get().getJoinedDate())) {
            throw new BadRequestException("Assigned date cannot before joined date of user.");
        }
        return userOpt.get();
    }
}
