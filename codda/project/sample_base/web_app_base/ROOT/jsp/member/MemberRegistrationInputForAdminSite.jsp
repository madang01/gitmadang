<%@ page import="kr.pe.codda.weblib.common.WebCommonStaticFinalVars" %><%
%><%@ page extends="kr.pe.codda.weblib.jdf.AbstractAdminJSP" language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%><%
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
<script type="text/javascript" src="/js/cryptoJS/rollups/aes.js"></script>
<script type="text/javascript" src="/js/cryptoJS/components/core-min.js"></script>
<script type="text/javascript" src="/js/cryptoJS/components/cipher-core-min.js"></script>
<script type='text/javascript'>
	function submitGoFormIfValid() {
		
		var f = document.frm;
		var g = document.gofrm;
		
		var regexID = /^[A-Za-z][A-Za-z0-9]{3,14}$/;
		var regexPwd = /^[A-Za-z0-9\`~!@\#$%<>\^&*\(\)\-=+_\'\[\]\{\}\\\|\:\;\"<>\?,\.\/]{8,15}$/;
		var regexPwdAlpha = /.*[A-Za-z]{1,}.*/;
		var regexPwdDigit = /.*[0-9]{1,}.*/;
		var regexPwdPunct = /.*[\`~!@\#$%<>\^&*\(\)\-=+_\'\[\]\{\}\\\|\:\;\"<>\?,\.\/]{1,}.*/;
	
		if(typeof(sessionStorage) == "undefined") {
			alert("Sorry! No HTML5 sessionStorage support..");
			return;
		}
	
		
		if (f.userID.value == '') {
			alert("아이디를 넣어주세요.");
			f.userID.focus();
			return;
		}
		
		if (!regexID.test(f.userID.value)) {
			alert("아이디는 첫 문자가 영문자 그리고 영문과 숫자로만 최소 4자, 최대 15자로 구성됩니다. 다시 입력해 주세요.");
			f.userID.value = '';
			f.userID.focus();
			return;
		}
		
		if (f.pwd.value == '') {
			alert("비밀번호를 넣어주세요.");
			f.pwd.focus();
			return;
		}
		
		/*
		if (!regexPwd.test(f.pwd.value)) {
			alert("비밀번호는 영문, 숫자 그리고 특수문자 조합으로 최소 8자, 최대 15자로 구성됩니다. 다시 입력해 주세요.");
			f.pwd.value = '';
			f.pwd.focus();
			return;
		}
	
		if (!regexPwdAlpha.test(f.pwd.value)) {
			alert("비밀번호는 최소 영문 1자가 포함되어야 합니다. 다시 입력해 주세요.");
			f.pwd.value = '';
			f.pwd.focus();
			return;
		}
	
		if (!regexPwdDigit.test(f.pwd.value)) {
			alert("비밀번호는 최소 숫자 1자가 포함되어야 합니다. 다시 입력해 주세요.");
			f.pwd.value = '';
			f.pwd.focus();
			return;
		}
	
		if (!regexPwdPunct.test(f.pwd.value)) {
			alert("비밀번호는 최소 특수문자 1자가 포함되어야 합니다. 다시 입력해 주세요.");
			f.pwd.value = '';
			f.pwd.focus();
			return;
		}
	*/
		
		if (f.pwdconfirm.value == '') {
			alert("비밀번호 확인을 넣어주세요.");
			f.pwdconfirm.focus();
			return;
		}
		
		if (f.pwdconfirm.value != f.pwd.value) {
			alert("비밀번호가 일치하지 않습니다. 다시 넣어주세요.");
			f.pwd.value='';	    
			f.pwdconfirm.value='';
			f.pwd.focus();	    
			return;
		}
		
		
	
		if (f.nickname.value == '') {
			alert("별명을 넣어주세요.");
			f.nickname.focus();
			return;
		}
	
				
		if (f.pwdHint.value == '') {
			alert("비밀번호 분실시 질문을 넣어주세요.");
			f.pwdHint.focus();
			return;
		}		
		
		if (f.pwdAnswer.value == '') {
			alert("비밀번호 분실시 답변을 넣어주세요.");
			f.pwdAnswer.focus();
			return;
		}		
		
		if (f.answer.value == '') {
			alert("Captcha 답변을 넣어주세요.");
			f.answer.focus();
			return;
		}					
		
		// FIXME!
		var privateKey = CryptoJS.lib.WordArray.random(<%= WebCommonStaticFinalVars.WEBSITE_PRIVATEKEY_SIZE %>);		
		// var privateKey = CryptoJS.enc.Hex.parse("cf5553bdbe3a6240a0a89fdd9be4e64c");
						
		// alert(privateKey.length);				
		
		var rsa = new RSAKey();
		rsa.setPublic("<%= getModulusHexString(request) %>", "10001");
		
		// FIXME!
		var sessionKeyHex = rsa.encrypt(CryptoJS.enc.Base64.stringify(privateKey));		
		var sessionkeyBase64 = CryptoJS.enc.Base64.stringify(CryptoJS.enc.Hex.parse(sessionKeyHex));				
	
		var iv = CryptoJS.lib.WordArray.random(<%= WebCommonStaticFinalVars.WEBSITE_IV_SIZE %>);
		// alert(g.ivBase64.value);				
		var symmetricKeyObj = CryptoJS.<%= WebCommonStaticFinalVars.WEBSITE_JAVASCRIPT_SYMMETRIC_KEY_ALGORITHM_NAME %>;
	
		g.<%= WebCommonStaticFinalVars.PARAMETER_KEY_NAME_OF_SESSION_KEY %>.value 
			= sessionkeyBase64;
		g.<%= WebCommonStaticFinalVars.PARAMETER_KEY_NAME_OF_SESSION_KEY_IV %>.value 
			= CryptoJS.enc.Base64.stringify(iv);
		
		g.userID.value = symmetricKeyObj.encrypt(f.userID.value, privateKey, { mode: CryptoJS.mode.CBC, padding: CryptoJS.pad.Pkcs7, iv: iv });
		g.pwd.value = symmetricKeyObj.encrypt(f.pwd.value, privateKey, { mode: CryptoJS.mode.CBC, padding: CryptoJS.pad.Pkcs7, iv: iv });
		g.nickname.value = symmetricKeyObj.encrypt(f.nickname.value, privateKey, { mode: CryptoJS.mode.CBC, padding: CryptoJS.pad.Pkcs7, iv: iv });
		g.pwdHint.value = symmetricKeyObj.encrypt(f.pwdHint.value, privateKey, { mode: CryptoJS.mode.CBC, padding: CryptoJS.pad.Pkcs7, iv: iv });
		g.pwdAnswer.value = symmetricKeyObj.encrypt(f.pwdAnswer.value, privateKey, { mode: CryptoJS.mode.CBC, padding: CryptoJS.pad.Pkcs7, iv: iv });
	
		g.answer.value = symmetricKeyObj.encrypt(f.answer.value, privateKey, { mode: CryptoJS.mode.CBC, padding: CryptoJS.pad.Pkcs7, iv: iv });
		
		g.submit();
		
		return;
	}
	
	function adminMemberRegistrationFailureCallBack(errorMessage) {
		var resultMessageView = document.getElementById("resultMessageView");
		
		resultMessageView.setAttribute("class", "alert alert-warning");
		resultMessageView.innerHTML = "<strong>Warning!</strong> " + errorMessage;
	}
	
	function adminMemberRegistrationOKCallBack() {
		var resultMessageView = document.getElementById("resultMessageView");
		
		resultMessageView.setAttribute("class", "alert alert-success");
		resultMessageView.innerHTML = "<strong>Success!</strong> "+document.frm.userID.value+" 님 회원 등록이 성공하였습니다.";
	}
	
	function reloadCaptcha() {
		var img = document.getElementById("captchaImage");
		img.src="/servlet/stickyImg?" + new Date().getTime();
	}

	function init() {
		var f = document.frm;
		f.userID.value =  "test00";
		f.pwd.value =  "test1234$";
		f.pwdconfirm.value =  f.pwd.value;
		f.nickname.value = "별명00";
		f.pwdHint.value = "질문";
		f.pwdAnswer.value = "답변";
	}

	window.onload = init;
</script>
</head>
<body>
<%= getSiteNavbarString(request) %>
<form method="post" name="gofrm" action="/servlet/MemberRegistration" target="hiddenFrame">
	<input type="hidden" name="<%= WebCommonStaticFinalVars.PARAMETER_KEY_NAME_OF_REQUEST_TYPE %>" value="proc" />
	<input type="hidden" name="<%= WebCommonStaticFinalVars.PARAMETER_KEY_NAME_OF_SESSION_KEY %>" />
	<input type="hidden" name="<%= WebCommonStaticFinalVars.PARAMETER_KEY_NAME_OF_SESSION_KEY_IV %>" />
	<input type="hidden" name="userID" />
	<input type="hidden" name="pwd" />
	<input type="hidden" name="nickname" />
	<input type="hidden" name="pwdHint" />
	<input type="hidden" name="pwdAnswer" />
	<input type="hidden" name="answer" />
</form>
	<div class="container-fluid">
		<h3>일반 회원 가입</h3>
		<div id="resultMessageView"></div>
		<form method="post" name="frm" onsubmit="submitGoFormIfValid(); return false;">
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
				<input type="password" id="pwdconfirm" class="form-control" name="pwdconfirm" maxlength="15">
			</div>
			<div class="form-group">
				<label for="nickname">별명</label>
				<input type="text" id="nickname" class="form-control" name="nickname" maxlength="20">
			</div>			
			<div class="form-group">
				<label for="pwdHint">비밀 번호 분실시 답변 힌트</label>
				<input type="text" id="pwdHint" class="form-control" name="pwdHint" maxlength="30">
			</div>
			<div class="form-group">
				<label for="pwdAnswer">비밀 번호 분실시 답변</label>
				<textarea id="pwdAnswer" class="form-control" name="pwdAnswer" rows="5" cols="30"></textarea>
			</div>
			<div class="form-group">
				<label for="captchaImage">Captcha 이미지</label>
				<img id="captchaImage" class="img-thumbnail" src="/servlet/stickyImg" alt="Captcha Image" />
				<a href="#" onClick="reloadCaptcha()" style="curso:pointer"><span class="glyphicon glyphicon-refresh"></span></a>
			</div>			
			<div class="form-group">
				<label for="answer">Captcha 답변</label>
				<input type="text" id="answer" class="form-control" name="answer" maxlength="20" />
			</div>
			<button type="submit" class="btn btn-default">가입</button>
		</form>
	</div>
	<iframe id="hiddenFrame" name="hiddenFrame" style="display:none;visibility:hidden"></iframe>
</body>
</html>

