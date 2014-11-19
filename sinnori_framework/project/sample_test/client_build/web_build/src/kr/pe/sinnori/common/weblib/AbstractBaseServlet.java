package kr.pe.sinnori.common.weblib;

import java.io.File;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import kr.pe.sinnori.common.lib.CommonRootIF;

import org.apache.commons.lang3.StringEscapeUtils;

@SuppressWarnings("serial")
public abstract class AbstractBaseServlet extends HttpServlet implements CommonRootIF {
	
	/**
	 * 문자열을 HTML4 기준 이스케이스 문자로 변환한다. 단 변환후 사용자가 지정하는 문자열 치환기들을 차례로 적용한다.
	 * @param str
	 * @param afterStringReplacerList 변환후 사용자가 지정하는 문자열 치환 가변 변수들을 담는 그릇
	 * @return HTML4 기준 이스케이스 문자열로 변환된후 지정한 문자열 치환들을 거친 문자열
	 */
	public String escapeHtml(String str, AbstractStringReplacer ... afterStringReplacerList) {
		if (null == str)
			return "";
		String ret = StringEscapeUtils.escapeHtml4(str);
		
		// if (null != stringReplacerList) {
			for (AbstractStringReplacer afterStringReplacer : afterStringReplacerList) {
				ret = afterStringReplacer.replace(ret);
			}
		// }
		
		return ret;
	}

	/**
	 * 로그인 여부를 반환한다.
	 * @param req HttpServletRequest 객체
	 * @return 로그인 여부
	 */		
	public boolean isLogin(HttpServletRequest req) {
		HttpSession httpSession = req.getSession();
		String userId = (String) httpSession
				.getAttribute(WebCommonStaticFinalVars.HTTPSESSION_USERID_NAME);
		if (null == userId || userId.equals("")) {
			return false;
		}
		return true;
	}
	
	/**
	 * 로그인 여부를 반환한다.
	 * @param httpSession HttpSession 객체
	 * @return 로그인 여부
	 */		
	public boolean isLogin(HttpSession httpSession) {
		String userId = (String) httpSession
				.getAttribute(WebCommonStaticFinalVars.HTTPSESSION_USERID_NAME);
		if (null == userId || userId.equals("")) {
			return false;
		}
		return true;
	}
	
	/**
	 * 로그인 아이디를 반환한다. 단 로그인을 안했을 경우 손님을 뜻하는 guest 아이디로 고정된다.
	 * @param req HttpServletRequest 객체
	 * @return 로그인 아이디
	 */
	public String getUserId(HttpServletRequest req) {
		HttpSession httpSession = req.getSession();
		String userId = (String) httpSession.getAttribute(WebCommonStaticFinalVars.HTTPSESSION_USERID_NAME);
		if (null == userId || userId.equals("")) {
			userId = "guest";
		}
		return userId;
	}
	
	public String getAttachSystemFullFileName(long uploadFileNameSeq) {
		StringBuilder attachSystemFullFileNameBuilder = new StringBuilder(WebCommonStaticFinalVars.WEBSITE_FILEUPLOAD_DIR.getAbsolutePath());
		attachSystemFullFileNameBuilder.append(File.separator);
		attachSystemFullFileNameBuilder.append(WebCommonStaticFinalVars.WEBSITE_FILEUPLOAD_PREFIX);
		attachSystemFullFileNameBuilder.append("_");
		attachSystemFullFileNameBuilder.append(uploadFileNameSeq);
		attachSystemFullFileNameBuilder.append(WebCommonStaticFinalVars.WEBSITE_FILEUPLOAD_SUFFIX);
		
		return attachSystemFullFileNameBuilder.toString();
	}
}
