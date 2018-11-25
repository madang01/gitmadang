<%@page import="com.google.gson.Gson"%><%
%><%@ page import="kr.pe.codda.weblib.common.WebCommonStaticFinalVars" %><%
%><%@ page extends="kr.pe.codda.weblib.jdf.AbstractAdminJSP" language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%><%
%><jsp:useBean id="messageResultRes" class="kr.pe.codda.impl.message.MessageResultRes.MessageResultRes" scope="request" /><%
	String messageResultResJsonString = new Gson().toJson(messageResultRes);

%><!DOCTYPE html>
<html lang="ko">
<head>
<meta charset="UTF-8">
<meta http-equiv="X-UA-Compatible" content="IE=edge">
<meta name="viewport" content="width=device-width, initial-scale=1">
<title><%=WebCommonStaticFinalVars.USER_WEBSITE_TITLE%></title>
<!-- Latest compiled and minified CSS -->
<link rel="stylesheet" href="/bootstrap/3.3.7/css/bootstrap.css">
<!-- jQuery library -->
<script src="/jquery/3.3.1/jquery.min.js"></script>
<!-- Latest compiled JavaScript -->
<script src="/bootstrap/3.3.7/js/bootstrap.min.js"></script> 
<script type='text/javascript'>
	function init() {
		if (parent != null && parent.membershipProcessOKCallBack != null) {
			parent.membershipProcessOKCallBack();
		} else {
			alert("parent.membershipProcessOKCallBack 이 존재하지 않습니다");
		}
	}

	window.onload = init;
</script>
</head>
<body>
	<div class="container-fluid">
		<h3>일반 회원 가입 성공</h3>
	</div>
</body>
</html>
