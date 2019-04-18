package kr.pe.codda.servlet.user;

import java.util.Arrays;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import kr.pe.codda.client.AnyProjectConnectionPoolIF;
import kr.pe.codda.client.ConnectionPoolManager;
import kr.pe.codda.common.etc.CommonStaticFinalVars;
import kr.pe.codda.common.exception.SymmetricException;
import kr.pe.codda.common.message.AbstractMessage;
import kr.pe.codda.common.sessionkey.ClientSessionKeyIF;
import kr.pe.codda.common.sessionkey.ClientSessionKeyManager;
import kr.pe.codda.common.sessionkey.ClientSymmetricKeyIF;
import kr.pe.codda.common.sessionkey.ServerSessionkeyIF;
import kr.pe.codda.common.sessionkey.ServerSessionkeyManager;
import kr.pe.codda.common.sessionkey.ServerSymmetricKeyIF;
import kr.pe.codda.common.util.CommonStaticUtil;
import kr.pe.codda.common.util.HexUtil;
import kr.pe.codda.impl.classloader.ClientMessageCodecManger;
import kr.pe.codda.impl.message.AccountSearchProcessReq.AccountSearchProcessReq;
import kr.pe.codda.impl.message.AccountSearchProcessRes.AccountSearchProcessRes;
import kr.pe.codda.impl.message.BinaryPublicKey.BinaryPublicKey;
import kr.pe.codda.impl.message.MessageResultRes.MessageResultRes;
import kr.pe.codda.weblib.common.AccountSearchType;
import kr.pe.codda.weblib.common.ValueChecker;
import kr.pe.codda.weblib.common.WebCommonStaticFinalVars;
import kr.pe.codda.weblib.jdf.AbstractServlet;

public class AccountSearchProcessSvl extends AbstractServlet {

	private static final long serialVersionUID = -8785148116253186154L;

	@Override
	protected void performTask(HttpServletRequest req, HttpServletResponse res) throws Exception {
		/**************** 파라미터 시작 *******************/
		String paramSessionKeyBase64 = req.getParameter(WebCommonStaticFinalVars.PARAMETER_KEY_NAME_OF_SESSION_KEY);
		String paramIVBase64 = req.getParameter(WebCommonStaticFinalVars.PARAMETER_KEY_NAME_OF_SESSION_KEY_IV);

		String paramEmailCipherBase64 = req.getParameter("email");
		String paramSecretAuthenticationValueCipherBase64 = req.getParameter("secretAuthenticationValue");
		String paramPasswordCipherBase64 = req.getParameter("pwd");
		String paramAccountSearchType = req.getParameter("accountSearchType");
		/**************** 파라미터 종료 *******************/
		// secretAuthenticationValue

		if (null == paramAccountSearchType) {
			String errorMessage = "the request parameter accountSearchType is null";
			String debugMessage = null;
			printErrorMessagePage(req, res, errorMessage, debugMessage);
			return;
		}

		byte accountSearchTypeValue;
		try {
			accountSearchTypeValue = Byte.parseByte(paramAccountSearchType);
		} catch (NumberFormatException e) {
			String errorMessage = "the request parameter accountSearchType is bad because it is not a byte type number";
			String debugMessage = new StringBuilder().append("the request parameter searchWhatType[")
					.append(paramAccountSearchType).append("] is not a byte type number").toString();
			printErrorMessagePage(req, res, errorMessage, debugMessage);
			return;
		}

		AccountSearchType accountSearchType = null;
		try {
			accountSearchType = AccountSearchType.valueOf(accountSearchTypeValue);
		} catch (IllegalArgumentException e) {
			String errorMessage = "the request parameter accountSearchType is bad because it is not a element of SearchWhatType";
			String debugMessage = new StringBuilder().append("the request parameter searchWhatType[")
					.append(paramAccountSearchType).append("] is not a element of SearchWhatType").toString();
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
		
		if (null == paramSecretAuthenticationValueCipherBase64) {
			String errorMessage = "the request parameter secretAuthenticationValue is null";
			String debugMessage = null;
			printErrorMessagePage(req, res, errorMessage, debugMessage);
			return;
		}

		if (AccountSearchType.PASSWORD.equals(accountSearchType)) {
			if (null == paramPasswordCipherBase64) {
				String errorMessage = "the request parameter pwd is null";
				String debugMessage = null;
				printErrorMessagePage(req, res, errorMessage, debugMessage);
				return;
			}
		}

		// searchWhatType

		log.info("param sessionkeyBase64=[{}]", paramSessionKeyBase64);
		log.info("param ivBase64=[{}]", paramIVBase64);
		log.info("param email=[{}]", paramEmailCipherBase64);
		log.info("param secretAuthenticationValue=[{}]", paramSecretAuthenticationValueCipherBase64);
		log.info("param pwd=[{}]", paramPasswordCipherBase64);

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

		
		byte[] secretAuthenticationValueBytes = webServerSymmetricKey
				.decrypt(CommonStaticUtil.Base64Decoder.decode(paramSecretAuthenticationValueCipherBase64));

		byte[] passwordBytes = null;

		if (AccountSearchType.PASSWORD.equals(accountSearchType)) {
			try {
				passwordBytes = webServerSymmetricKey
						.decrypt(CommonStaticUtil.Base64Decoder.decode(paramPasswordCipherBase64));
			} catch (IllegalArgumentException e) {
				String errorMessage = e.getMessage();
				String debugMessage = null;
				printErrorMessagePage(req, res, errorMessage, debugMessage);
				return;
			}
			
			try {
				ValueChecker.checkValidLoginPwd(passwordBytes);
			} catch (IllegalArgumentException e) {
				String errorMessage = e.getMessage();
				String debugMessage = null;

				printErrorMessagePage(req, res, errorMessage, debugMessage);
				return;
			}
		} else {
			passwordBytes = new byte[0];
		}
		
		String email = new String(emailBytes, CommonStaticFinalVars.CIPHER_CHARSET);
		String secretAuthenticationValue = new String(secretAuthenticationValueBytes, CommonStaticFinalVars.CIPHER_CHARSET);

		try {
			ValueChecker.checkValidEmail(email);
		} catch (IllegalArgumentException e) {
			String errorMessage = e.getMessage();
			String debugMessage = null;
			printErrorMessagePage(req, res, errorMessage, debugMessage);
			return;
		}
		
		
		try {
			CommonStaticUtil.Base64Decoder.decode(secretAuthenticationValue);
		} catch (IllegalArgumentException e) {
			String errorMessage = "비밀 값이 베이스 64가 아닙니다";
			String debugMessage = new StringBuilder()
					.append("비밀 값[")
					.append(secretAuthenticationValue)
					.append("]이 베이스 64가 아닙니다").toString();
			printErrorMessagePage(req, res, errorMessage, debugMessage);
			return;
		}

		AnyProjectConnectionPoolIF mainProjectConnectionPool = ConnectionPoolManager.getInstance()
				.getMainProjectConnectionPool();

		BinaryPublicKey binaryPublicKeyReq = new BinaryPublicKey();
		binaryPublicKeyReq.setPublicKeyBytes(webServerSessionkey.getDupPublicKeyBytes());

		AbstractMessage binaryPublicKeyOutputMessage = mainProjectConnectionPool
				.sendSyncInputMessage(ClientMessageCodecManger.getInstance(), binaryPublicKeyReq);

		if (!(binaryPublicKeyOutputMessage instanceof BinaryPublicKey)) {
			String errorMessage = "로그인 실패했습니다. 상세한 내용은 에러 로그를 참고하세요.";
			String debugMessage = new StringBuilder("입력 메시지[").append(binaryPublicKeyReq.getMessageID())
					.append("]에 대한 비 정상 출력 메시지[").append(binaryPublicKeyOutputMessage.toString()).append("] 도착")
					.toString();

			log.error(debugMessage);

			printErrorMessagePage(req, res, errorMessage, debugMessage);
			return;
		}

		BinaryPublicKey binaryPublicKeyRes = (BinaryPublicKey) binaryPublicKeyOutputMessage;
		byte[] binaryPublicKeyBytes = binaryPublicKeyRes.getPublicKeyBytes();

		ClientSessionKeyIF clientSessionKey = ClientSessionKeyManager.getInstance()
				.getNewClientSessionKey(binaryPublicKeyBytes, false);

		byte sessionKeyBytesOfServer[] = clientSessionKey.getDupSessionKeyBytes();
		byte ivBytesOfServer[] = clientSessionKey.getDupIVBytes();
		ClientSymmetricKeyIF clientSymmetricKey = clientSessionKey.getClientSymmetricKey();
		
		AccountSearchProcessReq accountSearchProcessReq = new AccountSearchProcessReq();
		accountSearchProcessReq.setAccountSearchType(accountSearchType.getValue());
		accountSearchProcessReq.setEmailCipherBase64(CommonStaticUtil.Base64Encoder.encodeToString(clientSymmetricKey.encrypt(emailBytes)));
		accountSearchProcessReq.setSecretAuthenticationValueCipherBase64(CommonStaticUtil.Base64Encoder.encodeToString(clientSymmetricKey.encrypt(secretAuthenticationValueBytes)));
		accountSearchProcessReq.setNewPwdCipherBase64(CommonStaticUtil.Base64Encoder.encodeToString(clientSymmetricKey.encrypt(passwordBytes)));
		accountSearchProcessReq.setSessionKeyBase64(CommonStaticUtil.Base64Encoder.encodeToString(sessionKeyBytesOfServer));
		accountSearchProcessReq.setIvBase64(CommonStaticUtil.Base64Encoder.encodeToString(ivBytesOfServer));
		accountSearchProcessReq.setIp(req.getRemoteAddr());
		
		Arrays.fill(passwordBytes, (byte)0);
		
		AbstractMessage outputMessage = mainProjectConnectionPool
				.sendSyncInputMessage(ClientMessageCodecManger.getInstance(), accountSearchProcessReq);
		
		if (! (outputMessage instanceof AccountSearchProcessRes)) {
			
			if ( (outputMessage instanceof MessageResultRes)) {
				MessageResultRes messageResultRes = (MessageResultRes) outputMessage;
				
				String errorMessage = messageResultRes.getResultMessage();
				String debugMessage = null;

				printErrorMessagePage(req, res, errorMessage, debugMessage);
				return;
			}
			
			String errorMessage = "아이디 혹은 비밀번호 찾기 처리가 실패했습니다. 상세한 내용은 에러 로그를 참고하세요.";
			String debugMessage = new StringBuilder("입력 메시지[")
					.append(accountSearchProcessReq.getMessageID())
					.append("]에 대한 비 정상 출력 메시지[")
					.append(outputMessage.toString())
					.append("] 도착").toString();
			
			log.error(debugMessage);

			printErrorMessagePage(req, res, errorMessage, debugMessage);
			return;
		}
		
		AccountSearchProcessRes accountSearchProcessRes = (AccountSearchProcessRes) outputMessage;
		req.setAttribute("accountSearchType", accountSearchType);
		req.setAttribute("accountSearchProcessRes", accountSearchProcessRes);
		printJspPage(req, res, "/jsp/member/AccountSearchProcess.jsp");
		
	}

}
