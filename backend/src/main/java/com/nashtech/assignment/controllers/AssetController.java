package com.nashtech.assignment.controllers;

import com.nashtech.assignment.data.constants.EAssetStatus;
import com.nashtech.assignment.dto.request.asset.SearchFilterAssetRequest;
import com.nashtech.assignment.dto.response.PaginationResponse;
import com.nashtech.assignment.dto.response.asset.AssetAndHistoriesResponse;
import com.nashtech.assignment.dto.response.asset.AssetResponse;
import com.nashtech.assignment.exceptions.NotFoundException;
import com.nashtech.assignment.services.FilterService;
import com.nashtech.assignment.services.GetService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/asset")
public class AssetController {

    @Autowired
    private FilterService filterService;
    @Autowired
    private GetService getService;

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
    public ResponseEntity<PaginationResponse<List<AssetResponse>>>
        filterAllAssetsByLocationAndKeyWordInStatusesAndCategoriesWithPagination(
            @RequestParam(name = "key-word", required = false) String keyword,
            @RequestParam(name = "statuses", required = false) List<EAssetStatus> statuses,
            @RequestParam(name = "categories", required = false) List<Integer> categoryIds,
            @RequestParam(name = "limit", defaultValue = "20") Integer limit,
            @RequestParam(name = "page", defaultValue = "0") Integer page,
            @RequestParam(name = "sort-field", defaultValue = "name") String sortField,
            @RequestParam(name = "sort-type", defaultValue = "ASC") String sortType
    ) {
        SearchFilterAssetRequest searchFilterAssetRequest = SearchFilterAssetRequest.builder()
                .keyword(keyword.trim().length() == 0 ? null : keyword)
                .statuses(statuses.isEmpty() ? null : statuses)
                .categoryIds(categoryIds.isEmpty() ? null : categoryIds)
                .limit(limit)
                .page(page)
                .sortField(sortField)
                .sortType(sortType).build();
        return ResponseEntity.status(HttpStatus.OK)
                .body(filterService.filterAllAssetsByLocationAndKeyWordInStatusesAndCategoriesWithPagination(searchFilterAssetRequest));
    }
}
