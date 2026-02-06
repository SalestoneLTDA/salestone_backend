package com.salestonetech.salestone.controller;

import com.salestonetech.salestone.controller.dto.SalesPersonRequestDTO;
import com.salestonetech.salestone.controller.dto.SalesPersonResponseDTO;
import com.salestonetech.salestone.service.SalesPersonService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/salespeople")
@RequiredArgsConstructor
public class SalesPersonController {

    private final SalesPersonService salesPersonService;

    @PostMapping
    public ResponseEntity<SalesPersonResponseDTO> create(@Valid @RequestBody SalesPersonRequestDTO request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(salesPersonService.create(request));
    }

    @GetMapping
    public ResponseEntity<List<SalesPersonResponseDTO>> findAll() {
        return ResponseEntity.ok(salesPersonService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<SalesPersonResponseDTO> findById(@PathVariable UUID id) {
        return ResponseEntity.ok(salesPersonService.findById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<SalesPersonResponseDTO> update(@PathVariable UUID id, @Valid @RequestBody SalesPersonRequestDTO request) {
        return ResponseEntity.ok(salesPersonService.update(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        salesPersonService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
