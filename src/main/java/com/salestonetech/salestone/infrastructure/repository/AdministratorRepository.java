package com.salestonetech.salestone.infrastructure.repository;

import com.salestonetech.salestone.model.Administrator;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface AdministratorRepository extends JpaRepository<Administrator, UUID> {
    Optional<Administrator> findByEmail(String email);
}
