package com.nashtech.assignment.services.search;

import java.text.ParseException;
import java.util.List;

import com.nashtech.assignment.data.constants.EAssignStatus;
import com.nashtech.assignment.dto.response.PaginationResponse;
import com.nashtech.assignment.dto.response.assignment.AssignAssetResponse;

public interface SearchAssignAssetService {
    PaginationResponse<List<AssignAssetResponse>> filterAndSearchAssignAsset(String name, List<EAssignStatus> status,
            String date, Integer page) throws ParseException;
}
