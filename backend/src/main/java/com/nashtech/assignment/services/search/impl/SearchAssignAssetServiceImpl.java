package com.nashtech.assignment.services.search.impl;

import com.nashtech.assignment.data.constants.Constants;
import com.nashtech.assignment.data.constants.EAssignStatus;
import com.nashtech.assignment.data.entities.AssignAsset;
import com.nashtech.assignment.data.entities.User;
import com.nashtech.assignment.data.repositories.AssignAssetRepository;
import com.nashtech.assignment.dto.response.PaginationResponse;
import com.nashtech.assignment.dto.response.assignment.AssignAssetResponse;
import com.nashtech.assignment.mappers.AssignAssetMapper;
import com.nashtech.assignment.services.auth.SecurityContextService;
import com.nashtech.assignment.services.search.SearchAssignAssetService;
import lombok.Builder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.List;

@Service
@Builder
public class SearchAssignAssetServiceImpl implements SearchAssignAssetService {
    @Autowired
    private AssignAssetRepository assignAssetRepository;
    @Autowired
    private AssignAssetMapper assignAssetMapper;
    @Autowired
    private SecurityContextService securityContextService;

    @Override
    public PaginationResponse<List<AssignAssetResponse>> filterAndSearchAssignAsset(
            String name, List<EAssignStatus> status, String date, Integer page)
            throws ParseException {
        User user = securityContextService.getCurrentUser();
        date = date == null ? "" : date;
        if (!date.equals("")) {
            SimpleDateFormat formatterDate = new SimpleDateFormat(Constants.DATE_FORMAT);
            formatterDate.setLenient(false);
            formatterDate.parse(date);
        }
        Pageable pageable = PageRequest.of(page, 19);
        Page<AssignAsset> assignAsset = assignAssetRepository
                .searchByNameOrStatusOrDateAndLocation(name, status, date,
                        user.getLocation(), pageable);
        if (assignAsset == null || assignAsset.isEmpty()) {
            return PaginationResponse.<List<AssignAssetResponse>>builder()
                    .data(Collections.emptyList()).build();
        }
        List<AssignAssetResponse> result = assignAssetMapper.toListAssignAssetResponses(assignAsset.getContent());
        return PaginationResponse.<List<AssignAssetResponse>>builder().data(result)
                .totalPage(assignAsset.getTotalPages())
                .totalRow(assignAsset.getTotalElements()).build();
    }

}
