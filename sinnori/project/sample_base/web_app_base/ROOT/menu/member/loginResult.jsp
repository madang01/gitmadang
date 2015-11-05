<%@ page extends="kr.pe.sinnori.weblib.jdf.AbstractJSP" language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%><%
%><%@ page import="kr.pe.sinnori.weblib.common.WebCommonStaticFinalVars" %><%
%><%@ page import="kr.pe.sinnori.weblib.htmlstring.HtmlStringUtil"%><%
%><jsp:useBean id="topmenu" class="java.lang.String" scope="request" /><%
%><jsp:useBean id="leftmenu" class="java.lang.String" scope="request" /><%
%><jsp:useBean id="parmIVBase64" class="java.lang.String" scope="request" /><%
%><jsp:useBean id="modulusHex" class="java.lang.String" scope="request" /><%
%><jsp:useBean id="messageResultOutObj" class="kr.pe.sinnori.impl.message.MessageResult.MessageResult" scope="request" /><%

	kr.pe.sinnori.common.sessionkey.SymmetricKey webUserSymmetricKey = (kr.pe.sinnori.common.sessionkey.SymmetricKey)request.getAttribute("webUserSymmetricKey");

	// String resultMessage = messageResultOutObj.getResultMessage();
	boolean isSuccess = messageResultOutObj.getIsSuccess();

	
%>
<script type="text/javascript" src="/js/cryptoJS/rollups/aes.js"></script>
<script type="text/javascript" src="/js/cryptoJS/components/core-min.js"></script>
<script type="text/javascript" src="/js/cryptoJS/components/cipher-core-min.js"></script>
<table>
	<tr><td><h1>로그인 결과</h1></td></tr>
	<tr>
	    <td id="idTxtResultMessage"></td>
	</tr>
	<tr>
		<td><%
if (isSuccess) {
%><a href="/">home</a><%
	} else {
%><a href="#" onclick="window.history.back()">back</a><%
	}
%></td>
	</tr>
</table>

<script type="text/javascript">
<!--
	function init() {
		var pageIV = CryptoJS.enc.Base64.parse("<%=parmIVBase64%>");
		var privateKey = CryptoJS.enc.Base64.parse(sessionStorage.getItem('<%=WebCommonStaticFinalVars.SESSIONSTORAGE_KEY_PRIVATEKEY_NAME%>'));

		var resultMessage = CryptoJS.AES.decrypt("<%=webUserSymmetricKey.encryptStringBase64(HtmlStringUtil.toScriptString(messageResultOutObj.toString()))%>", privateKey, { mode: CryptoJS.mode.CBC, padding: CryptoJS.pad.Pkcs7, iv: pageIV });
		document.getElementById('idTxtResultMessage').innerHTML = resultMessage.toString(CryptoJS.enc.Utf8);

		<!-- 보안을 위해서 로그인시 생성한 비밀키와 세션키 덮어쓰기 -->
		var privateKey = CryptoJS.lib.WordArray.random(<%=WebCommonStaticFinalVars.WEBSITE_PRIVATEKEY_SIZE%>);
		
		var rsa = new RSAKey();
		rsa.setPublic("<%=modulusHex%>", "10001");
			
		var sessionKeyHex = rsa.encrypt(CryptoJS.enc.Base64.stringify(privateKey));		
		var sessionkeyBase64 = CryptoJS.enc.Base64.stringify(CryptoJS.enc.Hex.parse(sessionKeyHex));

		sessionStorage.setItem('<%=WebCommonStaticFinalVars.SESSIONSTORAGE_KEY_PRIVATEKEY_NAME%>', CryptoJS.enc.Base64.stringify(privateKey));
		sessionStorage.setItem('<%=WebCommonStaticFinalVars.SESSIONSTORAGE_KEY_SESSIONKEY_NAME%>', sessionkeyBase64);
	}

	window.onload = init;
	
//-->
</script>
