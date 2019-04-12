package kr.pe.codda.impl.task.server;

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
		
		try {
			ValueChecker.checkValidRequestedUserID(memberWithdrawReq.getRequestedUserID());
		} catch (IllegalArgumentException e) {
			String errorMessage = e.getMessage();
			throw new ServerServiceException(errorMessage);
		}
		
		try {
			ValueChecker.checkValidIP(memberWithdrawReq.getIp());
		} catch (IllegalArgumentException e) {
			String errorMessage = e.getMessage();
			throw new ServerServiceException(errorMessage);
		}
				
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
		
		try {
			ValueChecker.checkValidLoginPwd(passwordBytes);
		} catch(IllegalArgumentException e) {			
			String errorMessage = e.getMessage();
			throw new ServerServiceException(errorMessage);
		}
		
		ServerDBUtil.withdrawMember(dbcpName, memberWithdrawReq.getRequestedUserID(), passwordBytes, 
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
}
