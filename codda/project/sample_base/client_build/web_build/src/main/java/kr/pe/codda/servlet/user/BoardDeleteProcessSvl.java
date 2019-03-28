package kr.pe.codda.servlet.user;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import kr.pe.codda.client.AnyProjectConnectionPoolIF;
import kr.pe.codda.client.ConnectionPoolManager;
import kr.pe.codda.common.exception.SymmetricException;
import kr.pe.codda.common.message.AbstractMessage;
import kr.pe.codda.common.sessionkey.ServerSessionkeyIF;
import kr.pe.codda.common.sessionkey.ServerSessionkeyManager;
import kr.pe.codda.common.sessionkey.ServerSymmetricKeyIF;
import kr.pe.codda.common.util.CommonStaticUtil;
import kr.pe.codda.common.util.HexUtil;
import kr.pe.codda.impl.classloader.ClientMessageCodecManger;
import kr.pe.codda.impl.message.BoardDeleteReq.BoardDeleteReq;
import kr.pe.codda.impl.message.MessageResultRes.MessageResultRes;
import kr.pe.codda.weblib.common.AccessedUserInformation;
import kr.pe.codda.weblib.common.ValueChecker;
import kr.pe.codda.weblib.common.WebCommonStaticFinalVars;
import kr.pe.codda.weblib.exception.WebClientException;
import kr.pe.codda.weblib.jdf.AbstractServlet;

public class BoardDeleteProcessSvl extends AbstractServlet {

	private static final long serialVersionUID = -238411848490485952L;

	@Override
	protected void performTask(HttpServletRequest req, HttpServletResponse res)
			throws Exception {
		/**************** 파라미터 시작 *******************/
		String paramSessionKeyBase64 = req.getParameter(WebCommonStaticFinalVars.PARAMETER_KEY_NAME_OF_SESSION_KEY);
		String paramIVBase64 = req.getParameter(WebCommonStaticFinalVars.PARAMETER_KEY_NAME_OF_SESSION_KEY_IV);
		String paramBoardID = req.getParameter("boardID");
		String paramBoardNo = req.getParameter("boardNo");
		String paramPwdCipherBase64 = req.getParameter("pwd");
		/**************** 파라미터 종료 *******************/
		
		// FIXME!
		log.info("paramPwdCipherBase64={}", paramPwdCipherBase64);
		
		if (null == paramSessionKeyBase64) {
			String errorMessage = "the request parameter paramSessionKeyBase64 is null";
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
		
		short boardID = -1;
		try {
			boardID = ValueChecker.checkValidBoardID(paramBoardID);
		} catch(IllegalArgumentException e) {
			String errorMessage = e.getMessage();
			String debugMessage = null;
			printErrorMessagePage(req, res, errorMessage, debugMessage);
			return;
		}
		
		long boardNo = 0L;
		try {
			boardNo = ValueChecker.checkValidBoardNo(paramBoardNo);
		} catch(IllegalArgumentException e) {
			String errorMessage = e.getMessage();
			String debugMessage = null;
			printErrorMessagePage(req, res, errorMessage, debugMessage);
			return;
		}
		
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
			webServerSymmetricKey = webServerSessionkey
					.getNewInstanceOfServerSymmetricKey(true, sessionkeyBytes,
							ivBytes);
		} catch (IllegalArgumentException e) {
			String errorMessage = "웹 세션키 인스턴스 생성 실패";
			log.warn(errorMessage, e);

			String debugMessage = new StringBuilder("sessionkeyBytes=[")
					.append(HexUtil.getHexStringFromByteArray(sessionkeyBytes))
					.append("], ivBytes=[")
					.append(HexUtil.getHexStringFromByteArray(ivBytes))
					.append("]").toString();

			printErrorMessagePage(req, res, errorMessage, debugMessage);
			return;
		} catch (SymmetricException e) {
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

		String pwdHashBase64 = null;
		if (null == paramPwdCipherBase64) {
			pwdHashBase64 = "";
		} else {			
			byte[] passwordBytes = webServerSymmetricKey.decrypt(CommonStaticUtil.Base64Decoder
					.decode(paramPwdCipherBase64));
			
			MessageDigest md = null;
			try {
				md = MessageDigest.getInstance(WebCommonStaticFinalVars.BOARD_HASH_ALGORITHM);
			} catch (NoSuchAlgorithmException e) {
				String errorMessage = "fail to get a MessageDigest class instance";
				log.warn(errorMessage, e);			
				
				String debugMessage = new StringBuilder("the 'algorithm'[")
						.append(WebCommonStaticFinalVars.BOARD_HASH_ALGORITHM)
						.append("], errmsg=")
						.append(e.getMessage()).toString();
				throw new WebClientException(errorMessage, debugMessage);
			}
			
			md.update(passwordBytes);
			pwdHashBase64 = CommonStaticUtil.Base64Encoder.encodeToString(md.digest());
		}
		
		
		AccessedUserInformation accessedUserformation = getAccessedUserInformation(req);
		
		BoardDeleteReq boardDeleteReq = new BoardDeleteReq();
		boardDeleteReq.setRequestedUserID(accessedUserformation.getUserID());
		boardDeleteReq.setBoardID(boardID);
		boardDeleteReq.setBoardNo(boardNo);
		boardDeleteReq.setPwdHashBase64(pwdHashBase64);
		boardDeleteReq.setIp(req.getRemoteAddr());
		
		AnyProjectConnectionPoolIF mainProjectConnectionPool = ConnectionPoolManager
				.getInstance().getMainProjectConnectionPool();
		AbstractMessage outputMessage = mainProjectConnectionPool
				.sendSyncInputMessage(ClientMessageCodecManger.getInstance(),
						boardDeleteReq);

		if (!(outputMessage instanceof MessageResultRes)) {

			String errorMessage = "게시판 삭제가 실패했습니다";
			String debugMessage = new StringBuilder("입력 메시지[")
					.append(boardDeleteReq.getMessageID())
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
			printErrorMessagePage(req, res, errorMessage, null);
			return;
		}

		req.setAttribute("boardNo", paramBoardNo);
		printJspPage(req, res, "/jsp/community/BoardDeleteProcess.jsp");
		return;
	}

}
