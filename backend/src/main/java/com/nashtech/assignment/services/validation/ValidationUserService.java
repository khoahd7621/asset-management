package com.nashtech.assignment.services.validation;

import com.nashtech.assignment.data.entities.User;

import java.util.Date;

public interface ValidationUserService {
    User validationUserAssignedToAssignment(long userId, Date assignedDate);
}
