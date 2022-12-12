package com.nashtech.assignment.utils;

import com.nashtech.assignment.data.entities.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

public class GeneratePasswordTest {
    private GeneratePassword generatePassword;

    @BeforeEach
    void setUp() {
        generatePassword = new GeneratePassword();
    }

    @Test
    void generatePassword_ShouldReturnData() throws ParseException {
        DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        Date date = dateFormat.parse("01/01/2001");
        User user = User.builder()
                .username("test")
                .dateOfBirth(date)
                .build();

        String actual = generatePassword.generatePassword(user);

        assertThat(actual, is("test@01012001"));
    }
}
