<%@ page extends="kr.pe.sinnori.weblib.jdf.AbstractJSP" language="java" session="true" autoFlush="true" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %><%
%><%@ page import="kr.pe.sinnori.weblib.common.WebCommonStaticFinalVars" %><%
%><%@page import="kr.pe.sinnori.weblib.htmlstring.HtmlStringUtil"%><%
%><jsp:useBean id="parmIVBase64" class="java.lang.String" scope="request" /><%
	
	kr.pe.sinnori.common.sessionkey.SymmetricKey webUserSymmetricKey = 
(kr.pe.sinnori.common.sessionkey.SymmetricKey)request.getAttribute("webUserSymmetricKey");
	
	String orignalMessage = "원문에 있는 이 문구가 복호문에서 잘 보시이면 " 
+ "AbstractSessionKeyServlet 모듈 테스트 통과 안보이면 실패\n<script type=\"text/javascript\">alert(\"hello\");</script>";
%>
<script type="text/javascript" src="/js/cryptoJS/rollups/aes.js"></script>
<script type="text/javascript" src="/js/cryptoJS/components/core-min.js"></script>
<script type="text/javascript" src="/js/cryptoJS/components/cipher-core-min.js"></script>
<h1>JDF 테스트 - 세션키</h1>
<ul>
	<li>이 페이지는 AbstractSessionKeyServlet 를 상속 받은 페이지입니다.<br/>
AbstractSessionKeyServlet 는  세션키 운영에 필요한 파라미터를 요구하며<br/>
없다면 파라미터 값들을 보존하며 세션키에 해당하는 파라미터 값들을 자동적으로 가져오는 페이지를 통해 가져옵니다.<br/>
자동으로 가져올때 만약 HTML sessionStorage 에 세션키 관련 값들이 없다면 역시 자동생성한다.<br/>
주) 파라미터 값들을 보존할때 암호화를 하지 않습니다.
	</li>
	<li><h2>AbstractSessionKeyServlet 모듈 테스트</h2>
		<table border="1">
		<tr>
			<td>원문</td><td>복호문</td>
		</tr>
		<tr>
			<td><%=HtmlStringUtil.toHtml4BRString(orignalMessage)%></td><td id="idTxtResultMessage"></td>
		</tr>
		</table>
	</li>
</ul>
<script type="text/javascript">
<!--
	var pageIV = CryptoJS.enc.Base64.parse("<%=parmIVBase64%>");	
	
	var privateKey = CryptoJS.enc.Base64.parse(sessionStorage.getItem('<%=WebCommonStaticFinalVars.SESSIONSTORAGE_KEY_PRIVATEKEY_NAME%>'));
	
	var messageTxt = CryptoJS.AES.decrypt("<%= webUserSymmetricKey.encryptStringBase64(HtmlStringUtil.toScriptString(orignalMessage)) %>", privateKey, { mode: CryptoJS.mode.CBC, padding: CryptoJS.pad.Pkcs7, iv: pageIV });
		
	document.getElementById('idTxtResultMessage').innerHTML = messageTxt.toString(CryptoJS.enc.Utf8);
//-->
</script>

