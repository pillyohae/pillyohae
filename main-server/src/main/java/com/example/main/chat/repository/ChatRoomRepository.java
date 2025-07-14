package com.example.main.chat.repository;

import com.example.common.chat.entity.ChatRoom;
import com.example.common.chat.entity.type.ChatRoomType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ChatRoomRepository extends JpaRepository<ChatRoom, Long> {
    
    List<ChatRoom> findByType(ChatRoomType type);
    
    @Query("SELECT cr FROM ChatRoom cr JOIN cr.participants cp WHERE cp.user.id = :userId AND cp.isActive = true")
    List<ChatRoom> findActiveRoomsByUserId(@Param("userId") Long userId);
    
    @Query("SELECT cr FROM ChatRoom cr WHERE cr.name LIKE %:name%")
    List<ChatRoom> findByNameContaining(@Param("name") String name);
    
    Optional<ChatRoom> findByIdAndDeletedAtIsNull(Long id);
}