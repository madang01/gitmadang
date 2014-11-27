<%@ page extends="kr.pe.sinnori.common.weblib.AbstractJSP" language="java" session="true" autoFlush="true" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %><%
%><%@ page import="kr.pe.sinnori.common.weblib.WebCommonStaticFinalVars" %><%
	String modulusHex = request.getParameter("modulusHex");	
%>
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
	function getSessionkeyBase64() {
		var sessionkeyBase64 = sessionStorage.getItem('<%=WebCommonStaticFinalVars.SESSIONSTORAGE_SESSIONKEY_NAME%>');
		if (typeof(sessionKey) == 'undefined') {
			<!-- 보안을 위해서 로그인시 생성한 비밀키와 세션키 덮어쓰기 -->
			var privateKey = CryptoJS.lib.WordArray.random(<%=WebCommonStaticFinalVars.WEBSITE_PRIVATEKEY_SIZE%>);
		
			var rsa = new RSAKey();
			rsa.setPublic("<%=modulusHex%>", "10001");
			
			var sessionKeyHex = rsa.encrypt(CryptoJS.enc.Base64.stringify(privateKey));		
			sessionkeyBase64 = CryptoJS.enc.Base64.stringify(CryptoJS.enc.Hex.parse(sessionKeyHex));

			sessionStorage.setItem('<%=WebCommonStaticFinalVars.SESSIONSTORAGE_PRIVATEKEY_NAME%>', CryptoJS.enc.Base64.stringify(privateKey));
			sessionStorage.setItem('<%=WebCommonStaticFinalVars.SESSIONSTORAGE_SESSIONKEY_NAME%>', sessionkeyBase64);
		}

		return sessionkeyBase64;
	}
</script>
