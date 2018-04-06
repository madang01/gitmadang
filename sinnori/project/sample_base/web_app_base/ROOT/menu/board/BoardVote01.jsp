<%@ page extends="kr.pe.sinnori.weblib.jdf.AbstractJSP" language="java" session="true" autoFlush="true" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %><%
%><%@ page import="kr.pe.sinnori.weblib.htmlstring.HtmlStringUtil"%><%
%><%@ page import="kr.pe.sinnori.weblib.common.WebCommonStaticFinalVars" %><%
%><%@ page import="kr.pe.sinnori.weblib.common.BoardType"%><%
%><jsp:useBean id="errorMessage" class="java.lang.String" scope="request" /><%
%><jsp:useBean id="parmBoardId" class="java.lang.String" scope="request" /><%
%><jsp:useBean id="parmBoardNo" class="java.lang.String" scope="request" /><%
%><jsp:useBean id="messageResultRes" class="kr.pe.sinnori.impl.message.MessageResultRes.MessageResultRes" scope="request" />
<script type="text/javascript" src="/js/jsbn/jsbn.js"></script>
<script type="text/javascript" src="/js/jsbn/jsbn2.js"></script>
<script type="text/javascript" src="/js/jsbn/prng4.js"></script>
<script type="text/javascript" src="/js/jsbn/rng.js"></script>
<script type="text/javascript" src="/js/jsbn/rsa.js"></script>
<script type="text/javascript" src="/js/jsbn/rsa2.js"></script>
<script type="text/javascript" src="/js/cryptoJS/rollups/sha256.js"></script>
<script type="text/javascript" src="/js/cryptoJS/rollups/aes.js"></script>
<script type="text/javascript" src="/js/cryptoJS/components/core-min.js"></script>
<script type="text/javascript" src="/js/cryptoJS/components/cipher-core-min.js"></script>
<h1><%= BoardType.valueOf(Short.parseShort(parmBoardId)).getName() %> 게시판 - 추천 하기 결과</h1>
<script type="text/javascript">
<!--
	function init() {<%
	if (null != errorMessage && !errorMessage.equals("")) {
%>
		var voteResponse = {isError:true, message:"<%=HtmlStringUtil.toScriptString(errorMessage)%>"};<%
	} else {
%>
		var voteResponse = {isError:<%=messageResultRes.getIsSuccess()%>, message:"<%=HtmlStringUtil.toScriptString(messageResultRes.getResultMessage())%>"};<%
	}
%>
		parent.callbackVote(voteResponse);
	}
	window.onload = init;	
//-->
</script>

