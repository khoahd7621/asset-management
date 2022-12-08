package com.nashtech.assignment.services.search;

import java.text.ParseException;
import java.util.List;

import com.nashtech.assignment.data.constants.EReturnStatus;
import com.nashtech.assignment.dto.response.PaginationResponse;
import com.nashtech.assignment.dto.response.returned.ReturnAssetResponse;

public interface SearchReturnAssetService {
    PaginationResponse<List<ReturnAssetResponse>> searchAllReturnAssetsByStateAndReturnedDateAndSearchWithPagination(
            String query, List<EReturnStatus> status, String date, Integer page) throws ParseException;
}
