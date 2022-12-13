package com.nashtech.assignment.services.user.impl;

import com.nashtech.assignment.data.constants.Constants;
import com.nashtech.assignment.data.constants.EUserType;
import com.nashtech.assignment.data.entities.User;
import com.nashtech.assignment.data.repositories.AssignAssetRepository;
import com.nashtech.assignment.data.repositories.UserRepository;
import com.nashtech.assignment.dto.request.user.ChangePasswordFirstRequest;
import com.nashtech.assignment.dto.request.user.ChangePasswordRequest;
import com.nashtech.assignment.dto.request.user.CreateNewUserRequest;
import com.nashtech.assignment.dto.request.user.EditUserRequest;
import com.nashtech.assignment.dto.response.user.UserResponse;
import com.nashtech.assignment.exceptions.BadRequestException;
import com.nashtech.assignment.exceptions.ForbiddenException;
import com.nashtech.assignment.exceptions.NotFoundException;
import com.nashtech.assignment.mappers.UserMapper;
import com.nashtech.assignment.services.auth.SecurityContextService;
import com.nashtech.assignment.services.user.UserService;
import lombok.Builder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.List;

@Service
@Builder
public class UserServiceImpl implements UserService {
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private SecurityContextService securityContextService;
    @Autowired
    private AssignAssetRepository assignAssetRepository;

    public UserResponse createNewUser(CreateNewUserRequest createNewUserRequest) throws ParseException {
        SimpleDateFormat formatterDate = new SimpleDateFormat(Constants.DATE_FORMAT);
        Date dateOfBirth = formatterDate.parse(createNewUserRequest.getDateOfBirth());
        LocalDate birth = LocalDate.ofInstant(dateOfBirth.toInstant(), ZoneId.systemDefault());
        long age = LocalDate.from(birth).until(LocalDate.now(), ChronoUnit.YEARS);
        if (age < 18) {
            throw new BadRequestException("Age cannot below 18.");
        }
        Date joinedDate = formatterDate.parse(createNewUserRequest.getJoinedDate());
        LocalDate joinedDay = LocalDate.ofInstant(joinedDate.toInstant(), ZoneId.systemDefault());
        if (dateOfBirth.after(joinedDate)) {
            throw new BadRequestException("Joined date cannot be after birth date.");
        }
        DayOfWeek day = joinedDay.getDayOfWeek();
        if (day == DayOfWeek.SATURDAY || day == DayOfWeek.SUNDAY) {
            throw new BadRequestException("Joined date cannot be Saturday or Sunday.");
        }
        User user = userMapper.toUser(createNewUserRequest);
        if (createNewUserRequest.getType() == EUserType.STAFF) {
            user.setLocation(securityContextService.getCurrentUser().getLocation());
        } else {
            if (createNewUserRequest.getLocation() == null || createNewUserRequest.getLocation().trim().equals(""))
                throw new BadRequestException("User type of ADMIN so location cannot be blank.");
            user.setLocation(createNewUserRequest.getLocation());
        }
        user = userRepository.save(user);
        NumberFormat formatter = new DecimalFormat("0000");
        String numberId = formatter.format(user.getId());
        StringBuilder staffCode = new StringBuilder("SD");
        StringBuilder userNameCode = new StringBuilder(createNewUserRequest.getFirstName());
        String[] listSingleWordLastName = createNewUserRequest.getLastName().split(" ");
        for (String word : listSingleWordLastName) {
            userNameCode.append(word.toLowerCase().charAt(0));
        }
        List<User> list = userRepository.findAllByUsernameMatchRegex(userNameCode.toString().toLowerCase() + "[0-9]?");
        if (!list.isEmpty()) {
            userNameCode.append(list.size());
        }
        user.setStaffCode(staffCode.append(numberId).toString());
        user.setUsername(userNameCode.toString().toLowerCase());
        user.setDateOfBirth(dateOfBirth);
        user.setJoinedDate(joinedDate);
        user.setFirstLogin(true);
        String birthPassword = createNewUserRequest.getDateOfBirth().replace("/", "");
        String password = user.getUsername() + "@" + birthPassword;
        user.setPassword(passwordEncoder.encode(password));
        userRepository.save(user);
        return userMapper.toUserResponse(user);
    }

    @Override
    public void deleteUser(String staffCode) {
        if (securityContextService.getCurrentUser().getStaffCode().equals(staffCode)) {
            throw new BadRequestException("Cannot disable yourself");
        }
        User user = userRepository.findByStaffCode(staffCode);
        if (user == null) {
            throw new NotFoundException("Cannot found user with staff code " + staffCode);
        }
        if (Boolean.TRUE.equals(assignAssetRepository.existsByUserAssignedToAndIsDeletedFalse(user))
                || Boolean.TRUE.equals(assignAssetRepository.existsByUserAssignedByAndIsDeletedFalse(user))) {
            throw new BadRequestException(
                    "There are valid assignments belonging to this user. Please close all assignments before disabling user.");
        }
        user.setDeleted(true);
        userRepository.save(user);
    }

    @Override
    public UserResponse editUserInformation(EditUserRequest userRequest) throws ParseException {
        User user = userRepository.findByStaffCode(userRequest.getStaffCode());
        if (user == null) {
            throw new NotFoundException(
                    "Cannot found staff with Id " + userRequest.getStaffCode());
        }
        SimpleDateFormat formatterDate = new SimpleDateFormat(Constants.DATE_FORMAT);
        formatterDate.setLenient(false);
        Date dateOfBirth = formatterDate.parse(userRequest.getDateOfBirth());
        LocalDate birth = LocalDate.ofInstant(dateOfBirth.toInstant(), ZoneId.systemDefault());
        long age = LocalDate.from(birth).until(LocalDate.now(), ChronoUnit.YEARS);
        if (age < 18) {
            throw new BadRequestException("Age cannot below 18.");
        }
        Date joinedDate = formatterDate.parse(userRequest.getJoinedDate());
        LocalDate joinedDay = LocalDate.ofInstant(joinedDate.toInstant(),
                ZoneId.systemDefault());
        if (LocalDate.from(joinedDay).until(LocalDate.now(),
                ChronoUnit.YEARS) > 18) {
            throw new BadRequestException("Joined date must lager or equal 18 years.");
        }
        if (LocalDate.from(joinedDay).until(LocalDate.now(), ChronoUnit.YEARS) < 0) {
            throw new BadRequestException("Joined date cannot lager than 100 years.");
        }
        DayOfWeek day = joinedDay.getDayOfWeek();
        if (day == DayOfWeek.SATURDAY || day == DayOfWeek.SUNDAY) {
            throw new BadRequestException("Joined date cannot be Saturday or Sunday.");
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
        if (!user.isFirstLogin()) {
            throw new BadRequestException("Is not first login");
        }
        if (passwordEncoder.matches(changePasswordFirstRequest.getNewPassword(), user.getPassword())) {
            throw new BadRequestException("Password no change");
        }
        user.setPassword(passwordEncoder.encode(changePasswordFirstRequest.getNewPassword()));
        user.setFirstLogin(false);
        user = userRepository.save(user);
        return userMapper.toUserResponse(user);
    }

    @Override
    public UserResponse changePassword(ChangePasswordRequest changePasswordRequest) {
        User user = securityContextService.getCurrentUser();
        if (!passwordEncoder.matches(changePasswordRequest.getOldPassword(), user.getPassword())) {
            throw new BadRequestException("Password is incorrect");
        }
        if (changePasswordRequest.getNewPassword().equals(changePasswordRequest.getOldPassword())) {
            throw new BadRequestException("Password no change");
        }
        user.setPassword(passwordEncoder.encode(changePasswordRequest.getNewPassword()));
        user = userRepository.save(user);
        return userMapper.toUserResponse(user);
    }

    @Override
    public UserResponse viewUserDetails(String staffCode) {
        User user = userRepository.findByStaffCode(staffCode);
        if (user == null) {
            throw new NotFoundException("Cannot Found Staff With Code: " + staffCode);
        }
        return userMapper.toUserResponse(user);
    }

    @Override
    public UserResponse getCurrentUserLoggedInInformation(String username) {
        User user = securityContextService.getCurrentUser();
        if (!user.getUsername().equals(username)) {
            throw new ForbiddenException("You don't have permission to get other user information.");
        }
        return userMapper.toUserResponse(user);
    }
}
