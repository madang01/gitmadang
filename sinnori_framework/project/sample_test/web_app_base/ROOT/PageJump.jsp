<%@ page language="java" session="true" autoFlush="true" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %><%

	final String arryTopMenuPage[][] =	{ 
		{ "소개", null, "/about.jsp"},
		{ "시작하기", null, "/stepbystep/main.jsp"},
		{ "다운로드", null, "/download/main.jsp"},
		{ "문서", "/techdoc/leftmenu.jsp", "/techdoc/sinnori_website_structure_intro.jsp"},
		{ "마당쇠", "/madangsoe/leftmenu.jsp", "/construction_zone.jsp"}, 
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
	
%><!DOCTYPE html>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html;charset=utf-8" />
<title><jsp:include page="/title.html" /></title>
<script type="text/javascript">
    function goURL(targeturl) {
	var f = document.directgofrm;
	f.action = targeturl;
	f.submit();	
    }
</script>
</head>
<body>
<form name="directgofrm" method="post">
<input type="hidden" name="topmenu" value="<%=nTopMenu%>"/>
</form>
<table>
    <tr>
	<td>
	    <nav>
	    <h1><%
		    for (int i=0; i < arryTopMenuPage.length; i++) {
	    %>
	    <a href="/PageJump.jsp?topmenu=<%=i%>"><%=arryTopMenuPage[i][0]%></a><%
		    }
	    %>
	    </h1>
	    </nav>
	</td>
    </tr>
    <tr>
	<td id="main" valign="top"><%
		    if (null == arryTopMenuPage[nTopMenu][1]) {
	    %>
		    <jsp:include page="<%=targeturl%>" /><%
		    } else {
	    %>
	    <table>
		<tr valign="top">
		<td id="leftmenu"><jsp:include page="<%=arryTopMenuPage[nTopMenu][1]%>" /></td><td id="body">
		<div style="overflow:auto;width:800px;"><%
		int inxOfExt = targeturl.lastIndexOf(".");
		String extStr = null; 
		if (inxOfExt > 0) extStr = targeturl.substring(inxOfExt);
		
		
		if (null != extStr && (extStr.equals(".jsp") || extStr.equals(".html") || extStr.equals(".htm"))) {
		%><jsp:include page="<%=targeturl%>" /><%
		} else {
		%><iframe src="<%=targeturl%>" width="800" height="600" /><%
		}
		%></div></td>
		</tr>
	    </table><%
		    }
	    %>
	</td>
    </tr>
    <tr>
	<td><jsp:include page="/footer.html" /></td>
    </tr>
</table>
</body>
</html>
