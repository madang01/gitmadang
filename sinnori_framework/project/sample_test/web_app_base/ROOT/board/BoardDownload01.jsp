<%@ page extends="kr.pe.sinnori.common.weblib.AbstractJSP" language="java" session="true" autoFlush="true" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %><%
%><%@ page import="kr.pe.sinnori.common.weblib.WebCommonStaticFinalVars" %><%
%><jsp:useBean id="topmenu" class="java.lang.String" scope="request" /><%
%><jsp:useBean id="leftmenu" class="java.lang.String" scope="request" /><%
%><jsp:useBean id="errorMessage" class="java.lang.String" scope="request" />
<h1>자유 게시판 - 다운로드 에러 전용 페이지</h1><br/>
<script type="text/javascript">
<!--
	function init() {<%
	if (null != errorMessage && !errorMessage.equals("")) {
%>
		var boardDownloadFileOutDTO = {isError:true, errorMessage:"<%=escapeScript(errorMessage)%>"};<%
	} else {
%>
		var boardDownloadFileOutDTO = {isError:true, errorMessage:"에러 메시지 내용을 전달  받지 못했습니다."};<%
	}
%>
		alert(boardDownloadFileOutDTO.errorMessage);
	}
	window.onload = init;	
//-->
</script>
