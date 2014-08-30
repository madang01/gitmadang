<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%><%
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
function chkform() {
	
		var f = document.frm;
		var g = document.gofrm;
		
		var re_id = /^[A-Za-z][A-Za-z0-9]{3,14}$/;
		var re_pwd = /^[A-Za-z0-9]{6,15}$/;
	
		if(typeof(Storage) == "undefined") {
		    alert("Sorry! No web storage support..");
		    return false;
		}
	
		/*
		if (f.id.value == '') {
		    alert("아이디를 넣어주세요.");
		    f.id.focus();
		    return false;
		}
		
		if (!re_id.test(f.id.value)) {
		    alert("아이디는 첫 문자가 영문자 그리고 영문과 숫자로만 최소 4자, 최대 15자로 구성됩니다. 다시 입력해 주세요.");
		    f.id.value = '';
		    f.id.focus();
		    return false;
		}
		
		if (f.pwd.value == '') {
		    alert("비밀번호를 넣어주세요.");
		    f.pwd.focus();
		    return false;
		}
		
		
		if (!re_id.test(f.pwd.value)) {
		    alert("비밀번호는 영문과 숫자로만 최소 6자, 최대 15자로 구성됩니다. 다시 입력해 주세요.");
		    f.pwd.value = '';
		    f.pwd.focus();
		    return false;
		}
		
		
		if (f.pwdconfirm.value == '') {
		    alert("비밀번호 확인을 넣어주세요.");
		    f.pwdconfirm.focus();
		    return false;
		}
		
		if (f.pwdconfirm.value != f.pwd.value) {
		    alert("비밀번호가 일치하지 않습니다. 다시 넣어주세요.");
		    f.pwd.value='';	    
		    f.pwdconfirm.value='';
		    f.pwd.focus();	    
		    return false;
		}
		
		if (f.nickname.value == '') {
		    alert("별명을 넣어주세요.");
		    f.nickname.focus();
		    return false;
		}
	
		
		if (trimcheck(f.nickname.value)) {
		    alert("별명의 앞뒤 공백을 제거 합니다.");
		    f.nickname.value = trim(f.nickname.value);
		    f.nickname.focus();
		    return false;
		}
		
		if (f.question.value == '') {
		    alert("질문을 넣어주세요.");
		    f.question.focus();
		    return false;
		}
		
		if (trimcheck(f.question.value)) {
		    alert("질문의 앞뒤 공백을 제거 합니다.");
		    f.question.value = trim(f.question.value);
		    f.question.focus();
		    return false;
		}
			
		
		if (f.answer.value == '') {
		    alert("답변을 넣어주세요.");
		    f.answer.focus();
		    return false;
		}	
		
		if (trimcheck(f.answer.value)) {
		    alert("답변의 앞뒤 공백을 제거 합니다.");
		    f.answer.value = trim(f.answer.value);
		    f.answer.focus();
		    return false;
		}
		*/
		
		f.id.value =  "test00";
		f.pwd.value =  "test1234";
		f.nickname.value = "별명";
		f.question.value = "질문";
		f.answer.value = "답변";
		
		// FIXME!
		var privateKey = CryptoJS.lib.WordArray.random(16);		
		// var privateKey = CryptoJS.enc.Hex.parse("cf5553bdbe3a6240a0a89fdd9be4e64c");
		localStorage.sinnori_key = privateKey;
		
		// alert(privateKey.length);
		// alert(localStorage.sinnori_key.length);
		
		
		var rsa = new RSAKey();
		rsa.setPublic("<%=modulusHex%>", "10001");
		
		// FIXME!
		var sessionKeyHex = rsa.encrypt(CryptoJS.enc.Base64.stringify(privateKey));
		// var sessionKeyHex =  "54f5499e2f6c40ec9a35361c4348d798acc3c31310ae949ae2f4e468ab9275aa34fe2452b194c0994d351d6124dd00eb6fdab614add4e661603e56dcccedf8704328f8ae907d4e8529d6457a628402bee0eb641860b10a0c31ceb6c535aeb86c7afa8534d2b486fb383f448394a8630b3620c8938c6b69d410e1d8f7f3ed1d62";
		
		g.privateKey.value = CryptoJS.enc.Hex.stringify(privateKey);
		g.sessionKeyHex.value = sessionKeyHex;
		g.sessionKey.value = CryptoJS.enc.Base64.stringify(CryptoJS.enc.Hex.parse(sessionKeyHex));
		// g.sessionKey.value = CryptoJS.enc.Base64.stringify(rsa.encrypt(privateKey));
		
		// alert(sessionKeyHex);		
		// alert(g.sessionKey.value);
		// alert((ttmp == sessionKeyHex));
		
	
		var iv = CryptoJS.lib.WordArray.random(16);
		g.iv.value = CryptoJS.enc.Base64.stringify(iv);

		// alert(g.iv.value);
		var pwdHash =  CryptoJS.SHA256(f.pwd.value);
		
		g.id.value = CryptoJS.AES.encrypt(f.id.value, privateKey, { mode: CryptoJS.mode.CBC, padding: CryptoJS.pad.Pkcs7, iv: iv });
		g.pwd.value = CryptoJS.AES.encrypt(pwdHash.toString(CryptoJS.enc.Base64), privateKey, { mode: CryptoJS.mode.CBC, padding: CryptoJS.pad.Pkcs7, iv: iv });
		g.nickname.value = CryptoJS.AES.encrypt(f.nickname.value, privateKey, { mode: CryptoJS.mode.CBC, padding: CryptoJS.pad.Pkcs7, iv: iv });
		g.question.value = CryptoJS.AES.encrypt(f.question.value, privateKey, { mode: CryptoJS.mode.CBC, padding: CryptoJS.pad.Pkcs7, iv: iv });
		g.answer.value = CryptoJS.AES.encrypt(f.answer.value, privateKey, { mode: CryptoJS.mode.CBC, padding: CryptoJS.pad.Pkcs7, iv: iv });
		
		// alert(g.id.value);
		// alert(pwdHash.toString(CryptoJS.enc.Base64));
		
		g.submit();
		
		return false;
	}
//-->
</script>
<form method="post" name="gofrm" target="_top">
<input type="hidden" name="topmenu" value="<%=topmenu%>" />
<input type="hidden" name="pagegubun" value="step2" />
<input type="hidden" name="privateKey" />
<input type="hidden" name="sessionKeyHex" />
<input type="hidden" name="sessionKey" />
<input type="hidden" name="iv" />
<input type="hidden" name="id" />
<input type="hidden" name="pwd" />
<input type="hidden" name="nickname" />
<input type="hidden" name="question" />
<input type="hidden" name="answer" />
</form>
<form method="post" name="frm" onsubmit="return chkform();">
	<table border="0">
	<tr>
		<td colspan="2" align="center" style="font-size:24px">마당쇠 회원 가입</td></tr>
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
	    <td>질문</td>
	    <td><input type="text" name="question" size="30" maxlength="30" /></td>
	</tr>
	<tr> 
	    <td>답변</td>
	    <td><textarea name="answer" rows="5" cols="30"></textarea></td>
	</tr>
	<tr> 
	    <td colspan="2" align="center"><input type="submit" value="확인" /></td>
	</tr>
	</table>
</form>

