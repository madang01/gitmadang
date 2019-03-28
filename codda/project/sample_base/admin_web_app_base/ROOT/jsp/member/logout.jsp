<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%><% 
%><%@page import="kr.pe.codda.weblib.common.WebCommonStaticFinalVars"%><%
	session.setAttribute(WebCommonStaticFinalVars.HTTPSESSION_KEY_NAME_OF_LOGINED_USER_INFORMATION, null);
%>
<script>				
top.document.location.href = "/about.jsp";
</script>
