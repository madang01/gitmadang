<%@ page extends="kr.pe.sinnori.common.weblib.AbstractJSP" language="java" session="true" autoFlush="true" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %><%
%><%@ page import="kr.pe.sinnori.common.weblib.WebCommonStaticFinalVars" %><%

	String userMessage = (String)request.getAttribute("userMessage");
	if (null == userMessage) userMessage="";
	String debugMessage = (String)request.getAttribute("debugMessage");
	
%>
userMessage=[<%=escapeHtml(userMessage, WebCommonStaticFinalVars.LINE2BR_STRING_REPLACER)%>]<br/><%
	if (null != debugMessage) {
%>
debugMessage=[<%=escapeHtml(debugMessage, WebCommonStaticFinalVars.LINE2BR_STRING_REPLACER)%>]<br/><%
	}
%>
<a href="/">HOME</a>
