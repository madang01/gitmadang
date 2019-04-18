<%@ page import="kr.pe.codda.weblib.common.WebCommonStaticFinalVars" %><%
%><%@ page extends="kr.pe.codda.weblib.jdf.AbstractAdminJSP" language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%><%
%><!DOCTYPE html>
<html lang="ko">
<head>
<meta charset="UTF-8">
<meta http-equiv="X-UA-Compatible" content="IE=edge">
<meta name="viewport" content="width=device-width, initial-scale=1">
<title><%= WebCommonStaticFinalVars.ADMIN_WEBSITE_TITLE %></title>
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

	function goMemberRegister() {
		var f = document.frm;
		
		try {
			checkValidUserID('회원', f.id.value);
		} catch(err) {
			alert(err);
			f.id.focus();
			return;
		}
		
		try {
			checkValidPwd('회원', f.pwd.value);
		} catch(err) {
			alert(err);
			f.pwd.focus();
			return;
		}
		
		try {
			checkValidPwdConfirm('회원', f.pwd.value, f.pwdConfirm.value);
		} catch(err) {
			alert(err);
			f.pwd.focus();
			return;
		}
	
		if (f.nickname.value == '') {
			alert("별명을 넣어주세요.");
			f.nickname.focus();
			return;
		}
	
				
		if (f.email.value == '') {
			alert("이메일을 넣어주세요.");
			f.pwdHint.focus();
			return;
		}		
		
		
		if (f.captchaAnswer.value == '') {
			alert("Captcha 답변을 넣어주세요.");
			f.captchaAnswer.focus();
			return;
		}					
		
		var symmetricKeyObj = CryptoJS.<%= WebCommonStaticFinalVars.WEBSITE_JAVASCRIPT_SYMMETRIC_KEY_ALGORITHM_NAME %>;
		var privateKey = buildPrivateKey();
		var privateKeyBase64 = CryptoJS.enc.Base64.stringify(privateKey);
		var iv = buildIV();
		
		var rsa = new RSAKey();
		rsa.setPublic("<%= getModulusHexString(request) %>", "10001");
		var sessionKeyHex = rsa.encrypt(CryptoJS.enc.Base64.stringify(privateKey));
		var sessionKey = CryptoJS.enc.Hex.parse(sessionKeyHex);
		
		var g = document.gofrm;
		
		g.<%= WebCommonStaticFinalVars.PARAMETER_KEY_NAME_OF_SESSION_KEY %>.value = CryptoJS.enc.Base64.stringify(sessionKey);
		g.<%= WebCommonStaticFinalVars.PARAMETER_KEY_NAME_OF_SESSION_KEY_IV %>.value = CryptoJS.enc.Base64.stringify(iv);
		
		g.userID.value = symmetricKeyObj.encrypt(f.userID.value, privateKey, { mode: CryptoJS.mode.CBC, padding: CryptoJS.pad.Pkcs7, iv: iv });
		g.pwd.value = symmetricKeyObj.encrypt(f.pwd.value, privateKey, { mode: CryptoJS.mode.CBC, padding: CryptoJS.pad.Pkcs7, iv: iv });
		g.nickname.value = symmetricKeyObj.encrypt(f.nickname.value, privateKey, { mode: CryptoJS.mode.CBC, padding: CryptoJS.pad.Pkcs7, iv: iv });
		g.email.value = symmetricKeyObj.encrypt(f.email.value, privateKey, { mode: CryptoJS.mode.CBC, padding: CryptoJS.pad.Pkcs7, iv: iv });
	
		g.captchaAnswer.value = symmetricKeyObj.encrypt(f.captchaAnswer.value, privateKey, { mode: CryptoJS.mode.CBC, padding: CryptoJS.pad.Pkcs7, iv: iv });
		
		f.pwd.value = '';
		f.pwdConfirm.value = '';
		putNewPrivateKeyToSessionStorage();
		reloadCaptcha();
		
		g.submit();
		return;
	}
	
	function callBackForErrorMessage(errorMessage) {
		var resultMessageDiv = document.getElementById("resultMessage");
		
		resultMessageDiv.setAttribute("class", "alert alert-warning");
		resultMessageDiv.innerHTML = "<strong>Warning!</strong> " + errorMessage;
		
		alert(resultMessageDiv.innerText);
	}
	
	function callBackForMembershipProcess() {
		var resultMessageDiv = document.getElementById("resultMessage");
		
		resultMessageDiv.setAttribute("class", "alert alert-success");
		resultMessageDiv.innerHTML = "<strong>Success!</strong> "+document.frm.userID.value+" 님 회원 등록이 성공하였습니다.";
		
		alert(resultMessageDiv.innerText);
		
		document.location.href = "/servlet/AdminLoginInput?userID="+document.frm.userID.value;
	}
	
	function reloadCaptcha() {
		var img = document.getElementById("captchaImage");
		img.src="/servlet/stickyImg?" + new Date().getTime();
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
		
		var f = document.frm;
		f.userID.value =  "test00";
		f.pwd.value =  "test1234$";
		f.pwdConfirm.value =  f.pwd.value;
		f.nickname.value = "별명00";
		f.email.value = "test00@codda.pe.kr";
	}

	window.onload = init;
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
				<div class="panel-heading"><h4>Codda HowTo</h4></div>
				<div class="panel-body">
					<form method="post" name="gofrm" action="/servlet/MemberRegisterProcess" target="hiddenFrame">
						<input type="hidden" name="<%= WebCommonStaticFinalVars.PARAMETER_KEY_NAME_OF_SESSION_KEY %>" />
						<input type="hidden" name="<%= WebCommonStaticFinalVars.PARAMETER_KEY_NAME_OF_SESSION_KEY_IV %>" />
						<input type="hidden" name="userID" />
						<input type="hidden" name="pwd" />
						<input type="hidden" name="nickname" />
						<input type="hidden" name="email" />
						<input type="hidden" name="captchaAnswer" />
					</form>
					<div id="resultMessage"></div>
					<div class="btn-group">
						<button type="button" class="btn btn-primary btn-sm" onClick="clickHiddenFrameButton(this);">Show Hidden Frame</button>			
					</div>
					<form method="post" name="frm" onsubmit="goMemberRegister(); return false;">
						<div class="form-group">
							<label for="userID">아이디</label>
							<input type="text" id="userID" class="form-control" name="userID" maxlength="15">
						</div>
						<div class="form-group">
							<label for="pwd">비빌번호</label>
							<input type="password" id="pwd" class="form-control" name="pwd" maxlength="15">
						</div>
						<div class="form-group">
							<label for="pwdconfirm">비빌번호 확인</label>
							<input type="password" id="pwdConfirm" class="form-control" name="pwdConfirm" maxlength="15">
						</div>
						<div class="form-group">
							<label for="nickname">별명</label>
							<input type="text" id="nickname" class="form-control" name="nickname" maxlength="20">
						</div>			
						<div class="form-group">
							<label for="email">이메일</label>
							<input type="email" id="email" class="form-control" name="pwdHint" maxlength="30">
						</div>						
						<div class="form-group">
							<label for="captchaImage">Captcha 이미지</label>
							<img id="captchaImage" class="img-thumbnail" src="/servlet/stickyImg" alt="Captcha Image" />
							<a href="#" onClick="reloadCaptcha()" style="curso:pointer"><span class="glyphicon glyphicon-refresh"></span></a>
						</div>			
						<div class="form-group">
							<label for="answer">Captcha 답변</label>
							<input type="text" id="captchaAnswer" class="form-control" name="captchaAnswer" maxlength="20" />
						</div>
						<button type="submit" class="btn btn-default">가입</button>
					</form>
					<iframe id="hiddenFrame" name="hiddenFrame" style="display:none;"></iframe>
				</div>
			</div>
		</div>
	</div>	
</body>
</html>

