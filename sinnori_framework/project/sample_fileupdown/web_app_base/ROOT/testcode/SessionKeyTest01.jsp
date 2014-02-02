<%@ page language="java" session="true" autoFlush="true" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="org.apache.commons.lang.StringEscapeUtils" %>
<%@ page import="kr.pe.sinnori.common.sessionkey.SymmetricKey" %>
<jsp:useBean id="pageIV" class="java.lang.String" scope="request" /><%
	
	SymmetricKey webUserSymmetricKey = (SymmetricKey)request.getAttribute("webUserSymmetricKey");
	
%>
<script type="text/javascript" src="/js/cryptoJS/rollups/aes.js"></script>
<script type="text/javascript" src="/js/cryptoJS/components/core-min.js"></script>
<script type="text/javascript" src="/js/cryptoJS/components/cipher-core-min.js"></script>

<table>
	<tr>
		<td id="messageTxt"></td>
	</tr>
</table>
<script type="text/javascript">
<!--
	var pageIV = CryptoJS.enc.Base64.parse("<%=pageIV%>");
	
	// 이유를 알 수 없지만 웹 저장소에 이진값을 저장하면 hex string 이 된다. 다시 가져올때 역 hex 가 필요하다.
	var privateKey = CryptoJS.enc.Hex.parse(sessionStorage.getItem('sinnori.privatekey'));
	
	var messageTxt = CryptoJS.AES.decrypt("<%= webUserSymmetricKey.encryptStringBase64("hello") %>", privateKey, { mode: CryptoJS.mode.CBC, padding: CryptoJS.pad.Pkcs7, iv: pageIV });
		
	document.getElementById('messageTxt').innerHTML = "call SessionKeyTest01.jsp::"+ messageTxt.toString(CryptoJS.enc.Utf8);
//-->
</script>

