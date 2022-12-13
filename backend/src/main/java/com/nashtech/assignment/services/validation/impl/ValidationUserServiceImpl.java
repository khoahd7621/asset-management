package com.nashtech.assignment.services.validation.impl;

import com.nashtech.assignment.data.entities.User;
import com.nashtech.assignment.data.repositories.AssignAssetRepository;
import com.nashtech.assignment.data.repositories.UserRepository;
import com.nashtech.assignment.exceptions.BadRequestException;
import com.nashtech.assignment.exceptions.NotFoundException;
import com.nashtech.assignment.services.auth.SecurityContextService;
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
    @Autowired
    private SecurityContextService securityContextService;
    @Autowired
    private AssignAssetRepository assignAssetRepository;

    @Override
    public User validationUserAssignedToAssignment(long userId) {
        Optional<User> userOpt = userRepository.findByIdAndIsDeletedFalse(userId);
        if (userOpt.isEmpty()) {
            throw new NotFoundException("Not exist user with this user id.");
        }
        return userOpt.get();
    }

    @Override
    public boolean checkValidUserForDelete(String staffCode) {
        if (securityContextService.getCurrentUser().getStaffCode().equals(staffCode)) {
            throw new BadRequestException("Cannot disable yourself");
        }
        User user = userRepository.findByStaffCode(staffCode);
        if (user == null) {
            throw new NotFoundException("Cannot found user with staff code " + staffCode);
        }
        if (Boolean.TRUE.equals(assignAssetRepository.existsByUserAssignedToAndIsDeletedFalse(user))
                || Boolean.TRUE.equals(assignAssetRepository.existsByUserAssignedByAndIsDeletedFalse(user))) {
            throw new BadRequestException(
                    "There are valid assignments belonging to this user. Please close all assignments before disabling user.");
        }
        return true;
    }
}
