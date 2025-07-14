package com.example.main.chat.controller;

import com.example.common.chat.entity.type.ChatRoomType;
import com.example.common.chat.entity.type.MessageType;
import com.example.main.chat.dto.ChatRoomCreateRequestDto;
import com.example.main.chat.dto.ChatRoomResponseDto;
import com.example.main.chat.dto.ChatMessageSendRequestDto;
import com.example.main.chat.dto.ChatMessageResponseDto;
import com.example.main.chat.service.ChatService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ChatController.class)
class ChatControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ChatService chatService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void 채팅방_생성_API_테스트() throws Exception {
        // Given
        ChatRoomCreateRequestDto requestDto = new ChatRoomCreateRequestDto();
        requestDto.setName("테스트 채팅방");
        requestDto.setType(ChatRoomType.PRIVATE);
        requestDto.setDescription("테스트용 채팅방");

        ChatRoomResponseDto responseDto = new ChatRoomResponseDto();
        responseDto.setId(1L);
        responseDto.setName("테스트 채팅방");
        responseDto.setType(ChatRoomType.PRIVATE);
        responseDto.setDescription("테스트용 채팅방");

        when(chatService.createChatRoom(any(ChatRoomCreateRequestDto.class), anyLong()))
                .thenReturn(responseDto);

        // When & Then
        mockMvc.perform(post("/api/chat/rooms")
                .header("User-Id", "1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("테스트 채팅방"))
                .andExpect(jsonPath("$.type").value("PRIVATE"))
                .andExpect(jsonPath("$.description").value("테스트용 채팅방"));
    }

    @Test
    void 메시지_전송_API_테스트() throws Exception {
        // Given
        ChatMessageSendRequestDto requestDto = new ChatMessageSendRequestDto();
        requestDto.setChatRoomId(1L);
        requestDto.setMessage("안녕하세요!");
        requestDto.setMessageType(MessageType.TEXT);

        ChatMessageResponseDto responseDto = new ChatMessageResponseDto();
        responseDto.setId(1L);
        responseDto.setChatRoomId(1L);
        responseDto.setSenderId(1L);
        responseDto.setSenderName("테스트유저");
        responseDto.setMessage("안녕하세요!");
        responseDto.setMessageType(MessageType.TEXT);

        when(chatService.sendMessage(any(ChatMessageSendRequestDto.class), anyLong()))
                .thenReturn(responseDto);

        // When & Then
        mockMvc.perform(post("/api/chat/messages")
                .header("User-Id", "1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.chatRoomId").value(1))
                .andExpect(jsonPath("$.senderId").value(1))
                .andExpect(jsonPath("$.senderName").value("테스트유저"))
                .andExpect(jsonPath("$.message").value("안녕하세요!"))
                .andExpect(jsonPath("$.messageType").value("TEXT"));
    }

    @Test
    void 채팅방_참여_API_테스트() throws Exception {
        // When & Then
        mockMvc.perform(post("/api/chat/rooms/1/join")
                .header("User-Id", "1"))
                .andExpect(status().isOk());
    }

    @Test
    void 채팅방_퇴장_API_테스트() throws Exception {
        // When & Then
        mockMvc.perform(post("/api/chat/rooms/1/leave")
                .header("User-Id", "1"))
                .andExpect(status().isOk());
    }

    @Test
    void 사용자_채팅방_목록_조회_API_테스트() throws Exception {
        // Given
        ChatRoomResponseDto responseDto = new ChatRoomResponseDto();
        responseDto.setId(1L);
        responseDto.setName("테스트 채팅방");
        responseDto.setType(ChatRoomType.PRIVATE);
        responseDto.setUnreadMessageCount(5L);

        when(chatService.getUserChatRooms(anyLong()))
                .thenReturn(List.of(responseDto));

        // When & Then
        mockMvc.perform(get("/api/chat/rooms")
                .header("User-Id", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].name").value("테스트 채팅방"))
                .andExpect(jsonPath("$[0].type").value("PRIVATE"))
                .andExpect(jsonPath("$[0].unreadMessageCount").value(5));
    }

    @Test
    void 채팅_메시지_조회_API_테스트() throws Exception {
        // Given
        ChatMessageResponseDto messageDto = new ChatMessageResponseDto();
        messageDto.setId(1L);
        messageDto.setChatRoomId(1L);
        messageDto.setSenderId(1L);
        messageDto.setSenderName("테스트유저");
        messageDto.setMessage("테스트 메시지");
        messageDto.setMessageType(MessageType.TEXT);

        Page<ChatMessageResponseDto> messagePage = new PageImpl<>(List.of(messageDto));

        when(chatService.getChatMessages(anyLong(), anyInt(), anyInt()))
                .thenReturn(messagePage);

        // When & Then
        mockMvc.perform(get("/api/chat/rooms/1/messages")
                .param("page", "0")
                .param("size", "20"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].id").value(1))
                .andExpect(jsonPath("$.content[0].chatRoomId").value(1))
                .andExpect(jsonPath("$.content[0].senderId").value(1))
                .andExpect(jsonPath("$.content[0].senderName").value("테스트유저"))
                .andExpect(jsonPath("$.content[0].message").value("테스트 메시지"))
                .andExpect(jsonPath("$.content[0].messageType").value("TEXT"));
    }

    @Test
    void 메시지_검색_API_테스트() throws Exception {
        // Given
        ChatMessageResponseDto messageDto = new ChatMessageResponseDto();
        messageDto.setId(1L);
        messageDto.setChatRoomId(1L);
        messageDto.setSenderId(1L);
        messageDto.setSenderName("테스트유저");
        messageDto.setMessage("상품에 대한 문의입니다");
        messageDto.setMessageType(MessageType.TEXT);

        when(chatService.searchMessages(anyLong(), anyString()))
                .thenReturn(List.of(messageDto));

        // When & Then
        mockMvc.perform(get("/api/chat/rooms/1/search")
                .param("keyword", "상품"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].chatRoomId").value(1))
                .andExpect(jsonPath("$[0].senderId").value(1))
                .andExpect(jsonPath("$[0].senderName").value("테스트유저"))
                .andExpect(jsonPath("$[0].message").value("상품에 대한 문의입니다"))
                .andExpect(jsonPath("$[0].messageType").value("TEXT"));
    }
}