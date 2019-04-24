<%@page import="kr.pe.codda.weblib.common.AccessedUserInformation"%><%
%><%@page import="kr.pe.codda.weblib.htmlstring.StringEscapeActorUtil"%><%
%><%@ page import="kr.pe.codda.weblib.common.WebCommonStaticFinalVars" %><%	
%><%@ page extends="kr.pe.codda.weblib.jdf.AbstractUserJSP" language="java" session="true" autoFlush="true" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %><%AccessedUserInformation accessedUserformation = getAccessedUserInformationFromSession(request);%><!DOCTYPE html>
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
<script type="text/javascript" src="/js/cryptoJS/rollups/sha256.js"></script>
<script type="text/javascript" src="/js/cryptoJS/rollups/aes.js"></script>
<script type="text/javascript" src="/js/cryptoJS/components/core-min.js"></script>
<script type="text/javascript" src="/js/cryptoJS/components/cipher-core-min.js"></script>

<script src="/js/common.js"></script>
<script type='text/javascript'>	
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

	function goMemberPasswordChange() {
		var f = document.frm;
		
		try {
			checkValidPwd('변경 전', f.oldPwd.value);
		} catch(err) {
			alert(err);
			f.pwd.focus();
			return;
		}
		
		try {
			checkValidPwd('변경 후', f.newPwd.value);
		} catch(err) {
			alert(err);
			f.pwd.focus();
			return;
		}
		
		try {
			checkValidPwdConfirm('변경 후', f.newPwd.value, f.newPwdConfirm.value);
		} catch(err) {
			alert(err);
			f.pwd.focus();
			return;
		}
		
		var symmetricKeyObj = CryptoJS.<%= WebCommonStaticFinalVars.WEBSITE_JAVASCRIPT_SYMMETRIC_KEY_ALGORITHM_NAME %>;		
		var privateKey = getPrivateKeyFromSessionStorage();
		var iv = buildIV();
		
		var g = document.gofrm;

		g.<%= WebCommonStaticFinalVars.PARAMETER_KEY_NAME_OF_SESSION_KEY %>.value = getSessionkeyBase64FromSessionStorage();
		g.<%= WebCommonStaticFinalVars.PARAMETER_KEY_NAME_OF_SESSION_KEY_IV %>.value = CryptoJS.enc.Base64.stringify(iv);
		
		g.oldPwd.value = symmetricKeyObj.encrypt(f.oldPwd.value, privateKey, { mode: CryptoJS.mode.CBC, padding: CryptoJS.pad.Pkcs7, iv: iv });
		g.newPwd.value = symmetricKeyObj.encrypt(f.newPwd.value, privateKey, { mode: CryptoJS.mode.CBC, padding: CryptoJS.pad.Pkcs7, iv: iv });
		
		f.oldPwd.value = '';
		f.newPwd.value = '';
		f.newPwdConfirm.value = '';
		
		g.submit();
		
	}
	
	function callBackForMemberPasswordChangeProcess() {
		var userNameDiv = document.getElementById("userName");
		var resultMessageDiv = document.getElementById("resultMessage");
		
		resultMessageDiv.setAttribute("class", "alert alert-success");
		resultMessageDiv.innerHTML = "<strong>Success!</strong> " + userNameDiv.innerText + " 님 비밀 번호 변경 처리가 완료되었습니다";
		
		alert(resultMessageDiv.innerText);
		
		document.location.href = "/jsp/member/logout.jsp";
	}
	
	function callBackForErrorMessage(errorMessage) {		
		var resultMessageDiv = document.getElementById("resultMessage");
		
		resultMessageDiv.setAttribute("class", "alert alert-warning");
		resultMessageDiv.innerHTML = "<strong>Warning!</strong> " + errorMessage;
		
		alert(resultMessageDiv.innerText);
	}

	function clickHiddenFrameButton(thisObj) {		
		var hiddenFrame = document.getElementById("hiddenFrame");
		
		if (hiddenFrame.style.display == 'none') {
			thisObj.innerText = "Hide Hidden Frame";
			hiddenFrame.style.display = "block";			
		} else {
			thisObj.innerText = "Show Hidden Frame";
			hiddenFrame.style.display = "none";
		}
	}
	
	function init() {
		if(typeof(sessionStorage) == "undefined") {
		    alert("Sorry! No HTML5 sessionStorage support..");
		    document.location.href = "/";
		    return;
		}
	}
	
	window.onload = init;
</script>
</head>
<body>
<div class="header">
	<div class="container">
<%= getMenuNavbarString(request) %>
	</div>
</div>
<div class="content">
	<div class="container">
		<div class="panel panel-default">
			<div class="panel-heading"><h4><%= StringEscapeActorUtil.replace(accessedUserformation.getUserName(),
					StringEscapeActorUtil.STRING_REPLACEMENT_ACTOR_TYPE.ESCAPEHTML4) %> 님의 비밀 번호 변경</h4></div>
			<div class="panel-body">
				<div id="resultMessage"></div>
				<div class="btn-group">
					<button type="button" class="btn btn-primary btn-sm" onClick="clickHiddenFrameButton(this);">Show Hidden Frame</button>			
				</div>
				<div id="userName" style="display:none"><%= StringEscapeActorUtil.replace(accessedUserformation.getUserName(), 
					StringEscapeActorUtil.STRING_REPLACEMENT_ACTOR_TYPE.ESCAPEHTML4) %></div>
				<form method="post" name="gofrm" target="hiddenFrame" action="/servlet/MemberPasswordChangeProcess">
					<input type="hidden" name="<%= WebCommonStaticFinalVars.PARAMETER_KEY_NAME_OF_SESSION_KEY %>" />
					<input type="hidden" name="<%= WebCommonStaticFinalVars.PARAMETER_KEY_NAME_OF_SESSION_KEY_IV %>" />					
					<input type="hidden" name="oldPwd" />
					<input type="hidden" name="newPwd" />
				</form>
				<br>
				<form method="post" name="frm" onsubmit="goMemberPasswordChange(); return false;">
					<div class="form-group">					
						<label for="oldPwd">변경 전 비빌번호:</label>
						<input type="password" class="form-control" id="oldPwd" placeholder="Enter old password" name="oldPwd">
						<label for="newPwd">변경 후 비빌번호:</label>
						<input type="password" class="form-control" id="newPwd" placeholder="Enter new password" name="newPwd">
						<label for="newPwdConfirm">변경 후 비빌번호 확인:</label>
						<input type="password" class="form-control" id="newPwdConfirm" placeholder="Enter new password confirm" name="newPwdConfirm">
					</div>
					<button type="submit" class="btn btn-default">확인</button>		
				</form>
				<iframe id="hiddenFrame" name="hiddenFrame" style="display:none;"></iframe>
			</div>
		</div>
	</div>
</div>
</body>
</html>