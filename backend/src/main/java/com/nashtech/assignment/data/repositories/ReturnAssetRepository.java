package com.nashtech.assignment.data.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.nashtech.assignment.data.entities.ReturnAsset;

@Repository
public interface ReturnAssetRepository extends JpaRepository<ReturnAsset, Long> {
    @Query(value = "SELECT r FROM ReturnAsset as r where r.id = :id")
    Optional<ReturnAsset> findByAssignAssetId(Long id);
}
