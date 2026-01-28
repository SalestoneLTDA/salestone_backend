package com.salestonetech.salestone.service;

import com.salestonetech.salestone.controller.dto.AdministratorRequestDTO;
import com.salestonetech.salestone.controller.dto.AdministratorResponseDTO;
import com.salestonetech.salestone.infrastructure.repository.AdministratorRepository;
import com.salestonetech.salestone.model.Administrator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AdministratorService {

    private final AdministratorRepository administratorRepository;

    @Transactional
    public AdministratorResponseDTO create(AdministratorRequestDTO request) {
        if (administratorRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new IllegalArgumentException("Email already in use");
        }

        Administrator administrator = Administrator.builder()
                .name(request.getName())
                .email(request.getEmail())
                .build();

        return toResponseDTO(administratorRepository.save(administrator));
    }

    public List<AdministratorResponseDTO> findAll() {
        return administratorRepository.findAll().stream()
                .map(this::toResponseDTO)
                .collect(Collectors.toList());
    }

    public AdministratorResponseDTO findById(UUID id) {
        return administratorRepository.findById(id)
                .map(this::toResponseDTO)
                .orElseThrow(() -> new RuntimeException("Administrator not found"));
    }

    @Transactional
    public AdministratorResponseDTO update(UUID id, AdministratorRequestDTO request) {
        Administrator administrator = administratorRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Administrator not found"));

        if (!administrator.getEmail().equals(request.getEmail()) && 
            administratorRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new IllegalArgumentException("Email already in use");
        }

        administrator.setName(request.getName());
        administrator.setEmail(request.getEmail());

        return toResponseDTO(administratorRepository.save(administrator));
    }

    @Transactional
    public void delete(UUID id) {
        if (!administratorRepository.existsById(id)) {
            throw new RuntimeException("Administrator not found");
        }
        administratorRepository.deleteById(id);
    }

    private AdministratorResponseDTO toResponseDTO(Administrator administrator) {
        return AdministratorResponseDTO.builder()
                .id(administrator.getId())
                .name(administrator.getName())
                .email(administrator.getEmail())
                .createdAt(administrator.getCreatedAt())
                .updatedAt(administrator.getUpdatedAt())
                .build();
    }
}
