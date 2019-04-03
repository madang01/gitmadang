package kr.pe.codda.servlet.user;

import java.util.HashMap;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import kr.pe.codda.common.etc.CommonStaticFinalVars;
import kr.pe.codda.common.exception.SymmetricException;
import kr.pe.codda.common.sessionkey.ServerSessionkeyIF;
import kr.pe.codda.common.sessionkey.ServerSessionkeyManager;
import kr.pe.codda.common.sessionkey.ServerSymmetricKeyIF;
import kr.pe.codda.common.util.CommonStaticUtil;
import kr.pe.codda.common.util.HexUtil;
import kr.pe.codda.weblib.common.AccessedUserInformation;
import kr.pe.codda.weblib.common.MemberRoleType;
import kr.pe.codda.weblib.common.WebCommonStaticFinalVars;
import kr.pe.codda.weblib.jdf.AbstractServlet;

public class HardCodingMemberLoginProcessSvl extends AbstractServlet {
	
	class FreePassUserInfo {
		private String userID = null;
		private String userName = null;
		private String password = null;
		private MemberRoleType memberType = null;
		
		public FreePassUserInfo(String userID, String userName, 
				String password, MemberRoleType memberType) {
			super();
			this.userID = userID;
			this.userName = userName;
			this.password = password;
			this.memberType = memberType;
		}

		public String getUserID() {
			return userID;
		}
		
		public String getUserName() {
			return userName;
		}

		public String getPassword() {
			return password;
		}

		public MemberRoleType getMemberType() {
			return memberType;
		}
	}

	private static final long serialVersionUID = -4900821586755098845L;
	
	private HashMap<String, FreePassUserInfo> freePassUserInfoHash = null;
	
	public HardCodingMemberLoginProcessSvl() {
		freePassUserInfoHash = new HashMap<String, FreePassUserInfo>();
		
		freePassUserInfoHash.put("admin", new FreePassUserInfo("admin", "관리자", "test1234$", MemberRoleType.ADMIN));
		freePassUserInfoHash.put("test01", new FreePassUserInfo("test01", "테스터1", "test1234$", MemberRoleType.MEMBER));
		freePassUserInfoHash.put("test02", new FreePassUserInfo("test02", "테스터2", "test1234$", MemberRoleType.MEMBER));
	}

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
		
		
		if (null == paramIVBase64) {
			String errorMessage = "the request parameter paramIVBase64 is null";
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
		
		
		if (null == paramPwdCipherBase64) {
			String errorMessage = "the request parameter paramPwdCipherBase64 is null";
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
			sessionkeyBytes = CommonStaticUtil.Base64Decoder.decode(paramSessionKeyBase64);
		} catch(Exception e) {
			log.warn("base64 encoding error for the parameter paramSessionKeyBase64[{}], errormessage=[{}]", paramSessionKeyBase64, e.getMessage());
			
			String errorMessage = "세션키 파라미터가 잘못되었습니다";
			String debugMessage = String.format("check whether the parameter paramSessionKeyBase64[%s] is a base64 encoding string, errormessage=[%s]", paramSessionKeyBase64, e.getMessage());
			printErrorMessagePage(req, res, errorMessage, debugMessage);
			return;
		}
		byte[] ivBytes = null;
		try {
			ivBytes = CommonStaticUtil.Base64Decoder.decode(paramIVBase64);
		} catch(Exception e) {
			log.warn("base64 encoding error for the parameter paramIVBase64[{}], errormessage=[{}]", paramIVBase64, e.getMessage());
			
			String errorMessage = "세션키 소금 파라미터가 잘못되었습니다";
			String debugMessage = String.format("check whether the parameter paramIVBase64[%s] is a base64 encoding string, errormessage=[%s]", paramIVBase64, e.getMessage());
			
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
		
		byte[] userIDBytes = webServerSymmetricKey.decrypt(CommonStaticUtil.Base64Decoder.decode(paramUserIDCipherBase64));
		byte[] passwordBytes = webServerSymmetricKey.decrypt(CommonStaticUtil.Base64Decoder.decode(paramPwdCipherBase64));

		String userID = new String(userIDBytes, CommonStaticFinalVars.CIPHER_CHARSET);
		String password = new String(passwordBytes, CommonStaticFinalVars.CIPHER_CHARSET);
		
		FreePassUserInfo freePassUserInfo = freePassUserInfoHash.get(userID);
		
		if (null == freePassUserInfo) {
			String errorMessage = "로그인 실패했습니다. 상세한 내용은 에러 로그를 참고하세요.";
			
			String debugMessage = new StringBuilder("사용자 아이디[")
			.append(userID)
			.append("]가 존재하지 않습니다").toString();
			
			log.warn(debugMessage);
			
			printErrorMessagePage(req, res, 
					errorMessage, 
					debugMessage);
			return;
		}
		
		if (! freePassUserInfo.getPassword().equals(password)) {
			String errorMessage = "로그인 실패했습니다. 상세한 내용은 에러 로그를 참고하세요.";
			
			String debugMessage = new StringBuilder("사용자 아이디[")
			.append(userID)
			.append("]의 비밀번호가 틀렸습니다").toString();
			
			log.warn(debugMessage);
			
			printErrorMessagePage(req, res, 
					errorMessage, 
					debugMessage);
			return;
		}
		
		HttpSession httpSession = req.getSession();
		
		httpSession.setAttribute(WebCommonStaticFinalVars.HTTPSESSION_KEY_NAME_OF_LOGINED_USER_INFORMATION,
				new AccessedUserInformation(true, freePassUserInfo.getUserID(), freePassUserInfo.getUserName(),
						freePassUserInfo.getMemberType()));
				
		req.setAttribute(WebCommonStaticFinalVars.REQUEST_KEY_NAME_OF_SYMMETRIC_KEY_FROM_SESSIONKEY, 
				webServerSymmetricKey);
		req.setAttribute(WebCommonStaticFinalVars.REQUEST_KEY_NAME_OF_MODULUS_HEX_STRING,
				webServerSessionkey.getModulusHexStrForWeb());

		printJspPage(req, res, "/jsp/member/UserLoginOKCallBack.jsp");
		return;		
	}

}
