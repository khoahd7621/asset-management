package com.nashtech.assignment.data.repositories;

import com.nashtech.assignment.data.entities.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {
    Optional<Category> findByName(String name);

    @Query(value = "SELECT c FROM Category as c WHERE c.prefixAssetCode = :prefixAssetCode")
    Optional<Category> findByPrefixAssetCode(@Param("prefixAssetCode") String prefixAssetCode);
}
