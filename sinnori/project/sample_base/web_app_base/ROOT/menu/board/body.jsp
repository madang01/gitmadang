<%@ page extends="kr.pe.sinnori.weblib.jdf.AbstractJSP" language="java" contentType="text/html; charset=UTF-8"   pageEncoding="UTF-8"%><%
%><%@ page import="kr.pe.sinnori.weblib.common.WebCommonStaticFinalVars" %><%
%><%@ page import="kr.pe.sinnori.weblib.sitemenu.SiteTopMenuType" %><%
	request.setAttribute(WebCommonStaticFinalVars.SITE_TOPMENU_REQUEST_KEY_NAME, SiteTopMenuType.COMMUNITY);
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
	function goList() {
		var g = document.goListForm;		
		g.submit();
	}

	window.onload=goList;
</script>
</head>
<body>
	<form name="goListForm" method="post" action="/servlet/BoardList">
	<input type="hidden" name="topmenu" value="<%=getCurrentTopMenuIndex(request)%>" />
	<input type="hidden" name="boardId" value="2" />
	</form>
	<session>
	사랑방 - body part
	</session>
</body>
</html>