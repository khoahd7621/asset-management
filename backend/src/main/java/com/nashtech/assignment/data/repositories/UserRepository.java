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
    +" where " 
    +" u.location = :location" 
    +" and (u.type = :type or :type is null)"
    +" and u.isDeleted = 'false'"
    +" and ((lower(concat(u.firstName,' ', u.lastName))  like lower(:name) or :name is null)"
        +" or (lower(concat(u.lastName,' ', u.firstName))  like lower(:name) or :name is null)"
        +" or (lower(u.staffCode) like lower(:code) or :code is null))"
    +" order by u.firstName asc")
    Page<User> search(@Param("name") String name, @Param("code") String staffCode, @Param("location") String location, @Param("type")EUserType type, Pageable pageable);
    
    @Query(value = "select * from user_tbl as u where u.user_name ~ :username", nativeQuery = true)
    public List<User> findAllByUsernameMatchRegex(String username);

    public Optional<User> findByUsernameAndIsDeletedFalse(String username);
}
