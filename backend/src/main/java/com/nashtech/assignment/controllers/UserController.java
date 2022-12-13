package com.nashtech.assignment.controllers;

import com.nashtech.assignment.data.constants.EUserType;
import com.nashtech.assignment.dto.request.user.*;
import com.nashtech.assignment.dto.response.PaginationResponse;
import com.nashtech.assignment.dto.response.user.UserResponse;
import com.nashtech.assignment.exceptions.BadRequestException;
import com.nashtech.assignment.exceptions.ForbiddenException;
import com.nashtech.assignment.exceptions.NotFoundException;
import com.nashtech.assignment.services.search.SearchUserService;
import com.nashtech.assignment.services.user.UserService;
import com.nashtech.assignment.services.validation.ValidationUserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.hibernate.type.TrueFalseType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.text.ParseException;
import java.util.List;

@RestController
@RequestMapping("/api/user")
public class UserController {

    @Autowired
    private SearchUserService searchUserService;
    @Autowired
    private UserService userService;
    @Autowired
    private ValidationUserService validationUserService;


    @Operation(summary = "View user detail by receive staff code")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Return user detail base on staffCode ")
    })
    @GetMapping("/get/{staffCode}")
    public UserResponse viewUserDetails(@PathVariable String staffCode) {
        return userService.viewUserDetails(staffCode);
    }

    @Operation(summary = "Create new user")
    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "Create user successfully", content = {
            @Content(mediaType = "application/json", schema = @Schema(implementation = UserResponse.class))})})
    @PostMapping
    public ResponseEntity<UserResponse> createNewUser(@Valid @RequestBody CreateNewUserRequest createNewUserRequest)
            throws ParseException {
        return ResponseEntity.status(HttpStatus.OK).body(userService.createNewUser(createNewUserRequest));
    }

    @Operation(summary = "Edit user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Edit user successfully", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = UserResponse.class))}),
            @ApiResponse(responseCode = "400", description = "User not valid for edit", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = BadRequestException.class))}),
            @ApiResponse(responseCode = "404", description = "User not found", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = NotFoundException.class))})})
    @PutMapping("/edit")
    public ResponseEntity<UserResponse> editUser(@Valid @RequestBody EditUserRequest editUserRequest)
            throws ParseException {
        return ResponseEntity.status(HttpStatus.OK).body(userService.editUserInformation(editUserRequest));
    }

    @Operation(summary = "Check user valid for delete")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User successfully for delete", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = TrueFalseType.class))}),
            @ApiResponse(responseCode = "400", description = "User not valid for delete", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = BadRequestException.class))}),
            @ApiResponse(responseCode = "404", description = "User not found", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = NotFoundException.class))})})
    @GetMapping("/check-user")
    public ResponseEntity<Boolean> checkValidUserForDelete(@RequestParam String staffCode) {
        return ResponseEntity.status(HttpStatus.OK).body(validationUserService.checkValidUserForDelete(staffCode));
    }

    @Operation(summary = "Delete user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Delete user successfully", content = {
                    @Content(mediaType = "application/json")}),
            @ApiResponse(responseCode = "400", description = "User not valid for delete", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = BadRequestException.class))}),
            @ApiResponse(responseCode = "404", description = "User not found", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = NotFoundException.class))})})
    @DeleteMapping
    public ResponseEntity<Void> deleteUser(@RequestParam String staffCode) {
        userService.deleteUser(staffCode);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Change password when user first login")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Change password successfully", content = {
                    @Content(mediaType = "application/json")}),
            @ApiResponse(responseCode = "400", description = "Password no change", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = BadRequestException.class))})})
    @PutMapping("/change-password/first")
    public ResponseEntity<UserResponse> changePasswordFirst(
            @Valid @RequestBody ChangePasswordFirstRequest changePasswordFirstRequest) {
        return ResponseEntity.status(HttpStatus.OK).body(userService.changePasswordFirst(changePasswordFirstRequest));
    }

    @Operation(summary = "Change password")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Change password successfully", content = {
                    @Content(mediaType = "application/json")}),
            @ApiResponse(responseCode = "400", description = "Password is incorrect or Password no change", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = BadRequestException.class))})})
    @PutMapping("/change-password")
    public ResponseEntity<UserResponse> changePassword(
            @Valid @RequestBody ChangePasswordRequest changePasswordRequest) {
        return ResponseEntity.status(HttpStatus.OK).body(userService.changePassword(changePasswordRequest));
    }

    @Operation(summary = "Search all users by staffCode or fullName (optional) in list types (optional) same location with current user with pagination")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Get all users successfully.")
    })
    @GetMapping("/search")
    public ResponseEntity<PaginationResponse<List<UserResponse>>> searchAllUsersByKeyWordInTypesWithPagination(
            @RequestParam(name = "key-word", required = false) String keyword,
            @RequestParam(name = "types", required = false) List<EUserType> types,
            @RequestParam(name = "limit", defaultValue = "20") Integer limit,
            @RequestParam(name = "page", defaultValue = "0") Integer page,
            @RequestParam(name = "sort-field", defaultValue = "firstName") String sortField,
            @RequestParam(name = "sort-type", defaultValue = "ASC") String sortType) {
        SearchUserRequest searchUserRequest = SearchUserRequest.builder()
                .keyword(keyword.trim().length() == 0 ? null : keyword)
                .types(types.isEmpty() ? null : types)
                .limit(limit)
                .page(page)
                .sortField(sortField)
                .sortType(sortType).build();
        return ResponseEntity.status(HttpStatus.OK).body(searchUserService
                .searchAllUsersByKeyWordInTypesWithPagination(searchUserRequest));
    }

    @Operation(summary = "Get information current user logged in")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Get information current user logged in successfully", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = UserResponse.class))}),
            @ApiResponse(responseCode = "403", description = "You don't have permission to get other user information", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = ForbiddenException.class))})})
    @GetMapping("/{username}/current")
    public ResponseEntity<UserResponse> getCurrentUserLoggedInInformation(@PathVariable String username) {
        return ResponseEntity.status(HttpStatus.OK).body(userService.getCurrentUserLoggedInInformation(username));
    }
}
