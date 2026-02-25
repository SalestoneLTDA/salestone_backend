package com.salestonetech.salestone.infrastructure.repository;

import com.salestonetech.salestone.infrastructure.repository.projection.DateCountProjection;
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

    List<Message> findByConversationIdAndTimestampBetweenOrderByTimestampAsc(UUID conversationId, LocalDateTime start, LocalDateTime end);
    
    List<Message> findByTimestampBetweenOrderByTimestampAsc(LocalDateTime start, LocalDateTime end);

    @Query(value = "SELECT CAST(timestamp AS DATE) as date, COUNT(*) as count " +
                   "FROM messages " +
                   "WHERE timestamp BETWEEN :start AND :end " +
                   "GROUP BY CAST(timestamp AS DATE) " +
                   "ORDER BY date", nativeQuery = true)
    List<DateCountProjection> countMessagesByDate(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end);

    @Query(value = "SELECT CAST(m.timestamp AS DATE) as date, COUNT(m.id) as count " +
                   "FROM messages m " +
                   "JOIN conversations c ON m.conversation_id = c.id " +
                   "WHERE m.timestamp BETWEEN :start AND :end " +
                   "AND c.salesperson_id = :salespersonId " +
                   "GROUP BY CAST(m.timestamp AS DATE) " +
                   "ORDER BY date", nativeQuery = true)
    List<DateCountProjection> countMessagesByDateAndSalesperson(@Param("start") LocalDateTime start, 
                                                                @Param("end") LocalDateTime end, 
                                                                @Param("salespersonId") String salespersonId);

    /**
     * Útil para o Job de sábado caso queira processar apenas conversas
     * que tiveram mensagens novas na última semana.
     */
    @Query("SELECT DISTINCT m.conversation.id FROM Message m WHERE m.timestamp >= :since")
    List<UUID> findConversationIdsWithRecentMessages(@Param("since") LocalDateTime since);
}
