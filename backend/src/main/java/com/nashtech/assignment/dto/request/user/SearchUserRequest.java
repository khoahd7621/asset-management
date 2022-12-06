package com.nashtech.assignment.dto.request.user;

import com.nashtech.assignment.data.constants.EUserType;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Builder
public class SearchUserRequest {
    private String keyword;
    private List<EUserType> types;
    private Integer limit;
    private Integer page;
    private String sortField;
    private String sortType;
}
