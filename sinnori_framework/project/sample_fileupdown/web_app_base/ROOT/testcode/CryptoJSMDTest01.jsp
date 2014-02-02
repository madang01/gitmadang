<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%><%
	String topmenu = request.getParameter("topmenu");
	if (null ==  topmenu) topmenu="";
%>
<script type="text/javascript" src="/js/cryptoJS/rollups/md5.js"></script>
<script type="text/javascript" src="/js/cryptoJS/rollups/sha1.js"></script>
<script type="text/javascript" src="/js/cryptoJS/rollups/sha256.js"></script>
<script type="text/javascript" src="/js/cryptoJS/rollups/sha512.js"></script>
<script type="text/javascript">
<!--
	function chkform() {
		var f = document.frm;
		var g = document.gofrm;
		
		switch(f.algorithm.selectedIndex) {
			case 0:
			  g.javascriptMD.value = CryptoJS.MD5(f.plainText.value);
			  break;
			case 1:
			  g.javascriptMD.value = CryptoJS.SHA1(f.plainText.value);
			  break;
			case 2:
			  g.javascriptMD.value = CryptoJS.SHA256(f.plainText.value);			  
			  break; 
			case 3:
			  g.javascriptMD.value = CryptoJS.SHA512(f.plainText.value);			  
			  break;  
			default:
			 alert("unkown digest message algothm");
			 return false;
		}
		
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
<input type="hidden" name="javascriptMD" />
<input type="hidden" name="plainText" />
</form>
<form method="post" name="frm" onsubmit="return chkform();">
	<table>
	<tr><td colspan="2"><h3>CryptoJS 해시 알고리즘(=메세지 다이제스트) 테스트</h3></td></tr>
	<tr>
	    <td>해시 알고리즘(=메세지 다이제스트) 알고리즘</td>
	    <td>
	    	<select name="algorithm">
	    		<option value="MD5">MD5</option>
	    		<option value="SHA1">SHA1</option>
	    		<option value="SHA-256">SHA256</option>
	    		<option value="SHA-512">SHA512</option>
	    	</select>
	    </td> 
	</tr>
	<tr>
	    <td>평문</td>
	    <td><input type="text" name="plainText" size="15" maxlength="15" /></td> 
	</tr>
	<tr> 
	    <td colspan="2"><input type="submit" value="확인" /></td>
	</tr>
	</table>
</form>	

