package com.nashtech.assignment.services;

import java.util.List;

import com.nashtech.assignment.data.constants.EUserType;
import com.nashtech.assignment.dto.response.PaginationResponse;
import com.nashtech.assignment.dto.response.user.UserResponse;

public interface FindService {
    PaginationResponse<List<UserResponse>>searchByNameOrStaffCodeAndFilterByTypeAndLocation(
            Integer pageNumber ,String name, String staffCode, EUserType type, String location);

    PaginationResponse<List<UserResponse>> filterByType(EUserType type, int page, String location);

    PaginationResponse<List<UserResponse>>  findByLocation(String location, Integer pageNumber);

    UserResponse viewUserDetails(String staffCode);
}
