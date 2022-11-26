package com.nashtech.assignment.controllers;

import com.nashtech.assignment.dto.response.category.CategoryResponse;
import com.nashtech.assignment.services.GetService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/category")
public class CategoryController {

    @Autowired
    private GetService getService;

    @Operation(summary = "Get all categories")
    @ApiResponse(responseCode = "200", description = "Get all categories successfully.")
    @GetMapping
    public ResponseEntity<List<CategoryResponse>> getAllCategories() {
        return ResponseEntity.status(HttpStatus.OK).body(getService.getAllCategories());
    }
}
