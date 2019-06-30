<%@page import="kr.pe.codda.weblib.common.MemberStateType"%>
<%@page import="kr.pe.codda.common.etc.CommonStaticFinalVars"%><%
%><%@page import="kr.pe.codda.weblib.common.WebCommonStaticFinalVars"%><%
%><%@page import="kr.pe.codda.impl.message.MemberManagerRes.MemberManagerRes.Member"%><%
%><%@page extends="kr.pe.codda.weblib.jdf.AbstractAdminJSP" language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%><%
%><jsp:useBean id="memberManagerRes" class="kr.pe.codda.impl.message.MemberManagerRes.MemberManagerRes" scope="request" /><%

	final MemberStateType memberStateType = MemberStateType.valueOf(memberManagerRes.getMemberState());
	
	
%><!DOCTYPE html>
<html lang="ko">
<head>
<meta charset="UTF-8">
<meta http-equiv="X-UA-Compatible" content="IE=edge">
<meta name="viewport" content="width=device-width, initial-scale=1">
<title><%= WebCommonStaticFinalVars.ADMIN_WEBSITE_TITLE %></title>
<!-- Latest compiled and minified CSS -->
<link rel="stylesheet" href="/bootstrap/3.3.7/css/bootstrap.css">
<!-- jQuery library -->
<script src="/jquery/3.3.1/jquery.min.js"></script>
<!-- Latest compiled JavaScript -->
<script src="/bootstrap/3.3.7/js/bootstrap.min.js"></script> 
<script type='text/javascript'>
function init() {
	}
	window.onload = init;
</script>
</head>
<body>
	<div class=header>
		<div class="container">
		<%= getMenuNavbarString(request) %>
		</div>
	</div>
	<div class="content">
		<div class="container">
			<div class="panel-heading"><h4>게시판 관리자</h4></div>
			<div class="panel-body">
			</div>		
		</div>
	</div>
</body>
</html>