<%@ page extends="kr.pe.codda.weblib.jdf.AbstractJSP" language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%><%
%><%@ page import="kr.pe.codda.weblib.common.WebCommonStaticFinalVars" %><%
%><%@page import="kr.pe.codda.weblib.htmlstring.HtmlStringUtil"%><%
%><%@ page import="kr.pe.codda.weblib.sitemenu.AdminSiteMenuManger" %><%
	AdminSiteMenuManger adminSiteMenuManger = AdminSiteMenuManger.getInstance();
%>
<!DOCTYPE html>
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
<script type="text/javascript" src="/js/cryptoJS/rollups/sha256.js"></script>
<script type="text/javascript" src="/js/cryptoJS/rollups/aes.js"></script>
<script type="text/javascript" src="/js/cryptoJS/components/core-min.js"></script>
<script type="text/javascript" src="/js/cryptoJS/components/cipher-core-min.js"></script>
<script type="text/javascript">
<!--
	function init() {
		var f = document.frm;
		f.id.value =  "test00";
		f.pwd.value =  "test1234$";
		// f.pwd.value =  "!\"#$%&'()*+,-./:;<=>?@[\\]^_`{|}~$";
	}

	window.onload = init;

	function chkform() {				
		var f = document.frm;
		var g = document.gofrm;
		
		var regexID = /^[A-Za-z][A-Za-z0-9]{3,14}$/;
		var regexPwd = /^[A-Za-z0-9\`~!@\#$%<>\^&*\(\)\-=+_\'\[\]\{\}\\\|\:\;\"<>\?,\.\/]{8,50}$/;
		var regexPwdAlpha = /.*[A-Za-z]{1,}.*/;
		var regexPwdDigit = /.*[0-9]{1,}.*/;
		//var regexPwdPunct = /.*[\`~!@\#$%<>\^&*\(\)\-=+_\'\[\]\{\}\\\|\:\;\"<>\?,\.\/]{1,}.*/;
		var regexPwdPunct = /.*[\!\"#$%&'()*+,\-\.\/:;<=>\?@\[\\\]^_`\{\|\}~]{1,}.*/;

					
		if(typeof(sessionStorage) == "undefined") {
			alert("Sorry! No HTML5 sessionStorage support..");
			return;
		}	
		
		if (f.id.value == '') {
			alert("아이디를 넣어주세요.");
			f.id.focus();
			return;
		}
		
		if (!regexID.test(f.id.value)) {
			alert("아이디는 첫 문자가 영문자 그리고 영문과 숫자로만 최소 4자, 최대 15자로 구성됩니다. 다시 입력해 주세요.");
			f.id.value = '';
			f.id.focus();
			return;
		}
		
		if (f.pwd.value == '') {
			alert("비밀번호를 넣어주세요.");
			f.pwd.focus();
			return;
		}
		
		
		/*
		for( var i=0; i< f.pwd.value.length; i++){
			if(regexp_pwd.test(f.pwd.value.charAt(i)) == false ){
				alert(f.pwd.value.charAt(i) + "는 입력불가능한 문자입니다");
				return;
			}
		}
		*/	

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
				
		
		var privateKey = CryptoJS.lib.WordArray.random(<%=WebCommonStaticFinalVars.WEBSITE_PRIVATEKEY_SIZE%>);
		
		var rsa = new RSAKey();
		rsa.setPublic("<%= getModulusHexString(request) %>", "10001");
			
		var sessionKeyHex = rsa.encrypt(CryptoJS.enc.Base64.stringify(privateKey));		
		g.<%= WebCommonStaticFinalVars.PARAMETER_KEY_NAME_OF_SESSION_KEY %>.value 
			= CryptoJS.enc.Base64.stringify(CryptoJS.enc.Hex.parse(sessionKeyHex));

		sessionStorage.setItem('<%= WebCommonStaticFinalVars.SESSIONSTORAGE_KEY_NAME_OF_SESSIONKEY %>', g.sessionkeyBase64.value);
		sessionStorage.setItem('<%= WebCommonStaticFinalVars.SESSIONSTORAGE_KEY_NAME_OF_PRIVATEKEY %>', CryptoJS.enc.Base64.stringify(privateKey));

		var iv = CryptoJS.lib.WordArray.random(<%= WebCommonStaticFinalVars.WEBSITE_IV_SIZE %>);
		g.<%= WebCommonStaticFinalVars.PARAMETER_KEY_NAME_OF_SESSION_KEY_IV %>.value 
			= CryptoJS.enc.Base64.stringify(iv);

		
		var symmetricKeyObj = CryptoJS.<%=WebCommonStaticFinalVars.WEBSITE_JAVASCRIPT_SYMMETRIC_KEY_ALGORITHM_NAME%>;
	
		g.id.value = symmetricKeyObj.encrypt(f.id.value, privateKey, { mode: CryptoJS.mode.CBC, padding: CryptoJS.pad.Pkcs7, iv: iv });
		g.pwd.value = symmetricKeyObj.encrypt(f.pwd.value, privateKey, { mode: CryptoJS.mode.CBC, padding: CryptoJS.pad.Pkcs7, iv: iv });
		
		f.pwd.value = '';
		
		// alert("성공");
		
		var chpherText01 = symmetricKeyObj.encrypt("한글", privateKey, { mode: CryptoJS.mode.CBC, padding: CryptoJS.pad.Pkcs7, iv: iv });
		console.log("chpherText01::"+chpherText01);

		g.submit();
		
		return;
	}
//-->
</script>
</head>
<body>
<%=adminSiteMenuManger.getSiteNavbarString(isAdminLogin(request))%>
	
	<div class="container-fluid">
		<h3>관리자 로그인</h3>
		<form method="post" name="gofrm" action="/servlet/AdminLogin">
			<input type="hidden" name="<%= WebCommonStaticFinalVars.PARAMETER_KEY_NAME_OF_REQUEST_TYPE %>" value="proc" />
			<input type="hidden" name="<%= WebCommonStaticFinalVars.PARAMETER_KEY_NAME_OF_SESSION_KEY %>" />
			<input type="hidden" name="<%= WebCommonStaticFinalVars.PARAMETER_KEY_NAME_OF_SESSION_KEY_IV %>" />
			<input type="hidden" name="id" />
			<input type="hidden" name="pwd" />
		</form>
		<form method="post" name="frm" onsubmit="return false;">
			<div class="form-group">
				<label for="email">관리자 아이디:</label>
				<input type="text" class="form-control" id="id" placeholder="Enter admin's id" name="id">
			</div>
			<div class="form-group">
				<label for="email">비빌번호:</label>
				<input type="password" class="form-control" id="pwd" placeholder="Enter password" name="pwd">
			</div>
			<button type="submit" class="btn btn-default" onclick="chkform()">Submit</button>		
		</form>
	</div>
</body>
</html>
