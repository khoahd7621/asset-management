package com.nashtech.assignment.data.repositories;

import com.nashtech.assignment.data.constants.EReturnStatus;
import com.nashtech.assignment.data.entities.ReturnAsset;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ReturnAssetRepository extends JpaRepository<ReturnAsset, Long> {
    @Query(value = "SELECT r FROM ReturnAsset as r where r.assignAsset.id = :id")
    Optional<ReturnAsset> findByAssignAssetId(Long id);

    @Query(value = "select RA1 from ReturnAsset RA1"
            + " where RA1.asset.location = :location"
            + " and (coalesce(:statuses, null) is null or RA1.status in :statuses)"
            + " and (:date = '' or to_char(cast(RA1.returnedDate as date),'dd/mm/yyyy') = :date)"
            + " and ((:query is null or lower(RA1.asset.assetCode) like lower(concat('%', :query, '%'))"
            + " or lower(RA1.asset.name) like lower(concat('%', :query, '%')) "
            + " or lower(RA1.userRequestedReturn.username) like lower(concat('%', :query, '%')))) "
            + " order by RA1.asset.assetCode asc")
    Page<ReturnAsset> searchAllReturnAssetsByStateAndReturnedDateAndSearchWithPagination(
            @Param("query") String query,
            @Param("statuses") List<EReturnStatus> status,
            @Param("date") String date,
            @Param("location") String location,
            Pageable pageable);
}
