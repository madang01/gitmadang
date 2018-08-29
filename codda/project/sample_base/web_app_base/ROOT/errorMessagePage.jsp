<%@page import="kr.pe.codda.weblib.htmlstring.StringReplacementActorUtil.STRING_REPLACEMENT_ACTOR_TYPE"%><%
%><%@page import="kr.pe.codda.weblib.htmlstring.StringReplacementActorUtil"%><%
%><%@ page import="kr.pe.codda.weblib.common.WebCommonStaticFinalVars" %><%
%><%@ page extends="kr.pe.codda.weblib.jdf.AbstractUserJSP" language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%><%
%><jsp:useBean id="userMessage" class="java.lang.String" scope="request" /><%
%><jsp:useBean id="debugMessage" class="java.lang.String" scope="request" /><%
%><!DOCTYPE html>
<html lang="ko">
<head>
<meta charset="UTF-8">
<meta http-equiv="X-UA-Compatible" content="IE=edge">
<meta name="viewport" content="width=device-width, initial-scale=1">
<title><%= WebCommonStaticFinalVars.WEBSITE_TITLE %></title>
<!-- Latest compiled and minified CSS -->
<link rel="stylesheet" href="/bootstrap/3.3.7/css/bootstrap.css">
<!-- jQuery library -->
<script src="/jquery/3.3.1/jquery.min.js"></script>
<!-- Latest compiled JavaScript -->
<script src="/bootstrap/3.3.7/js/bootstrap.min.js"></script> 

<script type="text/javascript" src="/js/jsbn/jsbn.js"></script>
<script type="text/javascript" src="/js/jsbn/jsbn2.js"></script>
<script type="text/javascript" src="/js/jsbn/prng4.js"></script>
<script type="text/javascript" src="/js/jsbn/rng.js"></script>
<script type="text/javascript" src="/js/jsbn/rsa.js"></script>
<script type="text/javascript" src="/js/jsbn/rsa2.js"></script>
<script type="text/javascript" src="/js/cryptoJS/rollups/aes.js"></script>
<script type="text/javascript" src="/js/cryptoJS/rollups/tripledes.js"></script>
<script type="text/javascript" src="/js/cryptoJS/components/core-min.js"></script>
<script type="text/javascript" src="/js/cryptoJS/components/cipher-core-min.js"></script>
<script type="text/javascript">
<!--
	function chkform() {		
		var f = document.frm;
		var g = document.gofrm;		
		var encryptedBytes;
			 
		switch(f.algorithm.selectedIndex) {
			case 0:
				var privateKey = CryptoJS.lib.WordArray.random(16);		
				g.privateKey.value = CryptoJS.enc.Hex.stringify(privateKey);
				var iv = CryptoJS.lib.WordArray.random(16);
				g.iv.value = CryptoJS.enc.Hex.stringify(iv);
				encryptedBytes = CryptoJS.AES.encrypt(f.plainText.value, privateKey, { mode: CryptoJS.mode.CBC, padding: CryptoJS.pad.Pkcs7, iv: iv });
				break;
			case 1:
				var privateKey = CryptoJS.lib.WordArray.random(8);		
				g.privateKey.value = CryptoJS.enc.Hex.stringify(privateKey);
				var iv = CryptoJS.lib.WordArray.random(8);
				g.iv.value = CryptoJS.enc.Hex.stringify(iv);
				encryptedBytes = CryptoJS.DES.encrypt(f.plainText.value, privateKey, { mode: CryptoJS.mode.CBC, padding: CryptoJS.pad.Pkcs7, iv: iv });
				break;
			case 2:
				var privateKey = CryptoJS.lib.WordArray.random(24);		
				g.privateKey.value = CryptoJS.enc.Hex.stringify(privateKey);
				var iv = CryptoJS.lib.WordArray.random(8);
				g.iv.value = CryptoJS.enc.Hex.stringify(iv);
				encryptedBytes = CryptoJS.TripleDES.encrypt(f.plainText.value, privateKey, { mode: CryptoJS.mode.CBC, padding: CryptoJS.pad.Pkcs7, iv: iv });			  
				break;  
			default:
			 alert("unkown digest message algothm");
			 return false;
		}
			
		// g.encryptedBytes.value = encryptedBytes.ciphertext;
		g.encryptedHexText.value = CryptoJS.enc.Hex.stringify(encryptedBytes);
		g.algorithm.value = f.algorithm.options[f.algorithm.selectedIndex].value;
		g.plainText.value = f.plainText.value;
		g.submit();
		
		return false;
	}
	

	function init() {	
	}

	window.onload = init;	
//-->
</script>
</head>
<body>
<%= getSiteNavbarString(request) %>
	
	<div class="container-fluid">
		<h2>에러 메시지</h2>
<div class="alert alert-warning">
<strong>에러내용:</strong> <% 
	if (null != userMessage && userMessage.trim().length() != 0) {
		out.print(StringReplacementActorUtil.replace(userMessage, STRING_REPLACEMENT_ACTOR_TYPE.ESCAPEHTML4));
	}
%>
</div><%
	if (null != debugMessage && debugMessage.trim().length() != 0) {
%>
<div class="alert alert-warning">
<strong>디버깅:</strong> <%= StringReplacementActorUtil.replace(debugMessage, STRING_REPLACEMENT_ACTOR_TYPE.ESCAPEHTML4) %>
</div><%
	}
%>
	</div>	
</body>
</html>
