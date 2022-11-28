package com.nashtech.assignment.dto.request.category;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class CreateNewCategoryRequest {
    @NotBlank(message = "Category name is required")
    @Pattern(regexp = "^.*[A-Za-z].*[\\s]?+.*$", message = "Invalid category name")
    @Size(max = 100, message = "Category name maximum 100 characters")
    private String categoryName;

    @NotBlank(message = "Prefix AssetCode is required")
    @Pattern(regexp = "^.*[A-Za-z].*$", message = "Invalid Prefix AssetCode")
    @Size(max = 2, message = "Prefix AssetCode maximum 100 characters")
    private String prefixAssetCode;
}
