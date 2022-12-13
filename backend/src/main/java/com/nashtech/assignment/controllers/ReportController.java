package com.nashtech.assignment.controllers;

import com.nashtech.assignment.dto.response.report.AssetReportResponse;
import com.nashtech.assignment.dto.response.report.AssetReportResponseInterface;
import com.nashtech.assignment.services.asset.AssetService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/report")
public class ReportController {

    @Autowired
    private AssetService assetService;

    @Operation(summary = "Get report of asset")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "List of asset report response", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = AssetReportResponseInterface.class))
            })
    })
    @GetMapping()
    public ResponseEntity<List<AssetReportResponse>> getReport() {
        return ResponseEntity.status(HttpStatus.OK).body(assetService.getAssetReport());
    }
}
