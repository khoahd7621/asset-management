package com.nashtech.assignment.data.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.nashtech.assignment.data.constants.EUserType;
import com.nashtech.assignment.data.entities.User;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    User findByStaffCode(String staffCode);

    Optional<User> findByUsername(String username);

    Page<User> findByLocationAndIsDeletedFalseOrderByFirstNameAsc(String location, Pageable pageable);

    Page<User> findByLocationAndTypeAndIsDeletedFalseOrderByFirstNameAsc(String location, EUserType type, Pageable pageable);

    @Query("SELECT u"
    +" FROM User u "
    +" WHERE "
    +" u.location = :location" 
    +" AND (u.type = :type OR :type IS NULL)"
    +" AND u.isDeleted = FALSE"
    +" AND ((lower(concat(u.firstName,' ', u.lastName))  LIKE lower(:name) OR :name IS NULL)"
        +" OR (lower(concat(u.lastName,' ', u.firstName))  LIKE lower(:name) OR :name IS NULL)"
        +" OR (lower(u.staffCode) LIKE lower(:code) OR :code IS NULL))"
    +" ORDER BY u.firstName ASC")
    Page<User> search(@Param("name") String name, @Param("code") String staffCode, @Param("location") String location, @Param("type")EUserType type, Pageable pageable);
    
    @Query(value = "select * from user_tbl as u where u.user_name ~ :username", nativeQuery = true)
    List<User> findAllByUsernameMatchRegex(String username);

    Optional<User> findByUsernameAndIsDeletedFalse(String username);

    List<User> findAllByLocationAndIsDeletedFalse(String location);

    Optional<User> findByIdAndIsDeletedFalse(Long id);
}
