package com.nashtech.assignment.services.asset.impl;

import com.nashtech.assignment.data.constants.Constants;
import com.nashtech.assignment.data.constants.EAssetStatus;
import com.nashtech.assignment.data.constants.EAssignStatus;
import com.nashtech.assignment.data.entities.Asset;
import com.nashtech.assignment.data.entities.Category;
import com.nashtech.assignment.data.entities.User;
import com.nashtech.assignment.data.repositories.AssetRepository;
import com.nashtech.assignment.data.repositories.AssignAssetRepository;
import com.nashtech.assignment.data.repositories.CategoryRepository;
import com.nashtech.assignment.dto.request.asset.CreateNewAssetRequest;
import com.nashtech.assignment.dto.request.asset.EditAssetInformationRequest;
import com.nashtech.assignment.dto.response.asset.AssetAndHistoriesResponse;
import com.nashtech.assignment.dto.response.asset.AssetResponse;
import com.nashtech.assignment.dto.response.report.AssetReportResponse;
import com.nashtech.assignment.dto.response.report.AssetReportResponseInterface;
import com.nashtech.assignment.exceptions.BadRequestException;
import com.nashtech.assignment.exceptions.NotFoundException;
import com.nashtech.assignment.mappers.AssetMapper;
import com.nashtech.assignment.mappers.AssetReportMapper;
import com.nashtech.assignment.services.auth.SecurityContextService;
import org.junit.jupiter.api.Assertions;
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
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

class AssetServiceImplTest {

    private AssetServiceImpl assetServiceImpl;
    private CategoryRepository categoryRepository;
    private AssetRepository assetRepository;
    private AssetMapper assetMapper;
    private SecurityContextService securityContextService;
    private AssignAssetRepository assignAssetRepository;
    private AssetReportMapper assetReportMapper;

    private Category category;
    private Asset asset;

    @BeforeEach
    void beforeEach() {
        categoryRepository = mock(CategoryRepository.class);
        assetRepository = mock(AssetRepository.class);
        assetMapper = mock(AssetMapper.class);
        securityContextService = mock(SecurityContextService.class);
        assignAssetRepository = mock(AssignAssetRepository.class);
        assetReportMapper = mock(AssetReportMapper.class);
        assetServiceImpl = AssetServiceImpl.builder()
                .securityContextService(securityContextService)
                .categoryRepository(categoryRepository)
                .assetRepository(assetRepository)
                .assetMapper(assetMapper)
                .assignAssetRepository(assignAssetRepository)
                .assetReportMapper(assetReportMapper)
                .build();
        asset = mock(Asset.class);
        category = mock(Category.class);
    }

    @Test
    void createAssetResponse_WhenDataInvalid_ShouldReturnException() throws ParseException {
        CreateNewAssetRequest createNewAssetRequest = CreateNewAssetRequest.builder()
                .assetName("assetName")
                .categoryName("categoryName")
                .specification("specification")
                .installedDate("01/01/2022")
                .assetStatus(EAssetStatus.AVAILABLE)
                .build();
        when(categoryRepository.findByName("categoryName")).thenReturn(Optional.empty());

        BadRequestException actualException = Assertions.assertThrows(BadRequestException.class,
                () -> assetServiceImpl.createAssetResponse(createNewAssetRequest));

        Assertions.assertEquals("Category not found",
                actualException.getMessage());
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

        SimpleDateFormat formatterDate = new SimpleDateFormat(Constants.DATE_FORMAT);
        NumberFormat formatter = new DecimalFormat("000000");

        String assetId = formatter.format(assets.size() + 1);
        StringBuilder assetCode = new StringBuilder(prefixAssetCode);
        Date installedDate = formatterDate.parse(createNewAssetRequest.getInstalledDate());

        AssetResponse actual = assetServiceImpl.createAssetResponse(createNewAssetRequest);

        verify(asset).setCategory(category);
        verify(asset).setAssetCode(assetCode.append(assetId).toString());
        verify(asset).setName(createNewAssetRequest.getAssetName());
        verify(asset).setSpecification(createNewAssetRequest.getSpecification());
        verify(asset).setLocation(securityContextService.getCurrentUser().getLocation());
        verify(asset).setInstalledDate(installedDate);
        verify(asset).setStatus(EAssetStatus.AVAILABLE);

        assertThat(actual, is(expected));
    }

    @Test
    void deleteAssetByAssetId_WhenAssetIdNotExist_ShouldThrowNotFoundException() {
        long assetId = 1L;

        when(assetRepository.findByIdAndIsDeletedFalse(assetId)).thenReturn(Optional.empty());

        NotFoundException actual = assertThrows(NotFoundException.class,
                () -> assetServiceImpl.deleteAssetByAssetId(assetId));

        assertThat(actual.getMessage(), is("Don't exist asset with this assetId."));
    }

    @Test
    void deleteAssetByAssetId_WhenAssetIdExistButNotValidForDelete_ShouldThrowBadRequestException() {
        long assetId = 1L;

        when(assetRepository.findByIdAndIsDeletedFalse(assetId)).thenReturn(Optional.of(asset));
        when(assignAssetRepository.existsByAssetIdAndStatusAndIsDeletedFalse(assetId, EAssignStatus.ACCEPTED))
                .thenReturn(true);

        BadRequestException actual = assertThrows(BadRequestException.class,
                () -> assetServiceImpl.deleteAssetByAssetId(assetId));

        assertThat(actual.getMessage(), is("Asset already assigned. Invalid for delete."));
    }

    @Test
    void deleteAssetByAssetId_WhenAssetIdExistAndValidForDelete_ShouldDeleteSuccess() {
        long assetId = 1L;

        when(assetRepository.findByIdAndIsDeletedFalse(assetId)).thenReturn(Optional.of(asset));
        when(assignAssetRepository.existsByAssetIdAndStatusAndIsDeletedFalse(assetId, EAssignStatus.ACCEPTED))
                .thenReturn(false);

        assetServiceImpl.deleteAssetByAssetId(assetId);

        verify(asset).setDeleted(true);
        verify(assetRepository).save(asset);
    }

    @Test
    void editAssetInformation_WhenFindAssetNull_ShouldReturnException() throws NotFoundException {
        long idAsset = 1L;
        EditAssetInformationRequest editAssetInformationRequest = EditAssetInformationRequest.builder().build();

        when(assetRepository.findById(idAsset)).thenReturn(Optional.empty());
        NotFoundException actualException = Assertions.assertThrows(NotFoundException.class,
                () -> assetServiceImpl.editAssetInformation(idAsset,
                        editAssetInformationRequest));

        Assertions.assertEquals("Asset not found", actualException.getMessage());
    }

    @Test
    void editAssetInformation_WhenAssetStatusAssigned_ShouldReturnException() {
        long idAsset = 1L;
        EditAssetInformationRequest editAssetInformationRequest = EditAssetInformationRequest.builder().build();

        Optional<Asset> assetOpt = Optional.of(asset);
        when(assetRepository.findById(idAsset)).thenReturn(assetOpt);
        when(assetOpt.get().getStatus())
                .thenReturn(EAssetStatus.ASSIGNED);

        BadRequestException actualException = Assertions.assertThrows(BadRequestException.class,
                () -> assetServiceImpl.editAssetInformation(idAsset,
                        editAssetInformationRequest));

        Assertions.assertEquals("Asset have state is assigned", actualException.getMessage());
    }

    @Test
    void editAssetInformation_WhenDataValid_ShouldReturnData() throws ParseException {
        long idAsset = 1L;
        EditAssetInformationRequest editAssetInformationRequest = EditAssetInformationRequest.builder()
                .assetName("assetName")
                .specification("assetSpecification")
                .assetStatus(EAssetStatus.AVAILABLE)
                .installedDate("01/01/2022")
                .build();

        AssetResponse expected = mock(AssetResponse.class);

        SimpleDateFormat sourceFormat = new SimpleDateFormat(Constants.DATE_FORMAT);
        Date installedDate = sourceFormat.parse(editAssetInformationRequest.getInstalledDate());

        when(assetRepository.findById(idAsset)).thenReturn(Optional.of(asset));
        when(asset.getStatus())
                .thenReturn(EAssetStatus.NOT_AVAILABLE);
        when(assetMapper.toAssetResponse(asset)).thenReturn(expected);

        AssetResponse actual = assetServiceImpl.editAssetInformation(idAsset,
                editAssetInformationRequest);

        verify(asset).setName("assetName");
        verify(asset).setSpecification("assetSpecification");
        verify(asset).setStatus(EAssetStatus.AVAILABLE);
        verify(asset).setInstalledDate(installedDate);
        verify(assetRepository).save(asset);

        assertThat(actual, is(expected));
    }

    @Test
    void getAssetAndItsHistoriesByAssetId_WhenAssetIdNotExist_ShouldThrowNotFoundException() {
        when(assetRepository.findByIdAndIsDeletedFalse(1L)).thenReturn(Optional.empty());

        NotFoundException actual = assertThrows(NotFoundException.class, () -> {
            assetServiceImpl.getAssetAndItsHistoriesByAssetId(1L);
        });

        assertThat(actual.getMessage(), is("Don't exist asset with this assetId."));
    }

    @Test
    void getAssetAndItsHistoriesByAssetId_WhenAssetIdExist_ShouldReturnData() {
        Optional<Asset> assetOptional = Optional.of(asset);
        AssetAndHistoriesResponse expected = mock(AssetAndHistoriesResponse.class);

        when(assetRepository.findByIdAndIsDeletedFalse(1L)).thenReturn(assetOptional);
        when(assetMapper.toAssetAndHistoriesResponse(assetOptional.get())).thenReturn(expected);

        AssetAndHistoriesResponse actual = assetServiceImpl.getAssetAndItsHistoriesByAssetId(1L);

        assertThat(actual, is(expected));
    }

    @Test
    void checkAssetIsValidForDeleteOrNot_WhenAssetIdNotExist_ShouldThrowNotFoundException() {
        long assetId = 1L;

        when(assetRepository.findByIdAndIsDeletedFalse(assetId)).thenReturn(Optional.empty());

        NotFoundException actual = assertThrows(NotFoundException.class, () -> {
            assetServiceImpl.checkAssetIsValidForDeleteOrNot(assetId);
        });

        assertThat(actual.getMessage(), is("Don't exist asset with this assetId."));
    }

    @Test
    void checkAssetIsValidForDeleteOrNot_WhenAssetIdExistButNotValidForDelete_ShouldThrowBadRequestException() {
        long assetId = 1L;

        when(assetRepository.findByIdAndIsDeletedFalse(assetId)).thenReturn(Optional.of(asset));
        when(assignAssetRepository.existsByAssetIdAndStatusAndIsDeletedFalse(assetId, EAssignStatus.ACCEPTED))
                .thenReturn(true);

        BadRequestException actual = assertThrows(BadRequestException.class, () -> {
            assetServiceImpl.checkAssetIsValidForDeleteOrNot(assetId);
        });

        assertThat(actual.getMessage(), is("Asset already assigned. Invalid for delete."));
    }

    @Test
    void getAssetReport_WhenDataValid_ShouldReturnListOfAssetResponse() {
        List<AssetReportResponseInterface> assetReportResponseInterfaces = mock(List.class);
        List<AssetReportResponse> expected = mock(List.class);

        when(assetRepository.getAssetReport()).thenReturn(assetReportResponseInterfaces);
        when(assetReportMapper.toListAssetReportResponses(assetReportResponseInterfaces)).thenReturn(expected);

        List<AssetReportResponse> actual = assetServiceImpl.getAssetReport();
        assertThat(actual, is(expected));
    }
}