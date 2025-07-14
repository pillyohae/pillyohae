package com.example.main.chat.service;

import com.example.common.chat.entity.ChatRoom;
import com.example.common.chat.entity.ChatParticipant;
import com.example.common.chat.entity.ChatLog;
import com.example.common.chat.entity.type.ChatRoomType;
import com.example.common.chat.entity.type.MessageType;
import com.example.common.user.entity.User;
import com.example.main.chat.repository.ChatRoomRepository;
import com.example.main.chat.repository.ChatParticipantRepository;
import com.example.main.chat.repository.ChatLogRepository;
import com.example.main.chat.dto.ChatRoomCreateRequestDto;
import com.example.main.chat.dto.ChatRoomResponseDto;
import com.example.main.chat.dto.ChatMessageSendRequestDto;
import com.example.main.chat.dto.ChatMessageResponseDto;
import com.example.main.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class ChatService {
    
    private final ChatRoomRepository chatRoomRepository;
    private final ChatParticipantRepository chatParticipantRepository;
    private final ChatLogRepository chatLogRepository;
    private final UserService userService;
    
    public ChatRoomResponseDto createChatRoom(ChatRoomCreateRequestDto requestDto, Long userId) {
        User user = userService.findById(userId);
        
        ChatRoom chatRoom = new ChatRoom(requestDto.getName(), requestDto.getType(), requestDto.getDescription());
        ChatRoom savedChatRoom = chatRoomRepository.save(chatRoom);
        
        // 채팅방 생성자를 참여자로 추가
        ChatParticipant participant = new ChatParticipant(user, savedChatRoom);
        chatParticipantRepository.save(participant);
        
        return new ChatRoomResponseDto(savedChatRoom);
    }
    
    public void joinChatRoom(Long chatRoomId, Long userId) {
        ChatRoom chatRoom = chatRoomRepository.findById(chatRoomId)
            .orElseThrow(() -> new IllegalArgumentException("채팅방을 찾을 수 없습니다."));
        User user = userService.findById(userId);
        
        // 이미 참여하고 있는지 확인
        if (chatParticipantRepository.existsByChatRoomIdAndUserIdAndIsActiveTrue(chatRoomId, userId)) {
            throw new IllegalArgumentException("이미 참여하고 있는 채팅방입니다.");
        }
        
        ChatParticipant participant = new ChatParticipant(user, chatRoom);
        chatParticipantRepository.save(participant);
        
        // 시스템 메시지 추가
        ChatLog systemMessage = new ChatLog(chatRoom, user, 
            user.getName() + "님이 입장했습니다.", MessageType.SYSTEM);
        chatLogRepository.save(systemMessage);
    }
    
    public void leaveChatRoom(Long chatRoomId, Long userId) {
        ChatParticipant participant = chatParticipantRepository.findByChatRoomIdAndUserId(chatRoomId, userId)
            .orElseThrow(() -> new IllegalArgumentException("참여하지 않은 채팅방입니다."));
        
        participant.leaveChatRoom();
        
        // 시스템 메시지 추가
        ChatLog systemMessage = new ChatLog(participant.getChatRoom(), participant.getUser(), 
            participant.getUser().getName() + "님이 퇴장했습니다.", MessageType.SYSTEM);
        chatLogRepository.save(systemMessage);
    }
    
    public ChatMessageResponseDto sendMessage(ChatMessageSendRequestDto requestDto, Long senderId) {
        ChatRoom chatRoom = chatRoomRepository.findById(requestDto.getChatRoomId())
            .orElseThrow(() -> new IllegalArgumentException("채팅방을 찾을 수 없습니다."));
        User sender = userService.findById(senderId);
        
        // 채팅방 참여자인지 확인
        if (!chatParticipantRepository.existsByChatRoomIdAndUserIdAndIsActiveTrue(requestDto.getChatRoomId(), senderId)) {
            throw new IllegalArgumentException("채팅방에 참여하지 않은 사용자입니다.");
        }
        
        ChatLog chatLog = new ChatLog(chatRoom, sender, requestDto.getMessage(), 
            requestDto.getMessageType(), requestDto.getFileUrl());
        ChatLog savedChatLog = chatLogRepository.save(chatLog);
        
        return new ChatMessageResponseDto(savedChatLog);
    }
    
    @Transactional(readOnly = true)
    public List<ChatRoomResponseDto> getUserChatRooms(Long userId) {
        List<ChatRoom> chatRooms = chatRoomRepository.findActiveRoomsByUserId(userId);
        
        return chatRooms.stream()
            .map(chatRoom -> {
                Long unreadCount = chatLogRepository.countUnreadMessagesByChatRoomId(chatRoom.getId());
                return new ChatRoomResponseDto(chatRoom, unreadCount);
            })
            .collect(Collectors.toList());
    }
    
    @Transactional(readOnly = true)
    public Page<ChatMessageResponseDto> getChatMessages(Long chatRoomId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<ChatLog> chatLogs = chatLogRepository.findByChatRoomIdOrderByCreatedAtDesc(chatRoomId, pageable);
        
        return chatLogs.map(ChatMessageResponseDto::new);
    }
    
    @Transactional(readOnly = true)
    public List<ChatMessageResponseDto> searchMessages(Long chatRoomId, String keyword) {
        List<ChatLog> chatLogs = chatLogRepository.searchMessagesByKeyword(chatRoomId, keyword);
        
        return chatLogs.stream()
            .map(ChatMessageResponseDto::new)
            .collect(Collectors.toList());
    }
}