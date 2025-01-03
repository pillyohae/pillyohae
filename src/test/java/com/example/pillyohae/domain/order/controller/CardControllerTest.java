package com.example.pillyohae.domain.order.controller;

import com.example.pillyohae.global.dto.Authentication;
import com.example.pillyohae.global.entity.Role;
import com.example.pillyohae.global.exception.code.SuccessCode;
import com.example.pillyohae.global.response.CommonResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.time.LocalDateTime;

import static com.example.pillyohae.global.constants.GlobalConstants.USER_AUTH;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(CardController.class)
class CardControllerTest {
    @Autowired
    MockMvc mockMvc;
    @MockitoBean
    CardService cardService;
    @InjectMocks
    CardController cardController;
    @Autowired
    private ObjectMapper objectMapper;

    MockHttpSession session = new MockHttpSession();
    Long userId = 1L;
    @BeforeEach
    void setUp() {
        session.setAttribute(USER_AUTH, new Authentication(userId,Role.USER));
    }

    @Test
    void createCard() throws Exception {
        Long cardId = 1L;
        Long workspaceId = 1L;
        Long processListId = 1L;
        Long boardId = 1L;
        String title = "title";
        String content = "content";
        LocalDateTime dueDate = LocalDateTime.of(2025,1,1, 0, 0, 0);
        CreateCardRequestDto requestDto = new CreateCardRequestDto(title, content, dueDate);
        CreateCardResponseDto responseDto = new CreateCardResponseDto(cardId, title,content,dueDate);
        ResponseEntity<CommonResponse<CreateCardResponseDto>> responseEntity = CommonResponse.success(SuccessCode.SUCCESS_INSERT, responseDto);

        when(cardService.createCard(any(CreateCardRequestDto.class),any(Long.class),any(Long.class),any(Long.class),any(Long.class))).thenReturn(responseDto);
        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/workspaces/1/boards/1/lists/1/cards")
                        .session(session)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(content().json(objectMapper.writeValueAsString(responseEntity.getBody())));

        verify(cardService, times(1))
                .createCard(any(CreateCardRequestDto.class), eq(userId), eq(workspaceId) ,eq(boardId), eq(processListId));

    }

    @Test
    void switchProcessList() throws Exception {
        Long processListId = 1L;
        SwitchProcessListResponseDto responseDto = new SwitchProcessListResponseDto(processListId);
        ResponseEntity<CommonResponse<SwitchProcessListResponseDto>> responseEntity = CommonResponse.success(SuccessCode.SUCCESS_UPDATE, responseDto);
        Long cardId = 1L;
        Long workspaceId = 1L;

        when(cardService.switchProcessList(cardId,processListId,workspaceId,userId)).thenReturn(responseDto);

        mockMvc.perform(MockMvcRequestBuilders.patch("/api/v1/workspaces/1/lists/1/cards/1")
                        .session(session)
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("processListId", String.valueOf(processListId)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(responseEntity.getBody())));

        verify(cardService, times(1))
                .switchProcessList(cardId, processListId, workspaceId, userId);
    }

    @Test
    void updateCard() throws Exception {
        Long cardId = 1L;
        Long workspaceId = 1L;
        Long processListId = 1L;
        String title = "title";
        String content = "content";
        LocalDateTime dueDate = LocalDateTime.of(2025,1,1, 0, 0, 0);
        UpdateCardRequestDto requestDto = new UpdateCardRequestDto(title, content, dueDate);
        UpdateCardResponseDto responseDto = new UpdateCardResponseDto(title,content,dueDate);
        ResponseEntity<CommonResponse<UpdateCardResponseDto>> responseEntity = CommonResponse.success(SuccessCode.SUCCESS_UPDATE, responseDto);

        when(cardService.updateCard(any(UpdateCardRequestDto.class),any(Long.class),any(Long.class),any(Long.class))).thenReturn(responseDto);
        mockMvc.perform(MockMvcRequestBuilders.put("/api/v1/workspaces/1/lists/1/cards/1")
                        .session(session)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                        .andDo(print())
                        .andExpect(status().isOk())
                        .andExpect(content().json(objectMapper.writeValueAsString(responseEntity.getBody())));

        verify(cardService, times(1))
                .updateCard(any(UpdateCardRequestDto.class), eq(1L), eq(1L), eq(1L));

    }

    @Test
    void deleteCardSuccess() throws Exception {
        Long cardId = 1L;
        DeleteCardResponseDto responseDto = new DeleteCardResponseDto(cardId);
        ResponseEntity<CommonResponse<DeleteCardResponseDto>> responseEntity =
                CommonResponse.success(SuccessCode.SUCCESS_DELETE, responseDto);
        when(cardService.deleteCard(any(Long.class),any(Long.class),any(Long.class))).thenReturn(responseDto);
        mockMvc.perform(MockMvcRequestBuilders.delete("/api/v1/workspaces/1/lists/1/cards/1")
                        .session(session)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(responseEntity.getBody())));
        verify(cardService, times(1))
                .deleteCard(eq(1L), eq(1L), eq(1L));
    }

//    @Test
//    void  findCardListSuccess() throws Exception {
//        Long cardId = 1L;
//        String cardTitle = "title";
//        String cardContent = "content";
//        Long userId = 1L;
//        String userName = "userName";
//        Long commentCount = 1L;
//        LocalDateTime dueDate = LocalDateTime.of(2025,1,1, 0, 0, 0);
//        Long pageNumber = 1L;
//        Long pageSize = 10L;
//        List<CardBriefInfo> cardBriefInfoList = new ArrayList<>();
//        CardBriefInfo cardBriefInfo = new CardBriefInfo(cardId,cardTitle,dueDate,userId,userName,commentCount);
//        FindCardListRequestDto requestDto = new FindCardListRequestDto(cardTitle,cardContent,1L,dueDate,"name",pageNumber,pageSize);
//        FindCardListResponseDto responseDto = new FindCardListResponseDto(cardBriefInfoList, new FindCardListResponseDto.pageInfo(1L,pageNumber,pageSize));
//        ResponseEntity<CommonResponse<FindCardListResponseDto>> responseEntity = CommonResponse.success(SuccessCode.SUCCESS, responseDto);
//        when(cardService.findCardList(any(Long.class),any(FindCardListRequestDto.class),any(Long.class))).thenReturn(responseDto);
//
//        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/workspaces/1/boards/1/lists/1/cards/1")
//                        .session(session)
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(objectMapper.writeValueAsString(requestDto)))
//                .andDo(print())
//                .andExpect(status().isOk())
//                .andExpect(content().json(objectMapper.writeValueAsString(responseEntity.getBody())));
//
//        verify(cardService, times(1))
//                .findCardList(eq(userId), any(FindCardListRequestDto.class),eq(1L));
//    }
}