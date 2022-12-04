package com.nashtech.assignment.services.validation;

import com.nashtech.assignment.data.entities.Asset;

import java.util.Date;

public interface ValidationAssetService {
    Asset validationAssetAssignedToAssignment(long assetId, Date assignedDate);
}
