package com.nashtech.assignment.controllers;

import com.nashtech.assignment.data.constants.EAssetStatus;
import com.nashtech.assignment.dto.request.asset.CreateNewAssetRequest;
import com.nashtech.assignment.dto.request.asset.EditAssetInformationRequest;
import com.nashtech.assignment.dto.request.asset.SearchFilterAssetRequest;
import com.nashtech.assignment.dto.response.PaginationResponse;
import com.nashtech.assignment.dto.response.asset.AssetAndHistoriesResponse;
import com.nashtech.assignment.dto.response.asset.AssetResponse;
import com.nashtech.assignment.exceptions.BadRequestException;
import com.nashtech.assignment.exceptions.NotFoundException;
import com.nashtech.assignment.services.create.CreateAssetService;
import com.nashtech.assignment.services.delete.DeleteAssetService;
import com.nashtech.assignment.services.edit.EditAssetService;
import com.nashtech.assignment.services.get.GetAssetService;
import com.nashtech.assignment.services.search.SearchAssetService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.text.ParseException;
import java.util.List;

@RestController
@RequestMapping("/api/asset")
public class AssetController {

    @Autowired
    private SearchAssetService searchAssetService;
    @Autowired
    private EditAssetService editAssetService;
    @Autowired
    private DeleteAssetService deleteAssetService;
    @Autowired
    private GetAssetService getAssetService;
    @Autowired
    private CreateAssetService createAssetService;

    @Operation(summary = "Get asset detail and its histories by assetId")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Get asset detail successfully.", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = AssetAndHistoriesResponse.class))}),
            @ApiResponse(responseCode = "404", description = "Not exist asset with this assetId.", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = NotFoundException.class))})
    })
    @GetMapping("/{assetId}")
    public ResponseEntity<AssetAndHistoriesResponse> getAssetAndItsHistoriesByAssetId(@PathVariable Long assetId) {
        return ResponseEntity.status(HttpStatus.OK).body(getAssetService.getAssetAndItsHistoriesByAssetId(assetId));
    }

    @Operation(summary = "Filter all assets by assetCode or assetName (optional) in list status (optional) and in list categories (optional) same location with current user with pagination")
    @ApiResponse(responseCode = "200", description = "Get list assets successfully.")
    @GetMapping
    public ResponseEntity<PaginationResponse<List<AssetResponse>>> filterAllAssetsByLocationAndKeyWordInStatusesAndCategoriesWithPagination(
            @RequestParam(name = "key-word", required = false) String keyword,
            @RequestParam(name = "statuses", required = false) List<EAssetStatus> statuses,
            @RequestParam(name = "categories", required = false) List<Integer> categoryIds,
            @RequestParam(name = "limit", defaultValue = "20") Integer limit,
            @RequestParam(name = "page", defaultValue = "0") Integer page,
            @RequestParam(name = "sort-field", defaultValue = "name") String sortField,
            @RequestParam(name = "sort-type", defaultValue = "ASC") String sortType) {
        SearchFilterAssetRequest searchFilterAssetRequest = SearchFilterAssetRequest.builder()
                .keyword(keyword.trim().length() == 0 ? null : keyword)
                .statuses(statuses.isEmpty() ? null : statuses)
                .categoryIds(categoryIds.isEmpty() ? null : categoryIds)
                .limit(limit)
                .page(page)
                .sortField(sortField)
                .sortType(sortType).build();
        return ResponseEntity.status(HttpStatus.OK)
                .body(searchAssetService.filterAllAssetsByLocationAndKeyWordInStatusesAndCategoriesWithPagination(
                        searchFilterAssetRequest));
    }

    @Operation(summary = "Edit asset information")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Edit asset success.", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = AssetResponse.class))}),
            @ApiResponse(responseCode = "400", description = "Asset have state is assigned", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = BadRequestException.class))}),
            @ApiResponse(responseCode = "404", description = "Asset not found", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = NotFoundException.class))})
    })
    @PutMapping("/{assetId}")
    public ResponseEntity<AssetResponse> editAssetInformation(
            @Valid @RequestBody EditAssetInformationRequest editAssetInformationRequest, @PathVariable Long assetId)
            throws ParseException {
        return ResponseEntity.status(HttpStatus.OK)
                .body(editAssetService.editAssetInformation(assetId, editAssetInformationRequest));
    }

    @Operation(summary = "Create new asset")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Create new asset success.", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = AssetResponse.class))}),
            @ApiResponse(responseCode = "404", description = "Asset name, category name, specification and installed date cannot be blank.", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = BadRequestException.class))})
    })
    @PostMapping
    public ResponseEntity<AssetResponse> createAssetResponse(
            @Valid @RequestBody CreateNewAssetRequest createNewAssetRequest) throws ParseException {
        return ResponseEntity.status(HttpStatus.OK)
                .body(createAssetService.createAssetResponse(createNewAssetRequest));
    }

    @Operation(summary = "Check asset is valid for delete or not by assetId")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Asset is valid for delete.", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = AssetAndHistoriesResponse.class))}),
            @ApiResponse(responseCode = "400", description = "Asset is not valid for delete.", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = BadRequestException.class))}),
            @ApiResponse(responseCode = "404", description = "Not exist asset with assetId.", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = NotFoundException.class))})
    })
    @GetMapping("/check-asset/{assetId}")
    public ResponseEntity<Void> checkAssetIsValidForDeleteOrNot(@PathVariable Long assetId) {
        getAssetService.checkAssetIsValidForDeleteOrNot(assetId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @Operation(summary = "Delete asset by assetId")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Delete successfully.", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = AssetAndHistoriesResponse.class))}),
            @ApiResponse(responseCode = "400", description = "Asset is not valid for delete.", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = BadRequestException.class))}),
            @ApiResponse(responseCode = "404", description = "Not exist asset with assetId.", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = NotFoundException.class))})
    })
    @DeleteMapping("/{assetId}")
    public ResponseEntity<Void> deleteAssetByAssetId(@PathVariable Long assetId) {
        deleteAssetService.deleteAssetByAssetId(assetId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @Operation(summary = "Get all assets by assetStatus")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Get all assets successfully.")
    })
    @GetMapping("/status/{assetStatus}")
    public ResponseEntity<List<AssetResponse>> getAllAssetByAssetStatus(@PathVariable EAssetStatus assetStatus) {
        return ResponseEntity.status(HttpStatus.OK).body(getAssetService.getAllAssetByAssetStatus(assetStatus));
    }

    @Operation(summary = "Get all assets by assetStatus with pagination")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Get all assets successfully.")
    })
    @GetMapping("/status/{assetStatus}/pagination")
    public ResponseEntity<PaginationResponse<List<AssetResponse>>> getAllAssetByAssetStatusWithPagination(
            @PathVariable EAssetStatus assetStatus,
            @RequestParam(name = "page", defaultValue = "0") Integer page,
            @RequestParam(name = "limit", defaultValue = "20") Integer limit,
            @RequestParam(name = "sort-field", defaultValue = "name") String sortField,
            @RequestParam(name = "sort-type", defaultValue = "ASC") String sortType) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(getAssetService.getAllAssetByAssetStatusWithPagination(assetStatus, page, limit, sortField, sortType));
    }
}
