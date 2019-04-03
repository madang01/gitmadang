CREATE DEFINER=`root`@`localhost` PROCEDURE `INSERT_TEN_MILLION_ARTICLES_IN_FREE_BOARD`(IN _FIRST_WRITER_ID VARCHAR(20), IN _NICKNAME VARCHAR(45), OUT RESULT INT)
BEGIN
	/* 시작 게시판 번호 변수를 선언한다. */
	DECLARE _START_BOARD_NO INT UNSIGNED;
    DECLARE _BOARD_ID TINYINT UNSIGNED;
	DECLARE _GROUP_NO INT UNSIGNED;
	DECLARE _GROUP_SQ SMALLINT UNSIGNED;
	DECLARE _PARENT_NO INT UNSIGNED;
	DECLARE _DEPTH TINYINT UNSIGNED;
	DECLARE _VIEW_CNT INT;
	DECLARE _BOARD_STATE NCHAR(1);

	DECLARE _HISTORY_SQ TINYINT UNSIGNED;
	DECLARE _SUBJECT VARCHAR(255);
	DECLARE _CONTENT TEXT;
	DECLARE _IP VARCHAR(40);
    
    DECLARE _ROOT_PARENT_NO INT UNSIGNED;
    DECLARE _BOARD_NAME VARCHAR(80);
    DECLARE _DEPTH1_INX INT UNSIGNED;
    DECLARE _DEPTH2_INX INT UNSIGNED;
    DECLARE _DEPTH1_PARENT_NO INT UNSIGNED;
	DECLARE _DEPTH2_PARENT_NO INT UNSIGNED;
    DECLARE _ACTIVITY_SQ bigint;
	
	DECLARE _IS_MEMBER BOOLEAN;

	/* 만약 SQL에러라면 ROLLBACK 처리한다. */
	DECLARE exit handler for SQLEXCEPTION
	BEGIN
		ROLLBACK;        
		SET RESULT = -1;  
	END;

	/* 트랜젝션 시작 */
	START TRANSACTION;
		/* 게시판 지정 */
		SET _BOARD_ID = 2;
		/* 게시판 이름 */
		SET _BOARD_NAME = '이슈';
		/* 그룹내 순서 지정 */
		SET _GROUP_SQ = 0;
		/* 최상위 글의 부모번호 지정 */
		SET _ROOT_PARENT_NO = 0;
		/* 최상위 글의 깊이 지정 */
		SET _DEPTH = 0;
		/* 최초 등록시 조회 횟수 지정 */
		SET _VIEW_CNT = 0;
		/* 최초 등록시 게시판 상태 '정상' 지정 */
		SET _BOARD_STATE = 'Y';
		
		SET _DEPTH1_INX = 0;
		SET _DEPTH2_INX = 0;
		
		/* 최초 등록시 히스토리 순번 지정 */
		SET _HISTORY_SQ = 0;
		/* 테스트위한 임의 아이피 주소 지정 */
		SET _IP = "172.16.0.1";
		
		SET _ACTIVITY_SQ = 0;
		
		SET _IS_MEMBER = FALSE;

		SET autocommit=0;
		SET unique_checks=0;
		SET FOREIGN_KEY_CHECKS=0;
		
		SELECT if (max(board_no) is null, 1, max(board_no)+1) INTO _START_BOARD_NO FROM SB_BOARD_TB WHERE board_id = _BOARD_ID;
        
        SELECT TRUE INTO _IS_MEMBER FROM sb_member_tb WHERE user_id = _FIRST_WRITER_ID;        
        
        SELECT _IS_MEMBER, _START_BOARD_NO FROM DUAL;
        
        
		IF _IS_MEMBER IS FALSE THEN			
            INSERT INTO `sb_member_tb` (`user_id`,`nickname`,`pwd_base64`,`pwd_salt_base64`,`role`,`state`,`pwd_hint`,`pwd_answer`,`pwd_fail_cnt`,`reg_dt`,`last_mod_dt`,`ip`) 
				VALUES (_FIRST_WRITER_ID,_NICKNAME,'bFT9mAQXZE/sWa3INB3xTBI14kuRzmvNuZ5x4hN+zsVgbuLWhkA+Uh71RWSYCGZjAkkZkDo5l3i3KABf9gM0+w==','3g2cXn+k0Xk=',77,89,'질문','답변',0,'2019-03-31 08:24:00','2019-03-31 08:24:00','127.0.0.1');
		END IF;        
		
		
		WHILE _START_BOARD_NO <= 500000 DO	
			SET _DEPTH = 0;
			SET _GROUP_NO = _START_BOARD_NO;
			SET _GROUP_SQ = 9;
			
			SET _SUBJECT = CONCAT(_BOARD_NAME, CONCAT(' ', CONCAT(_START_BOARD_NO, ' 번째 주제')));
			SET _CONTENT = CONCAT(_BOARD_NAME, CONCAT(' ', CONCAT(_START_BOARD_NO, ' 번째 내용')));
            
            -- select '11111', _BOARD_ID, _START_BOARD_NO, _SUBJECT, _CONTENT from dual;
			-- select '0000000' from dual;
           
			/* 자유 게시판에 글 추가 */
			INSERT INTO SB_BOARD_TB(board_id, board_no, group_no, group_sq, parent_no, depth, view_cnt, board_st, next_attached_file_sq)
			VALUES(_BOARD_ID, _START_BOARD_NO, _GROUP_NO, _GROUP_SQ, _ROOT_PARENT_NO, _DEPTH, _VIEW_CNT, ascii(_BOARD_STATE), 0);
			
            -- select '1111111111111' from dual;
            
			INSERT INTO SB_BOARD_HISTORY_TB(board_id, board_no, history_sq, subject, contents, registrant_id, ip, reg_dt)
			VALUES(_BOARD_ID, _START_BOARD_NO, _HISTORY_SQ, _SUBJECT, _CONTENT, _FIRST_WRITER_ID, _IP, now());			
            
            -- select '2222222' from dual;
			
			SELECT if (max(activity_sq) is null, 0, max(activity_sq) + 1) into _ACTIVITY_SQ FROM SB_MEMBER_ACTIVITY_HISTORY_TB where user_id = _FIRST_WRITER_ID;
			INSERT INTO SB_MEMBER_ACTIVITY_HISTORY_TB(user_id, activity_sq, board_id, board_no, activity_type, reg_dt)
			VALUES(_FIRST_WRITER_ID, _ACTIVITY_SQ, _BOARD_ID, _START_BOARD_NO, ascii('W'), now());
            
            -- select '44444444' from dual;
            
            -- select '0000000' from dual;
            
			SET _DEPTH1_INX = 0;			
			SET _DEPTH1_PARENT_NO = _START_BOARD_NO;
			SET _START_BOARD_NO = _START_BOARD_NO + 1;
			
			WHILE _DEPTH1_INX < 3 DO
				SET _DEPTH = _DEPTH + 1;				
				SET _GROUP_SQ = _GROUP_SQ - 1;
				
				
				SET _SUBJECT = CONCAT(_BOARD_NAME, CONCAT(' ', CONCAT(_START_BOARD_NO, ' 번째 주제')));
				SET _CONTENT = CONCAT(_BOARD_NAME, CONCAT(' ', CONCAT(_START_BOARD_NO, ' 번째 내용')));
                
				-- select '22222', _BOARD_ID, _START_BOARD_NO, _SUBJECT, _CONTENT from dual;
				
				INSERT INTO SB_BOARD_TB(board_id, board_no, group_no, group_sq, parent_no, depth, view_cnt, board_st, next_attached_file_sq)
				VALUES(_BOARD_ID, _START_BOARD_NO, _GROUP_NO, _GROUP_SQ, _DEPTH1_PARENT_NO, _DEPTH, _VIEW_CNT, ascii(_BOARD_STATE), 0);
                
                -- select '222222222' from dual;
				
				INSERT INTO SB_BOARD_HISTORY_TB(board_id, board_no, history_sq, subject, contents, registrant_id, ip, reg_dt)
				VALUES(_BOARD_ID, _START_BOARD_NO, _HISTORY_SQ, _SUBJECT, _CONTENT, _FIRST_WRITER_ID, _IP, now());
                
                -- select '3333333333' from dual;
				
                
				SELECT if (max(activity_sq) is null, 0, max(activity_sq) + 1) into _ACTIVITY_SQ FROM SB_MEMBER_ACTIVITY_HISTORY_TB where user_id = _FIRST_WRITER_ID;
				INSERT INTO SB_MEMBER_ACTIVITY_HISTORY_TB(user_id, activity_sq, board_id, board_no, activity_type, reg_dt)
				VALUES(_FIRST_WRITER_ID, _ACTIVITY_SQ, _BOARD_ID, _START_BOARD_NO, ascii('R'), now());
                
                -- select '444444444444' from dual;
                
				
				SET _DEPTH2_INX = 0;
				SET _DEPTH2_PARENT_NO = _START_BOARD_NO;
				SET _START_BOARD_NO = _START_BOARD_NO + 1;				
				
				
				WHILE _DEPTH2_INX < 2 DO
					SET _DEPTH = _DEPTH + 1;				
					SET _GROUP_SQ = _GROUP_SQ - 1;				
				
					SET _SUBJECT = CONCAT(_BOARD_NAME, CONCAT(' ', CONCAT(_START_BOARD_NO, ' 번째 주제')));
					SET _CONTENT = CONCAT(_BOARD_NAME, CONCAT(' ', CONCAT(_START_BOARD_NO, ' 번째 내용')));
                    
                    -- select '3333', _BOARD_ID, _START_BOARD_NO, _SUBJECT, _CONTENT from dual;
                    
                   --  select '5555555555555555' from dual;
					
					INSERT INTO SB_BOARD_TB(board_id, board_no, group_no, group_sq, parent_no, depth, view_cnt, board_st, next_attached_file_sq)
					VALUES(_BOARD_ID, _START_BOARD_NO, _GROUP_NO, _GROUP_SQ, _DEPTH2_PARENT_NO, _DEPTH, _VIEW_CNT, ascii(_BOARD_STATE), 0);
					
					INSERT INTO SB_BOARD_HISTORY_TB(board_id, board_no, history_sq, subject, contents, registrant_id, ip, reg_dt)
					VALUES(_BOARD_ID, _START_BOARD_NO, _HISTORY_SQ, _SUBJECT, _CONTENT, _FIRST_WRITER_ID, _IP, now());
                    
                    -- select '666666666666666' from dual;
					
                    
					SELECT if (max(activity_sq) is null, 0, max(activity_sq) + 1) into _ACTIVITY_SQ FROM SB_MEMBER_ACTIVITY_HISTORY_TB where user_id = _FIRST_WRITER_ID;
					INSERT INTO SB_MEMBER_ACTIVITY_HISTORY_TB(user_id, activity_sq, board_id, board_no, activity_type, reg_dt)
					VALUES(_FIRST_WRITER_ID, _ACTIVITY_SQ, _BOARD_ID, _START_BOARD_NO, ascii('R'), now());
                    
                    
                    
                   --  select '77777777777777777' from dual;
                    
				
					SET _START_BOARD_NO = _START_BOARD_NO + 1;
					
					SET _DEPTH2_INX = _DEPTH2_INX + 1;
                    
                    -- select '44444444', _BOARD_ID, _START_BOARD_NO, _ACTIVITY_SQ, _DEPTH1_INX, _DEPTH2_INX from dual;
				END WHILE;
				
				SET _DEPTH1_INX = _DEPTH1_INX + 1;
                
                -- select '55555555', _BOARD_ID, _START_BOARD_NO, _ACTIVITY_SQ, _DEPTH1_INX, _DEPTH2_INX from dual;
			END WHILE;
			
            -- select '66666666', _BOARD_ID, _START_BOARD_NO, _ACTIVITY_SQ, _DEPTH1_INX, _DEPTH2_INX from dual;
            
            -- select _START_BOARD_NO from dual;
			
			iF MOD(_START_BOARD_NO, 10000) = 0 THEN 
				COMMIT;
            END IF;
			
		END WHILE;

		SET autocommit=1;
		SET unique_checks=1;
		SET FOREIGN_KEY_CHECKS=1;
        
	/* 커밋 */
	COMMIT;
	SET RESULT = 0;
END