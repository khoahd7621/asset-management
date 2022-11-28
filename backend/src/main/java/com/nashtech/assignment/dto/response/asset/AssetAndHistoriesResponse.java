package com.nashtech.assignment.dto.response.asset;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Builder
public class AssetAndHistoriesResponse {
    private AssetResponse asset;
    List<AssetHistory> histories;
}
