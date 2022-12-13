package com.nashtech.assignment.services.search;

import com.nashtech.assignment.dto.request.user.SearchUserRequest;
import com.nashtech.assignment.dto.response.PaginationResponse;
import com.nashtech.assignment.dto.response.user.UserResponse;

import java.util.List;

public interface SearchUserService {
    PaginationResponse<List<UserResponse>> searchAllUsersByKeyWordInTypesWithPagination(SearchUserRequest searchUserRequest);
}
