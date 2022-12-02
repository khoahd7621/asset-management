package com.nashtech.assignment.controllers;

import java.text.ParseException;
import java.util.List;

import com.nashtech.assignment.dto.request.assignment.CreateNewAssignmentRequest;
import com.nashtech.assignment.exceptions.BadRequestException;
import com.nashtech.assignment.services.create.CreateAssignmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
    @Autowired
    private CreateAssignmentService createAssignmentService;

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

    @Operation(summary = "Create new assignment")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Create assignment successfully", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = AssignAssetResponse.class)) }),
            @ApiResponse(responseCode = "400", description = "Status of this asset is not available | Assign date is before today", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = BadRequestException.class)) }),
            @ApiResponse(responseCode = "404", description = "Not exist asset with this assetId | Not exist user with this userId | Assigned date cannot before joined date of user.", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = NotFoundException.class)) })
    })
    @PostMapping
    public ResponseEntity<AssignAssetResponse> createNewAssignment(
            @RequestBody CreateNewAssignmentRequest createNewAssignmentRequest) {
        return ResponseEntity.status(HttpStatus.OK).body(createAssignmentService.createNewAssignment(createNewAssignmentRequest));
    }
}
