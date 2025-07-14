package com.example.main.chat.dto;

import com.example.common.chat.entity.ChatLog;
import com.example.common.chat.entity.type.MessageType;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
public class ChatMessageResponseDto {
    private Long id;
    private Long chatRoomId;
    private Long senderId;
    private String senderName;
    private String message;
    private MessageType messageType;
    private String fileUrl;
    private Boolean isRead;
    private Boolean isEdited;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    public ChatMessageResponseDto(ChatLog chatLog) {
        this.id = chatLog.getId();
        this.chatRoomId = chatLog.getChatRoom().getId();
        this.senderId = chatLog.getSender().getId();
        this.senderName = chatLog.getSender().getName();
        this.message = chatLog.getMessage();
        this.messageType = chatLog.getMessageType();
        this.fileUrl = chatLog.getFileUrl();
        this.isRead = chatLog.getIsRead();
        this.isEdited = chatLog.getIsEdited();
        this.createdAt = chatLog.getCreatedAt();
        this.updatedAt = chatLog.getUpdatedAt();
    }
}