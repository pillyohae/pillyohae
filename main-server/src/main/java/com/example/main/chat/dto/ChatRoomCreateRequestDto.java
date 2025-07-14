package com.example.main.chat.dto;

import com.example.common.chat.entity.type.ChatRoomType;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ChatRoomCreateRequestDto {
    private String name;
    private ChatRoomType type;
    private String description;
}