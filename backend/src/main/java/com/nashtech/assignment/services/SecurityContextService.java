package com.nashtech.assignment.services;

import com.nashtech.assignment.data.entities.User;

public interface SecurityContextService {
    public void setSecurityContext(String username);

    public User getCurrentUser();
}
