package kr.pe.sinnori.common.weblib;

import java.io.File;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang3.StringEscapeUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@SuppressWarnings("serial")
public abstract class AbstractBaseServlet extends HttpServlet {
	protected Logger log = LoggerFactory
			.getLogger(AbstractBaseServlet.class);
	
	/**
	 * 문자열을 HTML4 기준 이스케이프 문자로 변환한다. 단 변환후 사용자가 지정하는 문자열 치환기들을 차례로 적용한다.
	 * @param str 변환을 원하는 원본 문자열
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
	 * 문자열을 Javascript 기준 이스케이프 문자로 변환한다.
	 * @param str 변환을 원하는 원본 문자열
	 * @param afterStringReplacerList 변환후 사용자가 지정하는 문자열 치환 가변 변수들을 담는 그릇
	 * @return Javascript 기준 이스케이프 문자열로 변환된후 지정한 문자열 치환들을 거친 문자열
	 */
	public String escapeScript(String str, AbstractStringReplacer ... afterStringReplacerList) {
		if (null == str)
			return "";
		String ret = StringEscapeUtils.escapeEcmaScript(str);
		
		
		for (AbstractStringReplacer afterStringReplacer : afterStringReplacerList) {
			ret = afterStringReplacer.replace(ret);
		}
		
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
	
	/**
	 * 파일명이 겹치지 않기 위해서 DB 를 이용한 시퀀스 값인 업로드 파일 이름 순번을 받아 업로드 파일의 시스템 절대 경로 파일명을 반환한다.
	 * @param uploadFileNameSeq 파일명이 겹치지 않기 위해서 DB 를 이용한 시퀀스 값인 업로드 파일 이름 순번
	 * @return 업로드 파일의 시스템 절대 경로 파일명
	 */
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
