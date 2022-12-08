package com.nashtech.assignment.services.create.impl;

import com.nashtech.assignment.data.constants.EAssetStatus;
import com.nashtech.assignment.data.entities.Asset;
import com.nashtech.assignment.data.entities.Category;
import com.nashtech.assignment.data.entities.User;
import com.nashtech.assignment.data.repositories.AssetRepository;
import com.nashtech.assignment.data.repositories.CategoryRepository;
import com.nashtech.assignment.dto.request.asset.CreateNewAssetRequest;
import com.nashtech.assignment.dto.response.asset.AssetResponse;
import com.nashtech.assignment.mappers.AssetMapper;
import com.nashtech.assignment.services.auth.SecurityContextService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.*;

class CreateAssetServiceImplTest {

    private CreateAssetServiceImpl createAssetServiceImpl;
    private CategoryRepository categoryRepository;
    private AssetRepository assetRepository;
    private AssetMapper assetMapper;
    private SecurityContextService securityContextService;
    private Category category;
    private Asset asset;

    @BeforeEach
    void beforeEach() {
        categoryRepository = mock(CategoryRepository.class);
        assetRepository = mock(AssetRepository.class);
        assetMapper = mock(AssetMapper.class);
        securityContextService = mock(SecurityContextService.class);
        createAssetServiceImpl = CreateAssetServiceImpl.builder()
                .securityContextService(securityContextService)
                .categoryRepository(categoryRepository)
                .assetRepository(assetRepository)
                .assetMapper(assetMapper)
                .build();
        asset = mock(Asset.class);
        category = mock(Category.class);
    }

    @Test
    void createAssetResponse_WhenDataValid_ShouldReturnData() throws ParseException {
        String prefixAssetCode = "CN";
        User currentUser = User.builder().staffCode("currentUser").build();

        CreateNewAssetRequest createNewAssetRequest = CreateNewAssetRequest.builder()
                .assetName("assetName")
                .categoryName("categoryName")
                .specification("specification")
                .installedDate("01/01/2022")
                .assetStatus(EAssetStatus.AVAILABLE)
                .build();

        AssetResponse expected = mock(AssetResponse.class);
        List<Asset> assets = new ArrayList<Asset>();

        when(assetMapper.toAsset(createNewAssetRequest)).thenReturn(asset);
        when(assetRepository.save(asset)).thenReturn(asset);

        when(categoryRepository.findByName(createNewAssetRequest.getCategoryName())).thenReturn(Optional.of(category));
        when(category.getPrefixAssetCode()).thenReturn(prefixAssetCode);
        when(assetMapper.toAssetResponse(asset)).thenReturn(expected);
        when(securityContextService.getCurrentUser()).thenReturn(currentUser);
        when(assetRepository.findAssetsByCategoryId(category.getId())).thenReturn(assets);

        SimpleDateFormat formatterDate = new SimpleDateFormat("dd/MM/yyyy");
        NumberFormat formatter = new DecimalFormat("000000");

        String assetId = formatter.format(assets.size() + 1);
        StringBuilder assetCode = new StringBuilder(prefixAssetCode);
        Date installedDate = formatterDate.parse(createNewAssetRequest.getInstalledDate());

        AssetResponse actual = createAssetServiceImpl.createAssetResponse(createNewAssetRequest);

        verify(asset).setAssetCode(assetCode.append(assetId).toString());
        verify(asset).setName(createNewAssetRequest.getAssetName());
        verify(asset).setSpecification(createNewAssetRequest.getSpecification());
        verify(asset).setLocation(securityContextService.getCurrentUser().getLocation());
        verify(asset).setInstalledDate(installedDate);
        verify(asset).setStatus(EAssetStatus.AVAILABLE);

        assertThat(actual, is(expected));
    }
}