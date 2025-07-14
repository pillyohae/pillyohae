package com.example.common.chat.entity;

import com.example.common.global.entity.BaseTimeEntity;
import com.example.common.user.entity.User;
import com.example.common.chat.entity.type.MessageType;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "chat_logs")
public class ChatLog extends BaseTimeEntity {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "chat_room_id", nullable = false)
    private ChatRoom chatRoom;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sender_id", nullable = false)
    private User sender;
    
    @Column(nullable = false, columnDefinition = "TEXT")
    private String message;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private MessageType messageType;
    
    @Column(length = 500)
    private String fileUrl;
    
    @Column(nullable = false)
    private Boolean isRead = false;
    
    @Column(nullable = false)
    private Boolean isEdited = false;
    
    public ChatLog(ChatRoom chatRoom, User sender, String message, MessageType messageType) {
        this.chatRoom = chatRoom;
        this.sender = sender;
        this.message = message;
        this.messageType = messageType;
        this.isRead = false;
        this.isEdited = false;
    }
    
    public ChatLog(ChatRoom chatRoom, User sender, String message, MessageType messageType, String fileUrl) {
        this(chatRoom, sender, message, messageType);
        this.fileUrl = fileUrl;
    }
    
    public void markAsRead() {
        this.isRead = true;
    }
    
    public void editMessage(String newMessage) {
        this.message = newMessage;
        this.isEdited = true;
    }
}