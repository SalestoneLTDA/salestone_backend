package com.salestonetech.salestone.service;

import com.salestonetech.salestone.controller.dto.SalesPersonRequestDTO;
import com.salestonetech.salestone.controller.dto.SalesPersonResponseDTO;
import com.salestonetech.salestone.infrastructure.repository.SalesPersonRepository;
import com.salestonetech.salestone.model.SalesPerson;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SalesPersonService {

    private final SalesPersonRepository salesPersonRepository;

    @Transactional
    public SalesPersonResponseDTO create(SalesPersonRequestDTO request) {
        if (salesPersonRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new IllegalArgumentException("Email already in use");
        }

        SalesPerson salesPerson = SalesPerson.builder()
                .name(request.getName())
                .email(request.getEmail())
                .phone(request.getPhone())
                .build();

        return toResponseDTO(salesPersonRepository.save(salesPerson));
    }

    public List<SalesPersonResponseDTO> findAll() {
        return salesPersonRepository.findAll().stream()
                .map(this::toResponseDTO)
                .collect(Collectors.toList());
    }

    public SalesPersonResponseDTO findById(UUID id) {
        return salesPersonRepository.findById(id)
                .map(this::toResponseDTO)
                .orElseThrow(() -> new RuntimeException("SalesPerson not found"));
    }

    @Transactional
    public SalesPersonResponseDTO update(UUID id, SalesPersonRequestDTO request) {
        SalesPerson salesPerson = salesPersonRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("SalesPerson not found"));

        if (!salesPerson.getEmail().equals(request.getEmail()) && 
            salesPersonRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new IllegalArgumentException("Email already in use");
        }

        salesPerson.setName(request.getName());
        salesPerson.setEmail(request.getEmail());
        salesPerson.setPhone(request.getPhone());

        return toResponseDTO(salesPersonRepository.save(salesPerson));
    }

    @Transactional
    public void delete(UUID id) {
        if (!salesPersonRepository.existsById(id)) {
            throw new RuntimeException("SalesPerson not found");
        }
        salesPersonRepository.deleteById(id);
    }

    private SalesPersonResponseDTO toResponseDTO(SalesPerson salesPerson) {
        return SalesPersonResponseDTO.builder()
                .id(salesPerson.getId())
                .name(salesPerson.getName())
                .email(salesPerson.getEmail())
                .phone(salesPerson.getPhone())
                .createdAt(salesPerson.getCreatedAt())
                .updatedAt(salesPerson.getUpdatedAt())
                .build();
    }
}
