package com.example.main.chat.repository;

import com.example.common.chat.entity.ChatParticipant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ChatParticipantRepository extends JpaRepository<ChatParticipant, Long> {
    
    List<ChatParticipant> findByChatRoomIdAndIsActiveTrue(Long chatRoomId);
    
    List<ChatParticipant> findByUserIdAndIsActiveTrue(Long userId);
    
    @Query("SELECT cp FROM ChatParticipant cp WHERE cp.chatRoom.id = :chatRoomId AND cp.user.id = :userId")
    Optional<ChatParticipant> findByChatRoomIdAndUserId(@Param("chatRoomId") Long chatRoomId, 
                                                       @Param("userId") Long userId);
    
    @Query("SELECT COUNT(cp) FROM ChatParticipant cp WHERE cp.chatRoom.id = :chatRoomId AND cp.isActive = true")
    Long countActiveByChatRoomId(@Param("chatRoomId") Long chatRoomId);
    
    boolean existsByChatRoomIdAndUserIdAndIsActiveTrue(Long chatRoomId, Long userId);
}