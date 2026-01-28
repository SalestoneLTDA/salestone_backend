package com.salestonetech.salestone.service;

import com.salestonetech.salestone.controller.dto.SalesPersonRequestDTO;
import com.salestonetech.salestone.controller.dto.SalesPersonResponseDTO;
import com.salestonetech.salestone.infrastructure.repository.SalesPersonRepository;
import com.salestonetech.salestone.model.SalesPerson;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SalesPersonServiceTest {

    @Mock
    private SalesPersonRepository salesPersonRepository;

    @InjectMocks
    private SalesPersonService salesPersonService;

    @Test
    void create_ShouldCreateSalesPerson_WhenEmailIsUnique() {
        SalesPersonRequestDTO request = new SalesPersonRequestDTO();
        request.setName("John Doe");
        request.setEmail("john@example.com");
        request.setPhone("1234567890");

        when(salesPersonRepository.findByEmail(request.getEmail())).thenReturn(Optional.empty());
        
        when(salesPersonRepository.save(any(SalesPerson.class))).thenAnswer(invocation -> {
            SalesPerson sp = invocation.getArgument(0);
            sp.setId(UUID.randomUUID());
            return sp;
        });

        SalesPersonResponseDTO response = salesPersonService.create(request);

        assertNotNull(response);
        assertEquals(request.getName(), response.getName());
        verify(salesPersonRepository).save(any(SalesPerson.class));
    }

    @Test
    void create_ShouldThrowException_WhenEmailExists() {
        SalesPersonRequestDTO request = new SalesPersonRequestDTO();
        request.setName("John Doe");
        request.setEmail("john@example.com");

        when(salesPersonRepository.findByEmail(request.getEmail())).thenReturn(Optional.of(new SalesPerson()));

        assertThrows(IllegalArgumentException.class, () -> salesPersonService.create(request));
        verify(salesPersonRepository, never()).save(any(SalesPerson.class));
    }
}
