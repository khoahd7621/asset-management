package com.nashtech.assignment.dto.request.asset;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.nashtech.assignment.data.constants.Constants;
import com.nashtech.assignment.data.constants.EAssetStatus;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

@Getter
@Setter
@Builder
public class EditAssetInformationRequest {
    @NotBlank(message = "Asset name is required")
    @Pattern(regexp = Constants.ONLY_ALPHABET_AND_NUMBER_AND_SPACE_REGEX, message = "Invalid asset name")
    @Size(max = Constants.MAX_LENGTH_INPUT, message = "Asset name maximum 100 characters")
    private String assetName;

    @Size(max = Constants.MAX_LENGTH_TEXTAREA, message = "Specification maximum 500 characters")
    private String specification;

    @NotBlank(message = "Joined date is required")
    @JsonFormat(pattern = Constants.DATE_FORMAT)
    private String installedDate;

    @NotNull(message = "Asset state is required")
    private EAssetStatus assetStatus;
}
