<%@page import="com.google.gson.Gson"%><%
%><%@page import="kr.pe.codda.impl.message.BoardWriteRes.BoardWriteRes"%>
<%@page import="kr.pe.codda.weblib.common.WebCommonStaticFinalVars"%><%
%><%@ page extends="kr.pe.codda.weblib.jdf.AbstractJSP" language="java" session="true" autoFlush="true" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %><%
	BoardWriteRes boardWriteRes = (BoardWriteRes)request.getAttribute("boardWriteRes");
%><!DOCTYPE html>
<html lang="ko">
<head>
<meta charset="UTF-8">
<title><%= WebCommonStaticFinalVars.USER_WEBSITE_TITLE %></title>
<script type="text/javascript">
    function init() {<%
    	if (null != boardWriteRes) {
    		String boardWriteResJsonString = new Gson().toJson(boardWriteRes);
    		
    		
%>
		parent.callBackForBoardWriteProcess("<%= boardWriteResJsonString %>");<%
    	} else {
%>
		alert("the var boardWriteRes is null");<%
    	}
%>    		
    }
    
    window.onload=init;
</script>
</head>
<body>
</html>
