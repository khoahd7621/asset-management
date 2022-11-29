package com.nashtech.assignment.dto.request.asset;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.nashtech.assignment.data.constants.EAssetStatus;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CreateNewAssetRequest {
    @NotBlank(message = "Asset name is required")
    @Pattern(regexp = "^[a-zA-Z0-9 ]*$", message = "Invalid asset name")
    @Size(max = 100, message = "Asset name maximum 100 characters")
    private String assetName;

    @Pattern(regexp = "^.*[A-Za-z].*[\\s]?+.*$", message = "Invalid category name")
    @Size(max = 100, message = "Category name maximum 100 characters")
    private String categoryName;

    @Size(max = 500, message = "Specification maximum 500 characters")
    private String specification;

    @NotBlank(message = "Joined date is required")
    @JsonFormat(pattern = "dd/MM/yyyy")
    private String installedDate;

    @NotNull(message = "Asset state is required")
    private EAssetStatus assetStatus;
}
