<%@ page extends="kr.pe.sinnori.weblib.jdf.AbstractJSP" language="java" session="true" autoFlush="true" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %><%
%><%@ page import="kr.pe.sinnori.weblib.htmlstring.StringReplacementActorUtil"%><%
%><jsp:useBean id="errorMessage" class="java.lang.String" scope="request" />
<h1>다운로드 에러 전용 페이지</h1><br/>
<script type="text/javascript">
<!--
	function init() {
	var boardDownloadFileOutDTO = {isError:true, errorMessage:"<%=
		StringReplacementActorUtil.replace(errorMessage, 
				StringReplacementActorUtil.STRING_REPLACEMENT_ACTOR_TYPE.ESCAPEECMASCRIPT) %>"};
	
		alert(boardDownloadFileOutDTO.errorMessage);
	}
	window.onload = init;	
//-->
</script>
