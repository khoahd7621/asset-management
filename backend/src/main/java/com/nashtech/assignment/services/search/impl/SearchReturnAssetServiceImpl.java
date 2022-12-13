package com.nashtech.assignment.services.search.impl;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.nashtech.assignment.data.constants.Constants;
import com.nashtech.assignment.data.constants.EReturnStatus;
import com.nashtech.assignment.data.entities.ReturnAsset;
import com.nashtech.assignment.data.entities.User;
import com.nashtech.assignment.data.repositories.ReturnAssetRepository;
import com.nashtech.assignment.dto.response.PaginationResponse;
import com.nashtech.assignment.dto.response.returned.ReturnAssetResponse;
import com.nashtech.assignment.mappers.ReturnAssetMapper;
import com.nashtech.assignment.services.auth.SecurityContextService;
import com.nashtech.assignment.services.search.SearchReturnAssetService;

import lombok.Builder;

@Service
@Builder
public class SearchReturnAssetServiceImpl implements SearchReturnAssetService {
        @Autowired
        private SecurityContextService securityContextService;
        @Autowired
        private ReturnAssetRepository returnAssetRepository;
        @Autowired
        private ReturnAssetMapper returnAssetMapper;

        @Override
        public PaginationResponse<List<ReturnAssetResponse>> searchAllReturnAssetsByStateAndReturnedDateAndSearchWithPagination(
                        String query, List<EReturnStatus> status, String date, Integer page) throws ParseException {
                User user = securityContextService.getCurrentUser();
                date = date == null ? "" : date;
                if (!date.equals("")) {
                        SimpleDateFormat formatterDate = new SimpleDateFormat(Constants.DATE_FORMAT);
                        formatterDate.setLenient(false);
                        formatterDate.parse(date);
                }
                Pageable pageable = PageRequest.of(page, 19);
                Page<ReturnAsset> returnAssetsPage = returnAssetRepository
                                .searchAllReturnAssetsByStateAndReturnedDateAndSearchWithPagination(
                                                query, status, date,
                                                user.getLocation(), pageable);
                if (returnAssetsPage == null || returnAssetsPage.isEmpty()) {
                        return PaginationResponse.<List<ReturnAssetResponse>>builder()
                                        .data(Collections.emptyList()).build();
                }
                List<ReturnAssetResponse> result = returnAssetMapper
                                .mapListEntityReturnAssetResponses(returnAssetsPage.getContent());
                return PaginationResponse.<List<ReturnAssetResponse>>builder().data(result)
                                .totalPage(returnAssetsPage.getTotalPages())
                                .totalRow(returnAssetsPage.getTotalElements()).build();
        }

}