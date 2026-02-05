package com.salestonetech.salestone.infrastructure.repository;

import com.salestonetech.salestone.model.Message;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Repository
public interface MessageRepository extends JpaRepository<Message, UUID> {
    List<Message> findByConversationIdOrderByTimestampAsc(UUID conversationId);
    
    List<Message> findByTimestampBetweenOrderByTimestampAsc(LocalDateTime start, LocalDateTime end);
}
