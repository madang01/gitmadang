<%@page import="com.google.gson.Gson"%><%
%><%@page import="java.util.List"%><%
%><%@page import="java.util.ArrayList"%><%
%><%@page import="java.sql.Timestamp"%><%
%><%@page import="kr.pe.codda.weblib.common.BoardStateType"%><%
%><%@page import="kr.pe.codda.weblib.htmlstring.StringEscapeActorUtil.STRING_REPLACEMENT_ACTOR_TYPE"%><%
%><%@page import="kr.pe.codda.weblib.htmlstring.StringEscapeActorUtil"%><%
%><%@page import="kr.pe.codda.weblib.common.BoardType"%><%
%><%@page import="kr.pe.codda.weblib.common.WebCommonStaticFinalVars"%><%
%><%@ page import="kr.pe.codda.impl.message.BoardDetailRes.BoardDetailRes" %><%
%><%@ page extends="kr.pe.codda.weblib.jdf.AbstractUserJSP" language="java" session="true" autoFlush="true" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %><%
%><jsp:useBean id="boardDetailRes" class="kr.pe.codda.impl.message.BoardDetailRes.BoardDetailRes" scope="request" /><%
	// FIXME!
	boardDetailRes.setBoardID(BoardType.FREE.getBoardID());
	boardDetailRes.setBoardNo(1);
	boardDetailRes.setViewCount(11);
	boardDetailRes.setBoardSate(BoardStateType.OK.getValue());
	boardDetailRes.setNickname("테스트01");
	boardDetailRes.setVotes(7);
	boardDetailRes.setSubject("게시글1");
	boardDetailRes.setContent("게시글1\n한글 그림 하나를 그리다\n호호하하");
	boardDetailRes.setWriterID("test01");
	boardDetailRes.setWriterIP("173.0.0.15");
	boardDetailRes.setRegisteredDate(Timestamp.valueOf("2018-09-01 13:00:01"));
	boardDetailRes.setLastModifierIP("173.0.0.17");
	boardDetailRes.setLastModifierID("admin");
	boardDetailRes.setLastModifierNickName("관리자");
	boardDetailRes.setLastModifiedDate(Timestamp.valueOf("2018-09-15 17:20:11"));
	
	{
		List<BoardDetailRes.AttachedFile> attachedFileList = new ArrayList<BoardDetailRes.AttachedFile>();
		
		{
			BoardDetailRes.AttachedFile attachedFile = new BoardDetailRes.AttachedFile();
			
			attachedFile.setAttachedFileName("temp01.pic");
			attachedFile.setAttachedFileSeq((short)0);
			
			attachedFileList.add(attachedFile);
		}
		
		{
			BoardDetailRes.AttachedFile attachedFile = new BoardDetailRes.AttachedFile();
			
			attachedFile.setAttachedFileName("temp0233f343434343434343.pic");
			attachedFile.setAttachedFileSeq((short)1);
			
			attachedFileList.add(attachedFile);
		}
		
		boardDetailRes.setAttachedFileCnt(attachedFileList.size());
		boardDetailRes.setAttachedFileList(attachedFileList);
	}	


	BoardType boardType = BoardType.valueOf(boardDetailRes.getBoardID());
	
	List<BoardDetailRes.AttachedFile> oldAttachedFileList = boardDetailRes.getAttachedFileList();
	
	String oldAttachedFileListJsonString = "[]";
	
	if (null != oldAttachedFileList) {
		oldAttachedFileListJsonString = new Gson().toJson(oldAttachedFileList);
	}
%><!DOCTYPE html>
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
<script type="text/javascript" src="/js/cryptoJS/rollups/aes.js"></script>
<script type="text/javascript" src="/js/cryptoJS/components/core-min.js"></script>
<script type="text/javascript" src="/js/cryptoJS/components/cipher-core-min.js"></script>
<script type="text/javascript">
<!--
	var uploadFileMaxCnt = <%= WebCommonStaticFinalVars.WEBSITE_ATTACHED_FILE_MAX_COUNT %>;
	var oldAttachedFileListJsonObj = <%= oldAttachedFileListJsonString %>;	
	
	var seqForNewAttachedFileDivOfModifyInputFrm = 0;
	var seqForNewAttachedFileDivOfReplyInputFrm = 0;
	
	
	function getSessionkey() {
		var privateKeyBase64 = sessionStorage.getItem('<%=WebCommonStaticFinalVars.SESSIONSTORAGE_KEY_NAME_OF_PRIVATEKEY%>');
		
		if (typeof(privateKeyBase64) == 'undefined') {
			var newPrivateKey = CryptoJS.lib.WordArray.random(<%=WebCommonStaticFinalVars.WEBSITE_PRIVATEKEY_SIZE%>);
			var newPrivateKeyBase64 = CryptoJS.enc.Base64.stringify(newPrivateKey);
			
			sessionStorage.setItem('<%=WebCommonStaticFinalVars.SESSIONSTORAGE_KEY_NAME_OF_PRIVATEKEY%>', newPrivateKeyBase64);
			
			privateKeyBase64 = newPrivateKeyBase64;
		}
		
		var rsa = new RSAKey();
		rsa.setPublic("<%=getModulusHexString(request)%>", "10001");
		
		var sessionKeyHex = rsa.encrypt(privateKeyBase64);
		return CryptoJS.enc.Base64.stringify(CryptoJS.enc.Hex.parse(sessionKeyHex));
	}	
	
	function showModifyInputModal() {
		var subjectDivNode = document.getElementById('subjectDiv');
		var cotentDivNode = document.getElementById('cotentDiv');
		
		restoreOldAttachedFileList();
		
		var newFileListDivNode = document.getElementById('newFileListDivForModifyInputFrm');		
		
		/** remove all child nodes of newFileListDivNode node */
		while(newFileListDivNode.hasChildNodes()) {
			newFileListDivNode.removeChild(newFileListDivNode.firstChild);
		}
		
		var f = document.modifyInputFrm;
		
		f.subject.value = subjectDivNode.innerText;
		f.content.value = cotentDivNode.innerText;
		
		$("#modifyInputModal").modal();
	}
	
	function addNewAttachFileForModifyInputFrm() {		
		var prefixOfNewChildDiv = 'modifyInputAttachedFileRowDiv';		

		var newFileListDivNode = document.getElementById('newFileListDivForModifyInputFrm');		
		var oldFileListDivNode = document.getElementById('oldFileListDivForModifyInputFrm');
		
		var uploadFileCnt = oldFileListDivNode.childNodes.length + newFileListDivNode.childNodes.length;
		
			
		if (uploadFileCnt >= uploadFileMaxCnt) {
			alert("업로드 할 수 있는 파일 갯수는 최대["+uploadFileMaxCnt+"] 까지 입니다.");
			return;
		}
	
		var attachedFileRowDivNode = makeAttachedFileRowDivForModifyInputFrm(prefixOfNewChildDiv+seqForNewAttachedFileDivOfModifyInputFrm);
		
		seqForNewAttachedFileDivOfModifyInputFrm++;
		
		newFileListDivNode.appendChild(attachedFileRowDivNode);
	}
	
	function makeAttachedFileRowDivForModifyInputFrm(attachedFileRowDivID) {
		var attachedFileRowDivNode = document.createElement("div");
		attachedFileRowDivNode.setAttribute("class", "row");
		attachedFileRowDivNode.setAttribute("id", attachedFileRowDivID);		
		
		var attachedFileNode  = document.createElement("INPUT");
		attachedFileNode .setAttribute("type", "file");
		attachedFileNode .setAttribute("class", "form-control");
		attachedFileNode .setAttribute("title", "첨부파일('"+attachedFileRowDivID+"')");
		attachedFileNode .setAttribute("name", "newAttachFile");
		
		var attachedFileColDivNode = document.createElement("div");
		attachedFileColDivNode.setAttribute("class", "col-sm-10");
		
		attachedFileColDivNode.appendChild(attachedFileNode);
		
		var deleteButtonNode = document.createElement("INPUT");
		deleteButtonNode.setAttribute("type", "button");
		deleteButtonNode.setAttribute("value", "삭제");
		deleteButtonNode.setAttribute("title", "첨부파일('"+attachedFileRowDivID+"') 삭제");
		deleteButtonNode.setAttribute("onclick", "removeNewAttachFileForModifyInputFrm('"+attachedFileRowDivID+"')");
		
		var buttonColDivNode = document.createElement("div");
		buttonColDivNode.setAttribute("class", "col-*-*");
		
		buttonColDivNode.appendChild(deleteButtonNode);
		
		attachedFileRowDivNode.appendChild(attachedFileColDivNode);
		attachedFileRowDivNode.appendChild(buttonColDivNode);
		
		return attachedFileRowDivNode;
	}
		
	function removeNewAttachFileForModifyInputFrm(selectedDivID) {
		var newFileListDivNode = document.getElementById('newFileListDivForModifyInputFrm');		
		var selectedDivNode = document.getElementById(selectedDivID);
		newFileListDivNode.removeChild(selectedDivNode);
	}
	
	function restoreOldAttachedFileList() {		
		var oldFileListDivNode = document.getElementById('oldFileListDivForModifyInputFrm');		
		
		/** remove all child nodes of oldFileListDivForModifyInputFrm node */
		while(oldFileListDivNode.hasChildNodes()) {
			oldFileListDivNode.removeChild(oldFileListDivNode.firstChild);
		}
		
		for (var i=0; i < oldAttachedFileListJsonObj.length; i++) {
			var oldAttachedFileRowDivID = "oldAttachedFileRow"+oldAttachedFileListJsonObj[i].attachedFileSeq;
			
			var oldAttachedFileRowDivNode = document.createElement("div");
			oldAttachedFileRowDivNode.setAttribute("class", "row");
			oldAttachedFileRowDivNode.setAttribute("id", oldAttachedFileRowDivID);

			var oldAttachedFileHiddenInputNode = document.createElement("INPUT");
			oldAttachedFileHiddenInputNode.setAttribute("type", "hidden");	
			oldAttachedFileHiddenInputNode.setAttribute("name", "oldAttachSeq");
			oldAttachedFileHiddenInputNode.setAttribute("value", oldAttachedFileListJsonObj[i].attachedFileSeq);			
			
			var fileNameTextNode = document.createTextNode(oldAttachedFileListJsonObj[i].attachedFileName+" ");
			
			var deleteButtonNode = document.createElement("INPUT");
			deleteButtonNode.setAttribute("type", "button");
			deleteButtonNode.setAttribute("value", "삭제");
			deleteButtonNode.setAttribute("title", "delete file(attachedFileSeq:"+oldAttachedFileListJsonObj[i].attachedFileSeq+", fileName:"+ oldAttachedFileListJsonObj[i].attachedFileName + ")");
			deleteButtonNode.setAttribute("onclick", "deleteOldAttachedFile('"+oldAttachedFileRowDivID+"')");			
			
			oldAttachedFileRowDivNode.appendChild(oldAttachedFileHiddenInputNode);
			oldAttachedFileRowDivNode.appendChild(fileNameTextNode);
			oldAttachedFileRowDivNode.appendChild(deleteButtonNode);
			
			oldFileListDivNode.appendChild(oldAttachedFileRowDivNode)
		}		
	}
	
	function deleteOldAttachedFile(oldAttachedFileRowDivID) {
		var d = document.getElementById('oldFileListDivForModifyInputFrm');		
		var deleteTagetDiv = document.getElementById(oldAttachedFileRowDivID);
		d.removeChild(deleteTagetDiv);
	}

	function modify() {			
		var f = document.modifyInputFrm;
		
		if ('' == f.subject.value) {
			alert("제목을 넣어 주세요.");
			f.subject.focus();
			return false;
		}

		if ('' == f.content.value) {
			alert("내용을 넣어 주세요.");
			f.content.focus();
			return false;
		}
		
		var sourceNewFileListDivNode = document.getElementById('newFileListDivForModifyInputFrm');		
		var oldFileListDivNode = document.getElementById('oldFileListDivForModifyInputFrm');
		
		var uploadFileCnt = oldFileListDivNode.childNodes.length + newFileListDivNode.childNodes.length;
		
			
		if (uploadFileCnt > uploadFileMaxCnt) {
			alert("업로드 할 수 있는 파일 갯수는 최대["+uploadFileMaxCnt+"] 까지 입니다.");
			return false;
		}
			
		var g = document.modofyProcessFrm;
		
		for (var i=0; i < newFileListDivNode.childNodes.length; i++) {				
			var fileInput = newFileListDivNode.childNodes[i].childNodes[0].childNodes[0];
			
			if (1 == newFileListDivNode.childNodes.length) {
				if (g.newAttachFile.value == '') {
					alert("첨부 파일을 선택하세요");
					return false;
				}
			} else {
				if (g.newAttachFile[i].value == '') {
					alert(fileInput.getAttribute("title")+"을 선택하세요");
					return false;
				}
			}			
		}
		
		$('#modifyInputModal').modal('toggle');		
		
		g.subject.value = f.subject.value;
		g.content.value = f.content.value;		
		
		g.<%= WebCommonStaticFinalVars.PARAMETER_KEY_NAME_OF_SESSION_KEY %>.value 
		= getSessionkey();
		var iv = CryptoJS.lib.WordArray.random(<%=WebCommonStaticFinalVars.WEBSITE_IV_SIZE%>);
		g.ivBase64.value = CryptoJS.enc.Base64.stringify(iv);
		
		g.submit();		
		
		return false;	
	}
	
	function callBackForBoardModifyProcess() {
		alert("게시글 수정이 완료되었습니다");
		document.location.reload();
	}
		
	function showReplyInputModal() {
		var newFileListDivNode = document.getElementById('newFileListDivForReplyInputFrm');		
		
		/** remove all child nodes of newFileListDivNode node */
		while(newFileListDivNode.hasChildNodes()) {
			newFileListDivNode.removeChild(newFileListDivNode.firstChild);
		}
		
		$("#replyInputModal").modal();
	}
	
	function addNewAttachFileForReplyInputFrm() {			
		var prefixOfNewChildDiv = 'replyInputAttachedFileRowDiv';		

		var newFileListDiv = document.getElementById('newFileListDivForReplyInputFrm');	
		
		var uploadFileCnt = newFileListDiv.childNodes.length; 
		if (uploadFileCnt >= uploadFileMaxCnt) {
			alert("업로드 할 수 있는 파일 갯수는 최대["+uploadFileMaxCnt+"] 까지 입니다.");
			return;
		}
				
		var attachedFileRowDiv = makeAttachedFileRowDivForReplyInputFrm(prefixOfNewChildDiv+seqForNewAttachedFileDivOfReplyInputFrm);
		seqForNewAttachedFileDivOfReplyInputFrm++;
		
		newFileListDiv.appendChild(attachedFileRowDiv);
	}
	
	function makeAttachedFileRowDivForReplyInputFrm(attachedFileRowDivID) {
		var attachedFileRowDiv = document.createElement("div");
		attachedFileRowDiv.setAttribute("class", "row");
		attachedFileRowDiv.setAttribute("id", attachedFileRowDivID);		
		
		var fileInputColDiv = document.createElement("div");
		fileInputColDiv.setAttribute("class", "col-sm-10");
		
		
		var fileInput = document.createElement("INPUT");
		fileInput.setAttribute("type", "file");
		fileInput.setAttribute("class", "form-control");
		fileInput.setAttribute("title", "첨부파일('"+attachedFileRowDivID+"')");
		fileInput.setAttribute("name", "newAttachFile");
		
		fileInputColDiv.appendChild(fileInput);		
		
		
		var deleteInputColDiv = document.createElement("div");
		deleteInputColDiv.setAttribute("class", "col-*-*");
		
		var deleteInput = document.createElement("INPUT");
		deleteInput.setAttribute("type", "button");
		deleteInput.setAttribute("value", "삭제");
		deleteInput.setAttribute("title", "첨부파일('"+attachedFileRowDivID+"') 삭제");
		deleteInput.setAttribute("onclick", "removeNewAttachFileForReplyInputFrm('"+attachedFileRowDivID+"')");
		
		deleteInputColDiv.appendChild(deleteInput);
		
		
		attachedFileRowDiv.appendChild(fileInputColDiv);
		attachedFileRowDiv.appendChild(deleteInputColDiv);
		
		return attachedFileRowDiv;
	}
		
	function removeNewAttachFileForReplyInputFrm(divIDName) {
		var d = document.getElementById('newFileListDivForReplyInputFrm');		
		var deleteTagetDiv = document.getElementById(divIDName);
		d.removeChild(deleteTagetDiv);
	}
	
	function reply() {
		var f = document.replyInputFrm;
		
		if ('' == f.subject.value) {
			alert("제목을 넣어 주세요.");
			f.subject.focus();
			return false;
		}

		if ('' == f.content.value) {
			alert("내용을 넣어 주세요.");
			f.content.focus();
			return false;
		}
				
		var sourceNewFileListDivNode = document.getElementById('newFileListDivForReplyInputFrm');		
		var uploadFileCnt = oldFileListDivNode.childNodes.length;	
			
		if (uploadFileCnt > uploadFileMaxCnt) {
			alert("업로드 할 수 있는 파일 갯수는 최대["+uploadFileMaxCnt+"] 까지 입니다.");
			return false;
		}
		
		var g = document.replyProcessFrm;
		
		for (var i=0; i < sourceNewFileListDivNode.childNodes.length; i++) {				
			var fileInput = sourceNewFileListDivNode.childNodes[i].childNodes[0].childNodes[0];
			
			if (1 == sourceNewFileListDivNode.childNodes.length) {
				if (g.newAttachFile.value == '') {
					alert("첨부 파일을 선택하세요");
					return false;
				}
			} else {
				if (g.newAttachFile[i].value == '') {
					alert(fileInput.getAttribute("title")+"을 선택하세요");
					return false;
				}
			}			
		}
		
		g.<%=WebCommonStaticFinalVars.PARAMETER_KEY_NAME_OF_SESSION_KEY%>.value 
		= getSessionkey();
		var iv = CryptoJS.lib.WordArray.random(<%=WebCommonStaticFinalVars.WEBSITE_IV_SIZE%>);
		g.ivBase64.value = CryptoJS.enc.Base64.stringify(iv);
		g.submit();
	}
	
	function callBackForBoardReplyProcess() {
		alert("댓글 등록이 완료되었습니다");
		goList();
	}
	
	function goVote() {	
		var g = document.voteFrm;
		g.<%=WebCommonStaticFinalVars.PARAMETER_KEY_NAME_OF_SESSION_KEY%>.value 
		= getSessionkey();
		var iv = CryptoJS.lib.WordArray.random(<%=WebCommonStaticFinalVars.WEBSITE_IV_SIZE%>);
		g.ivBase64.value = CryptoJS.enc.Base64.stringify(iv);		
		g.submit();
	}
	
	function callBackForBoardVote() {
		alert("게시글 추천이 완료되었습니다");
		document.location.reload();
	}	
	
	function goList() {	
		var g = document.goListFrm;		
		g.submit();
	}
	
	function downloadFile(attachedFileSeq) {
		var g = document.goDownloadFrm;
		g.attachedFileSeq.value = attachedFileSeq;
		g.submit();
	}
	
	function makeOldAttachedFileList() {		
		var detailAttachedFileListDivNode = document.getElementById('detailAttachedFileListDiv');		
		
		/** remove all child nodes of oldFileListDivForModifyInputFrm node */
		while(detailAttachedFileListDivNode.hasChildNodes()) {
			detailAttachedFileListDivNode.removeChild(detailAttachedFileListDivNode.firstChild);
		}
		
		for (var i=0; i < oldAttachedFileListJsonObj.length; i++) {
			var oldAttachedFileRowDivNode = document.createElement("div");
			oldAttachedFileRowDivNode.setAttribute("class", "row");
			
			var oldAttachedFileColDivNode = document.createElement("div");
			oldAttachedFileColDivNode.setAttribute("class", "col-*-*");
						
			var fileNameTextNode = document.createTextNode(oldAttachedFileListJsonObj[i].attachedFileName+" ");
						
			var downloadButtonNode = document.createElement("INPUT");
			downloadButtonNode.setAttribute("type", "button");
			downloadButtonNode.setAttribute("value", "다운로드");
			downloadButtonNode.setAttribute("title", "download file(attachedFileSeq:"+oldAttachedFileListJsonObj[i].attachedFileSeq+", fileName:"+ oldAttachedFileListJsonObj[i].attachedFileName + ")");
			downloadButtonNode.setAttribute("onclick", "downloadFile("+oldAttachedFileListJsonObj[i].attachedFileSeq+")");			
						
			oldAttachedFileColDivNode.appendChild(fileNameTextNode);
			oldAttachedFileColDivNode.appendChild(downloadButtonNode);
			
			oldAttachedFileRowDivNode.appendChild(oldAttachedFileColDivNode);
			
			detailAttachedFileListDivNode.appendChild(oldAttachedFileRowDivNode)
		}		
	}
	
	function init() {
		if(typeof(sessionStorage) == "undefined") {
		    alert("Sorry! No HTML5 sessionStorage support..");
		    top.location.href = "/";
		}
		// detailAttachedFileListDiv
		
		makeOldAttachedFileList();
		restoreOldAttachedFileList();		
	}

	window.onload=init;
//-->
</script>
</head>
<body>
<%= getSiteNavbarString(request) %>

	<form name=voteFrm target=hiddenFrame method="post" action="/servlet/BoardVote">
		<input type="hidden" name="boardID" value="<%=boardDetailRes.getBoardID()%>" />
		<input type="hidden" name="boardNo" value="<%=boardDetailRes.getBoardNo()%>" />
		<input type="hidden" name="<%=WebCommonStaticFinalVars.PARAMETER_KEY_NAME_OF_SESSION_KEY%>" />
		<input type="hidden" name="<%=WebCommonStaticFinalVars.PARAMETER_KEY_NAME_OF_SESSION_KEY_IV%>" />
	</form>
	
	<form name=goListFrm method="post" action="/servlet/BoardList">
		<input type="hidden" name="boardID" value="<%= boardDetailRes.getBoardID() %>" />
	</form>
	
	<form name=goDownloadFrm target="hiddenFrame" method="post" action="/servlet/BoardDownload">
		<input type="hidden" name="boardID" value="<%=boardDetailRes.getBoardID()%>" />
		<input type="hidden" name="boardNo" value="<%=boardDetailRes.getBoardNo()%>" />
		<input type="hidden" name="attachedFileSeq" />
	</form>

	<div class="container-fluid">
		<h3><%= boardType.getName() %> 게시판 - 상세보기</h3>
		<br>
		<div class="btn-group">
			<button type="button" class="btn btn-primary btn-sm" onClick="showReplyInputModal()">댓글</button>
			<button type="button" class="btn btn-primary btn-sm" onClick="showModifyInputModal()">수정</button>
			<button type="button" class="btn btn-primary btn-sm" onClick="goVote()">추천</button>
			<button type="button" class="btn btn-primary btn-sm" onClick="goList()">목록으로</button>
		</div>
		<br>
		<br>
		<div class="row">
			<div class="col-sm-1" style="background-color:lavender;">글번호</div>
			<div class="col-sm-1"><%= boardDetailRes.getBoardNo() %></div>
			<div class="col-sm-1" style="background-color:lavenderblush;">작성자</div>
			<div class="col-sm-2"><%= StringEscapeActorUtil.replace(boardDetailRes.getNickname(), 
					STRING_REPLACEMENT_ACTOR_TYPE.ESCAPEHTML4) %></div>	
			<div class="col-sm-2" style="background-color:lavender;">작성일</div>	
			<div class="col-sm-3"><%=boardDetailRes.getRegisteredDate()%></div>		
		</div>
		<div class="row">
			<div class="col-sm-1" class="col-sm-2" style="background-color:lavenderblush;">조회수</div>
			<div class="col-sm-1" class="col-sm-2"><%=boardDetailRes.getViewCount()%></div>
			<div class="col-sm-1" style="background-color:lavender;">추천수</div>
			<div class="col-sm-2" id="voteTxt"><%=boardDetailRes.getVotes()%></div>
			<div class="col-sm-2" class="col-sm-2" style="background-color:lavenderblush;">마지막 수정일</div>
			<div class="col-sm-3" class="col-sm-2"><%=boardDetailRes.getLastModifiedDate()%></div>
		</div>
		<div class="row">
			<div class="col-sm-1" style="background-color:lavender;">게시판 상태</div>
			<div class="col-sm-1"><%=BoardStateType.valueOf(boardDetailRes.getBoardSate(), false).getName()%></div>
			<div class="col-sm-1" class="col-sm-2" style="background-color:lavenderblush;">마지막 수정자</div>
			<div class="col-sm-2" class="col-sm-2"><%=boardDetailRes.getLastModifierNickName()%></div>			
			<div class="col-sm-2" style="background-color:lavender;">마지막 수정자 IP</div>
			<div class="col-sm-3"><%=boardDetailRes.getLastModifierIP()%></div>
		</div>
		<div class="row">
			<div class="col-sm-1" class="col-sm-1" style="background-color:lavenderblush;">제목</div>
			<div class="col-sm-8" class="col-sm-11" id="subjectDiv"><%=StringEscapeActorUtil.replace(boardDetailRes.getSubject(), 
					STRING_REPLACEMENT_ACTOR_TYPE.ESCAPEHTML4)%></div>
		</div>
		<div class="row">
			<div class="col-sm-1" style="background-color:lavender;">내용</div>
			<div class="col-sm-8" id="cotentDiv"><%=StringEscapeActorUtil.replace(boardDetailRes.getContent(), 
					STRING_REPLACEMENT_ACTOR_TYPE.ESCAPEHTML4, STRING_REPLACEMENT_ACTOR_TYPE.LINE2BR)%></div>
		</div>
		<div class="row">
			<div class="col-sm-1" style="background-color:lavenderblush;">점부파일</div>
			<div class="col-sm-8" id="detailAttachedFileListDiv"></div>
		</div>
	</div>
	<!-- 수정 입력 화면  모달 -->
	<div class="modal fade" id="modifyInputModal" role="dialog">
		<div class="modal-dialog">			
			<!-- Modal content-->
			<div class="modal-content">
				<div class="modal-header">
					<button type="button" class="close" data-dismiss="modal">&times;</button>
					<h4 class="modal-title">게시글 수정 화면</h4>
				</div>
				<div class="modal-body" style="overflow-y:auto">
					<form name="modifyInputFrm" method="post" onSubmit="return modify();" enctype="multipart/form-data">							
						 <div class="form-group">
						    <label for="subjectForModifyInputFrm">제목</label>
						    <input type="text" id="subjectForModifyInputFrm" name="subject" class="form-control" placeholder="Enter subject" />
						
						    <label for="contentForModifyInputFrm">내용</label>
						   <textarea name="content" id="contentForModifyInputFrm" class="form-control" placeholder="Enter content" rows="5"></textarea>
						 </div>
						<button type="submit" class="btn btn-default">저장</button>
						<div class="btn-group">
							<input type="button" class="btn btn-default" onClick="restoreOldAttachedFileList()" value="기존 첨부 파일 목록 복구" />	
							<input type="button" class="btn btn-default" onClick="addNewAttachFileForModifyInputFrm()" value="신규 첨부 파일 추가" />									
						</div>
					</form>	
					<form name=modofyProcessFrm target=hiddenFrame method="post" action="/servlet/BoardModifyProcess" enctype="multipart/form-data">
						<input type="hidden" name="boardID" value="<%=boardDetailRes.getBoardID()%>" />
						<input type="hidden" name="boardNo" value="<%=boardDetailRes.getBoardNo()%>" />
						<input type="hidden" name="subject" />
						<input type="hidden" name="content" />
						<input type="hidden" name="<%=WebCommonStaticFinalVars.PARAMETER_KEY_NAME_OF_SESSION_KEY%>" />
						<input type="hidden" name="<%=WebCommonStaticFinalVars.PARAMETER_KEY_NAME_OF_SESSION_KEY_IV%>" />
						<!-- 주의점 div 시작 태그와 종료 태그 사이에는 공백을 포함한 어떠한 것도 넣지 말것, 자식 노드로 인식됨 -->
						<div id="oldFileListDivForModifyInputFrm"></div>
						<div id="newFileListDivForModifyInputFrm"></div>
					</form>				
				</div>
				<div class="modal-footer">
					<button type="button" class="btn btn-default" data-dismiss="modal">Close</button>
				</div>
			</div>			
		</div>
	</div>
	<!-- 댓글 입력 화면  모달 -->
	<div class="modal fade" id="replyInputModal" role="dialog">
		<div class="modal-dialog">			
			<!-- Modal content-->
			<div class="modal-content">
				<div class="modal-header">
					<button type="button" class="close" data-dismiss="modal">&times;</button>
					<h4 class="modal-title">게시글 댓글 화면</h4>
				</div>
				<div class="modal-body">
					<form name="replyInputFrm" method="post" onSubmit="return reply();" enctype="multipart/form-data">							
						 <div class="form-group">
						    <label for="subjectForReplyInputFrm">제목</label>
						    <input type="text" id="subjectForReplyInputFrm" name="subject" class="form-control" placeholder="Enter subject" />
						
						    <label for="contentForReplyInputFrm">내용</label>
						   <textarea name="content" id="contentForReplyInputFrm" class="form-control" placeholder="Enter content" rows="5"></textarea>
						 </div>
						<button type="submit" class="btn btn-default">저장</button>
						<div class="btn-group">
							<input type="button" class="btn btn-default" onClick="addNewAttachFileForReplyInputFrm()" value="첨부 파일 추가" />			
						</div>
						
					</form>	
					<form name=replyProcessFrm target=hiddenFrame method="post" action="/servlet/BoardReplyProcess">
						<input type="hidden" name="boardID" value="<%=boardDetailRes.getBoardID()%>" />
						<input type="hidden" name="parentBoardNo" value="<%=boardDetailRes.getBoardNo()%>" />
						<input type="hidden" name="subject" />
						<input type="hidden" name="content" />
						<input type="hidden" name="<%=WebCommonStaticFinalVars.PARAMETER_KEY_NAME_OF_SESSION_KEY%>" /> 
						<input type="hidden" name="<%=WebCommonStaticFinalVars.PARAMETER_KEY_NAME_OF_SESSION_KEY_IV%>" />
						<!-- 주의점 div 시작 태그와 종료 태그 사이에는 공백을 포함한 어떠한 것도 넣지 말것, 자식 노드로 인식됨 -->
						<div id="newFileListDivForReplyInputFrm"></div>
					</form>				
				</div>
				<div class="modal-footer">
					<button type="button" class="btn btn-default" data-dismiss="modal">Close</button>
				</div>
			</div>			
		</div>
	</div>
	<iframe name="hiddenFrame" style="display:none;"></iframe>
</body>
</html>
