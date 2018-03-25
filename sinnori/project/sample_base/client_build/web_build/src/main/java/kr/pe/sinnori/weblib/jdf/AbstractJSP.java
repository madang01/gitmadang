package kr.pe.sinnori.weblib.jdf;

import java.io.IOException;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.jsp.HttpJspPage;

import org.apache.commons.codec.binary.Base64;

import kr.pe.sinnori.common.etc.CommonStaticFinalVars;
import kr.pe.sinnori.common.exception.SymmetricException;
import kr.pe.sinnori.common.sessionkey.ServerSymmetricKeyIF;
import kr.pe.sinnori.weblib.common.WebCommonStaticFinalVars;


@SuppressWarnings("serial")
public abstract class AbstractJSP extends AbstractBaseServlet implements HttpJspPage {
	private ServletConfig config;

	// Initialise global variables
	@Override
	final public void init(ServletConfig config) throws ServletException {

		this.config = config;
		jspInit();
	}

	// provide accessor to the ServletConfig object
	@Override
	final public ServletConfig getServletConfig() {

		return config;

	}

	// Provide simple service method that calls the generated _jspService method
	@Override
	final public void service(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {

		_jspService(request, response);
	}

	// Create an abstract method that will be implemented by the JSP processor
	// in the subclass
	abstract public void _jspService(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException;

	// Provide a destroy method
	@Override
	final public void destroy() {
		jspDestroy();
	}

	// provide some utility methods
	public String getUser(HttpServletRequest request) {
		// get the user name from the request
		return (String) request.getParameter("user");
	}

	public String getCompany(HttpServletRequest request) {
		// get the user name from the request
		return (String) request.getParameter("company");
	}

	@Override
	public String getServletInfo() {

		return new String("PureJSPBase");
	}

	@Override
	public void jspDestroy() {		
		
	}

	@Override
	public void jspInit() {		
	}
	
	
	public String getCipheredBase64String(HttpServletRequest req, String painText) throws IllegalArgumentException, SymmetricException {
		ServerSymmetricKeyIF webServerSymmetricKey = (ServerSymmetricKeyIF)req.getAttribute(WebCommonStaticFinalVars.WEB_SERVER_SYMMETRIC_KEY);
		if (null == webServerSymmetricKey) {
			/*String errorMessage = new StringBuilder("the jsp request's attribute[")
					.append(WebCommonStaticFinalVars.WEB_SERVER_SYMMETRIC_KEY)
					.append("] doesn't exist").toString();*/
			log.warn("the jsp request's attribute[{}] doesn't exist", WebCommonStaticFinalVars.WEB_SERVER_SYMMETRIC_KEY);
			return "";
		}
		return Base64.encodeBase64String(webServerSymmetricKey.encrypt(painText.getBytes(CommonStaticFinalVars.SINNORI_CIPHER_CHARSET)));
	}
	
	private void doBuildLeftMenuPartString(StringBuilder leftMenuPartStringBuilder, String leftmenu,
			final String[][] leftMenuInfoList, final Object[][] leftMenuLinkInfoList) {
		int leftMenuNo = -1;
		for (int i=0; i < leftMenuLinkInfoList.length; i++) {
			if (leftMenuLinkInfoList[i][0].equals(leftmenu)) {
				leftMenuNo = (Integer)leftMenuLinkInfoList[i][1];
				break;
			}
		}
		
		leftMenuPartStringBuilder.append("<div id=\"sidemenu\"><div id=\"smtop\">&nbsp;</div>");
		leftMenuPartStringBuilder.append(CommonStaticFinalVars.NEWLINE);
		leftMenuPartStringBuilder.append("<div id=\"smtitle\"><h1>");
		if (-1 == leftMenuNo) {
			leftMenuPartStringBuilder.append("외부 링크");
		} else {
			leftMenuPartStringBuilder.append(leftMenuInfoList[leftMenuNo][0]);
		}
		leftMenuPartStringBuilder.append(CommonStaticFinalVars.NEWLINE);
		
		leftMenuPartStringBuilder.append("</h1></div> <!-- side menu current page title -->");
		leftMenuPartStringBuilder.append(CommonStaticFinalVars.NEWLINE);
		
		leftMenuPartStringBuilder.append("<ul class=\"normal\">");
		leftMenuPartStringBuilder.append(CommonStaticFinalVars.NEWLINE);
		
		for (int i=0; i < leftMenuInfoList.length; i++) {
			leftMenuPartStringBuilder.append("<li><a href=\"#\" onClick=\"goURL('");
			leftMenuPartStringBuilder.append(leftMenuInfoList[i][1]);
			leftMenuPartStringBuilder.append("');\"");
			if (i == leftMenuNo) {
				leftMenuPartStringBuilder.append(" class=\"currentpage\"");
			}
			leftMenuPartStringBuilder.append(">");
			leftMenuPartStringBuilder.append(leftMenuInfoList[i][0]);
			leftMenuPartStringBuilder.append("</a></li>");			
			leftMenuPartStringBuilder.append(CommonStaticFinalVars.NEWLINE);
		}
		
		leftMenuPartStringBuilder.append("</ul>");
		leftMenuPartStringBuilder.append(CommonStaticFinalVars.NEWLINE);
		leftMenuPartStringBuilder.append("<div id=\"smbottom\">&nbsp;</div></div>");
	}	
	
	protected enum SITE_TOPMENU_TYPE {
		INTRODUCE(0), GETTING_STARTED(1), DOWNLOAD(2), COMMUNITY(3), TECH_DOCUMENT(4), MEMBER(5), TEST_EXAMPLE(6);
		
		private int topMenuIndex;
		private SITE_TOPMENU_TYPE(int topMenuIndex) {
			this.topMenuIndex = topMenuIndex;
		}
		
		public int getTopMenuIndex() {
			return topMenuIndex;
		}
		
		public static SITE_TOPMENU_TYPE matchIndex(int topMenuIndex) {			
			SITE_TOPMENU_TYPE[] siteTopMenuTypes = SITE_TOPMENU_TYPE.values();
			for (SITE_TOPMENU_TYPE siteTopMenuType : siteTopMenuTypes) {
				if (siteTopMenuType.getTopMenuIndex() == topMenuIndex) {
					return siteTopMenuType;
				}
			}
			
			return SITE_TOPMENU_TYPE.INTRODUCE;
		}
	}
	
	protected int getCurrentTopMenuIndex(HttpServletRequest req) {
		Object siteTopMenuRequstKeyValue = req.getAttribute(WebCommonStaticFinalVars.SITE_TOPMENU_REQUEST_KEY_NAME);
		
		SITE_TOPMENU_TYPE targetSiteTopMenuType = SITE_TOPMENU_TYPE.INTRODUCE;
		if (null != siteTopMenuRequstKeyValue) {
			targetSiteTopMenuType = (SITE_TOPMENU_TYPE)siteTopMenuRequstKeyValue;
		}
		
		return targetSiteTopMenuType.getTopMenuIndex();
	}
	
	protected String buildTopMenuPartString(HttpServletRequest req) {
		final String arryTopMenuPage[][] =	{ 
				{ "소개", "/menu/about.jsp"},
				{ "시작하기", "/menu/stepbystep/main.jsp"},
				{ "다운로드", "/menu/download/main.jsp"},
				{ "사랑방", "/menu/board/body.jsp"},
				{ "문서", "/menu/techdoc/body.jsp"},
				{ "회원", "/menu/member/body.jsp"}, 
				{ "실험과 검증", "/menu/testcode/body.jsp"},
			};
		
		Object siteTopMenuRequstKeyValue = req.getAttribute(WebCommonStaticFinalVars.SITE_TOPMENU_REQUEST_KEY_NAME);
		
		SITE_TOPMENU_TYPE targetSiteTopMenuType = SITE_TOPMENU_TYPE.INTRODUCE;
		if (null != siteTopMenuRequstKeyValue) {
			targetSiteTopMenuType = (SITE_TOPMENU_TYPE)siteTopMenuRequstKeyValue;
		}
		
		StringBuilder topMenuPartStringBuilder = new StringBuilder();
		for (int i=0; i < arryTopMenuPage.length; i++) {
			topMenuPartStringBuilder.append("<li");
			if (i == targetSiteTopMenuType.getTopMenuIndex()) {
				topMenuPartStringBuilder.append(" class=\"active\"");
			}
			topMenuPartStringBuilder.append("><a href=\"");
			topMenuPartStringBuilder.append(arryTopMenuPage[i][1]);			
			topMenuPartStringBuilder.append("\">");
			topMenuPartStringBuilder.append(arryTopMenuPage[i][0]);
			topMenuPartStringBuilder.append("</a></li>");
		}
		
		return topMenuPartStringBuilder.toString();
	}
	
	protected String buildLeftMenuPartString(HttpServletRequest req) {
		StringBuilder leftMenuPartStringBuilder = new StringBuilder();
		
		SITE_TOPMENU_TYPE targetSiteTopMenuType = (SITE_TOPMENU_TYPE)req.getAttribute(WebCommonStaticFinalVars.SITE_TOPMENU_REQUEST_KEY_NAME);
		if (null == targetSiteTopMenuType) {
			targetSiteTopMenuType = SITE_TOPMENU_TYPE.INTRODUCE;
		}
		
		
		Object siteLeftMenuRequestKeyValue = req.getAttribute(WebCommonStaticFinalVars.SITE_LEFTMENU_REQUEST_KEY_NAME);
		
		String leftmenu = null;
		if (null != siteLeftMenuRequestKeyValue) {
			leftmenu = (String) siteLeftMenuRequestKeyValue;
		}		
		
		if (targetSiteTopMenuType.equals(SITE_TOPMENU_TYPE.COMMUNITY)) {
			/** 0:좌측 메뉴명, 1:주 좌측 메뉴 링크 */
			final String[][] leftMenuInfoList = {
				{"자유 게시판", "/servlet/BoardList"}
			};

			/** 0:좌측 메뉴키, 1:좌측 메뉴 번호 */
			final Object[][] leftMenuLinkInfoList = {
				{"/servlet/BoardList",  0},
				{"/servlet/BoardWrite",  0},
				{"/servlet/BoardDetail",  0},
				{"/servlet/BoardModify",  0},
				{"/servlet/BoardReply",  0},
				{"/servlet/BoardVote",  0},		
			};
			
			doBuildLeftMenuPartString(leftMenuPartStringBuilder, leftmenu, leftMenuInfoList, leftMenuLinkInfoList);
		} else if (targetSiteTopMenuType.equals(SITE_TOPMENU_TYPE.TECH_DOCUMENT)) {
			int topmenu = SITE_TOPMENU_TYPE.TECH_DOCUMENT.getTopMenuIndex();
			/** 0:좌측 메뉴명, 1:주 좌측 메뉴 링크 */
			final String[][] leftMenuInfoList = {
				{"신놀이 개발 환경 구축 문서", "/PageJump.jsp?topmenu="+topmenu+"&bodyurl=/menu/techdoc/sinnori_environment_build.html"},
				{"신놀이 Ant Build 기술 문서", "/PageJump.jsp?topmenu="+topmenu+"&bodyurl=/menu/techdoc/sinnori_ant_buil_techdoc.html"},
				{"신놀이 서버 기술 문서", "/PageJump.jsp?topmenu="+topmenu+"&bodyurl=/menu/techdoc/sinnori_server_techdoc.html"},
				{"자바 클라이언트 API 기술 문서", "/PageJump.jsp?topmenu="+topmenu+"&bodyurl=/menu/techdoc/sinnori_client_api_techdoc.html"},
				{"동기 파일 송수신 기술 문서", "/PageJump.jsp?topmenu="+topmenu+"&bodyurl=/menu/techdoc/sinnori_fileupdown_v1_techdoc.html"},
				{"비동기 파일송수신 기술 문서", "/PageJump.jsp?topmenu="+topmenu+"&bodyurl=/menu/techdoc/sinnori_fileupdown_v2_techdoc.html"},
				{"파일 송수신 클라이언트 기능 명세", "/PageJump.jsp?topmenu="+topmenu+"&bodyurl=http://www.3rabbitz.com/c0b9eb893bd99490"},
			};

			/** 0:좌측 메뉴키, 1:좌측 메뉴 번호 */
			final Object[][] leftMenuLinkInfoList = {
			  {"/menu/techdoc/sinnori_environment_build.html",  0},
				{"/menu/techdoc/sinnori_ant_buil_techdoc.html",  1},
				{"/menu/techdoc/sinnori_server_techdoc.html",  2},
				{"/menu/techdoc/sinnori_client_api_techdoc.html",  3},
				{"/menu/techdoc/sinnori_fileupdown_v1_techdoc.html",  4},
				{"/menu/techdoc/sinnori_fileupdown_v2_techdoc.html",  5},
				{"http://www.3rabbitz.com/c0b9eb893bd99490",  6},
			};
			
			doBuildLeftMenuPartString(leftMenuPartStringBuilder, leftmenu, leftMenuInfoList, leftMenuLinkInfoList);
		} else if (targetSiteTopMenuType.equals(SITE_TOPMENU_TYPE.MEMBER)) {
			/** 0:좌측 메뉴명, 1:주 좌측 메뉴 링크 */
			final String[][] leftMenuInfoList = {
				{"로그인", "/servlet/Login"},
				{"회원 가입", "/servlet/Member"}
			};

			/** 0:좌측 메뉴키, 1:좌측 메뉴 번호 */
			final Object[][] leftMenuLinkInfoList = {
				{"/servlet/Login",  0},
				{"/servlet/Member",  1}		
			};
			
			doBuildLeftMenuPartString(leftMenuPartStringBuilder, leftmenu, leftMenuInfoList, leftMenuLinkInfoList);
		} else if (targetSiteTopMenuType.equals(SITE_TOPMENU_TYPE.TEST_EXAMPLE)) {
			int topmenu = SITE_TOPMENU_TYPE.TEST_EXAMPLE.getTopMenuIndex();
			
			/** 0:좌측 메뉴명, 1:주 좌측 메뉴 링크 */
			final String[][] leftMenuInfoList = {
				{"JDF-비 로그인 테스트", "/servlet/JDFNotLoginTest"},
				{"JDF-로그인 테스트", "/servlet/JDFLoginTest?topmenu="+topmenu},
				{"JDF-세션키 테스트", "/servlet/SessionKeyTest"},
				{"RSA 암/복호화 테스트", "/servlet/JSBNTest"},
				{"메세지 다이제스트(MD) 테스트", "/servlet/CryptoJSMDTest"},
				{"대칭키 테스트", "/servlet/CryptoJSSKTest"},
				{"에코 테스트", "/servlet/EchoTest"},
				{"모든 데이터 타입 검증", "/servlet/AllItemTypeTest"},
				{"동적 클래스 호출 실패", "#"},
			};

			/** 0:좌측 메뉴키, 1:좌측 메뉴 번호 */
			final Object[][] leftMenuLinkInfoList = {
				{"/servlet/JDFNotLoginTest",  0},
				{"/servlet/JDFLoginTest",  1},
				{"/servlet/SessionKeyTest",  2},
				{"/servlet/JSBNTest",  3},
				{"/servlet/CryptoJSMDTest",  4},
				{"/servlet/CryptoJSSKTest",  5},
				{"/servlet/EchoTest",  6},
				{"/servlet/AllItemTypeTest",  7},
			};
			
			doBuildLeftMenuPartString(leftMenuPartStringBuilder, leftmenu, leftMenuInfoList, leftMenuLinkInfoList);
		}
		
		return leftMenuPartStringBuilder.toString();
	}

	
}
