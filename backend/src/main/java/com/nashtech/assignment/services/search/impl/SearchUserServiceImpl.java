package com.nashtech.assignment.services.search.impl;

import com.nashtech.assignment.data.constants.EUserType;
import com.nashtech.assignment.data.entities.User;
import com.nashtech.assignment.data.repositories.UserRepository;
import com.nashtech.assignment.dto.request.user.SearchUserRequest;
import com.nashtech.assignment.dto.response.PaginationResponse;
import com.nashtech.assignment.dto.response.user.UserResponse;
import com.nashtech.assignment.mappers.UserMapper;
import com.nashtech.assignment.services.auth.SecurityContextService;
import com.nashtech.assignment.services.search.SearchUserService;
import com.nashtech.assignment.utils.PageableUtil;
import lombok.Builder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

@Service
@Builder
public class SearchUserServiceImpl implements SearchUserService {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private PageableUtil pageableUtil;
    @Autowired
    private SecurityContextService securityContextService;

    @Override
    public PaginationResponse<List<UserResponse>> findByLocation(String location, Integer pageNumber) {
        Pageable pageWithNumberAndSize = PageRequest.of(pageNumber, 20);

        Page<User> user = userRepository.findByLocationAndIsDeletedFalseOrderByFirstNameAsc(location, pageWithNumberAndSize);

        if (user == null || user.isEmpty()) {
            return new PaginationResponse<>(Collections.emptyList(), 0, 0);
        }

        return new PaginationResponse<>(
                userMapper.toListUserResponses(user.getContent()),
                user.getTotalPages(),
                user.getTotalElements());
    }

    @Override
    public PaginationResponse<List<UserResponse>> searchByNameOrStaffCodeAndFilterByTypeAndLocation(
            Integer page, String name, String staffCode, EUserType type, String location) {
        Pageable pageable = PageRequest.of(page, 20);
        Page<User> result = null;
        name = "%" + name + "%";
        if (type != EUserType.ADMIN && type != EUserType.STAFF) {
            result = userRepository.search(name, staffCode, location, type, pageable);
            if (result == null || result.isEmpty()) {
                return new PaginationResponse<>(Collections.emptyList(), 0, 0);
            }
            return new PaginationResponse<>(
                    userMapper.toListUserResponses(result.getContent()),
                    result.getTotalPages(),
                    result.getTotalElements());
        }
        result = userRepository.search(name, staffCode, location, type, pageable);
        if (result == null || result.isEmpty()) {
            return new PaginationResponse<>(
                    Collections.emptyList(), 0, 0);
        }
        return new PaginationResponse<>(
                userMapper.toListUserResponses(result.getContent()),
                result.getTotalPages(),
                result.getTotalElements());
    }

    @Override
    public PaginationResponse<List<UserResponse>> filterByType(EUserType type, int page, String location) {
        Pageable pagination = PageRequest.of(page, 20, Sort.by("firstName"));
        Page<User> users = null;
        PaginationResponse<List<UserResponse>> result = null;
        if (type != EUserType.ADMIN && type != EUserType.STAFF) {
            Page<User> pageUsers = userRepository.findByLocationAndIsDeletedFalseOrderByFirstNameAsc(location, pagination);
            if (pageUsers == null || pageUsers.isEmpty()) {
                return new PaginationResponse<>(
                        Collections.emptyList(), 0, 0);
            }
            result = new PaginationResponse<>(
                    userMapper.toListUserResponses(pageUsers.getContent()),
                    pageUsers.getTotalPages(),
                    pageUsers.getTotalElements());
            return result;
        }
        users = userRepository.findByLocationAndTypeAndIsDeletedFalseOrderByFirstNameAsc(location, type, pagination);
        if (users == null || users.isEmpty()) {
            return new PaginationResponse<>(
                    Collections.emptyList(), 0, 0);
        }
        result = new PaginationResponse<>(
                userMapper.toListUserResponses(users.getContent()),
                users.getTotalPages(),
                users.getTotalElements());
        return result;
    }

    @Override
    public PaginationResponse<List<UserResponse>> searchAllUsersByKeyWordInTypesWithPagination(SearchUserRequest searchUserRequest) {
        User user = securityContextService.getCurrentUser();
        Pageable pageable = pageableUtil.getPageable(searchUserRequest.getPage(),
                searchUserRequest.getLimit(), searchUserRequest.getSortField(),
                searchUserRequest.getSortType());
        Page<User> userPage = userRepository.searchAllUsersByKeyWordInTypesWithPagination(
                searchUserRequest.getKeyword(),
                searchUserRequest.getTypes(),
                user.getLocation(),
                pageable);
        List<UserResponse> userResponseList = userMapper.toListUserResponses(userPage.toList());
        return PaginationResponse.<List<UserResponse>>builder()
                .data(userResponseList)
                .totalRow(userPage.getTotalElements())
                .totalPage(userPage.getTotalPages()).build();
    }
}
