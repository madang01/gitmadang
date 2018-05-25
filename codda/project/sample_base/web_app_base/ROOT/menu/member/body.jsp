<%@ page extends="kr.pe.codda.weblib.jdf.AbstractJSP" language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%><%
%><%@ page import="kr.pe.codda.weblib.common.WebCommonStaticFinalVars" %><%
%><%@ page import="kr.pe.codda.weblib.sitemenu.SiteTopMenuType" %><%
	setSiteTopMenu(request, SiteTopMenuType.MEMBER);
%>
회원 - body part
<script>
top.document.location.href = "/servlet/Login?topmenu=<%=getCurrentTopMenuIndex(request)%>";
</script>
