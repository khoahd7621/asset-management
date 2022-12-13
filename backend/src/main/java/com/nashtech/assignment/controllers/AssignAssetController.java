package com.nashtech.assignment.controllers;

import com.nashtech.assignment.data.constants.EAssignStatus;
import com.nashtech.assignment.dto.request.assignment.CreateNewAssignmentRequest;
import com.nashtech.assignment.dto.request.assignment.EditAssignmentRequest;
import com.nashtech.assignment.dto.response.PaginationResponse;
import com.nashtech.assignment.dto.response.assignment.AssignAssetResponse;
import com.nashtech.assignment.exceptions.BadRequestException;
import com.nashtech.assignment.exceptions.ForbiddenException;
import com.nashtech.assignment.exceptions.NotFoundException;
import com.nashtech.assignment.services.assignment.AssignmentService;
import com.nashtech.assignment.services.search.SearchAssignAssetService;
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
@RequestMapping("/api/assignment")
public class AssignAssetController {

    @Autowired
    private SearchAssignAssetService searchAssignAssetService;
    @Autowired
    private AssignmentService assignmentService;

    @Operation(summary = "Accept an assignment")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Accept assignment successfully.", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = AssignAssetResponse.class))}),
            @ApiResponse(responseCode = "400", description = "Assignment is not waiting for acceptance", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = BadRequestException.class))}),
            @ApiResponse(responseCode = "403", description = "Current user is not match to this assignment ", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = ForbiddenException.class))}),
            @ApiResponse(responseCode = "404", description = "Assignment not found", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = NotFoundException.class))})})
    @PutMapping("/user/accept/{id}")
    public ResponseEntity<AssignAssetResponse> acceptAssignAsset(@PathVariable Long id) {
        return ResponseEntity.status(HttpStatus.OK).body(assignmentService.acceptAssignAsset(id));
    }

    @Operation(summary = "Decline an assignment")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Decline assignment successfully.", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = AssignAssetResponse.class))}),
            @ApiResponse(responseCode = "400", description = "Assignment is not waiting for acceptance", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = BadRequestException.class))}),
            @ApiResponse(responseCode = "403", description = "Current user is not match to this assignment ", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = ForbiddenException.class))}),
            @ApiResponse(responseCode = "404", description = "Assignment not found", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = NotFoundException.class))})})
    @PutMapping("/user/decline/{id}")
    public ResponseEntity<AssignAssetResponse> declineAssignAsset(@PathVariable Long id) {
        return ResponseEntity.status(HttpStatus.OK).body(assignmentService.declineAssignAsset(id));
    }

    @Operation(summary = "Search assign by receive name, status, date, page")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Return list of assignAsset, total page and total row that suitable with the information that user provided")})
    @GetMapping
    public ResponseEntity<PaginationResponse<List<AssignAssetResponse>>> searchAssignAsset(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) List<EAssignStatus> status,
            @RequestParam(required = false) String date,
            @RequestParam Integer page) throws ParseException {
        return ResponseEntity.status(HttpStatus.OK)
                .body(searchAssignAssetService.filterAndSearchAssignAsset(name, status, date, page));
    }

    @Operation(summary = "Get assignment details")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Return details of assignment", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = AssignAssetResponse.class))}),
            @ApiResponse(responseCode = "404", description = "Assignment not found", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = NotFoundException.class))})})
    @GetMapping("/details")
    public ResponseEntity<AssignAssetResponse> getAssignmentDetails(
            @RequestParam Long id) {
        return ResponseEntity.status(HttpStatus.OK).body(assignmentService.getAssignAssetDetails(id));
    }

    @Operation(summary = "Create new assignment")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Create assignment successfully", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = AssignAssetResponse.class))}),
            @ApiResponse(responseCode = "400", description = "Status of this asset is not available | Assign date is before today", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = BadRequestException.class))}),
            @ApiResponse(responseCode = "404", description = "Not exist asset with this assetId | Not exist user with this userId", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = NotFoundException.class))})
    })
    @PostMapping
    public ResponseEntity<AssignAssetResponse> createNewAssignment(
            @RequestBody @Valid CreateNewAssignmentRequest createNewAssignmentRequest) throws ParseException {
        return ResponseEntity.status(HttpStatus.OK)
                .body(assignmentService.createNewAssignment(createNewAssignmentRequest));
    }

    @Operation(summary = "Edit assignment by assignmentId")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Edit assignment successfully", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = AssignAssetResponse.class))}),
            @ApiResponse(responseCode = "400", description = "Can only edit assignment with status waiting for acceptance | Can only assign asset with status available | Assign date is before today", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = BadRequestException.class))}),
            @ApiResponse(responseCode = "404", description = "Not exist assignment with assignment id | Not exist asset with asset id | Not exist user with user id", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = NotFoundException.class))})
    })
    @PutMapping("/{assignmentId}")
    public ResponseEntity<AssignAssetResponse> editAssignment(
            @PathVariable Long assignmentId,
            @RequestBody @Valid EditAssignmentRequest editAssignmentRequest) throws ParseException {
        return ResponseEntity.status(HttpStatus.OK)
                .body(assignmentService.editAssignment(assignmentId, editAssignmentRequest));
    }

    @Operation(summary = "Get all assign asset by user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Get all assign asset successfully.", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = AssignAssetResponse.class))}),
            @ApiResponse(responseCode = "404", description = "User doesn't assign asset", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = NotFoundException.class))})})
    @GetMapping("/user")
    public ResponseEntity<List<AssignAssetResponse>> findAllAssignAssetByUser() {
        return ResponseEntity.status(HttpStatus.OK).body(assignmentService.getAssignAssetOfUser());
    }

    @Operation(summary = "View detail assign asset")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "View detail assign asset successfully.", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = AssignAssetResponse.class))}),
            @ApiResponse(responseCode = "404", description = "Assign asset not found", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = NotFoundException.class))})})
    @GetMapping("/user/{assignAssetId}")
    public ResponseEntity<AssignAssetResponse> viewDetailAssignAsset(@PathVariable Long assignAssetId) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(assignmentService.getDetailAssignAssetOfUser(assignAssetId));
    }

    @Operation(summary = "Delete assignment")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Delete assignment successfully", content = {
                    @Content(mediaType = "application/json")}),
            @ApiResponse(responseCode = "400", description = "Assignment not valid for delete", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = BadRequestException.class))}),
            @ApiResponse(responseCode = "404", description = "Assignment not found", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = NotFoundException.class))})})
    @DeleteMapping
    public ResponseEntity<Void> deleteAssignAsset(@RequestParam Long assignmentId) {
        assignmentService.deleteAssignAsset(assignmentId);
        return ResponseEntity.noContent().build();
    }

}
