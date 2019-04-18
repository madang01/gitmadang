
<%@page import="kr.pe.codda.impl.message.AccountSearchProcessRes.AccountSearchProcessRes"%>
<%@page import="kr.pe.codda.weblib.htmlstring.StringEscapeActorUtil"%>
<%@page import="kr.pe.codda.weblib.common.AccountSearchType"%><%
%><%@page import="java.util.Enumeration"%><%
%><%@ page import="kr.pe.codda.weblib.common.WebCommonStaticFinalVars" %><%
%><%@ page extends="kr.pe.codda.weblib.jdf.AbstractUserJSP" language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%><%
	AccountSearchType accountSearchType = (AccountSearchType)request.getAttribute("accountSearchType");
	AccountSearchProcessRes accountSearchProcessRes = (AccountSearchProcessRes)request.getAttribute("accountSearchProcessRes");
	
	if (null == accountSearchType) {
		accountSearchType = AccountSearchType.PASSWORD;
	}
	
	if (null == accountSearchProcessRes) {
		accountSearchProcessRes = new AccountSearchProcessRes();
		accountSearchProcessRes.setUserID("test00");
		accountSearchProcessRes.setNickname("테스터00");
		accountSearchProcessRes.setAccountSearchType(accountSearchType.getValue());
		
	}
%>
<!DOCTYPE html>
<html lang="ko">
<head>
<meta charset="UTF-8">
<meta http-equiv="X-UA-Compatible" content="IE=edge">
<meta name="viewport" content="width=device-width, initial-scale=1">
<title><%= WebCommonStaticFinalVars.USER_WEBSITE_TITLE %></title>
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
<script type="text/javascript" src="/js/cryptoJS/components/core-min.js"></script>
<script type="text/javascript" src="/js/cryptoJS/components/cipher-core-min.js"></script>

<script src="/js/common.js"></script>

<script type="text/javascript">
<!--
	function buildPrivateKey() {
		var privateKey = CryptoJS.lib.WordArray.random(<%= WebCommonStaticFinalVars.WEBSITE_PRIVATEKEY_SIZE %>);	
		return privateKey;
	}
	
	function putNewPrivateKeyToSessionStorage() {
		var newPrivateKey = buildPrivateKey();
		var newPrivateKeyBase64 = CryptoJS.enc.Base64.stringify(newPrivateKey);
		
		sessionStorage.setItem('<%= WebCommonStaticFinalVars.SESSIONSTORAGE_KEY_NAME_OF_PRIVATEKEY %>', newPrivateKeyBase64);
		
		return newPrivateKeyBase64;
	}
	
	function getPrivateKeyFromSessionStorage() {
		var privateKeyBase64 = sessionStorage.getItem('<%= WebCommonStaticFinalVars.SESSIONSTORAGE_KEY_NAME_OF_PRIVATEKEY %>');
		
		if (null == privateKeyBase64) {
			privateKeyBase64 = putNewPrivateKeyToSessionStorage();
		}
		
		var privateKey = null;
		try {
			privateKey = CryptoJS.enc.Base64.parse(privateKeyBase64);
		} catch(err) {
			console.log(err);
			throw err;
		}
		
		return privateKey;
	}
	
	function getSessionkeyBase64FromSessionStorage() {
		var privateKeyBase64 = sessionStorage.getItem('<%= WebCommonStaticFinalVars.SESSIONSTORAGE_KEY_NAME_OF_PRIVATEKEY %>');
		
		if (null == privateKeyBase64) {
			privateKeyBase64 = putNewPrivateKeyToSessionStorage();
		}		
		
		var rsa = new RSAKey();
		rsa.setPublic("<%= getModulusHexString(request) %>", "10001");
			
		var sessionKeyHex = rsa.encrypt(privateKeyBase64);
		var sessionKey = CryptoJS.enc.Hex.parse(sessionKeyHex);
		return CryptoJS.enc.Base64.stringify(sessionKey);
	}
	
	function buildIV() {
		var iv = CryptoJS.lib.WordArray.random(<%= WebCommonStaticFinalVars.WEBSITE_IV_SIZE %>);
		return iv;
	}
	
	
	function init() {
		if(typeof(sessionStorage) == "undefined") {
		    alert("Sorry! No HTML5 sessionStorage support..");
		    document.location.href = "/";
		    return;
		}		
	}
	
	window.onload = init;
//-->
</script>
</head>
<body>
	<div class=header>
		<div class="container">
		<%= getMenuNavbarString(request) %>
		</div>
	</div>
	<div class="content">
		<div class="container">
			<div class="panel panel-default">
				<div class="panel-heading"><h4><%= accountSearchType.getName() %> 찾기</h4></div>
				<div class="panel-body">
					<div class="alert alert-success">
						<strong>Success!</strong>&nbsp;&nbsp;아이디 <strong><%= accountSearchProcessRes.getUserID() %></strong> 이신 
					<strong><%= toEscapeHtml4(accountSearchProcessRes.getNickname()) %></strong> 님의 <%= accountSearchType.getName() %> 찾기가 성공하였습니다.<br>
					아래 <strong>확인</strong> 버튼을 클릭하여 로그인 해 주시기 바랍니다
					</div>					
					<a href="/servlet/MemberLoginInput?userID=<%= accountSearchProcessRes.getUserID() %>" class="btn btn-primary">확인</a>
				</div>
			</div>
		</div>
	</div>
</body>
</html>