package com.example.main.chat.example;

import com.example.common.chat.entity.type.ChatRoomType;
import com.example.common.chat.entity.type.MessageType;
import com.example.main.chat.dto.ChatRoomCreateRequestDto;
import com.example.main.chat.dto.ChatRoomResponseDto;
import com.example.main.chat.dto.ChatMessageSendRequestDto;
import com.example.main.chat.dto.ChatMessageResponseDto;
import com.example.main.chat.service.ChatService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 채팅 시스템 사용 예제
 * 이 클래스는 채팅 시스템을 어떻게 사용하는지 보여주는 예제입니다.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class ChatSystemExample {

    private final ChatService chatService;

    /**
     * 채팅방 생성 및 메시지 전송 예제
     */
    public void demonstrateChatSystem() {
        try {
            // 1. 채팅방 생성
            ChatRoomCreateRequestDto createRequest = new ChatRoomCreateRequestDto();
            createRequest.setName("상품 문의 채팅방");
            createRequest.setType(ChatRoomType.SUPPORT);
            createRequest.setDescription("고객 지원을 위한 채팅방입니다.");

            ChatRoomResponseDto chatRoom = chatService.createChatRoom(createRequest, 1L);
            log.info("채팅방이 생성되었습니다: {}", chatRoom.getName());

            // 2. 다른 사용자 채팅방 참여
            chatService.joinChatRoom(chatRoom.getId(), 2L);
            log.info("사용자 2가 채팅방에 참여했습니다.");

            // 3. 메시지 전송
            ChatMessageSendRequestDto messageRequest = new ChatMessageSendRequestDto();
            messageRequest.setChatRoomId(chatRoom.getId());
            messageRequest.setMessage("안녕하세요! 상품에 대해 문의드리고 싶습니다.");
            messageRequest.setMessageType(MessageType.TEXT);

            ChatMessageResponseDto sentMessage = chatService.sendMessage(messageRequest, 1L);
            log.info("메시지가 전송되었습니다: {}", sentMessage.getMessage());

            // 4. 상담사 답변
            ChatMessageSendRequestDto replyRequest = new ChatMessageSendRequestDto();
            replyRequest.setChatRoomId(chatRoom.getId());
            replyRequest.setMessage("안녕하세요! 어떤 상품에 대해 문의하시나요?");
            replyRequest.setMessageType(MessageType.TEXT);

            ChatMessageResponseDto replyMessage = chatService.sendMessage(replyRequest, 2L);
            log.info("답변이 전송되었습니다: {}", replyMessage.getMessage());

            // 5. 채팅 기록 조회
            Page<ChatMessageResponseDto> messages = chatService.getChatMessages(chatRoom.getId(), 0, 10);
            log.info("채팅 기록 조회 완료. 총 {} 개 메시지", messages.getTotalElements());

            // 6. 메시지 검색
            List<ChatMessageResponseDto> searchResults = chatService.searchMessages(chatRoom.getId(), "상품");
            log.info("'상품' 키워드로 검색된 메시지: {} 개", searchResults.size());

            // 7. 사용자 채팅방 목록 조회
            List<ChatRoomResponseDto> userChatRooms = chatService.getUserChatRooms(1L);
            log.info("사용자 1의 채팅방 목록: {} 개", userChatRooms.size());

        } catch (Exception e) {
            log.error("채팅 시스템 예제 실행 중 오류 발생", e);
        }
    }

    /**
     * 그룹 채팅 예제
     */
    public void demonstrateGroupChat() {
        try {
            // 1. 그룹 채팅방 생성
            ChatRoomCreateRequestDto createRequest = new ChatRoomCreateRequestDto();
            createRequest.setName("영양제 추천 그룹");
            createRequest.setType(ChatRoomType.GROUP);
            createRequest.setDescription("영양제 추천 및 정보 공유 그룹입니다.");

            ChatRoomResponseDto groupChatRoom = chatService.createChatRoom(createRequest, 1L);
            log.info("그룹 채팅방이 생성되었습니다: {}", groupChatRoom.getName());

            // 2. 여러 사용자 참여
            chatService.joinChatRoom(groupChatRoom.getId(), 2L);
            chatService.joinChatRoom(groupChatRoom.getId(), 3L);
            chatService.joinChatRoom(groupChatRoom.getId(), 4L);
            log.info("여러 사용자가 그룹 채팅방에 참여했습니다.");

            // 3. 그룹 메시지 전송
            ChatMessageSendRequestDto groupMessage = new ChatMessageSendRequestDto();
            groupMessage.setChatRoomId(groupChatRoom.getId());
            groupMessage.setMessage("안녕하세요! 비타민 D 추천 부탁드립니다.");
            groupMessage.setMessageType(MessageType.TEXT);

            chatService.sendMessage(groupMessage, 2L);
            log.info("그룹 메시지가 전송되었습니다.");

            // 4. 이미지 메시지 전송 예제
            ChatMessageSendRequestDto imageMessage = new ChatMessageSendRequestDto();
            imageMessage.setChatRoomId(groupChatRoom.getId());
            imageMessage.setMessage("이 제품 어떠세요?");
            imageMessage.setMessageType(MessageType.IMAGE);
            imageMessage.setFileUrl("https://example.com/product-image.jpg");

            chatService.sendMessage(imageMessage, 3L);
            log.info("이미지 메시지가 전송되었습니다.");

        } catch (Exception e) {
            log.error("그룹 채팅 예제 실행 중 오류 발생", e);
        }
    }

    /**
     * 채팅방 퇴장 예제
     */
    public void demonstrateLeavingChatRoom() {
        try {
            // 사용자가 채팅방을 떠나는 예제
            chatService.leaveChatRoom(1L, 2L);
            log.info("사용자 2가 채팅방을 떠났습니다.");

        } catch (Exception e) {
            log.error("채팅방 퇴장 예제 실행 중 오류 발생", e);
        }
    }
}