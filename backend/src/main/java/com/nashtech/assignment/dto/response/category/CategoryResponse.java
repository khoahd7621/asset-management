package com.nashtech.assignment.dto.response.category;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class CategoryResponse {
    private int id;
    private String name;
    private String prefixAssetCode;
}
