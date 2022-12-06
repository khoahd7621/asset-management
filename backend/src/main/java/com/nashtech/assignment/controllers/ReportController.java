package com.nashtech.assignment.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.nashtech.assignment.dto.response.report.AssetReportResponse;
import com.nashtech.assignment.services.get.GetAssetReportService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

@RestController
@RequestMapping("/api/report")
public class ReportController {
    @Autowired
    private GetAssetReportService getAssetReportService;

    @Operation(summary = "Get report of asset")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "List of asset report response", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = AssetReportResponse.class))
            })
    })
    @GetMapping()
    public ResponseEntity<List<AssetReportResponse>> getReport() {
        return ResponseEntity.status(HttpStatus.OK).body(
                getAssetReportService.getAssetReport());
    }
}
