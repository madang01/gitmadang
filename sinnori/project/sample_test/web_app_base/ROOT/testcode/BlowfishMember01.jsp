<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%><%
%><%@ page import="kr.pe.sinnori.common.weblib.WebCommonStaticFinalVars" %><%
	kr.pe.sinnori.common.sessionkey.ServerSessionKeyManager serverSessionKeyManager = kr.pe.sinnori.common.sessionkey.ServerSessionKeyManager.getInstance();


	String modulusHexStr = null;

	try {
		modulusHexStr = serverSessionKeyManager.getModulusHexStrForWeb(); 
	} catch(RuntimeException re) {
	}
%>
<!-- RSA -->
<script type="text/javascript" src="/js/rsa/jsbn.js"></script>
<script type="text/javascript" src="/js/rsa/jsbn2.js"></script>
<script type="text/javascript" src="/js/rsa/prng4.js"></script>
<script type="text/javascript" src="/js/rsa/rng.js"></script>
<script type="text/javascript" src="/js/rsa/rsa.js"></script>
<script type="text/javascript" src="/js/rsa/rsa2.js"></script>
<!-- crypto-JS AES -->
<script type="text/javascript" src="/js/cryptoJS/crypto-sha1-hmac-pbkdf2-blockmodes-aes.js"></script>
<script type="text/javascript">
<!--
	function trim(str) {
		return str.replace(/^\s+|\s+$/gm,'');
	}

	function trimcheck(str) {
		var r_first = /^\s\s*/;
		var r_last = /\s\s*$/;
		
		if (r_first.test(str) || r_last.test(str)) {
		    return true;
		} else {
		    return false;
		}
	}

	function chkform() {
		var f = document.frm;
		
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
	
		/*
		    Blowfish key size min 8bit, max 448bit, so 448 bit == 56 Byte == 112 Hex chars.
		    function Generate_key() return 64 Hex Chars, so 32byte
		    in order to debug easy, add a newline after 64 chars
		*/
	
		/*
		 * var digestBytes = Crypto.MD5("Message", { asBytes: true });
		 * var digestString = Crypto.MD5("Message", { asString: true });
		 * var helloBytes = Crypto.charenc.Binary.stringToBytes("Hello, World!");
		 * var helloString = Crypto.charenc.Binary.bytesToString(helloBytes);		
		 * var utf8Bytes = Crypto.charenc.UTF8.stringToBytes("България");
		 * var unicodeString = Crypto.charenc.UTF8.bytesToString(utf8Bytes);		
		 * var helloHex = Crypto.util.bytesToHex(helloBytes);
		 * var helloBytes = Crypto.util.hexToBytes(helloHex);		
		 * var helloBase64 = Crypto.util.bytesToBase64(helloBytes);
		 * var helloBytes = Crypto.util.base64ToBytes(helloBase64);
		 * var crypted = Crypto.AES.encrypt("Message", "Secret Passphrase", { mode: new Crypto.mode.CBC(Crypto.pad.ansix923) });		 
		 * var plain = Crypto.AES.decrypt(crypted, "Secret Passphrase", { mode: new Crypto.mode.CBC(Crypto.pad.ansix923) });
		 * The modes of operation currently available are:
			ECB
			CBC
			CFB
			OFB
			CTR 

		 * And the padding schemes currently available are:
		    iso7816
		    ansix923
		    iso10126
		    pkcs7
		    ZeroPadding
		    NoPadding
		 */
		var digest = Crypto.SHA1("Message");
		alert(digest);
		
		var keyHex = "08090A0B0D0E0F10121314151718191A1C1D1E1F21222324262728292B2C2D2E";
		var keyBytes = Crypto.util.hexToBytes(keyHex);
		// var keyString = Crypto.charenc.Binary.bytesToString(keyBytes);
		
		
		
		var plainTextHex = "68656c6c6f";
		var plainTextBytes = Crypto.util.hexToBytes(plainTextHex);
		
		// var iv = Crypto.util.randomBytes(16);
		var iv = Crypto.util.hexToBytes('101112131415161718191a1b1c1d1e1f');
		
		
		// var encrtypedTextBytes = Crypto.AES.encrypt(plainTextBytes, keyBytes, { mode: new Crypto.mode.CBC(Crypto.pad.pkcs7 ), iv : iv, asBytes: true });
		// var encrtypedTextHex = Crypto.util.bytesToHex(encrtypedTextBytes);
		
		var encrtypedTextHex = "26e97e49af0ff4e7f8d222a5c9b5eb1a";
		var encrtypedTextBytes = Crypto.util.hexToBytes(encrtypedTextHex);
		// var encrtypedText64 = Crypto.util.bytesToBase64(encrtypedTextBytes);
		
		
		// var decrtypedTextBytes = Crypto.AES.decrypt(encrtypedTextBytes, keyBytes , { mode: new Crypto.mode.CBC(Crypto.pad.pkcs7 ), iv : iv, asBytes: true });
		var decrtypedTextBytes = Crypto.AES.decrypt(encrtypedTextBytes, keyBytes , { mode: new Crypto.mode.ECB(Crypto.pad.pkcs7 ), asBytes: true });
		var decrtypedTextHex = Crypto.util.bytesToHex(decrtypedTextBytes);
	
		//alert(Crypto.util.bytesToHex(plainData));
		alert(encrtypedTextHex);
		alert(Crypto.charenc.UTF8.bytesToString(decrtypedTextBytes));
		
		// localStorage.sinnori_blowfishkey=symmetircKeyBytes;

		/*
		var rsa = new RSAKey();
	  	rsa.setPublic("<%=modulusHexStr%>", "10001");
	  	var sessionKey = rsa.encrypt(symmetircKeyBytes);

		var g = document.gofrm;
		g.sessionKey.value = sessionKey;		
		g.id.value = hex_from_chars(des(symmetircKeyBytes, f.id.value, 1, 0, ivBytes));	
		g.pwd.value = hex_from_chars(des(symmetircKeyBytes, f.pwd.value, 1, 0, ivBytes));	
		g.nickname.value = hex_from_chars(des(symmetircKeyBytes, f.nickname.value, 1, 0, ivBytes));	
		g.question.value = hex_from_chars(des(symmetircKeyBytes, f.question.value, 1, 0, ivBytes));	
		g.answer.value = hex_from_chars(des(symmetircKeyBytes, f.answer.value, 1, 0, ivBytes));
	
		alert(g.id.value);	
	
		g.submit();
		*/
		
		return false;
    }
// -->
</script>
SINNORI_CONFIG_FILE=[<%=java.lang.System.getenv("SINNORI_CONFIG_FILE")%>]<br/>
url : /testchcode/BlowfishMember01.jsp<br/>
소개 : Blowfish 이용한 섹션키를 사용한 회원 가입페이지<br/>

<form method="post" name="gofrm" target="_top">
<input type="hidden" name="pagegubun" value="step2" />
<input type="hidden" name="sessionKey" />
<!-- input type="hidden" name="iv" / -->
<input type="hidden" name="id" />
<input type="hidden" name="pwd" />
<input type="hidden" name="pwdMD5" />
<input type="hidden" name="pwdSalt" />
<input type="hidden" name="nickname" />
<input type="hidden" name="question" />
<input type="hidden" name="answer" />
</form>
	<form method="post" name="frm" onsubmit="return chkform();">
	<table>
	<tr><td colspan="2"><h3>마당쇠 회원 가입</h3></td></tr>
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
	    <td><input type="password" name="pwdconfirm" size="15" maxlength="15" /></td> 
	</tr>
	<tr>
	    <td>별명</td>
	    <td><input type="text" name="nickname" size="30" maxlength="30" /></td>
	</tr>
	<tr>
	    <td>질문</td>
	    <td><input type="text" name="question" size="30" maxlength="50" /></td>
	</tr>
	<tr> 
	    <td>답변</td>
	    <td><textarea name="answer" rows="5" cols="30"></textarea></td>
	</tr>
	<tr> 
	    <td colspan="2"><input type="submit" value="확인" /></td>
	</tr>
	</table>
	</form>
