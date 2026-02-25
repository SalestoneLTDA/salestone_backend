package com.salestonetech.salestone.infrastructure.repository;

import com.salestonetech.salestone.model.Objection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ObjectionRepository extends JpaRepository<Objection, UUID> {

    /**
     * Busca todas as objeções detectadas em uma conversa específica.
     */
    List<Objection> findByConversationId(UUID conversationId);

    /**
     * Busca todas as objeções associadas a um vendedor específico através de suas conversas.
     */
    @Query("SELECT o FROM Objection o JOIN o.conversation c WHERE c.salespersonId = :salespersonId")
    List<Objection> findBySalespersonId(@Param("salespersonId") String salespersonId);
}
