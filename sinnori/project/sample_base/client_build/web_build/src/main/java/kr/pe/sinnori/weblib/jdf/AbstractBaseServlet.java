package kr.pe.sinnori.weblib.jdf;

import java.io.File;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import kr.pe.sinnori.weblib.common.WebCommonStaticFinalVars;
import kr.pe.sinnori.weblib.sitemenu.SiteTopMenuType;

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
				.getAttribute(WebCommonStaticFinalVars.HTTPSESSION_KEY_NAME_OF_LOGIN_USERID);
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
				.getAttribute(WebCommonStaticFinalVars.HTTPSESSION_KEY_NAME_OF_LOGIN_USERID);
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
	public String getLoginUserIDFromHttpSession(HttpServletRequest req) {
		HttpSession httpSession = req.getSession();
		
		Object loginUserIDValue = httpSession.getAttribute(WebCommonStaticFinalVars.HTTPSESSION_KEY_NAME_OF_LOGIN_USERID);
		if (null == loginUserIDValue) {
			return "guest";
		}
		
		String loginUserID = (String) loginUserIDValue;
		if (loginUserID.equals("")) {
			loginUserID = "guest";
		}
		
		return loginUserID;
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
	
	/*public SiteTopMenuType setSiteTopMenuRequestAtrributeMatchingTopMenuParameter(HttpServletRequest req, SiteTopMenuType defaultSiteTopMenuType) {
		if (null == req) {
			throw new IllegalArgumentException("the parameter req is null");
		}
		if (null == defaultSiteTopMenuType) {
			throw new IllegalArgumentException("the parameter defaultSiteTopMenuType is null");
		}
		String parmTopmenu = req.getParameter("topmenu");
		if (null == parmTopmenu) {
			parmTopmenu = String.valueOf(defaultSiteTopMenuType.getTopMenuIndex());
		}
		parmTopmenu = parmTopmenu.trim();	
		if (parmTopmenu.equals("")) parmTopmenu=String.valueOf(defaultSiteTopMenuType.getTopMenuIndex());
		
		int nTopMenu = 0;
		
		try {
			nTopMenu = Integer.parseInt(parmTopmenu);
		} catch (NumberFormatException num_e) {
			// num_e.prin
		}
		
		SiteTopMenuType targetSiteTopMenuType = SiteTopMenuType.match(nTopMenu);
		
		req.setAttribute(WebCommonStaticFinalVars.REQUEST_KEY_NAME_OF_SITE_TOPMENU, targetSiteTopMenuType);
		return targetSiteTopMenuType;
	}*/
	
	
	public SiteTopMenuType getSiteTopMenuTypeFromParameter(HttpServletRequest req, SiteTopMenuType defaultSiteTopMenuType) {
		if (null == req) {
			throw new IllegalArgumentException("the parameter req is null");
		}
		if (null == defaultSiteTopMenuType) {
			throw new IllegalArgumentException("the parameter defaultSiteTopMenuType is null");
		}
		String parmTopmenu = req.getParameter(WebCommonStaticFinalVars.REQUEST_KEY_NAME_OF_SITE_TOPMENU);
		if (null == parmTopmenu) {
			parmTopmenu = String.valueOf(defaultSiteTopMenuType.getTopMenuIndex());
		}
		parmTopmenu = parmTopmenu.trim();	
		if (parmTopmenu.equals("")) parmTopmenu=String.valueOf(defaultSiteTopMenuType.getTopMenuIndex());
		
		
		SiteTopMenuType targetSiteTopMenuType = defaultSiteTopMenuType;
		
		try {
			int siteTopMenuTypeValue = Integer.parseInt(parmTopmenu);			
			targetSiteTopMenuType = SiteTopMenuType.match(siteTopMenuTypeValue);
		} catch (NumberFormatException num_e) {
		}
		
		
		return targetSiteTopMenuType;
	}

	protected void setSiteTopMenu(HttpServletRequest req, SiteTopMenuType targetSiteTopMenuType) {
		if (null == req) {
			throw new IllegalArgumentException("the parameter req is null");
		}
		if (null == targetSiteTopMenuType) {
			throw new IllegalArgumentException("the parameter targetSiteTopMenuType is null");
		}
		req.setAttribute(WebCommonStaticFinalVars.REQUEST_KEY_NAME_OF_SITE_TOPMENU, targetSiteTopMenuType);
	}
	
	protected void setSiteLeftMenu(HttpServletRequest req, String leftmenuURL) {
		if (null == req) {
			throw new IllegalArgumentException("the parameter req is null");
		}
		if (null == leftmenuURL) {
			throw new IllegalArgumentException("the parameter leftmenuURL is null");
		}
		req.setAttribute(WebCommonStaticFinalVars.REQUEST_KEY_NAME_OF_SITE_LEFTMENU, leftmenuURL);
	}
}
