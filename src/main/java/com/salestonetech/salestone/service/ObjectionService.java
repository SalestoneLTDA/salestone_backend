package com.salestonetech.salestone.service;

import com.salestonetech.salestone.exception.ResourceNotFoundException;
import com.salestonetech.salestone.infrastructure.repository.ObjectionRepository;
import com.salestonetech.salestone.infrastructure.repository.SalesPersonRepository;
import com.salestonetech.salestone.model.Objection;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class ObjectionService {

    private final ObjectionRepository objectionRepository;
    private final SalesPersonRepository salesPersonRepository;

    /**
     * Retrieve all objections associated with a specific salesperson.
     *
     * @param salespersonId The ID of the salesperson.
     * @return List of objections.
     */
    @Transactional(readOnly = true)
    public List<Objection> getObjectionsBySalespersonId(String salespersonId) {
        log.info("Fetching objections for salesperson ID: {}", salespersonId);
        
        UUID uuid;
        try {
            uuid = UUID.fromString(salespersonId);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid salesperson ID format: " + salespersonId);
        }

        if (!salesPersonRepository.existsById(uuid)) {
            throw new ResourceNotFoundException("Salesperson not found with ID: " + salespersonId);
        }

        return objectionRepository.findBySalespersonId(salespersonId);
    }
}
