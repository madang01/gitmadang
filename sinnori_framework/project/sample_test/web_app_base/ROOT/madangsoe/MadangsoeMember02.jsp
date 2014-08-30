<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<jsp:useBean id="topmenu" class="java.lang.String" scope="request" />
<jsp:useBean id="leftmenu" class="java.lang.String" scope="request" />
<jsp:useBean id="sessionKeyWebBean" class="kr.pe.sinnori.common.servlet.beans.SessionKeyWebBean" scope="request" />
<jsp:useBean id="pageIV" class="java.lang.String" scope="request" />
<jsp:useBean id="errorMessage" class="java.lang.String" scope="request" /><%
	kr.pe.sinnori.common.message.OutputMessage resultOutputMessage = 
		(kr.pe.sinnori.common.message.OutputMessage)request.getAttribute("resultOutputMessage");
		
	SymmetricKey webuserSymmetricKey = sessionKeyWebBean.getSymmetricKey();
%>
<script type="text/javascript" src="/js/cryptoJS/rollups/aes.js"></script>
<script type="text/javascript" src="/js/cryptoJS/components/core-min.js"></script>
<script type="text/javascript" src="/js/cryptoJS/components/cipher-core-min.js"></script>
<table>
	<tr><td><h3>회원가입 결과</h3></td></tr>
	<tr>
	    <td>eroror Message 복호문</td> 
	</tr>
	<tr>
	    <td id="idErrorMessage"></td> 
	</tr>
	<tr>
	    <td>eroror Message 원본</td> 
	</tr>	
	<tr>
	    <td><%=errorMessage %></td> 
	</tr><%
	if (null != resultOutputMessage) {
%>
	<tr>
	    <td>resultOutputMessage 복호문</td> 
	</tr>
	<tr>
	    <td id="idResultOutputMessage"></td> 
	</tr>
	<tr>
	    <td>resultOutputMessage 원본</td> 
	</tr>	
	<tr>
	    <td><%=resultOutputMessage.toString() %></td> 
	</tr><%
	}
%>
</table>

<script type="text/javascript">
<!--
	var pageIV = CryptoJS.enc.Base64.parse("<%=pageIV%>");
	var privateKey = CryptoJS.enc.Hex.parse(localStorage.sinnori_key);
	//var pageIV = CryptoJS.enc.Hex.parse("300490330fdf973614e10e935adbfd37");
	// var privateKey = CryptoJS.enc.Hex.parse("1f306b247f1c44d85ff3ebb8219c4203");
	// var iv = CryptoJS.enc.Base64.parse(pageIV);
	// alert(pageIV);	
	//var id = CryptoJS.enc.Hex.parse("b8b2b77c718c95f44c4eb4bdce8774dd");
	
	var errorMessage = CryptoJS.AES.decrypt("<%=webuserSymmetricKey.encryptStringBase64(errorMessage)%>", privateKey, { mode: CryptoJS.mode.CBC, padding: CryptoJS.pad.Pkcs7, iv: pageIV });
	document.getElementById('idErrorMessage').innerHTML = errorMessage.toString(CryptoJS.enc.Utf8);<%
	if (null != resultOutputMessage) {
%>	
	var resultOutputMessage = CryptoJS.AES.decrypt("<%=webuserSymmetricKey.encryptStringBase64(resultOutputMessage.toString())%>", privateKey, { mode: CryptoJS.mode.CBC, padding: CryptoJS.pad.Pkcs7, iv: pageIV });
	document.getElementById('idResultOutputMessage').innerHTML = resultOutputMessage.toString(CryptoJS.enc.Utf8);<%
	}
%>
//-->
</script>
