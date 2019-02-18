package kr.pe.codda.servlet.admin;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

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
import kr.pe.codda.impl.message.AdminLoginReq.AdminLoginReq;
import kr.pe.codda.impl.message.BinaryPublicKey.BinaryPublicKey;
import kr.pe.codda.impl.message.MessageResultRes.MessageResultRes;
import kr.pe.codda.weblib.common.WebCommonStaticFinalVars;
import kr.pe.codda.weblib.jdf.AbstractServlet;

/**
 * 관리자 로그인 처리 서블릿
 * @author Won Jonghoon
 *
 */
public class AdminLoginProcessSvl extends AbstractServlet {

	private static final long serialVersionUID = -8458712103045075706L;	
	

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
		
		
		if (null == paramUserIDCipherBase64) {
			String errorMessage = "the request parameter userID is null";
			String debugMessage = null;
			printErrorMessagePage(req, res, errorMessage, debugMessage);
			return;
		}
		
		
		if (null == paramPwdCipherBase64) {
			String errorMessage = "the request parameter paramPwdCipherBase64 is null";
			String debugMessage = null;
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
			sessionkeyBytes = CommonStaticUtil.Base64Decoder.decode(paramSessionKeyBase64);
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
			ivBytes = CommonStaticUtil.Base64Decoder.decode(paramIVBase64);
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
				
		
		// FIXME!
		log.info("한글 대칭키 암호문 base64={}", CommonStaticUtil.Base64Encoder.encodeToString(webServerSymmetricKey.encrypt("한글".getBytes("UTF8"))));

		byte[] userIDBytes = webServerSymmetricKey.decrypt(CommonStaticUtil.Base64Decoder.decode(paramUserIDCipherBase64));
		byte[] passwordBytes = webServerSymmetricKey.decrypt(CommonStaticUtil.Base64Decoder.decode(paramPwdCipherBase64));

		String userId = new String(userIDBytes, CommonStaticFinalVars.CIPHER_CHARSET);
		// String password = new String(passwordBytes,

		// log.info("id=[{}], password=[{}]", userId, password);
		
		// FIXME!
		// log.info("userID=[{}]", new String(userIDBytes, "UTF8"));

		AnyProjectConnectionPoolIF mainProjectConnectionPool = ConnectionPoolManager.getInstance()
				.getMainProjectConnectionPool();
		
		ClientSessionKeyIF clientSessionKey = null;
		
		synchronized (req) {
			clientSessionKey = (ClientSessionKeyIF)req.getAttribute(WebCommonStaticFinalVars.HTTPSESSION_KEY_NAME_OF_CLIENT_SESSIONKEY);
			
			if (null == clientSessionKey) {
				BinaryPublicKey binaryPublicKeyReq = new BinaryPublicKey();
				binaryPublicKeyReq.setPublicKeyBytes(webServerSessionkey.getDupPublicKeyBytes());

				AbstractMessage binaryPublicKeyOutputMessage = mainProjectConnectionPool.sendSyncInputMessage(ClientMessageCodecManger.getInstance(), binaryPublicKeyReq);

				if (!(binaryPublicKeyOutputMessage instanceof BinaryPublicKey)) {
					String errorMessage = "로그인 실패했습니다. 상세한 내용은 에러 로그를 참고하세요.";
					String debugMessage = new StringBuilder("입력 메시지[")
							.append(binaryPublicKeyReq.getMessageID())
							.append("]에 대한 비 정상 출력 메시지[")
							.append(binaryPublicKeyOutputMessage.toString())
							.append("] 도착").toString();
					
					log.error(debugMessage);

					printErrorMessagePage(req, res, errorMessage, debugMessage);
					return;
				}		
				
				
				BinaryPublicKey binaryPublicKeyRes = (BinaryPublicKey) binaryPublicKeyOutputMessage;
				byte[] binaryPublicKeyBytes = binaryPublicKeyRes.getPublicKeyBytes();

				clientSessionKey = ClientSessionKeyManager.getInstance()
						.getNewClientSessionKey(binaryPublicKeyBytes, false);
				
				req.setAttribute(WebCommonStaticFinalVars.HTTPSESSION_KEY_NAME_OF_CLIENT_SESSIONKEY, clientSessionKey);			
			}
		}

		byte sessionKeyBytesOfServer[] = clientSessionKey.getDupSessionKeyBytes();
		byte ivBytesOfServer[] = clientSessionKey.getDupIVBytes();
		ClientSymmetricKeyIF clientSymmetricKey = clientSessionKey.getClientSymmetricKey();
		AdminLoginReq adminLoginReq = new AdminLoginReq();

		adminLoginReq.setIdCipherBase64(CommonStaticUtil.Base64Encoder.encodeToString(clientSymmetricKey.encrypt(userIDBytes)));
		adminLoginReq.setPwdCipherBase64(CommonStaticUtil.Base64Encoder.encodeToString(clientSymmetricKey.encrypt(passwordBytes)));
		adminLoginReq.setSessionKeyBase64(CommonStaticUtil.Base64Encoder.encodeToString(sessionKeyBytesOfServer));
		adminLoginReq.setIvBase64(CommonStaticUtil.Base64Encoder.encodeToString(ivBytesOfServer));			

		AbstractMessage loginOutputMessage = mainProjectConnectionPool.sendSyncInputMessage(ClientMessageCodecManger.getInstance(), adminLoginReq);
		
		if (!(loginOutputMessage instanceof MessageResultRes)) {
			String errorMessage = "로그인 실패했습니다. 상세한 내용은 에러 로그를 참고하세요.";
			String debugMessage = new StringBuilder("입력 메시지[")
					.append(adminLoginReq.getMessageID())
					.append("]에 대한 비 정상 출력 메시지[")
					.append(loginOutputMessage.toString())
					.append("] 도착").toString();
			
			log.error(debugMessage);

			printErrorMessagePage(req, res, errorMessage, debugMessage);
			return;
		}		
		
		MessageResultRes messageResultRes = (MessageResultRes) loginOutputMessage;
		if (! messageResultRes.getIsSuccess()) {
			String debugMessage = null;
			printErrorMessagePage(req, res, messageResultRes.getResultMessage(), debugMessage);
			return;
		}
		
		HttpSession httpSession = req.getSession();
		httpSession.setAttribute(WebCommonStaticFinalVars.HTTPSESSION_KEY_NAME_OF_LOGINED_ADMIN_ID, userId);
		
		req.setAttribute(WebCommonStaticFinalVars.REQUEST_KEY_NAME_OF_WEB_SERVER_SYMMETRIC_KEY, 
				webServerSymmetricKey);
		req.setAttribute(WebCommonStaticFinalVars.REQUEST_KEY_NAME_OF_MODULUS_HEX_STRING,
				webServerSessionkey.getModulusHexStrForWeb());

		printJspPage(req, res, "/jsp/member/AdminLoginProcess.jsp");
		return;		
	}
}
