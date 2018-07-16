<%@page import="kr.pe.codda.weblib.sitemenu.AdminSiteMenuManger"%><%
%><%@page import="com.google.gson.Gson"%><%
%><%@ page import="kr.pe.codda.weblib.common.WebCommonStaticFinalVars" %><%
%><%@ page extends="kr.pe.codda.weblib.jdf.AbstractJSP" language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%><%
%><jsp:useBean id="messageResultRes" class="kr.pe.codda.impl.message.MessageResultRes.MessageResultRes" scope="request" /><%
	AdminSiteMenuManger adminSiteMenuManger = AdminSiteMenuManger.getInstance();	
	String messageResultResJsonString = new Gson().toJson(messageResultRes);

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
<script type='text/javascript'>
	function init() {
		var pageIV = CryptoJS.enc.Base64.parse("<%= getParameterIVBase64Value(request) %>");
		var privateKey = CryptoJS.enc.Base64.parse(sessionStorage.getItem('<%= WebCommonStaticFinalVars.SESSIONSTORAGE_KEY_NAME_OF_PRIVATEKEY %>'));
	
		var messageResultResDecryptedObj = CryptoJS.AES.decrypt("<%= getCipheredBase64String(request, messageResultResJsonString) %>", privateKey, { mode: CryptoJS.mode.CBC, padding: CryptoJS.pad.Pkcs7, iv: pageIV });
		
		var messageResultResJsonString = messageResultResDecryptedObj.toString(CryptoJS.enc.Utf8);
		
		var messageResultResJsonObj = JSON.parse(messageResultResJsonString);
		
		var resultView = document.getElementById('resultView');
		
		if (messageResultResJsonObj.isSuccess) {
			resultView.setAttribute("class", "alert alert-success");
			resultView.innerHTML = "<strong>Success!</strong> " + messageResultResJsonObj.resultMessage;
		} else {
			resultView.setAttribute("class", "alert alert-warning fade in");
			resultView.innerHTML = "<strong>Warning!</strong> " + messageResultResJsonObj.resultMessage;
		}
	
		<!-- 보안을 위해서 회원가입시 생성한 비밀키와 세션키 덮어쓰기 -->
		var privateKey = CryptoJS.lib.WordArray.random(<%= WebCommonStaticFinalVars.WEBSITE_PRIVATEKEY_SIZE %>);		
		var rsa = new RSAKey();
		rsa.setPublic("<%= getModulusHexString(request) %>", "10001");
		var sessionKeyHex = rsa.encrypt(CryptoJS.enc.Base64.stringify(privateKey));		
		var sessionkeyBase64 = CryptoJS.enc.Base64.stringify(CryptoJS.enc.Hex.parse(sessionKeyHex));
	
		sessionStorage.setItem('<%= WebCommonStaticFinalVars.SESSIONSTORAGE_KEY_NAME_OF_PRIVATEKEY %>', CryptoJS.enc.Base64.stringify(privateKey));
	}

	window.onload = init;
</script>
</head>
<body>
<%= adminSiteMenuManger.getSiteNavbarString(getGroupRequestURL(request), isAdminLogin(request)) %>
	<div class="container-fluid">
		<h3>일반 회원 가입 결과</h3>	
		<div id="resultView">
		</div>
	</div>
</body>
</html>
