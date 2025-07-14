package com.example.main.chat.dto;

import com.example.common.chat.entity.ChatRoom;
import com.example.common.chat.entity.type.ChatRoomType;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
public class ChatRoomResponseDto {
    private Long id;
    private String name;
    private ChatRoomType type;
    private String description;
    private Long participantCount;
    private Long unreadMessageCount;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    public ChatRoomResponseDto(ChatRoom chatRoom) {
        this.id = chatRoom.getId();
        this.name = chatRoom.getName();
        this.type = chatRoom.getType();
        this.description = chatRoom.getDescription();
        this.participantCount = (long) chatRoom.getParticipants().size();
        this.createdAt = chatRoom.getCreatedAt();
        this.updatedAt = chatRoom.getUpdatedAt();
    }
    
    public ChatRoomResponseDto(ChatRoom chatRoom, Long unreadMessageCount) {
        this(chatRoom);
        this.unreadMessageCount = unreadMessageCount;
    }
}