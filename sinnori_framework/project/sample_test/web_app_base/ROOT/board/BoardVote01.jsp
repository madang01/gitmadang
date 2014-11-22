<%@ page extends="kr.pe.sinnori.common.weblib.AbstractJSP" language="java" session="true" autoFlush="true" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %><%
%><%@ page import="kr.pe.sinnori.common.weblib.WebCommonStaticFinalVars" %><%
%><jsp:useBean id="topmenu" class="java.lang.String" scope="request" /><%
%><jsp:useBean id="leftmenu" class="java.lang.String" scope="request" /><%
%><jsp:useBean id="parmIVBase64" class="java.lang.String" scope="request" /><%
%><jsp:useBean id="errorMessage" class="java.lang.String" scope="request" /><%
%><jsp:useBean id="parmBoardId" class="java.lang.String" scope="request" /><%
%><jsp:useBean id="parmBoardNo" class="java.lang.String" scope="request" /><%
%><jsp:useBean id="messageResultOutObj" class="kr.pe.sinnori.impl.message.MessageResult.MessageResult" scope="request" />
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
<h1>자유 게시판 - 추천 하기 결과</h1>
<br/><%
	if (null != errorMessage && !errorMessage.equals("")) {
%>
	<div>
		<ul>
		<li>
			<dl>
				<dt>에러</dt>
				<dd><%=escapeHtml(errorMessage, WebCommonStaticFinalVars.LINE2BR_STRING_REPLACER)%></dd>
			</dl>
		</li>
		</ul>		
	</div><%
	} else {
%>
<script type="text/javascript">
	
	function goList() {
		if(typeof(sessionStorage) == "undefined") {
		    alert("Sorry! No HTML5 sessionStorage support..");
		    return;
		}

		var g = document.listfrm;		
		g.submit();
	}
</script>

<form name=listfrm method="post" action="/servlet/BoardList">
<input type="hidden" name="topmenu" value="<%=topmenu%>" />
<input type="hidden" name="boardId" value="<%=parmBoardId%>" />
</form>
<form name=frm onSubmit="return false">	
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
				<dt>글 번호(<%=parmBoardNo%>) 추천 결과 내용</dt>
				<dd><%=escapeHtml(messageResultOutObj.getResultMessage(), WebCommonStaticFinalVars.LINE2BR_STRING_REPLACER)%></dd>
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