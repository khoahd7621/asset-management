package com.nashtech.assignment.controllers;

import javax.validation.Valid;

import com.nashtech.assignment.services.CreateService;
import com.nashtech.assignment.services.EditService;
import com.nashtech.assignment.services.DeleteService;

import com.nashtech.assignment.services.get.GetUserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;

import org.hibernate.type.TrueFalseType;
import org.springframework.beans.factory.annotation.Autowired;
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

import com.nashtech.assignment.dto.request.user.ChangePasswordFirstRequest;
import com.nashtech.assignment.dto.request.user.ChangePasswordRequest;
import com.nashtech.assignment.dto.request.user.CreateNewUserRequest;
import com.nashtech.assignment.dto.request.user.EditUserRequest;
import com.nashtech.assignment.dto.response.user.UserResponse;
import com.nashtech.assignment.exceptions.BadRequestException;
import com.nashtech.assignment.exceptions.NotFoundException;

import java.text.ParseException;
import java.util.List;

@RestController
@RequestMapping("/api/user")
public class UserController {

    @Autowired
    private CreateService createService;
    @Autowired
    private EditService editService;
    @Autowired
    private DeleteService deleteService;
    @Autowired
    private GetUserService getUserService;

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

    @Operation(summary = "Change password when user first login")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Change password successfully", content = {
                    @Content(mediaType = "application/json") }),
            @ApiResponse(responseCode = "400", description = "Password no change", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = BadRequestException.class)) }) })
    @PutMapping("/change-password/first")
    public ResponseEntity<UserResponse> changePasswordFirst(
            @Valid @RequestBody ChangePasswordFirstRequest changePasswordFirstRequest) {
        return ResponseEntity.status(HttpStatus.OK).body(editService.changePasswordFirst(changePasswordFirstRequest));
    }

    @Operation(summary = "Change password")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Change password successfully", content = {
                    @Content(mediaType = "application/json") }),
            @ApiResponse(responseCode = "400", description = "Password is incrrect or Password no change", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = BadRequestException.class)) }) })
    @PutMapping("/change-password")
    public ResponseEntity<UserResponse> changePassword(
            @Valid @RequestBody ChangePasswordRequest changePasswordRequest) {
        return ResponseEntity.status(HttpStatus.OK).body(editService.changePassword(changePasswordRequest));
    }

    @Operation(summary = "Get all users")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Get all users successfully.")
    })
    @GetMapping()
    public ResponseEntity<List<UserResponse>> getAllUsers() {
        return ResponseEntity.status(HttpStatus.OK).body(getUserService.getAllUsers());
    }
}
