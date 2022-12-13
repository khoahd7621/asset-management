package com.nashtech.assignment.services.returned;

import com.nashtech.assignment.dto.response.returned.ReturnAssetResponse;

public interface ReturnedService {
    ReturnAssetResponse createReturnAsset(Long id);

    void deleteReturnAsset(long id);

    void completeReturnRequest(long id);
}
