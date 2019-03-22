<%@page import="org.apache.commons.text.StringEscapeUtils"%><%
%><%@page import="java.util.Enumeration"%><%
%><%@ page import="kr.pe.codda.weblib.common.WebCommonStaticFinalVars" %><%
%><%@ page extends="kr.pe.codda.weblib.jdf.AbstractUserJSP" language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%><%
%><jsp:useBean id="requestURI" class="java.lang.String" scope="request" /><%
%>
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
<script type="text/javascript">
<!--
	function init() {
		var f = document.frm;
		f.userID.value =  "test00";
		f.pwd.value =  "test1234$";
		// f.pwd.value =  "!\"#$%&'()*+,-./:;<=>?@[\\]^_`{|}~$";
	}

	window.onload = init;

	function submitGoFormIfValid() {				
		var f = document.frm;
		var g = document.gofrm;
		
		var regexID = /^[A-Za-z][A-Za-z0-9]{3,14}$/;
		var regexPwd = /^[A-Za-z0-9\`~!@\#$%<>\^&*\(\)\-=+_\'\[\]\{\}\\\|\:\;\"<>\?,\.\/]{<%= WebCommonStaticFinalVars.MIN_NUMBER_OF_PASSWRORD_CHARRACTERS %>,<%= WebCommonStaticFinalVars.MAX_NUMBER_OF_PASSWRORD_CHARRACTERS %>}$/;
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
			alert("비밀번호는 영문, 숫자 그리고 특수문자 조합으로 최소 <%= WebCommonStaticFinalVars.MIN_NUMBER_OF_PASSWRORD_CHARRACTERS %>자, 최대 <%= WebCommonStaticFinalVars.MAX_NUMBER_OF_PASSWRORD_CHARRACTERS %>자로 구성됩니다. 다시 입력해 주세요.");
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
				
		
		var privateKey = CryptoJS.lib.WordArray.random(<%= WebCommonStaticFinalVars.WEBSITE_PRIVATEKEY_SIZE %>);
		var privateKeyBase64 = CryptoJS.enc.Base64.stringify(privateKey);
		
		var rsa = new RSAKey();
		rsa.setPublic("<%= getModulusHexString(request) %>", "10001");
			
		var sessionKeyHex = rsa.encrypt(privateKeyBase64);		
		g.<%= WebCommonStaticFinalVars.PARAMETER_KEY_NAME_OF_SESSION_KEY %>.value 
			= CryptoJS.enc.Base64.stringify(CryptoJS.enc.Hex.parse(sessionKeyHex));
		
		var iv = CryptoJS.lib.WordArray.random(<%= WebCommonStaticFinalVars.WEBSITE_IV_SIZE %>);
		g.<%= WebCommonStaticFinalVars.PARAMETER_KEY_NAME_OF_SESSION_KEY_IV %>.value 
			= CryptoJS.enc.Base64.stringify(iv);

		
		var symmetricKeyObj = CryptoJS.<%=WebCommonStaticFinalVars.WEBSITE_JAVASCRIPT_SYMMETRIC_KEY_ALGORITHM_NAME%>;
	
		g.userID.value = symmetricKeyObj.encrypt(f.userID.value, privateKey, { mode: CryptoJS.mode.CBC, padding: CryptoJS.pad.Pkcs7, iv: iv });
		g.pwd.value = symmetricKeyObj.encrypt(f.pwd.value, privateKey, { mode: CryptoJS.mode.CBC, padding: CryptoJS.pad.Pkcs7, iv: iv });
		
		f.pwd.value = '';
		
		// alert("성공");
		g.submit();		
		
		var newPrivateKey = CryptoJS.lib.WordArray.random(<%=WebCommonStaticFinalVars.WEBSITE_PRIVATEKEY_SIZE%>);
		var newPrivateKeyBase64 = CryptoJS.enc.Base64.stringify(newPrivateKey);
		sessionStorage.setItem('<%= WebCommonStaticFinalVars.SESSIONSTORAGE_KEY_NAME_OF_PRIVATEKEY %>', newPrivateKeyBase64);
		
		return;
	}
	
	function errorMessageCallBack(errorMessage) {		
		var resultMessageView = document.getElementById("resultMessageView");
		
		resultMessageView.setAttribute("class", "alert alert-warning");
		resultMessageView.innerHTML = "<strong>Warning!</strong> " + errorMessage;
	}
	
	function loginOKCallBack() {
		var resultMessageView = document.getElementById("resultMessageView");
		
		resultMessageView.setAttribute("class", "alert alert-success");
		resultMessageView.innerHTML = "<strong>Success!</strong> "+document.frm.userID.value+" 님 로그인 성공하였습니다.";
		
		var g = document.successURLfrm;
		
		var privateKey = CryptoJS.lib.WordArray.random(<%=WebCommonStaticFinalVars.WEBSITE_PRIVATEKEY_SIZE%>);
		var privateKeyBase64 = CryptoJS.enc.Base64.stringify(privateKey);
		sessionStorage.setItem('<%= WebCommonStaticFinalVars.SESSIONSTORAGE_KEY_NAME_OF_PRIVATEKEY %>', privateKeyBase64);
		
		var rsa = new RSAKey();
		rsa.setPublic("<%= getModulusHexString(request) %>", "10001");
			
		var sessionKeyHex = rsa.encrypt(privateKeyBase64);		
		g.<%= WebCommonStaticFinalVars.PARAMETER_KEY_NAME_OF_SESSION_KEY %>.value 
			= CryptoJS.enc.Base64.stringify(CryptoJS.enc.Hex.parse(sessionKeyHex));
		
		var iv = CryptoJS.lib.WordArray.random(<%= WebCommonStaticFinalVars.WEBSITE_IV_SIZE %>);
		g.<%= WebCommonStaticFinalVars.PARAMETER_KEY_NAME_OF_SESSION_KEY_IV %>.value 
			= CryptoJS.enc.Base64.stringify(iv);
		
		g.submit();		
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
				<div class="panel-heading"><h4>사용자 로그인</h4></div>
				<div class="panel-body">
					<div id="resultMessageView"></div>
					<div class="btn-group">
						<button type="button" class="btn btn-primary btn-sm" onClick="clickHiddenFrameButton(this);">Show Hidden Frame</button>			
					</div>
					<form method="post" name="gofrm" target="hiddenFrame" action="/servlet/UserLoginProcess">
						<input type="hidden" name="<%= WebCommonStaticFinalVars.PARAMETER_KEY_NAME_OF_SESSION_KEY %>" />
						<input type="hidden" name="<%= WebCommonStaticFinalVars.PARAMETER_KEY_NAME_OF_SESSION_KEY_IV %>" />
						<input type="hidden" name="userID" />
						<input type="hidden" name="pwd" />
					</form>
					<form method="post" name="successURLfrm" action="<%= requestURI %>">
						<input type="hidden" name="<%= WebCommonStaticFinalVars.PARAMETER_KEY_NAME_OF_SESSION_KEY %>" />
						<input type="hidden" name="<%= WebCommonStaticFinalVars.PARAMETER_KEY_NAME_OF_SESSION_KEY_IV %>" /><%
				Enumeration<String> parmEnum = request.getParameterNames();
				while(parmEnum.hasMoreElements()) {
					String parmKey = parmEnum.nextElement();
					String parmValue = request.getParameter(parmKey);
							
					if (parmKey.equals(WebCommonStaticFinalVars.PARAMETER_KEY_NAME_OF_SESSION_KEY) ||
						parmKey.equals(WebCommonStaticFinalVars.PARAMETER_KEY_NAME_OF_SESSION_KEY_IV)) {
						continue;
					}
			%>
						<textarea style="visibility:hidden;" name="<%= StringEscapeUtils.escapeHtml4(parmKey) %>"><%= StringEscapeUtils.escapeHtml4(parmValue) %></textarea><%
						}
					%>
					</form><br>
					<form method="post" name="frm" onsubmit="submitGoFormIfValid(); return false;">
						<div class="form-group">				
							<label for="userID">아이디:</label>
							<input type="text" class="form-control" id="userID" placeholder="Enter admin's id" name="userID">
							<br>
						
							<label for="pwd">비빌번호:</label>
							<input type="password" class="form-control" id="pwd" placeholder="Enter password" name="pwd">
						</div>
						<button type="submit" class="btn btn-default">Submit</button>		
					</form>
					<iframe id="hiddenFrame" name="hiddenFrame" style="display:none;"></iframe>
				</div>
			</div>
		</div>
	</div>
</body>
</html>
