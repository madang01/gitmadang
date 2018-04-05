package kr.pe.sinnori.weblib.sitemenu;

import kr.pe.sinnori.weblib.common.WebCommonStaticFinalVars;

public class TechDocumentLeftMenuInfo extends AbstractLeftMenuInfoOfTopMenu {	
	private String buildPageWrapperURL(int topMenuIndex, String bodyURL) {
			
		String pageWrapperURLString = new StringBuilder("/PageWrapper.jsp?")
				.append(WebCommonStaticFinalVars.REQUEST_KEY_NAME_OF_SITE_TOPMENU)
				.append("=")
				.append(topMenuIndex)
				.append("&")
				.append(WebCommonStaticFinalVars.PARAMETER_KEY_NAME_OF_BODYURL_FOR_PAGEWRAPPER)
				.append("=")
				.append(bodyURL)
				.toString();
		return pageWrapperURLString;
	}
	
	
	protected void build() {
		SiteTopMenuType targetSiteTopMenuType = SiteTopMenuType.TECH_DOCUMENT;
		int topMenuIndex = targetSiteTopMenuType.getTopMenuIndex();		
		
		SiteLeftMenuInfo siteLeftMenuInfo = null;
		String bodyURL = null;
		
		bodyURL = "/menu/techdoc/sinnori_environment_build.html";
		siteLeftMenuInfo = addSiteLeftMenuInfo("신놀이 개발 환경 구축 문서", 
				buildPageWrapperURL(topMenuIndex, bodyURL));
		addSiteLeftMenuGroup(siteLeftMenuInfo, bodyURL);
		
		bodyURL = "/menu/techdoc/sinnori_ant_buil_techdoc.html";
		siteLeftMenuInfo = addSiteLeftMenuInfo("신놀이 Ant Build 기술 문서", 
				buildPageWrapperURL(topMenuIndex, bodyURL));		
		addSiteLeftMenuGroup(siteLeftMenuInfo, bodyURL);		
		
		bodyURL = "/menu/techdoc/sinnori_server_techdoc.html";
		siteLeftMenuInfo = addSiteLeftMenuInfo("서버 기술 문서", 
				buildPageWrapperURL(topMenuIndex, bodyURL));
		addSiteLeftMenuGroup(siteLeftMenuInfo, bodyURL);
		
		bodyURL = "/menu/techdoc/sinnori_client_api_techdoc.html";
		siteLeftMenuInfo = addSiteLeftMenuInfo("서버 접속 API 기술 문서", 
				buildPageWrapperURL(topMenuIndex, bodyURL));
		addSiteLeftMenuGroup(siteLeftMenuInfo, bodyURL);
		
		bodyURL = "/menu/techdoc/sinnori_fileupdown_v1_techdoc.html";
		siteLeftMenuInfo = addSiteLeftMenuInfo("동기 파일 송수신 기술 문서", 
				buildPageWrapperURL(topMenuIndex, bodyURL));
		addSiteLeftMenuGroup(siteLeftMenuInfo, bodyURL);
		
		bodyURL = "/menu/techdoc/sinnori_fileupdown_v2_techdoc.html";
		siteLeftMenuInfo = addSiteLeftMenuInfo("비동기 파일 송수신 기술 문서", 
				buildPageWrapperURL(topMenuIndex, bodyURL));
		addSiteLeftMenuGroup(siteLeftMenuInfo, bodyURL);
		
		
		bodyURL = "http://www.3rabbitz.com/c0b9eb893bd99490";
		siteLeftMenuInfo = addSiteLeftMenuInfo("파일 송수신 클라이언트 기능 명세", 
				buildPageWrapperURL(topMenuIndex, bodyURL));
		addSiteLeftMenuGroup(siteLeftMenuInfo, "http://www.3rabbitz.com/c0b9eb893bd99490");
	}
}
