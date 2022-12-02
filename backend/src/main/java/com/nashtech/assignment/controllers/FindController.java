package com.nashtech.assignment.controllers;

import com.nashtech.assignment.data.constants.EUserType;
import com.nashtech.assignment.dto.response.PaginationResponse;
import com.nashtech.assignment.dto.response.user.UserResponse;
import com.nashtech.assignment.services.get.GetUserService;
import com.nashtech.assignment.services.search.SearchUserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/find")
public class FindController {

    @Autowired
    private SearchUserService searchUserService;
    @Autowired
    private GetUserService getUserService;

    @Operation(summary = "Find all users by location. Receive user location and page")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Return list of user, total page and total row")
    })
    @GetMapping
    public ResponseEntity<PaginationResponse<List<UserResponse>>> findByLocation(
            @RequestParam String location, @RequestParam Integer pageNumber) {
        return ResponseEntity.status(HttpStatus.OK).body(
                searchUserService.findByLocation(location, pageNumber)
        );
    }

    @Operation(summary = "View user detail by receive staff code")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Return user detail base on staffcode ")
    })
    @GetMapping("/get/{staffCode}")
    public UserResponse viewUserDetails(@PathVariable String staffCode) {
        return getUserService.viewUserDetails(staffCode);
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
        return searchUserService.filterByType(type, page, location);
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
        return searchUserService.searchByNameOrStaffCodeAndFilterByTypeAndLocation(page, name, staffCode, type, location);
    }
}
