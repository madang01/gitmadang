<%@page import="kr.pe.codda.weblib.common.WebCommonStaticFinalVars"%><%
%><%@ page extends="kr.pe.codda.weblib.jdf.AbstractUserJSP" language="java" session="true" autoFlush="true" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %><%

%><!DOCTYPE html>
<html lang="ko">
<head>
<meta charset="UTF-8">
<title><%= WebCommonStaticFinalVars.USER_WEBSITE_TITLE %></title>
<script type="text/javascript">
    function init() {
    	parent.callBackForBoardMoveProcess();	
    }
    
    window.onload=init;
</script>
</head>
<body>
</html>