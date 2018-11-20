package kr.pe.codda.server.lib;

import static kr.pe.codda.impl.jooq.tables.SbBoardInfoTb.SB_BOARD_INFO_TB;
import static kr.pe.codda.impl.jooq.tables.SbBoardTb.SB_BOARD_TB;
import static kr.pe.codda.impl.jooq.tables.SbMemberTb.SB_MEMBER_TB;
import static kr.pe.codda.impl.jooq.tables.SbSeqTb.SB_SEQ_TB;

import java.nio.ByteBuffer;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.Timestamp;
import java.util.Arrays;
import java.util.Random;

import javax.sql.DataSource;

import kr.pe.codda.common.etc.CommonStaticFinalVars;
import kr.pe.codda.common.exception.DBCPDataSourceNotFoundException;
import kr.pe.codda.common.exception.ServerServiceException;
import kr.pe.codda.impl.task.server.MemberRegisterReqServerTask;
import kr.pe.codda.server.dbcp.DBCPManager;

import org.apache.commons.codec.binary.Base64;
import org.jooq.DSLContext;
import org.jooq.Record1;
import org.jooq.SQLDialect;
import org.jooq.conf.MappedSchema;
import org.jooq.conf.RenderMapping;
import org.jooq.conf.Settings;
import org.jooq.impl.DSL;
import org.jooq.types.UByte;
import org.jooq.types.UInteger;
import org.jooq.types.UShort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class ServerDBUtil {
	
	private static Settings DEFAULT_DBCP_SETTINGS = new Settings()
		    .withRenderMapping(new RenderMapping()
		    .withSchemata(
		        new MappedSchema().withInput(ServerCommonStaticFinalVars.DEFAULT_DBCP_NAME.toLowerCase())
		                          .withOutput(ServerCommonStaticFinalVars.DEFAULT_DBCP_NAME.toLowerCase())));
	
	private static Settings GENERAL_TEST_DBCP_SETTINGS = new Settings()
		    .withRenderMapping(new RenderMapping()
		    .withSchemata(
		        new MappedSchema().withInput(ServerCommonStaticFinalVars.DEFAULT_DBCP_NAME.toLowerCase())
		                          .withOutput(ServerCommonStaticFinalVars.GENERAL_TEST_DBCP_NAME.toLowerCase())));
	
	private static Settings LOAD_TEST_DBCP_SETTINGS = new Settings()
		    .withRenderMapping(new RenderMapping()
		    .withSchemata(
		        new MappedSchema().withInput(ServerCommonStaticFinalVars.DEFAULT_DBCP_NAME.toLowerCase())
		                          .withOutput(ServerCommonStaticFinalVars.LOAD_TEST_DBCP_NAME.toLowerCase())));
	

	public static void initializeDBEnvoroment(String dbcpName) throws Exception {
		Logger log = LoggerFactory.getLogger(ServerDBUtil.class);
		
		DataSource dataSource = null;
		
		try {
			dataSource = DBCPManager.getInstance().getBasicDataSource(dbcpName);		
		} catch(IllegalArgumentException | DBCPDataSourceNotFoundException e) {
			String errorMessage = e.getMessage();
			
			log.warn(errorMessage, e);		
			
			/** 지정한 이름의 dbcp 를 받지 못할 경우 아무 처리도 하지 않는다 */
			return;
		}

		Connection conn = null;
		try {
			conn = dataSource.getConnection();
			conn.setAutoCommit(true);

			DSLContext create = DSL.using(conn, SQLDialect.MYSQL, ServerDBUtil.getDBCPSettings(dbcpName));
			
			insertAllBoardIDIfNotExist(create);
			
			insertAllSeqIDIfNotExist(create);
			
		} catch (Exception e) {
			log.warn("error", e);
			
			if (null != conn) {
				try {
					conn.rollback();
				} catch (Exception e1) {
					log.warn("fail to rollback");
				}
			}
			
			throw e;
		} finally {
			if (null != conn) {
				try {
					conn.close();
				} catch (Exception e) {
					log.warn("fail to close the db connection", e);
				}
			}
		}
		
		
		/*String nickname = "테스트어드민";
		String pwdHint = "비밀번호힌트::그것이 알고싶다";
		String pwdAnswer = "비밀번호답변::";
		// 비밀번호는 영문으로 시작해서 영문/숫자/특수문자가 최소 1자이상 조합되어야 한다 
		byte[] passwordBytes = {(byte)'t', (byte)'e', (byte)'s', (byte)'t', (byte)'1', (byte)'2', (byte)'3', (byte)'4', (byte)'$'}; 
		
		try {
			registerMember(MemberType.ADMIN, adminID, nickname, pwdHint, pwdAnswer, passwordBytes);		
		} catch (Exception e) {
			log.warn("unknown error", e);
			throw e;
		}*/
		
	}
		
	/**
	 * 회원 종류에 따른 회원 등록을 수행한다.	어드민과 일반 회원 등록 관리를 일원화 시킬 목적으로 '회원 등록 서버 서버 타스크'(={@link MemberRegisterReqServerTask})가 아닌 이곳 서버 라이브러리에서 관리한다. 
	 * 
	 * @param dbcpName dbcp 이름(=db schema)
	 * @param memberType 회원 종류로 어드민과 일반 유저가 있다 
	 * @param userID 등록을 원하는 아이디
	 * @param nickname 별명
	 * @param pwdHint 패스워드 힌트 질문
	 * @param pwdAnswer 패스워드 답변
	 * @param passwordBytes 패스워드
	 * @throws Exception
	 */
	public static void registerMember(String dbcpName, MemberType memberType, String userID, String nickname, 
			String pwdHint, String pwdAnswer, byte[] passwordBytes, String ip) throws Exception {
		Logger log = LoggerFactory.getLogger(ServerDBUtil.class);
		
		if (null == memberType) {
			String errorMessage = "the parameter memberType is null";
			throw new ServerServiceException(errorMessage);
		}		
		
		try {
			ValueChecker.checkValidUserID(userID);
		} catch (IllegalArgumentException e) {
			throw new ServerServiceException(e.getMessage());
		}
		
		try {
			ValueChecker.checkValidNickname(nickname);
		} catch (IllegalArgumentException e) {
			throw new ServerServiceException(e.getMessage());
		}
		
		try {
			ValueChecker.checkValidPwdHint(pwdHint);
		} catch (RuntimeException e) {
			throw new ServerServiceException(e.getMessage());
		}		
		
		try {
			ValueChecker.checkValidPwdAnswer(pwdAnswer);
		} catch (RuntimeException e) {
			throw new ServerServiceException(e.getMessage());
		}
		
		try {
			ValueChecker.checkValidPwd(passwordBytes);
		} catch (IllegalArgumentException e) {
			throw new ServerServiceException(e.getMessage());
		}
		
		Random random = new Random();
		byte[] pwdSaltBytes = new byte[8];
		random.nextBytes(pwdSaltBytes);
		
		PasswordPairOfMemberTable passwordPairOfMemberTable = toPasswordPairOfMemberTable(passwordBytes, pwdSaltBytes);

		DataSource dataSource = DBCPManager.getInstance()
				.getBasicDataSource(dbcpName);		
		
		Connection conn = null;
		try {
			conn = dataSource.getConnection();
			conn.setAutoCommit(false);

			DSLContext create = DSL.using(conn, SQLDialect.MYSQL, ServerDBUtil.getDBCPSettings(dbcpName));

			boolean isSameIDMember = create
					.fetchExists(create.select().from(SB_MEMBER_TB).where(SB_MEMBER_TB.USER_ID.eq(userID)));

			if (isSameIDMember) {
				try {
					conn.rollback();
				} catch (Exception e) {
					log.warn("fail to rollback");
				}

				String errorMessage = new StringBuilder("기존 회원과 중복되는 아이디[").append(userID).append("] 입니다").toString();
				throw new ServerServiceException(errorMessage);
			}

			boolean isSameNicknameMember = create
					.fetchExists(create.select().from(SB_MEMBER_TB).where(SB_MEMBER_TB.NICKNAME.eq(nickname)));

			if (isSameNicknameMember) {
				try {
					conn.rollback();
				} catch (Exception e) {
					log.warn("fail to rollback");
				}

				String errorMessage = new StringBuilder("기존 회원과 중복되는 별명[").append(nickname).append("] 입니다").toString();
				throw new ServerServiceException(errorMessage);
			}

			int resultOfInsert = create.insertInto(SB_MEMBER_TB).set(SB_MEMBER_TB.USER_ID, userID)
					.set(SB_MEMBER_TB.NICKNAME, nickname).set(SB_MEMBER_TB.PWD_BASE64, passwordPairOfMemberTable.getPasswordBase64())
					.set(SB_MEMBER_TB.PWD_SALT_BASE64, passwordPairOfMemberTable.getPasswordSaltBase64())
					.set(SB_MEMBER_TB.MEMBER_TYPE, memberType.getValue())
					.set(SB_MEMBER_TB.MEMBER_ST, MemberStateType.OK.getValue()).set(SB_MEMBER_TB.PWD_HINT, pwdHint)
					.set(SB_MEMBER_TB.PWD_ANSWER, pwdAnswer).set(SB_MEMBER_TB.PWD_FAIL_CNT, UByte.valueOf(0))
					.set(SB_MEMBER_TB.REG_DT, JooqSqlUtil.getFieldOfSysDate(Timestamp.class))
					.set(SB_MEMBER_TB.MOD_DT, SB_MEMBER_TB.REG_DT)
					.set(SB_MEMBER_TB.IP, ip)
					.execute();

			if (0 == resultOfInsert) {
				try {
					conn.rollback();
				} catch (Exception e) {
					log.warn("fail to rollback");
				}

				String errorMessage = "회원 등록하는데 실패하였습니다";
				throw new ServerServiceException(errorMessage);
			}

			try {
				conn.commit();
			} catch (Exception e) {
				log.warn("fail to commit");
			}

			
		} catch (ServerServiceException e) {
			throw e;
		} catch (Exception e) {
			if (null != conn) {
				try {
					conn.rollback();
				} catch (Exception e1) {
					log.warn("fail to rollback");
				}
			}

			log.warn("unknown error", e);

			throw e;
		} finally {
			if (null != conn) {
				try {
					conn.close();
				} catch (Exception e) {
					log.warn("fail to close the db connection", e);
				}
			}
		}
	}
	
	/**
	 * <pre>
	 * 회원 테이블 '비밀번호' 필드와 '비밀번호 소금' 필드 값 묶음을 반환한다. 
	 * 
	 * 회원 테이블 '비밀번호' 필드 값은 base64로 인코딩한 비밀번호 해쉬 값이다.
	 * 회원 테이블  '비밀번호 소금' 필드 값은 base64로 인코딩한 소금값으로 
	 * 	비밀번호 역추적을 어렵게 하기 위해 목적을 갖는다. 
	 * 
	 * WARNING! 파라미터 유효성 검사를 수행하지 않기때문에 사용에 주의가 필요합니다. 보안상 매우 중요한 로직이지만 아직 보안에 대한 대비책이 없습니다.
	 * FIXME! 보안상 비밀번호 만드는 방법은 노출되어서는 안되므로 git 에도 반영하지 않고 따로 관리할 필요가 있다.  
	 * </pre>
	 * 
	 * @param passwordBytes 사용자가 입력한  비밀번호
	 * @param pwdSaltBytes 비밀번호 해쉬에 사용할 소금
	 * @return
	 * @throws NoSuchAlgorithmException
	 */
	public static PasswordPairOfMemberTable toPasswordPairOfMemberTable(byte[] passwordBytes, byte[] pwdSaltBytes) throws NoSuchAlgorithmException {		
		MessageDigest md = MessageDigest.getInstance(CommonStaticFinalVars.PASSWORD_ALGORITHM_NAME);
		
		ByteBuffer passwordByteBuffer = ByteBuffer.allocate(pwdSaltBytes.length + passwordBytes.length);
		passwordByteBuffer.put(pwdSaltBytes);
		passwordByteBuffer.put(passwordBytes);

		md.update(passwordByteBuffer.array());

		byte passwordMDBytes[] = md.digest();

		/** 복호환 비밀번호 초기화 */
		Arrays.fill(passwordBytes, CommonStaticFinalVars.ZERO_BYTE);		
		
		return new PasswordPairOfMemberTable(Base64.encodeBase64String(passwordMDBytes), Base64.encodeBase64String(pwdSaltBytes));
	}

	private static void insertAllSeqIDIfNotExist(DSLContext create) throws Exception {
		for (SequenceType sequenceTypeValue : SequenceType.values()) {
			boolean exists = create.fetchExists(create.select(SB_SEQ_TB.SQ_ID)
					.from(SB_SEQ_TB)
					.where(SB_SEQ_TB.SQ_ID
							.eq(UByte.valueOf(sequenceTypeValue.getSequenceID()))));
			
			if (! exists) {
				int countOfInsert = create.insertInto(SB_SEQ_TB)
				.set(SB_SEQ_TB.SQ_ID, UByte.valueOf(sequenceTypeValue.getSequenceID()))
				.set(SB_SEQ_TB.SQ_NAME, sequenceTypeValue.getName())
				.set(SB_SEQ_TB.SQ_VALUE, UInteger.valueOf(1))
				.execute();
				
				if (0 == countOfInsert) {
					String errorMessage = new StringBuilder()
							.append("fail to insert the sequence(id:")
							.append(sequenceTypeValue.getSequenceID())
							.append(", name:")
							.append(sequenceTypeValue.getName())
							.append(")").toString();
					throw new Exception(errorMessage);
				}
			}	
		}
	}

	private static void insertAllBoardIDIfNotExist(DSLContext create) throws Exception {
		for (BoardType boardType : BoardType.values()) {
			UByte boardTypeID = UByte.valueOf(boardType.getBoardID());
			
			boolean exists = create.fetchExists(create.select(SB_BOARD_INFO_TB.BOARD_ID)
					.from(SB_BOARD_INFO_TB)
					.where(SB_BOARD_INFO_TB.BOARD_ID
							.eq(boardTypeID)));

			if (! exists) {
				String boardInfo = new StringBuilder(boardType.getName())
				.append(" 게시판").toString();
				
				int countOfInsert = create.insertInto(SB_BOARD_INFO_TB)
				.set(SB_BOARD_INFO_TB.BOARD_ID, boardTypeID)
				.set(SB_BOARD_INFO_TB.BOARD_NAME, boardType.getName())
				.set(SB_BOARD_INFO_TB.BOARD_INFO, boardInfo)
				.set(SB_BOARD_INFO_TB.ADMIN_TOTAL, 0)
				.set(SB_BOARD_INFO_TB.USER_TOTAL, 0)
				.execute();
				
				if (0 == countOfInsert) {
					String errorMessage = new StringBuilder().append(boardInfo)
							.append(" 식별자 삽입 실패").toString();
					throw new Exception(errorMessage);
				}				
			}
		}
	}
	
	public static Settings getDBCPSettings(String dbcpName) {
		if (dbcpName.equals(ServerCommonStaticFinalVars.GENERAL_TEST_DBCP_NAME)) {
			return GENERAL_TEST_DBCP_SETTINGS;
		} else if (dbcpName.equals(ServerCommonStaticFinalVars.LOAD_TEST_DBCP_NAME)) {
			return LOAD_TEST_DBCP_SETTINGS;
		}
		
		return DEFAULT_DBCP_SETTINGS;
	}
	
	/*public static UShort getToGroupSeqOfRelativeRootBoard(DSLContext create, 
			UByte boardID, 
			UInteger groupNo, UShort groupSeq, UByte depth) {
		Result<Record2<UShort, UByte>> 
		childBoardResult = create.select(SB_BOARD_TB.GROUP_SQ, SB_BOARD_TB.DEPTH)
		.from(SB_BOARD_TB)
		.where(SB_BOARD_TB.BOARD_ID.eq(boardID))				
		.and(SB_BOARD_TB.GROUP_NO.eq(groupNo))
		.and(SB_BOARD_TB.GROUP_SQ.lt(groupSeq))
		.orderBy(SB_BOARD_TB.GROUP_SQ.desc())
		.fetch();
		
		UShort toGroupSeq = groupSeq;
		
		for (Record2<UShort, UByte> childBoardRecord : childBoardResult) {
			UShort childGroupSeq  = childBoardRecord.getValue(SB_BOARD_TB.GROUP_SQ);
			UByte childDepth = childBoardRecord.getValue(SB_BOARD_TB.DEPTH);
			
			if (childDepth.shortValue() <= depth.shortValue()) {
				break;
			}
			
			toGroupSeq = childGroupSeq;			
		}
		
		return toGroupSeq;
	}*/
	
	public static UShort getToGroupSeqOfRelativeRootBoard(DSLContext create, 
			UByte boardID, 
			UInteger groupNo, 
			UShort groupSq, 
			UInteger directParentNo) throws ServerServiceException {
		
		while(true) {
			if (0 == directParentNo.longValue()) {
				return UShort.valueOf(0);
			}
			
			Record1<UShort> 
			nearestGroupSeqBoardRecord = create.select(SB_BOARD_TB.GROUP_SQ.max().as(SB_BOARD_TB.GROUP_SQ))
			.from(SB_BOARD_TB)
			.where(SB_BOARD_TB.BOARD_ID.eq(boardID))				
			.and(SB_BOARD_TB.GROUP_NO.eq(groupNo))
			.and(SB_BOARD_TB.GROUP_SQ.lt(groupSq))
			.and(SB_BOARD_TB.PARENT_NO.eq(directParentNo))
			.fetchOne();
			
			if (null == nearestGroupSeqBoardRecord || null == nearestGroupSeqBoardRecord.getValue(SB_BOARD_TB.GROUP_SQ)) {
				Record1<UInteger> parnetBoardRecord = create.select(SB_BOARD_TB.PARENT_NO)
				.from(SB_BOARD_TB)
				.where(SB_BOARD_TB.BOARD_ID.eq(boardID))
				.and(SB_BOARD_TB.BOARD_NO.eq(directParentNo))
				.fetchOne();
				
				if (null == parnetBoardRecord) {
					String errorMessage = new StringBuilder()
					.append("직계 조상 게시글[boardID=")
					.append(boardID)
					.append(", boardNo=")
					.append(directParentNo)
					.append("]이 없습니다").toString();
					throw new ServerServiceException(errorMessage);
				}
				
				directParentNo = parnetBoardRecord.getValue(SB_BOARD_TB.PARENT_NO);
				
				continue;
			}
			
			UShort nearestGroupSeq = nearestGroupSeqBoardRecord.getValue(SB_BOARD_TB.GROUP_SQ);
			
			UShort toGroupSeq = UShort.valueOf(nearestGroupSeq.intValue() + 1);			
			
			return toGroupSeq;
		}
		
	}
}
