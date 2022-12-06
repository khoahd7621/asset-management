package com.nashtech.assignment.services.get;

import java.util.List;

import com.nashtech.assignment.dto.response.assignment.AssignAssetResponse;

public interface FindAssignAssetService {

    List<AssignAssetResponse> findAssignAssetByUser();

    AssignAssetResponse detailAssignAsset(Long id);
}
