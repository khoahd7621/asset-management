package com.nashtech.assignment.controllers;

import com.nashtech.assignment.data.constants.EReturnStatus;
import com.nashtech.assignment.dto.response.PaginationResponse;
import com.nashtech.assignment.dto.response.returned.ReturnAssetResponse;
import com.nashtech.assignment.exceptions.BadRequestException;
import com.nashtech.assignment.exceptions.ForbiddenException;
import com.nashtech.assignment.exceptions.NotFoundException;
import com.nashtech.assignment.services.returned.ReturnedService;
import com.nashtech.assignment.services.search.SearchReturnAssetService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.text.ParseException;
import java.util.List;

@RestController
@RequestMapping("/api/return-asset")
public class ReturnAssetController {

    @Autowired
    private SearchReturnAssetService searchReturnAssetService;
    @Autowired
    private ReturnedService returnedService;

    @Operation(summary = "Create request for returning asset")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Create request for returning asset successfully.", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = ReturnAssetResponse.class))}),
            @ApiResponse(responseCode = "400", description = "Assignment is not accepted or Assignment already exist in return list", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = BadRequestException.class))}),
            @ApiResponse(responseCode = "403", description = "Current user is not match to this assignment or current user is not admin ", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = ForbiddenException.class))}),
            @ApiResponse(responseCode = "404", description = "Assignment not found", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = NotFoundException.class))})
    })
    @PostMapping
    public ResponseEntity<ReturnAssetResponse> createReturnAsset(@RequestParam(value = "id") long id) {
        return ResponseEntity.status(HttpStatus.OK).body(returnedService.createReturnAsset(id));
    }

    @Operation(summary = "Search return asset by key-word, status, date, page")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Return list of return asset, total page")})
    @GetMapping
    public ResponseEntity<PaginationResponse<List<ReturnAssetResponse>>> searchAllReturnAssetsByStateAndReturnedDateAndSearchWithPagination(
            @RequestParam(name = "query", required = false) String query,
            @RequestParam(name = "statuses", required = false) List<EReturnStatus> statuses,
            @RequestParam(name = "date", required = false) String date,
            @RequestParam Integer page) throws ParseException {
        return ResponseEntity.status(HttpStatus.OK)
                .body(searchReturnAssetService
                        .searchAllReturnAssetsByStateAndReturnedDateAndSearchWithPagination(
                                query, statuses, date, page));
    }

    @Operation(summary = "Delete return asset")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Delete return asset", content = {
                    @Content(mediaType = "application/json")}),
            @ApiResponse(responseCode = "400", description = "Return asset not valid for delete", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = BadRequestException.class))}),
            @ApiResponse(responseCode = "404", description = "Return asset not found", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = NotFoundException.class))})})
    @DeleteMapping()
    public ResponseEntity<Void> cancelReturnAsset(@RequestParam() long id) {
        returnedService.deleteReturnAsset(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Complete returning request")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Complete returning request successfully", content = {
                    @Content(mediaType = "application/json")}),
            @ApiResponse(responseCode = "400", description = "Cannot complete returning request", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = BadRequestException.class))}),
            @ApiResponse(responseCode = "404", description = "Returning request not found", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = NotFoundException.class))})})
    @PatchMapping
    public ResponseEntity<Void> completeReturnRequest(@RequestParam() long id) {
        returnedService.completeReturnRequest(id);
        return ResponseEntity.noContent().build();
    }
}
