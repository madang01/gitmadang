<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%><%
	/* 문서 좌측 메뉴 */	

	/**
	 * PageJump.jsp 는 직접 호출과 간접 호출로 나뉜다.
	 * 직접 호출은 URL 직접 호출을 뜻하며, 간접 호출은 서블릿을 통해서 호출되는것을 말한다.
	 * 서블릿은 출력 담당 페이지를 지정하는데,
	 * 이때 페이지는 본문만 있기때문에 PageJump.jsp 를 통해 출력 해야 한다.
	 * 서블릿 호출시 공통 JDFBaseServlet::performBasePreTask 에서
	 * req.setAttribute("leftmenu", req.getRequestURI()); 로 강제로 설정되며,
	 * req.setAttribute("topmenu", req.getParameter("topmenu")); 설정된다.

	 * PageJump.jsp 에서는 leftmenu 에 대해서 어떠한 설정도 하지 않는다.
     * 따라서 leftmenu 가 null 이면 사용자가 PageJump.jsp 를 직접 호출한 경우로 
	 * targeturl 로 지정된 페이지가 본문 즉 좌측 메뉴가 된다.
	 */

	String targeturl = request.getParameter("targeturl");
	String leftmenu = (String)request.getAttribute("leftmenu");
	if (null == leftmenu) {
		leftmenu = targeturl;
	}

	int leftMenuNo = -1;
    final String arryLeftTopMenuPage[][] =      {
                { "신놀이 Ant Build 기술 문서", 
"/PageJump.jsp?topmenu=3&targeturl=/techdoc/sinnori_ant_buil_techdoc.html",
"/techdoc/sinnori_ant_buil_techdoc.html"},

                { "신놀이 서버 기술 문서", 
"/PageJump.jsp?topmenu=3&targeturl=/techdoc/sinnori_server_techdoc.html",
"/techdoc/sinnori_server_techdoc.html"
},

                { "자바 클라이언트 API 기술 문서", 
"/PageJump.jsp?topmenu=3&targeturl=/techdoc/sinnori_client_api_techdoc.html",
"/techdoc/sinnori_client_api_techdoc.html"},

{ "동기 파일 송수신 기술 문서", 
"/PageJump.jsp?topmenu=3&targeturl=/techdoc/sinnori_fileupdown_v1_techdoc.html",
"/techdoc/sinnori_fileupdown_v1_techdoc.html"},

{ "비동기 파일송수신 기술 문서", 
"/PageJump.jsp?topmenu=3&targeturl=/techdoc/sinnori_fileupdown_v2_techdoc.html",
"/techdoc/sinnori_fileupdown_v2_techdoc.html"},

{ "파일 송수신 클라이언트 기능 명세", 
"/PageJump.jsp?topmenu=3&targeturl=http://www.3rabbitz.com/c0b9eb893bd99490",
"http://www.3rabbitz.com/c0b9eb893bd99490"},

    };

	for (int i=0; i < arryLeftTopMenuPage.length; i++) {
		if (arryLeftTopMenuPage[i][2].equals(leftmenu)) {
			leftMenuNo = i;
			break;
		}
	}


%>
<div id="sidemenu"><div id="smtop">&nbsp;</div>
<div id="smtitle"><h1><%
	if (-1 == leftMenuNo) out.print("외부 링크");
	else out.print(arryLeftTopMenuPage[leftMenuNo][0]);
%></h1></div> <!-- side menu current page title -->
	<ul class="normal"><%
	for (int i=0; i < arryLeftTopMenuPage.length; i++) {
%>
		<li><a href="#" onClick="goURL('<%=arryLeftTopMenuPage[i][1]%>');"<%
	if (i == leftMenuNo) out.print(" class=\"currentpage\"");
%>><%=arryLeftTopMenuPage[i][0]%></a></li><%
	}
%>
	</ul>
<div id="smbottom">&nbsp;</div></div>
