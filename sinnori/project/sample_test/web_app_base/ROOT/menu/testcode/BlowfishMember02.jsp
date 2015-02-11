<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<script type="text/javascript" src="/js/blowfish/blowfish.js"></script>
<!-- key generator stuff - leave this out if you don't want to generate keys -->
<script type="text/javascript" src="/js/aes/aes.js"></script>
<script type="text/javascript" src="/js/aes/entropy.js"></script>
<script type="text/javascript" src="/js/aes/aesprng.js"></script>
<script type="text/javascript" src="/js/aes/md5.js"></script>
<script type="text/javascript" src="/js/aes/jscrypt.js"></script>
<script type="text/javascript">
<!--
    function trim(str) {
	return str.replace(/^\s\s*/, '').replace(/\s\s*$/, '');
    }
    
    
    function trimcheck(str) {
	var r_first = /^\s\s*/;
	var r_last = /\s\s*$/;
	
	if (r_fist.test(str) || r_last.test(str)) {
	    return true;
	} else {
	    return false;
	}
    }

    
    function chkform() {
	var f = document.frm;
	
	/*
	 * Blowfish key size min 8bit, max 448bit, so 448 bit == 56 Byte == 112 Hex chars.
	 * function Generate_key() return 64 Hex Chars, so 32byte
	 * in order to debug easy, add a newline after 64 chars
	*/
        var strvalue = Generate_key()+Generate_key();
        var strkey = strvalue.slice(0,64)+'\n'+strvalue.slice(64,112);

	var g = document.gofrm;
	return false;
    }
// -->
</script>
url : /testchcode/BlowfishMember02.jsp
소개 : Blowfish 이용한 섹션키를 사용한 회원 가입페이지 STEP2

