package com.nashtech.assignment.mappers;

import com.nashtech.assignment.data.entities.User;
import com.nashtech.assignment.dto.request.user.CreateNewUserRequest;
import com.nashtech.assignment.dto.response.user.UserResponse;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class UserMapper {
    public UserResponse toUserResponse(User user) {
        return UserResponse.builder()
                .id(user.getId())
                .username(user.getUsername())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .fullName(user.getFirstName() + " " + user.getLastName())
                .gender(user.getGender())
                .joinedDate(user.getJoinedDate())
                .dateOfBirth(user.getDateOfBirth())
                .staffCode(user.getStaffCode())
                .location(user.getLocation())
                .isFirstLogin(user.isFirstLogin())
                .type(user.getType()).build();
    }

    public List<UserResponse> toListUserResponses(List<User> users) {
        return users.stream().map(this::toUserResponse).collect(Collectors.toList());
    }

    public User toUser(CreateNewUserRequest createNewUserRequest) {
        return User.builder()
                .firstName(createNewUserRequest.getFirstName())
                .lastName(createNewUserRequest.getLastName())
                .gender(createNewUserRequest.getGender())
                .type(createNewUserRequest.getType())
                .isDeleted(false).build();
    }
}
