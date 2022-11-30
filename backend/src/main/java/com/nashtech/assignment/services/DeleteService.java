package com.nashtech.assignment.services;

public interface DeleteService {
    void deleteUser(String staffCode);

    boolean checkValidUser(String staffCode);

    void deleteAssetByAssetId(Long assetId);
}
