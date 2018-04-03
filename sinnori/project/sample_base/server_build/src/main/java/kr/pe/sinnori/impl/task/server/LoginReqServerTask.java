package kr.pe.sinnori.impl.task.server;


import static kr.pe.sinnori.impl.jooq.tables.SbMemberTb.SB_MEMBER_TB;

import java.nio.ByteBuffer;
import java.security.MessageDigest;
import java.sql.Connection;
import java.sql.Timestamp;
import java.util.Arrays;

import javax.sql.DataSource;

import org.apache.commons.codec.binary.Base64;
import org.jooq.DSLContext;
import org.jooq.Record;
import org.jooq.SQLDialect;
import org.jooq.impl.DSL;
import org.jooq.types.UByte;

import kr.pe.sinnori.common.etc.CommonStaticFinalVars;
import kr.pe.sinnori.common.etc.DBCPManager;
import kr.pe.sinnori.common.exception.SymmetricException;
import kr.pe.sinnori.common.message.AbstractMessage;
import kr.pe.sinnori.common.sessionkey.ServerSessionkeyIF;
import kr.pe.sinnori.common.sessionkey.ServerSessionkeyManager;
import kr.pe.sinnori.common.sessionkey.ServerSymmetricKeyIF;
import kr.pe.sinnori.impl.message.LoginReq.LoginReq;
import kr.pe.sinnori.impl.message.MessageResultRes.MessageResultRes;
import kr.pe.sinnori.server.PersonalLoginManagerIF;
import kr.pe.sinnori.server.lib.JooqSqlUtil;
import kr.pe.sinnori.server.lib.MemberStateType;
import kr.pe.sinnori.server.lib.MembershipLevel;
import kr.pe.sinnori.server.lib.ServerCommonStaticFinalVars;
import kr.pe.sinnori.server.lib.ValueChecker;
import kr.pe.sinnori.server.task.AbstractServerTask;
import kr.pe.sinnori.server.task.ToLetterCarrier;

public class LoginReqServerTask extends AbstractServerTask {
	private void sendErrorOutputMessageForCommit(String errorMessage,
			Connection conn,			
			ToLetterCarrier toLetterCarrier,
			AbstractMessage inputMessage) throws InterruptedException {		
		try {
			conn.commit();
		} catch (Exception e) {
			log.warn("fail to commit");
		}
		sendErrorOutputMessage(errorMessage, toLetterCarrier, inputMessage);
	}
	
	private void sendErrorOutputMessageForRollback(String errorMessage,
			Connection conn,			
			ToLetterCarrier toLetterCarrier,
			AbstractMessage inputMessage) throws InterruptedException {
		if (null != conn) {
			try {
				conn.rollback();
			} catch (Exception e) {
				log.warn("fail to rollback");
			}
		}		
		sendErrorOutputMessage(errorMessage, toLetterCarrier, inputMessage);
	}
	private void sendErrorOutputMessage(String errorMessage,			
			ToLetterCarrier toLetterCarrier,
			AbstractMessage inputMessage) throws InterruptedException {
		log.warn("{}, inObj=", errorMessage, inputMessage.toString());
		
		MessageResultRes messageResultRes = new MessageResultRes();
		messageResultRes.setTaskMessageID(inputMessage.getMessageID());
		messageResultRes.setIsSuccess(false);		
		messageResultRes.setResultMessage(errorMessage);
		toLetterCarrier.addSyncOutputMessage(messageResultRes);
	}
	
	private void sendSuccessOutputMessageForCommit(AbstractMessage outputMessage, Connection conn,
			ToLetterCarrier toLetterCarrier) throws InterruptedException {		
		try {
			conn.commit();
		} catch (Exception e) {
			log.warn("fail to commit");
		}
		
		toLetterCarrier.addSyncOutputMessage(outputMessage);
	}
	
	private String getDecryptedString(byte[] cipherBytes, ServerSymmetricKeyIF serverSymmetricKey)
			throws InterruptedException, IllegalArgumentException, SymmetricException {		
		byte[] valueBytes = serverSymmetricKey.decrypt(cipherBytes);
		String decryptedString = new String(valueBytes, CommonStaticFinalVars.SINNORI_CIPHER_CHARSET);
		return decryptedString;
	}

	@Override
	public void doTask(String projectName, 
			PersonalLoginManagerIF personalLoginManager, 
			ToLetterCarrier toLetterCarrier,
			AbstractMessage inputMessage) throws Exception {
		doWork(projectName, personalLoginManager, toLetterCarrier, (LoginReq)inputMessage);
	}
	
	
	public void doWork(String projectName, 
			PersonalLoginManagerIF personalLoginManager, 
			ToLetterCarrier toLetterCarrier,
			LoginReq loginReq) throws Exception {
		// FIXME!
		log.info(loginReq.toString());
		
		
		
		String idCipherBase64 = loginReq.getIdCipherBase64();		
		String pwdCipherBase64 = loginReq.getPwdCipherBase64();		
		String sessionKeyBase64 = loginReq.getSessionKeyBase64();		
		String ivBase64 = loginReq.getIvBase64();
		
		if (null == idCipherBase64) {
			sendErrorOutputMessage("아이디를 입력해 주세요", toLetterCarrier, loginReq);
			return;
		}
		
		if (null == pwdCipherBase64) {
			sendErrorOutputMessage("비밀번호를 입력해 주세요", toLetterCarrier, loginReq);
			return;
		}
		
		if (null == sessionKeyBase64) {
			sendErrorOutputMessage("세션키를 입력해 주세요", toLetterCarrier, loginReq);
			return;
		}
		
		if (null == ivBase64) {
			sendErrorOutputMessage("세션키 소금값을 입력해 주세요", toLetterCarrier, loginReq);
			return;
		}
		
		byte[] idCipherBytes = null;
		byte[] pwdCipherBytes = null;
		byte[] sessionKeyBytes = null;
		byte[] ivBytes = null;
		
		try {
			idCipherBytes = Base64.decodeBase64(idCipherBase64);
		} catch(Exception e) {
			sendErrorOutputMessage("아이디 암호문은 base64 인코딩되지 않았습니다", toLetterCarrier, loginReq);
			return;
		}
		
		try {
			pwdCipherBytes = Base64.decodeBase64(pwdCipherBase64);
		} catch(Exception e) {
			sendErrorOutputMessage("비밀번호 암호문은 base64 인코딩되지 않았습니다", toLetterCarrier, loginReq);
			return;
		}
		try {
			sessionKeyBytes = Base64.decodeBase64(sessionKeyBase64);
		} catch(Exception e) {
			sendErrorOutputMessage("세션키는 base64 인코딩되지 않았습니다", toLetterCarrier, loginReq);
			return;
		}
		
		try {
			ivBytes = Base64.decodeBase64(ivBase64);
		} catch(Exception e) {
			sendErrorOutputMessage("세션키 소금값은 base64 인코딩되지 않았습니다", toLetterCarrier, loginReq);
			return;
		}
		
		ServerSymmetricKeyIF serverSymmetricKey = null;
		try {
			ServerSessionkeyIF serverSessionkey = ServerSessionkeyManager.getInstance().getMainProjectServerSessionkey();
			
			serverSymmetricKey = serverSessionkey.getNewInstanceOfServerSymmetricKey(sessionKeyBytes, ivBytes);
					
		} catch (IllegalArgumentException e) {
			String errorMessage = "서버 세션키를 얻는데 실패하였습니다";
			log.warn(errorMessage, e);
			sendErrorOutputMessage(errorMessage, toLetterCarrier, loginReq);
			return;
		} catch (SymmetricException e) {
			String errorMessage = "서버 세션키를 얻는데 실패하였습니다";
			log.warn(loginReq.toString(), e);
			sendErrorOutputMessage(errorMessage, toLetterCarrier, loginReq);
			return;
		}
		
		String userId = null;		
		
		try {
			userId = getDecryptedString(idCipherBytes, serverSymmetricKey);
		} catch (IllegalArgumentException e) {
			String errorMessage = "아이디에 대한 복호문을 얻는데 실패하였습니다";
			log.warn(errorMessage, e);
			sendErrorOutputMessage(errorMessage, toLetterCarrier, loginReq);
			return;
		} catch (SymmetricException e) {
			String errorMessage = "아이디에 대한 복호문을 얻는데 실패하였습니다";
			log.warn(loginReq.toString(), e);
			sendErrorOutputMessage(errorMessage, toLetterCarrier, loginReq);
			return;
		}
		
		try {
			ValueChecker.checkValidUserId(userId);
		} catch(IllegalArgumentException e) {						
			sendErrorOutputMessage(e.getMessage(), toLetterCarrier, loginReq);
			return;
		}
		
		/*if (null== userId) {
			log.warn("아이디 복호문 값을 얻는데 실패하였습니다. inObj=[{}]", sessionKeyLoginReq.toString());			
			messageResultRes.setResultMessage("아이디 복호문 값을 얻는데 실패하였습니다");

			toLetterCarrier.addSyncOutputMessage(messageResultRes);
			return;
		}*/
		
		
		DataSource dataSource = DBCPManager.getInstance()
				.getBasicDataSource(ServerCommonStaticFinalVars.SB_CONNECTION_POOL_NAME);

		Connection conn = null;
		try {
			conn = dataSource.getConnection();
			conn.setAutoCommit(false);
			
			DSLContext create = DSL.using(conn, SQLDialect.MYSQL);
			
			
			
			/*
			select 
			user_id as userId, 
			nickname,
			pwd_base64 as pwdBase64,
			pwd_salt_base64 as pwdSaltBase64,
			member_gb as memberGubun,
			member_st as memberState,
			pwd_hint as pwdHint,
			pwd_answer as pwdAnswer,
			pwd_fail_cnt as pwdFailCount,
			reg_dt as registerDate,
			mod_dt as modifiedDate
			from SB_MEMBER_TB where user_id=#{userId} for update
			*/
			
			/*
			update SB_MEMBER_TB set pwd_fail_cnt=#{pwdFailCount}, mod_dt=sysdate() where user_id=#{userId} and member_gb=1 and member_st=0
			*/
			
			Record resultOfMember = create.select(
					SB_MEMBER_TB.LEVEL,
					SB_MEMBER_TB.MEMBER_ST,
					SB_MEMBER_TB.PWD_FAIL_CNT,
					SB_MEMBER_TB.PWD_BASE64,
					SB_MEMBER_TB.PWD_SALT_BASE64)
				.from(SB_MEMBER_TB)
				.where(SB_MEMBER_TB.USER_ID.eq(userId))
				.forUpdate().fetchOne();
			
			if (null == resultOfMember) {
				String errorMessage = new StringBuilder("아이디[")
						.append(userId)
						.append("]가 존재하지 않습니다").toString();
				
				sendErrorOutputMessageForCommit(errorMessage, conn, toLetterCarrier, loginReq);
				return;
			}
			
			byte nativeMembershipLevel = resultOfMember.get(SB_MEMBER_TB.LEVEL);
			String memberState = resultOfMember.get(SB_MEMBER_TB.MEMBER_ST);
			short pwdFailedCount = resultOfMember.get(SB_MEMBER_TB.PWD_FAIL_CNT).shortValue();
			String pwdMDBase64 =  resultOfMember.get(SB_MEMBER_TB.PWD_BASE64);
			String pwdSaltBase64 = resultOfMember.get(SB_MEMBER_TB.PWD_SALT_BASE64);			
			
			try {
				@SuppressWarnings("unused")
				MembershipLevel membershipLevel = MembershipLevel.valueOf(nativeMembershipLevel);
			} catch(IllegalArgumentException e) {
				String errorMessage = new StringBuilder("회원[")
						.append(userId)
						.append("]의 멤버 구분[")
						.append(nativeMembershipLevel)
						.append("]이 잘못되었습니다").toString();
				
				// log.warn(errorMessage);
				
				sendErrorOutputMessageForCommit(errorMessage, conn, toLetterCarrier, loginReq);
				return;
			}
			
			MemberStateType memberStateType = null;
			try {
				memberStateType = MemberStateType.valueOf(memberState, false);
			} catch(IllegalArgumentException e) {
				String errorMessage = new StringBuilder("회원[")
						.append(userId)
						.append("]의 멤버 상태[")
						.append(memberState)
						.append("]가 잘못되었습니다").toString();
				sendErrorOutputMessageForCommit(errorMessage, conn, toLetterCarrier, loginReq);
				return;
			}			
			
			if (ServerCommonStaticFinalVars.MAX_COUNT_OF_PASSWORD_FAILURES <= pwdFailedCount) {
				String errorMessage = new StringBuilder("최대 비밀번호 실패 횟수[")
						.append(ServerCommonStaticFinalVars.MAX_COUNT_OF_PASSWORD_FAILURES)
						.append("] 이상으로 비밀번호가 틀렸습니다, 비밀번호 초기화를 수행하시기 바랍니다").toString();
				sendErrorOutputMessageForCommit(errorMessage, conn, toLetterCarrier, loginReq);
				return;
			}
			
			if (memberStateType.equals(MemberStateType.BLOCK)) {
				String errorMessage = new StringBuilder("블락된 회원[")
						.append(userId)
						.append("] 입니다").toString();
				sendErrorOutputMessageForCommit(errorMessage, conn, toLetterCarrier, loginReq);
				return;
			}
			
			if (memberStateType.equals(MemberStateType.WITHDRAWAL)) {
				String errorMessage = new StringBuilder("탈퇴한 회원[")
						.append(userId)
						.append("] 입니다").toString();
				sendErrorOutputMessageForCommit(errorMessage, conn, toLetterCarrier, loginReq);
				return;
			}			
			
			byte[] passwordBytes = null;
			

			try {
				passwordBytes = serverSymmetricKey.decrypt(pwdCipherBytes);
			} catch (IllegalArgumentException e) {
				String errorMessage = "비밀번호 복호문을 얻는데 실패하였습니다";
				log.warn(errorMessage, e);				
				sendErrorOutputMessageForCommit(errorMessage, conn, toLetterCarrier, loginReq);
				return;
			} catch (SymmetricException e) {
				String errorMessage = "비밀번호 복호문을 얻는데 실패하였습니다";
				log.warn(errorMessage, e);				
				sendErrorOutputMessageForCommit(errorMessage, conn, toLetterCarrier, loginReq);
				return;
			}
						
			try {
				ValueChecker.checkValidPwd(passwordBytes);
			} catch(IllegalArgumentException e) {
				String errorMessage = "잘못된 비밀번호입니다";
				log.warn(errorMessage, e);
				sendErrorOutputMessageForCommit(errorMessage, conn, toLetterCarrier, loginReq);
				return;
			}
			
			byte[] pwdSaltBytes = Base64.decodeBase64(pwdSaltBase64);
			// byte[] passwordByteArray = password.getBytes(CommonStaticFinalVars.SINNORI_CIPHER_CHARSET);
			
			ByteBuffer passwordByteBuffer = ByteBuffer.allocate(pwdSaltBytes.length+passwordBytes.length);
			passwordByteBuffer.put(pwdSaltBytes);
			passwordByteBuffer.put(passwordBytes);
			MessageDigest md = MessageDigest.getInstance(CommonStaticFinalVars.SINNORI_PASSWORD_ALGORITHM_NAME);
			
			md.update(passwordByteBuffer.array());
			
			// FIXME!
			// log.info(HexUtil.getAllHexStringFromByteBuffer(passwordByteBuffer));
			
			byte pwdMDBytes[] =  md.digest();
			
			
			/** 복호환 비밀번호 초기화 */
			Arrays.fill(passwordBytes, CommonStaticFinalVars.ZERO_BYTE);
			
			String userPwdMDBase64 = Base64.encodeBase64String(pwdMDBytes);
			
			if (! pwdMDBase64.equals(userPwdMDBase64)) {
				/*
				update SB_MEMBER_TB set pwd_fail_cnt=#{pwdFailCount}, mod_dt=sysdate() where user_id=#{userId} and member_gb=1 and member_st=0
				*/				
				int countOfUpdate = create.update(SB_MEMBER_TB)
					.set(SB_MEMBER_TB.PWD_FAIL_CNT, UByte.valueOf(pwdFailedCount+1))
					.set(SB_MEMBER_TB.MOD_DT, JooqSqlUtil.getFieldOfSysDate(Timestamp.class))
					.where(SB_MEMBER_TB.USER_ID.eq(userId))
				.execute();
				
				if (0  == countOfUpdate) {
					String errorMessage = "비밀 번호 실패 횟수 갱신이 실패하였습니다";
					sendErrorOutputMessageForRollback(errorMessage, conn, toLetterCarrier, loginReq);
					return;
				}
				
				String errorMessage = "비밀 번호가 틀렸습니다";
				sendErrorOutputMessageForCommit(errorMessage, conn, toLetterCarrier, loginReq);
				return;
			}
			
			
			if (pwdFailedCount > 0) {
				int countOfUpdate = create.update(SB_MEMBER_TB)
					.set(SB_MEMBER_TB.PWD_FAIL_CNT, UByte.valueOf(0))
					.set(SB_MEMBER_TB.MOD_DT, JooqSqlUtil.getFieldOfSysDate(Timestamp.class))
					.where(SB_MEMBER_TB.USER_ID.eq(userId))
				.execute();
				
				if (0  == countOfUpdate) {
					String errorMessage = "비밀 번호 실패 횟수 초기화가 실패하였습니다";
					sendErrorOutputMessageForRollback(errorMessage, conn, toLetterCarrier, loginReq);
					return;
				}
			}
			
			MessageResultRes messageResultRes = new MessageResultRes();
			messageResultRes.setTaskMessageID(loginReq.getMessageID());
			messageResultRes.setIsSuccess(true);		
			messageResultRes.setResultMessage("로그인 성공하셨습니다");
			sendSuccessOutputMessageForCommit(messageResultRes, conn, toLetterCarrier);
			return;
		} catch (Exception e) {
			String errorMessage = new StringBuilder("unknown error, inObj=")
					.append(loginReq.toString()).toString();
			log.warn(errorMessage, e);

			if (null != conn) {
				try {
					conn.rollback();
				} catch (Exception e1) {
					log.warn("fail to rollback");
				}
			}			
			
			sendErrorOutputMessageForRollback("로그인 실패하였습니다", conn, toLetterCarrier, loginReq);
			return;

		} finally {
			if (null != conn) {
				try {
					conn.close();
				} catch(Exception e) {
					log.warn("fail to close the db connection", e);
				}
			}
		}
	}
}
