package com.salestonetech.salestone.service;

import com.salestonetech.salestone.controller.dto.MessageCountResponseDTO;
import com.salestonetech.salestone.controller.dto.MessageRequestDTO;
import com.salestonetech.salestone.controller.dto.MessageResponseDTO;
import com.salestonetech.salestone.infrastructure.repository.ConversationRepository;
import com.salestonetech.salestone.infrastructure.repository.MessageRepository;
import com.salestonetech.salestone.infrastructure.repository.projection.DateCountProjection;
import com.salestonetech.salestone.model.Conversation;
import com.salestonetech.salestone.model.ConversationStatus;
import com.salestonetech.salestone.model.Message;
import com.salestonetech.salestone.model.MessageSenderType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

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
    
    @Transactional(readOnly = true)
    public List<MessageCountResponseDTO> getMessageCountsByDate(LocalDate startDate, LocalDate endDate, String salespersonId) {
        LocalDateTime start = (startDate != null) ? startDate.atStartOfDay() : LocalDateTime.now().minusDays(30);
        LocalDateTime end = (endDate != null) ? endDate.atTime(23, 59, 59) : LocalDateTime.now();

        log.info("Fetching message counts from {} to {} for salesperson: {}", start, end, salespersonId);

        List<DateCountProjection> projections;
        if (salespersonId != null && !salespersonId.isEmpty()) {
            projections = messageRepository.countMessagesByDateAndSalesperson(start, end, salespersonId);
        } else {
            projections = messageRepository.countMessagesByDate(start, end);
        }

        return projections.stream()
                .map(p -> new MessageCountResponseDTO(p.getDate(), p.getCount()))
                .collect(Collectors.toList());
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
