package com.nashtech.assignment.services.create;

import com.nashtech.assignment.dto.request.assignment.CreateNewAssignmentRequest;
import com.nashtech.assignment.dto.response.assignment.AssignAssetResponse;

import java.text.ParseException;

public interface CreateAssignmentService {
    AssignAssetResponse createNewAssignment(CreateNewAssignmentRequest createNewAssignmentRequest) throws ParseException;
}
