package kr.pe.codda.server.lib;

import static kr.pe.codda.impl.jooq.tables.SbBoardInfoTb.SB_BOARD_INFO_TB;
import static kr.pe.codda.impl.jooq.tables.SbMemberTb.SB_MEMBER_TB;
import static kr.pe.codda.impl.jooq.tables.SbSeqTb.SB_SEQ_TB;

import java.sql.Connection;
import java.util.Arrays;

import javax.sql.DataSource;

import org.apache.commons.codec.binary.Base64;
import org.jooq.DSLContext;
import org.jooq.SQLDialect;
import org.jooq.impl.DSL;
import org.jooq.types.UByte;
import org.jooq.types.UInteger;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import kr.pe.codda.common.etc.CommonStaticFinalVars;
import kr.pe.codda.common.exception.ServerServiceException;
import kr.pe.codda.common.exception.SymmetricException;
import kr.pe.codda.common.sessionkey.ClientSessionKeyIF;
import kr.pe.codda.common.sessionkey.ClientSessionKeyManager;
import kr.pe.codda.common.sessionkey.ClientSymmetricKeyIF;
import kr.pe.codda.common.sessionkey.ServerSessionkeyManager;
import kr.pe.codda.impl.message.MemberRegisterReq.MemberRegisterReq;
import kr.pe.codda.impl.message.MessageResultRes.MessageResultRes;
import kr.pe.codda.impl.task.server.MemberRegisterReqServerTask;
import kr.pe.codda.server.dbcp.DBCPManager;

public abstract class ServerDBUtil {

	public static void initializeDBEnvoroment(String adminID) throws Exception {
		Logger log = LoggerFactory.getLogger(ServerDBUtil.class);
		
		DataSource dataSource = DBCPManager.getInstance()
				.getBasicDataSource(ServerCommonStaticFinalVars.SB_CONNECTION_POOL_NAME);

		Connection conn = null;
		try {
			conn = dataSource.getConnection();
			conn.setAutoCommit(true);

			DSLContext create = DSL.using(conn, SQLDialect.MYSQL);
			
			insertAllBoardIDIfNotExist(create);
			
			insertAllSeqIDIfNotExist(create);	
			
			byte[] passwordBytes = {(byte)'t', (byte)'e', (byte)'s', (byte)'t', (byte)'1', (byte)'2', (byte)'3', (byte)'4', (byte)'$'};
			createUser(adminID, "테스트어드민", passwordBytes, "비밀번호힌트::그것이 알고싶다", "비밀번호답변::");
			
			create.update(SB_MEMBER_TB).set(SB_MEMBER_TB.MEMBER_TYPE, MemberType.ADMIN.getValue())
			.where(SB_MEMBER_TB.USER_ID.eq(adminID)).execute();
			
		} catch (Exception e) {
			try {
				conn.rollback();
			} catch (Exception e1) {
				log.warn("fail to rollback");
			}
			
			log.warn("error", e);
			
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
	
	private static void createUser(String userID, String nickname, byte[] passwordBytes, String pwdHint, String pwdAnswer) throws Exception {
		Logger log = LoggerFactory.getLogger(ServerDBUtil.class);
		
		
		ServerSessionkeyManager serverSessionkeyManager = ServerSessionkeyManager.getInstance();
		
		ClientSessionKeyManager clientSessionKeyManager = ClientSessionKeyManager.getInstance();
		ClientSessionKeyIF clientSessionKey = null;
		try {
			clientSessionKey = clientSessionKeyManager
					.getNewClientSessionKey(serverSessionkeyManager.getMainProjectServerSessionkey().getDupPublicKeyBytes());
		} catch (SymmetricException e) {
			throw e;
		}
		
		ClientSymmetricKeyIF clientSymmetricKey = clientSessionKey.getClientSymmetricKey();
		
		byte[] idCipherTextBytes = null;
		try {
			idCipherTextBytes = clientSymmetricKey.encrypt(userID.getBytes(CommonStaticFinalVars.CIPHER_CHARSET));
		} catch (Exception e) {
			throw e;
		}
		byte[] passwordCipherTextBytes = null;
		
		try {
			passwordCipherTextBytes = clientSymmetricKey.encrypt(passwordBytes);
		} catch (Exception e) {
			throw e;
		}
		
		Arrays.fill(passwordBytes, CommonStaticFinalVars.ZERO_BYTE);
		
		byte[] nicknameCipherTextBytes = null;
		try {
			nicknameCipherTextBytes = clientSymmetricKey.encrypt(nickname.getBytes(CommonStaticFinalVars.CIPHER_CHARSET));
		} catch (Exception e) {
			throw e;
		}
		
		byte[] pwdHintCipherTextBytes = null;
		try {
			pwdHintCipherTextBytes = clientSymmetricKey.encrypt(pwdHint.getBytes(CommonStaticFinalVars.CIPHER_CHARSET));
		} catch (Exception e) {
			throw e;
		}
		
		byte[] pwdAnswerCipherTextBytes = null;
		try {
			pwdAnswerCipherTextBytes = clientSymmetricKey.encrypt(pwdAnswer.getBytes(CommonStaticFinalVars.CIPHER_CHARSET));
		} catch (Exception e) {
			throw e;
		}
		
		MemberRegisterReq memberRegisterReq = new MemberRegisterReq();
		memberRegisterReq.setIdCipherBase64(Base64.encodeBase64String(idCipherTextBytes));
		memberRegisterReq.setPwdCipherBase64(Base64.encodeBase64String(passwordCipherTextBytes));
		memberRegisterReq.setNicknameCipherBase64(Base64.encodeBase64String(nicknameCipherTextBytes));
		memberRegisterReq.setHintCipherBase64(Base64.encodeBase64String(pwdHintCipherTextBytes));
		memberRegisterReq.setAnswerCipherBase64(Base64.encodeBase64String(pwdAnswerCipherTextBytes));
		memberRegisterReq.setSessionKeyBase64(Base64.encodeBase64String(clientSessionKey.getDupSessionKeyBytes()));
		memberRegisterReq.setIvBase64(Base64.encodeBase64String(clientSessionKey.getDupIVBytes()));
	
		MemberRegisterReqServerTask memberRegisterReqServerTask= new MemberRegisterReqServerTask();
		
		try {
			@SuppressWarnings("unused")
			MessageResultRes messageResultRes = 
					memberRegisterReqServerTask.doService(memberRegisterReq);
		} catch (ServerServiceException e) {
			String expectedErrorMessage = new StringBuilder("기존 회원과 중복되는 아이디[")
					.append(userID)
					.append("] 입니다").toString();
			String actualErrorMessag = e.getMessage();
			
			if (! expectedErrorMessage.equals(actualErrorMessag)) {
				throw e;
			}
			
		} catch (Exception e) {
			log.warn("unknown error", e);
			throw e;
		}
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
				.execute();
				
				if (0 == countOfInsert) {
					String errorMessage = new StringBuilder().append(boardInfo)
							.append(" 식별자 삽입 실패").toString();
					throw new Exception(errorMessage);
				}				
			}
		}
	}
}
