<%@ page extends="kr.pe.sinnori.weblib.jdf.AbstractJSP" language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%><%
%><%@ page import="kr.pe.sinnori.weblib.common.WebCommonStaticFinalVars" %><%
	String topmenu = request.getParameter("topmenu");
	if (null ==  topmenu) topmenu="";
%>
<script type="text/javascript" src="/js/cryptoJS/rollups/aes.js"></script>
<script type="text/javascript" src="/js/cryptoJS/rollups/tripledes.js"></script>
<script type="text/javascript" src="/js/cryptoJS/components/core-min.js"></script>
<script type="text/javascript" src="/js/cryptoJS/components/cipher-core-min.js"></script>
<script type="text/javascript">
<!--
	function chkform() {
		var f = document.frm;
		var g = document.gofrm;		
		var encryptedBytes;
		 
		switch(f.algorithm.selectedIndex) {
			case 0:
				var privateKey = CryptoJS.lib.WordArray.random(16);		
				g.privateKey.value = CryptoJS.enc.Hex.stringify(privateKey);
				var iv = CryptoJS.lib.WordArray.random(16);
				g.iv.value = CryptoJS.enc.Hex.stringify(iv);
				encryptedBytes = CryptoJS.AES.encrypt(f.plainText.value, privateKey, { mode: CryptoJS.mode.CBC, padding: CryptoJS.pad.Pkcs7, iv: iv });
				break;
			case 1:
				var privateKey = CryptoJS.lib.WordArray.random(8);		
				g.privateKey.value = CryptoJS.enc.Hex.stringify(privateKey);
				var iv = CryptoJS.lib.WordArray.random(8);
				g.iv.value = CryptoJS.enc.Hex.stringify(iv);
				encryptedBytes = CryptoJS.DES.encrypt(f.plainText.value, privateKey, { mode: CryptoJS.mode.CBC, padding: CryptoJS.pad.Pkcs7, iv: iv });
				break;
			case 2:
				var privateKey = CryptoJS.lib.WordArray.random(24);		
				g.privateKey.value = CryptoJS.enc.Hex.stringify(privateKey);
				var iv = CryptoJS.lib.WordArray.random(8);
				g.iv.value = CryptoJS.enc.Hex.stringify(iv);
				encryptedBytes = CryptoJS.TripleDES.encrypt(f.plainText.value, privateKey, { mode: CryptoJS.mode.CBC, padding: CryptoJS.pad.Pkcs7, iv: iv });			  
				break;  
			default:
			 alert("unkown digest message algothm");
			 return false;
		}
		
		g.encryptedBytes.value = encryptedBytes.ciphertext;
		g.algorithm.value = f.algorithm.options[f.algorithm.selectedIndex].value;
		g.plainText.value = f.plainText.value;
		g.submit();
		
		return false;
	}
//-->
</script>

<form method="post" name="gofrm" target="_top">
<input type="hidden" name="topmenu" value="<%=topmenu%>" />
<input type="hidden" name="pagegubun" value="step2" />
<input type="hidden" name="algorithm" />
<input type="hidden" name="privateKey" />
<input type="hidden" name="iv" />
<input type="hidden" name="plainText" />
<input type="hidden" name="encryptedBytes" />
</form>
<form method="post" name="frm" onsubmit="return chkform();">
	<table>
	<tr><td colspan="2"><h3>CryptoJS 대칭키 테스트</h3></td></tr>
	<tr>
	    <td>대칭키 알고리즘</td>
	    <td>
	    	<select name="algorithm">
	    		<option value="AES">AES</option>
	    		<option value="DES">DES</option>
	    		<option value="DESede">DESede(=Triple DES)</option>
	    	</select>
	    </td> 
	</tr>
	<tr>
	    <td>평문</td>
	    <td><textarea name="plainText" size="15" maxlength="15" ></textarea></td> 
	</tr>
	<tr> 
	    <td colspan="2"><input type="submit" value="확인" /></td>
	</tr>
	</table>
</form>	

