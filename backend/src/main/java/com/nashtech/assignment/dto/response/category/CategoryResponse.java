package com.nashtech.assignment.dto.response.category;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Getter
@Setter
public class CategoryResponse {
    private int id;
    private String name;
    private String prefixAssetCode;
}
