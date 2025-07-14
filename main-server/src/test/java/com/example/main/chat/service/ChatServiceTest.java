package com.example.main.chat.service;

import com.example.common.chat.entity.ChatRoom;
import com.example.common.chat.entity.ChatLog;
import com.example.common.chat.entity.ChatParticipant;
import com.example.common.chat.entity.type.ChatRoomType;
import com.example.common.chat.entity.type.MessageType;
import com.example.common.user.entity.User;
import com.example.common.user.entity.type.Role;
import com.example.common.user.entity.type.Status;
import com.example.main.chat.repository.ChatRoomRepository;
import com.example.main.chat.repository.ChatLogRepository;
import com.example.main.chat.repository.ChatParticipantRepository;
import com.example.main.chat.dto.ChatRoomCreateRequestDto;
import com.example.main.chat.dto.ChatRoomResponseDto;
import com.example.main.chat.dto.ChatMessageSendRequestDto;
import com.example.main.chat.dto.ChatMessageResponseDto;
import com.example.main.user.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ChatServiceTest {

    @Mock
    private ChatRoomRepository chatRoomRepository;

    @Mock
    private ChatLogRepository chatLogRepository;

    @Mock
    private ChatParticipantRepository chatParticipantRepository;

    @Mock
    private UserService userService;

    @InjectMocks
    private ChatService chatService;

    private User testUser;
    private ChatRoom testChatRoom;

    @BeforeEach
    void setUp() {
        testUser = new User("테스트유저", "test@example.com", "password", null, Role.CUSTOMER);
        testChatRoom = new ChatRoom("테스트 채팅방", ChatRoomType.PRIVATE, "테스트용 채팅방입니다.");
    }

    @Test
    void 채팅방_생성_성공() {
        // Given
        ChatRoomCreateRequestDto requestDto = new ChatRoomCreateRequestDto();
        requestDto.setName("테스트 채팅방");
        requestDto.setType(ChatRoomType.PRIVATE);
        requestDto.setDescription("테스트용 채팅방입니다.");

        when(userService.findById(anyLong())).thenReturn(testUser);
        when(chatRoomRepository.save(any(ChatRoom.class))).thenReturn(testChatRoom);
        when(chatParticipantRepository.save(any(ChatParticipant.class))).thenReturn(new ChatParticipant());

        // When
        ChatRoomResponseDto response = chatService.createChatRoom(requestDto, 1L);

        // Then
        assertNotNull(response);
        assertEquals("테스트 채팅방", response.getName());
        assertEquals(ChatRoomType.PRIVATE, response.getType());
        assertEquals("테스트용 채팅방입니다.", response.getDescription());
        
        verify(chatRoomRepository).save(any(ChatRoom.class));
        verify(chatParticipantRepository).save(any(ChatParticipant.class));
    }

    @Test
    void 메시지_전송_성공() {
        // Given
        ChatMessageSendRequestDto requestDto = new ChatMessageSendRequestDto();
        requestDto.setChatRoomId(1L);
        requestDto.setMessage("안녕하세요!");
        requestDto.setMessageType(MessageType.TEXT);

        ChatLog testChatLog = new ChatLog(testChatRoom, testUser, "안녕하세요!", MessageType.TEXT);

        when(chatRoomRepository.findById(anyLong())).thenReturn(Optional.of(testChatRoom));
        when(userService.findById(anyLong())).thenReturn(testUser);
        when(chatParticipantRepository.existsByChatRoomIdAndUserIdAndIsActiveTrue(anyLong(), anyLong())).thenReturn(true);
        when(chatLogRepository.save(any(ChatLog.class))).thenReturn(testChatLog);

        // When
        ChatMessageResponseDto response = chatService.sendMessage(requestDto, 1L);

        // Then
        assertNotNull(response);
        assertEquals("안녕하세요!", response.getMessage());
        assertEquals(MessageType.TEXT, response.getMessageType());
        assertEquals(testUser.getName(), response.getSenderName());
        
        verify(chatLogRepository).save(any(ChatLog.class));
    }

    @Test
    void 채팅_메시지_조회_성공() {
        // Given
        ChatLog chatLog = new ChatLog(testChatRoom, testUser, "테스트 메시지", MessageType.TEXT);
        List<ChatLog> chatLogs = List.of(chatLog);
        Page<ChatLog> chatLogPage = new PageImpl<>(chatLogs);

        when(chatLogRepository.findByChatRoomIdOrderByCreatedAtDesc(anyLong(), any(PageRequest.class)))
                .thenReturn(chatLogPage);

        // When
        Page<ChatMessageResponseDto> response = chatService.getChatMessages(1L, 0, 20);

        // Then
        assertNotNull(response);
        assertEquals(1, response.getContent().size());
        assertEquals("테스트 메시지", response.getContent().get(0).getMessage());
        
        verify(chatLogRepository).findByChatRoomIdOrderByCreatedAtDesc(anyLong(), any(PageRequest.class));
    }
}