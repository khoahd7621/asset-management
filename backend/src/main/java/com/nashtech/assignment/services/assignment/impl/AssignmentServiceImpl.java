package com.nashtech.assignment.services.assignment.impl;

import com.nashtech.assignment.data.constants.Constants;
import com.nashtech.assignment.data.constants.EAssetStatus;
import com.nashtech.assignment.data.constants.EAssignStatus;
import com.nashtech.assignment.data.entities.Asset;
import com.nashtech.assignment.data.entities.AssignAsset;
import com.nashtech.assignment.data.entities.User;
import com.nashtech.assignment.data.repositories.AssetRepository;
import com.nashtech.assignment.data.repositories.AssignAssetRepository;
import com.nashtech.assignment.dto.request.assignment.CreateNewAssignmentRequest;
import com.nashtech.assignment.dto.request.assignment.EditAssignmentRequest;
import com.nashtech.assignment.dto.response.assignment.AssignAssetResponse;
import com.nashtech.assignment.exceptions.BadRequestException;
import com.nashtech.assignment.exceptions.ForbiddenException;
import com.nashtech.assignment.exceptions.NotFoundException;
import com.nashtech.assignment.mappers.AssignAssetMapper;
import com.nashtech.assignment.services.assignment.AssignmentService;
import com.nashtech.assignment.services.auth.SecurityContextService;
import com.nashtech.assignment.services.validation.ValidationAssetService;
import com.nashtech.assignment.services.validation.ValidationUserService;
import com.nashtech.assignment.utils.CompareDateUtil;
import lombok.Builder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
@Builder
public class AssignmentServiceImpl implements AssignmentService {

    @Autowired
    private AssignAssetRepository assignAssetRepository;
    @Autowired
    private AssetRepository assetRepository;
    @Autowired
    private AssignAssetMapper assignAssetMapper;
    @Autowired
    private ValidationAssetService validationAssetService;
    @Autowired
    private ValidationUserService validationUserService;
    @Autowired
    private CompareDateUtil compareDateUtil;
    @Autowired
    private SecurityContextService securityContextService;

    @Override
    public AssignAssetResponse createNewAssignment(CreateNewAssignmentRequest createNewAssignmentRequest) throws ParseException {
        SimpleDateFormat dateFormat = new SimpleDateFormat(Constants.DATE_FORMAT);
        Date today = new Date();
        Date assignedDate = dateFormat.parse(createNewAssignmentRequest.getAssignedDate());
        if (compareDateUtil.isAfter(today, assignedDate)) {
            throw new BadRequestException("Assign date is before today.");
        }
        Asset asset = validationAssetService.validationAssetAssignedToAssignment(
                createNewAssignmentRequest.getAssetId());
        User userAssignedTo = validationUserService.validationUserAssignedToAssignment(
                createNewAssignmentRequest.getUserId());
        User currentUser = securityContextService.getCurrentUser();
        asset.setStatus(EAssetStatus.ASSIGNED);
        AssignAsset assignAsset = AssignAsset.builder()
                .asset(asset)
                .assignedDate(assignedDate)
                .userAssignedBy(currentUser)
                .userAssignedTo(userAssignedTo)
                .status(EAssignStatus.WAITING_FOR_ACCEPTANCE)
                .note(createNewAssignmentRequest.getNote())
                .isDeleted(false).build();
        assignAsset = assignAssetRepository.save(assignAsset);
        return assignAssetMapper.toAssignAssetResponse(assignAsset);
    }

    @Override
    public void deleteAssignAsset(Long assignAssetId) {
        Optional<AssignAsset> assignAssetOtp = assignAssetRepository.findById(assignAssetId);
        if (assignAssetOtp.isEmpty()) {
            throw new NotFoundException("Cannot found assign asset with id " + assignAssetId);
        }
        AssignAsset assignAsset = assignAssetOtp.get();
        if (EAssignStatus.ACCEPTED == assignAsset.getStatus()) {
            throw new BadRequestException("Cannot delete this assignment");
        }
        Asset asset = assignAsset.getAsset();
        asset.setStatus(EAssetStatus.AVAILABLE);
        assignAsset.setDeleted(true);
        assetRepository.save(asset);
        assignAssetRepository.save(assignAsset);
    }

    @Override
    public AssignAssetResponse acceptAssignAsset(Long idAssignAsset) {
        Optional<AssignAsset> assignAssetOpt = assignAssetRepository.findById(idAssignAsset);
        if (assignAssetOpt.isEmpty()) {
            throw new NotFoundException("Assignment not found");
        }
        if (!assignAssetOpt.get().getStatus().equals(EAssignStatus.WAITING_FOR_ACCEPTANCE)) {
            throw new BadRequestException("Assignment is not waiting for acceptance");
        }

        Date today = new Date();
        Date assignedDate = assignAssetOpt.get().getAssignedDate();
        if (compareDateUtil.isBefore(today, assignedDate)) {
            throw new BadRequestException("Assign date is after today.");
        }

        User currentUser = securityContextService.getCurrentUser();
        if (currentUser.getId() != assignAssetOpt.get().getUserAssignedTo().getId())
            throw new ForbiddenException("Current user is not match to this assignment.");

        assignAssetOpt.get().setStatus(EAssignStatus.ACCEPTED);
        assignAssetRepository.save(assignAssetOpt.get());
        return assignAssetMapper.toAssignAssetResponse(assignAssetOpt.get());
    }

    @Override
    public AssignAssetResponse declineAssignAsset(Long idAssignAsset) {
        Optional<AssignAsset> assignAssetOpt = assignAssetRepository.findById(idAssignAsset);
        if (assignAssetOpt.isEmpty()) {
            throw new NotFoundException("Assignment not found");
        }
        if (!assignAssetOpt.get().getStatus().equals(EAssignStatus.WAITING_FOR_ACCEPTANCE)) {
            throw new BadRequestException("Assignment is not waiting for acceptance");
        }

        Date today = new Date();
        Date assignedDate = assignAssetOpt.get().getAssignedDate();
        if (compareDateUtil.isBefore(today, assignedDate)) {
            throw new BadRequestException("Assign date is after today.");
        }

        User currentUser = securityContextService.getCurrentUser();
        if (currentUser.getId() != assignAssetOpt.get().getUserAssignedTo().getId())
            throw new ForbiddenException("Current user is not match to this assignment.");

        assignAssetOpt.get().setStatus(EAssignStatus.DECLINED);
        assignAssetRepository.save(assignAssetOpt.get());
        Asset asset = assignAssetOpt.get().getAsset();
        asset.setStatus(EAssetStatus.AVAILABLE);
        assetRepository.save(asset);

        return assignAssetMapper.toAssignAssetResponse(assignAssetOpt.get());
    }

    public AssignAssetResponse editAssignment(Long assignmentId, EditAssignmentRequest editAssignmentRequest) throws ParseException {
        Optional<AssignAsset> assignAssetOpt = assignAssetRepository.findByIdAndIsDeletedFalse(assignmentId);
        if (assignAssetOpt.isEmpty()) {
            throw new NotFoundException("Not exist assignment with this assignment id.");
        }
        AssignAsset assignAsset = assignAssetOpt.get();
        if (assignAsset.getStatus() != EAssignStatus.WAITING_FOR_ACCEPTANCE) {
            throw new BadRequestException("Can only edit assignment with status waiting for acceptance.");
        }
        Date oldAssignedDate = assignAsset.getAssignedDate();
        DateFormat dateFormat = new SimpleDateFormat(Constants.DATE_FORMAT);
        Date newAssignedDate = dateFormat.parse(editAssignmentRequest.getAssignedDate());
        Date today = new Date();
        if (!compareDateUtil.isEquals(oldAssignedDate, newAssignedDate)
                && compareDateUtil.isAfter(today, newAssignedDate)) {
            throw new BadRequestException("Assign date is before today.");
        }
        User currentUserAssignTo = assignAsset.getUserAssignedTo();
        if (currentUserAssignTo.getId() != editAssignmentRequest.getUserId()) {
            currentUserAssignTo = validationUserService.validationUserAssignedToAssignment(
                    editAssignmentRequest.getUserId());
        }
        Asset currentAssetAssignedTo = assignAsset.getAsset();
        if (currentAssetAssignedTo.getId() != editAssignmentRequest.getAssetId()) {
            Asset newAsset = validationAssetService
                    .validationAssetAssignedToAssignment(editAssignmentRequest.getAssetId());
            newAsset.setStatus(EAssetStatus.ASSIGNED);
            currentAssetAssignedTo.setStatus(EAssetStatus.AVAILABLE);
            assetRepository.save(currentAssetAssignedTo);
            assignAsset.setAsset(newAsset);
        }
        assignAsset.setUserAssignedTo(currentUserAssignTo);
        assignAsset.setAssignedDate(newAssignedDate);
        assignAsset.setNote(editAssignmentRequest.getNote());
        assignAsset = assignAssetRepository.save(assignAsset);
        return assignAssetMapper.toAssignAssetResponse(assignAsset);
    }

    @Override
    public AssignAssetResponse getAssignAssetDetails(Long id) {
        Optional<AssignAsset> assignAssetOtp = assignAssetRepository.findById(id);
        if (assignAssetOtp.isEmpty()) {
            throw new NotFoundException("Cannot find assignment with id: " + id);
        }
        return assignAssetMapper.toAssignAssetResponse(assignAssetOtp.get());
    }

    @Override
    public List<AssignAssetResponse> getAssignAssetOfUser() {
        User user = securityContextService.getCurrentUser();
        LocalDate date = LocalDate.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(Constants.DATE_FORMAT);
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
