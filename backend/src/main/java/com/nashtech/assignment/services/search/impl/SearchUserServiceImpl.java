package com.nashtech.assignment.services.search.impl;

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
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

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
