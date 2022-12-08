package com.nashtech.assignment.services.get.impl;

import com.nashtech.assignment.data.constants.EAssignStatus;
import com.nashtech.assignment.data.entities.AssignAsset;
import com.nashtech.assignment.data.entities.User;
import com.nashtech.assignment.data.repositories.AssignAssetRepository;
import com.nashtech.assignment.dto.response.assignment.AssignAssetResponse;
import com.nashtech.assignment.exceptions.NotFoundException;
import com.nashtech.assignment.mappers.AssignAssetMapper;
import com.nashtech.assignment.services.auth.SecurityContextService;
import com.nashtech.assignment.services.get.GetAssignAssetService;
import lombok.Builder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

@Service
@Builder
public class GetAssignAssetServiceImpl implements GetAssignAssetService {

    @Autowired
    private AssignAssetRepository assignAssetRepository;
    @Autowired
    private AssignAssetMapper assignAssetMapper;
    @Autowired
    private SecurityContextService securityContextService;

    @Override
    public AssignAssetResponse getAssignAssetDetails(Long id) {
        Optional<AssignAsset> assignAssetOtp = assignAssetRepository.findById(id);
        if (assignAssetOtp.isEmpty()) {
            throw new NotFoundException("Cannot find assignment with id: " + id);
        }
        return assignAssetMapper.toAssignAssetResponse(assignAssetOtp.get());
    }

    @Override
    public List<AssignAssetResponse> getAssignAssetOfUser () {
        User user = securityContextService.getCurrentUser();
        LocalDate date = LocalDate.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/YYYY");
        List<AssignAsset> assignAssets = assignAssetRepository.findAllAssignAssetByUser(user.getId(), false,
                EAssignStatus.DECLINED, formatter.format(date));
        if (assignAssets == null || assignAssets.isEmpty()) {
            throw new NotFoundException("User doesn't assign asset");
        }
        return assignAssetMapper.toListAssignAssetResponses(assignAssets);
    }

    @Override
    public AssignAssetResponse getDetailAssignAssetOfUser(Long id) {
        User user = securityContextService.getCurrentUser();
        Optional<AssignAsset> assignAsset = assignAssetRepository.findByIdAndUser(id, user.getId());
        if (assignAsset.isEmpty()) {
            throw new NotFoundException("Cannot Found Assign Asset");
        }
        return assignAssetMapper.toAssignAssetResponse(assignAsset.get());
    }
}
