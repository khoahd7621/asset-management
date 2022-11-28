package com.nashtech.assignment.dto.response.asset;

import com.nashtech.assignment.data.constants.EAssetStatus;
import com.nashtech.assignment.dto.response.category.CategoryResponse;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
@Builder
public class AssetResponse {
    private long id;
    private String assetName;
    private String assetCode;
    private Date installedDate;
    private String specification;
    private EAssetStatus status;
    private String location;
    private boolean isDeleted;
    private CategoryResponse category;
}
