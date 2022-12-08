package com.nashtech.assignment.services.get;

import java.util.List;

import com.nashtech.assignment.dto.response.assignment.AssignAssetResponse;

public interface GetAssignAssetService {
    AssignAssetResponse getAssignAssetDetails(Long id);

    List<AssignAssetResponse> getAssignAssetOfUser();

    AssignAssetResponse getDetailAssignAssetOfUser(Long id);

}
