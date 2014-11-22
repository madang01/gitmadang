<%@ page extends="kr.pe.sinnori.common.weblib.AbstractJSP" language="java" session="true" autoFlush="true" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %><%
%><%@ page import="kr.pe.sinnori.common.weblib.WebCommonStaticFinalVars" %><%
%><jsp:useBean id="topmenu" class="java.lang.String" scope="request" /><%
%><jsp:useBean id="leftmenu" class="java.lang.String" scope="request" /><%
%><jsp:useBean id="parmIVBase64" class="java.lang.String" scope="request" /><%
%><jsp:useBean id="errorMessage" class="java.lang.String" scope="request" /><%
%><jsp:useBean id="parmBoardId" class="java.lang.String" scope="request" /><%
%><jsp:useBean id="parmBoardNo" class="java.lang.String" scope="request" /><%
%><jsp:useBean id="boardDetailOutDTO" class="kr.pe.sinnori.impl.message.BoardDetailOutDTO.BoardDetailOutDTO" scope="request" />
<style>
<!--
table {
	border:solid 1px;
	border-color:black;
	border-collapse:collapse;
}
thead {
	height : 30px;
	text-align:center;
}
tbody {
	height : 20px;
	text-align:center;
}
-->
</style>
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
<h1>자유 게시판 - 상세 보기</h1>
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
	function goModify() {
		if(typeof(sessionStorage) == "undefined") {
		    alert("Sorry! No HTML5 sessionStorage support..");
		    return;
		}

		var g = document.goModofyForm;
		g.sessionkeyBase64.value = sessionStorage.getItem('<%=WebCommonStaticFinalVars.SESSIONSTORAGE_SESSIONKEY_NAME%>');
		var iv = CryptoJS.lib.WordArray.random(<%=WebCommonStaticFinalVars.WEBSITE_IV_SIZE%>);
		g.ivBase64.value = CryptoJS.enc.Base64.stringify(iv);
		g.submit();
	}

	function goReply() {
		if(typeof(sessionStorage) == "undefined") {
		    alert("Sorry! No HTML5 sessionStorage support..");
		    return;
		}

		var g = document.goReplyForm;
		g.sessionkeyBase64.value = sessionStorage.getItem('<%=WebCommonStaticFinalVars.SESSIONSTORAGE_SESSIONKEY_NAME%>');
		var iv = CryptoJS.lib.WordArray.random(<%=WebCommonStaticFinalVars.WEBSITE_IV_SIZE%>);
		g.ivBase64.value = CryptoJS.enc.Base64.stringify(iv);
		g.submit();
	}

	function goVote() {
		if(typeof(sessionStorage) == "undefined") {
		    alert("Sorry! No HTML5 sessionStorage support..");
		    return;
		}

		var g = document.goVoteForm;
		g.sessionkeyBase64.value = sessionStorage.getItem('<%=WebCommonStaticFinalVars.SESSIONSTORAGE_SESSIONKEY_NAME%>');
		var iv = CryptoJS.lib.WordArray.random(<%=WebCommonStaticFinalVars.WEBSITE_IV_SIZE%>);
		g.ivBase64.value = CryptoJS.enc.Base64.stringify(iv);		
		g.submit();
	}

	
	function goList() {
		if(typeof(sessionStorage) == "undefined") {
		    alert("Sorry! No HTML5 sessionStorage support..");
		    return;
		}

		var g = document.goListForm;		
		g.submit();
	}

	function goDownload(attachId, attachSeq) {
		var g = document.goDownloadForm;
		g.attachId.value = attachId;
		g.attachSeq.value = attachSeq;
		g.submit();
	}
</script>
<form name=goModofyForm method="post" action="/servlet/BoardModify">
<input type="hidden" name="topmenu" value="<%=topmenu%>" />
<input type="hidden" name="pageMode" value="view" />
<input type="hidden" name="boardId" value="<%=parmBoardId%>" />
<input type="hidden" name="boardNo" value="<%=parmBoardNo%>" />
<input type="hidden" name="sessionkeyBase64" />
<input type="hidden" name="ivBase64" />
</form>

<form name=goReplyForm method="post" action="/servlet/BoardReply">
<input type="hidden" name="topmenu" value="<%=topmenu%>" />
<input type="hidden" name="pageMode" value="view" />
<input type="hidden" name="boardId" value="<%=parmBoardId%>" />
<input type="hidden" name="parentBoardNo" value="<%=parmBoardNo%>" />
<input type="hidden" name="sessionkeyBase64" />
<input type="hidden" name="ivBase64" />
</form>

<form name=goVoteForm method="post" action="/servlet/BoardVote">
<input type="hidden" name="topmenu" value="<%=topmenu%>" />
<input type="hidden" name="boardId" value="<%=parmBoardId%>" />
<input type="hidden" name="boardNo" value="<%=parmBoardNo%>" />
<input type="hidden" name="sessionkeyBase64" />
<input type="hidden" name="ivBase64" />
</form>

<form name=goListForm method="post" action="/servlet/BoardList">
<input type="hidden" name="topmenu" value="<%=topmenu%>" />
<input type="hidden" name="boardId" value="<%=parmBoardId%>" />
</form>

<form name=goDownloadForm target="downloadResultFrame" method="post" action="/servlet/BoardDownload">
<input type="hidden" name="topmenu" value="<%=topmenu%>" />
<input type="hidden" name="attachId" />
<input type="hidden" name="attachSeq" />
</form>
<form name=frm onSubmit="return false">
	<div><%
	if (isLogin(request)) {
		String userId = getUserId(request);
%><input type=button onClick="goReply()" value="댓글" />&nbsp;<%
		if (userId.equals(boardDetailOutDTO.getWriterId())) {
%><input type="button" onClick="goModify()" value="편집" />&nbsp;<%
		} else {
%><input type=button onClick="goVote()" value="추천" />&nbsp;<%
		}
	}
%><input type="button" onClick="goList()" value="목록으로" />
	</div>
	<br/>
	<div style="height:100%">
		<table border="1">
			<tbody>
			<tr>
				<td style="width:90px">작성자</td>
				<td style="width:90px"><%=boardDetailOutDTO.getNickname()%></td>
				<td style="width:70px">조회수</td>
				<td style="width:50px"><%=boardDetailOutDTO.getViewCount()%></td>
				<td style="width:70px">추천수</td>
				<td style="width:50px"><%=boardDetailOutDTO.getVotes()%></td>
				<td style="width:90px">최근 수정일</td>
				<td><%=boardDetailOutDTO.getModifiedDate()%></td>
			</tr><%
	java.util.List<kr.pe.sinnori.impl.message.BoardDetailOutDTO.BoardDetailOutDTO.AttachFile> attachFileList = boardDetailOutDTO.getAttachFileList();
	if (null != attachFileList) {
%>
			<tr>
				<td style="width:90px">첨부 파일</td>
				<td colspan="7" style="text-align:left;">
					<div><%
		for (kr.pe.sinnori.impl.message.BoardDetailOutDTO.BoardDetailOutDTO.AttachFile attachFile : attachFileList) {
			if (isLogin(request)) {
%><a href=# onClick="goDownload(<%=boardDetailOutDTO.getAttachId()%>, <%=attachFile.getAttachSeq()%>)"><%=escapeHtml(attachFile.getAttachFileName())%></a>&nbsp;<%
			} else {
%><%=escapeHtml(attachFile.getAttachFileName())%>&nbsp;<%
			}
		}
%>
					</div>
				</td>
			</tr><%

	}
%>
						
					

			<tr>
				<td>제목</td><td colspan="7" style="text-align:left;"><%=escapeHtml(boardDetailOutDTO.getSubject())%></td>
			</tr>
			<tr>
				<td>내용</td><td colspan="7" style="text-align:left;"><%=escapeHtml(boardDetailOutDTO.getContent(), WebCommonStaticFinalVars.LINE2BR_STRING_REPLACER)%></td>
			</tr>
			</tbody>
		</table>
	</div><br/>
	<div><%
	if (isLogin(request)) {
		String userId = getUserId(request);
%><input type=button onClick="goReply()" value="댓글" />&nbsp;<%
		if (userId.equals(boardDetailOutDTO.getWriterId())) {
%><input type="button" onClick="goModify()" value="편집" />&nbsp;<%
		} else {
%><input type=button onClick="goVote()" value="추천" />&nbsp;<%
		}
	}
%><input type="button" onClick="goList()" value="목록으로" />
	</div>
</form><%
	}
%>
<iframe name="downloadResultFrame" width="400" height="300" >
</iframe>
