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
	String topmenu = request.getParameter("topmenu");
	String targeturl = request.getParameter("targeturl");
	String leftmenu = (String)request.getAttribute("leftmenu");
	if (null == leftmenu) {
		leftmenu = targeturl;
	}
	
    final String arryLeftTopMenuPage[][] =      {				
                { "로그인", 
"/servlet/Login",
"/servlet/Login"},

                { "회원 가입", 
"/servlet/Member",
"/servlet/Member"},

    };

	
	int leftMenuNo = -1;
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
