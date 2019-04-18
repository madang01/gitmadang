package kr.pe.codda.servlet.user;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import kr.pe.codda.client.AnyProjectConnectionPoolIF;
import kr.pe.codda.client.ConnectionPoolManager;
import kr.pe.codda.common.etc.CommonStaticFinalVars;
import kr.pe.codda.common.exception.SymmetricException;
import kr.pe.codda.common.message.AbstractMessage;
import kr.pe.codda.common.sessionkey.ServerSessionkeyIF;
import kr.pe.codda.common.sessionkey.ServerSessionkeyManager;
import kr.pe.codda.common.sessionkey.ServerSymmetricKeyIF;
import kr.pe.codda.common.util.CommonStaticUtil;
import kr.pe.codda.common.util.HexUtil;
import kr.pe.codda.impl.classloader.ClientMessageCodecManger;
import kr.pe.codda.impl.message.AccountSearchReadyReq.AccountSearchReadyReq;
import kr.pe.codda.impl.message.MessageResultRes.MessageResultRes;
import kr.pe.codda.weblib.common.AccountSearchType;
import kr.pe.codda.weblib.common.ValueChecker;
import kr.pe.codda.weblib.common.WebCommonStaticFinalVars;
import kr.pe.codda.weblib.jdf.AbstractServlet;

public class AccountSearchInputSvl extends AbstractServlet {

	private static final long serialVersionUID = -4525175323268134643L;

	@Override
	protected void performTask(HttpServletRequest req, HttpServletResponse res) throws Exception {
		/**************** 파라미터 시작 *******************/
		String paramSessionKeyBase64 = req.getParameter(WebCommonStaticFinalVars.PARAMETER_KEY_NAME_OF_SESSION_KEY);
		String paramIVBase64 = req.getParameter(WebCommonStaticFinalVars.PARAMETER_KEY_NAME_OF_SESSION_KEY_IV);

		String paramEmailCipherBase64 = req.getParameter("email");
		String paramAccountSearchType = req.getParameter("accountSearchType");
		/**************** 파라미터 종료 *******************/
		
		if (null == paramAccountSearchType) {
			String errorMessage = "the request parameter accountSearchType is null";
			String debugMessage = null;
			printErrorMessagePage(req, res, errorMessage, debugMessage);
			return;
		}
		
		byte accountSearchTypeValue;
		try {
			accountSearchTypeValue = Byte.parseByte(paramAccountSearchType);
		} catch(NumberFormatException e) {
			String errorMessage = "the request parameter accountSearchType is bad because it is not a byte type number";
			String debugMessage = new StringBuilder()
					.append("the request parameter accountSearchType[")
					.append(paramAccountSearchType)
					.append("] is not a byte type number").toString();
			printErrorMessagePage(req, res, errorMessage, debugMessage);
			return;
		}
		
		AccountSearchType accountSearchType = null;
		try {
			accountSearchType = AccountSearchType.valueOf(accountSearchTypeValue);
		} catch (IllegalArgumentException e) {
			String errorMessage = "the request parameter accountSearchType is bad because it is not a element of SearchWhatType";
			String debugMessage = new StringBuilder()
					.append("the request parameter accountSearchType[")
					.append(paramAccountSearchType)
					.append("] is not a element of SearchWhatType").toString();
			printErrorMessagePage(req, res, errorMessage, debugMessage);
			return;
		}
		
		if (null == paramSessionKeyBase64) {
			String errorMessage = "the request parameter paramSessionKeyBase64 is null";
			String debugMessage = null;
			printErrorMessagePage(req, res, errorMessage, debugMessage);
			return;
		}

		if (null == paramIVBase64) {
			String errorMessage = "the request parameter paramIVBase64 is null";
			String debugMessage = null;
			printErrorMessagePage(req, res, errorMessage, debugMessage);
			return;
		}

		if (null == paramEmailCipherBase64) {
			String errorMessage = "the request parameter email is null";
			String debugMessage = null;
			printErrorMessagePage(req, res, errorMessage, debugMessage);
			return;
		}
		
		log.info("param sessionkeyBase64=[{}]", paramSessionKeyBase64);
		log.info("param ivBase64=[{}]", paramIVBase64);
		log.info("param email=[{}]", paramEmailCipherBase64);
		
		byte[] sessionkeyBytes = null;
		try {
			sessionkeyBytes = CommonStaticUtil.Base64Decoder.decode(paramSessionKeyBase64);
		} catch (Exception e) {
			log.warn("base64 encoding error for the parameter paramSessionKeyBase64[{}], errormessage=[{}]",
					paramSessionKeyBase64, e.getMessage());

			String errorMessage = "세션키 파라미터가 잘못되었습니다";
			String debugMessage = new StringBuilder().append("the parameter '")
					.append(WebCommonStaticFinalVars.PARAMETER_KEY_NAME_OF_SESSION_KEY).append("'[")
					.append(paramSessionKeyBase64).append("] is not a base64 encoding string, errmsg=")
					.append(e.getMessage()).toString();

			printErrorMessagePage(req, res, errorMessage, debugMessage);
			return;
		}
		byte[] ivBytes = null;
		try {
			ivBytes = CommonStaticUtil.Base64Decoder.decode(paramIVBase64);
		} catch (Exception e) {
			log.warn("base64 encoding error for the parameter paramIVBase64[{}], errormessage=[{}]", paramIVBase64,
					e.getMessage());

			String errorMessage = "세션키 소금 파라미터가 잘못되었습니다";
			String debugMessage = new StringBuilder().append("the parameter '")
					.append(WebCommonStaticFinalVars.PARAMETER_KEY_NAME_OF_SESSION_KEY_IV).append("'[")
					.append(paramIVBase64).append("] is not a base64 encoding string, errmsg=").append(e.getMessage())
					.toString();

			printErrorMessagePage(req, res, errorMessage, debugMessage);
			return;
		}

		ServerSessionkeyIF webServerSessionkey = null;
		try {
			ServerSessionkeyManager serverSessionkeyManager = ServerSessionkeyManager.getInstance();
			webServerSessionkey = serverSessionkeyManager.getMainProjectServerSessionkey();
		} catch (SymmetricException e) {
			String errorMessage = "fail to get a ServerSessionkeyManger class instance";
			log.warn(errorMessage, e);

			String debugMessage = e.getMessage();
			printErrorMessagePage(req, res, errorMessage, debugMessage);
			return;
		}

		ServerSymmetricKeyIF webServerSymmetricKey = null;
		try {
			webServerSymmetricKey = webServerSessionkey.getNewInstanceOfServerSymmetricKey(true, sessionkeyBytes,
					ivBytes);
		} catch (IllegalArgumentException e) {
			String errorMessage = "웹 세션키 인스턴스 생성 실패";
			log.warn(errorMessage, e);

			String debugMessage = new StringBuilder("sessionkeyBytes=[")
					.append(HexUtil.getHexStringFromByteArray(sessionkeyBytes)).append("], ivBytes=[")
					.append(HexUtil.getHexStringFromByteArray(ivBytes)).append("]").toString();

			printErrorMessagePage(req, res, errorMessage, debugMessage);
			return;
		} catch (SymmetricException e) {
			String errorMessage = "웹 세션키 인스턴스 생성 실패";
			log.warn(errorMessage, e);

			String debugMessage = new StringBuilder("sessionkeyBytes=[")
					.append(HexUtil.getHexStringFromByteArray(sessionkeyBytes)).append("], ivBytes=[")
					.append(HexUtil.getHexStringFromByteArray(ivBytes)).append("]").toString();

			printErrorMessagePage(req, res, errorMessage, debugMessage);
			return;
		}
		
		byte[] emailBytes = webServerSymmetricKey
				.decrypt(CommonStaticUtil.Base64Decoder.decode(paramEmailCipherBase64));
		
		String email = new String(emailBytes, CommonStaticFinalVars.CIPHER_CHARSET);
		
		try {
			ValueChecker.checkValidEmail(email);
		} catch(IllegalArgumentException e) {
			String errorMessage = e.getMessage();
			String debugMessage = null;
			printErrorMessagePage(req, res, errorMessage, debugMessage);
			return;
		}
		
		AccountSearchReadyReq accountSearchReadyReq = new AccountSearchReadyReq();
		accountSearchReadyReq.setAccountSearchType(accountSearchType.getValue());
		accountSearchReadyReq.setEmail(email);
		accountSearchReadyReq.setIp(req.getRemoteAddr());
		
		AnyProjectConnectionPoolIF mainProjectConnectionPool = ConnectionPoolManager.getInstance()
				.getMainProjectConnectionPool();
		
		AbstractMessage outputMessage = mainProjectConnectionPool
				.sendSyncInputMessage(ClientMessageCodecManger.getInstance(), accountSearchReadyReq);
		
		if (! (outputMessage instanceof MessageResultRes)) {
			String errorMessage = "아이디 혹은 비밀번호 찾기 준비 처리가 실패했습니다. 상세한 내용은 에러 로그를 참고하세요.";
			String debugMessage = new StringBuilder("입력 메시지[")
					.append(accountSearchReadyReq.getMessageID())
					.append("]에 대한 비 정상 출력 메시지[")
					.append(outputMessage.toString())
					.append("] 도착").toString();
			
			log.error(debugMessage);

			printErrorMessagePage(req, res, errorMessage, debugMessage);
			return;
		}
		
		MessageResultRes messageResultRes = (MessageResultRes) outputMessage;
		
		if (! messageResultRes.getIsSuccess()) {
			String errorMessage = messageResultRes.getResultMessage();
			String debugMessage = null;

			printErrorMessagePage(req, res, errorMessage, debugMessage);
			return;
		}
		
		req.setAttribute("accountSearchType", accountSearchType);
		req.setAttribute("email", email);
		printJspPage(req, res, "/jsp/member/AccountSearchInput.jsp");
	}

}
