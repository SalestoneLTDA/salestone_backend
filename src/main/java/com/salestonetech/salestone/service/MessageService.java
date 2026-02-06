package com.salestonetech.salestone.service;

import com.salestonetech.salestone.controller.dto.MessageRequestDTO;
import com.salestonetech.salestone.controller.dto.MessageResponseDTO;
import com.salestonetech.salestone.infrastructure.repository.ConversationRepository;
import com.salestonetech.salestone.infrastructure.repository.MessageRepository;
import com.salestonetech.salestone.model.Conversation;
import com.salestonetech.salestone.model.ConversationStatus;
import com.salestonetech.salestone.model.Message;
import com.salestonetech.salestone.model.MessageSenderType;
import lombok.RequiredArgsConstructor;
     import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
public class MessageService {

    private final ConversationRepository conversationRepository;
    private final MessageRepository messageRepository;

    @Transactional
    public MessageResponseDTO processMessage(MessageRequestDTO request, MessageSenderType senderType) {
        // Find or create conversation
        Conversation conversation = conversationRepository
                .findBySalespersonIdAndCustomerPhone(request.getSalespersonId(), request.getCustomerPhone())
                .orElseGet(() -> createConversation(request.getSalespersonId(), request.getCustomerPhone()));

        // Create message
        Message message = Message.builder()
                .conversation(conversation)
                .content(request.getContent())
                .senderType(senderType)
                .timestamp(request.getTimestamp() != null ? request.getTimestamp() : LocalDateTime.now())
                .build();

        Message savedMessage = messageRepository.save(message);
        
        log.info("Processed {} message for conversation ID: {}", senderType, conversation.getId());

        return toResponseDTO(savedMessage);
    }

    private Conversation createConversation(String salespersonId, String customerPhone) {
        log.info("Creating new conversation for Salesperson: {} and Customer: {}", salespersonId, customerPhone);
        Conversation conversation = Conversation.builder()
                .salespersonId(salespersonId)
                .customerPhone(customerPhone)
                .status(ConversationStatus.ACTIVE)
                .build();
        return conversationRepository.save(conversation);
    }

    private MessageResponseDTO toResponseDTO(Message message) {
        return MessageResponseDTO.builder()
                .id(message.getId())
                .conversationId(message.getConversation().getId())
                .content(message.getContent())
                .senderType(message.getSenderType())
                .timestamp(message.getTimestamp())
                .build();
    }
}
