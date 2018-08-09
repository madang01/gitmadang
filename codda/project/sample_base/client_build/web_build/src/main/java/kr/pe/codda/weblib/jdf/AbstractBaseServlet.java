package kr.pe.codda.weblib.jdf;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;
import kr.pe.codda.weblib.common.WebCommonStaticFinalVars;

@SuppressWarnings("serial")
public abstract class AbstractBaseServlet extends HttpServlet {
	protected InternalLogger log = InternalLoggerFactory.getInstance(AbstractBaseServlet.class);

	/**
	 * 어드민의 로그인 여부를 반환한다.
	 * @param req HttpServletRequest 객체
	 * @return 어드민 로그인 여부
	 */		
	public boolean isAdminLoginedIn(HttpServletRequest req) {
		HttpSession httpSession = req.getSession();
		String userId = (String) httpSession
				.getAttribute(WebCommonStaticFinalVars.HTTPSESSION_KEY_NAME_OF_LOGINED_ADMINID);
		if (null == userId || userId.equals("")) {
			return false;
		}
		return true;
	}
	
	/**
	 * 어드민의 로그인 여부를 반환한다.
	 * @param httpSession HttpSession 객체
	 * @return 어드민 로그인 여부
	 */		
	public boolean isAdminLoginedIn(HttpSession httpSession) {
		String userId = (String) httpSession
				.getAttribute(WebCommonStaticFinalVars.HTTPSESSION_KEY_NAME_OF_LOGINED_ADMINID);
		if (null == userId || userId.equals("")) {
			return false;
		}
		return true;
	}
	
	
	
	
	/**
	 * @param req HttpServletRequest 객체
	 * @return 어드민이 로그인했을 경우 '어드민 로그인 아이디'(={@link WebCommonStaticFinalVars#HTTPSESSION_KEY_NAME_OF_LOGINED_ADMINID} 로 저장된 HttpSession 의 값을 반환한다. 단 비 로그인시 손님 계정을 뜻하는 "guest" 를 반환한다.
	 */
	public String getLoginedAdminIDFromHttpSession(HttpServletRequest req) {
		HttpSession httpSession = req.getSession();
		
		Object loginUserIDValue = httpSession.getAttribute(WebCommonStaticFinalVars.HTTPSESSION_KEY_NAME_OF_LOGINED_ADMINID);
		if (null == loginUserIDValue) {
			return "guest";
		}
		
		String loginUserID = (String) loginUserIDValue;
		if (loginUserID.equals("")) {
			loginUserID = "guest";
		}
		
		return loginUserID;
	}
	
	public boolean isUserLoginedIn(HttpServletRequest req) {
		HttpSession httpSession = req.getSession();
		Object userIDFromHttpSession = httpSession
				.getAttribute(WebCommonStaticFinalVars.HTTPSESSION_KEY_NAME_OF_LOGINED_USERID);
		if (null == userIDFromHttpSession) {
			return false;
		}
		return true;
	}
	
	public boolean isUserLoginedIn(HttpSession httpSession) {
		Object userIDFromHttpSession = httpSession
				.getAttribute(WebCommonStaticFinalVars.HTTPSESSION_KEY_NAME_OF_LOGINED_USERID);
		if (null == userIDFromHttpSession) {
			return false;
		}
		return true;
	}
	
	/**
	 * @param req
	 * @return 일반 유저가 로그인 했을때 '일반인 유저 아이디'(={@link WebCommonStaticFinalVars#HTTPSESSION_KEY_NAME_OF_LOGINED_USERID} 로 저장된 HttpSession 의 값을 반환한다. 단 비 로그인시 손님 계정을 뜻하는 "guest" 를 반환한다.
	 */
	public String getLoginedUserIDFromHttpSession(HttpServletRequest req) {
		HttpSession httpSession = req.getSession();
		
		Object userIDFromHttpSession = httpSession.getAttribute(WebCommonStaticFinalVars.HTTPSESSION_KEY_NAME_OF_LOGINED_USERID);
		if (null == userIDFromHttpSession) {
			return "guest";
		}
		
		String userID = (String) userIDFromHttpSession;
		return userID;
	}
}
