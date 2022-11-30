package com.nashtech.assignment.services.impl;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.nashtech.assignment.data.constants.EAssetStatus;
import com.nashtech.assignment.data.entities.Asset;
import com.nashtech.assignment.data.entities.User;
import com.nashtech.assignment.data.repositories.AssetRepository;
import com.nashtech.assignment.data.repositories.UserRepository;
import com.nashtech.assignment.dto.request.asset.EditAssetInformationRequest;
import com.nashtech.assignment.dto.request.user.ChangePasswordFirstRequest;
import com.nashtech.assignment.dto.request.user.ChangePasswordRequest;
import com.nashtech.assignment.dto.request.user.EditUserRequest;
import com.nashtech.assignment.dto.response.asset.AssetResponse;
import com.nashtech.assignment.dto.response.user.UserResponse;
import com.nashtech.assignment.exceptions.BadRequestException;
import com.nashtech.assignment.exceptions.NotFoundException;
import com.nashtech.assignment.mappers.AssetMapper;
import com.nashtech.assignment.mappers.UserMapper;
import com.nashtech.assignment.services.EditService;
import com.nashtech.assignment.services.SecurityContextService;
import com.nashtech.assignment.utils.GeneratePassword;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class EditServiceImpl implements EditService {

    @Autowired
    UserRepository userRepository;
    @Autowired
    UserMapper userMapper;
    @Autowired
    private AssetRepository assetRepository;
    @Autowired
    private SecurityContextService securityContextService;
    @Autowired
    private AssetMapper assetMapper;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private GeneratePassword generatePassword;

    @Override
    public AssetResponse editAssetInformation(Long idAsset, EditAssetInformationRequest editAssetInformationRequest)
            throws ParseException {
        Optional<Asset> assetOpt = assetRepository.findById(idAsset);

        if (assetOpt.isEmpty()) {
            throw new NotFoundException("Asset not found");
        }

        Asset asset = assetOpt.get();

        if (asset.getStatus() == EAssetStatus.ASSIGNED) {
            throw new BadRequestException("Asset have state is assigned");
        }
        SimpleDateFormat sourceFormat = new SimpleDateFormat("dd/MM/yyyy");
        Date installedDate = sourceFormat.parse(editAssetInformationRequest.getInstalledDate());

        asset.setName(editAssetInformationRequest.getAssetName());
        asset.setSpecification(editAssetInformationRequest.getSpecification());
        asset.setStatus(editAssetInformationRequest.getAssetStatus());
        asset.setInstalledDate(installedDate);
        assetRepository.save(asset);
        return assetMapper.mapEntityToEditAssetInformationResponse(asset);
    }

    @Override
    public UserResponse editUserInformation(EditUserRequest userRequest) throws ParseException {
        User user = userRepository.findByStaffCode(userRequest.getStaffCode());

        if (user == null) {
            throw new NotFoundException("Cannot found staff with Id " + userRequest.getStaffCode());
        }

        SimpleDateFormat formatterDate = new SimpleDateFormat("dd/MM/yyyy");
        formatterDate.setLenient(false);

        Date dateOfBirth = formatterDate.parse(userRequest.getDateOfBirth());
        LocalDate birth = LocalDate.ofInstant(dateOfBirth.toInstant(), ZoneId.systemDefault());
        long age = LocalDate.from(birth).until(LocalDate.now(), ChronoUnit.YEARS);

        if (age < 18) {
            throw new BadRequestException("Age cannot below 18.");
        }

        Date joinedDate = formatterDate.parse(userRequest.getJoinedDate());
        LocalDate joinedDay = LocalDate.ofInstant(joinedDate.toInstant(), ZoneId.systemDefault());
        if (LocalDate.from(joinedDay).until(LocalDate.now(), ChronoUnit.YEARS) > 18) {
            throw new BadRequestException("Joined date must lager or equal 18 years.");
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
        return userMapper.mapEntityToResponseDto(user);
    }

    @Override
    public UserResponse changePasswordFirst(ChangePasswordFirstRequest changePasswordFirstRequest) {
        User user = securityContextService.getCurrentUser();
        if (passwordEncoder.matches(changePasswordFirstRequest.getNewPassword(), user.getPassword())) {
            throw new BadRequestException("Password no change");
        }
        user.setPassword(passwordEncoder.encode(changePasswordFirstRequest.getNewPassword()));
        user = userRepository.save(user);
        return userMapper.mapEntityToResponseDto(user);
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
        if (passwordEncoder.matches(changePasswordRequest.getNewPassword(), generatePassword.firstPassword(user))) {
            throw new BadRequestException("Password same password generated");
        }
        user.setPassword(passwordEncoder.encode(changePasswordRequest.getNewPassword()));
        user = userRepository.save(user);
        return userMapper.mapEntityToResponseDto(user);
    }

}
