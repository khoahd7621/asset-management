package com.nashtech.assignment.services.validation;

import com.nashtech.assignment.data.entities.User;

public interface ValidationUserService {
    User validationUserAssignedToAssignment(long userId);

    boolean checkValidUserForDelete(String staffCode);
}
