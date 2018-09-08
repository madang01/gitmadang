CREATE DEFINER=`root`@`localhost` PROCEDURE `INSERT_TEN_MILLION_ARTICLES_IN_FREE_BOARD`(IN _FIRST_WRITER_ID VARCHAR(20), OUT RESULT INT)
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

	/* 만약 SQL에러라면 ROLLBACK 처리한다. */
	DECLARE exit handler for SQLEXCEPTION
	BEGIN
		ROLLBACK;        
		SET RESULT = -1;  
	END;
    
	/* 트랜젝션 시작 */
	START TRANSACTION;
		/* 자유 게시판 지정 */
		SET _BOARD_ID = 1;
		/* 그룹내 순서 지정 */
		SET _GROUP_SQ = 0;
		/* 최상위 글의 부모번호 지정 */
		SET _PARENT_NO = 0;
		/* 최상위 글의 깊이 지정 */
		SET _DEPTH = 0;
		/* 최초 등록시 조회 횟수 지정 */
		SET _VIEW_CNT = 0;
		/* 최초 등록시 게시판 상태 '정상' 지정 */
		SET _BOARD_STATE = 'Y';
		
		/* 최초 등록시 히스토리 순번 지정 */
		SET _HISTORY_SQ = 0;
		/* 테스트위한 임의 아이피 주소 지정 */
		SET _IP = "172.16.0.1";

        
        ALTER TABLE SB_BOARD_TB disable keys;
		ALTER TABLE SB_BOARD_HISTORY_TB disable keys;
		-- LOCK TABLES SB_BOARD_TB WRITE, SB_BOARD_HISTORY_TB WRITE;
        
		SELECT if (max(board_no) is null, 1, max(board_no)+1) INTO _START_BOARD_NO FROM SB_BOARD_TB WHERE board_id = _BOARD_ID;        
        -- select _START_BOARD_NO, _BOARD_ID, _GROUP_SQ, _PARENT_NO, _DEPTH, _BOARD_STATE, _HISTORY_SQ, _IP;
		
		WHILE _START_BOARD_NO <= 10000000 DO
			/* 그룹 번호 지정 */
			SET _GROUP_NO = _START_BOARD_NO;
			
			SET _SUBJECT = CONCAT(_START_BOARD_NO, ' 번째 주제');
			SET _CONTENT = CONCAT(_START_BOARD_NO, ' 번째 내용');
            
            -- select _BOARD_ID, _START_BOARD_NO, _SUBJECT, _CONTENT;
			
            
			/* 자유 게시판에 글 추가 */
			INSERT INTO SB_BOARD_TB(board_id, board_no, group_no, group_sq, parent_no, depth, view_cnt, board_st)
			VALUES(_BOARD_ID, _START_BOARD_NO, _GROUP_NO, _GROUP_SQ, _PARENT_NO, _DEPTH, _VIEW_CNT, _BOARD_STATE);
            
            
			
			INSERT INTO SB_BOARD_HISTORY_TB(board_id, board_no, history_sq, subject, content, modifier_id, ip, reg_dt)
			VALUES(_BOARD_ID, _START_BOARD_NO, _HISTORY_SQ, _SUBJECT, _CONTENT, _FIRST_WRITER_ID, _IP, now());
            
            iF MOD(_START_BOARD_NO, 10000) = 0 THEN 
				COMMIT;
            END IF;
            
			SET _START_BOARD_NO = _START_BOARD_NO + 1;
		END WHILE;

		ALTER TABLE SB_BOARD_TB enable keys;
		ALTER TABLE SB_BOARD_HISTORY_TB enable keys;
        -- UNLOCK TABLES;
	/* 커밋 */
	COMMIT;
	SET RESULT = 0;
END