package com.salestonetech.salestone.controller;

import com.salestonetech.salestone.controller.dto.AdministratorRequestDTO;
import com.salestonetech.salestone.controller.dto.AdministratorResponseDTO;
import com.salestonetech.salestone.service.AdministratorService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/administrators")
@RequiredArgsConstructor
public class AdministratorController {

    private final AdministratorService administratorService = null;

    @PostMapping
    public ResponseEntity<AdministratorResponseDTO> create(@Valid @RequestBody AdministratorRequestDTO request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(administratorService.create(request));
    }

    @GetMapping
    public ResponseEntity<List<AdministratorResponseDTO>> findAll() {
        return ResponseEntity.ok(administratorService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<AdministratorResponseDTO> findById(@PathVariable UUID id) {
        return ResponseEntity.ok(administratorService.findById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<AdministratorResponseDTO> update(@PathVariable UUID id, @Valid @RequestBody AdministratorRequestDTO request) {
        return ResponseEntity.ok(administratorService.update(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        administratorService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
