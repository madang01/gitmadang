package kr.pe.codda.servlet.user;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import kr.pe.codda.client.AnyProjectConnectionPoolIF;
import kr.pe.codda.client.ConnectionPoolManager;
import kr.pe.codda.common.etc.CommonStaticFinalVars;
import kr.pe.codda.common.message.AbstractMessage;
import kr.pe.codda.common.sessionkey.ClientSessionKeyIF;
import kr.pe.codda.common.sessionkey.ClientSessionKeyManager;
import kr.pe.codda.common.sessionkey.ClientSymmetricKeyIF;
import kr.pe.codda.common.sessionkey.ServerSymmetricKeyIF;
import kr.pe.codda.common.util.CommonStaticUtil;
import kr.pe.codda.impl.classloader.ClientMessageCodecManger;
import kr.pe.codda.impl.message.BinaryPublicKey.BinaryPublicKey;
import kr.pe.codda.impl.message.MemberPasswordChangeReq.MemberPasswordChangeReq;
import kr.pe.codda.impl.message.MessageResultRes.MessageResultRes;
import kr.pe.codda.weblib.common.AccessedUserInformation;
import kr.pe.codda.weblib.common.ValueChecker;
import kr.pe.codda.weblib.common.WebCommonStaticFinalVars;
import kr.pe.codda.weblib.exception.WebClientException;
import kr.pe.codda.weblib.jdf.AbstractUserLoginServlet;

public class MemberPasswordChangeProcessSvl extends AbstractUserLoginServlet {

	private static final long serialVersionUID = 966535867318719804L;
	

	@Override
	protected void performTask(HttpServletRequest req, HttpServletResponse res) throws Exception {
		MessageResultRes messageResultRes = null;
		
		try {
			messageResultRes = doWork(req, res);
		} catch (WebClientException e) {
			String errorMessage = e.getErrorMessage();
			String debugMessage = e.getDebugMessage();

			AccessedUserInformation  accessedUserformation = getAccessedUserInformationFromSession(req);
			
			String  logMessage = new StringBuilder()
			.append("errmsg=")
			.append(errorMessage)
			.append(CommonStaticFinalVars.NEWLINE)
			.append(", debugmsg=")
			.append(debugMessage)
			.append(", userID=")
			.append(accessedUserformation.getUserID())
			.append(", ip=")
			.append(req.getRemoteAddr())
			.append(", errmsg=")
			.append(e.getMessage())
			.toString();
			
			log.warn(logMessage);

			printErrorMessagePage(req, res, errorMessage, debugMessage);
			return;
		} catch (Exception e) {
			AccessedUserInformation  accessedUserformation = getAccessedUserInformationFromSession(req);
			
			String errorMessage = "비밀 번호 변경이 실패하였습니다";
			String debugMessage = new StringBuilder()
					.append(errorMessage)
					.append(", userID=")
					.append(accessedUserformation.getUserID())
					.append(", ip=")
					.append(req.getRemoteAddr())
					.append(", errmsg=")
					.append(e.getMessage())
					.toString();
			
			log.warn(debugMessage, e);

			printErrorMessagePage(req, res, errorMessage, debugMessage);
			return;
		}
		
		if (! messageResultRes.getIsSuccess()) {
			printErrorMessagePage(req, res, messageResultRes.getResultMessage(), null);
			return;
		}
		
		printJspPage(req, res, "/jsp/my/MemberPasswordChangeProcess.jsp");
		return;
	
	}
	
	public MessageResultRes doWork(HttpServletRequest req, HttpServletResponse res)
			throws Exception {
		ServerSymmetricKeyIF  symmetricKeyFromSessionkey = (ServerSymmetricKeyIF)req
				.getAttribute(WebCommonStaticFinalVars.REQUEST_KEY_NAME_OF_SYMMETRIC_KEY_FROM_SESSIONKEY);
		
		if (null == symmetricKeyFromSessionkey) {
			String errorMessage = "세션키를 다룰 준비가 안되었습니다";
			String debugMessage = new StringBuilder()
					.append("the request attribute key[")
					.append(WebCommonStaticFinalVars.REQUEST_KEY_NAME_OF_SYMMETRIC_KEY_FROM_SESSIONKEY)
					.append("]'s value is null").toString();
			throw new WebClientException(errorMessage, debugMessage);
		}
		
		/**************** 파라미터 시작 *******************/
		String paramOldPwdBase64 = req.getParameter("oldPwd");
		String paramNewPwdBase64 = req.getParameter("newPwd");
		/**************** 파라미터 종료 *******************/
		
		if (null == paramOldPwdBase64) {
			String errorMessage = "변경 전 비밀번호를 입력해 주세요";
			String debugMessage = null;
			throw new WebClientException(errorMessage, debugMessage);
		}
		
		byte[] oldPwdCipherBytes = null;
		try {
			oldPwdCipherBytes = CommonStaticUtil.Base64Decoder.decode(paramOldPwdBase64);
		} catch(IllegalArgumentException e) {
			String errorMessage = "변경 전 비밀번호가 베이스64 인코딩 문자열이 아닙니다";
			String debugMessage = new StringBuilder()
					.append(errorMessage).append(", paramOldPwdBase64=[")
					.append(paramOldPwdBase64)
					.append("]").toString();
			throw new WebClientException(errorMessage, debugMessage);
		}
		
		if (null == paramNewPwdBase64) {
			String errorMessage = "변경 후 비밀번호를 입력해 주세요";
			String debugMessage = null;
			throw new WebClientException(errorMessage, debugMessage);
		}
		
		byte[] newPwdCipherBytes = null;
		try {
			newPwdCipherBytes = CommonStaticUtil.Base64Decoder.decode(paramNewPwdBase64);
		} catch(IllegalArgumentException e) {
			String errorMessage = "변경 후 비밀번호가 베이스64 인코딩 문자열이 아닙니다";
			String debugMessage = new StringBuilder()
					.append(errorMessage).append(", paramNewPwdBase64=[")
					.append(paramNewPwdBase64)
					.append("]").toString();
			throw new WebClientException(errorMessage, debugMessage);
		}
		

		byte[] oldPasswordBytes = symmetricKeyFromSessionkey.decrypt(oldPwdCipherBytes);
		byte[] newPasswordBytes = symmetricKeyFromSessionkey.decrypt(newPwdCipherBytes);
		
		try {
			ValueChecker.checkValidOldPwd(oldPasswordBytes);
		} catch (IllegalArgumentException e) {
			String errorMessage = e.getMessage();
			String debugMessage = null;

			throw new WebClientException(errorMessage, debugMessage);
		}
		
		try {
			ValueChecker.checkValidNewPwd(newPasswordBytes);
		} catch (IllegalArgumentException e) {
			String errorMessage = e.getMessage();
			String debugMessage = null;

			throw new WebClientException(errorMessage, debugMessage);
		}
		
		AnyProjectConnectionPoolIF mainProjectConnectionPool = ConnectionPoolManager.getInstance()
				.getMainProjectConnectionPool();
		
		BinaryPublicKey binaryPublicKeyReq = new BinaryPublicKey();			
		binaryPublicKeyReq.setPublicKeyBytes(new byte[0]);
		
		AbstractMessage binaryPublicKeyOutputMessage = mainProjectConnectionPool
				.sendSyncInputMessage(ClientMessageCodecManger.getInstance(), binaryPublicKeyReq);					
		if (! (binaryPublicKeyOutputMessage instanceof BinaryPublicKey)) {
			String errorMessage = "비밀 번호 변경이 실패하였습니다";
			String debugMessage = new StringBuilder("입력 메시지[")
					.append(binaryPublicKeyReq.getMessageID())
					.append("]에 대한 비 정상 출력 메시지[")
					.append(binaryPublicKeyOutputMessage.toString())
					.append("] 도착").toString();

			throw new WebClientException(errorMessage, debugMessage);
		}
		
		BinaryPublicKey binaryPublicKeyOutObj = (BinaryPublicKey) binaryPublicKeyOutputMessage;
		byte[] binaryPublicKeyBytes = binaryPublicKeyOutObj.getPublicKeyBytes();
		ClientSessionKeyIF clientSessionKey = ClientSessionKeyManager.getInstance()
				.getNewClientSessionKey(binaryPublicKeyBytes, false);
		
		byte sessionKeyBytesOfServer[] = clientSessionKey.getDupSessionKeyBytes();								
		byte ivBytesOfServer[] = clientSessionKey.getDupIVBytes();
		ClientSymmetricKeyIF clientSymmetricKey = clientSessionKey.getClientSymmetricKey();
		
		
		AccessedUserInformation accessedUserformation = getAccessedUserInformationFromSession(req);
		
		MemberPasswordChangeReq memberPasswordChangeReq = new MemberPasswordChangeReq();		
		memberPasswordChangeReq.setRequestedUserID(accessedUserformation.getUserID());
		memberPasswordChangeReq.setOldPwdCipherBase64(CommonStaticUtil
				.Base64Encoder.encodeToString(clientSymmetricKey.encrypt(oldPasswordBytes)));
		memberPasswordChangeReq.setNewPwdCipherBase64(CommonStaticUtil
				.Base64Encoder.encodeToString(clientSymmetricKey.encrypt(newPasswordBytes)));
		
		memberPasswordChangeReq.setSessionKeyBase64(CommonStaticUtil
				.Base64Encoder.encodeToString(sessionKeyBytesOfServer));
		memberPasswordChangeReq.setIvBase64(CommonStaticUtil.Base64Encoder.encodeToString(ivBytesOfServer));
		memberPasswordChangeReq.setIp(req.getRemoteAddr());
		
		AbstractMessage memberRegisterOutputMessage = mainProjectConnectionPool
				.sendSyncInputMessage(ClientMessageCodecManger.getInstance(), memberPasswordChangeReq);					
		if (! (memberRegisterOutputMessage instanceof MessageResultRes)) {
			String errorMessage = "비밀 번호 변경이 실패하였습니다";
			String debugMessage = new StringBuilder("입력 메시지[")
					.append(memberPasswordChangeReq.getMessageID())
					.append("]에 대한 비 정상 출력 메시지[")
					.append(memberRegisterOutputMessage.toString())
					.append("] 도착").toString();

			throw new WebClientException(errorMessage, debugMessage);
		}
		
		MessageResultRes messageResultRes = (MessageResultRes)memberRegisterOutputMessage;
		
		return messageResultRes;
	}

}
