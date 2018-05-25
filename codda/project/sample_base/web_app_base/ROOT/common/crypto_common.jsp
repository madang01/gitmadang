<%@ page extends="kr.pe.codda.weblib.jdf.AbstractJSP" language="java" session="true" autoFlush="true" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %><%
%><%@ page import="kr.pe.codda.weblib.common.WebCommonStaticFinalVars" %><%
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
		var sessionkeyBase64 = sessionStorage.getItem('<%=WebCommonStaticFinalVars.SESSIONSTORAGE_KEY_NAME_OF_SESSIONKEY%>');
		if (typeof(sessionKey) == 'undefined') {
			var privateKey = CryptoJS.lib.WordArray.random(<%=WebCommonStaticFinalVars.WEBSITE_PRIVATEKEY_SIZE%>);
		
			var rsa = new RSAKey();
			rsa.setPublic("<%=modulusHex%>", "10001");
			
			var sessionKeyHex = rsa.encrypt(CryptoJS.enc.Base64.stringify(privateKey));		
			sessionkeyBase64 = CryptoJS.enc.Base64.stringify(CryptoJS.enc.Hex.parse(sessionKeyHex));

			sessionStorage.setItem('<%=WebCommonStaticFinalVars.SESSIONSTORAGE_KEY_NAME_OF_PRIVATEKEY%>', CryptoJS.enc.Base64.stringify(privateKey));
			sessionStorage.setItem('<%=WebCommonStaticFinalVars.SESSIONSTORAGE_KEY_NAME_OF_SESSIONKEY%>', sessionkeyBase64);
		}

		return sessionkeyBase64;
	}
</script>
