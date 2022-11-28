package com.nashtech.assignment.dto.request.asset;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.nashtech.assignment.data.constants.EAssetStatus;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class EditAssetInformationRequest {
    @NotBlank(message = "Asset name is required")
    @Pattern(regexp = "^[a-zA-Z0-9 ]*$", message = "Invalid asset name")
    @Size(max = 100, message = "Asset name maximum 100 characters")
    private String assetName;

    @Pattern(regexp = "^[a-zA-Z0-9 ].*[\\s]?+.*$", message = "Invalid specification")
    @Size(max = 500, message = "Specification maximum 500 characters")
    private String specification;

    @NotBlank(message = "Joined date is required")
    @JsonFormat(pattern = "dd/MM/yyyy")
    private String installedDate;

    @NotNull(message = "Asset state is required")
    private EAssetStatus assetStatus;
}
