package com.nashtech.assignment.services.delete;

public interface DeleteUserService {
    void deleteUser(String staffCode);

    boolean checkValidUser(String staffCode);
}
