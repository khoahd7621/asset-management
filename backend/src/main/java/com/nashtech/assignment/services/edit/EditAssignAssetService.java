package com.nashtech.assignment.services.edit;

import com.nashtech.assignment.dto.request.assignment.EditAssignmentRequest;
import com.nashtech.assignment.dto.response.assignment.AssignAssetResponse;

import java.text.ParseException;

public interface EditAssignAssetService {
    AssignAssetResponse editAssignment(Long assignmentId, EditAssignmentRequest editAssignmentRequest) throws ParseException;
        
    AssignAssetResponse acceptAssignAsset(Long id);

    AssignAssetResponse declineAssignAsset(Long id);
}
