package com.nashtech.assignment.data.repositories;

import com.nashtech.assignment.data.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    @Query(value = "select * from user_tbl as u where u.user_name ~ :username", nativeQuery = true)
    public List<User> findAllByUsernameMatchRegex(String username);

    public Optional<User> findByUsernameAndIsDeletedFalse(String username);
}