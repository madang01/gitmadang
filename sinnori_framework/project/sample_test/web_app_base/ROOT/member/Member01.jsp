<%@ page extends="kr.pe.sinnori.common.weblib.AbstractJSP" language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%><%
%><%@ page import="kr.pe.sinnori.common.weblib.WebCommonStaticFinalVars" %><%
%><jsp:useBean id="topmenu" class="java.lang.String" scope="request" /><%
%><jsp:useBean id="leftmenu" class="java.lang.String" scope="request" /><%
%><jsp:useBean id="errorMessage" class="java.lang.String" scope="request" /><%
%><jsp:useBean id="modulusHex" class="java.lang.String" scope="request" /><%
%><script type="text/javascript" src="/js/jsbn/jsbn.js"></script>
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
		f.pwdconfirm.value =  f.pwd.value;
		f.nickname.value = "별명00";
		f.pwdHint.value = "질문";
		f.pwdAnswer.value = "답변";
	}

	window.onload = init;

	function chkform() {
	
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
		var privateKey = CryptoJS.lib.WordArray.random(<%=WebCommonStaticFinalVars.WEBSITE_PRIVATEKEY_SIZE%>);		
		// var privateKey = CryptoJS.enc.Hex.parse("cf5553bdbe3a6240a0a89fdd9be4e64c");
		
		
		// alert(privateKey.length);
		
		
		var rsa = new RSAKey();
		rsa.setPublic("<%=modulusHex%>", "10001");
		
		// FIXME!
		var sessionKeyHex = rsa.encrypt(CryptoJS.enc.Base64.stringify(privateKey));
		// var sessionKeyHex =  "54f5499e2f6c40ec9a35361c4348d798acc3c31310ae949ae2f4e468ab9275aa34fe2452b194c0994d351d6124dd00eb6fdab614add4e661603e56dcccedf8704328f8ae907d4e8529d6457a628402bee0eb641860b10a0c31ceb6c535aeb86c7afa8534d2b486fb383f448394a8630b3620c8938c6b69d410e1d8f7f3ed1d62";
		
		// g.privateKey.value = CryptoJS.enc.Hex.stringify(privateKey);
		// g.sessionKeyHex.value = sessionKeyHex;
		g.sessionkeyBase64.value = CryptoJS.enc.Base64.stringify(CryptoJS.enc.Hex.parse(sessionKeyHex));
		// g.sessionKey.value = CryptoJS.enc.Base64.stringify(rsa.encrypt(privateKey));
		
		// alert(sessionKeyHex);		
		// alert(g.sessionKey.value);
		// alert((ttmp == sessionKeyHex));

		sessionStorage.setItem('<%=WebCommonStaticFinalVars.SESSIONSTORAGE_PRIVATEKEY_NAME%>', CryptoJS.enc.Base64.stringify(privateKey));
		sessionStorage.setItem('<%=WebCommonStaticFinalVars.SESSIONSTORAGE_SESSIONKEY_NAME%>', g.sessionkeyBase64.value);
		
	
		var iv = CryptoJS.lib.WordArray.random(<%=WebCommonStaticFinalVars.WEBSITE_IV_SIZE%>);
		g.ivBase64.value = CryptoJS.enc.Base64.stringify(iv);

		// alert(g.ivBase64.value);
		
		var symmetricKeyObj = CryptoJS.<%=WebCommonStaticFinalVars.WEBSITE_JAVASCRIPT_SYMMETRIC_KEY_ALGORITHM_NAME%>;

		g.id.value = symmetricKeyObj.encrypt(f.id.value, privateKey, { mode: CryptoJS.mode.CBC, padding: CryptoJS.pad.Pkcs7, iv: iv });
		g.pwd.value = symmetricKeyObj.encrypt(f.pwd.value, privateKey, { mode: CryptoJS.mode.CBC, padding: CryptoJS.pad.Pkcs7, iv: iv });
		g.nickname.value = symmetricKeyObj.encrypt(f.nickname.value, privateKey, { mode: CryptoJS.mode.CBC, padding: CryptoJS.pad.Pkcs7, iv: iv });
		g.pwdHint.value = symmetricKeyObj.encrypt(f.pwdHint.value, privateKey, { mode: CryptoJS.mode.CBC, padding: CryptoJS.pad.Pkcs7, iv: iv });
		g.pwdAnswer.value = symmetricKeyObj.encrypt(f.pwdAnswer.value, privateKey, { mode: CryptoJS.mode.CBC, padding: CryptoJS.pad.Pkcs7, iv: iv });

		g.answer.value = symmetricKeyObj.encrypt(f.answer.value, privateKey, { mode: CryptoJS.mode.CBC, padding: CryptoJS.pad.Pkcs7, iv: iv });
		
		// alert(g.id.value);
		
		g.submit();
		
		return;
	}

	function reloadCaptcha() {
		var img = document.getElementById("captchaImage");
		img.src="/servlet/stickyImg?" + new Date().getTime();
	}
//-->
</script>
<form method="post" name="gofrm" action="/servlet/Member">
<input type="hidden" name="topmenu" value="<%=topmenu%>" />
<input type="hidden" name="pageMode" value="proc" />
<input type="hidden" name="sessionkeyBase64" />
<input type="hidden" name="ivBase64" />
<input type="hidden" name="id" />
<input type="hidden" name="pwd" />
<input type="hidden" name="nickname" />
<input type="hidden" name="pwdHint" />
<input type="hidden" name="pwdAnswer" />
<input type="hidden" name="answer" />
</form>
<form method="post" name="frm" onsubmit="return;">
	<table border="0">
	<tr>
		<td colspan="2" align="center" style="font-size:24px">신놀이 회원 가입</td>
	</tr><%
	if (null != errorMessage && !errorMessage.equals("")) {
%>
	<tr>
		<td colspan="2"><%=escapeHtml(errorMessage, WebCommonStaticFinalVars.LINE2BR_STRING_REPLACER)%></td>
	</tr><%
	} else {
%>
	<tr>
	    <td>아이디</td>
	    <td><input type="text" name="id" size="15" maxlength="15" /></td> 
	</tr>
	<tr> 
	    <td>비빌번호</td>
	    <td><input type="password" name="pwd" size="15" maxlength="15" /></td>
	</tr>
	<tr>
	    <td>비밀번호 확인</td>
	    <td><input type="password" name="pwdconfirm" siz="15" maxlength="15" /></td> 
	</tr>
	<tr>
	    <td>별명</td>
	    <td><input type="text" name="nickname" size="20" maxlength="20" /></td>
	</tr>
	<tr>
	    <td>비밀 번호 분실시 답변 힌트</td>
	    <td><input type="text" name="pwdHint" size="30" maxlength="30" /></td>
	</tr>
	<tr> 
	    <td>비밀 번호 분실시 답변</td>
	    <td><textarea name="pwdAnswer" rows="5" cols="30"></textarea></td>
	</tr>
	<tr> 
		<td>Captcha 이미지</td>
	    <td><table border="0"><tr><td width="80%"><img id="captchaImage" src="/servlet/stickyImg" alt="Captcha Image" width="100%" /></td><td><a href="#" onClick="reloadCaptcha()" id="refresh" style="curso:pointer"><img src="/images/gtk_refresh.png" width="100%" /></a></td></tr></table></td>	    
	</tr>
	<tr> 
	    <td>Captcha 답변</td>
	    <td><input type="text" name="answer" size="20" maxlength="20" /></td>
	</tr>

	<tr> 
	    <td colspan="2" align="center"><input type="button" onClick="chkform()" value="확인" /></td>
	</tr><%
	}
%>
	
	</table>
</form>

