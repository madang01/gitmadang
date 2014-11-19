<%@ page extends="kr.pe.sinnori.common.weblib.AbstractJSP" language="java" session="true" autoFlush="true" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %><%
%><%@ page import="kr.pe.sinnori.common.weblib.WebCommonStaticFinalVars" %><%
%><jsp:useBean id="topmenu" class="java.lang.String" scope="request" /><%
%><jsp:useBean id="leftmenu" class="java.lang.String" scope="request" /><%
%><jsp:useBean id="errorMessage" class="java.lang.String" scope="request" /><%
%><jsp:useBean id="boardUploadFileOutDTO" class="kr.pe.sinnori.impl.message.BoardUploadFileOutDTO.BoardUploadFileOutDTO" scope="request" />
<h1>자유 게시판 - 업로드 파일 결과</h1><br/><%
	if (null != errorMessage && !errorMessage.equals("")) {
%>
	<div>
		<ul>
		<li>
			<dl>
				<dt>에러</dt>
				<dd><%=escapeHtml(errorMessage, WebCommonStaticFinalVars.LINE2BR_STRING_REPLACER)%></dd>
			</dl>
		</li>
		</ul>		
	</div><%
	} else {
%>
	<div>
		<ul>
		<li>
			<dl>
				<dt>Attach ID</dt>
				<dd><%= boardUploadFileOutDTO.getAttachId() %></dd>
			</dl>
		</li>
		</ul>		
	</div><%
	}
%>
