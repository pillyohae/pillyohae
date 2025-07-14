-- 채팅 로그 저장 시스템 데이터베이스 테이블 생성 스크립트

-- 1. 채팅방 테이블
CREATE TABLE chat_rooms (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    type VARCHAR(20) NOT NULL,
    description VARCHAR(500),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deleted_at TIMESTAMP NULL
);

-- 2. 채팅 참여자 테이블
CREATE TABLE chat_participants (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    chat_room_id BIGINT NOT NULL,
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deleted_at TIMESTAMP NULL,
    FOREIGN KEY (user_id) REFERENCES user(id),
    FOREIGN KEY (chat_room_id) REFERENCES chat_rooms(id),
    UNIQUE KEY unique_user_room (user_id, chat_room_id)
);

-- 3. 채팅 로그 테이블
CREATE TABLE chat_logs (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    chat_room_id BIGINT NOT NULL,
    sender_id BIGINT NOT NULL,
    message TEXT NOT NULL,
    message_type VARCHAR(20) NOT NULL,
    file_url VARCHAR(500),
    is_read BOOLEAN DEFAULT FALSE,
    is_edited BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deleted_at TIMESTAMP NULL,
    FOREIGN KEY (chat_room_id) REFERENCES chat_rooms(id),
    FOREIGN KEY (sender_id) REFERENCES user(id)
);

-- 인덱스 생성 (성능 최적화)
CREATE INDEX idx_chat_logs_room_created ON chat_logs(chat_room_id, created_at);
CREATE INDEX idx_chat_logs_sender ON chat_logs(sender_id);
CREATE INDEX idx_chat_participants_user ON chat_participants(user_id, is_active);
CREATE INDEX idx_chat_participants_room ON chat_participants(chat_room_id, is_active);
CREATE INDEX idx_chat_logs_message_type ON chat_logs(message_type);
CREATE INDEX idx_chat_logs_read_status ON chat_logs(is_read);

-- 샘플 데이터 삽입 (테스트용)
INSERT INTO chat_rooms (name, type, description) VALUES 
('일반 채팅방', 'PRIVATE', '1:1 개인 채팅방'),
('영양제 추천 그룹', 'GROUP', '영양제 추천 및 정보 공유'),
('고객 지원', 'SUPPORT', '고객 문의 및 지원');

-- 데이터 확인
SELECT 'chat_rooms' as table_name, COUNT(*) as count FROM chat_rooms
UNION ALL
SELECT 'chat_participants' as table_name, COUNT(*) as count FROM chat_participants
UNION ALL
SELECT 'chat_logs' as table_name, COUNT(*) as count FROM chat_logs;