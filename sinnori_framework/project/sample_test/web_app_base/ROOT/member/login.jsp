<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%><%
%><%@ page import="kr.pe.sinnori.common.servlet.WebCommonStaticFinalVars" %><%
%><jsp:useBean id="topmenu" class="java.lang.String" scope="request" /><%
%><jsp:useBean id="leftmenu" class="java.lang.String" scope="request" /><%
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
	}

	window.onload = init;

	function chkform() {	
		var f = document.frm;
		var g = document.gofrm;
		
		var regexp_id = /^[A-Za-z][A-Za-z0-9]{3,14}$/;
		var regexp_pwd = /^[A-Za-z0-9\`~!@\#$%<>\^&*\(\)\-=+_\'\[\]\{\}\\\|\:\;\"<>\?,\.\/]{8,15}$/;
		var regexp_pwd_alphabet = /[A-Za-z]{1,}/;
		var regexp_pwd_digit = /[0-9]{1,}/;
		var regexp_pwd_special = /[\`~!@\#$%<>\^&*\(\)\-=+_\'\[\]\{\}\\\|\:\;\"<>\?,\.\/]{1,}/;
 			
		if(typeof(Storage) == "undefined") {
		    alert("Sorry! No web storage support..");
		    return;
		}	
		
		if (f.id.value == '') {
		    alert("아이디를 넣어주세요.");
		    f.id.focus();
		    return;
		}
		
		if (!regexp_id.test(f.id.value)) {
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
		
		
		if (!regexp_pwd.test(f.pwd.value)) {
		    alert("비밀번호는 영문, 숫자 그리고 특수문자 조합으로 최소 8자, 최대 15자로 구성됩니다. 다시 입력해 주세요.");
		    f.pwd.value = '';
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

		if (!regexp_pwd_alphabet.test(f.pwd.value)) {
		    alert("비밀번호는 최소 영문 1자가 포함되어야 합니다. 다시 입력해 주세요.");
		    f.pwd.value = '';
		    f.pwd.focus();
		    return;
		}

		if (!regexp_pwd_digit.test(f.pwd.value)) {
		    alert("비밀번호는 최소 숫자 1자가 포함되어야 합니다. 다시 입력해 주세요.");
		    f.pwd.value = '';
		    f.pwd.focus();
		    return;
		}

		if (!regexp_pwd_special.test(f.pwd.value)) {
		    alert("비밀번호는 최소 특수문자 1자가 포함되어야 합니다. 다시 입력해 주세요.");
		    f.pwd.value = '';
		    f.pwd.focus();
		    return;
		}		
				
		
		var privateKey = CryptoJS.lib.WordArray.random(<%=WebCommonStaticFinalVars.WEBSITE_PRIVATEKEY_SIZE%>);
		
		var rsa = new RSAKey();
		rsa.setPublic("<%=modulusHex%>", "10001");
			
		var sessionKeyHex = rsa.encrypt(CryptoJS.enc.Base64.stringify(privateKey));		
		g.sessionkeyBase64.value = CryptoJS.enc.Base64.stringify(CryptoJS.enc.Hex.parse(sessionKeyHex));

		sessionStorage.setItem('<%=WebCommonStaticFinalVars.SESSIONSTORAGE_PRIVATEKEY_NAME%>', CryptoJS.enc.Base64.stringify(privateKey));
		sessionStorage.setItem('<%=WebCommonStaticFinalVars.SESSIONSTORAGE_SESSIONKEY_NAME%>', g.sessionkeyBase64.value);

		var iv = CryptoJS.lib.WordArray.random(<%=WebCommonStaticFinalVars.WEBSITE_IV_SIZE%>);
		g.ivBase64.value = CryptoJS.enc.Base64.stringify(iv);

		
		var symmetricKeyObj = CryptoJS.<%=WebCommonStaticFinalVars.WEBSITE_JAVASCRIPT_SYMMETRIC_KEY_ALGORITHM_NAME%>;
	
		g.id.value = symmetricKeyObj.encrypt(f.id.value, privateKey, { mode: CryptoJS.mode.CBC, padding: CryptoJS.pad.Pkcs7, iv: iv });
		g.pwd.value = symmetricKeyObj.encrypt(f.pwd.value, privateKey, { mode: CryptoJS.mode.CBC, padding: CryptoJS.pad.Pkcs7, iv: iv });
		
		f.pwd.value = '';
		
		// alert("성공");

		g.submit();
		
		return;
	}
//-->
</script>
<form method="post" name="gofrm" action="/servlet/Login">
<input type="hidden" name="topmenu" value="<%=topmenu%>" />
<input type="hidden" name="pageGubun" value="step2" />
<input type="hidden" name="sessionkeyBase64" />
<input type="hidden" name="ivBase64" />
<input type="hidden" name="id" />
<input type="hidden" name="pwd" />
</form>
<form method="post" name="frm" onsubmit="return false;">
<table border="0">
	<tr>
		<td colspan="2" align="center" style="font-size:24px">로그인</td></tr>
	<tr>
	    <td>아이디</td>
	    <td><input type="text" name="id" size="15" maxlength="15" /></td> 
	</tr>
	<tr> 
	    <td>비빌번호</td>
	    <td><input type="password" name="pwd" size="15" maxlength="15" /></td>
	</tr>
	<tr> 
	    <td colspan="2" align="center"><input type="button" value="확인" onclick="chkform()" /></td>
	</tr>
	</table>
</form>
