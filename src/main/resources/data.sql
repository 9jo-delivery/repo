-- p_users 테이블의 id는 bigint이므로 1을 직접 넣기 좋습니다.
INSERT INTO p_users (id, username, password, name, phone, role, is_deleted , created_at)
VALUES (1, 'test1', 'encoded_password123', '최유준', '010-1111-2222', 'CUSTOMER',false, NOW());