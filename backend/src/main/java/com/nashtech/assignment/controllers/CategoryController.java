package com.nashtech.assignment.controllers;

import java.util.List;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.nashtech.assignment.dto.request.category.CreateNewCategoryRequest;
import com.nashtech.assignment.dto.response.category.CategoryResponse;
import com.nashtech.assignment.exceptions.BadRequestException;
import com.nashtech.assignment.services.CreateService;
import com.nashtech.assignment.services.GetService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

@RestController
@RequestMapping("/api/category")
public class CategoryController {
    @Autowired
    CreateService createService;

    @Autowired
    private GetService getService;

    @Operation(summary = "Create new category")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Create new asset success.", content = {
            @Content(mediaType = "application/json", schema = @Schema(implementation = CategoryResponse.class)) }),
        @ApiResponse(responseCode = "404", description = "Category name or prefix assetcode is existed.", content = {
            @Content(mediaType = "application/json", schema = @Schema(implementation = BadRequestException.class)) })
    })
    @PostMapping
    public ResponseEntity<CategoryResponse> createNewCategory(
        @Valid @RequestBody CreateNewCategoryRequest createNewCategoryRequest) {
        return ResponseEntity.status(HttpStatus.OK)
            .body(createService.createNewCategory(createNewCategoryRequest));
    }

    @Operation(summary = "Get all categories")
    @ApiResponse(responseCode = "200", description = "Get all categories successfully.")
    @GetMapping
    public ResponseEntity<List<CategoryResponse>> getAllCategories() {
        return ResponseEntity.status(HttpStatus.OK).body(getService.getAllCategories());
    }
}
