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
import com.nashtech.assignment.services.CreateService;
import com.nashtech.assignment.services.EditService;
import com.nashtech.assignment.services.FilterService;
import com.nashtech.assignment.services.GetService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

import java.text.ParseException;
import java.util.List;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/asset")
public class AssetController {
    @Autowired
    private CreateService createService;
    @Autowired
    private FilterService filterService;
    @Autowired
    private GetService getService;
    @Autowired
    private EditService editService;

    @Operation(summary = "Get asset detail and its histories by assetId")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Get asset detail successfully.", content = {
            @Content(mediaType = "application/json", schema = @Schema(implementation = AssetAndHistoriesResponse.class)) }),
        @ApiResponse(responseCode = "404", description = "Not exist asset with this assetId.", content = {
            @Content(mediaType = "application/json", schema = @Schema(implementation = NotFoundException.class)) })
    })
    @GetMapping("/{assetId}")
    public ResponseEntity<AssetAndHistoriesResponse> getAssetAndItsHistoriesByAssetId(@PathVariable Long assetId) {
      return ResponseEntity.status(HttpStatus.OK).body(getService.getAssetAndItsHistoriesByAssetId(assetId));
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
                .body(filterService
                        .filterAllAssetsByLocationAndKeyWordInStatusesAndCategoriesWithPagination(
                                searchFilterAssetRequest));
    }

    @Operation(summary = "Edit asset information")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Edit asset success.", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = AssetResponse.class)) }),
            @ApiResponse(responseCode = "400", description = "Asset have state is assigned", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = BadRequestException.class)) }),
            @ApiResponse(responseCode = "404", description = "Asset not found", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = NotFoundException.class)) })
    })
    @PutMapping("/{assetId}")
    public ResponseEntity<AssetResponse> editAssetInformation(
            @Valid @RequestBody EditAssetInformationRequest editAssetInformationRequest, @PathVariable Long assetId)
            throws ParseException {
        return ResponseEntity.status(HttpStatus.OK)
                .body(editService.editAssetInformation(assetId, editAssetInformationRequest));
    }

    @Operation(summary = "Create new asset")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Create new asset success.", content = {
            @Content(mediaType = "application/json", schema = @Schema(implementation = AssetResponse.class)) }),
        @ApiResponse(responseCode = "404", description = "Asset name, category name, specification and installed date cannot be blank.", content = {
            @Content(mediaType = "application/json", schema = @Schema(implementation = BadRequestException.class)) })
    })
    @PostMapping
    public ResponseEntity<AssetResponse> createAssetResponse(
        @Valid @RequestBody CreateNewAssetRequest createNewAssetRequest) throws ParseException {
      return ResponseEntity.status(HttpStatus.OK)
          .body(createService.createAssetResponse(createNewAssetRequest));
    }
}