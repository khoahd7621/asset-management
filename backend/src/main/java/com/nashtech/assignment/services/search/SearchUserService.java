package com.nashtech.assignment.services.search;

import com.nashtech.assignment.data.constants.EUserType;
import com.nashtech.assignment.dto.request.user.SearchUserRequest;
import com.nashtech.assignment.dto.response.PaginationResponse;
import com.nashtech.assignment.dto.response.user.UserResponse;

import java.util.List;

public interface SearchUserService {
    PaginationResponse<List<UserResponse>> searchByNameOrStaffCodeAndFilterByTypeAndLocation(
            Integer pageNumber , String name, String staffCode, EUserType type, String location);

    PaginationResponse<List<UserResponse>> filterByType(EUserType type, int page, String location);

    PaginationResponse<List<UserResponse>>  findByLocation(String location, Integer pageNumber);

    PaginationResponse<List<UserResponse>> searchAllUsersByKeyWordInTypesWithPagination(SearchUserRequest searchUserRequest);
}
