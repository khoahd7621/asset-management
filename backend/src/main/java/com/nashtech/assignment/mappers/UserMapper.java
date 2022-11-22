package com.nashtech.assignment.mappers;

import com.nashtech.assignment.data.entities.User;
import com.nashtech.assignment.dto.request.CreateNewUserRequest;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {
    public User toUser(CreateNewUserRequest createNewUserRequest) {
        return User.builder()
                .firstName(createNewUserRequest.getFirstName())
                .lastName(createNewUserRequest.getLastName())
                .gender(createNewUserRequest.getGender())
                .type(createNewUserRequest.getType())
                .isDeleted(false)
                .build();
    }
}
