package com.nashtech.assignment.data.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.nashtech.assignment.data.entities.AssignAsset;
import com.nashtech.assignment.data.entities.User;

public interface AssignAssetRepository extends JpaRepository<AssignAsset, Long> {
    Boolean existsByUserAssignedToAndIsDeletedFalse(User user);

    Boolean existsByUserAssignedByAndIsDeletedFalse(User user);
}
