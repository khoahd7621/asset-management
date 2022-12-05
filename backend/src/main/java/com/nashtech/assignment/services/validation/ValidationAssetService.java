package com.nashtech.assignment.services.validation;

import com.nashtech.assignment.data.entities.Asset;

public interface ValidationAssetService {
    Asset validationAssetAssignedToAssignment(long assetId);
}
