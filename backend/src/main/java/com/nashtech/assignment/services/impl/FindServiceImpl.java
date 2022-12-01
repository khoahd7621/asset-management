package com.nashtech.assignment.services.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.util.Collections;

import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import com.nashtech.assignment.data.constants.EUserType;
import com.nashtech.assignment.data.entities.User;
import com.nashtech.assignment.data.repositories.UserRepository;
import com.nashtech.assignment.dto.response.PaginationResponse;
import com.nashtech.assignment.dto.response.user.UserResponse;
import com.nashtech.assignment.exceptions.NotFoundException;
import com.nashtech.assignment.mappers.UserMapper;
import com.nashtech.assignment.services.FindService;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class FindServiceImpl implements FindService {
    @Autowired
    UserRepository userRepository;
    @Autowired
    UserMapper userMapper;

    @Override
    public PaginationResponse<List<UserResponse>> findByLocation(String location, Integer pageNumber) {
        Pageable pageWithNumberAndSize = PageRequest.of(pageNumber, 20);

        Page<User> user = userRepository.findByLocationAndIsDeletedFalseOrderByFirstNameAsc(location, pageWithNumberAndSize);

        if(user == null || user.isEmpty()){
            return new PaginationResponse<>(Collections.emptyList(), 0, 0);
        }

        return new PaginationResponse<>(
            userMapper.mapListEntityUserResponses(user.getContent()), 
            user.getTotalPages(), 
            user.getTotalElements());
    }

    @Override
    public UserResponse viewUserDetails(String staffCode) {
        User user = userRepository.findByStaffCode(staffCode);
        if (user == null) {
            throw new NotFoundException("Cannot Found Staff With Code: " + staffCode);
        }
        return userMapper.mapEntityToResponseDto(user);
    }

    @Override
    public PaginationResponse<List<UserResponse>> searchByNameOrStaffCodeAndFilterByTypeAndLocation(
            Integer page, String name, String staffCode, EUserType type, String location) {
        Pageable pageable = PageRequest.of(page, 20);
        Page<User> result = null;
        name = "%" + name + "%";
        if (type != EUserType.ADMIN && type != EUserType.STAFF) {
            result = userRepository.search(name,staffCode,location, type, pageable);
            if (result == null || result.isEmpty()) {
                return new PaginationResponse<>( Collections.emptyList(), 0, 0);
            }
            return new PaginationResponse<>(
                userMapper.mapListEntityUserResponses(result.getContent()), 
                result.getTotalPages(), 
                result.getTotalElements());
        }
        result = userRepository.search(name,staffCode, location, type, pageable);
        if (result == null || result.isEmpty()) {
            return new PaginationResponse<>(
                Collections.emptyList(), 0, 0);
        }
        return new PaginationResponse<>(
            userMapper.mapListEntityUserResponses(result.getContent()), 
            result.getTotalPages(),
            result.getTotalElements());
    }

    @Override
    public PaginationResponse<List<UserResponse>> filterByType(EUserType type, int page, String location) {
        Pageable pagination = PageRequest.of(page, 20, Sort.by("firstName"));
        Page<User> users = null;
        PaginationResponse<List<UserResponse>> result = null;
        if (type != EUserType.ADMIN && type != EUserType.STAFF) {
            Page<User> pageUsers = userRepository.findByLocationAndIsDeletedFalseOrderByFirstNameAsc(location,pagination);
            if (pageUsers == null || pageUsers.isEmpty()) {
                return new PaginationResponse<>(
                        Collections.emptyList(), 0, 0);
            }
            result = new PaginationResponse<>(
                    userMapper.mapListEntityUserResponses(pageUsers.getContent()),
                    pageUsers.getTotalPages(),
                    pageUsers.getTotalElements());
            return result;
        }
        users = userRepository.findByLocationAndTypeAndIsDeletedFalseOrderByFirstNameAsc(location,type, pagination);
        if (users == null || users.isEmpty()) {
            return new PaginationResponse<>(
                    Collections.emptyList(), 0, 0);
        }
        result = new PaginationResponse<>(
                userMapper.mapListEntityUserResponses(users.getContent()),
                users.getTotalPages(),
                users.getTotalElements());
        return result;
    }
            
}
