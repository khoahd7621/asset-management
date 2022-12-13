package com.nashtech.assignment.services.assignment;

import com.nashtech.assignment.dto.request.assignment.CreateNewAssignmentRequest;
import com.nashtech.assignment.dto.request.assignment.EditAssignmentRequest;
import com.nashtech.assignment.dto.response.assignment.AssignAssetResponse;

import java.text.ParseException;
import java.util.List;

public interface AssignmentService {
    AssignAssetResponse createNewAssignment(CreateNewAssignmentRequest createNewAssignmentRequest) throws ParseException;

    void deleteAssignAsset(Long assignAssetId);

    AssignAssetResponse editAssignment(Long assignmentId, EditAssignmentRequest editAssignmentRequest) throws ParseException;

    AssignAssetResponse acceptAssignAsset(Long id);

    AssignAssetResponse declineAssignAsset(Long id);

    AssignAssetResponse getAssignAssetDetails(Long id);

    List<AssignAssetResponse> getAssignAssetOfUser();

    AssignAssetResponse getDetailAssignAssetOfUser(Long id);
}
