package kr.pe.sinnori.server.lib;

import kr.pe.sinnori.common.type.BoradType;
import kr.pe.sinnori.common.type.SequenceType;

public class SampleBaseDB extends AbstractDBCommand {
	private final String dbcpConnectionPoolName = "sample_base_db";
	
	public String getDBCPConnectionPoolName() {
		return dbcpConnectionPoolName;
	}
	
	
	public SampleBaseDB() {
		super();
		try {
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
		stringBuilder.append(BoradType.NOTICE.getBoradTypeID());
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
		stringBuilder.append(BoradType.FREE.getBoradTypeID());
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
		stringBuilder.append(BoradType.FAQ.getBoradTypeID());
		stringBuilder.append(", 'FAQ 게시판', '신놀이 관련 질문답 게시판');");
		return stringBuilder.toString();
	}

	/**	 * 
	 * @return 업로드 파일 이름에 쓰이는 시퀀스 생성 쿼리문
	 */
	public String getInsertingSqlForUploadFileNameSequence() {
		// INSERT INTO `SB_DB`.`SB_SEQ_MANAGER_TB` (`sq_type_id`, `sq_value`, `sq_type_name`) VALUES (0, 1,  'upload file name sequence');
		StringBuffer stringBuilder = new StringBuffer();
		stringBuilder.append("INSERT INTO `SB_SEQ_MANAGER_TB`");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("(`sq_type_id`, `sq_value`, `sq_type_name`)");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("VALUES");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("(");
		stringBuilder.append(SequenceType.UPLOAD_FILE_NAME.getSequenceTypeID());
		stringBuilder.append(", 1, 'upload file name sequence');");
		return stringBuilder.toString();
	}
}
