package com.nashtech.assignment.dto.request.asset;

import com.nashtech.assignment.data.constants.EAssetStatus;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Builder
public class SearchAssetRequest {
    private String keyword;
    private List<EAssetStatus> statuses;
    private List<Integer> categoryIds;
    private Integer limit;
    private Integer page;
    private String sortField;
    private String sortType;
}
