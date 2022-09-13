package com.example.dxc.security.repository;

import com.example.dxc.security.entities.Privilege;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.Optional;

public interface PrivilegeRepository extends JpaRepository<Privilege,Long> {
    Optional<Privilege> findByName(String name);
}
