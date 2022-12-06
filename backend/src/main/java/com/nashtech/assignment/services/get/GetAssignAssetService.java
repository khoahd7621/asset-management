package com.nashtech.assignment.services.get;

import com.nashtech.assignment.dto.response.assignment.AssignAssetResponse;

public interface GetAssignAssetService {
    AssignAssetResponse getAssignAssetDetails(Long id);

}
