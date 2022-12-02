package com.nashtech.assignment.services.create.impl;

import com.nashtech.assignment.data.entities.Asset;
import com.nashtech.assignment.data.entities.Category;
import com.nashtech.assignment.data.repositories.AssetRepository;
import com.nashtech.assignment.data.repositories.CategoryRepository;
import com.nashtech.assignment.dto.request.asset.CreateNewAssetRequest;
import com.nashtech.assignment.dto.response.asset.AssetResponse;
import com.nashtech.assignment.mappers.AssetMapper;
import com.nashtech.assignment.services.auth.SecurityContextService;
import com.nashtech.assignment.services.create.CreateAssetService;
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
public class CreateAssetServiceImpl implements CreateAssetService {

    @Autowired
    private SecurityContextService securityContextService;
    @Autowired
    private CategoryRepository categoryRepository;
    @Autowired
    private AssetRepository assetRepository;
    @Autowired
    private AssetMapper assetMapper;

    @Override
    public AssetResponse createAssetResponse(CreateNewAssetRequest createNewAssetRequest) throws ParseException {
        Optional<Category> categoryOpt = categoryRepository.findByName(createNewAssetRequest.getCategoryName());
        List<Asset> assets = assetRepository.findAssetsByCategoryId(categoryOpt.get().getId());

        Asset asset = assetMapper.mapAssetRequestToEntity(createNewAssetRequest);
        asset = assetRepository.save(asset);
        String prefixAssetCode = categoryOpt.get().getPrefixAssetCode();

        SimpleDateFormat formatterDate = new SimpleDateFormat("dd/MM/yyyy");
        NumberFormat formatter = new DecimalFormat("000000");
        String assetId = formatter.format(assets.size() + 1);
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
}
