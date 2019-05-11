<%@page import="kr.pe.codda.common.etc.CommonStaticFinalVars"%><%
%><%@page import="kr.pe.codda.weblib.common.AccessedUserInformation"%><%
%><%@page import="kr.pe.codda.weblib.common.PermissionType"%><%	
%><%@page import="kr.pe.codda.weblib.common.BoardListType"%><%	
%><%@page import="java.util.List"%><%
%><%@page import="kr.pe.codda.weblib.htmlstring.StringEscapeActorUtil.STRING_REPLACEMENT_ACTOR_TYPE"%><%
%><%@page import="kr.pe.codda.weblib.htmlstring.StringEscapeActorUtil"%><%
%><%@ page import="kr.pe.codda.weblib.common.WebCommonStaticFinalVars" %><%	
%><%@ page import="kr.pe.codda.impl.message.BoardListRes.BoardListRes" %><%
%><%@ page extends="kr.pe.codda.weblib.jdf.AbstractUserJSP" language="java" session="true" autoFlush="true" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %><%
%><jsp:useBean id="boardListRes" class="kr.pe.codda.impl.message.BoardListRes.BoardListRes" scope="request" /><%//String boardListResJsonString = new Gson().toJson(boardListRes);

	/* {
	String requestUserID = "guest";
	boardListRes.setBoardID(BoardType.FREE.getBoardID());
	boardListRes.setPageNo(1);
	boardListRes.setPageSize(20);
	
	boardListRes.setTotal(5);
	
	List<BoardListRes.Board> boardList = new ArrayList<BoardListRes.Board>();
	{		
		for (long boardNo=5; boardNo >= 1; boardNo--) {
	BoardListRes.Board board = new BoardListRes.Board();
	board.setBoardNo(boardNo);
	board.setGroupNo(boardNo);
	board.setGroupSeq(0);
	board.setParentNo(0);
	board.setDepth((short)0);
	board.setWriterID("test01");
	board.setViewCount(0);
	board.setBoardSate(BoardStateType.OK.getValue());
	// yyyy-mm-dd hh:mm:ss
	board.setRegisteredDate(Timestamp.valueOf("2018-09-22 13:00:01"));
	board.setNickname("테스트아이디01");
	board.setVotes(0);
	board.setSubject("게시글"+boardNo);
	board.setLastModifiedDate(board.getRegisteredDate());
	
	boardList.add(board);
		}
		
	}
	
	
	boardListRes.setCnt(boardList.size());
	boardListRes.setBoardList(boardList);
	} */	
	
	
	AccessedUserInformation accessedUserformation = getAccessedUserInformationFromSession(request);
	
	BoardListType boardListType = BoardListType.valueOf(boardListRes.getBoardListType());		
	PermissionType boardWritePermissionType = PermissionType.valueOf(boardListRes.getBoardWritePermissionType());%><!DOCTYPE html>
<html lang="ko">
<head>
<meta charset="UTF-8">
<meta http-equiv="X-UA-Compatible" content="IE=edge">
<meta name="viewport" content="width=device-width, initial-scale=1">
<title><%=WebCommonStaticFinalVars.USER_WEBSITE_TITLE%></title>
<!-- Latest compiled and minified CSS -->
<link rel="stylesheet" href="/bootstrap/3.3.7/css/bootstrap.css">
<!-- jQuery library -->
<script src="/jquery/3.3.1/jquery.min.js"></script>
<!-- Latest compiled JavaScript -->
<script src="/bootstrap/3.3.7/js/bootstrap.min.js"></script>

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

<script src="/js/common.js"></script>

<script type='text/javascript'>
	function buildPrivateKey() {
		var privateKey = CryptoJS.lib.WordArray.random(<%= WebCommonStaticFinalVars.WEBSITE_PRIVATEKEY_SIZE %>);	
		return privateKey;
	}
	
	function putNewPrivateKeyToSessionStorage() {
		var newPrivateKey = buildPrivateKey();
		var newPrivateKeyBase64 = CryptoJS.enc.Base64.stringify(newPrivateKey);
		
		sessionStorage.setItem('<%= WebCommonStaticFinalVars.SESSIONSTORAGE_KEY_NAME_OF_PRIVATEKEY %>', newPrivateKeyBase64);
		
		return newPrivateKeyBase64;
	}
	
	function getPrivateKeyFromSessionStorage() {
		var privateKeyBase64 = sessionStorage.getItem('<%=WebCommonStaticFinalVars.SESSIONSTORAGE_KEY_NAME_OF_PRIVATEKEY%>');
		
		if (null == privateKeyBase64) {			
			privateKeyBase64 = putNewPrivateKeyToSessionStorage();
		}
		
		var privateKey = null;
		try {
			privateKey = CryptoJS.enc.Base64.parse(privateKeyBase64);
		} catch(err) {
			console.log(err);
			throw err;
		}
		
		return privateKey;
	}

	function getSessionkeyBase64FromSessionStorage() {
		var privateKeyBase64 = sessionStorage.getItem('<%=WebCommonStaticFinalVars.SESSIONSTORAGE_KEY_NAME_OF_PRIVATEKEY%>');
		
		if (null == privateKeyBase64) {
			privateKeyBase64 = putNewPrivateKeyToSessionStorage();
		}
		
		
		var rsa = new RSAKey();	
		rsa.setPublic("<%= getModulusHexString(request) %>", "10001");
		
		var sessionKeyHex = rsa.encrypt(privateKeyBase64);
		var sessionKey = CryptoJS.enc.Hex.parse(sessionKeyHex);
		return CryptoJS.enc.Base64.stringify(sessionKey);
	}
	
	function buildIV() {
		var iv = CryptoJS.lib.WordArray.random(<%= WebCommonStaticFinalVars.WEBSITE_IV_SIZE %>);
		return iv;
	}
		
	function writeBoard() {		
		var f = document.writeInputFrm;		
		
		if ('' == f.subject.value) {
			alert("제목을 넣어 주세요.");
			f.subject.focus();
			return;
		}

		if ('' == f.contents.value) {
			alert("내용을 넣어 주세요.");
			f.contents.focus();
			return;
		}
		
		if (f.pwd != undefined) {
			try {
				checkValidPwd('게시글', f.pwd.value);
			} catch(err) {
				alert(err);
				f.pwd.focus();
				return;
			}
			
			try {
				checkValidPwdConfirm('게시글', f.pwd.value, f.pwdConfirm.value);
			} catch(err) {
				alert(err);
				f.pwd.focus();
				return;
			}
		}		
		
		var symmetricKeyObj = CryptoJS.<%= WebCommonStaticFinalVars.WEBSITE_JAVASCRIPT_SYMMETRIC_KEY_ALGORITHM_NAME %>;		
		var privateKey = getPrivateKeyFromSessionStorage();
		var iv = buildIV();

		var g = document.writeProcessFrm;	
		
		var newFileListDivNode = document.getElementById('newAttachedFileList');
		var uploadFileCnt = newFileListDivNode.childNodes.length;	
		
		if (uploadFileCnt > _ATTACHED_FILE_MAX_COUNT) {
			alert("업로드 할 수 있는 파일 갯수는 최대["+_ATTACHED_FILE_MAX_COUNT+"] 까지 입니다.");
			return;
		}
			
		for (var i=0; i < newFileListDivNode.childNodes.length; i++) {				
			var fileInput = newFileListDivNode.childNodes[i].childNodes[0].childNodes[0];
			
			if (1 == newFileListDivNode.childNodes.length) {
				if (g.newAttachedFile.value == '') {
					alert("첨부 파일을 선택하세요");
					return;
				}
			} else {
				if (g.newAttachedFile[i].value == '') {
					alert(fileInput.getAttribute("title")+"을 선택하세요");
					return;
				}
			}			
		}
		
		g.<%= WebCommonStaticFinalVars.PARAMETER_KEY_NAME_OF_SESSION_KEY %>.value = getSessionkeyBase64FromSessionStorage();	
		g.<%= WebCommonStaticFinalVars.PARAMETER_KEY_NAME_OF_SESSION_KEY_IV %>.value = CryptoJS.enc.Base64.stringify(iv);
		
		g.subject.value = f.subject.value;
		g.contents.value = f.contents.value;
		
		if (f.pwd != undefined) {
			var symmetricKeyObj = CryptoJS.AES;		
			g.pwd.value = symmetricKeyObj.encrypt(f.pwd.value, getPrivateKeyFromSessionStorage(), { mode: CryptoJS.mode.CBC, padding: CryptoJS.pad.Pkcs7, iv: iv });
		}

		g.submit()
	}
	
	function callBackForBoardWriteProcess(boardWriteResObj) {
		alert("게시글 작성이 완료되었습니다");		
		goListPage(1);
	}	
	
	
	function showWriteEditScreen() {	
		var writePartObj = document.getElementById('editScreenOfBoard0');
		writePartObj.style.display = "block";
		
		var f = document.writeInputFrm;	
		f.reset();
		
		var newFileListDivNode = document.getElementById('newAttachedFileList');
		
		while(newFileListDivNode.hasChildNodes()) {
			newFileListDivNode.removeChild(newFileListDivNode.firstChild);
		}
	}
	
	function hideWriteEditScreen() {	
		var writePartObj = document.getElementById('editScreenOfBoard0');
		writePartObj.style.display = "none";
	}
	
	
	function goDetailPage(boardID, boardNo, isTreeTypeList) {
		var detailPageURL = "/servlet/BoardDetail?boardID="+boardID+"&boardNo="+boardNo;
		
		if (isTreeTypeList) {
			window.open(detailPageURL, "", "width=800,height=600");
		}  else {
			document.location.href = detailPageURL;
		}		
	}

	
	function goListPage(pageNo) {
		var iv = buildIV();
		
		var g = document.listwriteInputFrm;
		
		g.<%= WebCommonStaticFinalVars.PARAMETER_KEY_NAME_OF_SESSION_KEY %>.value = getSessionkeyBase64FromSessionStorage();		
		g.<%= WebCommonStaticFinalVars.PARAMETER_KEY_NAME_OF_SESSION_KEY_IV %>.value = CryptoJS.enc.Base64.stringify(iv);
		
		
		g.pageNo.value = pageNo;		
		g.submit();
	}
	
	function addNewAttachedFile(f) {		
		var prefixOfNewChildDiv = 'newAttachedFileRowDiv';		

		var newFileListDivNode = document.getElementById('newAttachedFileList');		
		var oldFileListDivNode = document.getElementById('oldAttachedFileList');
		
		var uploadFileCnt;
		
		if (oldFileListDivNode == undefined) {
			uploadFileCnt = newFileListDivNode.childNodes.length;
		} else {
			uploadFileCnt = oldFileListDivNode.childNodes.length + newFileListDivNode.childNodes.length;
		}
			
		if (uploadFileCnt >= _ATTACHED_FILE_MAX_COUNT) {
			alert("업로드 할 수 있는 파일 갯수는 최대["+_ATTACHED_FILE_MAX_COUNT+"] 까지 입니다.");
			return;
		}
		
		var newAttachedFileRowSeq = parseInt(f.newAttachedFileRowSeq.value, 10);

		var attachedFileRowDivNode = makeNewAttachedFileRowDiv(prefixOfNewChildDiv+newAttachedFileRowSeq);
		
		newFileListDivNode.appendChild(attachedFileRowDivNode);
		
		newAttachedFileRowSeq++;
		f.newAttachedFileRowSeq.value = newAttachedFileRowSeq;
	}

	function makeNewAttachedFileRowDiv(attachedFileRowDivID) {
		var attachedFileRowDivNode = document.createElement("div");
		attachedFileRowDivNode.setAttribute("class", "row");
		attachedFileRowDivNode.setAttribute("id", attachedFileRowDivID);		
		
		var attachedFileNode  = document.createElement("INPUT");
		attachedFileNode .setAttribute("type", "file");
		attachedFileNode .setAttribute("class", "form-control");
		attachedFileNode .setAttribute("title", "첨부파일('"+attachedFileRowDivID+"')");
		attachedFileNode .setAttribute("name", "newAttachedFile");
		
		var attachedFileColDivNode = document.createElement("div");
		attachedFileColDivNode.setAttribute("class", "col-sm-10");
		
		attachedFileColDivNode.appendChild(attachedFileNode);
		
		var deleteButtonNode = document.createElement("INPUT");
		deleteButtonNode.setAttribute("type", "button");
		deleteButtonNode.setAttribute("value", "삭제");
		deleteButtonNode.setAttribute("title", "첨부파일('"+attachedFileRowDivID+"') 삭제");
		deleteButtonNode.setAttribute("onclick", "removeNewAttachFile('"+attachedFileRowDivID+"')");
		
		var buttonColDivNode = document.createElement("div");
		buttonColDivNode.setAttribute("class", "col-*-*");
		
		buttonColDivNode.appendChild(deleteButtonNode);
		
		attachedFileRowDivNode.appendChild(attachedFileColDivNode);
		attachedFileRowDivNode.appendChild(buttonColDivNode);
		
		return attachedFileRowDivNode;
	}
		
	function removeNewAttachFile(selectedDivID) {
		var newFileListDivNode = document.getElementById('newAttachedFileList');		
		var selectedDivNode = document.getElementById(selectedDivID);
		newFileListDivNode.removeChild(selectedDivNode);
	}
	
	function clickHiddenFrameButton(thisObj) {		
		var hiddenFrameObj = document.getElementById("hiddenFrame");
		
		if (hiddenFrameObj.style.display == 'none') {
			thisObj.innerText = "Hide Hidden Frame";
			hiddenFrameObj.style.display = "block";			
		} else {
			thisObj.innerText = "Show Hidden Frame";
			hiddenFrameObj.style.display = "none";
		}
	}	

	function init() {
		if(typeof(sessionStorage) == "undefined") {
		    alert("Sorry! No HTML5 sessionStorage support..");
		    document.location.href = "/";
		    return;
		}
	}
	
	window.onload = init;
</script>
</head>
<body>
<div class="header">
	<div class="container">
<%= getMenuNavbarString(request) %>
	</div>
</div>

<form name=listwriteInputFrm method="post" action="/servlet/BoardList">
<input type="hidden" name="boardID" value="<%=boardListRes.getBoardID()%>" />
<input type="hidden" name="pageNo" />
<input type="hidden" name="<%= WebCommonStaticFinalVars.PARAMETER_KEY_NAME_OF_SESSION_KEY %>" />
<input type="hidden" name="<%= WebCommonStaticFinalVars.PARAMETER_KEY_NAME_OF_SESSION_KEY_IV %>" />
</form>
<div class="content">
	<div class="container">
		<div class="panel panel-default">
			<div class="panel-heading"><h4><%= boardListRes.getBoardName() %> 게시판</h4></div>
			<div class="panel-body">
				<div class="btn-group"><%
	if (PermissionType.MEMBER.equals(boardWritePermissionType)) {
		/** 본문 작성 권한이 회원인 경우 */
		if (accessedUserformation.isLoginedIn()) {
			out.write(CommonStaticFinalVars.NEWLINE);
			out.write("					");
			out.write("<button type=\"button\" class=\"btn btn-primary btn-sm\" onClick=\"showWriteEditScreen();\">글 작성하기</button>");
		}
	} else if (PermissionType.ADMIN.equals(boardWritePermissionType)) {
		/** 본문 작성 권한이 관리자인 경우 */
		if (accessedUserformation.isAdmin()) {
			out.write(CommonStaticFinalVars.NEWLINE);
			out.write("					");
			out.write("<button type=\"button\" class=\"btn btn-primary btn-sm\" onClick=\"showWriteEditScreen();\">글 작성하기</button>");
		}
	} else if (PermissionType.GUEST.equals(boardWritePermissionType)) {
		/** 본문 작성 권한이 손님인 경우 */
		out.write(CommonStaticFinalVars.NEWLINE);
		out.write("					");
		out.write("<button type=\"button\" class=\"btn btn-primary btn-sm\" onClick=\"showWriteEditScreen();\">글 작성하기</button>");
	}
%>
					<button type="button" class="btn btn-primary btn-sm" onClick="clickHiddenFrameButton(this);">Show Hidden Frame</button>
				</div>			 
				<div id="resultMessage"></div>
				<br>
				<div id="listPartView">
					<div class="row">
						<div class="col-sm-1" style="background-color:lavender;">번호</div>
						<div class="col-sm-3" style="background-color:lavender;">제목</div>
						<div class="col-sm-2" style="background-color:lavender;">작성자</div>
						<div class="col-sm-1" style="background-color:lavender;">조회수</div>
						<div class="col-sm-1" style="background-color:lavender;">추천수</div>
						<div class="col-sm-2" style="background-color:lavender;">최초 작성일</div>
						<div class="col-sm-2" style="background-color:lavender;">마지막 수정일</div>
					</div><%
	List<BoardListRes.Board> boardList = boardListRes.getBoardList();
	if (null == boardList || boardList.isEmpty()) {
%>
					<div class="row">
						<div class="col-sm-12" align="center">조회 결과가 없습니다</div>
					</div><%
	} else {
		for (BoardListRes.Board board : boardList) {
			int depth = board.getDepth();
%>
					<div class="row">
						<div class="col-sm-1"><%=board.getBoardNo()%></div>
						<div class="col-sm-3"><%
			if (depth > 0) {
				for (int i=0; i < depth; i++) {
					out.print("&nbsp;&nbsp;&nbsp;&nbsp;");
				}
				out.print("ㄴ");
			}
%><a href="#" onClick="goDetailPage(<%=boardListRes.getBoardID()%>, <%=board.getBoardNo()%>, <%=BoardListType.TREE.equals(boardListType)%>)"><%=StringEscapeActorUtil.replace(board.getSubject(), STRING_REPLACEMENT_ACTOR_TYPE.ESCAPEHTML4)%></a></div>
						<div class="col-sm-2"><%=StringEscapeActorUtil.replace(board.getWriterNickname(), STRING_REPLACEMENT_ACTOR_TYPE.ESCAPEHTML4)%></div>
						<div class="col-sm-1"><%=board.getViewCount()%></div>
						<div class="col-sm-1"><%=board.getVotes()%></div>
						<div class="col-sm-2"><%=board.getRegisteredDate()%></div>
						<div class="col-sm-2"><%=board.getLastModifiedDate()%></div>
					</div><%
		}
	}

	if (boardListRes.getTotal() > 1) {
		final int pageNo = boardListRes.getPageNo();
		final int pageSize = boardListRes.getPageSize();
		
		// long pageNo = boardListRes.getPageOffset() / boardListRes.getPageLength() + 1;
		
		long startPageNo = 1 + WebCommonStaticFinalVars.WEBSITE_BOARD_PAGE_LIST_SIZE*(long)((pageNo - 1) / WebCommonStaticFinalVars.WEBSITE_BOARD_PAGE_LIST_SIZE);
		long endPageNo = Math.min(startPageNo + WebCommonStaticFinalVars.WEBSITE_BOARD_PAGE_LIST_SIZE, 
		(boardListRes.getTotal() + pageSize - 1) / pageSize);
		
		
		out.write(CommonStaticFinalVars.NEWLINE);
		out.write("					");
		out.write("<ul class=\"pagination pagination-sm\">");
		
		if (startPageNo > 1) {
			out.write(CommonStaticFinalVars.NEWLINE);
			out.write("						");
			out.write("<li class=\"previous\"><a href=\"#\" onClick=\"goListPage(");
			out.write(String.valueOf(startPageNo-1));
			out.write(")\">이전</a></li>");
		} else {
			out.write(CommonStaticFinalVars.NEWLINE);
			out.write("						");
			out.write("<li class=\"disabled previous\"><a href=\"#\">이전</a></li>");
		}
		
		for (int i=0; i < WebCommonStaticFinalVars.WEBSITE_BOARD_PAGE_LIST_SIZE; i++) {
			long workingPageNo = startPageNo + i;
			if (workingPageNo > endPageNo) break;

			if (workingPageNo == pageNo) {
				out.write(CommonStaticFinalVars.NEWLINE);
				out.write("						");
				out.write("<li class=\"active\"><a href=\"#\">");
				out.write(String.valueOf(workingPageNo));
				out.write("</a></li>");
			} else {
				out.write(CommonStaticFinalVars.NEWLINE);
				out.write("						");
				out.write("<li><a href=\"#\" onClick=\"goListPage(");
				out.write(String.valueOf(workingPageNo));
				out.write(")\">");
				out.write(String.valueOf(workingPageNo));
				out.write("</a></li>");
			}
		}
		
		if (startPageNo+WebCommonStaticFinalVars.WEBSITE_BOARD_PAGE_LIST_SIZE <= endPageNo) {
			out.write(CommonStaticFinalVars.NEWLINE);
			out.write("						");
			out.write("<li class=\"next\"><a href=\"#\" onClick=\"goListPage(");
			out.write(String.valueOf(startPageNo+WebCommonStaticFinalVars.WEBSITE_BOARD_PAGE_LIST_SIZE));
			out.write(")\">다음</a></li>");
		} else {
			out.write(CommonStaticFinalVars.NEWLINE);
			out.write("						");
			out.write("<li class=\"disabled next\"><a href=\"#\">다음</a></li>");
		}
		
		out.write(CommonStaticFinalVars.NEWLINE);
		out.write("					");
		out.write("</ul>");
	}
%>				
				</div>
				<div id="editScreenOfBoard0" style="display:none">
					<form name="writeInputFrm" enctype="multipart/form-data" method="post" action="/servlet/BoardWriteProcess" onsubmit="return false;">
						<input type="hidden" name="newAttachedFileRowSeq" value="0" />
						<div class="form-group">
							<label for="subject">제목</label>
							<input type="text" name="subject" class="form-control" placeholder="Enter subject" />
							<label for="content">내용</label>
							<textarea name="contents" id="contentsInWritePart" class="form-control input-block-level" placeholder="Enter contents" rows="20"></textarea><%
								if (! accessedUserformation.isLoginedIn()) {
							%>
							<label for="content">게시글 비밀번호</label>
							<input type="password" class="form-control" placeholder="Enter password" name="pwd">
							<label for="content">비밀번호 확인</label>
							<input type="password" class="form-control" placeholder="Enter password" name="pwdConfirm"><%
								}
							%>
														
						</div>
					</form>
					<div class="btn-group">
						<input type="button" class="btn btn-default" onClick="writeBoard();" value="저장" />					
						<input type="button" class="btn btn-default" onClick="addNewAttachedFile(document.writeInputFrm);" value="첨부 파일 추가" />
						<input type="button" class="btn btn-default" onClick="hideWriteEditScreen();" value="닫기" />
					</div>
					<form name="writeProcessFrm" enctype="multipart/form-data" method="post" target="hiddenFrame" action="/servlet/BoardWriteProcess">
						<div class="form-group">
							<input type="hidden" name="boardID" value="<%=boardListRes.getBoardID()%>" />
							<input type="hidden" name="subject" />
							<input type="hidden" name="contents" /><%
	if (! accessedUserformation.isLoginedIn()) {
		out.write(CommonStaticFinalVars.NEWLINE);
		out.write("							");
		out.write("<input type=\"hidden\" name=\"pwd\" />");
	}
%>
							<input type="hidden" name="<%= WebCommonStaticFinalVars.PARAMETER_KEY_NAME_OF_SESSION_KEY %>" />
							<input type="hidden" name="<%= WebCommonStaticFinalVars.PARAMETER_KEY_NAME_OF_SESSION_KEY_IV %>" />					
							<!-- 주의점 div 시작 태그와 종료 태그 사이에는 공백을 포함한 어떠한 것도 넣지 말것, 자식 노드로 인식됨 -->
							<div id="newAttachedFileList"></div>
						</div>
					</form>				
				</div>
				<iframe id="hiddenFrame" name="hiddenFrame" style="display:none;"></iframe>		
			</div>			
		</div>
	</div>
</div>
</body>
</html>