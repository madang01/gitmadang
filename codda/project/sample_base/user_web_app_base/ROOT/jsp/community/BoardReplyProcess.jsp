<%@page import="com.google.gson.Gson"%><%
%><%@page import="kr.pe.codda.weblib.common.WebCommonStaticFinalVars"%><%
%><%@page import="kr.pe.codda.impl.message.BoardReplyRes.BoardReplyRes"%><%
%><%@ page extends="kr.pe.codda.weblib.jdf.AbstractUserJSP" language="java" session="true" autoFlush="true" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %><%
	BoardReplyRes boardReplyRes = (BoardReplyRes)request.getAttribute("boardReplyRes");
%><!DOCTYPE html>
<html lang="ko">
<head>
<meta charset="UTF-8">
<title><%= WebCommonStaticFinalVars.USER_WEBSITE_TITLE %></title>
<script type="text/javascript">
    function init() {<%
    if (null != boardReplyRes) {
    		String boardReplyResJsonString = new Gson().toJson(boardReplyRes);	
%>
		var boardReplyResObj = <%= boardReplyResJsonString %>;	
    	parent.callBackForBoardReplyProcess(boardReplyResObj);<%
    } else {
%>
		alert("the var boardReplyRes is null");<%
	}
%>
    }
    
    window.onload=init;
</script>
</head>
<body>
</html>