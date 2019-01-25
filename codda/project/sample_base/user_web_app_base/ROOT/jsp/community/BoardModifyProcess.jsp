<%@page import="com.google.gson.Gson"%><%
%><%@page import="kr.pe.codda.weblib.common.WebCommonStaticFinalVars"%><%
%><%@page import="kr.pe.codda.impl.message.BoardModifyRes.BoardModifyRes"%><%
%><%@ page extends="kr.pe.codda.weblib.jdf.AbstractUserJSP" language="java" session="true" autoFlush="true" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %><%
	BoardModifyRes boardModifyRes = (BoardModifyRes)request.getAttribute("boardModifyRes");
%><!DOCTYPE html>
<html lang="ko">
<head>
<meta charset="UTF-8">
<title><%= WebCommonStaticFinalVars.USER_WEBSITE_TITLE %></title>
<script type="text/javascript">
    function init() {<%
    if (null != boardModifyRes) {
    		String boardModifyResJsonString = new Gson().toJson(boardModifyRes);	
%>
		var boardModifyRes = <%= boardModifyResJsonString %>;
    	parent.callBackForBoardModifyProcess(boardModifyRes);<%
    } else {
%>
		alert("the var boardModifyRes is null");<%
	}
%>	
    }
    
    window.onload=init;
</script>
</head>
<body>
</html>