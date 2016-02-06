package main;

import java.sql.DatabaseMetaData;
import java.sql.SQLException;
import java.sql.Statement;

import kr.pe.sinnori.common.config.configvo.ProjectPartConfigurationVO;
import kr.pe.sinnori.common.etc.DBCPManager;
import kr.pe.sinnori.common.exception.DBNotReadyException;
import kr.pe.sinnori.common.exception.NotFoundProjectException;
import kr.pe.sinnori.common.project.ProjectWorkerIF;
import kr.pe.sinnori.common.serverlib.ServerCommonStaticFinalVars;
import kr.pe.sinnori.server.ServerProject;
import kr.pe.sinnori.server.ServerProjectManager;

import org.apache.commons.dbcp2.BasicDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SinnoriServerMain implements ProjectWorkerIF {	
	public static void main(String argv[]) throws NotFoundProjectException {
		Logger log = LoggerFactory.getLogger("kr.pe.sinnori.main");

		try {
			ServerProject mainServerProject = ServerProjectManager
					.getInstance().getMainServerProject();
			mainServerProject.startServer(new SinnoriServerMain());
		} catch (Throwable e) {
			log.warn("unknown error", e);
		}
	}

	/**
	 * Warning! 테이블 의존성을 반영한 순서에 맞지 않으면 생성 실패함, mysql 기준 "Can't create table '#테이블명>' (errno: 150)"
	 */
	private String arrySqlCreatingTable[][] = {
			{"OA_MEMBER_TB", getCreatingTableSqlForOA_MEMBER_TB()},				
			{"SB_SEQ_MANAGER_TB", getCreatingTableSqlForSB_SEQ_MANAGER_TB()},
			{"SB_MEMBER_TB", getCreatingTableSqlForSB_MEMBER_TB()},				
			{"SB_BOARD_INFO_TB", getCreatingTableSqlForSB_BOARD_INFO_TB()},					
			{"SB_BOARD_FILEINFO_TB", getCreatingTableSqlForSB_BOARD_FILEINFO_TB()},
			{"SB_BOARD_FILELIST_TB", getCreatingTableSqlForSB_BOARD_FILELIST_TB()},
			{"SB_BOARD_TB", getCreatingTableSqlForSB_BOARD_TB()},
			{"SB_BOARD_VOTE_TB", getCreatingTableSqlForSB_BOARD_VOTE_TB()}							
	};
	
	public void checkConnectionValidation(java.sql.Connection conn) {
		Logger log = LoggerFactory.getLogger("kr.pe.sinnori.main");

		java.sql.PreparedStatement pstmt = null;

		java.sql.ResultSet rs = null;

		try {

			String sql = "select 1";

			pstmt = conn.prepareStatement(sql);

			rs = pstmt.executeQuery();

			if (!rs.next()) {
				log.error("db connectin validation check sql execution fail");
				System.exit(1);
			}
			
			log.info("db connectin validation check sql execution success");

			// conn.commit();

		} catch (SQLException e) {
			log.error("SQLException", e);
			System.exit(1);
		} finally {

			if (null != rs) {

				try {

					rs.close();

				} catch (Exception e) {

					log.warn("when rs close, unknown error", e);

				}

			}

			if (null != pstmt) {

				try {

					pstmt.close();

				} catch (Exception e) {

					log.warn("when pstmt close, unknown error", e);

				}

			}
		}
	}

	public boolean isBoardTable(java.sql.Connection conn) throws SQLException {
		Logger log = LoggerFactory.getLogger("kr.pe.sinnori.main");
		DatabaseMetaData databaseMetaData = null;
		java.sql.ResultSet rs = null;

		try {
			databaseMetaData = conn.getMetaData();

			rs = databaseMetaData.getTables(null, null, "SB_BOARD_TB",
					new String[] { "TABLE" });

			if (rs.next()) {
				return true;
			}

		} finally {
			if (null != rs) {

				try {

					rs.close();

				} catch (Exception e) {

					log.warn("when rs close, unknown error", e);

				}

			}
		}

		return false;
	}
	
	public void createAllTables(java.sql.Connection conn) {
		Logger log = LoggerFactory.getLogger("kr.pe.sinnori.main");			
		
		for (int i=0; i < arrySqlCreatingTable.length; i++) {
			String tableName = arrySqlCreatingTable[i][0];
			String creatingTableSql = arrySqlCreatingTable[i][1];			
			try {
				createTable(conn, tableName, creatingTableSql);
			} catch (SQLException e) {
				log.error("1.creating table sql["+creatingTableSql+"] execution fail", e);
				try {
					conn.rollback();
				} catch (SQLException e1) {
					log.warn("rollback fail when creating all table", e1);
				}
				System.exit(1);
			} catch (Exception e) {
				log.error("2.creating table sql["+creatingTableSql+"] execution fail", e);
				try {
					conn.rollback();
				} catch (SQLException e1) {
					log.warn("rollback fail when creating all table", e1);
				}
				System.exit(1);
			}
		}
		/*
		try {
			conn.commit();
		} catch (SQLException e) {
			log.error(e.getMessage(), e);
			try {
				conn.rollback();
			} catch (SQLException e1) {
				log.warn("rollback fail when creating all table", e1);
			}
			System.exit(1);
		}*/
	}
	public void createTable(java.sql.Connection conn, String tableName, String sql)
			throws SQLException {
		Logger log = LoggerFactory.getLogger("kr.pe.sinnori.main");		
		
		log.info("1.{} table creattion sql=[{}]", tableName, sql);

		Statement stmt = null;

		stmt = conn.createStatement();

		stmt.executeUpdate(sql);
		
		log.info("2.{} table creattion sql success", tableName);
	}
	
	public void dropAllTables(java.sql.Connection conn) {
		Logger log = LoggerFactory.getLogger("kr.pe.sinnori.main");	
		
		/**
		 * 테이블 생성 순서 반대로 테이블 삭제
		 */
		for (int i=arrySqlCreatingTable.length-1; i >= 0; i--) {
			String tableName = arrySqlCreatingTable[i][0];
						
			try {
				dropTable(conn, tableName);
			} catch (SQLException e) {
				log.error("1.dropping table sql["+tableName+"] execution fail", e);
				try {
					conn.rollback();
				} catch (SQLException e1) {
					log.warn("rollback fail when creating all table", e1);
				}
				System.exit(1);
			} catch (Exception e) {
				log.error("2.dropping table sql["+tableName+"] execution fail", e);
				try {
					conn.rollback();
				} catch (SQLException e1) {
					log.warn("rollback fail when creating all table", e1);
				}
				System.exit(1);
			}
		}
	}
	public void dropTable(java.sql.Connection conn, String tableName)
			throws SQLException {
		Logger log = LoggerFactory.getLogger("kr.pe.sinnori.main");		
		
		String sql = new StringBuilder("DROP TABLE IF EXISTS `").append(tableName).append("`").toString();
		
		log.info("1.{} table drop sql=[{}]", tableName, sql);

		Statement stmt = null;

		stmt = conn.createStatement();

		stmt.executeUpdate(sql);
		
		log.info("2.{} table drop sql success", tableName);
	}
	
	public void executeUpdateAllSql(java.sql.Connection conn) {
		Logger log = LoggerFactory.getLogger("kr.pe.sinnori.main");	
		
		String arrySql[] = {
				getInsertingSqlForNoticeBoardInfo(),				
				getInsertingSqlForGeneralBoardInfo(),
				getInsertingSqlForUploadFileNameSequence()						
		};
		
		for (int i=0; i < arrySql.length; i++) {
						
			try {
				executeUpdateSql(conn, arrySql[i]);
			} catch (SQLException e) {
				log.error(e.getMessage(), e);
				try {
					conn.rollback();
				} catch (SQLException e1) {
					log.warn("rollback fail when creating all table", e1);
				}
				System.exit(1);
			} catch (Exception e) {
				log.error("unknown error", e);
				try {
					conn.rollback();
				} catch (SQLException e1) {
					log.warn("rollback fail when creating all table", e1);
				}
				System.exit(1);
			}
		}
	}
	
	public void executeUpdateSql(java.sql.Connection conn, String sql)
			throws SQLException {
		Logger log = LoggerFactory.getLogger("kr.pe.sinnori.main");		
		
		log.info("1.sql=[{}]", sql);

		Statement stmt = null;

		stmt = conn.createStatement();

		stmt.executeUpdate(sql);
		
		log.info("2. sql success");
	}

	@Override
	public void doStartingWork(
			ProjectPartConfigurationVO projectPartConfigurationVO) {
		Logger log = LoggerFactory.getLogger("kr.pe.sinnori.main");
		
		
		boolean isDropAllTable = false;
		String keyValueOfIsDropAlltable = System
				.getProperty(ServerCommonStaticFinalVars.JAVA_SYSTEM_PROPERTIES_KEY_IS_DROP_ALL_TABLE);
		
		if (null != keyValueOfIsDropAlltable) {
			 if (!keyValueOfIsDropAlltable.equals("true") && !keyValueOfIsDropAlltable.equals("false")) {
				 log.warn("this server's java system properties variable '{}' has 'true' or 'false', this value[{}] is wrong", 
						 ServerCommonStaticFinalVars.JAVA_SYSTEM_PROPERTIES_KEY_IS_DROP_ALL_TABLE, keyValueOfIsDropAlltable);
			 }
			 
			 if (keyValueOfIsDropAlltable.equals("true")) {
				 isDropAllTable = true;
			 } else if (keyValueOfIsDropAlltable.equals("false")) {
				 isDropAllTable = false;
			 } else {
				 log.error("this server's java system properties variable '{}' has 'true' or 'false', this value[{}] is wrong", 
						 ServerCommonStaticFinalVars.JAVA_SYSTEM_PROPERTIES_KEY_IS_DROP_ALL_TABLE, keyValueOfIsDropAlltable);
				 
				 System.exit(1);
			 }
			
		}	
		

		DBCPManager dbcpManager = DBCPManager.getInstance();
		BasicDataSource basicDataSource = null;

		try {
			basicDataSource = dbcpManager.getBasicDataSource("sample_base_db");
		} catch (DBNotReadyException e) {
			log.error("DB not ready", e);
			System.exit(1);
		}

		java.sql.Connection conn = null;

		try {

			conn = basicDataSource.getConnection();

			log.info("conn auto commit=[{}], change auto commit false",
					conn.getAutoCommit());

			conn.setAutoCommit(false);

			checkConnectionValidation(conn);
			
			if (isBoardTable(conn)) {				
				if (isDropAllTable) {
					dropAllTables(conn);
				}
				
				// FIXME!
				// dropAllTables(conn);
			} else  {
				createAllTables(conn);
				executeUpdateAllSql(conn);
			}
			

			conn.commit();

		} catch (SQLException e) {

			log.error("SQLException", e);
			System.exit(1);

		} finally {

			if (null != conn) {
				try {
					conn.close();
				} catch (SQLException e) {
					log.warn("when basicDataSource close, unknown error", e);
				}
			}
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
		stringBuilder.append("(`board_id`,");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("`board_name`,");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("`board_type_id`,");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("`board_info`)");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("VALUES");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("(1, '공지 게시판',");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("1,");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("'공지를 목적으로 하는 관리자 전용 게시판');");
		return stringBuilder.toString();
	}
	
	/**
	 * 
	 * @return 일반 게시판 정보 생성 쿼리
	 */
	public String getInsertingSqlForGeneralBoardInfo() {
		StringBuffer stringBuilder = new StringBuffer();
		stringBuilder.append("INSERT INTO `SB_BOARD_INFO_TB`");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("(`board_id`,");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("`board_name`,");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("`board_type_id`,");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("`board_info`)");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("VALUES");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("(2, '자유 게시판',");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("2,");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("'일반 회원 게시판');");
		return stringBuilder.toString();
	}

	/**	 * 
	 * @return 업로드 파일 이름에 쓰이는 시퀀스 생성 쿼리문
	 */
	public String getInsertingSqlForUploadFileNameSequence() {
		StringBuffer stringBuilder = new StringBuffer();
		stringBuilder.append("INSERT INTO `SB_SEQ_MANAGER_TB`");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("(`sq_type_id`,");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("`sq_value`)");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("VALUES");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("(1,");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("1);");
		return stringBuilder.toString();
	}
	
	public String getCreatingTableSqlForOA_MEMBER_TB() {
		StringBuffer stringBuilder = new StringBuffer();
	stringBuilder.append("CREATE TABLE IF NOT EXISTS `OA_MEMBER_TB` (");
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
	stringBuilder.append("CREATE TABLE IF NOT EXISTS `SB_SEQ_MANAGER_TB` (");
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
	stringBuilder.append("CREATE TABLE IF NOT EXISTS `SB_MEMBER_TB` (");
	stringBuilder.append(System.getProperty("line.separator"));
	stringBuilder.append("  `user_id` VARCHAR(20) NOT NULL COMMENT '사용자 아이디',");
	stringBuilder.append(System.getProperty("line.separator"));
	stringBuilder.append("  `nickname` VARCHAR(45) NOT NULL COMMENT '별명',");
	stringBuilder.append(System.getProperty("line.separator"));
	stringBuilder.append("  `pwd_base64` VARCHAR(88) NULL COMMENT '비밀번호, 비밀번호는 해쉬 값으로 변환되어 base64 형태로 저장된다.',");
	stringBuilder.append(System.getProperty("line.separator"));
	stringBuilder.append("  `pwd_salt_base64` VARCHAR(12) NULL COMMENT '비밀번호를 해쉬로 바꿀때 역 추적 방해를 목적으로 함께 사용하는 랜덤 값',");
	stringBuilder.append(System.getProperty("line.separator"));
	stringBuilder.append("  `member_gb` INT(11) NULL COMMENT '회원 구분, 0:관리자, 1:일반회원',");
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
	
	public String getCreatingTableSqlForSB_BOARD_INFO_TB() {
		StringBuffer stringBuilder = new StringBuffer();
	stringBuilder.append("CREATE TABLE IF NOT EXISTS `SB_BOARD_INFO_TB` (");
	stringBuilder.append(System.getProperty("line.separator"));
	stringBuilder.append("  `board_id` INT(11) UNSIGNED NOT NULL COMMENT '게시판 종류 식별자',");
	stringBuilder.append(System.getProperty("line.separator"));
	stringBuilder.append("  `board_name` VARCHAR(30) NULL COMMENT '게시판 이름',");
	stringBuilder.append(System.getProperty("line.separator"));
	stringBuilder.append("  `board_type_id` INT(11) UNSIGNED NOT NULL COMMENT '게시판 종류 식별자, 1:공지, 2:일반',");
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
	stringBuilder.append("CREATE TABLE IF NOT EXISTS `SB_BOARD_FILEINFO_TB` (");
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
	stringBuilder.append("  CONSTRAINT `fk_board_fileinfo_01`");
	stringBuilder.append(System.getProperty("line.separator"));
	stringBuilder.append("    FOREIGN KEY (`owner_id`)");
	stringBuilder.append(System.getProperty("line.separator"));
	stringBuilder.append("    REFERENCES `SB_MEMBER_TB` (`user_id`)");
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
	stringBuilder.append("CREATE TABLE IF NOT EXISTS `SB_BOARD_FILELIST_TB` (");
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
	stringBuilder.append("  CONSTRAINT `fk_tw_board_filelist_01`");
	stringBuilder.append(System.getProperty("line.separator"));
	stringBuilder.append("    FOREIGN KEY (`attach_id`)");
	stringBuilder.append(System.getProperty("line.separator"));
	stringBuilder.append("    REFERENCES `SB_BOARD_FILEINFO_TB` (`attach_id`)");
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
	stringBuilder.append("CREATE TABLE IF NOT EXISTS `SB_BOARD_TB` (");
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
	stringBuilder.append("  `board_id` INT UNSIGNED NOT NULL COMMENT '게시판 종류 식별자, 어떤 게시판인지 설명하는 게시판 정보(board_info) 테이블을 바라본다.',");
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
	stringBuilder.append("  CONSTRAINT `fk_tw_board_01`");
	stringBuilder.append(System.getProperty("line.separator"));
	stringBuilder.append("    FOREIGN KEY (`writer_id`)");
	stringBuilder.append(System.getProperty("line.separator"));
	stringBuilder.append("    REFERENCES `SB_MEMBER_TB` (`user_id`)");
	stringBuilder.append(System.getProperty("line.separator"));
	stringBuilder.append("    ON DELETE NO ACTION");
	stringBuilder.append(System.getProperty("line.separator"));
	stringBuilder.append("    ON UPDATE NO ACTION,");
	stringBuilder.append(System.getProperty("line.separator"));
	stringBuilder.append("  CONSTRAINT `fk_tw_board_02`");
	stringBuilder.append(System.getProperty("line.separator"));
	stringBuilder.append("    FOREIGN KEY (`board_id`)");
	stringBuilder.append(System.getProperty("line.separator"));
	stringBuilder.append("    REFERENCES `SB_BOARD_INFO_TB` (`board_id`)");
	stringBuilder.append(System.getProperty("line.separator"));
	stringBuilder.append("    ON DELETE NO ACTION");
	stringBuilder.append(System.getProperty("line.separator"));
	stringBuilder.append("    ON UPDATE NO ACTION,");
	stringBuilder.append(System.getProperty("line.separator"));
	stringBuilder.append("  CONSTRAINT `fk_tw_board_03`");
	stringBuilder.append(System.getProperty("line.separator"));
	stringBuilder.append("    FOREIGN KEY (`attach_id`)");
	stringBuilder.append(System.getProperty("line.separator"));
	stringBuilder.append("    REFERENCES `SB_BOARD_FILEINFO_TB` (`attach_id`)");
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
	stringBuilder.append("CREATE TABLE IF NOT EXISTS `SB_BOARD_VOTE_TB` (");
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
	stringBuilder.append("  CONSTRAINT `fk_tw_board_vote_01`");
	stringBuilder.append(System.getProperty("line.separator"));
	stringBuilder.append("    FOREIGN KEY (`board_no`)");
	stringBuilder.append(System.getProperty("line.separator"));
	stringBuilder.append("    REFERENCES `SB_BOARD_TB` (`board_no`)");
	stringBuilder.append(System.getProperty("line.separator"));
	stringBuilder.append("    ON DELETE NO ACTION");
	stringBuilder.append(System.getProperty("line.separator"));
	stringBuilder.append("    ON UPDATE NO ACTION,");
	stringBuilder.append(System.getProperty("line.separator"));
	stringBuilder.append("  CONSTRAINT `fk_tw_board_vote_02`");
	stringBuilder.append(System.getProperty("line.separator"));
	stringBuilder.append("    FOREIGN KEY (`user_id`)");
	stringBuilder.append(System.getProperty("line.separator"));
	stringBuilder.append("    REFERENCES `SB_MEMBER_TB` (`user_id`)");
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
