<%@ page language="java" session="true" autoFlush="true" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %><%
%><%@ page import="org.apache.commons.lang3.StringEscapeUtils" %><%
%><%@ page import="kr.pe.sinnori.common.weblib.WebCommonStaticFinalVars" %><%
%><jsp:useBean id="topmenu" class="java.lang.String" scope="request" /><%
%><jsp:useBean id="leftmenu" class="java.lang.String" scope="request" /><%
%><jsp:useBean id="errorMessage" class="java.lang.String" scope="request" /><%
%><jsp:useBean id="parmBoardId" class="java.lang.String" scope="request" /><%!
	public String escapeHtml(String str) {
		if (null == str) return "";
		return StringEscapeUtils.escapeHtml4(str).replaceAll("\r\n|\n", "<br/>");
	}
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
<h1>자유 게시판 - 글 작성하기</h1>
<br/><%
	if (null != errorMessage && !errorMessage.equals("")) {
%>
	<div>
		<ul>
		<li>
			<dl>
				<dt>에러</dt>
				<dd><%=escapeHtml(errorMessage)%></dd>
			</dl>
		</li>
		</ul>		
	</div><%
	} else {
%>
<script type="text/javascript">
	function save() {
		var f = document.frm;
		
		if ('' == f.subject.value) {
			alert("제목을 넣어 주세요.");
			f.subject.focus();
			return;
		}

		if ('' == f.content.value) {
			alert("내용을 넣어 주세요.");
			f.content.focus();
			return;
		}


		var g = document.gofrm;
		g.subject.value = f.subject.value;
		g.content.value = f.content.value;
		g.sessionkeyBase64.value = sessionStorage.getItem('<%=WebCommonStaticFinalVars.SESSIONSTORAGE_SESSIONKEY_NAME%>');
		var iv = CryptoJS.lib.WordArray.random(<%=WebCommonStaticFinalVars.WEBSITE_IV_SIZE%>);
		g.ivBase64.value = CryptoJS.enc.Base64.stringify(iv);
		g.submit();
	}

	function cancel() {
		var g = document.cancelfrm;
		g.sessionkeyBase64.value = sessionStorage.getItem('<%=WebCommonStaticFinalVars.SESSIONSTORAGE_SESSIONKEY_NAME%>');
		var iv = CryptoJS.lib.WordArray.random(<%=WebCommonStaticFinalVars.WEBSITE_IV_SIZE%>);
		g.ivBase64.value = CryptoJS.enc.Base64.stringify(iv);
		g.submit();
	}
</script>
<form name=gofrm method="post" action="/servlet/BoardWrite">
<input type="hidden" name="topmenu" value="<%=topmenu%>" />
<input type="hidden" name="pageMode" value="proc" />
<input type="hidden" name="boardId" value="<%=parmBoardId%>" />
<input type="hidden" name="subject" />
<input type="hidden" name="content" />
<input type="hidden" name="sessionkeyBase64" />
<input type="hidden" name="ivBase64" />
</form>

<form name=cancelfrm method="post" action="/servlet/BoardList">
<input type="hidden" name="topmenu" value="<%=topmenu%>" />
<input type="hidden" name="boardId" value="<%=parmBoardId%>" />
<input type="hidden" name="sessionkeyBase64" />
<input type="hidden" name="ivBase64" />
</form>
<form name=frm onSubmit="return false">
	<div>
		<ul>
		<li>
			<dl>
				<dt>제목</dt>
				<dd><input type="text" name="subject" size="50" /></dd>
			</dl>
		</li>
		<li>
			<dl>
				<dt>내용</dt>
				<dd><textarea name="content" style="width: 500px; height: 220px;"></textarea></dd>
			</dl>
		</li>
		<li>
			<dl>
				<dt>기능</dt>
				<dd><input type="button" onClick="save()" value="저장" /> <input type="button" onClick="cancel()" value="취소" /></dd>
			</dl>
		</li>
		</ul>	
	</div>
</form><%
	}
%>
