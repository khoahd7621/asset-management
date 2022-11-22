package com.nashtech.assignment.services.impl;

import com.nashtech.assignment.data.constants.EUserType;
import com.nashtech.assignment.data.entities.User;
import com.nashtech.assignment.data.repositories.UserRepository;
import com.nashtech.assignment.dto.request.CreateNewUserRequest;
import com.nashtech.assignment.exceptions.BadRequestException;
import com.nashtech.assignment.mappers.UserMapper;
import com.nashtech.assignment.services.CreateService;
import com.nashtech.assignment.services.SecurityContextService;
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
public class CreateServiceImpl implements CreateService {

    @Autowired
    private UserMapper userMapper;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private SecurityContextService securityContextService;

    public void createNewUser(CreateNewUserRequest createNewUserRequest) throws ParseException {
        SimpleDateFormat formatterDate = new SimpleDateFormat("dd/MM/yyyy");
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
        if (list.size() > 0) {
            userNameCode.append(list.size());
        }

        user.setStaffCode(staffCode.append(numberId).toString());
        user.setUsername(userNameCode.toString().toLowerCase());
        user.setDateOfBirth(dateOfBirth);
        user.setJoinedDate(joinedDate);
        String birthPassword = createNewUserRequest.getDateOfBirth().replace("/", "");
        String password = user.getUsername() + "@" + birthPassword;
        user.setPassword(passwordEncoder.encode(password));

        userRepository.save(user);
    }
}
