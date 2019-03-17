<%@page import="kr.pe.codda.weblib.common.WebCommonStaticFinalVars"%><%
%><%@page extends="kr.pe.codda.weblib.jdf.AbstractAdminJSP" language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%><%
%><jsp:useBean id="boardID" class="java.lang.String" scope="request" /><%
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
		if (parent != null && parent.callBackForBoadInfoModification != null) {
			parent.callBackForBoadInfoModification("<%= boardID %>");
		} else {
			alert("parent.callBackForBoadInfoModification 이 존재하지 않습니다");
		}
	}
	window.onload = init;
</script>
</head>
	<body>
		<div class="container-fluid">
			<h1>게시판 정보 수정 성공 콜백 호출</h1>
		</div>
	<body>
</html>