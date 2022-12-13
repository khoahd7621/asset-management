package com.nashtech.assignment.data.constants;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class Constants {
    public static final String DATE_FORMAT = "dd/MM/yyyy";
    public static final String ONLY_ALPHABET_REGEX = "^.*[A-Za-z].*$";
    public static final String ONLY_ALPHABET_AND_SPACE_REGEX = "^.*[A-Za-z].*[\\s]?+.*$";
    public static final String ONLY_ALPHABET_AND_NUMBER_REGEX = "^[a-zA-Z0-9]*$";
    public static final String ONLY_ALPHABET_AND_NUMBER_AND_SPACE_REGEX = "^[a-zA-Z0-9 ]*$";
    public static final int MIN_LENGTH_PASSWORD = 6;
    public static final int MAX_LENGTH_PASSWORD = 24;
    public static final int MAX_LENGTH_INPUT = 100;
    public static final int MAX_LENGTH_TEXTAREA = 500;
}
