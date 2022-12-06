package com.nashtech.assignment.data.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.nashtech.assignment.data.entities.Category;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {
    public Optional<Category> findByName(String name);

    @Query(value = "SELECT c FROM Category as c WHERE c.prefixAssetCode = :prefixAssetCode")
    public Optional<Category> findByPrefixAssetCode(@Param("prefixAssetCode") String prefixAssetCode);
}
