package com.nashtech.assignment.services.get.impl;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.nashtech.assignment.data.constants.EAssignStatus;
import com.nashtech.assignment.data.entities.AssignAsset;
import com.nashtech.assignment.data.entities.User;
import com.nashtech.assignment.data.repositories.AssignAssetRepository;
import com.nashtech.assignment.dto.response.assignment.AssignAssetResponse;
import com.nashtech.assignment.exceptions.NotFoundException;
import com.nashtech.assignment.mappers.AssignAssetMapper;
import com.nashtech.assignment.services.auth.SecurityContextService;
import com.nashtech.assignment.services.get.FindAssignAssetService;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class FindAssignAssetServiceImpl implements FindAssignAssetService {
    @Autowired
    SecurityContextService securityContextService;
    @Autowired
    AssignAssetRepository assignAssetRepository;
    @Autowired
    AssignAssetMapper assignAssetMapper;

    @Override
    public List<AssignAssetResponse> findAssignAssetByUser() {
        User user = securityContextService.getCurrentUser();
        List<AssignAsset> assignAssets = assignAssetRepository.findAllAssignAssetByUser(user.getId(), false,
                EAssignStatus.DECLINED);
        if (assignAssets == null || assignAssets.isEmpty()) {
            throw new NotFoundException("User doesn't assign asset");
        }
        return assignAssetMapper.mapListEntityToDto(assignAssets);
    }

    @Override
    public AssignAssetResponse detailAssignAsset(Long id) {
        User user = securityContextService.getCurrentUser();
        Optional<AssignAsset> assignAsset = assignAssetRepository.findByIdAndUser(id, user.getId());
        if (assignAsset.isEmpty()) {
            throw new NotFoundException("Cannot Found Assign Asset");
        }
        return assignAssetMapper.toAssignAssetResponse(assignAsset.get());
    }

}