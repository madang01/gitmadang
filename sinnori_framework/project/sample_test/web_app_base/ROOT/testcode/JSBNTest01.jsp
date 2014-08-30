<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<jsp:useBean id="modulusHex" class="java.lang.String" scope="request" /><%
	String topmenu = request.getParameter("topmenu");
	if (null ==  topmenu) topmenu="";
%>
<script type="text/javascript" src="/js/jsbn/jsbn.js"></script>
<script type="text/javascript" src="/js/jsbn/jsbn2.js"></script>
<script type="text/javascript" src="/js/jsbn/prng4.js"></script>
<script type="text/javascript" src="/js/jsbn/rng.js"></script>
<script type="text/javascript" src="/js/jsbn/rsa.js"></script>
<script type="text/javascript" src="/js/jsbn/rsa2.js"></script>
<script type="text/javascript">
<!--
	function chkform() {
	
		var f = document.frm;
		var g = document.gofrm;
		
		var rsa = new RSAKey();
		rsa.setPublic("<%=modulusHex%>", "10001");		
		g.plainText.value = f.plainText.value;
		g.encryptedBytesWithPublicKey.value = rsa.encrypt(f.plainText.value);
		g.submit();
		
		return false;
	}
//-->
</script>

<form method="post" name="gofrm" target="_top">
<input type="hidden" name="topmenu" value="<%=topmenu%>" />
<input type="hidden" name="pagegubun" value="step2" />
<input type="hidden" name="encryptedBytesWithPublicKey" />
<input type="hidden" name="plainText" />
</form>
<form method="post" name="frm" onsubmit="return chkform();">
	<table>
	<tr><td colspan="2"><h3>RSA 암/복호화 테스트</h3></td></tr>
	<tr>
	    <td>평문</td>
	    <td><input type="text" name="plainText" size="15" maxlength="15" /></td> 
	</tr>
	<tr> 
	    <td colspan="2"><input type="submit" value="확인" /></td>
	</tr>
	</table>
</form>	

