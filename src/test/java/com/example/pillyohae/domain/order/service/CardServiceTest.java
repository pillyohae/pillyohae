package com.example.pillyohae.domain.order.service;

import com.example.pillyohae.domain.list.entity.ProcessList;
import com.example.pillyohae.domain.list.repository.ProcessListRepository;
import com.example.pillyohae.domain.member.entity.Member;
import com.example.pillyohae.domain.member.repository.MemberRepository;
import com.example.pillyohae.domain.user.entity.User;
import com.example.pillyohae.domain.user.repository.UserRepository;
import com.example.pillyohae.domain.workspace.entity.Workspace;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CardServiceTest {
    @Mock
    CardRepository cardRepository;
    @Mock
    UserRepository userRepository;
    @Mock
    MemberRepository memberRepository;
    @Mock
    ProcessListRepository processListRepository;
    @InjectMocks
    CardService cardService;

    User user;
    Member member;
    ProcessList processList;
    Workspace workspace;
    Card card;
    @BeforeEach
    void setUp() {
        Long cardId = 1L;
        Long memberId = 1L;
        Long processListId = 1L;
        Long userId = 1L;
        Long workspaceId = 1L;
        String title = "title";
        String content = "content";
        LocalDateTime dueDate = LocalDateTime.of(2025,1,1, 0, 0, 0);

        user = User.builder().build();
        workspace = Workspace.builder().build();
        processList = ProcessList.builder().build();
        member = Member.builder().build();

        user = Mockito.spy(user);
        member = Mockito.spy(member);
        processList = Mockito.spy(processList);
        workspace = Mockito.spy(workspace);

        card = Card.builder().title(title).content(content).dueDate(dueDate).user(user)
                .processList(processList).build();

        when(workspace.getId()).thenReturn(1L);
        when(memberRepository.findByUser_IdAndWorkspace_Id(user.getId(),workspace.getId())).thenReturn(Optional.ofNullable(member));




    }

    @Test
    void createCardSuccess() {
        Long boardId = 1L;
        when(userRepository.findById(user.getId())).thenReturn(Optional.ofNullable(user));
        when(processListRepository.findById(processList.getId())).thenReturn(Optional.ofNullable(processList));
        CreateCardRequestDto requestDto = new CreateCardRequestDto(card.getTitle(), card.getContent(), card.getDueDate());
        CreateCardResponseDto responseDto = new CreateCardResponseDto(card.getId(), card.getTitle(),card.getContent(),card.getDueDate());
        assertThat(responseDto.equals(cardService.createCard(requestDto,user.getId(),workspace.getId(),boardId,processList.getId()))).isTrue();
    }

    @Test
    void switchProcessListSuccess() {
        SwitchProcessListResponseDto responseDto = new SwitchProcessListResponseDto(processList.getId());
        when(processListRepository.findById(processList.getId())).thenReturn(Optional.ofNullable(processList));
        card = Card.builder().processList(processList).build();
        ProcessList formerProcessList = ProcessList.builder().build();
        formerProcessList.getCards().add(card);

        when(cardRepository.findById(card.getId())).thenReturn(Optional.of(card));

        assertThat(responseDto.equals(cardService.switchProcessList(card.getId(),processList.getId(),workspace.getId(),user.getId()))).isTrue();
    }

    @Test
    void updateCardSuccess() {
        UpdateCardRequestDto requestDto = new UpdateCardRequestDto(card.getTitle(), card.getContent(), card.getDueDate());
        UpdateCardResponseDto responseDto = new UpdateCardResponseDto(card.getTitle(),card.getContent(),card.getDueDate());
        when(cardRepository.findById(card.getId())).thenReturn(Optional.of(card));
        assertThat(responseDto.equals(cardService.updateCard(requestDto,user.getId(),workspace.getId(), card.getId()))).isTrue();
    }

//    @Test
//    void searchCardSuccess() {
//        List<CardBriefInfo> cardBriefInfoList = new ArrayList<>();
//        Long pageNumber = 1L;
//        Long pageSize = 10L;
//        CardBriefInfo cardBriefInfo = new CardBriefInfo(card.getId(),card.getTitle(),card.getDueDate(),1L,"testName",1L);
//        cardBriefInfoList.add(cardBriefInfo);
//        FindCardListResponseDto responseDto = new FindCardListResponseDto(cardBriefInfoList, new FindCardListResponseDto.pageInfo(1L,pageNumber,pageSize));
//
//        FindCardListRequestDto requestDto = new FindCardListRequestDto(card.getTitle(),card.getContent(),1L,card.getDueDate(),"name",pageNumber,pageSize);
//        when(cardRepository.searchAllCards(any(String.class),any(String.class),any(LocalDateTime.class),any(String.class),any(Long.class),any(Long.class),any(Long.class),any(Long.class))).thenReturn(cardBriefInfoList);
//        assertThat(responseDto.equals(cardService.findCardList(user.getId(),requestDto,workspace.getId()))).isTrue();
//    }


}