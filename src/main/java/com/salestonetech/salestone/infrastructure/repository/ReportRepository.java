package com.salestonetech.salestone.infrastructure.repository;

import com.salestonetech.salestone.model.Report;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ReportRepository extends JpaRepository<Report, String> {
}
