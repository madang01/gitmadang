<%@page import="org.apache.commons.text.StringEscapeUtils"%><%
%><%@page import="java.util.Enumeration"%><%
%><%@ page import="kr.pe.codda.weblib.common.WebCommonStaticFinalVars" %><%
%><%@ page extends="kr.pe.codda.weblib.jdf.AbstractUserJSP" language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%><%

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
	
	function goIDPasswordSearchReady() {				
		var f = document.frm;
		
		if (f.email.value == '') {
			alert("이메일을 넣어주세요");
			f.email.focus();
		}		
		
		var g = document.gofrm;
		
		if (f.accountSearchType[0].checked) {
			g.accountSearchType.value = f.accountSearchType[0].value; 
		} else {
			g.accountSearchType.value = f.accountSearchType[1].value;
		}		
		
		var symmetricKeyObj = CryptoJS.<%= WebCommonStaticFinalVars.WEBSITE_JAVASCRIPT_SYMMETRIC_KEY_ALGORITHM_NAME %>;
		var privateKey = getPrivateKeyFromSessionStorage();		
		var iv = buildIV();		
		
		g.<%= WebCommonStaticFinalVars.PARAMETER_KEY_NAME_OF_SESSION_KEY %>.value = getSessionkeyBase64FromSessionStorage();
		g.<%= WebCommonStaticFinalVars.PARAMETER_KEY_NAME_OF_SESSION_KEY_IV %>.value = CryptoJS.enc.Base64.stringify(iv);
	
		g.email.value = symmetricKeyObj.encrypt(f.email.value, privateKey, { mode: CryptoJS.mode.CBC, padding: CryptoJS.pad.Pkcs7, iv: iv });
		g.submit();
		return;
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
				<div class="panel-heading"><h4>계정 찾기</h4></div>
				<div class="panel-body">					
					<form method="post" name="gofrm" action="/servlet/AccountSearchInput">
						<input type="hidden" name="<%= WebCommonStaticFinalVars.PARAMETER_KEY_NAME_OF_SESSION_KEY %>" />
						<input type="hidden" name="<%= WebCommonStaticFinalVars.PARAMETER_KEY_NAME_OF_SESSION_KEY_IV %>" />
						<input type="hidden" name="accountSearchType" />
						<input type="hidden" name="email" />
					</form>
					<form class="form-horizontal" method="post" name="frm" onsubmit="goIDPasswordSearchReady(); return false;">
						<div class="form-group">
							<label class="control-label col-sm-2" for="accountSearchType">계정 찾기 대상 :</label>
							<div class="col-sm-10">
								<label class="radio-inline"><input type="radio" name="accountSearchType" checked value="0">아이디</label><label class="radio-inline"><input type="radio" name="accountSearchType" value="1">비밀번호</label>
							</div>
						</div>
						<div class="form-group">
							<label class="control-label col-sm-2" for="email">이메일 :</label>
							<div class="col-sm-10">
								<input type="text" class="form-control" id="email" placeholder="Enter email" name="email">
							</div>
						</div>
						<div class="form-group"> 
    						<div class="col-sm-offset-2 col-sm-10">
								<button type="submit" class="btn btn-default">Submit</button>
							</div>
						</div>
					</form>
				</div>
			</div>
		</div>
	</div>
</body>
</html>