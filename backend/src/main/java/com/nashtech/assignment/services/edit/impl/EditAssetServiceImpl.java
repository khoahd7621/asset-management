package com.nashtech.assignment.services.edit.impl;

import com.nashtech.assignment.data.constants.EAssetStatus;
import com.nashtech.assignment.data.entities.Asset;
import com.nashtech.assignment.data.repositories.AssetRepository;
import com.nashtech.assignment.dto.request.asset.EditAssetInformationRequest;
import com.nashtech.assignment.dto.response.asset.AssetResponse;
import com.nashtech.assignment.exceptions.BadRequestException;
import com.nashtech.assignment.exceptions.NotFoundException;
import com.nashtech.assignment.mappers.AssetMapper;
import com.nashtech.assignment.services.edit.EditAssetService;
import lombok.Builder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Optional;

@Service
@Builder
public class EditAssetServiceImpl implements EditAssetService {

    @Autowired
    private AssetRepository assetRepository;
    @Autowired
    private AssetMapper assetMapper;

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
        SimpleDateFormat sourceFormat = new SimpleDateFormat("dd/MM/yyyy");
        Date installedDate = sourceFormat
                .parse(editAssetInformationRequest.getInstalledDate());

        asset.setName(editAssetInformationRequest.getAssetName());
        asset.setSpecification(editAssetInformationRequest.getSpecification());
        asset.setStatus(editAssetInformationRequest.getAssetStatus());
        asset.setInstalledDate(installedDate);
        assetRepository.save(asset);
        return assetMapper.mapEntityToEditAssetInformationResponse(asset);
    }
}
