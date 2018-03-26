<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%><% 
%><%@page import="kr.pe.sinnori.weblib.common.WebCommonStaticFinalVars"%><% 
%><%@ page import="org.apache.commons.lang3.StringEscapeUtils" %><%	
	session.setAttribute(WebCommonStaticFinalVars.HTTPSESSION_KEY_USERID_NAME, null);
%>
<script>				
top.document.location.href = "/";
</script>
