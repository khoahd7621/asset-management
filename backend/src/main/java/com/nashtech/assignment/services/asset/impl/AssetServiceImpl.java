package com.nashtech.assignment.services.asset.impl;

import com.nashtech.assignment.data.constants.Constants;
import com.nashtech.assignment.data.constants.EAssetStatus;
import com.nashtech.assignment.data.constants.EAssignStatus;
import com.nashtech.assignment.data.entities.Asset;
import com.nashtech.assignment.data.entities.Category;
import com.nashtech.assignment.data.repositories.AssetRepository;
import com.nashtech.assignment.data.repositories.AssignAssetRepository;
import com.nashtech.assignment.data.repositories.CategoryRepository;
import com.nashtech.assignment.dto.request.asset.CreateNewAssetRequest;
import com.nashtech.assignment.dto.request.asset.EditAssetInformationRequest;
import com.nashtech.assignment.dto.response.asset.AssetAndHistoriesResponse;
import com.nashtech.assignment.dto.response.asset.AssetResponse;
import com.nashtech.assignment.dto.response.report.AssetReportResponse;
import com.nashtech.assignment.exceptions.BadRequestException;
import com.nashtech.assignment.exceptions.NotFoundException;
import com.nashtech.assignment.mappers.AssetMapper;
import com.nashtech.assignment.mappers.AssetReportMapper;
import com.nashtech.assignment.services.asset.AssetService;
import com.nashtech.assignment.services.auth.SecurityContextService;
import lombok.Builder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
@Builder
public class AssetServiceImpl implements AssetService {

    private static final String NOT_EXIST_ERROR_MESSAGE = "Don't exist asset with this assetId.";

    @Autowired
    private SecurityContextService securityContextService;
    @Autowired
    private CategoryRepository categoryRepository;
    @Autowired
    private AssetRepository assetRepository;
    @Autowired
    private AssetMapper assetMapper;
    @Autowired
    private AssignAssetRepository assignAssetRepository;
    @Autowired
    private AssetReportMapper assetReportMapper;

    @Override
    public AssetResponse createAssetResponse(CreateNewAssetRequest createNewAssetRequest) throws ParseException {
        Optional<Category> categoryOpt = categoryRepository.findByName(createNewAssetRequest.getCategoryName());
        if (categoryOpt.isEmpty()) {
            throw new BadRequestException("Category not found");
        }
        List<Asset> assets = assetRepository.findAssetsByCategoryId(categoryOpt.get().getId());

        Asset asset = assetMapper.toAsset(createNewAssetRequest);
        asset.setCategory(categoryOpt.get());
        asset = assetRepository.save(asset);
        String prefixAssetCode = categoryOpt.get().getPrefixAssetCode();

        SimpleDateFormat formatterDate = new SimpleDateFormat(Constants.DATE_FORMAT);
        NumberFormat formatter = new DecimalFormat("000000");
        String assetId = formatter.format(assets.size() + 1L);
        StringBuilder assetCode = new StringBuilder(prefixAssetCode);

        Date installedDate = formatterDate.parse(createNewAssetRequest.getInstalledDate());

        asset.setAssetCode(assetCode.append(assetId).toString());
        asset.setName(createNewAssetRequest.getAssetName());
        asset.setSpecification(createNewAssetRequest.getSpecification());
        asset.setLocation(securityContextService.getCurrentUser().getLocation());
        asset.setStatus(createNewAssetRequest.getAssetStatus());
        asset.setInstalledDate(installedDate);

        assetRepository.save(asset);
        return assetMapper.toAssetResponse(asset);
    }

    @Override
    public void deleteAssetByAssetId(Long assetId) {
        Optional<Asset> assetOptional = assetRepository.findByIdAndIsDeletedFalse(assetId);
        if (assetOptional.isEmpty()) {
            throw new NotFoundException(NOT_EXIST_ERROR_MESSAGE);
        }
        boolean isAssigned = assignAssetRepository
                .existsByAssetIdAndStatusAndIsDeletedFalse(assetId, EAssignStatus.ACCEPTED);
        if (isAssigned) {
            throw new BadRequestException("Asset already assigned. Invalid for delete.");
        }
        Asset asset = assetOptional.get();
        asset.setDeleted(true);
        assetRepository.save(asset);
    }

    @Override
    public AssetResponse editAssetInformation(Long idAsset, EditAssetInformationRequest editAssetInformationRequest)
            throws ParseException {
        Optional<Asset> assetOpt = assetRepository.findById(idAsset);

        if (assetOpt.isEmpty()) {
            throw new NotFoundException("Asset not found");
        }

        Asset asset = assetOpt.get();

        if (asset.getStatus() == EAssetStatus.ASSIGNED) {
            throw new BadRequestException("Asset have state is assigned");
        }
        SimpleDateFormat sourceFormat = new SimpleDateFormat(Constants.DATE_FORMAT);
        Date installedDate = sourceFormat
                .parse(editAssetInformationRequest.getInstalledDate());

        asset.setName(editAssetInformationRequest.getAssetName());
        asset.setSpecification(editAssetInformationRequest.getSpecification());
        asset.setStatus(editAssetInformationRequest.getAssetStatus());
        asset.setInstalledDate(installedDate);
        assetRepository.save(asset);
        return assetMapper.toAssetResponse(asset);
    }

    @Override
    public AssetAndHistoriesResponse getAssetAndItsHistoriesByAssetId(long assetId) {
        Optional<Asset> assetOptional = assetRepository.findByIdAndIsDeletedFalse(assetId);
        if (assetOptional.isEmpty()) {
            throw new NotFoundException(NOT_EXIST_ERROR_MESSAGE);
        }
        return assetMapper.toAssetAndHistoriesResponse(assetOptional.get());
    }

    @Override
    public void checkAssetIsValidForDeleteOrNot(Long assetId) {
        Optional<Asset> assetOptional = assetRepository.findByIdAndIsDeletedFalse(assetId);
        if (assetOptional.isEmpty()) {
            throw new NotFoundException(NOT_EXIST_ERROR_MESSAGE);
        }
        boolean isAssigned = assignAssetRepository
                .existsByAssetIdAndStatusAndIsDeletedFalse(assetId, EAssignStatus.ACCEPTED);
        if (isAssigned) {
            throw new BadRequestException("Asset already assigned. Invalid for delete.");
        }
    }

    @Override
    public List<AssetReportResponse> getAssetReport() {
        return assetReportMapper.toListAssetReportResponses(assetRepository.getAssetReport());
    }
}
