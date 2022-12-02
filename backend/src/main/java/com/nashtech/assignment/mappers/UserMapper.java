package com.nashtech.assignment.mappers;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.nashtech.assignment.data.entities.User;
import com.nashtech.assignment.dto.request.user.CreateNewUserRequest;
import com.nashtech.assignment.dto.response.user.UserResponse;

@Component
public class UserMapper {
    public UserResponse mapEntityToResponseDto(User user) {
        return UserResponse.builder()
                .username(user.getUsername())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .fullName(user.getFirstName() + " " + user.getLastName())
                .gender(user.getGender())
                .joinedDate(user.getJoinedDate())
                .dateOfBirth(user.getDateOfBirth())
                .staffCode(user.getStaffCode())
                .location(user.getLocation())
                .type(user.getType())
                .build();
    }

    public List<UserResponse> mapListEntityUserResponses(List<User> users) {
        return users.stream()
                .map(this::mapEntityToResponseDto)
                .collect(Collectors.toList());
    }

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
