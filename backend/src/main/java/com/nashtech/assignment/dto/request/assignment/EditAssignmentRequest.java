package com.nashtech.assignment.dto.request.assignment;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.nashtech.assignment.data.constants.Constants;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Getter
@Setter
@Builder
public class EditAssignmentRequest {
    @Min(1)
    private Long assetId;
    @Min(1)
    private Long userId;
    @NotBlank(message = "Assigned date is required")
    @JsonFormat(pattern = Constants.DATE_FORMAT)
    private String assignedDate;
    @Size(max = Constants.MAX_LENGTH_TEXTAREA)
    private String note;
}
