package com.example.main.chat.repository;

import com.example.common.chat.entity.ChatLog;
import com.example.common.chat.entity.type.MessageType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ChatLogRepository extends JpaRepository<ChatLog, Long> {
    
    Page<ChatLog> findByChatRoomIdOrderByCreatedAtDesc(Long chatRoomId, Pageable pageable);
    
    @Query("SELECT cl FROM ChatLog cl WHERE cl.chatRoom.id = :chatRoomId AND cl.createdAt >= :since ORDER BY cl.createdAt DESC")
    List<ChatLog> findByChatRoomIdAndCreatedAtAfter(@Param("chatRoomId") Long chatRoomId, 
                                                   @Param("since") LocalDateTime since);
    
    @Query("SELECT cl FROM ChatLog cl WHERE cl.chatRoom.id = :chatRoomId AND cl.sender.id = :senderId ORDER BY cl.createdAt DESC")
    List<ChatLog> findByChatRoomIdAndSenderId(@Param("chatRoomId") Long chatRoomId, 
                                             @Param("senderId") Long senderId);
    
    @Query("SELECT cl FROM ChatLog cl WHERE cl.chatRoom.id = :chatRoomId AND cl.messageType = :messageType ORDER BY cl.createdAt DESC")
    List<ChatLog> findByChatRoomIdAndMessageType(@Param("chatRoomId") Long chatRoomId, 
                                                @Param("messageType") MessageType messageType);
    
    @Query("SELECT COUNT(cl) FROM ChatLog cl WHERE cl.chatRoom.id = :chatRoomId AND cl.isRead = false")
    Long countUnreadMessagesByChatRoomId(@Param("chatRoomId") Long chatRoomId);
    
    @Query("SELECT cl FROM ChatLog cl WHERE cl.message LIKE %:keyword% AND cl.chatRoom.id = :chatRoomId ORDER BY cl.createdAt DESC")
    List<ChatLog> searchMessagesByKeyword(@Param("chatRoomId") Long chatRoomId, 
                                         @Param("keyword") String keyword);
}