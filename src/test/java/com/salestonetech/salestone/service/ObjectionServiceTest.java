package com.salestonetech.salestone.service;

import com.salestonetech.salestone.exception.ResourceNotFoundException;
import com.salestonetech.salestone.infrastructure.repository.ObjectionRepository;
import com.salestonetech.salestone.infrastructure.repository.SalesPersonRepository;
import com.salestonetech.salestone.model.Conversation;
import com.salestonetech.salestone.model.Objection;
import com.salestonetech.salestone.model.SalesPerson;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ObjectionServiceTest {

    @Mock
    private ObjectionRepository objectionRepository;

    @Mock
    private SalesPersonRepository salesPersonRepository;

    @InjectMocks
    private ObjectionService objectionService;

    @Test
    void getObjectionsBySalespersonId_ShouldReturnObjections_WhenSalespersonExists() {
        // Arrange
        String salespersonId = UUID.randomUUID().toString();
        UUID salespersonUuid = UUID.fromString(salespersonId);
        
        when(salesPersonRepository.existsById(salespersonUuid)).thenReturn(true);
        
        Objection objection = new Objection();
        objection.setId(UUID.randomUUID());
        objection.setDescription("Price too high");
        
        when(objectionRepository.findBySalespersonId(salespersonId)).thenReturn(List.of(objection));

        // Act
        List<Objection> result = objectionService.getObjectionsBySalespersonId(salespersonId);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Price too high", result.get(0).getDescription());
        verify(salesPersonRepository).existsById(salespersonUuid);
        verify(objectionRepository).findBySalespersonId(salespersonId);
    }

    @Test
    void getObjectionsBySalespersonId_ShouldThrowException_WhenSalespersonNotFound() {
        // Arrange
        String salespersonId = UUID.randomUUID().toString();
        UUID salespersonUuid = UUID.fromString(salespersonId);
        
        when(salesPersonRepository.existsById(salespersonUuid)).thenReturn(false);

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> objectionService.getObjectionsBySalespersonId(salespersonId));
        
        verify(salesPersonRepository).existsById(salespersonUuid);
        verify(objectionRepository, never()).findBySalespersonId(anyString());
    }


    @Test
    void getObjectionsBySalespersonId_ShouldThrowException_WhenSalespersonIdInvalid() {
        // Arrange
        String invalidId = "invalid-uuid";

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> objectionService.getObjectionsBySalespersonId(invalidId));
        
        verify(salesPersonRepository, never()).existsById(any());
        verify(objectionRepository, never()).findBySalespersonId(anyString());
    }
}
