package com.nashtech.assignment.services.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.nashtech.assignment.data.entities.User;
import com.nashtech.assignment.data.repositories.AssignAssetRepository;
import com.nashtech.assignment.data.repositories.UserRepository;
import com.nashtech.assignment.exceptions.BadRequestException;
import com.nashtech.assignment.exceptions.NotFoundException;
import com.nashtech.assignment.services.DeleteService;
import com.nashtech.assignment.services.SecurityContextService;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class DeleteServiceImpl implements DeleteService {
    @Autowired
    AssignAssetRepository assignAssetRepository;
    @Autowired
    UserRepository userRepository;
    @Autowired
    SecurityContextService securityContextService;

    @Override
    public void deleteUser(String staffCode) {
        if (securityContextService.getCurrentUser().getStaffCode().equals(staffCode)) {
            throw new BadRequestException("Cannot disable yourselft");
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

        user.setDeleted(true);
        userRepository.save(user);

    }

    @Override
    public boolean checkValidUser(String staffCode) {
        if (securityContextService.getCurrentUser().getStaffCode().equals(staffCode)) {
            throw new BadRequestException("Cannot disable yourselft");
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
