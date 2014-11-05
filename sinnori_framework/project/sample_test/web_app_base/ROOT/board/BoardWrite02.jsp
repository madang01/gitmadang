<%@ page extends="kr.pe.sinnori.common.weblib.AbstractJSPBase" language="java" session="true" autoFlush="true" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %><%
%><%@ page import="kr.pe.sinnori.common.weblib.WebCommonStaticFinalVars" %><%
%><jsp:useBean id="topmenu" class="java.lang.String" scope="request" /><%
%><jsp:useBean id="leftmenu" class="java.lang.String" scope="request" /><%
%><jsp:useBean id="errorMessage" class="java.lang.String" scope="request" /><%
%><jsp:useBean id="parmBoardId" class="java.lang.String" scope="request" /><%
%><jsp:useBean id="messageResultOutObj" class="kr.pe.sinnori.impl.message.MessageResult.MessageResult" scope="request" />
<h1>자유 게시판 - 글 저장 결과</h1>
<br/><%
	if (null != errorMessage && !errorMessage.equals("")) {
%>
	<div>
		<ul>
		<li>
			<dl>
				<dt>에러</dt>
				<dd><%=escapeHtml(errorMessage, true)%></dd>
			</dl>
		</li>
		</ul>		
	</div><%
	} else {
%>
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
<script type="text/javascript">
	function goList() {
		var g = document.listfrm;
		g.submit();
	}
</script>
<form name=listfrm method="post" action="/servlet/BoardList">
<input type="hidden" name="topmenu" value="<%=topmenu%>" />
<input type="hidden" name="boardId" value="<%=parmBoardId%>" />
</form>
<form name=frm onsubmit="return false">
	<div>
		<ul>
		<li>
			<dl>
				<dt>성공 여부</dt>
				<dd><%=messageResultOutObj.getIsSuccess()%></dd>
			</dl>
		</li>
		<li>
			<dl>
				<dt>처리 결과 내용</dt>
				<dd><%=escapeHtml(messageResultOutObj.getResultMessage(), true)%></dd>
			</dl>
		</li>
		<li>
			<dl>
				<dt>기능</dt>
				<dd><input type="button" onClick="goList()" value="목록으로..." /></dd>
			</dl>
		</li>
		</ul>	
	</div>
</form><%
	}
%>
