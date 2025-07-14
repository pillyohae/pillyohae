package com.example.common.chat.entity;

import com.example.common.global.entity.BaseTimeEntity;
import com.example.common.chat.entity.type.ChatRoomType;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor
@Table(name = "chat_rooms")
public class ChatRoom extends BaseTimeEntity {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false, length = 100)
    private String name;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ChatRoomType type;
    
    @Column(length = 500)
    private String description;
    
    @OneToMany(mappedBy = "chatRoom", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<ChatParticipant> participants = new ArrayList<>();
    
    @OneToMany(mappedBy = "chatRoom", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<ChatLog> chatLogs = new ArrayList<>();
    
    public ChatRoom(String name, ChatRoomType type, String description) {
        this.name = name;
        this.type = type;
        this.description = description;
    }
    
    public void addParticipant(ChatParticipant participant) {
        this.participants.add(participant);
        participant.setChatRoom(this);
    }
    
    public void addChatLog(ChatLog chatLog) {
        this.chatLogs.add(chatLog);
        chatLog.setChatRoom(this);
    }
}