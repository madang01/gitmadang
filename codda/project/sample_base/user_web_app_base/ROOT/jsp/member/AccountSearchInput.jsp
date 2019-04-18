<%@page import="kr.pe.codda.weblib.common.AccountSearchType"%><%
%><%@ page import="kr.pe.codda.weblib.common.WebCommonStaticFinalVars" %><%
%><%@ page extends="kr.pe.codda.weblib.jdf.AbstractUserJSP" language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%><% 
	AccountSearchType accountSearchType = (AccountSearchType)request.getAttribute("accountSearchType");
	String email = (String)request.getAttribute("email");
	
	if (null == accountSearchType) {
		accountSearchType = AccountSearchType.PASSWORD;
	}
	
	if (null == email) {
		email = "sample@codda.pe.kr";
	}%>
<!DOCTYPE html>
<html lang="ko">
<head>
<meta charset="UTF-8">
<meta http-equiv="X-UA-Compatible" content="IE=edge">
<meta name="viewport" content="width=device-width, initial-scale=1">
<title><%=WebCommonStaticFinalVars.USER_WEBSITE_TITLE%></title>
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
		var privateKeyBase64 = sessionStorage.getItem('<%=WebCommonStaticFinalVars.SESSIONSTORAGE_KEY_NAME_OF_PRIVATEKEY%>');
		
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
		var privateKeyBase64 = sessionStorage.getItem('<%=WebCommonStaticFinalVars.SESSIONSTORAGE_KEY_NAME_OF_PRIVATEKEY%>');
		
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
	
	function goIDPasswordSearchProcess() {				
		var f = document.frm;
		
		if (f.secretAuthenticationValue.value == '') {
			alert("이메일로 받은 비밀 값을 넣어주세요");
			f.secretAuthenticationValue.focus();
		}	
		
		if (f.pwd != undefined) {
			if (f.pwd != undefined) {
				try {
					checkValidPwd('새로운', f.pwd.value);
				} catch(err) {
					alert(err);
					f.pwd.focus();
					return;
				}
				
				try {
					checkValidPwdConfirm('새로운', f.pwd.value, f.pwdConfirm.value);
				} catch(err) {
					alert(err);
					f.pwd.focus();
					return;
				}
			}
		}
			
		
		var g = document.gofrm;		
		
		var symmetricKeyObj = CryptoJS.<%= WebCommonStaticFinalVars.WEBSITE_JAVASCRIPT_SYMMETRIC_KEY_ALGORITHM_NAME %>;
		var privateKey = getPrivateKeyFromSessionStorage();		
		var iv = buildIV();		
		
		g.<%= WebCommonStaticFinalVars.PARAMETER_KEY_NAME_OF_SESSION_KEY %>.value = getSessionkeyBase64FromSessionStorage();
		g.<%= WebCommonStaticFinalVars.PARAMETER_KEY_NAME_OF_SESSION_KEY_IV %>.value = CryptoJS.enc.Base64.stringify(iv);
		
		var emailDiv = document.getElementById("email");
		g.email.value = symmetricKeyObj.encrypt(emailDiv.innerText, privateKey, { mode: CryptoJS.mode.CBC, padding: CryptoJS.pad.Pkcs7, iv: iv });
		g.secretAuthenticationValue.value = symmetricKeyObj.encrypt(f.secretAuthenticationValue.value, privateKey, { mode: CryptoJS.mode.CBC, padding: CryptoJS.pad.Pkcs7, iv: iv });
		
		if (f.pwd != undefined) {
			g.pwd.value = symmetricKeyObj.encrypt(f.pwd.value, privateKey, { mode: CryptoJS.mode.CBC, padding: CryptoJS.pad.Pkcs7, iv: iv });
		}
		
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
		<%=getMenuNavbarString(request)%>
		</div>
	</div>
	<div class="content">
		<div class="container">
			<div class="panel panel-default">
				<div class="panel-heading"><h4><%=accountSearchType.getName()%> 찾기</h4></div>
				<div class="panel-body">					
					<form method="post" name="gofrm" action="/servlet/AccountSearchProcess">
						<input type="hidden" name="<%=WebCommonStaticFinalVars.PARAMETER_KEY_NAME_OF_SESSION_KEY%>" />
						<input type="hidden" name="<%=WebCommonStaticFinalVars.PARAMETER_KEY_NAME_OF_SESSION_KEY_IV%>" />
						<input type="hidden" name="accountSearchType" value="<%=accountSearchType.getValue()%>" />
						<input type="hidden" name="email" />
						<input type="hidden" name="secretAuthenticationValue" />
						<input type="hidden" name="pwd" />
					</form>
					
					<form method="post" name="frm" onsubmit="goIDPasswordSearchProcess(); return false;">
						<div class="form-group">
							<div style="display:none;" id="email"><%= toEscapeHtml4(email) %></div>
							<table class="table">
								<tbody>									
									<tr><td>이메일: <%= toEscapeHtml4(email) %></td></tr>
									<tr><td><label for="secretAuthenticationValue">비밀 값: </label>
							<input type="text" class="form-control" id="secretAuthenticationValue" placeholder="이메일로 받은 비밀값을 넣어주세요" name="secretAuthenticationValue"></td></tr><%
								if (AccountSearchType.PASSWORD.equals(accountSearchType)) {
							%>
									<tr><td><label for="pwd">비밀번호: </label>
							<input type="password" class="form-control" id="pwd" placeholder="Enter password" name="pwd"></td></tr>
									<tr><td><label for="pwd">비밀번호 확인: </label>
							<input type="password" class="form-control" id="pwdConfirm" placeholder="Enter password confirm" name="pwdConfirm"></td></tr><%
	}
%>	
								</tbody>
							</table>						
						</div>
						<button type="submit" class="btn btn-default">Submit</button>		
					</form>
				</div>
			</div>
		</div>
	</div>
</body>
</html>