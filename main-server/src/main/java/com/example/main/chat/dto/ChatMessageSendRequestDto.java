package com.example.main.chat.dto;

import com.example.common.chat.entity.type.MessageType;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ChatMessageSendRequestDto {
    private Long chatRoomId;
    private String message;
    private MessageType messageType;
    private String fileUrl;
}