package kr.pe.codda.impl.task.server;

import static kr.pe.codda.jooq.tables.SbMemberTb.SB_MEMBER_TB;

import java.sql.Timestamp;

import org.jooq.Record5;
import org.jooq.types.UByte;

import io.netty.util.internal.logging.InternalLogger;
import kr.pe.codda.common.exception.DynamicClassCallException;
import kr.pe.codda.common.exception.ServerServiceException;
import kr.pe.codda.common.exception.SymmetricException;
import kr.pe.codda.common.message.AbstractMessage;
import kr.pe.codda.common.sessionkey.ServerSessionkeyIF;
import kr.pe.codda.common.sessionkey.ServerSessionkeyManager;
import kr.pe.codda.common.sessionkey.ServerSymmetricKeyIF;
import kr.pe.codda.common.util.CommonStaticUtil;
import kr.pe.codda.impl.message.MemberWithdrawReq.MemberWithdrawReq;
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

public class MemberWithdrawReqServerTask extends AbstractServerTask {

	public MemberWithdrawReqServerTask() throws DynamicClassCallException {
		super();
	}

	private void sendErrorOutputMessage(String errorMessage, ToLetterCarrier toLetterCarrier,
			AbstractMessage inputMessage) throws InterruptedException {
		log.warn("{}, inObj=", errorMessage, inputMessage.toString());

		MessageResultRes messageResultRes = new MessageResultRes();
		messageResultRes.setTaskMessageID(inputMessage.getMessageID());
		messageResultRes.setIsSuccess(false);
		messageResultRes.setResultMessage(errorMessage);
		toLetterCarrier.addSyncOutputMessage(messageResultRes);
	}
	
	
	@Override
	public void doTask(String projectName, PersonalLoginManagerIF personalLoginManager, ToLetterCarrier toLetterCarrier,
			AbstractMessage inputMessage) throws Exception {
		try {
			AbstractMessage outputMessage = doWork(ServerCommonStaticFinalVars.DEFAULT_DBCP_NAME, (MemberWithdrawReq)inputMessage);
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
						
			sendErrorOutputMessage("회웥 탈퇴하는데 실패하였습니다", toLetterCarrier, inputMessage);
			return;
		}
	}
	
	public MessageResultRes doWork(String dbcpName, MemberWithdrawReq memberWithdrawReq)
			throws Exception {
		// FIXME!
		log.info(memberWithdrawReq.toString());
				
		String pwdCipherBase64 = memberWithdrawReq.getPwdCipherBase64();
		String sessionKeyBase64 = memberWithdrawReq.getSessionKeyBase64();
		String ivBase64 = memberWithdrawReq.getIvBase64();		
		
		
		if (null == pwdCipherBase64) {
			String errorMessage = "비밀번호를 입력해 주세요";
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
		
		byte[] pwdCipherBytes = null;
		byte[] sessionKeyBytes = null;
		byte[] ivBytes = null;

		try {
			pwdCipherBytes = CommonStaticUtil.Base64Decoder.decode(pwdCipherBase64);
		} catch (Exception e) {
			String errorMessage = "비밀번호 암호문은 베이스64로 인코딩되지 않았습니다";
			throw new ServerServiceException(errorMessage);
		}
		
		try {
			sessionKeyBytes = CommonStaticUtil.Base64Decoder.decode(sessionKeyBase64);
		} catch (Exception e) {
			String errorMessage = "세션키는 베이스64로 인코딩되지 않았습니다";
			throw new ServerServiceException(errorMessage);
		}

		try {
			ivBytes = CommonStaticUtil.Base64Decoder.decode(ivBase64);
		} catch (Exception e) {
			String errorMessage = "세션키 소금값은 베이스64로 인코딩되지 않았습니다";
			throw new ServerServiceException(errorMessage);
		}
		
		ServerSymmetricKeyIF serverSymmetricKey = null;
		try {
			ServerSessionkeyIF serverSessionkey = ServerSessionkeyManager.getInstance()
					.getMainProjectServerSessionkey();
			serverSymmetricKey = serverSessionkey.getNewInstanceOfServerSymmetricKey(sessionKeyBytes, ivBytes);
		} catch (IllegalArgumentException e) {
			String debugMessage = new StringBuilder().append("잘못된 파라미터로 인한 대칭키 생성 실패, ")
					.append(memberWithdrawReq.toString()).toString();

			log.warn(debugMessage, e);

			String errorMessage = "대칭키 생성 실패로 멤버 등록이 실패하였습니다. 상세한 이유는 서버 로그를 확인해 주세요.";
			throw new ServerServiceException(errorMessage);
		} catch (SymmetricException e) {
			String debugMessage = new StringBuilder().append("알수 없는 이유로 대칭키 생성 실패, ")
					.append(memberWithdrawReq.toString()).toString();

			log.warn(debugMessage, e);

			String errorMessage = "대칭키 생성 실패로 멤버 등록이 실패하였습니다. 상세한 이유는 서버 로그를 확인해 주세요.";
			throw new ServerServiceException(errorMessage);
		}
		
		
		byte[] passwordBytes = null;
		try {
			passwordBytes = serverSymmetricKey.decrypt(pwdCipherBytes);
		} catch (IllegalArgumentException e) {
			String debugMessage = new StringBuilder().append("잘못된 파라미터로 인한 비밀번호 복호화 실패, ")
					.append(memberWithdrawReq.toString()).toString();

			log.warn(debugMessage, e);

			String errorMessage = "비밀번호 복호화 실패로 멤버 등록이 실패하였습니다. 상세한 이유는 서버 로그를 확인해 주세요.";
			throw new ServerServiceException(errorMessage);
		} catch (SymmetricException e) {
			String debugMessage = new StringBuilder().append("알 수 없는 에러로 인한 비밀번호 복호화 실패, ")
					.append(memberWithdrawReq.toString()).toString();

			log.warn(debugMessage, e);

			String errorMessage = "비밀번호 복호화 실패로 멤버 등록이 실패하였습니다. 상세한 이유는 서버 로그를 확인해 주세요.";
			throw new ServerServiceException(errorMessage);
		}
		
		withdrawMember(dbcpName, log, memberWithdrawReq.getRequestedUserID(), passwordBytes, 
				new java.sql.Timestamp(System.currentTimeMillis()), memberWithdrawReq.getIp());
		
		
		String successResultMessage = new StringBuilder()
				.append(memberWithdrawReq.getMessageID())
				.append("님의 회원 탈퇴 처리가 완료되었습니다").toString();
		
		MessageResultRes messageResultRes = new MessageResultRes();
		messageResultRes.setTaskMessageID(memberWithdrawReq.getMessageID());
		messageResultRes.setIsSuccess(true);
		messageResultRes.setResultMessage(successResultMessage);
		
		return messageResultRes;
	}
	
	public void withdrawMember(String dbcpName, InternalLogger log, String requestedUserID, byte[] passwordBytes, 
			Timestamp registeredDate, String ip) throws Exception {
		
		try {
			ValueChecker.checkValidRequestedUserID(requestedUserID);
			ValueChecker.checkValidLoginPwd(passwordBytes);
			ValueChecker.checkValidIP(ip);
		} catch(IllegalArgumentException e) {			
			String errorMessage = e.getMessage();
			throw new ServerServiceException(errorMessage);
		}
		
		ServerDBUtil.execute(dbcpName, (conn, create) -> {
			/** 탈퇴 대상 회원 레코드 락 */
			Record5<Byte, Byte, UByte, String, String> memberRecordOfRequestedUserID = create.select(
					SB_MEMBER_TB.ROLE,
					SB_MEMBER_TB.STATE,
					SB_MEMBER_TB.PWD_FAIL_CNT,
					SB_MEMBER_TB.PWD_BASE64,
					SB_MEMBER_TB.PWD_SALT_BASE64)
			.from(SB_MEMBER_TB)
			.where(SB_MEMBER_TB.USER_ID.eq(requestedUserID))
			.forUpdate()
			.fetchOne();
			
			if (null == memberRecordOfRequestedUserID) {
				try {
					conn.rollback();
				} catch (Exception e) {
					log.warn("fail to rollback");
				}
				
				String errorMessage = new StringBuilder("회원 탈퇴 요청자[")
						.append(requestedUserID)
						.append("]가 회원 테이블에 존재하지 않습니다").toString();				
				throw new ServerServiceException(errorMessage);
			}
			
			byte memberRole = memberRecordOfRequestedUserID.getValue(SB_MEMBER_TB.ROLE);
			byte memberState = memberRecordOfRequestedUserID.get(SB_MEMBER_TB.STATE);
			short pwdFailedCount = memberRecordOfRequestedUserID.get(SB_MEMBER_TB.PWD_FAIL_CNT).shortValue();
			String pwdBase64 =  memberRecordOfRequestedUserID.get(SB_MEMBER_TB.PWD_BASE64);
			String pwdSaltBase64 = memberRecordOfRequestedUserID.get(SB_MEMBER_TB.PWD_SALT_BASE64);
			
			
			MemberRoleType  memberRoleType = null;
			try {
				memberRoleType = MemberRoleType.valueOf(memberRole);
			} catch(IllegalArgumentException e) {
				try {
					conn.rollback();
				} catch (Exception e1) {
					log.warn("fail to rollback");
				}
				
				String errorMessage = new StringBuilder("회원 탈퇴 요청자[")
						.append(requestedUserID)
						.append("]의 멤버 역활 유형[")
						.append(memberRole)
						.append("]이 잘못되어있습니다").toString();
				throw new ServerServiceException(errorMessage);
			}	
			
			if (! MemberRoleType.MEMBER.equals(memberRoleType)) {
				try {
					conn.rollback();
				} catch (Exception e) {
					log.warn("fail to rollback");
				}
				
				String errorMessage = new StringBuilder()
						.append( "회원 탈퇴 요청자[역활:")
						.append(memberRoleType.getName())
						.append("]가 일반 회원이 아닙니다").toString();
				throw new ServerServiceException(errorMessage);
			}	
			
			
			MemberStateType memberStateTypeOfRequestedUserID = null;
			try {
				memberStateTypeOfRequestedUserID = MemberStateType.valueOf(memberState);
			} catch(IllegalArgumentException e) {
				try {
					conn.rollback();
				} catch (Exception e1) {
					log.warn("fail to rollback");
				}
				
				String errorMessage = new StringBuilder("회원 탈퇴 요청자[")
						.append(requestedUserID)
						.append("]의 상태[")
						.append(memberState)
						.append("]가 잘못 되어 있습니다").toString();
				throw new ServerServiceException(errorMessage);
			}
			
			if (! MemberStateType.OK.equals(memberStateTypeOfRequestedUserID)) {
				try {
					conn.rollback();
				} catch (Exception e1) {
					log.warn("fail to rollback");
				}
				
				String errorMessage = new StringBuilder("탈퇴 대상 사용자[")
						.append(requestedUserID)
						.append("]의 상태[")
						.append(memberStateTypeOfRequestedUserID.getName())
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
						.append("] 이상으로 비밀번호가 틀려 탈퇴하실 수 없습니다, 먼저 비밀번호 찾기 혹은 관리자를 통해 비밀번호 최대 실패 횟수를 초기화 하시기 바랍니다").toString();
				throw new ServerServiceException(errorMessage);
			}
			
			byte[] pwdSaltBytes = CommonStaticUtil.Base64Decoder.decode(pwdSaltBase64);			
			
			PasswordPairOfMemberTable passwordPairOfMemberTable = ServerDBUtil.toPasswordPairOfMemberTable(passwordBytes, pwdSaltBytes);
			
			if (! pwdBase64.equals(passwordPairOfMemberTable.getPasswordBase64())) {
				int countOfPwdFailedCountUpdate = create.update(SB_MEMBER_TB)
					.set(SB_MEMBER_TB.PWD_FAIL_CNT, UByte.valueOf(pwdFailedCount+1))
					.where(SB_MEMBER_TB.USER_ID.eq(requestedUserID))
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
				
				ServerDBUtil.insertSiteLog(conn, create, log, requestedUserID, "회원 탈퇴 비밀번호 틀림", 
						new java.sql.Timestamp(System.currentTimeMillis()), ip);
				
				conn.commit();
				
				String errorMessage = "비밀 번호가 틀렸습니다";
				throw new ServerServiceException(errorMessage);
			}
			
			create.update(SB_MEMBER_TB)
			.set(SB_MEMBER_TB.STATE, MemberStateType.WITHDRAWAL.getValue())
			.where(SB_MEMBER_TB.USER_ID.eq(requestedUserID))
			.execute();
			
			
			conn.commit();
			
			ServerDBUtil.insertSiteLog(conn, create, log, requestedUserID, "회원 탈퇴 완료", 
					new java.sql.Timestamp(System.currentTimeMillis()), ip);
			
			conn.commit();
		});
	}
}
