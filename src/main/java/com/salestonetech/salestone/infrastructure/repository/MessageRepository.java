package com.salestonetech.salestone.infrastructure.repository;

import com.salestonetech.salestone.model.Message;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Repository
public interface MessageRepository extends JpaRepository<Message, UUID> {

    // Já estava correto, apenas confirmando a importância do OrderBy para o RAG
    List<Message> findByConversationIdOrderByTimestampAsc(UUID conversationId);

    List<Message> findByTimestampBetweenOrderByTimestampAsc(LocalDateTime start, LocalDateTime end);

    /**
     * Útil para o Job de sábado caso queira processar apenas conversas
     * que tiveram mensagens novas na última semana.
     */
    @Query("SELECT DISTINCT m.conversation.id FROM Message m WHERE m.timestamp >= :since")
    List<UUID> findConversationIdsWithRecentMessages(@Param("since") LocalDateTime since);
}