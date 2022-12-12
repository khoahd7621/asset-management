package com.nashtech.assignment.utils;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

import org.springframework.stereotype.Component;

import com.nashtech.assignment.data.entities.User;

@Component
public class GeneratePassword {
    public String generatePassword(User user) {
        DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        String birthDate = dateFormat.format(user.getDateOfBirth());
        String[] birthArray = birthDate.split("/");
        String birthPassword = birthArray[0] + birthArray[1] + birthArray[2];
        String password = user.getUsername() + "@" + birthPassword;
        return password;
    }
}
