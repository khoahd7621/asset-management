package com.nashtech.assignment.utils;

import org.springframework.stereotype.Component;

import com.nashtech.assignment.data.entities.User;

@Component
public class GeneratePassword {
    public String generatePassword(User user) {
        String birthDate = user.getDateOfBirth().toString().split(" ")[0];
        String[] birthArray = birthDate.split("-");
        String birthPassword = birthArray[2] + birthArray[1] + birthArray[0];
        String password = user.getUsername() + "@" + birthPassword;
        return password;
    }
}
