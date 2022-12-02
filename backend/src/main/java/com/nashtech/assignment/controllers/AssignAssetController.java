package com.nashtech.assignment.controllers;

import java.text.ParseException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.nashtech.assignment.data.constants.EAssignStatus;
import com.nashtech.assignment.dto.response.PaginationResponse;
import com.nashtech.assignment.dto.response.assignment.AssignAssetResponse;
import com.nashtech.assignment.exceptions.NotFoundException;
import com.nashtech.assignment.services.GetService;
import com.nashtech.assignment.services.search.SearchAssignAssetService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

@RestController
@RequestMapping("/api/assignment")
public class AssignAssetController {

        @Autowired
        SearchAssignAssetService searchAssignAssetService;
        @Autowired
        GetService getService;

        @Operation(summary = "Search assign by receive name, status, date, page")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Return list of assignAsset, total page and total row that suitable with the information that user provided", content = {
                                @Content(mediaType = "application/json", schema = @Schema(implementation = PaginationResponse.class)) }) })
        @GetMapping
        public ResponseEntity<PaginationResponse<List<AssignAssetResponse>>> searchAssignAsset(
                        @RequestParam(required = false) String name,
                        @RequestParam(required = false) List<EAssignStatus> status,
                        @RequestParam(required = false) String date,
                        @RequestParam Integer page) throws ParseException {
                return ResponseEntity.status(HttpStatus.OK)
                        .body(searchAssignAssetService.filterAndSearchAssignAsset(name,
                                status, date, page));
        }

        @Operation(summary = "Get assignment details")
        @ApiResponses(value = {
                @ApiResponse(responseCode = "200", description = "Return details of assignment", content = {
                        @Content(mediaType = "application/json", schema = @Schema(implementation = AssignAssetResponse.class)) }),
                @ApiResponse(responseCode = "404", description = "Assignment not found", content = {
                        @Content(mediaType = "application/json", schema = @Schema(implementation = NotFoundException.class)) }) })
        @GetMapping("/details")
        public ResponseEntity<AssignAssetResponse> getAssignmentDetails(
                        @RequestParam Long id) {
                return ResponseEntity.status(HttpStatus.OK)
                                .body(getService.getAssignAssetDetails(id));
        }
}
