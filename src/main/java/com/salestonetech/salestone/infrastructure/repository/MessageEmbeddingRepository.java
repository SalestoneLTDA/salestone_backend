package com.salestonetech.salestone.infrastructure.repository;

import com.salestonetech.salestone.model.MessageEmbedding;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface MessageEmbeddingRepository extends JpaRepository<MessageEmbedding, UUID> {

    // Mantém a busca via relacionamento (útil para integridade)
    List<MessageEmbedding> findByMessageConversationId(UUID conversationId);

    // Busca direta pelo ID da mensagem
    MessageEmbedding findByMessageId(UUID messageId);

    /**
     * IMPORTANTE: Se você optar por usar o VectorStore do Spring AI para buscas complexas,
     * este repositório servirá principalmente para a manutenção (CRUD) dos vetores.
     */
}