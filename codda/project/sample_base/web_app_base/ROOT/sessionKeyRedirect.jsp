<%@page import="org.apache.commons.text.StringEscapeUtils"%>
<%@page import="java.util.Enumeration"%>
<%@page import="kr.pe.codda.weblib.common.WebCommonStaticFinalVars"%><%
%><%@ page extends="kr.pe.codda.weblib.jdf.AbstractAdminJSP" language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%><%
%><jsp:useBean id="requestURI" class="java.lang.String" scope="request" /><%
	String modulusHexString =  getModulusHexString(request);
	 
%><!DOCTYPE html>
<html lang="ko">
<head>
<meta charset="UTF-8">
<!-- 세션키 암화화 모듈 --> 
<script type="text/javascript" src="/js/jsbn/jsbn.js"></script>
<script type="text/javascript" src="/js/jsbn/jsbn2.js"></script>
<script type="text/javascript" src="/js/jsbn/prng4.js"></script>
<script type="text/javascript" src="/js/jsbn/rng.js"></script>
<script type="text/javascript" src="/js/jsbn/rsa.js"></script>
<script type="text/javascript" src="/js/jsbn/rsa2.js"></script>
<script type="text/javascript" src="/js/cryptoJS/rollups/sha256.js"></script>
<script type="text/javascript" src="/js/cryptoJS/rollups/aes.js"></script>
<script type="text/javascript" src="/js/cryptoJS/components/core-min.js"></script>
<script type="text/javascript" src="/js/cryptoJS/components/cipher-core-min.js"></script>
<script type="text/javascript">
<!--
	function init() {
		if (typeof(sessionStorage) == 'undefined' ) {
			alert("Your browser does not support HTML5 sessionStorage. Please Upgrade your browser.");
			return;
		}
		
		var webUserPrivateKeyBase64 = sessionStorage.getItem("<%= WebCommonStaticFinalVars.SESSIONSTORAGE_KEY_NAME_OF_PRIVATEKEY %>");
		
		var webUserPrivateKey;
		
		if (null == webUserPrivateKeyBase64 || '' == webUserPrivateKeyBase64) {
			webUserPrivateKey = CryptoJS.lib.WordArray.random(<%= WebCommonStaticFinalVars.WEBSITE_PRIVATEKEY_SIZE %>);
			webUserPrivateKeyBase64 = CryptoJS.enc.Base64.stringify(webUserPrivateKey);
			sessionStorage.setItem('<%= WebCommonStaticFinalVars.SESSIONSTORAGE_KEY_NAME_OF_PRIVATEKEY %>', webUserPrivateKeyBase64);
		} else {
			webUserPrivateKey = CryptoJS.enc.Base64.parse(webUserPrivateKeyBase64);
		}
		
		var rsa = new RSAKey();
		rsa.setPublic("<%= modulusHexString %>", "10001");
		
		
		var sessionKeyHex = rsa.encrypt(webUserPrivateKeyBase64);		
		var sessionkeyBase64 = CryptoJS.enc.Base64.stringify(CryptoJS.enc.Hex.parse(sessionKeyHex));
		
		var iv = CryptoJS.lib.WordArray.random(<%= WebCommonStaticFinalVars.WEBSITE_IV_SIZE %>);
		
		var g = document.gofrm;
		g.<%= WebCommonStaticFinalVars.PARAMETER_KEY_NAME_OF_SESSION_KEY %>.value = sessionkeyBase64;
		g.<%= WebCommonStaticFinalVars.PARAMETER_KEY_NAME_OF_SESSION_KEY_IV %>.value = CryptoJS.enc.Base64.stringify(iv);
		
		g.submit();
	}
	
	window.onload = init;
//-->
</script>
</head>
<body>
	<form name=gofrm method=post action="<%= requestURI %>">
		<input type=hidden name="<%=WebCommonStaticFinalVars.PARAMETER_KEY_NAME_OF_SESSION_KEY %>" >
		<input type=hidden name="<%=WebCommonStaticFinalVars.PARAMETER_KEY_NAME_OF_SESSION_KEY_IV %>" ><%
	Enumeration<String> parmEnum = request.getParameterNames();
	while(parmEnum.hasMoreElements()) {
		String parmKey = parmEnum.nextElement();
		String parmValue = request.getParameter(parmKey);
		
		if (parmKey.equals(WebCommonStaticFinalVars.PARAMETER_KEY_NAME_OF_SESSION_KEY) ||
				parmKey.equals(WebCommonStaticFinalVars.PARAMETER_KEY_NAME_OF_SESSION_KEY_IV)) {
			continue;
		}
%>
		<textarea style="visibility:hidden;" name="<%= StringEscapeUtils.escapeHtml4(parmKey) %>"><%= StringEscapeUtils.escapeHtml4(parmValue) %></textarea><%

	}
%>	
	</form>
</body>
</html>