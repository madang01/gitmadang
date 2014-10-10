<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%><%
%><%@ page import="org.apache.commons.lang.StringEscapeUtils" %><%
	// String topmenu = request.getParameter("topmenu");
	
	session.setAttribute(kr.pe.sinnori.common.servlet.WebCommonStaticFinalVars.USERID_HTTPSESSION_NAME, null);
%>
<script>
goURL("/PageJump.jsp?topmenu=0");
</script>
