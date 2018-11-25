package kr.pe.codda.servlet;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.codec.binary.Base64;

import kr.pe.codda.client.AnyProjectConnectionPoolIF;
import kr.pe.codda.client.ConnectionPoolManager;
import kr.pe.codda.common.exception.SymmetricException;
import kr.pe.codda.common.message.AbstractMessage;
import kr.pe.codda.common.sessionkey.ClientSessionKeyIF;
import kr.pe.codda.common.sessionkey.ClientSessionKeyManager;
import kr.pe.codda.common.sessionkey.ClientSymmetricKeyIF;
import kr.pe.codda.common.sessionkey.ServerSessionkeyIF;
import kr.pe.codda.common.sessionkey.ServerSessionkeyManager;
import kr.pe.codda.common.sessionkey.ServerSymmetricKeyIF;
import kr.pe.codda.common.util.HexUtil;
import kr.pe.codda.impl.message.BinaryPublicKey.BinaryPublicKey;
import kr.pe.codda.impl.message.MessageResultRes.MessageResultRes;
import kr.pe.codda.impl.message.UserLoginReq.UserLoginReq;
import kr.pe.codda.impl.message.UserLoginRes.UserLoginRes;
import kr.pe.codda.weblib.common.MemberType;
import kr.pe.codda.weblib.common.WebCommonStaticFinalVars;
import kr.pe.codda.weblib.jdf.AbstractServlet;

public class UserLoginProcessSvl extends AbstractServlet {

	
	private static final long serialVersionUID = -5979668829130203071L;
	
	/*private void printUserLoginFailureCallBackPage(HttpServletRequest req, HttpServletResponse res,
			ServerSymmetricKeyIF webServerSymmetricKey,
			String modulusHexString, 
			String errorMessage) {
		req.setAttribute(WebCommonStaticFinalVars.REQUEST_KEY_NAME_OF_WEB_SERVER_SYMMETRIC_KEY, webServerSymmetricKey);
		req.setAttribute(WebCommonStaticFinalVars.REQUEST_KEY_NAME_OF_MODULUS_HEX_STRING,
				modulusHexString);
		req.setAttribute("errorMessage", errorMessage);
		
		printJspPage(req, res, "/jsp/member/UserLoginFailureCallBack.jsp");
	}*/


	@Override
	protected void performTask(HttpServletRequest req, HttpServletResponse res)
			throws Exception {
		/**************** 파라미터 시작 *******************/
		String paramSessionKeyBase64 = req.getParameter(WebCommonStaticFinalVars.PARAMETER_KEY_NAME_OF_SESSION_KEY);
		String paramIVBase64 = req.getParameter(WebCommonStaticFinalVars.PARAMETER_KEY_NAME_OF_SESSION_KEY_IV);

		String paramUserIDCipherBase64 = req.getParameter("userID");
		String paramPwdCipherBase64 = req.getParameter("pwd");
		/**************** 파라미터 종료 *******************/

		
		if (null == paramSessionKeyBase64) {
			String errorMessage = "the request parameter paramSessionKeyBase64 is null";
			String debugMessage = errorMessage;
			printErrorMessagePage(req, res, errorMessage, debugMessage);
			return;
		}
		
		if (! Base64.isBase64(paramSessionKeyBase64)) {
			String errorMessage = "the request parameter paramSessionKeyBase64 is not a base64 string";
			String debugMessage = errorMessage;
			printErrorMessagePage(req, res, errorMessage, debugMessage);
			return;
		}
		
		if (null == paramIVBase64) {
			String errorMessage = "the request parameter paramIVBase64 is null";
			String debugMessage = errorMessage;
			printErrorMessagePage(req, res, errorMessage, debugMessage);
			return;
		}
		
		if (! Base64.isBase64(paramIVBase64)) {
			String errorMessage = "the request parameter paramIVBase64 is not a base64 string";
			String debugMessage = errorMessage;
			printErrorMessagePage(req, res, errorMessage, debugMessage);
			return;
		}
		
		if (null == paramUserIDCipherBase64) {
			String errorMessage = "the request parameter userID is null";
			String debugMessage = errorMessage;
			printErrorMessagePage(req, res, errorMessage, debugMessage);
			return;
		}
		
		if (! Base64.isBase64(paramUserIDCipherBase64)) {
			String errorMessage = "the request parameter userID is not a base64 cipher text";
			String debugMessage = errorMessage;
			printErrorMessagePage(req, res, errorMessage, debugMessage);
			return;
		}
		
		if (null == paramPwdCipherBase64) {
			String errorMessage = "the request parameter paramPwdCipherBase64 is null";
			String debugMessage = errorMessage;
			printErrorMessagePage(req, res, errorMessage, debugMessage);
			return;
		}
		
		if (! Base64.isBase64(paramPwdCipherBase64)) {
			String errorMessage = "the request parameter paramPwdCipherBase64 is not a base64 cipher string";
			String debugMessage = errorMessage;
			printErrorMessagePage(req, res, errorMessage, debugMessage);
			return;
		} 		
		
		
		/*if (successURL.indexOf('/') != 0) {
			String errorMessage = "the request parameter successURL doesn't begin a char '/'";
			String debugMessage = errorMessage;
			printErrorMessagePage(req, res, errorMessage, debugMessage);
			return;
		}*/

		log.info("param sessionkeyBase64=[{}]", paramSessionKeyBase64);
		log.info("param ivBase64=[{}]", paramIVBase64);
		log.info("param userID=[{}]", paramUserIDCipherBase64);
		log.info("param pwd=[{}}]", paramPwdCipherBase64);

		// req.setAttribute("isSuccess", Boolean.FALSE);

		byte[] sessionkeyBytes = null;
		try {
			sessionkeyBytes = org.apache.commons.codec.binary.Base64.decodeBase64(paramSessionKeyBase64);
		} catch(Exception e) {
			log.warn("base64 encoding error for the parameter paramSessionKeyBase64[{}], errormessage=[{}]", paramSessionKeyBase64, e.getMessage());
			
			String errorMessage = "세션키 파라미터가 잘못되었습니다";
			String debugMessage = new StringBuilder()
			.append("the parameter '")
			.append(WebCommonStaticFinalVars.PARAMETER_KEY_NAME_OF_SESSION_KEY)
			.append("'[")
			.append(paramSessionKeyBase64)
			.append("] is not a base64 encoding string, errmsg=")
			.append(e.getMessage()).toString();
			
			printErrorMessagePage(req, res, errorMessage, debugMessage);
			return;
		}
		byte[] ivBytes = null;
		try {
			ivBytes = org.apache.commons.codec.binary.Base64.decodeBase64(paramIVBase64);
		} catch(Exception e) {
			log.warn("base64 encoding error for the parameter paramIVBase64[{}], errormessage=[{}]", paramIVBase64, e.getMessage());
			
			String errorMessage = "세션키 소금 파라미터가 잘못되었습니다";
			String debugMessage = new StringBuilder()
			.append("the parameter '")
			.append(WebCommonStaticFinalVars.PARAMETER_KEY_NAME_OF_SESSION_KEY_IV)
			.append("'[")
			.append(paramIVBase64)
			.append("] is not a base64 encoding string, errmsg=")
			.append(e.getMessage()).toString();

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
			webServerSymmetricKey = webServerSessionkey.getNewInstanceOfServerSymmetricKey(true, sessionkeyBytes, ivBytes);
		} catch(IllegalArgumentException e) {
			String errorMessage = "웹 세션키 인스턴스 생성 실패";
			log.warn(errorMessage, e);
			
			String debugMessage = new StringBuilder("sessionkeyBytes=[")
					.append(HexUtil.getHexStringFromByteArray(sessionkeyBytes))
					.append("], ivBytes=[")
					.append(HexUtil.getHexStringFromByteArray(ivBytes))
					.append("]").toString();
			
			printErrorMessagePage(req, res, errorMessage, debugMessage);
			return;
		} catch(SymmetricException e) {
			String errorMessage = "웹 세션키 인스턴스 생성 실패";
			log.warn(errorMessage, e);
			
			String debugMessage = new StringBuilder("sessionkeyBytes=[")
					.append(HexUtil.getHexStringFromByteArray(sessionkeyBytes))
					.append("], ivBytes=[")
					.append(HexUtil.getHexStringFromByteArray(ivBytes))
					.append("]").toString();
			
			printErrorMessagePage(req, res, errorMessage, debugMessage);
			return;
		}
				
		byte[] userIDBytes = webServerSymmetricKey.decrypt(Base64.decodeBase64(paramUserIDCipherBase64));
		byte[] passwordBytes = webServerSymmetricKey.decrypt(Base64.decodeBase64(paramPwdCipherBase64));

		// String userId = new String(userIDBytes, CommonStaticFinalVars.CIPHER_CHARSET);
		// String password = new String(passwordBytes,
		// CommonStaticFinalVars.SINNORI_CIPHER_CHARSET);

		// log.info("id=[{}], password=[{}]", userId, password);
		
		// FIXME!
		//log.info("userID=[{}]", new String(userIDBytes, "UTF8"));
		
		
		AnyProjectConnectionPoolIF mainProjectConnectionPool = ConnectionPoolManager.getInstance()
				.getMainProjectConnectionPool();
		
		ClientSessionKeyIF clientSessionKey = null;
		
		synchronized (req) {
			clientSessionKey = (ClientSessionKeyIF)req.getAttribute(WebCommonStaticFinalVars.HTTPSESSION_KEY_NAME_OF_CLIENT_SESSIONKEY);
			
			if (null == clientSessionKey) {
				BinaryPublicKey binaryPublicKeyReq = new BinaryPublicKey();
				binaryPublicKeyReq.setPublicKeyBytes(webServerSessionkey.getDupPublicKeyBytes());

				AbstractMessage binaryPublicKeyOutputMessage = mainProjectConnectionPool.sendSyncInputMessage(binaryPublicKeyReq);
				
				if (!(binaryPublicKeyOutputMessage instanceof BinaryPublicKey)) {
					String errorMessage = "로그인 실패했습니다. 상세한 내용은 에러 로그를 참고하세요.";
					String debugMessage = new StringBuilder("입력 메시지[")
							.append(binaryPublicKeyReq.getMessageID())
							.append("]에 대한 비 정상 출력 메시지[")
							.append(binaryPublicKeyOutputMessage.toString())
							.append("] 도착").toString();
					
					log.error(debugMessage);

					printErrorMessagePage(req, res, 
							errorMessage, 
							debugMessage);
					return;
				}		
				
				
				BinaryPublicKey binaryPublicKeyRes = (BinaryPublicKey) binaryPublicKeyOutputMessage;
				byte[] binaryPublicKeyBytes = binaryPublicKeyRes.getPublicKeyBytes();

				clientSessionKey = ClientSessionKeyManager.getInstance()
						.getNewClientSessionKey(binaryPublicKeyBytes);
				
				req.setAttribute(WebCommonStaticFinalVars.HTTPSESSION_KEY_NAME_OF_CLIENT_SESSIONKEY, clientSessionKey);			
			}
		}		
		

		byte sessionKeyBytesOfServer[] = clientSessionKey.getDupSessionKeyBytes();
		byte ivBytesOfServer[] = clientSessionKey.getDupIVBytes();
		ClientSymmetricKeyIF clientSymmetricKey = clientSessionKey.getClientSymmetricKey();
		UserLoginReq userLoginReq = new UserLoginReq();

		userLoginReq.setIdCipherBase64(Base64.encodeBase64String(clientSymmetricKey.encrypt(userIDBytes)));
		userLoginReq.setPwdCipherBase64(Base64.encodeBase64String(clientSymmetricKey.encrypt(passwordBytes)));
		userLoginReq.setSessionKeyBase64(Base64.encodeBase64String(sessionKeyBytesOfServer));
		userLoginReq.setIvBase64(Base64.encodeBase64String(ivBytesOfServer));			

		AbstractMessage outputMessage = mainProjectConnectionPool.sendSyncInputMessage(userLoginReq);
		
		
		if ((outputMessage instanceof MessageResultRes)) {
			MessageResultRes messageResultRes = (MessageResultRes) outputMessage;
			
			printErrorMessagePage(req, res, 
					messageResultRes.getResultMessage(), 
					null);
			return;
		}
		
		if (! (outputMessage instanceof UserLoginRes)) {
			String errorMessage = "일반 유저 로그인 실패했습니다. 상세한 내용은 에러 로그를 참고하세요.";
			String debugMessage = new StringBuilder("입력 메시지[")
					.append(userLoginReq.getMessageID())
					.append("]에 대한 비 정상 출력 메시지[")
					.append(outputMessage.toString())
					.append("] 도착").toString();
			
			log.error(debugMessage);

			printErrorMessagePage(req, res, 
					errorMessage, 
					debugMessage);
			return;
		}
		
		UserLoginRes userLoginRes = (UserLoginRes) outputMessage;
		MemberType memberType = MemberType.USER;
		
		try {
			memberType =  MemberType.valueOf(userLoginRes.getMemberType(), false);
		} catch(IllegalArgumentException e) {
			log.warn("사용자[{}]의 멤버 종류[{}] 가 잘못되어 멤버 종류를 '일반 유저'로 강제 변경합니다", userLoginRes.getUserID(), userLoginRes.getMemberType());
		}
		
		HttpSession httpSession = req.getSession();
		httpSession.setAttribute(WebCommonStaticFinalVars.HTTPSESSION_KEY_NAME_OF_LOGINED_USER_ID, userLoginRes.getUserID());
		httpSession.setAttribute(WebCommonStaticFinalVars.HTTPSESSION_KEY_NAME_OF_LOGINED_USER_MEMBER_TYPE, memberType);
		
		req.setAttribute(WebCommonStaticFinalVars.REQUEST_KEY_NAME_OF_WEB_SERVER_SYMMETRIC_KEY, webServerSymmetricKey);
		req.setAttribute(WebCommonStaticFinalVars.REQUEST_KEY_NAME_OF_MODULUS_HEX_STRING,
				webServerSessionkey.getModulusHexStrForWeb());
		printJspPage(req, res, "/jsp/member/UserLoginProcess.jsp");
		return;
		
	}

}
