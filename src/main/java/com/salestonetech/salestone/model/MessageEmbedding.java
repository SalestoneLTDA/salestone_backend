package com.salestonetech.salestone.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "message_embeddings")
public class MessageEmbedding {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @OneToOne
    @JoinColumn(name = "message_id", nullable = false)
    private Message message;

    @Column(name = "embedding", columnDefinition = "vector(768)")
    private String embedding;  // Armazenado como string no formato '[1.0,2.0,...]' (pgvector lida com isso)

    // Metadata opcional para busca rápida
    @Column(name = "sender_type")
    private MessageSenderType senderType;

    @Column(name = "timestamp")
    private LocalDateTime timestamp;
}