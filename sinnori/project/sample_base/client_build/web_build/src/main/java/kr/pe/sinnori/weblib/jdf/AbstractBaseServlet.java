package kr.pe.sinnori.weblib.jdf;

import java.io.File;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import kr.pe.sinnori.weblib.common.WebCommonStaticFinalVars;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@SuppressWarnings("serial")
public abstract class AbstractBaseServlet extends HttpServlet {
	protected Logger log = LoggerFactory
			.getLogger(AbstractBaseServlet.class);
	

	/**
	 * 로그인 여부를 반환한다.
	 * @param req HttpServletRequest 객체
	 * @return 로그인 여부
	 */		
	public boolean isLogin(HttpServletRequest req) {
		HttpSession httpSession = req.getSession();
		String userId = (String) httpSession
				.getAttribute(WebCommonStaticFinalVars.HTTPSESSION_KEY_USERID_NAME);
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
				.getAttribute(WebCommonStaticFinalVars.HTTPSESSION_KEY_USERID_NAME);
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
		String userId = (String) httpSession.getAttribute(WebCommonStaticFinalVars.HTTPSESSION_KEY_USERID_NAME);
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
