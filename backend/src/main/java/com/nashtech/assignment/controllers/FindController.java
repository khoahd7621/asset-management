package com.nashtech.assignment.controllers;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.nashtech.assignment.data.constants.EUserType;
import com.nashtech.assignment.dto.response.PaginationResponse;
import com.nashtech.assignment.dto.response.user.UserResponse;
import com.nashtech.assignment.services.FindService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.responses.ApiResponse;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;


@RestController
@RequestMapping("/api/find")
public class FindController {
    
    FindService findService;

    @Autowired
    public FindController(FindService findService) {
        this.findService = findService;
    }

    @Operation(summary = "Find all users by location. Receive user location and page")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Return list of user, total page and total row")
    })
    @GetMapping
    public ResponseEntity<PaginationResponse<List<UserResponse>>> findByLocation(
            @RequestParam String location, @RequestParam Integer pageNumber) {
        return ResponseEntity.status(HttpStatus.OK).body(
            findService.findByLocation(location, pageNumber)
        );
    }

    @Operation(summary = "View user detail by receive staff code")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Return user detail base on staffcode ")
    })
    @GetMapping("/get/{staffCode}")
    public UserResponse viewUserDetails(@PathVariable String staffCode) {
        return findService.viewUserDetails(staffCode);
    }

    @Operation(summary = "Filter user by receive admin location, user type, page")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Return list of user, total page and total row base on type and page user required ")
    })
    @GetMapping("/filter/{page}")
    public PaginationResponse<List<UserResponse>> filterByType(
            @RequestParam(required = false) EUserType type,
            @RequestParam String location,
            @PathVariable int page) {
        return findService.filterByType(type, page,location);
    }

    @Operation(summary = "Search user by receive admin location, user type, name or staffcode, page")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Return list of user, total page and total row that suitable with the information that user provided")
    })
    @GetMapping("/search")
    public PaginationResponse<List<UserResponse>> search(
            @RequestParam int page,
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String staffCode,
            @RequestParam(required = false) EUserType type,
            @RequestParam String location) {
        return findService.searchByNameOrStaffCodeAndFilterByTypeAndLocation(page,name, staffCode, type, location);
    }
}
