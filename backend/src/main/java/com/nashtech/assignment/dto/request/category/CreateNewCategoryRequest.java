package com.nashtech.assignment.dto.request.category;

import com.nashtech.assignment.data.constants.Constants;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

@Getter
@Setter
@Builder
public class CreateNewCategoryRequest {
    @NotBlank(message = "Category name is required")
    @Pattern(regexp = Constants.ONLY_ALPHABET_AND_SPACE_REGEX, message = "Invalid category name")
    @Size(max = Constants.MAX_LENGTH_INPUT, message = "Category name maximum 100 characters")
    private String categoryName;

    @NotBlank(message = "Prefix AssetCode is required")
    @Pattern(regexp = Constants.ONLY_ALPHABET_REGEX, message = "Invalid Prefix AssetCode")
    @Size(max = 2, message = "Prefix AssetCode maximum 2 characters")
    private String prefixAssetCode;
}
