package com.example.main.chat.controller;

import com.example.main.chat.service.ChatService;
import com.example.main.chat.dto.ChatRoomCreateRequestDto;
import com.example.main.chat.dto.ChatRoomResponseDto;
import com.example.main.chat.dto.ChatMessageSendRequestDto;
import com.example.main.chat.dto.ChatMessageResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/chat")
@RequiredArgsConstructor
public class ChatController {
    
    private final ChatService chatService;
    
    @PostMapping("/rooms")
    public ResponseEntity<ChatRoomResponseDto> createChatRoom(
            @RequestBody ChatRoomCreateRequestDto requestDto,
            @RequestHeader("User-Id") Long userId) {
        ChatRoomResponseDto response = chatService.createChatRoom(requestDto, userId);
        return ResponseEntity.ok(response);
    }
    
    @PostMapping("/rooms/{chatRoomId}/join")
    public ResponseEntity<Void> joinChatRoom(
            @PathVariable Long chatRoomId,
            @RequestHeader("User-Id") Long userId) {
        chatService.joinChatRoom(chatRoomId, userId);
        return ResponseEntity.ok().build();
    }
    
    @PostMapping("/rooms/{chatRoomId}/leave")
    public ResponseEntity<Void> leaveChatRoom(
            @PathVariable Long chatRoomId,
            @RequestHeader("User-Id") Long userId) {
        chatService.leaveChatRoom(chatRoomId, userId);
        return ResponseEntity.ok().build();
    }
    
    @PostMapping("/messages")
    public ResponseEntity<ChatMessageResponseDto> sendMessage(
            @RequestBody ChatMessageSendRequestDto requestDto,
            @RequestHeader("User-Id") Long senderId) {
        ChatMessageResponseDto response = chatService.sendMessage(requestDto, senderId);
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/rooms")
    public ResponseEntity<List<ChatRoomResponseDto>> getUserChatRooms(
            @RequestHeader("User-Id") Long userId) {
        List<ChatRoomResponseDto> response = chatService.getUserChatRooms(userId);
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/rooms/{chatRoomId}/messages")
    public ResponseEntity<Page<ChatMessageResponseDto>> getChatMessages(
            @PathVariable Long chatRoomId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        Page<ChatMessageResponseDto> response = chatService.getChatMessages(chatRoomId, page, size);
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/rooms/{chatRoomId}/search")
    public ResponseEntity<List<ChatMessageResponseDto>> searchMessages(
            @PathVariable Long chatRoomId,
            @RequestParam String keyword) {
        List<ChatMessageResponseDto> response = chatService.searchMessages(chatRoomId, keyword);
        return ResponseEntity.ok(response);
    }
}