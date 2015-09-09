<%@ page extends="kr.pe.sinnori.common.weblib.AbstractJSP" language="java" session="true" autoFlush="true" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %><%
%><%@ page import="kr.pe.sinnori.common.weblib.WebCommonStaticFinalVars" %><%
%><jsp:useBean id="reqHeaderInfo" class="kr.pe.sinnori.impl.javabeans.ReqHeaderInfoBean" scope="request" />
<h1>JDF 테스트 - 비 로그인</h1>
<script type="text/javascript">
	function trim(str) {
		return str.replace(/^\s+|\s+$/gm,'');
	}

</script>
<ul>
<li>이 페이지는 AbstractServlet 를 상속 받은 페이지입니다.<br/>
모든 서블릿이 상속 받는 AbstractServlet 는 JDF 기본 상속 서블릿입니다.</li>
<li>
<%
	
	if (null == reqHeaderInfo) {
%>reqHeaderInfo is null<%		
	} else {
%>
title=[<%=reqHeaderInfo.title%>]<br/>
headerInfoSize=[<%=reqHeaderInfo.headerInfoSize%>]<br/>
<%
		for (int i=0; i < reqHeaderInfo.headerInfoSize; i++) {
			String headerKey = reqHeaderInfo.headerInfoList[i].headerKey;
			String headerValue = reqHeaderInfo.headerInfoList[i].headerValue;
%>
index[<%=i%>].headerKey=[<%=escapeHtml(headerKey, WebCommonStaticFinalVars.LINE2BR_STRING_REPLACER)%>]<br/>
index[<%=i%>].headerValue=[<%=escapeHtml(headerValue, WebCommonStaticFinalVars.LINE2BR_STRING_REPLACER)%>]<br/><br/><%
		}
	}
%>
</li>
</ul>
