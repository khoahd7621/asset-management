package com.nashtech.assignment.dto.request.assignment;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import java.util.Date;

@Getter
@Setter
@Builder
public class CreateNewAssignmentRequest {
    @Min(1)
    private Long assetId;
    @Min(1)
    private Long userId;
    @NotBlank(message = "assigned date is required")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date assignedDate;
    private String note;
}
