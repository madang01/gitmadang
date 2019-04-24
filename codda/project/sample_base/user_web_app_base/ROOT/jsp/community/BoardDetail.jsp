<%@page import="kr.pe.codda.weblib.common.AccessedUserInformation"%><%
%><%@page import="kr.pe.codda.weblib.common.MemberRoleType"%><%
%><%@page import="kr.pe.codda.common.etc.CommonStaticFinalVars"%><%
%><%@page import="kr.pe.codda.weblib.common.BoardReplyPolicyType"%><%
%><%@page import="kr.pe.codda.weblib.common.PermissionType"%><%
%><%@page import="kr.pe.codda.weblib.common.BoardListType"%><%
%><%@page import="com.google.gson.Gson"%><%
%><%@page import="kr.pe.codda.weblib.common.BoardStateType"%><%
%><%@page import="kr.pe.codda.weblib.htmlstring.StringEscapeActorUtil.STRING_REPLACEMENT_ACTOR_TYPE"%><%
%><%@page import="kr.pe.codda.weblib.htmlstring.StringEscapeActorUtil"%><%
%><%@page import="kr.pe.codda.weblib.common.WebCommonStaticFinalVars"%><%
%><%@ page import="kr.pe.codda.impl.message.BoardDetailRes.BoardDetailRes" %><%
%><%@ page extends="kr.pe.codda.weblib.jdf.AbstractUserJSP" language="java" session="true" autoFlush="true" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %><%
%><jsp:useBean id="boardDetailRes" class="kr.pe.codda.impl.message.BoardDetailRes.BoardDetailRes" scope="request" /><%// FIXME!
	/* boardDetailRes.setBoardID(BoardType.FREE.getBoardID());
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
	}	 */
	
	
	MemberRoleType firstWriterRoleType = MemberRoleType.valueOf(boardDetailRes.getFirstWriterRole());
	
	
	BoardListType boardListType = BoardListType.valueOf(boardDetailRes.getBoardListType());
	BoardReplyPolicyType boardReplyPolicyType = BoardReplyPolicyType.valueOf(boardDetailRes.getBoardReplyPolicyType());	
	PermissionType boardReplyPermissionType = PermissionType.valueOf(boardDetailRes.getBoardReplyPermssionType());		
	
	AccessedUserInformation  accessedUserformation = getAccessedUserInformationFromSession(request);

	String paramInterestedBoadNo = request.getParameter("interestedBoadNo");
	
	long interestedBoadNo = 0L;
	
	try {
		interestedBoadNo = Long.parseLong(paramInterestedBoadNo);
	} catch(Exception e) {		
	}%><!DOCTYPE html>
<html lang="ko">
<head>
<meta charset="UTF-8">
<meta http-equiv="X-UA-Compatible" content="IE=edge">
<meta name="viewport" content="width=device-width, initial-scale=1">
<title><%= WebCommonStaticFinalVars.USER_WEBSITE_TITLE %></title>
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

<script src="/js/common.js"></script>
<script type="text/javascript">
<!--
	var currentEditScreenDiv = null;
	
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
		return CryptoJS.enc.Base64.stringify(CryptoJS.enc.Hex.parse(sessionKeyHex));
	}
	
	function buildIV() {
		var iv = CryptoJS.lib.WordArray.random(<%= WebCommonStaticFinalVars.WEBSITE_IV_SIZE %>);
		return iv;
	}
	
	function goModify() {			
		var f = document.modifyInputFrm;
		
		if (f.subject != undefined) {
			if (f.subject.value == '') {
				alert("제목을 넣어 주세요");
				f.subject.focus();
				return;
			}
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
		}

		var newFileListDivNode = document.getElementById('newAttachedFileList');		
		var oldFileListDivNode = document.getElementById('oldAttachedFileList');
		
		var uploadFileCnt = oldFileListDivNode.childNodes.length + newFileListDivNode.childNodes.length;
			
		if (uploadFileCnt > _ATTACHED_FILE_MAX_COUNT) {
			alert("업로드 할 수 있는 파일 갯수는 최대["+_ATTACHED_FILE_MAX_COUNT+"] 까지 입니다.");
			return;
		}
		
		var symmetricKeyObj = CryptoJS.<%= WebCommonStaticFinalVars.WEBSITE_JAVASCRIPT_SYMMETRIC_KEY_ALGORITHM_NAME %>;
		var privateKey = getPrivateKeyFromSessionStorage();		
		var iv = buildIV();
		
		var g = document.modifyProcessFrm;
		
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

		if (f.subject != undefined) {
			g.subject.value = f.subject.value;
		}
		g.contents.value = f.contents.value;

		if (f.pwd != undefined) {
			g.pwd.value = symmetricKeyObj.encrypt(f.pwd.value, privateKey, { mode: CryptoJS.mode.CBC, padding: CryptoJS.pad.Pkcs7, iv: iv });
		}

		g.submit();		
		
		return;	
	}
	
	function callBackForBoardModifyProcess(boardModifyRes) {
		alert("게시글["+boardModifyRes.boardNo+"] 수정이 완료되었습니다");
		
		var g = document.goDetailFrm;
		g.interestedBoadNo.value = boardModifyRes.boardNo;
		g.submiet
	}
	
	function goReply() {
		var f = document.replyInputFrm;
		
		if (f.subject != undefined) {
			if (f.subject.value == '') {
				alert("제목을 넣어 주세요");
				f.subject.focus();
				return;
			}
		}

		if ('' == f.contents.value) {
			alert("내용을 넣어 주세요.");
			f.contents.focus();
			return;
		}
		
		if (f.pwd != undefined) {
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
		}		
				
		var newFileListDivNode = document.getElementById('newAttachedFileList');		
		var uploadFileCnt = newFileListDivNode.childNodes.length;	
		
		if (uploadFileCnt > _ATTACHED_FILE_MAX_COUNT) {
			alert("업로드 할 수 있는 파일 갯수는 최대["+_ATTACHED_FILE_MAX_COUNT+"] 까지 입니다.");
			return;
		}
		
		var symmetricKeyObj = CryptoJS.<%= WebCommonStaticFinalVars.WEBSITE_JAVASCRIPT_SYMMETRIC_KEY_ALGORITHM_NAME %>;
		var privateKey = getPrivateKeyFromSessionStorage();		
		var iv = buildIV();
		
		var g = document.replyProcessFrm;
		
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
		
		
		if (f.subject != undefined) {
			g.subject.value = f.subject.value;
		}
		g.contents.value = f.contents.value;
		
		if (f.pwd != undefined) {
			g.pwd.value = symmetricKeyObj.encrypt(f.pwd.value, getPrivateKeyFromSessionStorage(), { mode: CryptoJS.mode.CBC, padding: CryptoJS.pad.Pkcs7, iv: iv });
		}

		g.submit();
	}
	
	function callBackForBoardReplyProcess(boardWriteResObj) {		
		alert("댓글["+boardWriteResObj.boardNo+"] 등록이 완료되었습니다");
		if (opener != undefined) {
			opener.document.location.reload();
			self.close();
		} else {
			document.location.reload();
		}
	}
	
	function goDelete(boardNo) {		
		var f = document.deleteInputFrm;
		
		if (f != undefined) {
			try {
				checkValidPwd('게시글', f.pwd.value);
			} catch(err) {
				alert(err);
				f.pwd.focus();
				return;
			}
		}
		
		var symmetricKeyObj = CryptoJS.<%= WebCommonStaticFinalVars.WEBSITE_JAVASCRIPT_SYMMETRIC_KEY_ALGORITHM_NAME %>;
		var privateKey = getPrivateKeyFromSessionStorage();		
		var iv = buildIV();
		
		var g = document.deleteProcessFrm;

		g.<%= WebCommonStaticFinalVars.PARAMETER_KEY_NAME_OF_SESSION_KEY %>.value = getSessionkeyBase64FromSessionStorage();		
		g.<%= WebCommonStaticFinalVars.PARAMETER_KEY_NAME_OF_SESSION_KEY_IV %>.value = CryptoJS.enc.Base64.stringify(iv);
		
		g.boardNo.value = boardNo;		
		if (f != undefined) {
			g.pwd.value = symmetricKeyObj.encrypt(f.pwd.value, getPrivateKeyFromSessionStorage(), { mode: CryptoJS.mode.CBC, padding: CryptoJS.pad.Pkcs7, iv: iv });
		}
		
		g.submit();
	}
	
	
	function showDeleteEditScreen(boardNo) {	
		var targetDiv = document.getElementById("editorScreenForBoard"+boardNo);
		
		if (null != currentEditScreenDiv) {
			hideEditScreen();
		}
		
		currentEditScreenDiv = targetDiv;
		targetDiv.style.display = "block";
		
		var titleTextNode = document.createTextNode("삭제용 비밀번호 입력 화면");
		var titleH4Node = document.createElement("h4");		
		titleH4Node.appendChild(titleTextNode);
		
		targetDiv.appendChild(titleH4Node);

		var inputFormNode = document.createElement("form");
		inputFormNode.setAttribute("name", "deleteInputFrm");
		inputFormNode.setAttribute("method", "get");
		inputFormNode.setAttribute("onSubmit", "return false;");
		
		var inputFormDiv = document.createElement("div");
		inputFormDiv.setAttribute("class", "form-group");
				
		
		var passwordLabelTextNode = document.createTextNode("비밀번호");
		
		var passwordLabelNode = document.createElement("label");
		passwordLabelNode.setAttribute("for", "passwordInEditor");
		passwordLabelNode.appendChild(passwordLabelTextNode);
		
		var passwordInputNode = document.createElement("input");
		passwordInputNode.setAttribute("type", "password");
		passwordInputNode.setAttribute("id", "passwordInEditor");
		passwordInputNode.setAttribute("name", "pwd");
		passwordInputNode.setAttribute("class", "form-control");
		passwordInputNode.setAttribute("placeholder", "Enter password");	
		
		inputFormDiv.appendChild(passwordLabelNode);
		inputFormDiv.appendChild(passwordInputNode);
		
		inputFormNode.appendChild(inputFormDiv);
		
		targetDiv.appendChild(inputFormNode);
		
		var functionDiv = document.createElement("div");
		functionDiv.setAttribute("class", "btn-group");
		
		var deleteProcessButtonNode = document.createElement("input");
		deleteProcessButtonNode.setAttribute("type", "button");
		deleteProcessButtonNode.setAttribute("class", "btn btn-default");		
		deleteProcessButtonNode.setAttribute("onClick", "goDelete("+boardNo+");");
		deleteProcessButtonNode.setAttribute("value", "삭제 처리");
		
		var hideButtonNode = document.createElement("input");
		hideButtonNode.setAttribute("type", "button");
		hideButtonNode.setAttribute("class", "btn btn-default");		
		hideButtonNode.setAttribute("onClick", "hideEditScreen();");
		hideButtonNode.setAttribute("value", "닫기");			
		
		functionDiv.appendChild(deleteProcessButtonNode);
		functionDiv.appendChild(hideButtonNode);
				
		targetDiv.appendChild(functionDiv);
	}
	
	function callBackForBoardDeleteProcess(boardNo) {
		var resultMessageDiv = document.getElementById("resultMessage");
		
		resultMessageDiv.setAttribute("class", "alert alert-success");
		resultMessageDiv.innerHTML = "<strong>Success!</strong> 게시글[" + boardNo+ "] 삭제가 완료되었습니다";
		
		alert(resultMessageDiv.innerText);
		
		if (opener != undefined) {
			opener.document.location.reload();
			self.close();
		} else {
			document.location.reload();
		}
	}
	
	function goVote(boardNo) {	
		var iv = buildIV();
		
		var g = document.voteFrm;
		
		g.<%= WebCommonStaticFinalVars.PARAMETER_KEY_NAME_OF_SESSION_KEY %>.value = getSessionkeyBase64FromSessionStorage();		
		g.<%= WebCommonStaticFinalVars.PARAMETER_KEY_NAME_OF_SESSION_KEY_IV %>.value = CryptoJS.enc.Base64.stringify(iv);
		
		g.boardNo.value = boardNo;
				
		g.submit();
	}
	
	function callBackForBoardVoteProcess(boardNo) {
		var resultMessageDiv = document.getElementById("resultMessage");
		
		resultMessageDiv.setAttribute("class", "alert alert-success");
		resultMessageDiv.innerHTML = "<strong>Success!</strong> 게시글[" + boardNo+ "] 추천이 완료되었습니다";
		
		var voteDiv = document.getElementById("voteOfBoard"+boardNo);
		
		var numberOfVote = parseInt(voteDiv.innerText, 10);
		
		numberOfVote++;
		
		voteDiv.innerText = "" + numberOfVote;
	}
	
	function goBoardChangeHistory(boardNo) {
		var g = document.boardChangeHistoryFrm;
		g.boardNo.value = boardNo;		
		g.submit();
	}
	
	function goList() {	
		var g = document.goListFrm;		
		g.submit();
	}
	
	function downloadFile(boardNo, attachedFileSeq) {
		var g = document.goDownloadFrm;
		g.boardNo.value = boardNo;
		g.attachedFileSeq.value = attachedFileSeq;
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

	function restoreOldAttachedFileList(boardNo) {
		var oldAttachedFileListJosnObj = JSON.parse(document.getElementById('oldAttachedFileListJosnStringOfBoard'+boardNo+'InViewScreen').innerText);
		var oldAttachedFileListDiv = document.getElementById('oldAttachedFileList');		
		
		/** remove all child nodes of oldFileListInModifyPartForBoard node */
		while(oldAttachedFileListDiv.hasChildNodes()) {
			oldAttachedFileListDiv.removeChild(oldAttachedFileListDiv.firstChild);
		}
		
		for (var i=0; i < oldAttachedFileListJosnObj.length; i++) {
			var oldAttachedFileRowDivID = "oldAttachedFileRow"+oldAttachedFileListJosnObj[i].attachedFileSeq;
			
			var oldAttachedFileRowDiv = document.createElement("div");
			oldAttachedFileRowDiv.setAttribute("class", "row");
			oldAttachedFileRowDiv.setAttribute("id", oldAttachedFileRowDivID);			
			
			var oldAttachedFileColDiv = document.createElement("div");
			oldAttachedFileColDiv.setAttribute("class", "col-sm-12");
			

			var oldAttachedFileHiddenInputNode = document.createElement("INPUT");
			oldAttachedFileHiddenInputNode.setAttribute("type", "hidden");	
			oldAttachedFileHiddenInputNode.setAttribute("name", "oldAttachedFileSeq");
			oldAttachedFileHiddenInputNode.setAttribute("value", oldAttachedFileListJosnObj[i].attachedFileSeq);			
			
			var fileNameTextNode = document.createTextNode(oldAttachedFileListJosnObj[i].attachedFileName+" ");			
					
			
			var deleteButtonNode = document.createElement("INPUT");
			deleteButtonNode.setAttribute("type", "button");
			deleteButtonNode.setAttribute("id", "oldAttachedFileButton"+oldAttachedFileListJosnObj[i].attachedFileSeq);
			deleteButtonNode.setAttribute("value", "삭제");
			deleteButtonNode.setAttribute("title", "delete file(attachedFileSeq:"+oldAttachedFileListJosnObj[i].attachedFileSeq+", fileName:"+ oldAttachedFileListJosnObj[i].attachedFileName + ")");
			deleteButtonNode.setAttribute("onclick", "deleteOldAttachedFile('"+oldAttachedFileRowDivID+"')");			
			
			oldAttachedFileColDiv.appendChild(oldAttachedFileHiddenInputNode);
			oldAttachedFileColDiv.appendChild(fileNameTextNode);
			oldAttachedFileColDiv.appendChild(deleteButtonNode);
			
			oldAttachedFileRowDiv.appendChild(oldAttachedFileColDiv);
			
			oldAttachedFileListDiv.appendChild(oldAttachedFileRowDiv)
		}		
	}

	function deleteOldAttachedFile(oldAttachedFileRowDivID) {
		var oldAttachedFileListDiv = document.getElementById('oldAttachedFileList');	
		var deleteTagetDiv = document.getElementById(oldAttachedFileRowDivID);
		oldAttachedFileListDiv.removeChild(deleteTagetDiv);
	}

	function showReplyEditScreen(boardID, boardNo, isSubject, isPassword) {	
		var targetDiv = document.getElementById("editorScreenForBoard"+boardNo);
		
		
		/** remove all child nodes of targetDiv node */
		while(targetDiv.hasChildNodes()) {
			targetDiv.removeChild(targetDiv.firstChild);
		}
		
		if (null != currentEditScreenDiv) {
			hideEditScreen();
		}
		
		currentEditScreenDiv = targetDiv;
		targetDiv.style.display = "block";		
		
		var titleTextNode = document.createTextNode("댓글 입력 화면");
		var titleH4Node = document.createElement("h4");
		
		titleH4Node.appendChild(titleTextNode);
		
		targetDiv.appendChild(titleH4Node);
		

		var inputFormNode = document.createElement("form");
		inputFormNode.setAttribute("name", "replyInputFrm");
		inputFormNode.setAttribute("method", "get");
		inputFormNode.setAttribute("onSubmit", "return false;");
		
		var inputFormDiv = document.createElement("div");
		inputFormDiv.setAttribute("class", "form-group");
				
		var newAttachedFileRowSeqHiddenNode = document.createElement("input");
		newAttachedFileRowSeqHiddenNode.setAttribute("type", "hidden");
		newAttachedFileRowSeqHiddenNode.setAttribute("name", "newAttachedFileRowSeq");
		newAttachedFileRowSeqHiddenNode.setAttribute("value", "0");
		
		inputFormDiv.appendChild(newAttachedFileRowSeqHiddenNode);
		
		if (isSubject) {
			var subjetLabelTextNode = document.createTextNode("제목");
			
			var subjectLabelNode = document.createElement("label");
			subjectLabelNode.setAttribute("for", "subjectInEditor");
			subjectLabelNode.appendChild(subjetLabelTextNode);
			
			var subjectInputNode = document.createElement("input");
			subjectInputNode.setAttribute("type", "text");
			subjectInputNode.setAttribute("id", "subjectInEditor");
			subjectInputNode.setAttribute("name", "subject");
			subjectInputNode.setAttribute("class", "form-control");
			subjectInputNode.setAttribute("placeholder", "Enter subject");			
			
			inputFormDiv.appendChild(subjectLabelNode);
			inputFormDiv.appendChild(subjectInputNode);
		}
		
		var contentsLabelTextNode = document.createTextNode("내용");
		
		var contentsLabelNode = document.createElement("label");
		contentsLabelNode.setAttribute("for", "contentsInEditor");
		contentsLabelNode.appendChild(contentsLabelTextNode);
		
		var contentsInputNode = document.createElement("textarea");		
		contentsInputNode.setAttribute("name", "contents");
		contentsInputNode.setAttribute("id", "contentsInEditor");
		contentsInputNode.setAttribute("class", "form-control");
		contentsInputNode.setAttribute("placeholder", "Enter contents");		
		contentsInputNode.setAttribute("rows", "5");
		
		inputFormDiv.appendChild(contentsLabelNode);
		inputFormDiv.appendChild(contentsInputNode);
		
		if (isPassword) {
			var passwordLabelTextNode = document.createTextNode("비밀번호");
			
			var passwordLabelNode = document.createElement("label");
			passwordLabelNode.setAttribute("for", "passwordInEditor");
			passwordLabelNode.appendChild(passwordLabelTextNode);
			
			var passwordInputNode = document.createElement("input");
			passwordInputNode.setAttribute("type", "password");
			passwordInputNode.setAttribute("id", "passwordInEditor");
			passwordInputNode.setAttribute("name", "pwd");
			passwordInputNode.setAttribute("class", "form-control");
			passwordInputNode.setAttribute("placeholder", "Enter password");	
			
			
			var passwordConfirmLabelTextNode = document.createTextNode("비밀번호 확인");
			var passwordConfirmLabelNode = document.createElement("label");
			passwordConfirmLabelNode.setAttribute("for", "passwordConfirmInEditor");
			passwordConfirmLabelNode.appendChild(passwordConfirmLabelTextNode);
			
			var passwordConfirmInputNode = document.createElement("input");
			passwordConfirmInputNode.setAttribute("type", "password");
			passwordConfirmInputNode.setAttribute("id", "passwordConfirmInEditor");
			passwordConfirmInputNode.setAttribute("name", "pwdConfirm");
			passwordConfirmInputNode.setAttribute("class", "form-control");
			passwordConfirmInputNode.setAttribute("placeholder", "Enter password confirm");			
			
			inputFormDiv.appendChild(passwordLabelNode);
			inputFormDiv.appendChild(passwordInputNode);
			
			inputFormDiv.appendChild(passwordConfirmLabelNode);
			inputFormDiv.appendChild(passwordConfirmInputNode);
		}
		
		inputFormNode.appendChild(inputFormDiv);
		
		targetDiv.appendChild(inputFormNode);
		
		var functionDiv = document.createElement("div");
		functionDiv.setAttribute("class", "btn-group");
		
		var saveButtonNode = document.createElement("input");
		saveButtonNode.setAttribute("type", "button");
		saveButtonNode.setAttribute("class", "btn btn-default");		
		saveButtonNode.setAttribute("onClick", "goReply();");
		saveButtonNode.setAttribute("value", "저장");
		
		var addNewAttachedFIleButtonNode = document.createElement("input");
		addNewAttachedFIleButtonNode.setAttribute("type", "button");
		addNewAttachedFIleButtonNode.setAttribute("class", "btn btn-default");		
		addNewAttachedFIleButtonNode.setAttribute("onClick", "addNewAttachedFile(document.replyInputFrm);");
		addNewAttachedFIleButtonNode.setAttribute("value", "신규 첨부 파일 추가");		
		
		var hideButtonNode = document.createElement("input");
		hideButtonNode.setAttribute("type", "button");
		hideButtonNode.setAttribute("class", "btn btn-default");		
		hideButtonNode.setAttribute("onClick", "hideEditScreen();");
		hideButtonNode.setAttribute("value", "닫기");
			
		
		functionDiv.appendChild(saveButtonNode);
		functionDiv.appendChild(addNewAttachedFIleButtonNode);
		functionDiv.appendChild(hideButtonNode);
				
		targetDiv.appendChild(functionDiv);
		
		
		var processFormNode = document.createElement("form");
		processFormNode.setAttribute("name", "replyProcessFrm");
		processFormNode.setAttribute("target", "hiddenFrame");
		processFormNode.setAttribute("method", "post");
		processFormNode.setAttribute("action", "/servlet/BoardReplyProcess");
		processFormNode.setAttribute("enctype", "multipart/form-data");
		
		
		var boardIDHiddenNode = document.createElement("input");
		boardIDHiddenNode.setAttribute("type", "hidden");
		boardIDHiddenNode.setAttribute("name", "boardID");
		boardIDHiddenNode.setAttribute("value", boardID);
		
		
		var parentBoardNoHiddenNode = document.createElement("input");
		parentBoardNoHiddenNode.setAttribute("type", "hidden");
		parentBoardNoHiddenNode.setAttribute("name", "parentBoardNo");
		parentBoardNoHiddenNode.setAttribute("value", boardNo);
		
		var contentsHiddenNode = document.createElement("input");
		contentsHiddenNode.setAttribute("type", "hidden");
		contentsHiddenNode.setAttribute("name", "contents");
		
		var sessionkeyHiddenNode = document.createElement("input");
		sessionkeyHiddenNode.setAttribute("type", "hidden");
		sessionkeyHiddenNode.setAttribute("name", "<%= WebCommonStaticFinalVars.PARAMETER_KEY_NAME_OF_SESSION_KEY %>");
		
		
		var ivHiddenNode = document.createElement("input");
		ivHiddenNode.setAttribute("type", "hidden");
		ivHiddenNode.setAttribute("name", "<%= WebCommonStaticFinalVars.PARAMETER_KEY_NAME_OF_SESSION_KEY_IV %>");
		
		var newAttachedFileListDiv = document.createElement("div");
		newAttachedFileListDiv.setAttribute("id", "newAttachedFileList");		
		
		processFormNode.appendChild(boardIDHiddenNode);
		processFormNode.appendChild(parentBoardNoHiddenNode);
		if (isSubject) {
			var subjectHiddenNode = document.createElement("input");
			subjectHiddenNode.setAttribute("type", "hidden");
			subjectHiddenNode.setAttribute("name", "subject");
			
			processFormNode.appendChild(subjectHiddenNode);
		}
		processFormNode.appendChild(contentsHiddenNode);		
		processFormNode.appendChild(sessionkeyHiddenNode);		
		processFormNode.appendChild(ivHiddenNode);
		processFormNode.appendChild(newAttachedFileListDiv);
		
		if (isPassword) {
			var passwordHiddenNode = document.createElement("input");
			passwordHiddenNode.setAttribute("type", "hidden");
			passwordHiddenNode.setAttribute("name", "pwd");
			
			processFormNode.appendChild(passwordHiddenNode);
		}		
		
		targetDiv.appendChild(processFormNode);
		
		expandTextarea('contentsInEditor');
	}

	function showMoidfyEditScreen(boardID, boardNo, nextAttachedFileSeq, isSubject, isPassword) {
		var targetDiv = document.getElementById("editorScreenForBoard"+boardNo);
		var oldAttachedFileListJosnObj = JSON.parse(document.getElementById('oldAttachedFileListJosnStringOfBoard'+boardNo+'InViewScreen').innerText);
		
		/** remove all child nodes of targetDiv node */
		while(targetDiv.hasChildNodes()) {
			targetDiv.removeChild(targetDiv.firstChild);
		}
		
		if (null != currentEditScreenDiv) {
			hideEditScreen();
		}
		
		currentEditScreenDiv = targetDiv;
		targetDiv.style.display = "block";		
		
		var titleTextNode = document.createTextNode("게시글 수정 화면");
		var titleH4Node = document.createElement("h4");
		
		titleH4Node.appendChild(titleTextNode);
		
		targetDiv.appendChild(titleH4Node);
		

		var inputFormNode = document.createElement("form");
		inputFormNode.setAttribute("name", "modifyInputFrm");
		inputFormNode.setAttribute("method", "get");
		inputFormNode.setAttribute("onSubmit", "return false;");
		
		var inputFormDiv = document.createElement("div");
		inputFormDiv.setAttribute("class", "form-group");
		
		var newAttachedFileRowSeqHiddenNode = document.createElement("input");
		newAttachedFileRowSeqHiddenNode.setAttribute("type", "hidden");
		newAttachedFileRowSeqHiddenNode.setAttribute("name", "newAttachedFileRowSeq");
		newAttachedFileRowSeqHiddenNode.setAttribute("value", "0");
		
		inputFormDiv.appendChild(newAttachedFileRowSeqHiddenNode);
		
		if (isSubject) {			
			var subjetLabelTextNode = document.createTextNode("제목");
			
			var subjectLabelNode = document.createElement("label");
			subjectLabelNode.setAttribute("for", "subjectInEditor");
			subjectLabelNode.appendChild(subjetLabelTextNode);
			
			var subjectInputNode = document.createElement("input");
			subjectInputNode.setAttribute("type", "text");
			subjectInputNode.setAttribute("id", "subjectInEditor");
			subjectInputNode.setAttribute("name", "subject");
			subjectInputNode.setAttribute("class", "form-control");
			subjectInputNode.setAttribute("placeholder", "Enter subject");			
			
			inputFormDiv.appendChild(subjectLabelNode);
			inputFormDiv.appendChild(subjectInputNode);
		}
		
		var contentsLabelTextNode = document.createTextNode("내용");
		
		var contentsLabelNode = document.createElement("label");
		contentsLabelNode.setAttribute("for", "contentsInEditor");
		contentsLabelNode.appendChild(contentsLabelTextNode);
		
		var contentsInputNode = document.createElement("textarea");		
		contentsInputNode.setAttribute("name", "contents");
		contentsInputNode.setAttribute("id", "contentsInEditor");
		contentsInputNode.setAttribute("class", "form-control");
		contentsInputNode.setAttribute("placeholder", "Enter contents");		
		contentsInputNode.setAttribute("rows", "5");
		
		inputFormDiv.appendChild(contentsLabelNode);
		inputFormDiv.appendChild(contentsInputNode);
		
		if (isPassword) {
			var subjetLabelTextNode = document.createTextNode("비밀번호");
			
			var passwordLabelNode = document.createElement("label");
			passwordLabelNode.setAttribute("for", "passwordInEditor");
			passwordLabelNode.appendChild(subjetLabelTextNode);
			
			var passwordInputNode = document.createElement("input");
			passwordInputNode.setAttribute("type", "password");
			passwordInputNode.setAttribute("id", "passwordInEditor");
			passwordInputNode.setAttribute("name", "pwd");
			passwordInputNode.setAttribute("class", "form-control");
			passwordInputNode.setAttribute("placeholder", "Enter password");			
			
			inputFormDiv.appendChild(passwordLabelNode);
			inputFormDiv.appendChild(passwordInputNode);
		}
		
		inputFormNode.appendChild(inputFormDiv);
		
		targetDiv.appendChild(inputFormNode);
		
		var functionDiv = document.createElement("div");
		functionDiv.setAttribute("class", "btn-group");
		
		var saveButtonNode = document.createElement("input");
		saveButtonNode.setAttribute("type", "button");
		saveButtonNode.setAttribute("class", "btn btn-default");		
		saveButtonNode.setAttribute("onClick", "goModify();");
		saveButtonNode.setAttribute("value", "저장");
		
		
		var restoreOldAttachedFileListButtonNode = document.createElement("input");
		restoreOldAttachedFileListButtonNode.setAttribute("type", "button");
		restoreOldAttachedFileListButtonNode.setAttribute("class", "btn btn-default");		
		restoreOldAttachedFileListButtonNode.setAttribute("onClick", "restoreOldAttachedFileList(" + boardNo + ");");
		restoreOldAttachedFileListButtonNode.setAttribute("value", "기존 첨부 파일 목록 복구");	
		
		var addNewAttachedFIleButtonNode = document.createElement("input");
		addNewAttachedFIleButtonNode.setAttribute("type", "button");
		addNewAttachedFIleButtonNode.setAttribute("class", "btn btn-default");		
		addNewAttachedFIleButtonNode.setAttribute("onClick", "addNewAttachedFile(document.modifyInputFrm);");
		addNewAttachedFIleButtonNode.setAttribute("value", "신규 첨부 파일 추가");		
		
		var hideButtonNode = document.createElement("input");
		hideButtonNode.setAttribute("type", "button");
		hideButtonNode.setAttribute("class", "btn btn-default");		
		hideButtonNode.setAttribute("onClick", "hideEditScreen();");
		hideButtonNode.setAttribute("value", "닫기");
		
		functionDiv.appendChild(saveButtonNode);
		functionDiv.appendChild(restoreOldAttachedFileListButtonNode);
		functionDiv.appendChild(addNewAttachedFIleButtonNode);
		functionDiv.appendChild(hideButtonNode);
				
		targetDiv.appendChild(functionDiv);		
		
		var processFormNode = document.createElement("form");
		processFormNode.setAttribute("name", "modifyProcessFrm");
		processFormNode.setAttribute("target", "hiddenFrame");
		processFormNode.setAttribute("method", "post");
		processFormNode.setAttribute("action", "/servlet/BoardModifyProcess");
		processFormNode.setAttribute("enctype", "multipart/form-data");
		
		var processFormDiv = document.createElement("div");
		processFormDiv.setAttribute("class", "form-group");
		
		var boardIDHiddenNode = document.createElement("input");
		boardIDHiddenNode.setAttribute("type", "hidden");
		boardIDHiddenNode.setAttribute("name", "boardID");
		boardIDHiddenNode.setAttribute("value", boardID);	
		
		var boardNoHiddenNode = document.createElement("input");
		boardNoHiddenNode.setAttribute("type", "hidden");
		boardNoHiddenNode.setAttribute("name", "boardNo");
		boardNoHiddenNode.setAttribute("value", boardNo);
		
		var nextAttachedFileSeqHiddenNode = document.createElement("input");
		nextAttachedFileSeqHiddenNode.setAttribute("type", "hidden");
		nextAttachedFileSeqHiddenNode.setAttribute("name", "nextAttachedFileSeq");
		nextAttachedFileSeqHiddenNode.setAttribute("value", nextAttachedFileSeq);
				
		var contentsHiddenNode = document.createElement("input");
		contentsHiddenNode.setAttribute("type", "hidden");
		contentsHiddenNode.setAttribute("name", "contents");
		
		var sessionkeyHiddenNode = document.createElement("input");
		sessionkeyHiddenNode.setAttribute("type", "hidden");
		sessionkeyHiddenNode.setAttribute("name", "<%= WebCommonStaticFinalVars.PARAMETER_KEY_NAME_OF_SESSION_KEY %>");		
		
		var ivHiddenNode = document.createElement("input");
		ivHiddenNode.setAttribute("type", "hidden");
		ivHiddenNode.setAttribute("name", "<%= WebCommonStaticFinalVars.PARAMETER_KEY_NAME_OF_SESSION_KEY_IV %>");		
		
		
		var oldAttachedFileListDiv = document.createElement("div");
		oldAttachedFileListDiv.setAttribute("id", "oldAttachedFileList");
		
		var newAttachedFileListDiv = document.createElement("div");
		newAttachedFileListDiv.setAttribute("id", "newAttachedFileList");		
		
		processFormDiv.appendChild(boardIDHiddenNode);
		processFormDiv.appendChild(boardNoHiddenNode);
		processFormDiv.appendChild(nextAttachedFileSeqHiddenNode);
		if (isSubject) {
			var subjectHiddenNode = document.createElement("input");
			subjectHiddenNode.setAttribute("type", "hidden");
			subjectHiddenNode.setAttribute("name", "subject");
			
			processFormDiv.appendChild(subjectHiddenNode);
		}
		processFormDiv.appendChild(contentsHiddenNode);		
		processFormDiv.appendChild(sessionkeyHiddenNode);		
		processFormDiv.appendChild(ivHiddenNode);		
		processFormDiv.appendChild(oldAttachedFileListDiv);
		processFormDiv.appendChild(newAttachedFileListDiv);		
		
		if (isPassword) {
			var passwordHiddenNode = document.createElement("input");
			passwordHiddenNode.setAttribute("type", "hidden");
			passwordHiddenNode.setAttribute("name", "pwd");
			
			processFormDiv.appendChild(passwordHiddenNode);
		}		
		
		
		processFormNode.appendChild(processFormDiv);		
		
		targetDiv.appendChild(processFormNode);
				
		var f = document.modifyInputFrm;
		
		if (isSubject) {
			var subjectDiv = document.getElementById("subjectOfBoard"+boardNo+"InViewScreen");
			f.subject.value = subjectDiv.innerText;
		}
		var contentsDiv = document.getElementById("contentsOfBoard"+boardNo+"InViewScreen");
		f.contents.value = contentsDiv.innerText;
		
		restoreOldAttachedFileList(boardNo);
		expandTextarea('contentsInEditor');
	}
	
	function hideEditScreen() {
		if (null != currentEditScreenDiv) {
			currentEditScreenDiv.style.display = "none";
			
			/** remove all child nodes of targetDiv node */
			while(currentEditScreenDiv.hasChildNodes()) {
				currentEditScreenDiv.removeChild(currentEditScreenDiv.firstChild);
			}
			
			currentEditScreenDiv = null;
		}
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
	
	
	function goMemberInformation(targetUserID) {		
		if (opener != undefined) {			
			opener.document.location.href = "/servlet/MemberInformation?targetUserID="+targetUserID;
			self.close();
		} else {
			document.location.href = "/servlet/MemberInformation?targetUserID="+targetUserID;
		}
	}
	
	function goPersonalActivityHistory(targetUserID) {
		if (opener != undefined) {			
			opener.document.location.href = "/servlet/PersonalActivityHistory?targetUserID="+targetUserID;
			self.close();
		} else {
			document.location.href = "/servlet/PersonalActivityHistory?targetUserID="+targetUserID;
		}
	}<%
	
	if (MemberRoleType.ADMIN.equals(accessedUserformation.getMemberRoleType())) {
%>

	function goBlock(boardNo) {	
		var iv = buildIV();
		
		var g = document.goBlockFrm;
		
		g.<%= WebCommonStaticFinalVars.PARAMETER_KEY_NAME_OF_SESSION_KEY %>.value = getSessionkeyBase64FromSessionStorage();
		g.<%= WebCommonStaticFinalVars.PARAMETER_KEY_NAME_OF_SESSION_KEY_IV %>.value = CryptoJS.enc.Base64.stringify(iv);
		
		g.boardNo.value = boardNo;
				
		g.submit();
	}
	
	function callBackForBoardBlockProcess(boardNo) {
		var resultMessageDiv = document.getElementById("resultMessage");
		
		resultMessageDiv.setAttribute("class", "alert alert-success");
		resultMessageDiv.innerHTML = "<strong>Success!</strong> 게시글[" + boardNo+ "] 차단이 완료 되었습니다";
		
		document.location.reload();
	}
	
	function goMove() {
		var f = document.moveFrm;
		var g = document.goMoveFrm;
		
		g.<%= WebCommonStaticFinalVars.PARAMETER_KEY_NAME_OF_SESSION_KEY %>.value = getSessionkeyBase64FromSessionStorage();

		var iv = buildIV();
		g.<%= WebCommonStaticFinalVars.PARAMETER_KEY_NAME_OF_SESSION_KEY_IV %>.value = CryptoJS.enc.Base64.stringify(iv);
		
		g.targetBoardID.value = f.targetBoardID.value;
				
		g.submit();
	}
	
	function callBackForBoardMoveProcess() {
		var resultMessageDiv = document.getElementById("resultMessage");
		
		resultMessageDiv.setAttribute("class", "alert alert-success");
		resultMessageDiv.innerHTML = "<strong>Success!</strong> 게시글 이동 처리가 완료 되었습니다";
		
		var g = document.goMoveFrm;
		
		if (opener != undefined) {
			opener.document.location.href="/servlet/BoardList?boardID="+g.targetBoardID.value;
			self.close();
		} else {
			document.location.href="/servlet/BoardList?boardID="+g.targetBoardID.value;
		}
	}<%
	}
%>
	
	function init() {
		if(typeof(sessionStorage) == "undefined") {
		    alert("Sorry! No HTML5 sessionStorage support..");
		    top.location.href = "/";
		}
		
		var interestedBoadNoDiv = document.getElementById('viewScreenForBoard<%= interestedBoadNo %>');
		
		if (interestedBoadNoDiv != undefined) {
			
			var offsetTop = getOffsetTop(interestedBoadNoDiv);
			var offsetLeft = getOffsetLeft(interestedBoadNoDiv);
			<!-- WARNING! setTimeout 함수에 window.scrollTo 을 넣어야 크롬에서 잘 동작함 -->
			setTimeout( () => { window.scrollTo(offsetLeft, offsetTop); }, 100);
		}	
	}

	window.onload=init;
//-->
</script>
</head>
<body>
	<div class=header>
		<div class="container"><%
	if (BoardListType.ONLY_GROUP_ROOT.equals(boardListType)) {
		out.write(getMenuNavbarString(request));
	}
%>
		</div>
	</div>	
	<form name=goListFrm method="get" action="/servlet/BoardList">
		<input type="hidden" name="boardID" value="<%= boardDetailRes.getBoardID() %>" />
	</form>
	
	<form name=goDetailFrm method="get" action="/servlet/BoardDetail">
		<input type="hidden" name="boardID" value="<%= boardDetailRes.getBoardID() %>" />
		<input type="hidden" name="boardNo" value="<%= boardDetailRes.getBoardNo() %>" />
		<input type="hidden" name="interestedBoadNo" />
	</form>
	
	<form name=deleteProcessFrm target=hiddenFrame method="post" action="/servlet/BoardDeleteProcess">
		<input type="hidden" name="boardID" value="<%=boardDetailRes.getBoardID()%>" />
		<input type="hidden" name="boardNo" />
		<input type="hidden" name="<%= WebCommonStaticFinalVars.PARAMETER_KEY_NAME_OF_SESSION_KEY %>" />
		<input type="hidden" name="<%= WebCommonStaticFinalVars.PARAMETER_KEY_NAME_OF_SESSION_KEY_IV %>" /><%
	if (! accessedUserformation.isLoginedIn()) {
%>
		<input type="hidden" name="pwd" /><%
	}
%>
	</form>
	
	<form name=voteFrm target=hiddenFrame method="post" action="/servlet/BoardVoteProcess">
		<input type="hidden" name="boardID" value="<%=boardDetailRes.getBoardID()%>" />
		<input type="hidden" name="boardNo" />
		<input type="hidden" name="<%=WebCommonStaticFinalVars.PARAMETER_KEY_NAME_OF_SESSION_KEY%>" />
		<input type="hidden" name="<%=WebCommonStaticFinalVars.PARAMETER_KEY_NAME_OF_SESSION_KEY_IV%>" />
	</form>
	
	<form name=boardChangeHistoryFrm method="get" action="/servlet/BoardChangeHistory">
		<input type="hidden" name="boardID" value="<%= boardDetailRes.getBoardID() %>" />
		<input type="hidden" name="boardNo" />
	</form>
	
	<form name=goDownloadFrm target="hiddenFrame" method="post" action="/servlet/BoardDownload">
		<input type="hidden" name="boardID" value="<%=boardDetailRes.getBoardID()%>" />
		<input type="hidden" name="boardNo" />
		<input type="hidden" name="attachedFileSeq" />
	</form><%
	if (MemberRoleType.ADMIN.equals(accessedUserformation.getMemberRoleType())) {
%>

	<form name=goBlockFrm target=hiddenFrame method="post" action="/servlet/BoardBlockProcess">
		<input type="hidden" name="boardID" value="<%= boardDetailRes.getBoardID() %>" />
		<input type="hidden" name="boardNo" />
		<input type="hidden" name="<%=WebCommonStaticFinalVars.PARAMETER_KEY_NAME_OF_SESSION_KEY%>" />
		<input type="hidden" name="<%=WebCommonStaticFinalVars.PARAMETER_KEY_NAME_OF_SESSION_KEY_IV%>" />
	</form>
	<form name=goMoveFrm target=hiddenFrame method="post" action="/servlet/BoardMoveProcess">
		<input type="hidden" name="<%=WebCommonStaticFinalVars.PARAMETER_KEY_NAME_OF_SESSION_KEY%>" />
		<input type="hidden" name="<%=WebCommonStaticFinalVars.PARAMETER_KEY_NAME_OF_SESSION_KEY_IV%>" />
		<input type="hidden" name="sourceBoardID" value="<%= boardDetailRes.getBoardID() %>" />
		<input type="hidden" name="sourceBoardNo" value="<%= boardDetailRes.getBoardNo() %>" />
		<input type="hidden" name="targetBoardID" />
	</form><%
	}
%>
<div class="content">
	<div class="container">
		<div class="panel panel-default">
			<div class="panel-heading"><h4><%= boardDetailRes.getBoardName() %> 게시판 - 상세보기</h4></div>
			<div class="panel-body">
				<div class="btn-group"><%
	if (BoardReplyPolicyType.ALL.equals(boardReplyPolicyType) ||
		(BoardReplyPolicyType.ONLY_ROOT.equals(boardReplyPolicyType) && (0 == boardDetailRes.getParentNo()))) {
		/** 댓글 버튼 유무는 댓글 정책 유형이 본문과 댓글 모두인 경우와 본문에만 허용되는 경우로 결정된다 */
		out.write(CommonStaticFinalVars.NEWLINE);
		out.write("					");
		
		
		out.write("<button type=\"button\" class=\"btn btn-primary btn-sm\" onClick=\"showReplyEditScreen(");
		out.write(String.valueOf(boardDetailRes.getBoardID()));
		out.write(", ");
		out.write(String.valueOf(boardDetailRes.getBoardNo()));
		out.write(", ");
		out.write(String.valueOf(BoardListType.TREE.equals(boardListType) || boardDetailRes.getParentNo() == 0));
		out.write(", ");
		out.write(String.valueOf(! accessedUserformation.isLoginedIn()));
		out.write(")\">댓글</button>");				
	}
	
	if (accessedUserformation.getUserID().equals(boardDetailRes.getFirstWriterID()) || accessedUserformation.isAdmin()) {
		out.write(CommonStaticFinalVars.NEWLINE);
		out.write("					");
		out.write("<button type=\"button\" class=\"btn btn-primary btn-sm\" onClick=\"showMoidfyEditScreen(");
		out.write(String.valueOf(boardDetailRes.getBoardID()));
		out.write(", ");		
		out.write(String.valueOf(boardDetailRes.getBoardNo()));
		out.write(", ");		
		out.write(String.valueOf(boardDetailRes.getNextAttachedFileSeq()));		
		out.write(", ");		
		out.write(String.valueOf(BoardListType.TREE.equals(boardListType) || boardDetailRes.getParentNo() == 0));
		out.write(", ");
		out.write(String.valueOf(boardDetailRes.getIsBoardPassword()));
		out.write(")\">수정</button>");
	}
	
	if (accessedUserformation.getUserID().equals(boardDetailRes.getFirstWriterID())) {								
		if (accessedUserformation.isLoginedIn()) {
			out.write(CommonStaticFinalVars.NEWLINE);
			out.write("						  				");
			out.write("<button type=\"button\" class=\"btn btn-primary btn-sm\" onClick=\"goDelete(");
			out.write(String.valueOf(boardDetailRes.getBoardNo()));
			out.write(")\">삭제</button>");
		} else {
			out.write(CommonStaticFinalVars.NEWLINE);
			out.write("						  				");
			out.write("<button type=\"button\" class=\"btn btn-primary btn-sm\" onClick=\"showDeleteEditScreen(");
			out.write(String.valueOf(boardDetailRes.getBoardNo()));
			out.write(")\">삭제</button>");
		}
	}
	
	if (accessedUserformation.isLoginedIn()) {
		out.write(CommonStaticFinalVars.NEWLINE);
		out.write("					");
		out.write("<button type=\"button\" class=\"btn btn-primary btn-sm\" onClick=\"goVote(");
		out.write(String.valueOf(boardDetailRes.getBoardNo()));
		out.write(")\">추천</button>");
	}
%>
					<button type="button" class="btn btn-primary btn-sm" onClick="goBoardChangeHistory(<%=boardDetailRes.getBoardNo()%>)">수정 이력 조회</button><%
	if (BoardListType.ONLY_GROUP_ROOT.equals(boardListType)) {
		out.write(CommonStaticFinalVars.NEWLINE);
		out.write("					");
		out.write("<button type=\"button\" class=\"btn btn-primary btn-sm\" onClick=\"goList()\">목록으로</button>");
	}
			
	if (accessedUserformation.isAdmin()) {
		out.write(CommonStaticFinalVars.NEWLINE);
		out.write("					");
		out.write("<button type=\"button\" class=\"btn btn-primary btn-sm\" onClick=\"goBlock(");
		out.write(String.valueOf(boardDetailRes.getBoardNo()));
		out.write(")\">차단</button>");
	}
%>
					<button type="button" class="btn btn-primary btn-sm" onClick="clickHiddenFrameButton(this);">Show Hidden Frame</button>
				</div><%
	if (accessedUserformation.isAdmin()) {
		if (0 == boardDetailRes.getParentNo() && (1 == boardDetailRes.getBoardID() || 2 == boardDetailRes.getBoardID())) {
%>
				<br><br><form name=moveFrm class="form-inline" method="post" onSubmit="return false;">
						<div class="form-group">
							<label  for="targetBoardID">이동할 게시판:</label>
														
							<select class="form-control" name="targetBoardID" id="targetBoardID"><%
			if (2 == boardDetailRes.getBoardID()) {
%>
					<option value="1">자유</option><%
			} else {
%>
					<option value="2">이슈</option><%
			}
%>							
							</select>							
							
							<button type="button" class="btn btn-primary btn-sm" onclick="goMove()">게시글 이동</button>
						</div>
					</form><%
		}
	}
%>
				<div id="resultMessage"></div>
				<br>
				<div id="viewScreenForBoard<%=boardDetailRes.getBoardNo()%>">
					<div style="display:none" id="oldAttachedFileListJosnStringOfBoard<%=boardDetailRes.getBoardNo()%>InViewScreen"><%= StringEscapeActorUtil
					.replace((null == boardDetailRes.getAttachedFileList()) ? "[]" : new Gson().toJson(boardDetailRes.getAttachedFileList()), 
							StringEscapeActorUtil.STRING_REPLACEMENT_ACTOR_TYPE.ESCAPEHTML4)%></div>
					<table class="table">
						<thead>
							<tr>
								<th>글번호</th>
								<th><%= boardDetailRes.getBoardNo() %></th>
						    	<th>작성자</th>
						    	<th><%
		if (MemberRoleType.GUEST.equals(firstWriterRoleType)) {			
			out.write(boardDetailRes.getFirstWriterID());
		} else {
			String firstWriterNickName = StringEscapeActorUtil.replace(boardDetailRes.getFirstWriterNickname(), STRING_REPLACEMENT_ACTOR_TYPE.ESCAPEHTML4);
			
			out.write("<div class=\"dropdown\">");
			
			out.write("<a href=\"#\" data-toggle=\"dropdown\">");
			out.write(firstWriterNickName);
			out.write("</a>");			
			
			out.write("<ul class=\"dropdown-menu\" role=\"menu\">");
			
			out.write("<li role=\"presentation\">");
			out.write("<a role=\"menuitem\" tabindex=\"-1\" href=\"#\" onclick=\"goMemberInformation('");
			// out.write("/servlet/MemberInformation?targetUserID=");
			out.write(boardDetailRes.getFirstWriterID());
			out.write("')\">개인 정보</a>");
			out.write("</li>");
			
			out.write("<li role=\"presentation\">");
			out.write("<a role=\"menuitem\" tabindex=\"-1\" href=\"#\" onclick=\"goPersonalActivityHistory('");
			// out.write("/servlet/PersonalActivityHistory?targetUserID=");
			out.write(boardDetailRes.getFirstWriterID());
			out.write("')\">회원 활동 이력 조회</a>");
			out.write("</li>");
			
			out.write("</ul>");
			out.write("</div>");
			
		}
						    	 %></th>
						    	<th>최초 작성일</th>
						    	<th><%=boardDetailRes.getFirstRegisteredDate()%></th>
						  	</tr>
						  	<tr>
								<th>추천수</th>
								<th id="voteOfBoard<%=boardDetailRes.getBoardNo()%>"><%=boardDetailRes.getVotes()%></th>
						    	<th>게시판 상태</th>
						    	<th><%=BoardStateType.valueOf(boardDetailRes.getBoardSate()).getName()%></th>
						    	<th>마지막 수정일</th>
						    	<th><%=boardDetailRes.getLastModifiedDate()%></th>
						  	</tr>
						  	<tr>
						  		<th>제목</th>
						  		<th colspan="5" id="subjectOfBoard<%=boardDetailRes.getBoardNo()%>InViewScreen"><%=StringEscapeActorUtil.replace(boardDetailRes.getSubject(), 
								STRING_REPLACEMENT_ACTOR_TYPE.ESCAPEHTML4)%></th>
						  	</tr>
						</thead>
						<tbody>
							<tr>
								<td colspan="6">
									<article id="contentsOfBoard<%=boardDetailRes.getBoardNo()%>InViewScreen" style="white-space:pre-wrap;"><%=StringEscapeActorUtil.replace(boardDetailRes.getContents(), 
										STRING_REPLACEMENT_ACTOR_TYPE.ESCAPEHTML4)%></article>
								</td>
							</tr>
							<tr>
								<td><b>첨부 파일</b></td>
								<td colspan="5" id="oldFileListOfBoard<%=boardDetailRes.getBoardNo()%>InViewScreen"><%
	if (null != boardDetailRes.getAttachedFileList()) {
		boolean isFirst = true;
		for (BoardDetailRes.AttachedFile oldAttachedFile : boardDetailRes.getAttachedFileList()) {
			if (isFirst) {
				isFirst = false;
			} else {
				out.write("&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;");
			}
			out.write("<a style=\"text-decoration: underline;\" title=\"다운로드 ");
			out.write(StringEscapeActorUtil.replace(oldAttachedFile.getAttachedFileName(), 
			STRING_REPLACEMENT_ACTOR_TYPE.ESCAPEHTML4));
			out.write("(seq:");
			out.write(String.valueOf(oldAttachedFile.getAttachedFileSeq()));
			out.write(", size:");
			out.write(String.valueOf(oldAttachedFile.getAttachedFileSize()));
			out.write(")\" onClick=\"downloadFile(");
			out.write(String.valueOf(boardDetailRes.getBoardNo()));
			out.write(", ");
			out.write(String.valueOf(oldAttachedFile.getAttachedFileSeq()));
			out.write(")\">");
			out.write(StringEscapeActorUtil.replace(oldAttachedFile.getAttachedFileName(), 
			STRING_REPLACEMENT_ACTOR_TYPE.ESCAPEHTML4));
			out.write("</a>");
		}
	}	
%></td>
							</tr>
						</tbody>
					</table>			
					<div id="editorScreenForBoard<%=boardDetailRes.getBoardNo()%>" style="display:block"></div>		
				</div>	
			</div>			
			<iframe id="hiddenFrame" name="hiddenFrame" style="display:none;"></iframe>
		</div><%
	if (null != boardDetailRes.getChildNodeList() && ! boardDetailRes.getChildNodeList().isEmpty()) {
		%>
		
		<div class="panel panel-default">
			<div class="panel-heading">댓글</div>
			<div class="panel-body"><%
		for (BoardDetailRes.ChildNode childNode :  boardDetailRes.getChildNodeList()) {
			MemberRoleType childFirstWriterRoleType = MemberRoleType.valueOf(childNode.getFirstWriterRole());
%>
				<div id="viewScreenForBoard<%=childNode.getBoardNo()%>">
					<div style="display:none" id="oldAttachedFileListJosnStringOfBoard<%=childNode.getBoardNo()%>InViewScreen"><%= StringEscapeActorUtil
					.replace((null == childNode.getAttachedFileList()) ? "[]" : new Gson().toJson(childNode.getAttachedFileList()), 
							StringEscapeActorUtil.STRING_REPLACEMENT_ACTOR_TYPE.ESCAPEHTML4)%></div>
					<table class="table">
						<thead>
							<tr>
								<th>글번호</th>
								<th><%= childNode.getBoardNo() %></th>
						    	<th>작성자</th>
						    	<th><% 
	    	if (MemberRoleType.GUEST.equals(childFirstWriterRoleType)) {			
				out.write(childNode.getFirstWriterID());
			} else {
				String firstWriterNickName = StringEscapeActorUtil.replace(childNode.getFirstWriterNickname(), STRING_REPLACEMENT_ACTOR_TYPE.ESCAPEHTML4);
				
				out.write("<div class=\"dropdown\">");
				
				out.write("<a href=\"#\" data-toggle=\"dropdown\">");
				out.write(firstWriterNickName);
				out.write("</a>");			
				
				out.write("<ul class=\"dropdown-menu\" role=\"menu\">");
				
				out.write("<li role=\"presentation\">");
				out.write("<a role=\"menuitem\" tabindex=\"-1\" href=\"#\" onclick=\"goMemberInformation('");				
				out.write(childNode.getFirstWriterID());
				out.write("')\">회원 정보</a>");
				out.write("</li>");
				
				out.write("<li role=\"presentation\">");
				out.write("<a role=\"menuitem\" tabindex=\"-1\" href=\"#\" onclick=\"goPersonalActivityHistory('");
				// out.write("/servlet/PersonalActivityHistory?targetUserID=");
				out.write(childNode.getFirstWriterID());
				out.write("')\">회원 활동 이력 조회</a>");
				out.write("</li>");
				
				out.write("</ul>");
				out.write("</div>");
				
			}
%></th>
						    	<th>최초 작성일</th>
						    	<th><%=childNode.getFirstRegisteredDate()%></th>
						  	</tr>
						  	<tr>
								<th>추천수</th>
								<th id="voteOfBoard<%=childNode.getBoardNo()%>"><%=childNode.getVotes()%></th>
						    	<th>게시판 상태</th>
						    	<th><%=BoardStateType.valueOf(childNode.getBoardSate()).getName()%></th>
						    	<th>마지막 수정일</th>
						    	<th><%=childNode.getLastModifiedDate()%></th>
						  	</tr>
						  	<tr>
						  		<th>기능</th>
						  		<th colspan="5">
						  			<div class="btn-group"><%
			if (BoardReplyPolicyType.ALL.equals(boardReplyPolicyType) ||
				(BoardReplyPolicyType.ONLY_ROOT.equals(boardReplyPolicyType) && (0 == childNode.getParentNo()))) {
				/** 댓글 버튼 유무는 댓글 정책 유형이 본문과 댓글 모두인 경우와 본문에만 허용되는 경우로 결정된다 */
				out.write(CommonStaticFinalVars.NEWLINE);
				out.write("						  				");
				
				
				out.write("<button type=\"button\" class=\"btn btn-primary btn-sm\" onClick=\"showReplyEditScreen(");
				out.write(String.valueOf(boardDetailRes.getBoardID()));
				out.write(", ");
				out.write(String.valueOf(childNode.getBoardNo()));
				out.write(", ");
				out.write(String.valueOf(BoardListType.TREE.equals(boardListType) || childNode.getParentNo() == 0));
				out.write(", ");
				out.write(String.valueOf(! accessedUserformation.isLoginedIn()));
				out.write(")\">댓글</button>");				
			}
			
			if (accessedUserformation.getUserID().equals(childNode.getFirstWriterID()) || accessedUserformation.isAdmin()) {
				/** 본인 혹은 관리자만 수정 가능 */
				out.write(CommonStaticFinalVars.NEWLINE);
				out.write("						  				");
				out.write("<button type=\"button\" class=\"btn btn-primary btn-sm\" onClick=\"showMoidfyEditScreen(");
				out.write(String.valueOf(boardDetailRes.getBoardID()));
				out.write(", ");		
				out.write(String.valueOf(childNode.getBoardNo()));
				out.write(", ");		
				out.write(String.valueOf(childNode.getNextAttachedFileSeq()));		
				out.write(", ");		
				out.write(String.valueOf(BoardListType.TREE.equals(boardListType) || childNode.getParentNo() == 0));
				out.write(", ");
				out.write(String.valueOf(childNode.getIsBoardPassword()));
				out.write(")\">수정</button>");
			}
			
			if (accessedUserformation.getUserID().equals(childNode.getFirstWriterID())) {
				/** 오직 본인만 삭제 가능, 삭제는 오직 본인만 허용하고자 하여 관리자라도 못함 */
				if (accessedUserformation.isLoginedIn()) {
					out.write(CommonStaticFinalVars.NEWLINE);
					out.write("						  				");
					out.write("<button type=\"button\" class=\"btn btn-primary btn-sm\" onClick=\"goDelete(");
					out.write(String.valueOf(childNode.getBoardNo()));
					out.write(")\">삭제</button>");
				} else {
					out.write(CommonStaticFinalVars.NEWLINE);
					out.write("						  				");
					out.write("<button type=\"button\" class=\"btn btn-primary btn-sm\" onClick=\"showDeleteEditScreen(");
					out.write(String.valueOf(childNode.getBoardNo()));
					out.write(")\">삭제</button>");
				}
				
			}
			
			if (accessedUserformation.isLoginedIn()) {
				out.write(CommonStaticFinalVars.NEWLINE);
				out.write("						  				");
				out.write("<button type=\"button\" class=\"btn btn-primary btn-sm\" onClick=\"goVote(");
				out.write(String.valueOf(childNode.getBoardNo()));
				out.write(")\">추천</button>");
			}
			
			if (MemberRoleType.ADMIN.equals(accessedUserformation.getMemberRoleType())) {
				out.write(CommonStaticFinalVars.NEWLINE);
				out.write("					");
				out.write("<button type=\"button\" class=\"btn btn-primary btn-sm\" onClick=\"goBlock(");
				out.write(String.valueOf(childNode.getBoardNo()));
				out.write(")\">차단</button>");
			}
%>
										<button type="button" class="btn btn-primary btn-sm" onClick="goBoardChangeHistory(<%= childNode.getBoardNo() %>)">수정 이력 조회</button>
									</div>
						  		</th>
						  	</tr>
						</thead>
						<tbody>
							<tr>
								<td colspan="6">
									<article id="contentsOfBoard<%= childNode.getBoardNo() %>InViewScreen" style="white-space:pre-wrap;"><%=StringEscapeActorUtil.replace(childNode.getContents(), 
										STRING_REPLACEMENT_ACTOR_TYPE.ESCAPEHTML4)%></article>
								</td>
							</tr>
							<tr>
								<td><b>첨부 파일</b></td>
								<td colspan="5" id="oldFileListOfBoard<%= childNode.getBoardNo() %>InViewScreen"><%
			
			if (null != childNode.getAttachedFileList()) {
				boolean isFirst = true;
				for (BoardDetailRes.ChildNode.AttachedFile oldAttachedFile : childNode.getAttachedFileList()) {
					if (isFirst) {
						isFirst = false;
					} else {
						out.write("&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;");
					}
					
					out.write("<a style=\"text-decoration: underline;\" title=\"다운로드 ");
					out.write(StringEscapeActorUtil.replace(oldAttachedFile.getAttachedFileName(), 
							STRING_REPLACEMENT_ACTOR_TYPE.ESCAPEHTML4));
					out.write("(seq:");
					out.write(String.valueOf(oldAttachedFile.getAttachedFileSeq()));
					out.write(", size:");
					out.write(String.valueOf(oldAttachedFile.getAttachedFileSize()));
					out.write(")\" onClick=\"downloadFile(");
					out.write(String.valueOf(childNode.getBoardNo()));
					out.write(", ");
					out.write(String.valueOf(oldAttachedFile.getAttachedFileSeq()));
					out.write(")\">");
					out.write(StringEscapeActorUtil.replace(oldAttachedFile.getAttachedFileName(), 
							STRING_REPLACEMENT_ACTOR_TYPE.ESCAPEHTML4));
					out.write("</a>");
				}	
			}			
%></td>
								
							</tr>
						</tbody>
					</table>			
					<div id="editorScreenForBoard<%= childNode.getBoardNo() %>" style="display:block"></div>		
				</div><%
		}
%>			
			</div>
		</div><%
	}
%>
	</div>
</div>
</body>
</html>