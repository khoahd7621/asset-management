package com.nashtech.assignment.controllers;

import com.nashtech.assignment.dto.request.UserLoginRequest;
import com.nashtech.assignment.dto.response.UserLoginResponse;
import com.nashtech.assignment.exceptions.BadRequestException;
import com.nashtech.assignment.services.auth.LoginService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
@RequestMapping("/api")
public class AuthController {

    @Autowired
    private LoginService loginService;

    @Operation(summary = "Login")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Login successfully.", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = UserLoginResponse.class))}),
            @ApiResponse(responseCode = "400", description = "Email or password is incorrect. | User is not active.", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = BadRequestException.class))})
    })
    @PostMapping("/login")
    public ResponseEntity<UserLoginResponse> login(@Valid @RequestBody UserLoginRequest userLoginRequest) {
        return ResponseEntity.status(HttpStatus.OK).body(
                loginService.login(userLoginRequest));
    }
}
