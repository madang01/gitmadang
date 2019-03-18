<%@page import="kr.pe.codda.common.etc.CommonStaticFinalVars"%><%
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
%><jsp:useBean id="paramPageNo" class="java.lang.String" scope="request" /><%
%><jsp:useBean id="boardDetailRes" class="kr.pe.codda.impl.message.BoardDetailRes.BoardDetailRes" scope="request" /><%
	// FIXME!
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
	
	BoardListType boardListType = BoardListType.valueOf(boardDetailRes.getBoardListType());
	BoardReplyPolicyType boardReplyPolicyType = BoardReplyPolicyType.valueOf(boardDetailRes.getBoardReplyPolicyType());	
	PermissionType boardReplyPermissionType = PermissionType.valueOf(boardDetailRes.getBoardReplyPermssionType());		
	
	boolean isUserLoginedIn = isUserLoginedIn(request);
	// boolean isSubject =  (BoardListType.TREE.equals(boardListType) || boardDetailRes.getParentNo() == 0);
	// boolean isBoardPassword = boardDetailRes.getIsBoardPassword();
		
	// List<BoardDetailRes.AttachedFile> detailAttachedFileList = boardDetailRes.getAttachedFileList();
	
	// String detailAttachedFileListJsonString = (null == boardDetailRes.getAttachedFileList()) ? "[]" : new Gson().toJson(boardDetailRes.getAttachedFileList());
	
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
<script type="text/javascript" src="/js/cryptoJS/rollups/aes.js"></script>
<script type="text/javascript" src="/js/cryptoJS/components/core-min.js"></script>
<script type="text/javascript" src="/js/cryptoJS/components/cipher-core-min.js"></script>
<script type="text/javascript">
<!--
	var rsa = new RSAKey();
	
	var regexPwd = /^[A-Za-z0-9\`~!@\#$%<>\^&*\(\)\-=+_\'\[\]\{\}\\\|\:\;\"<>\?,\.\/]{8,15}$/;
	var regexPwdAlpha = /.*[A-Za-z]{1,}.*/;
	var regexPwdDigit = /.*[0-9]{1,}.*/;
	var regexPwdPunct = /.*[\!\"#$%&'()*+,\-\.\/:;<=>\?@\[\\\]^_`\{\|\}~]{1,}.*/;

	var uploadFileMaxCnt = <%= WebCommonStaticFinalVars.WEBSITE_ATTACHED_FILE_MAX_COUNT %>;
	
	function getSessionkey() {
		var privateKeyBase64 = sessionStorage.getItem('<%=WebCommonStaticFinalVars.SESSIONSTORAGE_KEY_NAME_OF_PRIVATEKEY%>');
		
		if (typeof(privateKeyBase64) == 'undefined') {
			var newPrivateKey = CryptoJS.lib.WordArray.random(<%=WebCommonStaticFinalVars.WEBSITE_PRIVATEKEY_SIZE%>);
			var newPrivateKeyBase64 = CryptoJS.enc.Base64.stringify(newPrivateKey);
			
			sessionStorage.setItem('<%=WebCommonStaticFinalVars.SESSIONSTORAGE_KEY_NAME_OF_PRIVATEKEY%>', newPrivateKeyBase64);
			
			privateKeyBase64 = newPrivateKeyBase64;
		}
		
		var sessionKeyHex = rsa.encrypt(privateKeyBase64);
		return CryptoJS.enc.Base64.stringify(CryptoJS.enc.Hex.parse(sessionKeyHex));
	}	
	
	function modify() {			
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
			if (f.pwd.value == '') {
				alert("비밀번호를 넣어주세요.");
				f.pwd.focus();
				return;
			}
			
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
			}
		}

		var newFileListDivNode = document.getElementById('newAttachedFileList');		
		var oldFileListDivNode = document.getElementById('oldAttachedFileList');
		
		var uploadFileCnt = oldFileListDivNode.childNodes.length + newFileListDivNode.childNodes.length;
			
		if (uploadFileCnt > uploadFileMaxCnt) {
			alert("업로드 할 수 있는 파일 갯수는 최대["+uploadFileMaxCnt+"] 까지 입니다.");
			return;
		}
		
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

		if (f.subject != undefined) {
			g.subject.value = f.subject.value;
		}
		g.contents.value = f.contents.value;

		if (f.pwd != undefined) {
			var symmetricKeyObj = CryptoJS.<%= WebCommonStaticFinalVars.WEBSITE_JAVASCRIPT_SYMMETRIC_KEY_ALGORITHM_NAME %>;
			g.pwd.value = symmetricKeyObj.encrypt(f.pwd.value, privateKey, { mode: CryptoJS.mode.CBC, padding: CryptoJS.pad.Pkcs7, iv: iv });
		}

		g.submit();		
		
		return;	
	}
	
	function callBackForBoardModifyProcess(boardModifyRes) {
		alert("게시글["+boardModifyRes.boardNo+"] 수정이 완료되었습니다");
		document.location.reload();
	}
	
	function reply() {
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
			if (f.pwd.value == '') {
				alert("비밀번호를 넣어주세요.");
				f.pwd.focus();
				return;
			}

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
			}
		}		
				
		var newFileListDivNode = document.getElementById('newAttahcedFileList');		
		var uploadFileCnt = newFileListDivNode.childNodes.length;	
		
		if (uploadFileCnt > uploadFileMaxCnt) {
			alert("업로드 할 수 있는 파일 갯수는 최대["+uploadFileMaxCnt+"] 까지 입니다.");
			return;
		}
		
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
		g.<%= WebCommonStaticFinalVars.PARAMETER_KEY_NAME_OF_SESSION_KEY_IV %>.value = CryptoJS.enc.Base64.stringify(iv);
		
		
		if (f.subject != undefined) {
			g.subject.value = f.subject.value;
		}
		g.contents.value = f.contents.value;
		
		if (f.pwd != undefined) {
			var symmetricKeyObj = CryptoJS.<%= WebCommonStaticFinalVars.WEBSITE_JAVASCRIPT_SYMMETRIC_KEY_ALGORITHM_NAME %>;
			g.pwd.value = symmetricKeyObj.encrypt(f.pwd.value, privateKey, { mode: CryptoJS.mode.CBC, padding: CryptoJS.pad.Pkcs7, iv: iv });
		}

		g.submit();
	}
	
	function callBackForBoardReplyProcess(boardWriteResObj) {
		alert("댓글["+boardWriteResObj.boardNo+"] 등록이 완료되었습니다");
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
	
	var currentEditScreenDiv = null;
	
	function hideEditScreen() {
		if (null != currentEditScreenDiv) {
			currentEditScreenDiv.style.display = "none";
			currentEditScreenDiv = null;
		}
	}
	
	function showReplyEditScreen(boardID, boardNo, isSubject, isPassword) {	
		var targetDiv = document.getElementById("editScreenOfBoard"+boardNo);
		
		
		/** remove all child nodes of targetDiv node */
		while(targetDiv.hasChildNodes()) {
			targetDiv.removeChild(targetDiv.firstChild);
		}
		
		if (null != currentEditScreenDiv) {
			currentEditScreenDiv.style.display = "none";
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
		contentsLabelNode.appendChild(subjetLabelTextNode);
		
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
		saveButtonNode.setAttribute("onClick", "reply();");
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
		

		var subjectHiddenNode = document.createElement("input");
		subjectHiddenNode.setAttribute("type", "hidden");
		subjectHiddenNode.setAttribute("name", "subject");
		
		var contentsHiddenNode = document.createElement("input");
		contentsHiddenNode.setAttribute("type", "hidden");
		contentsHiddenNode.setAttribute("name", "contents");
		
		var sessionkeyHiddenNode = document.createElement("input");
		sessionkeyHiddenNode.setAttribute("type", "hidden");
		sessionkeyHiddenNode.setAttribute("name", "sessionkeyBase64");
		
		
		var ivHiddenNode = document.createElement("input");
		ivHiddenNode.setAttribute("type", "hidden");
		ivHiddenNode.setAttribute("name", "ivBase64");
		
		var newAttachedFileListDiv = document.createElement("div");
		newAttachedFileListDiv.setAttribute("id", "newAttachedFileList");		
		
		processFormNode.appendChild(boardIDHiddenNode);
		processFormNode.appendChild(parentBoardNoHiddenNode);
		processFormNode.appendChild(subjectHiddenNode);
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
	}
	
	function showMoidfyEditScreen(boardID, boardNo, nextAttachedFileSeq, isSubject, isPassword) {
		var targetDiv = document.getElementById("editScreenOfBoard"+boardNo);
		var oldAttachedFileListJosnObj = JSON.parse(document.getElementById('oldAttachedFileListJosnStringOfBoard'+boardNo+'InViewScreen').value);
		
		/** remove all child nodes of targetDiv node */
		while(targetDiv.hasChildNodes()) {
			targetDiv.removeChild(targetDiv.firstChild);
		}
		
		if (null != currentEditScreenDiv) {
			currentEditScreenDiv.style.display = "none";
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
		contentsLabelNode.appendChild(subjetLabelTextNode);
		
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
		saveButtonNode.setAttribute("onClick", "modify();");
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
		

		var subjectHiddenNode = document.createElement("input");
		subjectHiddenNode.setAttribute("type", "hidden");
		subjectHiddenNode.setAttribute("name", "subject");
		
		var contentsHiddenNode = document.createElement("input");
		contentsHiddenNode.setAttribute("type", "hidden");
		contentsHiddenNode.setAttribute("name", "contents");
		
		var sessionkeyHiddenNode = document.createElement("input");
		sessionkeyHiddenNode.setAttribute("type", "hidden");
		sessionkeyHiddenNode.setAttribute("name", "sessionkeyBase64");		
		
		var ivHiddenNode = document.createElement("input");
		ivHiddenNode.setAttribute("type", "hidden");
		ivHiddenNode.setAttribute("name", "ivBase64");		
		
		
		var oldAttachedFileListDiv = document.createElement("div");
		oldAttachedFileListDiv.setAttribute("id", "oldAttachedFileList");
		
		var newAttachedFileListDiv = document.createElement("div");
		newAttachedFileListDiv.setAttribute("id", "newAttachedFileList");		
		
		processFormNode.appendChild(boardIDHiddenNode);
		processFormNode.appendChild(boardNoHiddenNode);
		processFormNode.appendChild(nextAttachedFileSeqHiddenNode);
		processFormNode.appendChild(subjectHiddenNode);
		processFormNode.appendChild(contentsHiddenNode);		
		processFormNode.appendChild(sessionkeyHiddenNode);		
		processFormNode.appendChild(ivHiddenNode);		
		processFormNode.appendChild(oldAttachedFileListDiv);
		processFormNode.appendChild(newAttachedFileListDiv);		
		
		if (isPassword) {
			var passwordHiddenNode = document.createElement("input");
			passwordHiddenNode.setAttribute("type", "hidden");
			passwordHiddenNode.setAttribute("name", "pwd");
			
			processFormNode.appendChild(passwordHiddenNode);
		}		
		
		targetDiv.appendChild(processFormNode);	
		
		var subjectDiv = document.getElementById("subjectOfBoard"+boardNo+"InViewScreen");
		var contentsDiv = document.getElementById("contentsOfBoard"+boardNo+"InViewScreen");
				
		var f = document.modifyInputFrm;
		f.subject.value = subjectDiv.innerText;
		f.contents.value = contentsDiv.innerText;
		
		restoreOldAttachedFileList(boardNo);
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
			
		if (uploadFileCnt >= uploadFileMaxCnt) {
			alert("업로드 할 수 있는 파일 갯수는 최대["+uploadFileMaxCnt+"] 까지 입니다.");
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
		var oldAttachedFileListJosnObj = JSON.parse(document.getElementById('oldAttachedFileListJosnStringOfBoard'+boardNo+'InViewScreen').value);
		var oldAttachedFileListDiv = document.getElementById('oldAttachedFileList');		
		
		/** remove all child nodes of oldFileListInModifyPartForBoard node */
		while(oldAttachedFileListDiv.hasChildNodes()) {
			oldAttachedFileListDiv.removeChild(oldAttachedFileListDiv.firstChild);
		}
		
		for (var i=0; i < oldAttachedFileListJosnObj.length; i++) {
			var oldAttachedFileRowDivID = "oldAttachedFileRow"+oldAttachedFileListJosnObj[i].attachedFileSeq;
			
			var oldAttachedFileRowDivNode = document.createElement("div");
			oldAttachedFileRowDivNode.setAttribute("class", "row");
			oldAttachedFileRowDivNode.setAttribute("id", oldAttachedFileRowDivID);

			var oldAttachedFileHiddenInputNode = document.createElement("INPUT");
			oldAttachedFileHiddenInputNode.setAttribute("type", "hidden");	
			oldAttachedFileHiddenInputNode.setAttribute("name", "oldAttachSeq");
			oldAttachedFileHiddenInputNode.setAttribute("value", oldAttachedFileListJosnObj[i].attachedFileSeq);			
			
			var fileNameTextNode = document.createTextNode(oldAttachedFileListJosnObj[i].attachedFileName+" ");
			
			var deleteButtonNode = document.createElement("INPUT");
			deleteButtonNode.setAttribute("type", "button");
			deleteButtonNode.setAttribute("value", "삭제");
			deleteButtonNode.setAttribute("title", "delete file(attachedFileSeq:"+oldAttachedFileListJosnObj[i].attachedFileSeq+", fileName:"+ oldAttachedFileListJosnObj[i].attachedFileName + ")");
			deleteButtonNode.setAttribute("onclick", "deleteOldAttachedFile('"+oldAttachedFileRowDivID+"')");			
			
			oldAttachedFileRowDivNode.appendChild(oldAttachedFileHiddenInputNode);
			oldAttachedFileRowDivNode.appendChild(fileNameTextNode);
			oldAttachedFileRowDivNode.appendChild(deleteButtonNode);
			
			oldAttachedFileListDiv.appendChild(oldAttachedFileRowDivNode)
		}		
	}
	
	function deleteOldAttachedFile(oldAttachedFileRowDivID) {
		var oldAttachedFileListDiv = document.getElementById('oldAttachedFileList');	
		var deleteTagetDiv = document.getElementById(oldAttachedFileRowDivID);
		oldAttachedFileListDiv.removeChild(deleteTagetDiv);
	}
	
	function showOldAttachedFileList(boardNo) {		
		var targetDiv = document.getElementById('oldFileListOfBoard'+boardNo+'InViewScreen');
		var oldAttachedFileListJosnObj = JSON.parse(document.getElementById('oldAttachedFileListJosnStringOfBoard'+boardNo+'InViewScreen').value);

		/** remove all child nodes of oldFileListInModifyPartForBoard node */
		while(targetDiv.hasChildNodes()) {
			targetDiv.removeChild(targetDiv.firstChild);
		}
		
		for (var i=0; i < oldAttachedFileListJosnObj.length; i++) {
			var oldAttachedFileRowDivNode = document.createElement("div");
			oldAttachedFileRowDivNode.setAttribute("class", "row");
			
			var oldAttachedFileColDivNode = document.createElement("div");
			oldAttachedFileColDivNode.setAttribute("class", "col-*-*");
						
			var fileNameTextNode = document.createTextNode(oldAttachedFileListJosnObj[i].attachedFileName+" ");
						
			var downloadButtonNode = document.createElement("INPUT");
			downloadButtonNode.setAttribute("type", "button");
			downloadButtonNode.setAttribute("value", "다운로드");
			downloadButtonNode.setAttribute("title", "download file(attachedFileSeq:"+oldAttachedFileListJosnObj[i].attachedFileSeq+", fileName:"+ oldAttachedFileListJosnObj[i].attachedFileName + ")");
			downloadButtonNode.setAttribute("onclick", "downloadFile("+oldAttachedFileListJosnObj[i].attachedFileSeq+")");			
						
			oldAttachedFileColDivNode.appendChild(fileNameTextNode);
			oldAttachedFileColDivNode.appendChild(downloadButtonNode);
			
			oldAttachedFileRowDivNode.appendChild(oldAttachedFileColDivNode);
			
			targetDiv.appendChild(oldAttachedFileRowDivNode)
		}		
	}
	
	function init() {
		if(typeof(sessionStorage) == "undefined") {
		    alert("Sorry! No HTML5 sessionStorage support..");
		    top.location.href = "/";
		}
		
		rsa.setPublic("<%= getModulusHexString(request) %>", "10001");
		
		// makeDetailAttachedFileList();
		// restoreAttachedFileListForBoardModify();
		
		// expandTextarea('contentsInModifyPartForBoard');
		// expandTextarea('contentsInReplyPartForBoard');
		
		showOldAttachedFileList(<%= boardDetailRes.getBoardNo() %>);
		
		// showReplyEditScreen(<%= boardDetailRes.getBoardID() %>, <%= boardDetailRes.getBoardNo() %>, <%= (BoardListType.TREE.equals(boardListType) || boardDetailRes.getParentNo() == 0) %>, <%= ! isUserLoginedIn %>);
		
		// showMoidfyEditScreen(<%= boardDetailRes.getBoardID() %>, <%= boardDetailRes.getBoardNo() %>, <%= boardDetailRes.getNextAttachedFileSeq() %>, <%= (BoardListType.TREE.equals(boardListType) || boardDetailRes.getParentNo() == 0) %>, <%= boardDetailRes.getIsBoardPassword() %>);
	}

	window.onload=init;
//-->
</script>
</head>
<body>
<div class=header>
	<div class="container">
<%= getMenuNavbarString(request) %>
	</div>
</div>

	<form name=voteFrm target=hiddenFrame method="post" action="/servlet/BoardVote">
		<input type="hidden" name="boardID" value="<%=boardDetailRes.getBoardID()%>" />
		<input type="hidden" name="boardNo" value="<%=boardDetailRes.getBoardNo()%>" />
		<input type="hidden" name="<%=WebCommonStaticFinalVars.PARAMETER_KEY_NAME_OF_SESSION_KEY%>" />
		<input type="hidden" name="<%=WebCommonStaticFinalVars.PARAMETER_KEY_NAME_OF_SESSION_KEY_IV%>" />
	</form>
	
	<form name=goListFrm method="post" action="/servlet/BoardList">
		<input type="hidden" name="boardID" value="<%= boardDetailRes.getBoardID() %>" />
		<input type="hidden" name="pageNo" value="<%= paramPageNo %>" />
	</form>
	
	<form name=goDownloadFrm target="hiddenFrame" method="post" action="/servlet/BoardDownload">
		<input type="hidden" name="boardID" value="<%=boardDetailRes.getBoardID()%>" />
		<input type="hidden" name="boardNo" value="<%=boardDetailRes.getBoardNo()%>" />
		<input type="hidden" name="attachedFileSeq" />
	</form>
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
		out.write(String.valueOf(! isUserLoginedIn));
		out.write(")\">댓글</button>");				
	}
	
	if (boardDetailRes.getFirstWriterID().equals(getLoginedUserIDFromHttpSession(request))) {
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

	if (isUserLoginedIn) {
		out.write(CommonStaticFinalVars.NEWLINE);
		out.write("					");
		out.write("<button type=\"button\" class=\"btn btn-primary btn-sm\" onClick=\"goVote()\">추천</button>");
	}
%>
					<button type="button" class="btn btn-primary btn-sm" onClick="goList()">목록으로</button>
					<button type="button" class="btn btn-primary btn-sm" onClick="clickHiddenFrameButton(this);">Show Hidden Frame</button>
				</div>
				<div id="resultMessageView"></div>
				<br>
				<div id="viewScreenForBoard<%= boardDetailRes.getBoardNo() %>">
					<div class="row">
						<div class="col-sm-1" style="background-color:lavender;">글번호</div>
						<div class="col-sm-1"><%= boardDetailRes.getBoardNo() %></div>								
						<div class="col-sm-2" style="background-color:lavender;">작성자</div>
						<div class="col-sm-1"><%= StringEscapeActorUtil.replace(boardDetailRes.getFirstWriterNickname(), 
								STRING_REPLACEMENT_ACTOR_TYPE.ESCAPEHTML4) %></div>
						<div class="col-sm-2" style="background-color:lavender;">최초 작성일</div>	
						<div class="col-sm-2"><%=boardDetailRes.getFirstRegisteredDate()%></div>							
					</div>
					<div class="row">				
						<div class="col-sm-1" style="background-color:lavender;">추천수</div>
						<div class="col-sm-1" id="voteTxt"><%=boardDetailRes.getVotes()%></div>
						<div class="col-sm-2" style="background-color:lavender;">게시판 상태</div>
						<div class="col-sm-1"><%=BoardStateType.valueOf(boardDetailRes.getBoardSate(), false).getName()%></div>
						<div class="col-sm-2" class="col-sm-2" style="background-color:lavender;">마지막 수정일</div>
						<div class="col-sm-2" class="col-sm-2"><%=boardDetailRes.getLastModifiedDate()%></div>				
					</div>
					<div class="row">
						<div class="col-sm-1" style="background-color:lavender;">제목</div>
						<div class="col-sm-4" class="col-sm-11" id="subjectOfBoard<%= boardDetailRes.getBoardNo() %>InViewScreen"><p><%=StringEscapeActorUtil.replace(boardDetailRes.getSubject(), 
								STRING_REPLACEMENT_ACTOR_TYPE.ESCAPEHTML4)%></p></div>
						<div class="col-sm-1" class="col-sm-1" style="background-color:lavender;">조회수</div>
						<div class="col-sm-1" id="voteTxt"><%= boardDetailRes.getViewCount() %></div>
					</div>
					<div class="row">
						<div class="col-sm-1" style="background-color:lavender;">내용</div>
						<div class="col-sm-6" id="contentsOfBoard<%= boardDetailRes.getBoardNo() %>InViewScreen"><p><%=StringEscapeActorUtil.replace(boardDetailRes.getContents(), 
								STRING_REPLACEMENT_ACTOR_TYPE.ESCAPEHTML4, STRING_REPLACEMENT_ACTOR_TYPE.LINE2BR)%></p></div>
					</div>
					<div class="row">
						<div class="col-sm-1" style="background-color:lavender;">점부파일</div>
						<div class="col-sm-8" id="oldFileListOfBoard<%= boardDetailRes.getBoardNo() %>InViewScreen"></div>
					</div>
					<input type="hidden" id="oldAttachedFileListJosnStringOfBoard<%= boardDetailRes.getBoardNo() %>InViewScreen"  value="<%= StringEscapeActorUtil
					.replace((null == boardDetailRes.getAttachedFileList()) ? "[]" : new Gson().toJson(boardDetailRes.getAttachedFileList()), 
							StringEscapeActorUtil.STRING_REPLACEMENT_ACTOR_TYPE.ESCAPEHTML4) %>" />
							
					<div id="editScreenOfBoard<%= boardDetailRes.getBoardNo() %>" style="display:block"></div>		
				</div>	
			</div>			
			<iframe id="hiddenFrame" name="hiddenFrame" style="display:none;"></iframe>
		</div><%
	if (! boardDetailRes.getChildNodeList().isEmpty()) {
%>
		
		<div class="panel panel-default">
			<div class="panel-heading">댓글</div>
			<div class="panel-body">
				<div id="childListPartView"><%
		for (BoardDetailRes.ChildNode childNode :  boardDetailRes.getChildNodeList()) {
%>
					<div id="board<%= childNode.getBoardNo() %>PartView">
						<div class="row">
							<div class="col-sm-1" style="background-color:lavender;">번호</div>
							<div class="col-sm-1"><%= childNode.getBoardNo() %></div>					
							
							<div class="col-sm-2" style="background-color:lavender;">작성자</div>
							<div class="col-sm-1"><%= StringEscapeActorUtil.replace(childNode.getFirstWriterNickname(), 
									StringEscapeActorUtil.STRING_REPLACEMENT_ACTOR_TYPE.ESCAPEHTML4) %></div>
							
							<div class="col-sm-2" style="background-color:lavender;">최초 작성일</div>
							<div class="col-sm-2"><%= childNode.getFirstRegisteredDate() %></div>
							
						</div>
						<div class="row">
							<div class="col-sm-1" style="background-color:lavender;">추천수</div>
							<div class="col-sm-1"><%= childNode.getVotes() %></div>
							<div class="col-sm-2" style="background-color:lavender;">게시판 상태</div>		
							<div class="col-sm-1"><%= BoardStateType.valueOf(childNode.getBoardSate(), false).getName() %></div>					
							<div class="col-sm-2" style="background-color:lavender;">마지막 수정일</div>
							<div class="col-sm-2"><%= childNode.getLastModifiedDate() %></div>
						</div>
						<div class="row">
							<div class="col-sm-1" style="background-color:lavender;">내용</div>
							<div class="col-sm-11"><p><%= StringEscapeActorUtil.replace(childNode.getContents(), 
									StringEscapeActorUtil.STRING_REPLACEMENT_ACTOR_TYPE.ESCAPEHTML4,
									StringEscapeActorUtil.STRING_REPLACEMENT_ACTOR_TYPE.LINE2BR) %></p></div>
						</div>
						
					</div><%
		}
%>
				</div>
			</div>
		</div><%
	}
%>
	</div>
</div>
</body>
</html>