package com.salestonetech.salestone.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "conversations", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"salesperson_id", "customer_phone"})
})
public class Conversation {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "salesperson_id", nullable = false)
    private String salespersonId;

    @Column(name = "customer_phone", nullable = false)
    private String customerPhone;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private ConversationStatus status = ConversationStatus.ACTIVE;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;
    
    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @OneToMany(mappedBy = "conversation", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<Message> messages = new ArrayList<>();
}
