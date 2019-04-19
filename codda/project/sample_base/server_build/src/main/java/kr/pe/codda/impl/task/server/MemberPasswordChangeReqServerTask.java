package kr.pe.codda.impl.task.server;

import static kr.pe.codda.jooq.tables.SbMemberTb.SB_MEMBER_TB;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.sql.Timestamp;

import org.jooq.Record6;
import org.jooq.types.UByte;

import kr.pe.codda.common.exception.DynamicClassCallException;
import kr.pe.codda.common.exception.ServerServiceException;
import kr.pe.codda.common.exception.SymmetricException;
import kr.pe.codda.common.message.AbstractMessage;
import kr.pe.codda.common.sessionkey.ServerSessionkeyIF;
import kr.pe.codda.common.sessionkey.ServerSessionkeyManager;
import kr.pe.codda.common.sessionkey.ServerSymmetricKeyIF;
import kr.pe.codda.common.util.CommonStaticUtil;
import kr.pe.codda.impl.message.MemberPasswordChangeReq.MemberPasswordChangeReq;
import kr.pe.codda.impl.message.MessageResultRes.MessageResultRes;
import kr.pe.codda.server.PersonalLoginManagerIF;
import kr.pe.codda.server.lib.MemberRoleType;
import kr.pe.codda.server.lib.MemberStateType;
import kr.pe.codda.server.lib.PasswordPairOfMemberTable;
import kr.pe.codda.server.lib.ServerCommonStaticFinalVars;
import kr.pe.codda.server.lib.ServerDBUtil;
import kr.pe.codda.server.lib.ValueChecker;
import kr.pe.codda.server.task.AbstractServerTask;
import kr.pe.codda.server.task.ToLetterCarrier;

public class MemberPasswordChangeReqServerTask extends AbstractServerTask {
	
	public MemberPasswordChangeReqServerTask() throws DynamicClassCallException {
		super();
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
	

	@Override
	public void doTask(String projectName, 
			PersonalLoginManagerIF personalLoginManager, 
			ToLetterCarrier toLetterCarrier,
			AbstractMessage inputMessage) throws Exception {
		try {
			AbstractMessage outputMessage = doWork(ServerCommonStaticFinalVars.DEFAULT_DBCP_NAME, (MemberPasswordChangeReq)inputMessage);
			toLetterCarrier.addSyncOutputMessage(outputMessage);
		} catch(ServerServiceException e) {
			String errorMessage = e.getMessage();
			log.warn("errmsg=={}, inObj={}", errorMessage, inputMessage.toString());
			
			sendErrorOutputMessage(errorMessage, toLetterCarrier, inputMessage);
			return;
		} catch(Exception e) {
			String errorMessage = new StringBuilder().append("unknwon errmsg=")
					.append(e.getMessage())
					.append(", inObj=")
					.append(inputMessage.toString()).toString();
			
			log.warn(errorMessage, e);			
			
			sendErrorOutputMessage("사용자 로그인이 실패하였습니다", toLetterCarrier, inputMessage);
			return;
		}
	}

	public MessageResultRes doWork(String dbcpName, MemberPasswordChangeReq memberPasswordChangeReq) throws Exception {
		// FIXME!
		log.info(memberPasswordChangeReq.toString());
		
		try {
			ValueChecker.checkValidRequestedUserID(memberPasswordChangeReq.getRequestedUserID());
			ValueChecker.checkValidIP(memberPasswordChangeReq.getIp());
		} catch (IllegalArgumentException e) {
			String errorMessage = e.getMessage();
			throw new ServerServiceException(errorMessage);
		}
		
		String oldPwdCipherBase64 = memberPasswordChangeReq.getOldPwdCipherBase64();
		String newPwdCipherBase64 = memberPasswordChangeReq.getNewPwdCipherBase64();
		String sessionKeyBase64 = memberPasswordChangeReq.getSessionKeyBase64();		
		String ivBase64 = memberPasswordChangeReq.getIvBase64();
		
		
		if (null == oldPwdCipherBase64) {
			String errorMessage = "변경전 비밀번호를 입력해 주세요";
			throw new ServerServiceException(errorMessage);
		}
		
		if (null == newPwdCipherBase64) {
			String errorMessage = "변경후 비밀번호를 입력해 주세요";
			throw new ServerServiceException(errorMessage);
		}
		
		if (null == sessionKeyBase64) {
			String errorMessage = "세션키를 입력해 주세요";
			throw new ServerServiceException(errorMessage);
		}
		
		if (null == ivBase64) {
			String errorMessage = "세션키 소금값을 입력해 주세요";
			throw new ServerServiceException(errorMessage);
		}
		
		byte[] oldPwdCipherBytes = null;
		byte[] newPwdCipherBytes = null;
		byte[] sessionKeyBytes = null;
		byte[] ivBytes = null;
		
		try {
			oldPwdCipherBytes = CommonStaticUtil.Base64Decoder.decode(oldPwdCipherBase64);
		} catch(Exception e) {
			String errorMessage = "변경전 비밀번호 암호문은 베이스64로 인코딩되지 않았습니다";
			throw new ServerServiceException(errorMessage);
		}
		
		try {
			newPwdCipherBytes = CommonStaticUtil.Base64Decoder.decode(newPwdCipherBase64);
		} catch(Exception e) {
			String errorMessage = "비밀번호 암호문은 베이스64로인코딩되지 않았습니다";
			throw new ServerServiceException(errorMessage);
		}
		try {
			sessionKeyBytes = CommonStaticUtil.Base64Decoder.decode(sessionKeyBase64);
		} catch(Exception e) {
			String errorMessage = "세션키는 베이스64로 인코딩되지 않았습니다";
			throw new ServerServiceException(errorMessage);
		}
		
		try {
			ivBytes = CommonStaticUtil.Base64Decoder.decode(ivBase64);
		} catch(Exception e) {
			String errorMessage = "세션키 소금값은 베이스64로 인코딩되지 않았습니다";
			throw new ServerServiceException(errorMessage);
		}
		
		ServerSymmetricKeyIF serverSymmetricKey = null;
		try {
			ServerSessionkeyIF serverSessionkey = ServerSessionkeyManager.getInstance().getMainProjectServerSessionkey();
			
			serverSymmetricKey = serverSessionkey.getNewInstanceOfServerSymmetricKey(sessionKeyBytes, ivBytes);
					
		} catch (IllegalArgumentException e) {
			String errorMessage = "서버 세션키를 얻는데 실패하였습니다";
			log.warn(errorMessage, e);
			throw new ServerServiceException(errorMessage);
		} catch (SymmetricException e) {
			String errorMessage = "서버 세션키를 얻는데 실패하였습니다";
			log.warn(errorMessage, e);
			throw new ServerServiceException(errorMessage);
		}
		
		final byte[] oldPasswordBytes;
		try {
			oldPasswordBytes = serverSymmetricKey.decrypt(oldPwdCipherBytes);
		} catch (IllegalArgumentException e) {			
			
			String errorMessage = "변경전 비밀번호 복호문을 얻는데 실패하였습니다";
			log.warn(errorMessage, e);
			throw new ServerServiceException(errorMessage);
		} catch (SymmetricException e) {
			String errorMessage = "변경전 비밀번호 복호문을 얻는데 실패하였습니다";
			log.warn(errorMessage, e);
			throw new ServerServiceException(errorMessage);
		}
		
		final byte[] newPasswordBytes;		

		try {
			newPasswordBytes = serverSymmetricKey.decrypt(newPwdCipherBytes);
		} catch (IllegalArgumentException e) {			
			
			String errorMessage = "변경후 비밀번호 복호문을 얻는데 실패하였습니다";
			log.warn(errorMessage, e);
			throw new ServerServiceException(errorMessage);
		} catch (SymmetricException e) {
			String errorMessage = "변경후 비밀번호 복호문을 얻는데 실패하였습니다";
			log.warn(errorMessage, e);
			throw new ServerServiceException(errorMessage);
		}
					
		try {
			ValueChecker.checkValidOldPwd(oldPasswordBytes);
			ValueChecker.checkValidOldPwd(newPasswordBytes);
		} catch(IllegalArgumentException e) {
			String errorMessage = e.getMessage();
			throw new ServerServiceException(errorMessage);
		}
		
		final StringBuilder resultMessageStringBuilder = new StringBuilder();
		
		ServerDBUtil.execute(dbcpName, (conn, create) -> {
			
			Record6<String, Byte, Byte, UByte, String, String> memberRecord = create.select(
					SB_MEMBER_TB.NICKNAME,
					SB_MEMBER_TB.ROLE,
					SB_MEMBER_TB.STATE,
					SB_MEMBER_TB.PWD_FAIL_CNT,
					SB_MEMBER_TB.PWD_BASE64,
					SB_MEMBER_TB.PWD_SALT_BASE64)
				.from(SB_MEMBER_TB)
				.where(SB_MEMBER_TB.USER_ID.eq(memberPasswordChangeReq.getRequestedUserID()))
				.forUpdate().fetchOne();
			
			if (null == memberRecord) {
				try {
					conn.rollback();
				} catch (Exception e) {
					log.warn("fail to rollback");
				}
				
				String errorMessage = new StringBuilder("비밀번호 변경 요청자[")
						.append(memberPasswordChangeReq.getRequestedUserID())
						.append("]가 존재하지 않습니다").toString();
				throw new ServerServiceException(errorMessage);
			}
			
			String nickname =  memberRecord.get(SB_MEMBER_TB.NICKNAME);
			byte memberRole = memberRecord.get(SB_MEMBER_TB.ROLE);
			byte memberState = memberRecord.get(SB_MEMBER_TB.STATE);
			short pwdFailedCount = memberRecord.get(SB_MEMBER_TB.PWD_FAIL_CNT).shortValue();
			String pwdBase64 =  memberRecord.get(SB_MEMBER_TB.PWD_BASE64);
			String pwdSaltBase64 = memberRecord.get(SB_MEMBER_TB.PWD_SALT_BASE64);
			
			MemberRoleType memberRoleType = null;
			try {
				memberRoleType = MemberRoleType.valueOf(memberRole);
			} catch(IllegalArgumentException e) {
				try {
					conn.rollback();
				} catch (Exception e1) {
					log.warn("fail to rollback");
				}
				
				String errorMessage = new StringBuilder("회원[")
						.append(memberPasswordChangeReq.getRequestedUserID())
						.append("]의 멤버 구분[")
						.append(memberRole)
						.append("]이 잘못되었습니다").toString();
				
				// log.warn(errorMessage);
				
				throw new ServerServiceException(errorMessage);
			}
			
			if (MemberRoleType.GUEST.equals(memberRoleType)) {
				try {
					conn.rollback();
				} catch (Exception e1) {
					log.warn("fail to rollback");
				}
				
				String errorMessage = "비밀번호 변경은 회원만 가능합니다";
				
				throw new ServerServiceException(errorMessage);
			}
			
			MemberStateType memberStateType = null;
			try {
				memberStateType = MemberStateType.valueOf(memberState);
			} catch(IllegalArgumentException e) {
				try {
					conn.rollback();
				} catch (Exception e1) {
					log.warn("fail to rollback");
				}
				
				String errorMessage = new StringBuilder("비밀번호 변경 요청자[")
						.append(memberPasswordChangeReq.getRequestedUserID())
						.append("]의 멤버 상태[")
						.append(memberState)
						.append("]가 잘못되었습니다").toString();
				throw new ServerServiceException(errorMessage);
			}
			
			if (! MemberStateType.OK.equals(memberStateType)) {
				try {
					conn.rollback();
				} catch (Exception e) {
					log.warn("fail to rollback");
				}
				
				String errorMessage = new StringBuilder().append("비밀번호 변경 요청자[")
						.append(memberPasswordChangeReq.getRequestedUserID())
						.append("] 상태[")
						.append(memberStateType.getName())
						.append("]가 정상이 아닙니다").toString();
				throw new ServerServiceException(errorMessage);
			}
			
			if (ServerCommonStaticFinalVars.MAX_COUNT_OF_PASSWORD_FAILURES <= pwdFailedCount) {
				try {
					conn.rollback();
				} catch (Exception e) {
					log.warn("fail to rollback");
				}
				
				String errorMessage = new StringBuilder("최대 비밀번호 실패 횟수[")
						.append(ServerCommonStaticFinalVars.MAX_COUNT_OF_PASSWORD_FAILURES)
						.append("] 이상으로 비밀번호가 틀렸습니다, 관리자를 통해 비밀번호 초기화를 해 주세요").toString();
				throw new ServerServiceException(errorMessage);
			}
			
			
			byte[] oldPwdSaltBytes = CommonStaticUtil.Base64Decoder.decode(pwdSaltBase64);			
			
			PasswordPairOfMemberTable oldPasswordPairOfMemberTable = ServerDBUtil.toPasswordPairOfMemberTable(oldPasswordBytes, oldPwdSaltBytes);
			
			if (! pwdBase64.equals(oldPasswordPairOfMemberTable.getPasswordBase64())) {				
				int countOfPwdFailedCountUpdate = create.update(SB_MEMBER_TB)
					.set(SB_MEMBER_TB.PWD_FAIL_CNT, UByte.valueOf(pwdFailedCount+1))
					.where(SB_MEMBER_TB.USER_ID.eq(memberPasswordChangeReq.getRequestedUserID()))
				.execute();
				
				if (0  == countOfPwdFailedCountUpdate) {
					try {
						conn.rollback();
					} catch (Exception e) {
						log.warn("fail to rollback");
					}
					
					String errorMessage = "비밀 번호 실패 횟수 갱신이 실패하였습니다";
					throw new ServerServiceException(errorMessage);
				}
				
				conn.commit();
				
				ServerDBUtil.insertSiteLog(conn, create, log, memberPasswordChangeReq.getRequestedUserID(), "비밀번호 변경 비밀번호 틀림", 
						new java.sql.Timestamp(System.currentTimeMillis()), memberPasswordChangeReq.getIp());
				
				conn.commit();
				
				String errorMessage = "비밀 번호가 틀렸습니다";
				throw new ServerServiceException(errorMessage);
			}
			
			Timestamp lastPwdModifiedDate = new java.sql.Timestamp(System.currentTimeMillis());

			SecureRandom random = null;
			try {
				random = SecureRandom.getInstance("SHA1PRNG");
			} catch (NoSuchAlgorithmException e) {
				/** dead code */
				log.error("NoSuchAlgorithmException", e);
				System.exit(1);
			}
			
			byte[] newPwdSaltBytes = new byte[8];
			random.nextBytes(newPwdSaltBytes);
			
			PasswordPairOfMemberTable newPasswordPairOfMemberTable = ServerDBUtil.toPasswordPairOfMemberTable(newPasswordBytes, newPwdSaltBytes);
			
			create.update(SB_MEMBER_TB)
			.set(SB_MEMBER_TB.PWD_BASE64, newPasswordPairOfMemberTable.getPasswordBase64())
			.set(SB_MEMBER_TB.PWD_SALT_BASE64, newPasswordPairOfMemberTable.getPasswordSaltBase64())
			.set(SB_MEMBER_TB.LAST_PWD_MOD_DT, lastPwdModifiedDate)
			.where(SB_MEMBER_TB.USER_ID.eq(memberPasswordChangeReq.getRequestedUserID()))
			.execute();
			
			conn.commit();
			
			ServerDBUtil.insertSiteLog(conn, create, log, memberPasswordChangeReq.getRequestedUserID(), "비밀번호 변경 완료", 
					lastPwdModifiedDate, memberPasswordChangeReq.getIp());
			conn.commit();
			
			resultMessageStringBuilder.append(nickname)
			.append("님의 비밀번호 변경 처리가 완료되었습니다");
		});
				
		MessageResultRes messageResultRes = new MessageResultRes();
		messageResultRes.setTaskMessageID(memberPasswordChangeReq.getMessageID());
		messageResultRes.setIsSuccess(true);		
		messageResultRes.setResultMessage(resultMessageStringBuilder.toString());

		return messageResultRes;
		
	}
}
