package com.nashtech.assignment.controllers;

import com.nashtech.assignment.dto.request.category.CreateNewCategoryRequest;
import com.nashtech.assignment.dto.response.category.CategoryResponse;
import com.nashtech.assignment.exceptions.BadRequestException;
import com.nashtech.assignment.services.category.CategoryService;
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
import java.util.List;

@RestController
@RequestMapping("/api/category")
public class CategoryController {

    @Autowired
    private CategoryService categoryService;

    @Operation(summary = "Create new category")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Create new asset success.", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = CategoryResponse.class))}),
            @ApiResponse(responseCode = "409", description = "Category name or prefix assetCode is existed.", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = BadRequestException.class))})
    })
    @PostMapping
    public ResponseEntity<CategoryResponse> createNewCategory(
            @Valid @RequestBody CreateNewCategoryRequest createNewCategoryRequest) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(categoryService.createNewCategory(createNewCategoryRequest));
    }

    @Operation(summary = "Get all categories")
    @ApiResponse(responseCode = "200", description = "Get all categories successfully.")
    @GetMapping
    public ResponseEntity<List<CategoryResponse>> getAllCategories() {
        return ResponseEntity.status(HttpStatus.OK).body(categoryService.getAllCategories());
    }
}
