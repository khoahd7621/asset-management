package com.nashtech.assignment.services.create;

import com.nashtech.assignment.dto.request.assignment.CreateNewAssignmentRequest;
import com.nashtech.assignment.dto.response.assignment.AssignAssetResponse;

public interface CreateAssignmentService {
    AssignAssetResponse createNewAssignment(CreateNewAssignmentRequest createNewAssignmentRequest);
}
