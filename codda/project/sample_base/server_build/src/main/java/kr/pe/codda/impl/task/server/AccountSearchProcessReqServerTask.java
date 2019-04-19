package kr.pe.codda.impl.task.server;

import static kr.pe.codda.jooq.tables.SbMemberTb.SB_MEMBER_TB;
import static kr.pe.codda.jooq.tables.SbAccountSerarchTb.SB_ACCOUNT_SERARCH_TB;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.sql.Timestamp;

import org.jooq.Record2;
import org.jooq.Record3;
import org.jooq.types.UByte;

import io.netty.util.internal.logging.InternalLogger;
import kr.pe.codda.common.etc.CommonStaticFinalVars;
import kr.pe.codda.common.exception.DynamicClassCallException;
import kr.pe.codda.common.exception.ServerServiceException;
import kr.pe.codda.common.exception.SymmetricException;
import kr.pe.codda.common.message.AbstractMessage;
import kr.pe.codda.common.sessionkey.ServerSessionkeyIF;
import kr.pe.codda.common.sessionkey.ServerSessionkeyManager;
import kr.pe.codda.common.sessionkey.ServerSymmetricKeyIF;
import kr.pe.codda.common.util.CommonStaticUtil;
import kr.pe.codda.impl.message.AccountSearchProcessReq.AccountSearchProcessReq;
import kr.pe.codda.impl.message.AccountSearchProcessRes.AccountSearchProcessRes;
import kr.pe.codda.impl.message.MessageResultRes.MessageResultRes;
import kr.pe.codda.server.PersonalLoginManagerIF;
import kr.pe.codda.server.lib.AccountSearchType;
import kr.pe.codda.server.lib.MemberRoleType;
import kr.pe.codda.server.lib.PasswordPairOfMemberTable;
import kr.pe.codda.server.lib.ServerCommonStaticFinalVars;
import kr.pe.codda.server.lib.ServerDBUtil;
import kr.pe.codda.server.lib.ValueChecker;
import kr.pe.codda.server.task.AbstractServerTask;
import kr.pe.codda.server.task.ToLetterCarrier;

public class AccountSearchProcessReqServerTask extends AbstractServerTask {

	public AccountSearchProcessReqServerTask() throws DynamicClassCallException {
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
			AbstractMessage outputMessage = doWork(ServerCommonStaticFinalVars.DEFAULT_DBCP_NAME,
					(AccountSearchProcessReq) inputMessage);
			toLetterCarrier.addSyncOutputMessage(outputMessage);
		} catch (ServerServiceException e) {
			String errorMessage = e.getMessage();
			log.warn("errmsg=={}, inObj={}", errorMessage, inputMessage.toString());

			sendErrorOutputMessage(errorMessage, toLetterCarrier, inputMessage);
			return;
		} catch (Exception e) {
			String errorMessage = new StringBuilder().append("unknwon errmsg=").append(e.getMessage())
					.append(", inObj=").append(inputMessage.toString()).toString();

			log.warn(errorMessage, e);

			sendErrorOutputMessage("게시글 상세 조회가 실패하였습니다", toLetterCarrier, inputMessage);
			return;
		}
		
	}

	public AccountSearchProcessRes doWork(final String dbcpName, final AccountSearchProcessReq passwordSearchProcessReq) throws Exception {
		// FIXME!
		log.info(passwordSearchProcessReq.toString());
		
		AccountSearchType accountSearchType = null;
		try {
			accountSearchType = AccountSearchType.valueOf(passwordSearchProcessReq.getAccountSearchType());
		} catch(IllegalArgumentException e) {
			String errorMessage = e.getMessage();
			throw new ServerServiceException(errorMessage);
		}
		
		String emailCipherBase64 = passwordSearchProcessReq.getEmailCipherBase64();
		String secretAuthenticationValueCipherBase64 = passwordSearchProcessReq.getSecretAuthenticationValueCipherBase64();
		String newPwdCipherBase64 = passwordSearchProcessReq.getNewPwdCipherBase64();		
		String sessionKeyBase64 = passwordSearchProcessReq.getSessionKeyBase64();		
		String ivBase64 = passwordSearchProcessReq.getIvBase64();
		
		if (null == emailCipherBase64) {
			String errorMessage = "이메일를 입력해 주세요";
			throw new ServerServiceException(errorMessage);
		}
		
		if (null == secretAuthenticationValueCipherBase64) {
			String errorMessage = "아이디/비밀버호 찾기용 비밀 값을 입력해 주세요";
			throw new ServerServiceException(errorMessage);
		}
		
		if (AccountSearchType.PASSWORD.equals(accountSearchType)) {
			if (null == newPwdCipherBase64) {
				String errorMessage = "비밀번호를 입력해 주세요";
				throw new ServerServiceException(errorMessage);
			}
		}
		
		if (null == sessionKeyBase64) {
			String errorMessage = "세션키를 입력해 주세요";
			throw new ServerServiceException(errorMessage);
		}
		
		if (null == ivBase64) {
			String errorMessage = "세션키 소금값을 입력해 주세요";
			throw new ServerServiceException(errorMessage);
		}
		
		byte[] emailCipherBytes = null;
		byte[] secretAuthenticationValueCipherBytes = null;
		byte[] newPwdCipherBytes = null;
		byte[] sessionKeyBytes = null;
		byte[] ivBytes = null;
	
		try {
			emailCipherBytes = CommonStaticUtil.Base64Decoder.decode(emailCipherBase64);
		} catch(Exception e) {
			String errorMessage = "이메일 암호문은 베이스64로 인코딩되지 않았습니다";
			throw new ServerServiceException(errorMessage);
		}
		
		try {
			secretAuthenticationValueCipherBytes = CommonStaticUtil.Base64Decoder.decode(secretAuthenticationValueCipherBase64);
		} catch(Exception e) {
			String errorMessage = "아이디 혹은 비밀번호 찾기용 비밀값 암호문은 베이스64로 인코딩되지 않았습니다";
			throw new ServerServiceException(errorMessage);
		}
		
		if (AccountSearchType.PASSWORD.equals(accountSearchType)) {
			try {
				newPwdCipherBytes = CommonStaticUtil.Base64Decoder.decode(newPwdCipherBase64);
			} catch(Exception e) {
				String errorMessage = "새로운 비밀번호 암호문은 베이스64로 인코딩되지 않았습니다";
				throw new ServerServiceException(errorMessage);
			}
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
		
		String email = null;
		String secretAuthenticationValue = null;
		byte[] newPasswordBytes = null;
		
		try {
			email = getDecryptedString(emailCipherBytes, serverSymmetricKey);
		} catch (IllegalArgumentException e) {
			String errorMessage = "이메일에 대한 복호문을 얻는데 실패하였습니다";
			log.warn(errorMessage, e);
			throw new ServerServiceException(errorMessage);
		} catch (SymmetricException e) {
			String errorMessage = "이메일에 대한 복호문을 얻는데 실패하였습니다";
			log.warn(errorMessage, e);
			throw new ServerServiceException(errorMessage);
		}
		
		try {
			secretAuthenticationValue = getDecryptedString(secretAuthenticationValueCipherBytes, serverSymmetricKey);
		} catch (IllegalArgumentException e) {
			String errorMessage = "이메일에 대한 복호문을 얻는데 실패하였습니다";
			log.warn(errorMessage, e);
			throw new ServerServiceException(errorMessage);
		} catch (SymmetricException e) {
			String errorMessage = "이메일에 대한 복호문을 얻는데 실패하였습니다";
			log.warn(errorMessage, e);
			throw new ServerServiceException(errorMessage);
		}
		
		if (AccountSearchType.PASSWORD.equals(accountSearchType)) {
			try {
				newPasswordBytes = serverSymmetricKey.decrypt(newPwdCipherBytes);
			} catch (IllegalArgumentException e) {			
				
				String errorMessage = "새로운 비밀번호 복호문을 얻는데 실패하였습니다";
				log.warn(errorMessage, e);
				throw new ServerServiceException(errorMessage);
			} catch (SymmetricException e) {
				String errorMessage = "새로운 비밀번호 복호문을 얻는데 실패하였습니다";
				log.warn(errorMessage, e);
				throw new ServerServiceException(errorMessage);
			}
		}
		
		return doPasswordChangeProcess(dbcpName, log, accountSearchType, email, secretAuthenticationValue, newPasswordBytes, passwordSearchProcessReq.getIp());
	}
	
	private String getDecryptedString(byte[] cipherBytes, ServerSymmetricKeyIF serverSymmetricKey)
			throws InterruptedException, IllegalArgumentException, SymmetricException {		
		byte[] valueBytes = serverSymmetricKey.decrypt(cipherBytes);
		String decryptedString = new String(valueBytes, CommonStaticFinalVars.CIPHER_CHARSET);
		return decryptedString;
	}
	
	public AccountSearchProcessRes doPasswordChangeProcess(final String dbcpName, final InternalLogger log,
			final AccountSearchType accountSearchType, 
			final String email,
			final String secretAuthenticationValue,
			final byte[] newPasswordBytes,
			final String ip) throws Exception {
		try {
			ValueChecker.checkValidEmail(email);
		} catch(IllegalArgumentException e) {
			String errorMessage = e.getMessage();
			throw new ServerServiceException(errorMessage);
		}
		
		if (null == secretAuthenticationValue || secretAuthenticationValue.isEmpty()) {
			String errorMessage = "아이디 혹은 비밀번호 찾기용 비밀 값을 입력해 주세요";
			throw new ServerServiceException(errorMessage);
		}
		
		try {
			CommonStaticUtil.Base64Decoder.decode(secretAuthenticationValue);
		} catch(IllegalArgumentException e) {
			String errorMessage = "아이디 혹은 비밀번호 찾기용 비밀 값이 베이스64가 아닙니다";
			throw new ServerServiceException(errorMessage);
		}
		
		if (AccountSearchType.PASSWORD.equals(accountSearchType)) {
			try {
				ValueChecker.checkValidPasswordChangePwd(newPasswordBytes);
			} catch(IllegalArgumentException e) {
				String errorMessage = e.getMessage();
				throw new ServerServiceException(errorMessage);
			}
		}
		
		
		try {
			ValueChecker.checkValidIP(ip);
		} catch(IllegalArgumentException e) {
			String errorMessage = e.getMessage();
			throw new ServerServiceException(errorMessage);
		}
		
		final AccountSearchProcessRes accountSearchProcessRes = new AccountSearchProcessRes();
		
		final String title;
		if (AccountSearchType.PASSWORD.equals(accountSearchType)) {
			title = "비밀번호 찾기";
		} else {
			title = "아이디 찾기";
		}
				
		ServerDBUtil.execute(dbcpName, (conn, create) -> {
			
			Record2<String, String> memberRecord = create
					.select(SB_MEMBER_TB.USER_ID,
							SB_MEMBER_TB.NICKNAME).from(SB_MEMBER_TB)
					.where(SB_MEMBER_TB.EMAIL.eq(email))
					.and(SB_MEMBER_TB.ROLE.eq(MemberRoleType.MEMBER.getValue())).forUpdate().fetchOne();

			if (null == memberRecord) {
				try {
					conn.rollback();
				} catch (Exception e) {
					log.warn("fail to rollback", e);
				}

				String errorMessage = "입력한 이메일에 해당하는 일반 회원이 없습니다";

				throw new ServerServiceException(errorMessage);
			}
			
			String userID = memberRecord.get(SB_MEMBER_TB.USER_ID);
			String nickname = memberRecord.get(SB_MEMBER_TB.NICKNAME);
			
			Record3<UByte, String, Timestamp> passwordSearchRequestRecord = create
					.select(SB_ACCOUNT_SERARCH_TB.FAIL_CNT, 
							SB_ACCOUNT_SERARCH_TB.LAST_SECRET_AUTH_VALUE,
							SB_ACCOUNT_SERARCH_TB.LAST_REQ_DT).from(SB_ACCOUNT_SERARCH_TB)
					.where(SB_ACCOUNT_SERARCH_TB.USER_ID.eq(userID)).fetchOne();
			
			if (null ==  passwordSearchRequestRecord) {
				try {
					conn.rollback();
				} catch (Exception e) {
					log.warn("fail to rollback", e);
				}

				String errorMessage = "아이디 혹은 비밀번호 찾기 준비 단계가 생략되었습니다";

				throw new ServerServiceException(errorMessage);
			}
			
			UByte failCount = passwordSearchRequestRecord.get(SB_ACCOUNT_SERARCH_TB.FAIL_CNT);
			String sourceSecretAuthenticationValue = passwordSearchRequestRecord.get(SB_ACCOUNT_SERARCH_TB.LAST_SECRET_AUTH_VALUE);
			Timestamp lastRegisteredDate = passwordSearchRequestRecord.get(SB_ACCOUNT_SERARCH_TB.LAST_REQ_DT);

			if (ServerCommonStaticFinalVars.MAX_WRONG_PASSWORD_COUNT_OF_PASSWORD_SEARCH_SERVICE == failCount
					.shortValue()) {
				try {
					conn.rollback();
				} catch (Exception e) {
					log.warn("fail to rollback", e);
				}

				String errorMessage = new StringBuilder()
						.append(title).append("로 비밀 값 틀린 횟수가  최대 횟수 ")
						.append(ServerCommonStaticFinalVars.MAX_WRONG_PASSWORD_COUNT_OF_PASSWORD_SEARCH_SERVICE)
						.append("회에 도달하여 더 이상 진행할 수 없습니다, 관리자에게 문의하여 주시기 바랍니다").toString();

				throw new ServerServiceException(errorMessage);
			}
			
			long elapsedTime = (new java.util.Date().getTime() - lastRegisteredDate.getTime());
			
			if (elapsedTime > ServerCommonStaticFinalVars.TIMEOUT_OF_PASSWORD_SEARCH_SERVICE) {
				try {
					conn.rollback();
				} catch (Exception e) {
					log.warn("fail to rollback", e);
				}

				String errorMessage = new StringBuilder()
						.append(title)
						.append("에서 비밀 값 입력 제한 시간[")
						.append(ServerCommonStaticFinalVars.TIMEOUT_OF_PASSWORD_SEARCH_SERVICE)
						.append(" ms]을 초과하여 더 이상 진행할 수 없습니다, 처음 부터 다시 시작해 주시기 바랍니다").toString();

				throw new ServerServiceException(errorMessage);
			}
			
			if (! sourceSecretAuthenticationValue.equals(secretAuthenticationValue)) {
				create.update(SB_ACCOUNT_SERARCH_TB)
				.set(SB_ACCOUNT_SERARCH_TB.FAIL_CNT, SB_ACCOUNT_SERARCH_TB.FAIL_CNT.add(1))
				.where(SB_ACCOUNT_SERARCH_TB.USER_ID.eq(userID))
				.execute();
				
				try {	
					conn.commit();
				} catch (Exception e) {
					log.warn("fail to commit", e);
				}
				
				Timestamp lastPwdModifiedDate = new java.sql.Timestamp(System.currentTimeMillis());
				
				
				ServerDBUtil.insertSiteLog(conn, create, log, userID, new StringBuilder()
						.append(title)
						.append(" ")
						.append(failCount.byteValue()+1)
						.append("회 비밀 값 틀림").toString(), 
						lastPwdModifiedDate, ip);
				
				conn.commit();

				String errorMessage = new StringBuilder()
						.append(title)
						.append(" ")
						.append(failCount.byteValue()+1)
						.append("회 비밀 값이 틀렸습니다, 처음 부터 다시 시도해 주시기 바랍니다").toString();

				throw new ServerServiceException(errorMessage);
			}
			
			create.update(SB_ACCOUNT_SERARCH_TB)
			.set(SB_ACCOUNT_SERARCH_TB.IS_FINISHED, "Y")
			.where(SB_ACCOUNT_SERARCH_TB.USER_ID.eq(userID))
			.execute();
			
			
			if (AccountSearchType.PASSWORD.equals(accountSearchType)) {
				byte[] newPwdSaltBytes = new byte[8];
				SecureRandom random = null;
				try {
					random = SecureRandom.getInstance("SHA1PRNG");
				} catch (NoSuchAlgorithmException e) {
					/** dead code */
					log.error("NoSuchAlgorithmException", e);
					System.exit(1);
				}
				random.nextBytes(newPwdSaltBytes);
				
				PasswordPairOfMemberTable newPasswordPairOfMemberTable = ServerDBUtil.toPasswordPairOfMemberTable(newPasswordBytes, newPwdSaltBytes);
				
				Timestamp lastPwdModifiedDate = new java.sql.Timestamp(System.currentTimeMillis());
				
				create.update(SB_MEMBER_TB)
				.set(SB_MEMBER_TB.PWD_FAIL_CNT, UByte.valueOf(0))
				.set(SB_MEMBER_TB.PWD_BASE64, newPasswordPairOfMemberTable.getPasswordBase64())
				.set(SB_MEMBER_TB.PWD_SALT_BASE64, newPasswordPairOfMemberTable.getPasswordSaltBase64())
				.set(SB_MEMBER_TB.LAST_PWD_MOD_DT, lastPwdModifiedDate)
				.where(SB_MEMBER_TB.USER_ID.eq(userID))
				.execute();
				
				conn.commit();
				
				ServerDBUtil.insertSiteLog(conn, create, log, userID, "비밀 번호 찾기 완료", 
						lastPwdModifiedDate, ip);
				conn.commit();
			} else {				
				conn.commit();
				
				Timestamp lastPwdModifiedDate = new java.sql.Timestamp(System.currentTimeMillis());
				
				ServerDBUtil.insertSiteLog(conn, create, log, userID, "아이디 찾기 완료", 
						lastPwdModifiedDate, ip);
				
				conn.commit();
			}			
			
			accountSearchProcessRes.setAccountSearchType(accountSearchType.getValue());
			accountSearchProcessRes.setUserID(userID);
			accountSearchProcessRes.setNickname(nickname);						
		});	
		
		return accountSearchProcessRes;
	}
}
