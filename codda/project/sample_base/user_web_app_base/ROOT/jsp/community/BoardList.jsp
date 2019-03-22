<%@page import="kr.pe.codda.common.etc.CommonStaticFinalVars"%><%
%><%@page import="kr.pe.codda.weblib.common.MemberRoleType"%><%
%><%@page import="kr.pe.codda.weblib.common.PermissionType"%><%
%><%@page import="kr.pe.codda.weblib.common.BoardListType"%><%
%><%@page import="java.util.List"%><%%><%@page import="kr.pe.codda.weblib.htmlstring.StringEscapeActorUtil.STRING_REPLACEMENT_ACTOR_TYPE"%><%%><%@page import="kr.pe.codda.weblib.htmlstring.StringEscapeActorUtil"%><%%><%@ page import="kr.pe.codda.weblib.common.WebCommonStaticFinalVars" %><%
%><%@ page import="kr.pe.codda.impl.message.BoardListRes.BoardListRes" %><%
%><%@ page extends="kr.pe.codda.weblib.jdf.AbstractUserJSP" language="java" session="true" autoFlush="true" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %><%
%><jsp:useBean id="boardListRes" class="kr.pe.codda.impl.message.BoardListRes.BoardListRes" scope="request" /><%
	//String boardListResJsonString = new Gson().toJson(boardListRes);

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
	
	
	
	
	BoardListType boardListType = BoardListType.valueOf(boardListRes.getBoardListType());		
	PermissionType boardWritePermissionType = PermissionType.valueOf(boardListRes.getBoardWritePermissionType());		
	
	boolean isUserLoginedIn = isUserLoginedIn(request);
	
%><!DOCTYPE html>
<html lang="ko">
<head>
<meta charset="UTF-8">
<meta http-equiv="X-UA-Compatible" content="IE=edge">
<meta name="viewport" content="width=device-width, initial-scale=1">
<title><%=WebCommonStaticFinalVars.USER_WEBSITE_TITLE%></title>
<!-- Latest compiled and minified CSS -->
<link rel="stylesheet" href="/bootstrap/3.3.7/css/bootstrap.css">
<style>
	textarea {
	  /* margin:0px 0px; this is redundant anyways since its specified below*/
	  padding-top:10px;
	  padding-bottom:25px; /* increased! */
	  /* height:16px; */
	  /* line-height:16px; */
	  width:100%; /* changed from 96 to 100% */
	  display:block;
	  /* margin:0px auto; not needed since i have width 100% now */
	}
</style>
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
<script type='text/javascript'>
	var rsa = new RSAKey();
	

	function getSessionkeyBase64() {
		var privateKey;
		var privateKeyBase64 = sessionStorage.getItem('<%=WebCommonStaticFinalVars.SESSIONSTORAGE_KEY_NAME_OF_PRIVATEKEY%>');
		
		if (typeof(privateKeyBase64) == 'undefined') {
			privateKey = CryptoJS.lib.WordArray.random(<%=WebCommonStaticFinalVars.WEBSITE_PRIVATEKEY_SIZE%>);
			privateKeyBase64 = CryptoJS.enc.Base64.stringify(privateKey);
			
			sessionStorage.setItem('<%=WebCommonStaticFinalVars.SESSIONSTORAGE_KEY_NAME_OF_PRIVATEKEY%>', privateKeyBase64);
		} else {
			privateKey = CryptoJS.enc.Base64.parse(privateKeyBase64);
		}
		
		var sessionKeyHex = rsa.encrypt(CryptoJS.enc.Base64.stringify(privateKey));		
		var sessionkeyBase64 = CryptoJS.enc.Base64.stringify(CryptoJS.enc.Hex.parse(sessionKeyHex));		
	
		return sessionkeyBase64;
	}
	
	
	function save() {		
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
		}<%
	if (! isUserLoginedIn) {
%>	
		if (f.pwd.value == '') {
			alert("비밀번호를 넣어주세요.");
			f.pwd.focus();
			return;
		}
		
		var regexPwd = /^[A-Za-z0-9\`~!@\#$%<>\^&*\(\)\-=+_\'\[\]\{\}\\\|\:\;\"<>\?,\.\/]{8,15}$/;
		var regexPwdAlpha = /.*[A-Za-z]{1,}.*/;
		var regexPwdDigit = /.*[0-9]{1,}.*/;
		var regexPwdPunct = /.*[\!\"#$%&'()*+,\-\.\/:;<=>\?@\[\\\]^_`\{\|\}~]{1,}.*/;
		
		if (!regexPwd.test(f.pwd.value)) {
			alert("게시글 비밀번호는 영문, 숫자 그리고 특수문자 조합으로 최소 8자, 최대 15자로 구성됩니다. 다시 입력해 주세요.");
			f.pwd.value = '';
			f.pwd.focus();
			return;
		}
		
		if (!regexPwdAlpha.test(f.pwd.value)) {
			alert("게시글 비밀번호는 최소 영문 1자가 포함되어야 합니다. 다시 입력해 주세요.");
			f.pwd.value = '';
			f.pwd.focus();
			return;
		}

		if (!regexPwdDigit.test(f.pwd.value)) {
			alert("게시글 비밀번호는 최소 숫자 1자가 포함되어야 합니다. 다시 입력해 주세요.");
			f.pwd.value = '';
			f.pwd.focus();
			return;
		}

		if (!regexPwdPunct.test(f.pwd.value)) {
			alert("게시글 비밀번호는 최소 특수문자 1자가 포함되어야 합니다. 다시 입력해 주세요.");
			f.pwd.value = '';
			f.pwd.focus();
			return;
		}<%
	}
%>

		var g = document.writeProcessFrm;		
		var sourceNewFileListDivNode = document.getElementById('sourceNewFileListDiv');	
			
		for (var i=0; i < sourceNewFileListDivNode.childNodes.length; i++) {				
			var fileInput = sourceNewFileListDivNode.childNodes[i].childNodes[0].childNodes[0];
			
			if (1 == sourceNewFileListDivNode.childNodes.length) {
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
		
		var privateKey;
		var privateKeyBase64 = sessionStorage.getItem('<%= WebCommonStaticFinalVars.SESSIONSTORAGE_KEY_NAME_OF_PRIVATEKEY %>');
		
		if (typeof(privateKeyBase64) == 'undefined') {
			privateKey = CryptoJS.lib.WordArray.random(<%= WebCommonStaticFinalVars.WEBSITE_PRIVATEKEY_SIZE %>);
			privateKeyBase64 = CryptoJS.enc.Base64.stringify(privateKey);
			
			sessionStorage.setItem('<%= WebCommonStaticFinalVars.SESSIONSTORAGE_KEY_NAME_OF_PRIVATEKEY %>', privateKeyBase64);
		} else {
			privateKey = CryptoJS.enc.Base64.parse(privateKeyBase64);
		}
		
		var sessionKeyHex = rsa.encrypt(CryptoJS.enc.Base64.stringify(privateKey));		
		var sessionkeyBase64 = CryptoJS.enc.Base64.stringify(CryptoJS.enc.Hex.parse(sessionKeyHex));	
		
		g.<%= WebCommonStaticFinalVars.PARAMETER_KEY_NAME_OF_SESSION_KEY %>.value = sessionkeyBase64;	
	
		var iv = CryptoJS.lib.WordArray.random(<%= WebCommonStaticFinalVars.WEBSITE_IV_SIZE %>);
		g.<%= WebCommonStaticFinalVars.PARAMETER_KEY_NAME_OF_SESSION_KEY_IV %>.value 
			= CryptoJS.enc.Base64.stringify(iv);
		
		g.subject.value = f.subject.value;
		g.contents.value = f.contents.value;<%
	if (! isUserLoginedIn) {
%>

		var symmetricKeyObj = CryptoJS.<%= WebCommonStaticFinalVars.WEBSITE_JAVASCRIPT_SYMMETRIC_KEY_ALGORITHM_NAME %>;		
		g.pwd.value = symmetricKeyObj.encrypt(f.pwd.value, privateKey, { mode: CryptoJS.mode.CBC, padding: CryptoJS.pad.Pkcs7, iv: iv });<%
	}
%>
		g.submit()
	}
	
	function callBackForBoardWriteProcess(boardWriteResObj) {
		alert("게시글 작성이 완료되었습니다");
		
		goListPage('1')
	}
	
	function addNewAttachFile() {		
		var uploadFileMaxCnt = <%= WebCommonStaticFinalVars.WEBSITE_ATTACHED_FILE_MAX_COUNT %>;
		var prefixOfNewChildDiv = 'attachedFileRowDiv';
		var maxIndex = -1;		
		var uploadFileCnt = 0;

		var sourceNewFileListDiv = document.getElementById('sourceNewFileListDiv');		
		
		for (var i=0;i < sourceNewFileListDiv.childNodes.length; i++) {
			var childNode = sourceNewFileListDiv.childNodes[i];
			
			if (childNode.id.indexOf(prefixOfNewChildDiv) == 0) {
				uploadFileCnt++;
				var numStr = childNode.id.substring(prefixOfNewChildDiv.length);

				var num = parseInt(numStr);

				if (maxIndex < num) {
					maxIndex = num;
				}	
			}						
		}		
		
	
		if (uploadFileCnt > uploadFileMaxCnt) {
			alert("업로드 할 수 있는 파일 갯수는 최대["+uploadFileMaxCnt+"] 까지 입니다.");
			return;
		}
		

		var inx = maxIndex+1;		
		
		var attachedFileRowDiv = makeAttachedFileRowDiv(prefixOfNewChildDiv+inx);
		
		
		sourceNewFileListDiv.appendChild(attachedFileRowDiv);
	}
	
	function makeAttachedFileRowDiv(attachedFileRowDivID) {
		var attachedFileRowDiv = document.createElement("div");
		attachedFileRowDiv.setAttribute("class", "row");
		attachedFileRowDiv.setAttribute("id", attachedFileRowDivID);		
		
		var fileInputColDiv = document.createElement("div");
		fileInputColDiv.setAttribute("class", "col-sm-11");
		
		
		var fileInput = document.createElement("INPUT");
		fileInput.setAttribute("type", "file");
		fileInput.setAttribute("class", "form-control");
		fileInput.setAttribute("title", "첨부파일('"+attachedFileRowDivID+"')");
		fileInput.setAttribute("name", "newAttachedFile");
		
		fileInputColDiv.appendChild(fileInput);		
		attachedFileRowDiv.appendChild(fileInputColDiv);
		
		var deleteInputColDiv = document.createElement("div");
		deleteInputColDiv.setAttribute("class", "col-sm-1");
		
		var deleteInput = document.createElement("INPUT");
		deleteInput.setAttribute("type", "button");
		deleteInput.setAttribute("value", "삭제");
		deleteInput.setAttribute("title", "첨부파일('"+attachedFileRowDivID+"') 삭제");
		deleteInput.setAttribute("class", "form-control");
		deleteInput.setAttribute("onclick", "removeNewAttachFile('"+attachedFileRowDivID+"')");
		
		deleteInputColDiv.appendChild(deleteInput);		
		attachedFileRowDiv.appendChild(deleteInputColDiv);
		
		return attachedFileRowDiv;
	}
	
	function showWritePart() {	
		var writePartObj = document.getElementById('writePart');
		writePartObj.style.display = "block";
	}
	
	function closeWritePart() {	
		var writePartObj = document.getElementById('writePart');
		writePartObj.style.display = "none";
	}
		
	function removeNewAttachFile(divIdName) {
		var d = document.getElementById('sourceNewFileListDiv');		
		var olddiv = document.getElementById(divIdName);
		d.removeChild(olddiv);
	}
	
	
	function goDetailPage(boardNo) {
		var detailPageURL = "/servlet/BoardDetail?boardID=<%= boardListRes.getBoardID() %>&boardNo="+boardNo;<%
	if (BoardListType.TREE.equals(boardListType)) {
		out.write(CommonStaticFinalVars.NEWLINE);
		out.write("		");
		out.write("window.open(detailPageURL, \"\", \"width=800,height=600\");");
	} else {
		out.write(CommonStaticFinalVars.NEWLINE);
		out.write("		");
		out.write("document.location.href = detailPageURL;");
	}
%>
		
	}
	
	
	function goListPage(pageNo) {
		var g = document.listwriteInputFrm;
		g.pageNo.value = pageNo;
		g.sessionkeyBase64.value = getSessionkeyBase64();
		var iv = CryptoJS.lib.WordArray.random(<%= WebCommonStaticFinalVars.WEBSITE_IV_SIZE %>);
		g.ivBase64.value = CryptoJS.enc.Base64.stringify(iv);
		g.submit();
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
	
	function expandTextarea(id) {
	    document.getElementById(id).addEventListener('keyup', function() {
	        this.style.overflow = 'hidden';
	        this.style.height = 0;
	        this.style.height = this.scrollHeight + 'px';
	    }, false);
	}

	function init() {
		if(typeof(sessionStorage) == "undefined") {
		    alert("Sorry! No HTML5 sessionStorage support..");
		    document.location.href = "/";
		    return;
		}
		
		rsa.setPublic("<%= getModulusHexString(request) %>", "10001");
		expandTextarea('contentsInWritePart');
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
		if (isUserLoginedIn) {
%>
					<button type="button" class="btn btn-primary btn-sm" onClick="showWritePart();">글 작성하기</button><%
		}
	} else if (PermissionType.ADMIN.equals(boardWritePermissionType)) {
		MemberRoleType memberRoleType = getMemberRoleTypeFromHttpSession(request);
		if (MemberRoleType.ADMIN.equals(memberRoleType)) {
%>
					<button type="button" class="btn btn-primary btn-sm" onClick="showWritePart();">글 작성하기</button><%			
		}
	} else {
		/** 손님 허용 */
%>
					<button type="button" class="btn btn-primary btn-sm" onClick="showWritePart();">글 작성하기</button><%
	}
%>
					<button type="button" class="btn btn-primary btn-sm" onClick="clickHiddenFrameButton(this);">Show Hidden Frame</button>
				</div>			 
				<div id="resultMessageView"></div>
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
%><a href="#" onClick="goDetailPage('<%=board.getBoardNo()%>')"><%=StringEscapeActorUtil.replace(board.getSubject(), STRING_REPLACEMENT_ACTOR_TYPE.ESCAPEHTML4)%></a></div>
						<div class="col-sm-2"><%=StringEscapeActorUtil.replace(board.getWriterNickname(), STRING_REPLACEMENT_ACTOR_TYPE.ESCAPEHTML4)%></div>
						<div class="col-sm-1"><%= board.getViewCount() %></div>
						<div class="col-sm-1"><%= board.getVotes() %></div>
						<div class="col-sm-2"><%= board.getRegisteredDate() %></div>
						<div class="col-sm-2"><%= board.getLastModifiedDate() %></div>
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

%>
					<ul class="pagination pagination-sm"><%

		if (startPageNo > 1) {
%>
						<li class="previous"><a href="#" onClick="goListPage('<%= startPageNo-1 %>')">이전</a></li><%
		} else {
%>
						<li class="disabled previous"><a href="#">이전</a></li><%			
		}

		for (int i=0; i < WebCommonStaticFinalVars.WEBSITE_BOARD_PAGE_LIST_SIZE; i++) {
			long workingPageNo = startPageNo + i;
			if (workingPageNo > endPageNo) break;

			if (workingPageNo == pageNo) {
%>
						<li class="active"><a href="#"><%= workingPageNo %></a></li><%
			} else {
%>
						<li><a href="#" onClick="goListPage('<%= workingPageNo %>')"><%= workingPageNo %></a></li><%
			}				
		}
		
		if (startPageNo+WebCommonStaticFinalVars.WEBSITE_BOARD_PAGE_LIST_SIZE <= endPageNo) {
%>
						<li class="next"><a href="#" onClick="goListPage('<%=startPageNo+WebCommonStaticFinalVars.WEBSITE_BOARD_PAGE_LIST_SIZE%>')">다음</a></li><%
		} else {
%>
						<li class="disabled next"><a href="#">다음</a></li><%
		}
%>
					</ul><%		
	}
%>	
				</div>
				<div id="writePart" style="display:none">
					<form name="writeInputFrm" enctype="multipart/form-data" method="post" action="/servlet/BoardWriteProcess" onsubmit="return false;">
						<div class="form-group">	
							<label for="subject">제목</label>
							<input type="text" name="subject" class="form-control" placeholder="Enter subject" />
							<label for="content">내용</label>
							<textarea name="contents" id="contentsInWritePart" class="form-control" placeholder="Enter contents" rows="5"></textarea><%
	if (! isUserLoginedIn) {
%>
							<label for="content">게시글 비밀번호</label>
							<input type="password" class="form-control" placeholder="Enter password" name="pwd"><%		
	}
%>
														
						</div>
					</form>
					<div class="btn-group">
						<input type="button" class="btn btn-default" onClick="save();" value="저장" />					
						<input type="button" class="btn btn-default" onClick="addNewAttachFile();" value="첨부 파일 추가" />
						<input type="button" class="btn btn-default" onClick="closeWritePart();" value="닫기" />
					</div>
					<form name="writeProcessFrm" enctype="multipart/form-data" method="post" target="hiddenFrame" action="/servlet/BoardWriteProcess">
						<div class="form-group">
							<input type="hidden" name="boardID" value="<%= boardListRes.getBoardID() %>" />
							<input type="hidden" name="subject" />
							<input type="hidden" name="contents" /><%
	if (! isUserLoginedIn) {
%>
							<input type="hidden" name="pwd" /><%
	}
%>
							<input type="hidden" name="<%= WebCommonStaticFinalVars.PARAMETER_KEY_NAME_OF_SESSION_KEY %>" />
							<input type="hidden" name="<%= WebCommonStaticFinalVars.PARAMETER_KEY_NAME_OF_SESSION_KEY_IV %>" />					
							<!-- 주의점 div 시작 태그와 종료 태그 사이에는 공백을 포함한 어떠한 것도 넣지 말것, 자식 노드로 인식됨 -->
							<div id="sourceNewFileListDiv"></div>
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


