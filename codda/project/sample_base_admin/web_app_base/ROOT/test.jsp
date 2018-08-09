<%@ page extends="kr.pe.codda.weblib.jdf.AbstractAdminJSP" language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%><%
%><%@ page import="kr.pe.codda.weblib.common.WebCommonStaticFinalVars" %><%
%><%@ page import="kr.pe.codda.weblib.sitemenu.AdminSiteMenuManger" %><%
	AdminSiteMenuManger adminSiteMenuManger = AdminSiteMenuManger.getInstance();
	
	String tmp= request.getParameter("tmp"); 
	
	log.info("tmp={}", tmp);
%><!DOCTYPE html>
<html lang="ko">
<head>
<meta charset="UTF-8">
<meta http-equiv="X-UA-Compatible" content="IE=edge">
<meta name="viewport" content="width=device-width, initial-scale=1">
<title><%= WebCommonStaticFinalVars.WEBSITE_TITLE %></title>
<!-- Latest compiled and minified CSS -->
<link rel="stylesheet" href="/bootstrap/3.3.7/css/bootstrap.css">
<!-- jQuery library -->
<script src="/jquery/3.3.1/jquery.min.js"></script>
<!-- Latest compiled JavaScript -->
<script src="/bootstrap/3.3.7/js/bootstrap.min.js"></script> 

<script type="text/javascript">
<!--
	
//-->
</script>
</head>
<body>
<%=adminSiteMenuManger.getSiteNavbarString(getGroupRequestURL(request), isAdminLoginedIn(request))%>	
	<div class="container-fluid">
		<h3>Test Page</h3>
		<a href="/servlet/MenuModify?menuNo=60&menuName=테스트2&linkURL=/test2">메뉴60번 수정</a>
		<form name="frm" method="post" class="form-inline" action="/test.jsp">
			<input type="text" name="tmp">		
			<button type="submit" class="btn btn-default">추가</button>
		</form>
		파라미터 'tmp' = [<%= tmp %>]
	</div>
</body>
</html>
