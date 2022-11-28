package com.nashtech.assignment.controllers;

import javax.validation.Valid;

import com.nashtech.assignment.services.CreateService;
import com.nashtech.assignment.services.EditService;
import com.nashtech.assignment.services.DeleteService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;

import org.hibernate.type.TrueFalseType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.nashtech.assignment.dto.request.user.CreateNewUserRequest;
import com.nashtech.assignment.dto.request.user.EditUserRequest;
import com.nashtech.assignment.dto.response.user.UserResponse;
import com.nashtech.assignment.exceptions.BadRequestException;
import com.nashtech.assignment.exceptions.NotFoundException;

import java.text.ParseException;

@RestController
@RequestMapping("/api/user")
public class UserController {

    @Autowired
    CreateService createService;
    @Autowired
    EditService editService;
    @Autowired
    DeleteService deleteService;

    @Operation(summary = "Create new user")
    @ApiResponses(value = { @ApiResponse(responseCode = "200", description = "Create user successfully", content = {
            @Content(mediaType = "application/json", schema = @Schema(implementation = UserResponse.class)) }) })
    @PostMapping
    public ResponseEntity<UserResponse> createNewUser(@Valid @RequestBody CreateNewUserRequest createNewUserRequest)
            throws ParseException {
        return ResponseEntity.status(HttpStatus.OK).body(createService.createNewUser(createNewUserRequest));
    }

    @Operation(summary = "Edit user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Edit user successfully", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = UserResponse.class)) }),
            @ApiResponse(responseCode = "400", description = "User not valid for edit", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = BadRequestException.class)) }),
            @ApiResponse(responseCode = "404", description = "User not found", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = NotFoundException.class)) }) })
    @PutMapping("/edit")
    public ResponseEntity<UserResponse> editUser(@Valid @RequestBody EditUserRequest editUserRequest)
            throws ParseException {
        return ResponseEntity.status(HttpStatus.OK).body(editService.editUserInformation(editUserRequest));
    }

    @Operation(summary = "Check user valid for delete")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User successfully for delete", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = TrueFalseType.class)) }),
            @ApiResponse(responseCode = "400", description = "User not valid for delete", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = BadRequestException.class)) }),
            @ApiResponse(responseCode = "404", description = "User not found", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = NotFoundException.class)) }) })
    @GetMapping("/check-user")
    public ResponseEntity<Boolean> checkValidUserForDelete(@RequestParam String staffCode) {
        return ResponseEntity.status(HttpStatus.OK).body(deleteService.checkValidUser(staffCode));
    }

    @Operation(summary = "Delete user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Delete user successfully", content = {
                    @Content(mediaType = "application/json") }),
            @ApiResponse(responseCode = "400", description = "User not valid for delete", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = BadRequestException.class)) }),
            @ApiResponse(responseCode = "404", description = "User not found", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = NotFoundException.class)) }) })
    @DeleteMapping
    public ResponseEntity<Void> deleteUser(@RequestParam String staffCode) {
        deleteService.deleteUser(staffCode);
        return ResponseEntity.noContent().build();
    }
}
