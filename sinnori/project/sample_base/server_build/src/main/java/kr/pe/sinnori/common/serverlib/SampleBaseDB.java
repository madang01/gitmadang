package kr.pe.sinnori.common.serverlib;

import kr.pe.sinnori.common.etc.CommonType;

public class SampleBaseDB extends AbstractDBCommand {
	private final String dbcpConnectionPoolName = "sample_base_db";
	
	public String getDBCPConnectionPoolName() {
		return dbcpConnectionPoolName;
	}
	
	
	public SampleBaseDB() {
		super();
		try {
			addCreatingTableSql(getCreatingTableSqlForOA_MEMBER_TB());
			addCreatingTableSql(getCreatingTableSqlForSB_SEQ_MANAGER_TB());
			addCreatingTableSql(getCreatingTableSqlForSB_MEMBER_TB());
			addCreatingTableSql(getCreatingTableSqlForSB_GROUP_INFO_TB());
			addCreatingTableSql(getCreatingTableSqlForSB_GROUP_TB());
			addCreatingTableSql(getCreatingTableSqlForSB_BOARD_INFO_TB());
			addCreatingTableSql(getCreatingTableSqlForSB_BOARD_FILEINFO_TB());
			addCreatingTableSql(getCreatingTableSqlForSB_BOARD_FILELIST_TB());
			addCreatingTableSql(getCreatingTableSqlForSB_BOARD_TB());
			addCreatingTableSql(getCreatingTableSqlForSB_BOARD_VOTE_TB());
			
			addInsertingSql(getInsertingSqlForNoticeBoardInfo());
			addInsertingSql(getInsertingSqlForFreeBoardInfo());
			addInsertingSql(getInsertingSqlForFAQBoardInfo());
			addInsertingSql(getInsertingSqlForUploadFileNameSequence());
		} catch(IllegalArgumentException e) {
			log.error("IllegalArgumentException", e);
			System.exit(1);
		}
	}
	
	/**
	 * 
	 * @return 공지 게시판 정보 생성  쿼리문
	 */
	public String getInsertingSqlForNoticeBoardInfo() {
		StringBuffer stringBuilder = new StringBuffer();
		stringBuilder.append("INSERT INTO `SB_BOARD_INFO_TB`");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("(`board_id`, `board_name`, `board_info`)");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("VALUES");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("(");
		stringBuilder.append(CommonType.BOARD_ID.NOTICE.ordinal());
		stringBuilder.append(", '공지 게시판', '공지를 목적으로 하는 관리자 전용 게시판');");
		return stringBuilder.toString();
	}
	
	/**
	 * 
	 * @return 일반 게시판 정보 생성 쿼리
	 */
	public String getInsertingSqlForFreeBoardInfo() {
		StringBuffer stringBuilder = new StringBuffer();
		stringBuilder.append("INSERT INTO `SB_BOARD_INFO_TB`");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("(`board_id`, `board_name`, `board_info`)");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("VALUES");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("(");
		stringBuilder.append(CommonType.BOARD_ID.FREE.ordinal());
		stringBuilder.append(", '자유 게시판', '일반 유저들를 위한 자유 게시판');");
		return stringBuilder.toString();
	}
	
	/**
	 * 
	 * @return 일반 게시판 정보 생성 쿼리
	 */
	public String getInsertingSqlForFAQBoardInfo() {
		StringBuffer stringBuilder = new StringBuffer();
		stringBuilder.append("INSERT INTO `SB_BOARD_INFO_TB`");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("(`board_id`, `board_name`, `board_info`)");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("VALUES");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("(");
		stringBuilder.append(CommonType.BOARD_ID.FAQ.ordinal());
		stringBuilder.append(", 'FAQ 게시판', '신놀이 관련 질문답 게시판');");
		return stringBuilder.toString();
	}

	/**	 * 
	 * @return 업로드 파일 이름에 쓰이는 시퀀스 생성 쿼리문
	 */
	public String getInsertingSqlForUploadFileNameSequence() {
		StringBuffer stringBuilder = new StringBuffer();
		stringBuilder.append("INSERT INTO `SB_SEQ_MANAGER_TB`");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("(`sq_type_id`, `sq_value`)");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("VALUES");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("(");
		stringBuilder.append(CommonType.SEQ_TYPE_ID.UPLOAD_FILE_NAME.ordinal());
		stringBuilder.append(", 1);");
		return stringBuilder.toString();
	}
	
	public String getCreatingTableSqlForOA_MEMBER_TB() {
		StringBuffer stringBuilder = new StringBuffer();
	stringBuilder.append("CREATE TABLE IF NOT EXISTS `SB_DB`.`OA_MEMBER_TB` (");
	stringBuilder.append(System.getProperty("line.separator"));
	stringBuilder.append("  `id` VARCHAR(30) NOT NULL,");
	stringBuilder.append(System.getProperty("line.separator"));
	stringBuilder.append("  `pwd` VARCHAR(45) NULL,");
	stringBuilder.append(System.getProperty("line.separator"));
	stringBuilder.append("  `email` VARCHAR(45) NULL,");
	stringBuilder.append(System.getProperty("line.separator"));
	stringBuilder.append("  `phone` VARCHAR(45) NULL,");
	stringBuilder.append(System.getProperty("line.separator"));
	stringBuilder.append("  `regdate` DATETIME NULL,");
	stringBuilder.append(System.getProperty("line.separator"));
	stringBuilder.append("  PRIMARY KEY (`id`))");
	stringBuilder.append(System.getProperty("line.separator"));
	stringBuilder.append("ENGINE = InnoDB");
	stringBuilder.append(System.getProperty("line.separator"));
	stringBuilder.append("DEFAULT CHARACTER SET = utf8");
	stringBuilder.append(System.getProperty("line.separator"));
	stringBuilder.append("COLLATE = utf8_general_ci");
	stringBuilder.append(System.getProperty("line.separator"));
		return stringBuilder.toString();
	}
	
	public String getCreatingTableSqlForSB_SEQ_MANAGER_TB() {
		StringBuffer stringBuilder = new StringBuffer();
	stringBuilder.append("CREATE TABLE IF NOT EXISTS `SB_DB`.`SB_SEQ_MANAGER_TB` (");
	stringBuilder.append(System.getProperty("line.separator"));
	stringBuilder.append("  `sq_type_id` TINYINT UNSIGNED NOT NULL COMMENT '시퀀스 종류 식별자, 1:업로드 파일 이름 시퀀스',");
	stringBuilder.append(System.getProperty("line.separator"));
	stringBuilder.append("  `sq_value` INT UNSIGNED NULL,");
	stringBuilder.append(System.getProperty("line.separator"));
	stringBuilder.append("  `sq_type_name` VARCHAR(45) NULL,");
	stringBuilder.append(System.getProperty("line.separator"));
	stringBuilder.append("  PRIMARY KEY (`sq_type_id`))");
	stringBuilder.append(System.getProperty("line.separator"));
	stringBuilder.append("ENGINE = InnoDB");
	stringBuilder.append(System.getProperty("line.separator"));
	stringBuilder.append("DEFAULT CHARACTER SET = utf8");
	stringBuilder.append(System.getProperty("line.separator"));
	stringBuilder.append("COLLATE = utf8_general_ci");
	stringBuilder.append(System.getProperty("line.separator"));
		return stringBuilder.toString();
	}
	
	public String getCreatingTableSqlForSB_MEMBER_TB() {
		StringBuffer stringBuilder = new StringBuffer();
	stringBuilder.append("CREATE TABLE IF NOT EXISTS `SB_DB`.`SB_MEMBER_TB` (");
	stringBuilder.append(System.getProperty("line.separator"));
	stringBuilder.append("  `user_id` VARCHAR(20) NOT NULL COMMENT '사용자 아이디',");
	stringBuilder.append(System.getProperty("line.separator"));
	stringBuilder.append("  `nickname` VARCHAR(45) NOT NULL COMMENT '별명',");
	stringBuilder.append(System.getProperty("line.separator"));
	stringBuilder.append("  `pwd_base64` VARCHAR(88) NULL COMMENT '비밀번호, 비밀번호는 해쉬 값으로 변환되어 base64 형태로 저장된다.',");
	stringBuilder.append(System.getProperty("line.separator"));
	stringBuilder.append("  `pwd_salt_base64` VARCHAR(12) NULL COMMENT '비밀번호를 해쉬로 바꿀때 역 추적 방해를 목적으로 함께 사용하는 랜덤 값',");
	stringBuilder.append(System.getProperty("line.separator"));
	stringBuilder.append("  `member_gb` TINYINT NULL COMMENT '회원 구분, 0:관리자, 1:일반회원',");
	stringBuilder.append(System.getProperty("line.separator"));
	stringBuilder.append("  `member_st` TINYINT NULL DEFAULT NULL COMMENT '회원 상태, 0:정상, 1:블락, 2:탈퇴',");
	stringBuilder.append(System.getProperty("line.separator"));
	stringBuilder.append("  `pwd_hint` TINYTEXT NULL DEFAULT NULL COMMENT '비밀번호 힌트, 비밀번호 분실시 답변 유도용 사용자한테 보여주는 힌트',");
	stringBuilder.append(System.getProperty("line.separator"));
	stringBuilder.append("  `pwd_answer` TINYTEXT NULL DEFAULT NULL COMMENT '비밀번호 답변, 비밀번호 분실시 맞춘다면 비밀번호 재 설정 혹은 비밀번호 초기화를 진행한다.',");
	stringBuilder.append(System.getProperty("line.separator"));
	stringBuilder.append("  `pwd_fail_cnt` TINYINT(4) UNSIGNED NULL DEFAULT NULL COMMENT '비밀번호 틀린 횟수, 로그인시 비밀번호 틀릴 경우 1 씩 증가하며 최대 n 번까지 시도 가능하다.  비밀번호를 맞쳤을 경우 0 으로 초기화 된다.',");
	stringBuilder.append(System.getProperty("line.separator"));
	stringBuilder.append("  `reg_dt` DATETIME NULL DEFAULT NULL COMMENT '회원 가입일',");
	stringBuilder.append(System.getProperty("line.separator"));
	stringBuilder.append("  `mod_dt` DATETIME NULL DEFAULT NULL COMMENT '회원 정보 수정일',");
	stringBuilder.append(System.getProperty("line.separator"));
	stringBuilder.append("  PRIMARY KEY (`user_id`),");
	stringBuilder.append(System.getProperty("line.separator"));
	stringBuilder.append("  UNIQUE INDEX `tw_member_01_idx` (`nickname` ASC),");
	stringBuilder.append(System.getProperty("line.separator"));
	stringBuilder.append("  INDEX `tw_member_02_idx` (`member_st` ASC))");
	stringBuilder.append(System.getProperty("line.separator"));
	stringBuilder.append("ENGINE = InnoDB");
	stringBuilder.append(System.getProperty("line.separator"));
	stringBuilder.append("DEFAULT CHARACTER SET = utf8");
	stringBuilder.append(System.getProperty("line.separator"));
	stringBuilder.append("COLLATE = utf8_general_ci");
	stringBuilder.append(System.getProperty("line.separator"));
		return stringBuilder.toString();
	}
	
	public String getCreatingTableSqlForSB_GROUP_INFO_TB() {
		StringBuffer stringBuilder = new StringBuffer();
	stringBuilder.append("CREATE TABLE IF NOT EXISTS `SB_DB`.`SB_GROUP_INFO_TB` (");
	stringBuilder.append(System.getProperty("line.separator"));
	stringBuilder.append("  `group_id` TINYINT NOT NULL COMMENT '그룹 식별자, 0:admin, 1:joho',");
	stringBuilder.append(System.getProperty("line.separator"));
	stringBuilder.append("  `group_name` VARCHAR(45) NULL,");
	stringBuilder.append(System.getProperty("line.separator"));
	stringBuilder.append("  `group_info` TEXT NULL,");
	stringBuilder.append(System.getProperty("line.separator"));
	stringBuilder.append("  PRIMARY KEY (`group_id`))");
	stringBuilder.append(System.getProperty("line.separator"));
	stringBuilder.append("ENGINE = InnoDB");
	stringBuilder.append(System.getProperty("line.separator"));
	stringBuilder.append("DEFAULT CHARACTER SET = utf8");
	stringBuilder.append(System.getProperty("line.separator"));
	stringBuilder.append("COLLATE = utf8_general_ci");
	stringBuilder.append(System.getProperty("line.separator"));
		return stringBuilder.toString();
	}
	
	public String getCreatingTableSqlForSB_GROUP_TB() {
		StringBuffer stringBuilder = new StringBuffer();
	stringBuilder.append("CREATE TABLE IF NOT EXISTS `SB_DB`.`SB_GROUP_TB` (");
	stringBuilder.append(System.getProperty("line.separator"));
	stringBuilder.append("  `group_id` TINYINT NOT NULL,");
	stringBuilder.append(System.getProperty("line.separator"));
	stringBuilder.append("  `user_id` VARCHAR(20) NOT NULL,");
	stringBuilder.append(System.getProperty("line.separator"));
	stringBuilder.append("  `reg_dt` DATETIME NULL,");
	stringBuilder.append(System.getProperty("line.separator"));
	stringBuilder.append("  PRIMARY KEY (`group_id`, `user_id`),");
	stringBuilder.append(System.getProperty("line.separator"));
	stringBuilder.append("  INDEX `fk_sb_member_tb_02_idx` (`user_id` ASC),");
	stringBuilder.append(System.getProperty("line.separator"));
	stringBuilder.append("  CONSTRAINT `fk_sb_group_01`");
	stringBuilder.append(System.getProperty("line.separator"));
	stringBuilder.append("    FOREIGN KEY (`group_id`)");
	stringBuilder.append(System.getProperty("line.separator"));
	stringBuilder.append("    REFERENCES `SB_DB`.`SB_GROUP_INFO_TB` (`group_id`)");
	stringBuilder.append(System.getProperty("line.separator"));
	stringBuilder.append("    ON DELETE NO ACTION");
	stringBuilder.append(System.getProperty("line.separator"));
	stringBuilder.append("    ON UPDATE NO ACTION,");
	stringBuilder.append(System.getProperty("line.separator"));
	stringBuilder.append("  CONSTRAINT `fk_sb_group_02`");
	stringBuilder.append(System.getProperty("line.separator"));
	stringBuilder.append("    FOREIGN KEY (`user_id`)");
	stringBuilder.append(System.getProperty("line.separator"));
	stringBuilder.append("    REFERENCES `SB_DB`.`SB_MEMBER_TB` (`user_id`)");
	stringBuilder.append(System.getProperty("line.separator"));
	stringBuilder.append("    ON DELETE NO ACTION");
	stringBuilder.append(System.getProperty("line.separator"));
	stringBuilder.append("    ON UPDATE NO ACTION)");
	stringBuilder.append(System.getProperty("line.separator"));
	stringBuilder.append("ENGINE = InnoDB");
	stringBuilder.append(System.getProperty("line.separator"));
	stringBuilder.append("DEFAULT CHARACTER SET = utf8");
	stringBuilder.append(System.getProperty("line.separator"));
	stringBuilder.append("COLLATE = utf8_general_ci");
	stringBuilder.append(System.getProperty("line.separator"));
		return stringBuilder.toString();
	}
	
	public String getCreatingTableSqlForSB_BOARD_INFO_TB() {
		StringBuffer stringBuilder = new StringBuffer();
	stringBuilder.append("CREATE TABLE IF NOT EXISTS `SB_DB`.`SB_BOARD_INFO_TB` (");
	stringBuilder.append(System.getProperty("line.separator"));
	stringBuilder.append("  `board_id` TINYINT UNSIGNED NOT NULL COMMENT '게시판 식별자,\n0 : 공지, 1:자유',");
	stringBuilder.append(System.getProperty("line.separator"));
	stringBuilder.append("  `board_name` VARCHAR(30) NULL COMMENT '게시판 이름',");
	stringBuilder.append(System.getProperty("line.separator"));
	stringBuilder.append("  `board_info` TEXT NULL COMMENT '게시판 설명',");
	stringBuilder.append(System.getProperty("line.separator"));
	stringBuilder.append("  PRIMARY KEY (`board_id`))");
	stringBuilder.append(System.getProperty("line.separator"));
	stringBuilder.append("ENGINE = InnoDB");
	stringBuilder.append(System.getProperty("line.separator"));
	stringBuilder.append("AUTO_INCREMENT = 3");
	stringBuilder.append(System.getProperty("line.separator"));
	stringBuilder.append("DEFAULT CHARACTER SET = utf8");
	stringBuilder.append(System.getProperty("line.separator"));
	stringBuilder.append("COLLATE = utf8_general_ci");
	stringBuilder.append(System.getProperty("line.separator"));
		return stringBuilder.toString();
	}
	
	public String getCreatingTableSqlForSB_BOARD_FILEINFO_TB() {
		StringBuffer stringBuilder = new StringBuffer();
	stringBuilder.append("CREATE TABLE IF NOT EXISTS `SB_DB`.`SB_BOARD_FILEINFO_TB` (");
	stringBuilder.append(System.getProperty("line.separator"));
	stringBuilder.append("  `attach_id` INT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '업로드 식별자, 자동증가',");
	stringBuilder.append(System.getProperty("line.separator"));
	stringBuilder.append("  `owner_id` VARCHAR(20) NOT NULL COMMENT '첨부 파일 등록자 아이디',");
	stringBuilder.append(System.getProperty("line.separator"));
	stringBuilder.append("  `ip` VARCHAR(15) NULL COMMENT '첨부 파일 등록자 IP 주소',");
	stringBuilder.append(System.getProperty("line.separator"));
	stringBuilder.append("  `reg_dt` DATETIME NULL COMMENT '최초 등록일',");
	stringBuilder.append(System.getProperty("line.separator"));
	stringBuilder.append("  `mod_dt` DATETIME NULL COMMENT '최근 수정일',");
	stringBuilder.append(System.getProperty("line.separator"));
	stringBuilder.append("  PRIMARY KEY (`attach_id`),");
	stringBuilder.append(System.getProperty("line.separator"));
	stringBuilder.append("  INDEX `fk_board_fileinfo_01_idx` (`owner_id` ASC),");
	stringBuilder.append(System.getProperty("line.separator"));
	stringBuilder.append("  CONSTRAINT `fk_sb_board_fileinfo_01`");
	stringBuilder.append(System.getProperty("line.separator"));
	stringBuilder.append("    FOREIGN KEY (`owner_id`)");
	stringBuilder.append(System.getProperty("line.separator"));
	stringBuilder.append("    REFERENCES `SB_DB`.`SB_MEMBER_TB` (`user_id`)");
	stringBuilder.append(System.getProperty("line.separator"));
	stringBuilder.append("    ON DELETE NO ACTION");
	stringBuilder.append(System.getProperty("line.separator"));
	stringBuilder.append("    ON UPDATE NO ACTION)");
	stringBuilder.append(System.getProperty("line.separator"));
	stringBuilder.append("ENGINE = InnoDB");
	stringBuilder.append(System.getProperty("line.separator"));
	stringBuilder.append("DEFAULT CHARACTER SET = utf8");
	stringBuilder.append(System.getProperty("line.separator"));
	stringBuilder.append("COLLATE = utf8_general_ci");
	stringBuilder.append(System.getProperty("line.separator"));
		return stringBuilder.toString();
	}
	
	public String getCreatingTableSqlForSB_BOARD_FILELIST_TB() {
		StringBuffer stringBuilder = new StringBuffer();
	stringBuilder.append("CREATE TABLE IF NOT EXISTS `SB_DB`.`SB_BOARD_FILELIST_TB` (");
	stringBuilder.append(System.getProperty("line.separator"));
	stringBuilder.append("  `attach_id` INT UNSIGNED NOT NULL COMMENT '업로드 식별자',");
	stringBuilder.append(System.getProperty("line.separator"));
	stringBuilder.append("  `attach_sq` TINYINT UNSIGNED NOT NULL COMMENT '첨부 파일 순번, 자동 증가',");
	stringBuilder.append(System.getProperty("line.separator"));
	stringBuilder.append("  `attach_fname` VARCHAR(255) NULL COMMENT '첨부 파일 이름',");
	stringBuilder.append(System.getProperty("line.separator"));
	stringBuilder.append("  `sys_fname` VARCHAR(600) NULL COMMENT '첨부 파일의 시스템 절대 경로 파일명',");
	stringBuilder.append(System.getProperty("line.separator"));
	stringBuilder.append("  PRIMARY KEY (`attach_sq`, `attach_id`),");
	stringBuilder.append(System.getProperty("line.separator"));
	stringBuilder.append("  INDEX `fk_tw_board_filelist_01_idx` (`attach_id` ASC),");
	stringBuilder.append(System.getProperty("line.separator"));
	stringBuilder.append("  CONSTRAINT `fk_sb_board_filelist_01`");
	stringBuilder.append(System.getProperty("line.separator"));
	stringBuilder.append("    FOREIGN KEY (`attach_id`)");
	stringBuilder.append(System.getProperty("line.separator"));
	stringBuilder.append("    REFERENCES `SB_DB`.`SB_BOARD_FILEINFO_TB` (`attach_id`)");
	stringBuilder.append(System.getProperty("line.separator"));
	stringBuilder.append("    ON DELETE NO ACTION");
	stringBuilder.append(System.getProperty("line.separator"));
	stringBuilder.append("    ON UPDATE NO ACTION)");
	stringBuilder.append(System.getProperty("line.separator"));
	stringBuilder.append("ENGINE = InnoDB");
	stringBuilder.append(System.getProperty("line.separator"));
	stringBuilder.append("DEFAULT CHARACTER SET = utf8");
	stringBuilder.append(System.getProperty("line.separator"));
	stringBuilder.append("COLLATE = utf8_general_ci");
	stringBuilder.append(System.getProperty("line.separator"));
		return stringBuilder.toString();
	}
	
	public String getCreatingTableSqlForSB_BOARD_TB() {
		StringBuffer stringBuilder = new StringBuffer();
	stringBuilder.append("CREATE TABLE IF NOT EXISTS `SB_DB`.`SB_BOARD_TB` (");
	stringBuilder.append(System.getProperty("line.separator"));
	stringBuilder.append("  `board_no` INT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '글 번호',");
	stringBuilder.append(System.getProperty("line.separator"));
	stringBuilder.append("  `group_no` INT UNSIGNED NOT NULL COMMENT '그룹 번호',");
	stringBuilder.append(System.getProperty("line.separator"));
	stringBuilder.append("  `group_sq` SMALLINT UNSIGNED NOT NULL COMMENT '그룹 즉 동일한 그룹 번호(=group_no)  에서의 순번',");
	stringBuilder.append(System.getProperty("line.separator"));
	stringBuilder.append("  `parent_no` INT UNSIGNED NULL COMMENT '부모 글 번호,  부모가 없는 경우  다른 말로 댓글 깊이가 0 인 글은 0 값을 갖는다.',");
	stringBuilder.append(System.getProperty("line.separator"));
	stringBuilder.append("  `depth` TINYINT UNSIGNED NULL COMMENT '트리 깊이,  0 부터 시작하며 트리 깊이가 0 일 경우 최상위 글로써 최상위 글을 기준으로 이후 댓글이 달린다. 자식 글의 댓글 깊이는 부모 글의 댓글 깊이보다 1 이 크다.',");
	stringBuilder.append(System.getProperty("line.separator"));
	stringBuilder.append("  `board_id` TINYINT UNSIGNED NOT NULL COMMENT '게시판 종류 식별자, 어떤 게시판인지 설명하는 게시판 정보(board_info) 테이블을 바라본다.',");
	stringBuilder.append(System.getProperty("line.separator"));
	stringBuilder.append("  `writer_id` VARCHAR(20) NOT NULL COMMENT '작성자',");
	stringBuilder.append(System.getProperty("line.separator"));
	stringBuilder.append("  `subject` VARCHAR(255) NULL COMMENT '제목',");
	stringBuilder.append(System.getProperty("line.separator"));
	stringBuilder.append("  `content` TEXT NULL COMMENT '본문',");
	stringBuilder.append(System.getProperty("line.separator"));
	stringBuilder.append("  `attach_id` INT UNSIGNED NULL,");
	stringBuilder.append(System.getProperty("line.separator"));
	stringBuilder.append("  `view_cnt` INT NULL COMMENT '조회수',");
	stringBuilder.append(System.getProperty("line.separator"));
	stringBuilder.append("  `del_fl` CHAR(1) NULL COMMENT 'Y : 삭제된 게시글, N : 삭제 되지 않은 정상 게시글',");
	stringBuilder.append(System.getProperty("line.separator"));
	stringBuilder.append("  `ip` VARCHAR(15) NULL,");
	stringBuilder.append(System.getProperty("line.separator"));
	stringBuilder.append("  `reg_dt` DATETIME NULL COMMENT '최초 작성일',");
	stringBuilder.append(System.getProperty("line.separator"));
	stringBuilder.append("  `mod_dt` DATETIME NULL COMMENT '최근 수정일',");
	stringBuilder.append(System.getProperty("line.separator"));
	stringBuilder.append("  PRIMARY KEY (`board_no`),");
	stringBuilder.append(System.getProperty("line.separator"));
	stringBuilder.append("  INDEX `tw_board_01_idx` (`writer_id` ASC),");
	stringBuilder.append(System.getProperty("line.separator"));
	stringBuilder.append("  INDEX `tw_board_02_idx` (`board_id` ASC),");
	stringBuilder.append(System.getProperty("line.separator"));
	stringBuilder.append("  INDEX `tw_board_03_idx` (`group_no` ASC, `group_sq` ASC),");
	stringBuilder.append(System.getProperty("line.separator"));
	stringBuilder.append("  INDEX `fk_tw_board_03_idx` (`attach_id` ASC),");
	stringBuilder.append(System.getProperty("line.separator"));
	stringBuilder.append("  CONSTRAINT `fk_sb_board_01`");
	stringBuilder.append(System.getProperty("line.separator"));
	stringBuilder.append("    FOREIGN KEY (`writer_id`)");
	stringBuilder.append(System.getProperty("line.separator"));
	stringBuilder.append("    REFERENCES `SB_DB`.`SB_MEMBER_TB` (`user_id`)");
	stringBuilder.append(System.getProperty("line.separator"));
	stringBuilder.append("    ON DELETE NO ACTION");
	stringBuilder.append(System.getProperty("line.separator"));
	stringBuilder.append("    ON UPDATE NO ACTION,");
	stringBuilder.append(System.getProperty("line.separator"));
	stringBuilder.append("  CONSTRAINT `fk_sb_board_02`");
	stringBuilder.append(System.getProperty("line.separator"));
	stringBuilder.append("    FOREIGN KEY (`board_id`)");
	stringBuilder.append(System.getProperty("line.separator"));
	stringBuilder.append("    REFERENCES `SB_DB`.`SB_BOARD_INFO_TB` (`board_id`)");
	stringBuilder.append(System.getProperty("line.separator"));
	stringBuilder.append("    ON DELETE NO ACTION");
	stringBuilder.append(System.getProperty("line.separator"));
	stringBuilder.append("    ON UPDATE NO ACTION,");
	stringBuilder.append(System.getProperty("line.separator"));
	stringBuilder.append("  CONSTRAINT `fk_sb_board_03`");
	stringBuilder.append(System.getProperty("line.separator"));
	stringBuilder.append("    FOREIGN KEY (`attach_id`)");
	stringBuilder.append(System.getProperty("line.separator"));
	stringBuilder.append("    REFERENCES `SB_DB`.`SB_BOARD_FILEINFO_TB` (`attach_id`)");
	stringBuilder.append(System.getProperty("line.separator"));
	stringBuilder.append("    ON DELETE NO ACTION");
	stringBuilder.append(System.getProperty("line.separator"));
	stringBuilder.append("    ON UPDATE NO ACTION)");
	stringBuilder.append(System.getProperty("line.separator"));
	stringBuilder.append("ENGINE = InnoDB");
	stringBuilder.append(System.getProperty("line.separator"));
	stringBuilder.append("DEFAULT CHARACTER SET = utf8");
	stringBuilder.append(System.getProperty("line.separator"));
	stringBuilder.append("COLLATE = utf8_general_ci");
	stringBuilder.append(System.getProperty("line.separator"));
		return stringBuilder.toString();
	}
	
	public String getCreatingTableSqlForSB_BOARD_VOTE_TB() {
		StringBuffer stringBuilder = new StringBuffer();
	stringBuilder.append("CREATE TABLE IF NOT EXISTS `SB_DB`.`SB_BOARD_VOTE_TB` (");
	stringBuilder.append(System.getProperty("line.separator"));
	stringBuilder.append("  `board_no` INT UNSIGNED NOT NULL,");
	stringBuilder.append(System.getProperty("line.separator"));
	stringBuilder.append("  `user_id` VARCHAR(20) NOT NULL,");
	stringBuilder.append(System.getProperty("line.separator"));
	stringBuilder.append("  `ip` VARCHAR(15) NULL,");
	stringBuilder.append(System.getProperty("line.separator"));
	stringBuilder.append("  `reg_dt` DATETIME NULL,");
	stringBuilder.append(System.getProperty("line.separator"));
	stringBuilder.append("  PRIMARY KEY (`board_no`, `user_id`),");
	stringBuilder.append(System.getProperty("line.separator"));
	stringBuilder.append("  INDEX `tw_board_vote_01_idx` (`user_id` ASC),");
	stringBuilder.append(System.getProperty("line.separator"));
	stringBuilder.append("  CONSTRAINT `fk_sb_board_vote_01`");
	stringBuilder.append(System.getProperty("line.separator"));
	stringBuilder.append("    FOREIGN KEY (`board_no`)");
	stringBuilder.append(System.getProperty("line.separator"));
	stringBuilder.append("    REFERENCES `SB_DB`.`SB_BOARD_TB` (`board_no`)");
	stringBuilder.append(System.getProperty("line.separator"));
	stringBuilder.append("    ON DELETE NO ACTION");
	stringBuilder.append(System.getProperty("line.separator"));
	stringBuilder.append("    ON UPDATE NO ACTION,");
	stringBuilder.append(System.getProperty("line.separator"));
	stringBuilder.append("  CONSTRAINT `fk_sb_board_vote_02`");
	stringBuilder.append(System.getProperty("line.separator"));
	stringBuilder.append("    FOREIGN KEY (`user_id`)");
	stringBuilder.append(System.getProperty("line.separator"));
	stringBuilder.append("    REFERENCES `SB_DB`.`SB_MEMBER_TB` (`user_id`)");
	stringBuilder.append(System.getProperty("line.separator"));
	stringBuilder.append("    ON DELETE NO ACTION");
	stringBuilder.append(System.getProperty("line.separator"));
	stringBuilder.append("    ON UPDATE NO ACTION)");
	stringBuilder.append(System.getProperty("line.separator"));
	stringBuilder.append("ENGINE = InnoDB");
	stringBuilder.append(System.getProperty("line.separator"));
	stringBuilder.append("DEFAULT CHARACTER SET = utf8");
	stringBuilder.append(System.getProperty("line.separator"));
	stringBuilder.append("COLLATE = utf8_general_ci");
	stringBuilder.append(System.getProperty("line.separator"));
		return stringBuilder.toString();
	}
}
