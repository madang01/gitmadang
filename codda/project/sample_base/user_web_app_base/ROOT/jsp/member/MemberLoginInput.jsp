<%@page import="org.apache.commons.text.StringEscapeUtils"%><%
%><%@page import="java.util.Enumeration"%><%
%><%@ page import="kr.pe.codda.weblib.common.WebCommonStaticFinalVars" %><%
%><%@ page extends="kr.pe.codda.weblib.jdf.AbstractUserJSP" language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%><%
%><jsp:useBean id="requestURI" class="java.lang.String" scope="request" /><%
%><jsp:useBean id="userID" class="java.lang.String" scope="request" /><%
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
	
	function goLogin() {				
		var f = document.frm;
		
		try {
			checkValidUserID('로그인', f.id.value);
		} catch(err) {
			alert(err);
			f.id.focus();
			return;
		}
		
		try {
			checkValidPwd('로그인', f.pwd.value);
		} catch(err) {
			alert(err);
			f.pwd.focus();
			return;
		}	
				
		var symmetricKeyObj = CryptoJS.<%=WebCommonStaticFinalVars.WEBSITE_JAVASCRIPT_SYMMETRIC_KEY_ALGORITHM_NAME%>;
		var privateKey = buildPrivateKey();
		var privateKeyBase64 = CryptoJS.enc.Base64.stringify(privateKey);
		var iv = buildIV();		
		
		var rsa = new RSAKey();
		rsa.setPublic("<%= getModulusHexString(request) %>", "10001");
		var sessionKeyHex = rsa.encrypt(privateKeyBase64);
		var sessionKey = CryptoJS.enc.Hex.parse(sessionKeyHex);
		
		var g = document.gofrm;
		
		g.<%= WebCommonStaticFinalVars.PARAMETER_KEY_NAME_OF_SESSION_KEY %>.value = CryptoJS.enc.Base64.stringify(sessionKey);
		g.<%= WebCommonStaticFinalVars.PARAMETER_KEY_NAME_OF_SESSION_KEY_IV %>.value = CryptoJS.enc.Base64.stringify(iv);		
	
		g.userID.value = symmetricKeyObj.encrypt(f.userID.value, privateKey, { mode: CryptoJS.mode.CBC, padding: CryptoJS.pad.Pkcs7, iv: iv });
		g.pwd.value = symmetricKeyObj.encrypt(f.pwd.value, privateKey, { mode: CryptoJS.mode.CBC, padding: CryptoJS.pad.Pkcs7, iv: iv });
		
		f.pwd.value = '';
		putNewPrivateKeyToSessionStorage();
		
		
		
		g.submit();
		return;
	}
	
	function callBackForErrorMessage(errorMessage) {		
		var resultMessageDiv = document.getElementById("resultMessage");
		
		resultMessageDiv.setAttribute("class", "alert alert-warning");
		resultMessageDiv.innerHTML = "<strong>Warning!</strong> " + errorMessage;
	}
	
	function callBackForMemberLoginProcess() {
		var resultMessageDiv = document.getElementById("resultMessage");
		
		resultMessageDiv.setAttribute("class", "alert alert-success");
		resultMessageDiv.innerHTML = "<strong>Success!</strong> "+document.frm.userID.value+" 님 로그인 성공하였습니다.";
		
		var iv = buildIV();
		
		var g = document.successURLfrm;
		
		g.<%= WebCommonStaticFinalVars.PARAMETER_KEY_NAME_OF_SESSION_KEY %>.value = getSessionkeyBase64FromSessionStorage();
		g.<%= WebCommonStaticFinalVars.PARAMETER_KEY_NAME_OF_SESSION_KEY_IV %>.value = CryptoJS.enc.Base64.stringify(iv);
		
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
	
	function init() {
		if(typeof(sessionStorage) == "undefined") {
		    alert("Sorry! No HTML5 sessionStorage support..");
		    document.location.href = "/";
		    return;
		}
		
		/*
		var f = document.frm;
		f.userID.value =  "test00";
		f.pwd.value =  "test1234$";
		*/
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
				<div class="panel-heading"><h4>사용자 로그인</h4></div>
				<div class="panel-body">
					<div id="resultMessage"></div>
					<div class="btn-group">
						<button type="button" class="btn btn-primary btn-sm" onClick="clickHiddenFrameButton(this);">Show Hidden Frame</button>			
					</div>
					<form method="post" name="gofrm" target="hiddenFrame" action="/servlet/MemberLoginProcess">
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
						<textarea style="display:none;" name="<%= StringEscapeUtils.escapeHtml4(parmKey) %>"><%= StringEscapeUtils.escapeHtml4(parmValue) %></textarea><%
						}
					%>
					</form><br>
					<form method="post" name="frm" onsubmit="goLogin(); return false;">
						<div class="form-group">				
							<label for="userID">아이디:</label>
							<input type="text" class="form-control" id="userID" placeholder="Enter admin's id" name="userID" value="<%= userID %>">
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
