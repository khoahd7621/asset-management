package com.nashtech.assignment.data.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.nashtech.assignment.data.constants.EAssignStatus;
import com.nashtech.assignment.data.entities.AssignAsset;
import com.nashtech.assignment.data.entities.User;

public interface AssignAssetRepository extends JpaRepository<AssignAsset, Long> {
    Boolean existsByUserAssignedToAndIsDeletedFalse(User user);

    Boolean existsByUserAssignedByAndIsDeletedFalse(User user);

    Boolean existsByAssetIdAndStatusAndIsDeletedFalse(Long assetId, EAssignStatus status);

    @Query(value = "select AS1"
            + " from AssignAsset AS1"
            + " where"
            + " AS1.userAssignedBy.location = :location"
            + " and (coalesce(:statuses, null) is null or AS1.status in :statuses)"
            + " and (:date ='' or to_char(cast(AS1.assignedDate as date),'dd/mm/yyyy') = :date)"
            + " and((:name is null or lower(AS1.asset.assetCode) like lower(concat('%',:name,'%'))"
            + " or lower(AS1.asset.name) like lower(concat('%', :name, '%')) "
            + " or lower(AS1.userAssignedTo.username) like lower(concat('%',:name,'%')) ) ) "
            + " and AS1.isDeleted = false"
            + " order by AS1.userAssignedTo.username asc")
    Page<AssignAsset> searchByNameOrStatusOrDateAndLocation(@Param("name") String name,
            @Param("statuses") List<EAssignStatus> status, @Param("date") String date,
            @Param("location") String location, Pageable pageable);

    Optional<AssignAsset> findByIdAndIsDeletedFalse(long id);

    @Query(value = "select * from assign_asset_tbl as a where a.assigned_to_user_id = :userId and a.is_deleted = :isDeleted and a.status != :status", nativeQuery = true)
    public List<AssignAsset> findAllAssignAssetByUser(@Param("userId") Long userId,
            @Param("isDeleted") Boolean isDeleted, @Param("status") String status);

    @Query(value = "select * from assign_asset_tbl as a where a.id = :id and a.assigned_to_user_id = :userId", nativeQuery = true)
    Optional<AssignAsset> findByIdAndUser(@Param("id") Long id, @Param("userId") Long userId);
}
