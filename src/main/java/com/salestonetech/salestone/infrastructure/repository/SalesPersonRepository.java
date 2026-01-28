package com.salestonetech.salestone.infrastructure.repository;

import com.salestonetech.salestone.model.SalesPerson;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface SalesPersonRepository extends JpaRepository<SalesPerson, UUID> {
    Optional<SalesPerson> findByEmail(String email);
}
