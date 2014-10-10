<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%><%
%><%@ page import="kr.pe.sinnori.common.servlet.WebCommonStaticFinalVars" %><%
%><%@ page import="org.apache.commons.lang.StringEscapeUtils" %><%
%><jsp:useBean id="topmenu" class="java.lang.String" scope="request" /><%
%><jsp:useBean id="leftmenu" class="java.lang.String" scope="request" /><%
%><jsp:useBean id="ivBase64" class="java.lang.String" scope="request" /><%
%><jsp:useBean id="messageResultOutObj" class="kr.pe.sinnori.impl.message.MessageResult.MessageResult" scope="request" /><%

	kr.pe.sinnori.common.sessionkey.SymmetricKey webUserSymmetricKey = (kr.pe.sinnori.common.sessionkey.SymmetricKey)request.getAttribute("webUserSymmetricKey");

	String resultMessage = messageResultOutObj.getResultMessage();
	String taskResult = messageResultOutObj.getTaskResult();

	String orignalResultMessage = StringEscapeUtils.escapeHtml(resultMessage);
%>
<script type="text/javascript" src="/js/cryptoJS/rollups/aes.js"></script>
<script type="text/javascript" src="/js/cryptoJS/components/core-min.js"></script>
<script type="text/javascript" src="/js/cryptoJS/components/cipher-core-min.js"></script>
<table>
	<tr><td><h1>회원 가입 결과</h1></td></tr>
	<tr>
	    <td id="idTxtResultMessage"></td> 
	</tr>	
</table>

<script type="text/javascript">
<!--
	function init() {
		var pageIV = CryptoJS.enc.Base64.parse("<%=ivBase64%>");
		var privateKey = CryptoJS.enc.Base64.parse(sessionStorage.getItem('<%=WebCommonStaticFinalVars.SESSIONSTORAGE_PRIVATEKEY_NAME%>'));

		var resultMessage = CryptoJS.AES.decrypt("<%=webUserSymmetricKey.encryptStringBase64(orignalResultMessage)%>", privateKey, { mode: CryptoJS.mode.CBC, padding: CryptoJS.pad.Pkcs7, iv: pageIV });
		document.getElementById('idTxtResultMessage').innerHTML = resultMessage.toString(CryptoJS.enc.Utf8);

		<!-- 보안을 위해서 회원가입시 생성한 비밀키와 세션키 삭제 -->
		sessionStorage.removeItem('<%=WebCommonStaticFinalVars.SESSIONSTORAGE_PRIVATEKEY_NAME%>');
		sessionStorage.removeItem('<%=WebCommonStaticFinalVars.SESSIONSTORAGE_SESSIONKEY_NAME%>');
	}

	window.onload = init;
	
//-->
</script>
