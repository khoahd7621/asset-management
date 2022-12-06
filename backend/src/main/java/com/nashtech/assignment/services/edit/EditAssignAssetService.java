package com.nashtech.assignment.services.edit;

import com.nashtech.assignment.dto.request.assignment.EditAssignmentRequest;
import com.nashtech.assignment.dto.response.assignment.AssignAssetResponse;

public interface EditAssignAssetService {
    AssignAssetResponse editAssignment(Long assignmentId, EditAssignmentRequest editAssignmentRequest);
        
    AssignAssetResponse acceptAssignAsset(Long id);

    AssignAssetResponse declineAssignAsset(Long id);
}
