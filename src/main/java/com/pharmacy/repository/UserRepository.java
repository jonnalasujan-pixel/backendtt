package com.pharmacy.repository;

import com.pharmacy.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);
    Optional<User> findByEmail(String email);
    java.util.List<User> findAllByRole(String role);
    boolean existsByUsername(String username);
    boolean existsByEmail(String email);
    long countByRole(String role);
}
