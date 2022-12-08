package com.nashtech.assignment.data.repositories;

import com.nashtech.assignment.data.constants.EAssetStatus;
import com.nashtech.assignment.data.entities.Asset;
import com.nashtech.assignment.dto.response.report.AssetReportResponseInterface;
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

    @Query(value = "select * from asset_tbl as a where a.category_id = :categoryId", nativeQuery = true)
    List<Asset> findAssetsByCategoryId(@Param("categoryId") int categoryId);

    @Query("select sa.category.name as name,count(sa) as count," +
            " sum(case when sa.status = com.nashtech.assignment.data.constants.EAssetStatus.ASSIGNED then 1 else 0 end) as assigned," +
            " sum(case when sa.status = com.nashtech.assignment.data.constants.EAssetStatus.AVAILABLE then 1 else 0 end) as available," +
            " sum(case when sa.status = com.nashtech.assignment.data.constants.EAssetStatus.NOT_AVAILABLE then 1 else 0 end) as notAvailable," +
            " sum(case when sa.status = com.nashtech.assignment.data.constants.EAssetStatus.WAITING_FOR_RECYCLING then 1 else 0 end) as waitingForRecycling," +
            " sum(case when sa.status = com.nashtech.assignment.data.constants.EAssetStatus.RECYCLED then 1 else 0 end) as recycling" +
            " from Asset sa" +
            " left join sa.category" +
            " where sa.isDeleted = false" +
            " group by sa.category.name")
    List<AssetReportResponseInterface> getAssetReport();
}
