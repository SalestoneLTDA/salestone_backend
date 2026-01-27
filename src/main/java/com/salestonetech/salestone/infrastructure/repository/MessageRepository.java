package com.salestonetech.salestone.infrastructure.repository;

import com.salestonetech.salestone.model.Message;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;
import java.util.List;

@Repository
public interface MessageRepository extends JpaRepository<Message, UUID> {
    List<Message> findByConversationIdOrderByTimestampAsc(UUID conversationId);
}
