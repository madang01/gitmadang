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
import kr.pe.codda.impl.message.BinaryPublicKey.BinaryPublicKey;
import kr.pe.codda.impl.message.MemberRegisterReq.MemberRegisterReq;
import kr.pe.codda.impl.message.MessageResultRes.MessageResultRes;
import kr.pe.codda.weblib.common.WebCommonStaticFinalVars;
import kr.pe.codda.weblib.jdf.AbstractServlet;
import nl.captcha.Captcha;

public class MembershipProcessSvl extends AbstractServlet {

	private static final long serialVersionUID = 2742729909269799873L;

	@Override
	protected void performTask(HttpServletRequest req, HttpServletResponse res)
			throws Exception {
		/**************** 파라미터 시작 *******************/
		String paramSessionKeyBase64 = req
				.getParameter(WebCommonStaticFinalVars.PARAMETER_KEY_NAME_OF_SESSION_KEY);
		String paramIVBase64 = req
				.getParameter(WebCommonStaticFinalVars.PARAMETER_KEY_NAME_OF_SESSION_KEY_IV);

		String paramUserID = req.getParameter("userID");
		String paramPwd = req.getParameter("pwd");
		String paramNickname = req.getParameter("nickname");
		String paramPwdHint = req.getParameter("pwdHint");
		String paramPwdAnswer = req.getParameter("pwdAnswer");
		String paramCaptchaAnswer = req.getParameter("captchaAnswer");
		/**************** 파라미터 종료 *******************/

		log.info(
				"param sessionkeyBase64=[{}], param ivBase64=[{}], "
						+ "param userID=[{}], param pwd=[{}], param nickname=[{}], "
						+ "param pwdHint=[{}], param pwdAnswer=[{}], param answer=[{}]",
				paramSessionKeyBase64, paramIVBase64, paramUserID, paramPwd,
				paramNickname, paramPwdHint, paramPwdAnswer, paramCaptchaAnswer);

		if (null == paramSessionKeyBase64) {
			String errorMessage = "세션키 값을 입력해 주세요";
			String debugMessage = null;
			printErrorMessagePage(req, res, errorMessage, debugMessage);
			return;
		}

		if (null == paramIVBase64) {
			String errorMessage = "IV 값을 입력해 주세요";
			String debugMessage = null;
			printErrorMessagePage(req, res, errorMessage, debugMessage);
			return;
		}

		if (null == paramUserID) {
			String errorMessage = "아이디 값을 입력해 주세요";
			String debugMessage = null;
			printErrorMessagePage(req, res, errorMessage, debugMessage);
			return;
		}

		if (null == paramPwd) {
			String errorMessage = "비밀번호 값을 입력해 주세요";
			String debugMessage = null;
			printErrorMessagePage(req, res, errorMessage, debugMessage);
			return;
		}

		if (null == paramNickname) {
			String errorMessage = "별명 값을 입력해 주세요";
			String debugMessage = null;
			printErrorMessagePage(req, res, errorMessage, debugMessage);
			return;
		}

		if (null == paramPwdHint) {
			String errorMessage = "비밀번호 분실시 힌트 값을 입력해 주세요";
			String debugMessage = null;
			printErrorMessagePage(req, res, errorMessage, debugMessage);
			return;
		}

		if (null == paramPwdAnswer) {
			String errorMessage = "비밀번호 분실시 답변 값을 입력해 주세요";
			String debugMessage = null;
			printErrorMessagePage(req, res, errorMessage, debugMessage);
			return;
		}

		if (null == paramCaptchaAnswer) {
			String errorMessage = "Captcha 값을 입력해 주세요";
			String debugMessage = null;
			printErrorMessagePage(req, res, errorMessage, debugMessage);
			return;
		}

		byte[] sessionkeyBytes = null;
		try {
			sessionkeyBytes = CommonStaticUtil.Base64Decoder
					.decode(paramSessionKeyBase64);
		} catch (Exception e) {
			log.warn(
					"base64 encoding error for the parameter paramSessionKeyBase64[{}], errormessage=[{}]",
					paramSessionKeyBase64, e.getMessage());

			String errorMessage = "세션키 파라미터가 잘못되었습니다";
			String debugMessage = new StringBuilder()
					.append("the parameter '")
					.append(WebCommonStaticFinalVars.PARAMETER_KEY_NAME_OF_SESSION_KEY)
					.append("'[").append(paramSessionKeyBase64)
					.append("] is not a base64 encoding string, errmsg=")
					.append(e.getMessage()).toString();

			printErrorMessagePage(req, res, errorMessage, debugMessage);
			return;
		}
		byte[] ivBytes = null;
		try {
			ivBytes = CommonStaticUtil.Base64Decoder.decode(paramIVBase64);
		} catch (Exception e) {
			log.warn(
					"base64 encoding error for the parameter paramIVBase64[{}], errormessage=[{}]",
					paramIVBase64, e.getMessage());

			String errorMessage = "세션키 소금 파라미터가 잘못되었습니다";

			String debugMessage = new StringBuilder()
					.append("the parameter '")
					.append(WebCommonStaticFinalVars.PARAMETER_KEY_NAME_OF_SESSION_KEY_IV)
					.append("'[").append(paramIVBase64)
					.append("] is not a base64 encoding string, errmsg=")
					.append(e.getMessage()).toString();

			printErrorMessagePage(req, res, errorMessage, debugMessage);
			return;
		}

		ServerSessionkeyIF webServerSessionkey = null;
		// ServerSymmetricKeyIF serverSymmetricKey = null;
		try {
			ServerSessionkeyManager serverSessionkeyManager = ServerSessionkeyManager
					.getInstance();
			webServerSessionkey = serverSessionkeyManager
					.getMainProjectServerSessionkey();
			// serverSymmetricKey =
			// serverSessionkey.getNewInstanceOfServerSymmetricKey(sessionkeyBytes,
			// ivBytes);
		} catch (SymmetricException e) {
			String errorMessage = "fail to get a ServerSessionkeyManger class instance";
			log.warn(errorMessage, e);

			String debugMessage = e.getMessage();
			printErrorMessagePage(req, res, errorMessage, debugMessage);
			return;
		}

		ServerSymmetricKeyIF webServerSymmetricKey = null;
		try {
			webServerSymmetricKey = webServerSessionkey
					.getNewInstanceOfServerSymmetricKey(true, sessionkeyBytes,
							ivBytes);
		} catch (IllegalArgumentException e) {
			String errorMessage = "웹 세션키 인스턴스 생성 실패";

			String debugMessage = new StringBuilder("sessionkeyBytes=[")
					.append(HexUtil.getHexStringFromByteArray(sessionkeyBytes))
					.append("], ivBytes=[")
					.append(HexUtil.getHexStringFromByteArray(ivBytes))
					.append("]").toString();

			log.warn(debugMessage, e);

			printErrorMessagePage(req, res, errorMessage, debugMessage);
			return;
		} catch (SymmetricException e) {
			String errorMessage = "웹 세션키 인스턴스 생성 실패";
			String debugMessage = new StringBuilder("sessionkeyBytes=[")
					.append(HexUtil.getHexStringFromByteArray(sessionkeyBytes))
					.append("], ivBytes=[")
					.append(HexUtil.getHexStringFromByteArray(ivBytes))
					.append("]").toString();

			log.warn(debugMessage, e);

			printErrorMessagePage(req, res, errorMessage, debugMessage);
			return;
		}

		byte[] userIdBytes = webServerSymmetricKey
				.decrypt(CommonStaticUtil.Base64Decoder.decode(paramUserID));
		byte[] passwordBytes = webServerSymmetricKey
				.decrypt(CommonStaticUtil.Base64Decoder.decode(paramPwd));
		byte[] nicknameBytes = webServerSymmetricKey
				.decrypt(CommonStaticUtil.Base64Decoder.decode(paramNickname));
		byte[] pwdHintBytes = webServerSymmetricKey
				.decrypt(CommonStaticUtil.Base64Decoder.decode(paramPwdHint));
		byte[] pwdAnswerBytes = webServerSymmetricKey
				.decrypt(CommonStaticUtil.Base64Decoder.decode(paramPwdAnswer));
		byte[] answerBytes = webServerSymmetricKey
				.decrypt(CommonStaticUtil.Base64Decoder
						.decode(paramCaptchaAnswer));

		String answer = new String(answerBytes,
				CommonStaticFinalVars.CIPHER_CHARSET);

		HttpSession httpSession = req.getSession();
		Captcha captcha = (Captcha) httpSession.getAttribute(Captcha.NAME);
		if (!captcha.isCorrect(answer)) {

			String debugMessage = String.format(
					"사용자가 입력한 Captcha 값[%s]과 내부 Captcha 값[%s]이 다릅니다.", answer,
					captcha.getAnswer());
			log.warn(debugMessage);

			String errorMessage = "입력한 Captcha 값이 틀렸습니다.";
			printErrorMessagePage(req, res, errorMessage, debugMessage);
			return;
		}

		httpSession.removeAttribute(Captcha.NAME);

		// log.info("userId=[{}]", userId);

		AnyProjectConnectionPoolIF mainProjectConnectionPool = ConnectionPoolManager
				.getInstance().getMainProjectConnectionPool();

		BinaryPublicKey binaryPublicKeyReq = new BinaryPublicKey();
		binaryPublicKeyReq.setPublicKeyBytes(webServerSessionkey
				.getDupPublicKeyBytes());

		AbstractMessage binaryPublicKeyOutputMessage = mainProjectConnectionPool
				.sendSyncInputMessage(ClientMessageCodecManger.getInstance(),
						binaryPublicKeyReq);
		if (!(binaryPublicKeyOutputMessage instanceof BinaryPublicKey)) {
			String errorMessage = "회원 가입이 실패했습니다";
			String debugMessage = new StringBuilder("입력 메시지[")
					.append(binaryPublicKeyReq.getMessageID())
					.append("]에 대한 비 정상 출력 메시지[")
					.append(binaryPublicKeyOutputMessage.toString())
					.append("] 도착").toString();

			log.error(debugMessage);

			printErrorMessagePage(req, res, errorMessage, debugMessage);
			return;
		}

		BinaryPublicKey binaryPublicKeyOutObj = (BinaryPublicKey) binaryPublicKeyOutputMessage;
		byte[] binaryPublicKeyBytes = binaryPublicKeyOutObj.getPublicKeyBytes();
		ClientSessionKeyIF clientSessionKey = ClientSessionKeyManager
				.getInstance().getNewClientSessionKey(binaryPublicKeyBytes,
						false);

		byte sessionKeyBytesOfServer[] = clientSessionKey
				.getDupSessionKeyBytes();
		byte ivBytesOfServer[] = clientSessionKey.getDupIVBytes();
		ClientSymmetricKeyIF clientSymmetricKey = clientSessionKey
				.getClientSymmetricKey();

		MemberRegisterReq memberRegisterReq = new MemberRegisterReq();


		memberRegisterReq.setIdCipherBase64(CommonStaticUtil.Base64Encoder
				.encodeToString(clientSymmetricKey.encrypt(userIdBytes)));
		memberRegisterReq.setPwdCipherBase64(CommonStaticUtil.Base64Encoder
				.encodeToString(clientSymmetricKey.encrypt(passwordBytes)));
		memberRegisterReq.setNicknameCipherBase64(CommonStaticUtil.Base64Encoder
				.encodeToString(clientSymmetricKey.encrypt(nicknameBytes)));
		memberRegisterReq.setHintCipherBase64(CommonStaticUtil.Base64Encoder
				.encodeToString(clientSymmetricKey.encrypt(pwdHintBytes)));
		memberRegisterReq.setAnswerCipherBase64(CommonStaticUtil.Base64Encoder
				.encodeToString(clientSymmetricKey.encrypt(pwdAnswerBytes)));
		memberRegisterReq.setSessionKeyBase64(CommonStaticUtil.Base64Encoder
				.encodeToString(sessionKeyBytesOfServer));
		memberRegisterReq.setIvBase64(CommonStaticUtil.Base64Encoder
				.encodeToString(ivBytesOfServer));
		memberRegisterReq.setIp(req.getRemoteAddr());

		AbstractMessage memberRegisterOutputMessage = mainProjectConnectionPool
				.sendSyncInputMessage(ClientMessageCodecManger.getInstance(),
						memberRegisterReq);
		if (!(memberRegisterOutputMessage instanceof MessageResultRes)) {
			String errorMessage = "회원 가입이 실패했습니다";
			String debugMessage = new StringBuilder("입력 메시지[")
					.append(memberRegisterReq.getMessageID())
					.append("]에 대한 비 정상 출력 메시지[")
					.append(memberRegisterOutputMessage.toString())
					.append("] 도착").toString();

			log.error(debugMessage);

			printErrorMessagePage(req, res, errorMessage, debugMessage);
			return;
		}

		MessageResultRes messageResultRes = (MessageResultRes) memberRegisterOutputMessage;

		if (!messageResultRes.getIsSuccess()) {
			printErrorMessagePage(req, res,
					messageResultRes.getResultMessage(), null);
			return;
		}

		printJspPage(req, res, "/jsp/member/MembershipProcess.jsp");
		return;
	}

}
