package kr.pe.sinnori.weblib.sitemenu;

public class TechDocumentLeftMenuInfo extends AbstractLeftMenuInfoOfTopMenu {	
	protected void build() {
		int topmenu = SiteTopMenuType.TECH_DOCUMENT.getTopMenuIndex();
		
		/** 0:좌측 메뉴명, 1:주 좌측 메뉴 링크 */
		/*final String[][] leftMenuInfoList = {
			{"신놀이 개발 환경 구축 문서", "/PageJump.jsp?topmenu="+topmenu+"&bodyurl=/menu/techdoc/sinnori_environment_build.html"},
			{"신놀이 Ant Build 기술 문서", "/PageJump.jsp?topmenu="+topmenu+"&bodyurl=/menu/techdoc/sinnori_ant_buil_techdoc.html"},
			{"신놀이 서버 기술 문서", "/PageJump.jsp?topmenu="+topmenu+"&bodyurl=/menu/techdoc/sinnori_server_techdoc.html"},
			{"자바 클라이언트 API 기술 문서", "/PageJump.jsp?topmenu="+topmenu+"&bodyurl=/menu/techdoc/sinnori_client_api_techdoc.html"},
			{"동기 파일 송수신 기술 문서", "/PageJump.jsp?topmenu="+topmenu+"&bodyurl=/menu/techdoc/sinnori_fileupdown_v1_techdoc.html"},
			{"비동기 파일송수신 기술 문서", "/PageJump.jsp?topmenu="+topmenu+"&bodyurl=/menu/techdoc/sinnori_fileupdown_v2_techdoc.html"},
			{"파일 송수신 클라이언트 기능 명세", "/PageJump.jsp?topmenu="+topmenu+"&bodyurl=http://www.3rabbitz.com/c0b9eb893bd99490"},
		};*/

		/** 0:좌측 메뉴키, 1:좌측 메뉴 번호 */
		/*final Object[][] leftMenuLinkInfoList = {
		  {"/menu/techdoc/sinnori_environment_build.html",  0},
			{"/menu/techdoc/sinnori_ant_buil_techdoc.html",  1},
			{"/menu/techdoc/sinnori_server_techdoc.html",  2},
			{"/menu/techdoc/sinnori_client_api_techdoc.html",  3},
			{"/menu/techdoc/sinnori_fileupdown_v1_techdoc.html",  4},
			{"/menu/techdoc/sinnori_fileupdown_v2_techdoc.html",  5},
			{"http://www.3rabbitz.com/c0b9eb893bd99490",  6},
		};*/
		
		SiteLeftMenuInfo siteLeftMenuInfo = null;
		
		siteLeftMenuInfo = addSiteLeftMenuInfo("신놀이 개발 환경 구축 문서", 
				"/PageJump.jsp?topmenu="+topmenu+"&bodyurl=/menu/techdoc/sinnori_environment_build.html");
		addSiteLeftMenuGroup(siteLeftMenuInfo, "/menu/techdoc/sinnori_environment_build.html");
		
		siteLeftMenuInfo = addSiteLeftMenuInfo("신놀이 Ant Build 기술 문서", 
				"/PageJump.jsp?topmenu="+topmenu+"&bodyurl=/menu/techdoc/sinnori_ant_buil_techdoc.html");
		addSiteLeftMenuGroup(siteLeftMenuInfo, "/menu/techdoc/sinnori_ant_buil_techdoc.html");
		
		siteLeftMenuInfo = addSiteLeftMenuInfo("서버 기술 문서", 
				"/PageJump.jsp?topmenu="+topmenu+"&bodyurl=/menu/techdoc/sinnori_server_techdoc.html");
		addSiteLeftMenuGroup(siteLeftMenuInfo, "/menu/techdoc/sinnori_server_techdoc.html");
		
		siteLeftMenuInfo = addSiteLeftMenuInfo("서버 접속 API 기술 문서", 
				"/PageJump.jsp?topmenu="+topmenu+"&bodyurl=/menu/techdoc/sinnori_client_api_techdoc.html");
		addSiteLeftMenuGroup(siteLeftMenuInfo, "/menu/techdoc/sinnori_client_api_techdoc.html");
		
		siteLeftMenuInfo = addSiteLeftMenuInfo("동기 파일 송수신 기술 문서", 
				"/PageJump.jsp?topmenu="+topmenu+"&bodyurl=/menu/techdoc/sinnori_fileupdown_v1_techdoc.html");
		addSiteLeftMenuGroup(siteLeftMenuInfo, "/menu/techdoc/sinnori_fileupdown_v1_techdoc.html");
		
		siteLeftMenuInfo = addSiteLeftMenuInfo("비동기 파일 송수신 기술 문서", 
				"/PageJump.jsp?topmenu="+topmenu+"&bodyurl=/menu/techdoc/sinnori_fileupdown_v2_techdoc.html");
		addSiteLeftMenuGroup(siteLeftMenuInfo, "/menu/techdoc/sinnori_fileupdown_v2_techdoc.html");
		
		siteLeftMenuInfo = addSiteLeftMenuInfo("파일 송수신 클라이언트 기능 명세", 
				"/PageJump.jsp?topmenu="+topmenu+"&bodyurl=http://www.3rabbitz.com/c0b9eb893bd99490");
		addSiteLeftMenuGroup(siteLeftMenuInfo, "http://www.3rabbitz.com/c0b9eb893bd99490");
	}
}
