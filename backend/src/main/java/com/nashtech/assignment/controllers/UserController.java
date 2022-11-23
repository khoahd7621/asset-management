package com.nashtech.assignment.controllers;

import javax.validation.Valid;

import com.nashtech.assignment.services.CreateService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.nashtech.assignment.dto.request.CreateNewUserRequest;
import com.nashtech.assignment.dto.response.user.UserResponse;


import java.text.ParseException;

@RestController
@RequestMapping("/api/user")
public class UserController {

    @Autowired
    CreateService createService;

    @Operation(summary = "Create new user")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Create user successfully", content = {
            @Content(mediaType = "application/json", schema = @Schema(implementation = UserResponse.class))
        })
    })
    @PostMapping
    public ResponseEntity<UserResponse> createNewUser(@Valid @RequestBody CreateNewUserRequest createNewUserRequest)
            throws ParseException {
        return ResponseEntity.status(HttpStatus.OK).body(createService.createNewUser(createNewUserRequest));
    }
}
