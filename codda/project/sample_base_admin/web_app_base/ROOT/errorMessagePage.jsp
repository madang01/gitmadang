<%@ page extends="kr.pe.codda.weblib.jdf.AbstractJSP" language="java" session="true" autoFlush="true" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %><%
%><%@ page import="kr.pe.codda.weblib.common.WebCommonStaticFinalVars" %><%
%><%@ page import="kr.pe.codda.weblib.htmlstring.HtmlStringUtil"%><%
%><%@ page import="kr.pe.codda.weblib.sitemenu.AdminSiteMenuManger" %><%
%><jsp:useBean id="userMessage" class="java.lang.String" scope="request" /><%
%><jsp:useBean id="debugMessage" class="java.lang.String" scope="request" /><%
	AdminSiteMenuManger adminSiteMenuManger = AdminSiteMenuManger.getInstance();
%><!DOCTYPE html>
<html lang="ko">
<head>
<meta charset="UTF-8">
<meta http-equiv="X-UA-Compatible" content="IE=edge">
<meta name="viewport" content="width=device-width, initial-scale=1">
<title>Codda JAVA System Properties</title>
<!-- Latest compiled and minified CSS -->
<link rel="stylesheet" href="/bootstrap/3.3.7/css/bootstrap.css">
<!-- jQuery library -->
<script src="/jquery/3.3.1/jquery.min.js"></script>
<!-- Latest compiled JavaScript -->
<script src="/bootstrap/3.3.7/js/bootstrap.min.js"></script> 

</head>
<body>
<%=adminSiteMenuManger.getSiteNavbarString(isAdminLogin(request))%>
	
	<div class="container-fluid">
		<h3>에러 내용</h3>
		<table class="table table-striped">
			<thead>
				<tr>
					<th>종류</th>
					<th>내용</th>
				</tr>
			</thead>
			<tbody>
				<tr>
					<td>일반</td>
					<td><%= userMessage %></td>
				</tr>
				<tr>
					<td>디버깅</td>
					<td><%= debugMessage %></td>
				</tr>
			</tbody>
		</table>
	</div>
</body>
</html>
