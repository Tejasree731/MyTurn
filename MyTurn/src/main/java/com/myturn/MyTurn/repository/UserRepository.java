package com.myturn.MyTurn.repository;
import com.myturn.MyTurn.model.User;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);
    Optional<User> findByEmail(String email);

    //avoid duplicates
    @Query("SELECT u.id id FROM User u WHERE u.username = :username")
    boolean existsByUsername(String username);
    boolean existsByEmail(String email);
}
