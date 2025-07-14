# 채팅 로그 저장 시스템

## 개요
이 시스템은 실시간 채팅 기능을 위한 채팅 로그 저장 시스템을 구현합니다. MySQL 데이터베이스를 사용하여 채팅 메시지를 영구 저장하고, 채팅방과 참여자를 관리합니다.

## 데이터베이스 구조

### 1. ChatRoom (채팅방)
- **ID**: 채팅방 고유 식별자
- **Name**: 채팅방 이름
- **Type**: 채팅방 타입 (PRIVATE, GROUP, SUPPORT)
- **Description**: 채팅방 설명
- **생성/수정 시간**: BaseTimeEntity 상속으로 자동 관리

### 2. ChatParticipant (채팅 참여자)
- **ID**: 참여자 고유 식별자
- **User**: 참여 사용자 (User 엔티티와 연관)
- **ChatRoom**: 참여 채팅방 (ChatRoom 엔티티와 연관)
- **IsActive**: 활성 참여 여부

### 3. ChatLog (채팅 로그)
- **ID**: 메시지 고유 식별자
- **ChatRoom**: 메시지가 속한 채팅방
- **Sender**: 메시지 발신자
- **Message**: 메시지 내용
- **MessageType**: 메시지 타입 (TEXT, IMAGE, FILE, SYSTEM)
- **FileUrl**: 파일 메시지의 경우 파일 URL
- **IsRead**: 읽음 여부
- **IsEdited**: 수정 여부
- **생성/수정 시간**: BaseTimeEntity 상속으로 자동 관리

## 주요 기능

### 1. 채팅방 관리
- 채팅방 생성
- 채팅방 참여/퇴장
- 사용자별 채팅방 목록 조회

### 2. 메시지 관리
- 메시지 전송 (텍스트, 이미지, 파일)
- 메시지 이력 조회 (페이지네이션)
- 메시지 검색
- 시스템 메시지 (입장/퇴장 알림)

### 3. 상태 관리
- 읽음/읽지 않음 상태
- 메시지 수정 이력
- 참여자 활성 상태

## API 엔드포인트

### 채팅방 관리
```
POST /api/chat/rooms - 채팅방 생성
POST /api/chat/rooms/{id}/join - 채팅방 참여
POST /api/chat/rooms/{id}/leave - 채팅방 퇴장
GET /api/chat/rooms - 사용자 채팅방 목록
```

### 메시지 관리
```
POST /api/chat/messages - 메시지 전송
GET /api/chat/rooms/{id}/messages - 채팅 메시지 조회
GET /api/chat/rooms/{id}/search - 메시지 검색
```

## 사용 방법

### 1. 채팅방 생성
```json
POST /api/chat/rooms
{
  "name": "새 채팅방",
  "type": "PRIVATE",
  "description": "1:1 채팅방입니다."
}
```

### 2. 메시지 전송
```json
POST /api/chat/messages
{
  "chatRoomId": 1,
  "message": "안녕하세요!",
  "messageType": "TEXT"
}
```

### 3. 메시지 조회
```
GET /api/chat/rooms/1/messages?page=0&size=20
```

## 확장 가능성

### 1. 실시간 기능
- WebSocket을 이용한 실시간 메시지 전송
- Server-Sent Events (SSE)를 이용한 실시간 알림

### 2. 추가 기능
- 메시지 반응 (이모지)
- 답장 기능
- 메시지 전달
- 채팅방 멤버 권한 관리

### 3. 성능 최적화
- Redis 캐싱
- 메시지 아카이빙
- 이미지/파일 CDN 연동

## 보안 고려사항

### 1. 인증/인가
- JWT 토큰을 통한 사용자 인증
- 채팅방 참여 권한 확인
- 메시지 전송 권한 확인

### 2. 데이터 보호
- 메시지 내용 암호화 (선택사항)
- 파일 업로드 제한
- SQL 인젝션 방지

## 모니터링 및 로그

### 1. 지표
- 채팅방별 메시지 수
- 사용자별 활동 로그
- 응답 시간 모니터링

### 2. 로그 관리
- 메시지 전송 로그
- 에러 로그
- 사용자 활동 로그