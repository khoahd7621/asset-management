package com.nashtech.assignment.services.edit.impl;

import com.nashtech.assignment.data.entities.User;
import com.nashtech.assignment.data.repositories.UserRepository;
import com.nashtech.assignment.dto.request.user.ChangePasswordFirstRequest;
import com.nashtech.assignment.dto.request.user.ChangePasswordRequest;
import com.nashtech.assignment.dto.request.user.EditUserRequest;
import com.nashtech.assignment.dto.response.user.UserResponse;
import com.nashtech.assignment.exceptions.BadRequestException;
import com.nashtech.assignment.exceptions.NotFoundException;
import com.nashtech.assignment.mappers.UserMapper;
import com.nashtech.assignment.services.auth.SecurityContextService;
import com.nashtech.assignment.services.edit.EditUserService;
import com.nashtech.assignment.utils.GeneratePassword;
import lombok.Builder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.Date;

@Service
@Builder
public class EditUserServiceImpl implements EditUserService {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private SecurityContextService securityContextService;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private GeneratePassword generatePassword;

    @Override
    public UserResponse editUserInformation(EditUserRequest userRequest)
            throws ParseException {
        User user = userRepository.findByStaffCode(userRequest.getStaffCode());

        if (user == null) {
            throw new NotFoundException(
                    "Cannot found staff with Id " + userRequest.getStaffCode());
        }

        SimpleDateFormat formatterDate = new SimpleDateFormat("dd/MM/yyyy");
        formatterDate.setLenient(false);

        Date dateOfBirth = formatterDate.parse(userRequest.getDateOfBirth());
        LocalDate birth = LocalDate.ofInstant(dateOfBirth.toInstant(),
                ZoneId.systemDefault());
        long age = LocalDate.from(birth).until(LocalDate.now(), ChronoUnit.YEARS);

        if (age < 18) {
            throw new BadRequestException("Age cannot below 18.");
        }

        Date joinedDate = formatterDate.parse(userRequest.getJoinedDate());
        LocalDate joinedDay = LocalDate.ofInstant(joinedDate.toInstant(),
                ZoneId.systemDefault());
        if (LocalDate.from(joinedDay).until(LocalDate.now(),
                ChronoUnit.YEARS) > 18) {
            throw new BadRequestException(
                    "Joined date must lager or equal 18 years.");
        }
        if (LocalDate.from(joinedDay).until(LocalDate.now(), ChronoUnit.YEARS) < 0) {
            throw new BadRequestException(
                    "Joined date cannot lager than 100 years.");
        }

        DayOfWeek day = joinedDay.getDayOfWeek();
        if (day == DayOfWeek.SATURDAY || day == DayOfWeek.SUNDAY) {
            throw new BadRequestException(
                    "Joined date cannot be Saturday or Sunday.");
        }
        user.setDateOfBirth(dateOfBirth);
        user.setJoinedDate(joinedDate);
        user.setGender(userRequest.getGender());
        user.setType(userRequest.getType());
        user = userRepository.save(user);
        return userMapper.toUserResponse(user);
    }

    @Override
    public UserResponse changePasswordFirst(ChangePasswordFirstRequest changePasswordFirstRequest) {
        User user = securityContextService.getCurrentUser();

        if (!passwordEncoder.matches(generatePassword.generatePassword(user), user.getPassword())) {
            throw new BadRequestException("Is not first login");
        }
        if (passwordEncoder.matches(changePasswordFirstRequest.getNewPassword(), user.getPassword())) {
            throw new BadRequestException("Password no change");
        }
        user.setPassword(
                passwordEncoder.encode(changePasswordFirstRequest.getNewPassword()));
        user = userRepository.save(user);
        return userMapper.toUserResponse(user);
    }

    @Override
    public UserResponse changePassword(ChangePasswordRequest changePasswordRequest) {
        User user = securityContextService.getCurrentUser();
        if (!passwordEncoder.matches(changePasswordRequest.getOldPassword(),
                user.getPassword())) {
            throw new BadRequestException("Password is incorrect");
        }
        if (changePasswordRequest.getNewPassword()
                .equals(changePasswordRequest.getOldPassword())) {
            throw new BadRequestException("Password no change");
        }
        if (passwordEncoder.matches(changePasswordRequest.getNewPassword(),
                passwordEncoder.encode(generatePassword.generatePassword(user)))) {
            throw new BadRequestException("Password same password generated");
        }
        user.setPassword(passwordEncoder.encode(changePasswordRequest.getNewPassword()));
        user = userRepository.save(user);
        return userMapper.toUserResponse(user);
    }
}
