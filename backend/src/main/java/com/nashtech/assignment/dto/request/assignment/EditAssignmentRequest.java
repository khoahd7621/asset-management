package com.nashtech.assignment.dto.request.assignment;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.Min;
import javax.validation.constraints.Size;
import java.util.Date;

@Getter
@Setter
@Builder
public class EditAssignmentRequest {
    @Min(1)
    private Long assetId;
    @Min(1)
    private Long userId;
    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date assignedDate;
    @Size(max = 500)
    private String note;
}
