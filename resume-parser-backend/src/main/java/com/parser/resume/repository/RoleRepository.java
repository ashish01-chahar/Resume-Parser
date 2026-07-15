package com.parser.resume.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.parser.resume.model.Role;

public interface RoleRepository extends JpaRepository<Role, Long> {
    Optional<Role> findByName(String name);
}
