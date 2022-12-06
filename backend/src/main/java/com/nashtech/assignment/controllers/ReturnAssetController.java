package com.nashtech.assignment.controllers;

import java.text.ParseException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.nashtech.assignment.data.constants.EReturnStatus;
import com.nashtech.assignment.dto.response.PaginationResponse;
import com.nashtech.assignment.dto.response.return_asset.ReturnAssetResponse;
import com.nashtech.assignment.exceptions.BadRequestException;
import com.nashtech.assignment.exceptions.ForbiddenException;
import com.nashtech.assignment.exceptions.NotFoundException;
import com.nashtech.assignment.services.create.CreateReturnAssetService;
import com.nashtech.assignment.services.search.SearchReturnAssetService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

@RestController
@RequestMapping("/api/return-asset")
public class ReturnAssetController {
        @Autowired
        private CreateReturnAssetService createReturnAssetService;
        @Autowired
        private SearchReturnAssetService searchReturnAssetService;

        @Operation(summary = "Create request for returning asset")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Create request for returning asset successfully.", content = {
                                        @Content(mediaType = "application/json", schema = @Schema(implementation = ReturnAssetResponse.class)) }),
                        @ApiResponse(responseCode = "400", description = "Assignment is not accepted or Assignment already exist in return list", content = {
                                        @Content(mediaType = "application/json", schema = @Schema(implementation = BadRequestException.class)) }),
                        @ApiResponse(responseCode = "403", description = "Current user is not match to this assignment or current user is not admin ", content = {
                                        @Content(mediaType = "application/json", schema = @Schema(implementation = ForbiddenException.class)) }),
                        @ApiResponse(responseCode = "404", description = "Assignment not found", content = {
                                        @Content(mediaType = "application/json", schema = @Schema(implementation = NotFoundException.class)) })
        })
        @PostMapping
        public ResponseEntity<ReturnAssetResponse> createReturnAsset(@RequestParam(value = "id") long id) {
                return ResponseEntity.status(HttpStatus.OK)
                                .body(createReturnAssetService.createReturnAsset(id));
        }

        @Operation(summary = "Search return asset by key-word, status, date, page")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Return list of return asset, total page", content = {
                                        @Content(mediaType = "application/json", schema = @Schema(implementation = PaginationResponse.class)) }) })
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

}
