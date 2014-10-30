<%@ page language="java" session="true" autoFlush="true" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %><%
%><%@ page import="kr.pe.sinnori.common.weblib.WebCommonStaticFinalVars" %><%

	final String arryTopMenuPage[][] =	{ 
		{ "소개", null, "/about.jsp"},
		{ "시작하기", null, "/stepbystep/main.jsp"},
		{ "다운로드", null, "/download/main.jsp"},
		{ "사랑방", "/board/leftmenu.jsp", "/board/body.jsp"},
		{ "문서", "/techdoc/leftmenu.jsp", "/techdoc/body.jsp"},
		{ "회원", "/member/leftmenu.jsp", "/member/body.jsp"}, 
		{ "실험과 검증", "/testcode/leftmenu.jsp" , "/testcode/body.jsp"}
	};


	String topMenu = request.getParameter("topmenu");
	if (null == topMenu) {
		topMenu = (String)request.getAttribute("topmenu");
		if (null == topMenu) {
			topMenu = "";
		}
	}
	topMenu = topMenu.trim();	
	if (topMenu.equals("")) topMenu="0";
	
	int nTopMenu = 0;
	
	try {
		nTopMenu = Integer.parseInt(topMenu);
	} catch (NumberFormatException num_e) {
		// num_e.prin
	}
	if (nTopMenu < 0 || nTopMenu >= arryTopMenuPage.length) nTopMenu=0;
	
	String targeturl = request.getParameter("targeturl");
	if (null == targeturl) {
		targeturl = (String)request.getAttribute("targeturl");
		if (null == targeturl) {		
			targeturl = arryTopMenuPage[nTopMenu][2];
		}
	}

	String userID = (String)session.getAttribute(WebCommonStaticFinalVars.HTTPSESSION_USERID_NAME);
	
%><!DOCTYPE html>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html;charset=utf-8" />
<title><%=WebCommonStaticFinalVars.WEBSITE_TITLE%></title>
<meta name="Author" content="SinnoriTeam - website / Design by Ian Smith - N-vent Design Services LLC - www.n-vent.com" />
<meta name="distribution" content="global" />
<meta name="rating" content="general" />
<meta name="Keywords" content="" />
<meta name="ICBM" content=""/> <!-- see geourl.org -->
<meta name="DC.title" content="Your Company"/>
<link rel="shortcut icon" href="favicon.ico"/> <!-- see favicon.com -->
<link rel="stylesheet" type="text/css" href="/css/style.css" />
<script type="text/javascript">
    function goURL(targeturl) {
	var inx = targeturl.indexOf("/servlet/");	
	if (0 == inx) {
		var f = document.directgofrm;
		f.action = targeturl;
		f.submit();		
	} else {
		top.document.location.href = targeturl;
	}
		
    }
</script>
</head>
<body>
<form name="directgofrm" method="post">
<input type="hidden" name="topmenu" value="<%=nTopMenu%>"/>
</form>
<!-- The ultra77 template is designed and released by Ian Smith - N-vent Design Services LLC - www.n-vent.com. Feel free to use this, but please don't sell it and kindly leave the credits intact. Muchas Gracias! -->
<div id="wrapper">
<a name="top"></a>
<!-- header -->
<div id="header">
	<div id="pagedescription"><h1>Sinnori Framework::공사중</h1><br /><h2> Sinnori Framework is an open software<br/> that help to create a server/client application.</h2><%
	if (null == userID) {
%><a href="/servlet/Login?topmenu=<%=topMenu%>">login</a><%		
	} else {
%><a href="/PageJump.jsp?topmenu=4&targeturl=/member/logout.jsp">logout</a><%
	}
%>
	
	</div>
	<div id="branding"><p><span class="templogo"><!-- your logo here -->Sinnori Framework</span><br />of the developer, by the developer, for the developer</p></div>
</div>

<!-- top menu -->
<div id="menu">
	<ul><%
	for (int i=0; i < arryTopMenuPage.length; i++) {
%>
		<li<% if (i == nTopMenu) out.print(" class=\"active\""); %>><a href="/PageJump.jsp?topmenu=<%=i%>"><%=arryTopMenuPage[i][0]%></a></li><%
	}
%>
	</ul>
</div> <!-- end top menu -->
<!-- bodywrap -->
<div id="bodytop">&nbsp;</div>
<div id="bodywrap">
	<div id="contentbody"><%
		String arryOfOutSite[] = { "http://www.3rabbitz.com" };	
		boolean isSinnoriSite = true;

		for (int i=0; i < arryOfOutSite.length; i++) {
			int startInx = targeturl.indexOf(arryOfOutSite[i]);
			if (startInx ==0 ) {
				isSinnoriSite = false;
				break;
			}			
		}	
		
		if (isSinnoriSite) {
%>

<!-- content here 
============================================================ -->
<a name="content"></a><jsp:include page="<%=targeturl%>"  flush="false">
	<jsp:param name="topmenu" value="<%=topMenu%>" />
	<jsp:param name="targeturl" value="<%=targeturl%>" />
</jsp:include><%
		} else {
%><iframe src="<%=targeturl%>" width="600" height="600"></iframe><%
		}
%>
	</div>
</div> <!-- end bodywrap -->
<div id="bodybottom">&nbsp;</div>


<!-- footer -->
<div id="footer">
<p><jsp:include page="/footer.html"  flush="false" />. Design by <a href="http://www.n-vent.com" title="The ultra77 template is designed and released by N-vent Design Services LLC">N-vent</a></p>
<ul>
<li><a href="http://www.oswd.org" title="Open Source Web Design">Open Source Web Design</a></li>

</ul>
</div> <!-- end footer -->

<!-- side menu  --><%
	if (null != arryTopMenuPage[nTopMenu][1]) {
%><jsp:include page="<%=arryTopMenuPage[nTopMenu][1]%>" flush="false">
	<jsp:param name="topmenu" value="<%=topMenu%>" />
	<jsp:param name="targeturl" value="<%=targeturl%>" />
</jsp:include><%
	}
%> <!-- end side menu -->

</div> <!-- end wrapper -->
</body>
</html>
