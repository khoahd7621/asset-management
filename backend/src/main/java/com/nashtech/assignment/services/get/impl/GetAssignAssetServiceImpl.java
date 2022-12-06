package com.nashtech.assignment.services.get.impl;

import com.nashtech.assignment.data.entities.AssignAsset;
import com.nashtech.assignment.data.repositories.AssignAssetRepository;
import com.nashtech.assignment.dto.response.assignment.AssignAssetResponse;
import com.nashtech.assignment.exceptions.NotFoundException;
import com.nashtech.assignment.mappers.AssignAssetMapper;
import com.nashtech.assignment.services.get.GetAssignAssetService;
import lombok.Builder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@Builder
public class GetAssignAssetServiceImpl implements GetAssignAssetService {

    @Autowired
    private AssignAssetRepository assignAssetRepository;
    @Autowired
    private AssignAssetMapper assignAssetMapper;

    @Override
    public AssignAssetResponse getAssignAssetDetails(Long id) {
        Optional<AssignAsset> assignAssetOtp = assignAssetRepository.findById(id);
        if (assignAssetOtp.isEmpty()) {
            throw new NotFoundException("Cannot find assignment with id: " + id);
        }
        return assignAssetMapper.toAssignAssetResponse(assignAssetOtp.get());
    }
}
