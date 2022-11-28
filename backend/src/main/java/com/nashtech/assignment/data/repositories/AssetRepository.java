package com.nashtech.assignment.data.repositories;

import com.nashtech.assignment.data.constants.EAssetStatus;
import com.nashtech.assignment.data.entities.Asset;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AssetRepository extends JpaRepository<Asset, Long> {
    @Query("SELECT a FROM Asset a " +
            "WHERE (coalesce(:query, NULL) IS NULL OR " +
            "(lower(a.assetCode) LIKE lower(concat('%', :query ,'%')) " +
            "OR lower(a.name) LIKE lower(concat('%', :query ,'%')))) " +
            "AND (coalesce(:statuses, NULL) IS NULL OR a.status IN :statuses) " +
            "AND (coalesce(:categoryIds, NULL) IS NULL OR a.category.id IN :categoryIds) " +
            "AND a.location = :location AND a.isDeleted = FALSE")
    Page<Asset> findAllAssetsByQueryAndStatusesAndCategoryIds(
                @Param("query") String query,
                @Param("statuses") List<EAssetStatus> statuses,
                @Param("categoryIds") List<Integer> categoryIds,
                @Param("location") String location,
                Pageable pageable);

    Optional<Asset> findByIdAndIsDeletedFalse(long id);
}
