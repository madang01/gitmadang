<%@page import="kr.pe.codda.common.etc.CommonStaticFinalVars"%><%
%><%@page import="kr.pe.codda.weblib.common.BoardReplyPolicyType"%><%
%><%@page import="kr.pe.codda.weblib.common.PermissionType"%><%
%><%@page import="kr.pe.codda.weblib.common.BoardListType"%><%
%><%@page import="com.google.gson.Gson"%><%
%><%@page import="java.util.List"%><%
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
	boolean isTitle =  (BoardListType.TREE.equals(boardListType) || boardDetailRes.getParentNo() == 0);
	// boolean isBoardPassword = boardDetailRes.getIsBoardPassword();
		
	List<BoardDetailRes.AttachedFile> detailAttachedFileList = boardDetailRes.getAttachedFileList();
	
	String detailAttachedFileListJsonString = (null == detailAttachedFileList) ? "[]" : new Gson().toJson(detailAttachedFileList);
	
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
	var detailAttachedFileListJsonObj = <%= detailAttachedFileListJsonString %>;	
	
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
		
		var sessionKeyHex = rsa.encrypt(privateKeyBase64);
		return CryptoJS.enc.Base64.stringify(CryptoJS.enc.Hex.parse(sessionKeyHex));
	}	
	
	function showModifyPart() {
		var subjectDivInViewPartForBoardNode = document.getElementById('subjectDivInViewPartForBoard');
		var cotentDivInViewPartForBoardNode = document.getElementById('cotentDivInViewPartForBoard');
		
		restoreAttachedFileListForBoardModify();
		
		var newFileListDivNode = document.getElementById('newFileListDivInModifyPartForBoard');		
		
		/** remove all child nodes of newFileListDivNode node */
		while(newFileListDivNode.hasChildNodes()) {
			newFileListDivNode.removeChild(newFileListDivNode.firstChild);
		}
		
		var f = document.modifyInputFrmForBoard;<%

	if (isTitle) {
		out.write(CommonStaticFinalVars.NEWLINE);
		out.write(CommonStaticFinalVars.NEWLINE);
		out.write("		");
		out.write("f.subject.value = subjectDivInViewPartForBoardNode.innerText;");
	} 
%>
		f.contents.value = cotentDivInViewPartForBoardNode.innerText;
		
		//$("#modifyInputModal").modal();

		var modifyPartForBoardObj = document.getElementById('modifyPartForBoard');		
		modifyPartForBoardObj.style.display = "block";
		
		hideReplyPart();
	}
	
	function hideModifyPart() {
		var modifyPartForBoardObj = document.getElementById('modifyPartForBoard');
		modifyPartForBoardObj.style.display = "none";
	}
	
	function addNewAttachFileForBoardModify() {		
		var prefixOfNewChildDiv = 'modifyInputAttachedFileRowDiv';		

		var newFileListDivNode = document.getElementById('newFileListDivInModifyPartForBoard');		
		var oldFileListDivNode = document.getElementById('oldFileListDivInModifyPartForBoard');
		
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
		attachedFileNode .setAttribute("name", "newAttachedFile");
		
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
		var newFileListDivNode = document.getElementById('newFileListDivInModifyPartForBoard');		
		var selectedDivNode = document.getElementById(selectedDivID);
		newFileListDivNode.removeChild(selectedDivNode);
	}
	
	function restoreAttachedFileListForBoardModify() {		
		var oldFileListDivNode = document.getElementById('oldFileListDivInModifyPartForBoard');		
		
		/** remove all child nodes of oldFileListDivInModifyPartForBoard node */
		while(oldFileListDivNode.hasChildNodes()) {
			oldFileListDivNode.removeChild(oldFileListDivNode.firstChild);
		}
		
		for (var i=0; i < detailAttachedFileListJsonObj.length; i++) {
			var oldAttachedFileRowDivID = "oldAttachedFileRow"+detailAttachedFileListJsonObj[i].attachedFileSeq;
			
			var oldAttachedFileRowDivNode = document.createElement("div");
			oldAttachedFileRowDivNode.setAttribute("class", "row");
			oldAttachedFileRowDivNode.setAttribute("id", oldAttachedFileRowDivID);

			var oldAttachedFileHiddenInputNode = document.createElement("INPUT");
			oldAttachedFileHiddenInputNode.setAttribute("type", "hidden");	
			oldAttachedFileHiddenInputNode.setAttribute("name", "oldAttachSeq");
			oldAttachedFileHiddenInputNode.setAttribute("value", detailAttachedFileListJsonObj[i].attachedFileSeq);			
			
			var fileNameTextNode = document.createTextNode(detailAttachedFileListJsonObj[i].attachedFileName+" ");
			
			var deleteButtonNode = document.createElement("INPUT");
			deleteButtonNode.setAttribute("type", "button");
			deleteButtonNode.setAttribute("value", "삭제");
			deleteButtonNode.setAttribute("title", "delete file(attachedFileSeq:"+detailAttachedFileListJsonObj[i].attachedFileSeq+", fileName:"+ detailAttachedFileListJsonObj[i].attachedFileName + ")");
			deleteButtonNode.setAttribute("onclick", "deleteOldAttachedFile('"+oldAttachedFileRowDivID+"')");			
			
			oldAttachedFileRowDivNode.appendChild(oldAttachedFileHiddenInputNode);
			oldAttachedFileRowDivNode.appendChild(fileNameTextNode);
			oldAttachedFileRowDivNode.appendChild(deleteButtonNode);
			
			oldFileListDivNode.appendChild(oldAttachedFileRowDivNode)
		}		
	}
	
	function deleteOldAttachedFile(oldAttachedFileRowDivID) {
		var d = document.getElementById('oldFileListDivInModifyPartForBoard');		
		var deleteTagetDiv = document.getElementById(oldAttachedFileRowDivID);
		d.removeChild(deleteTagetDiv);
	}

	function modify(isBoardPassword) {			
		var f = document.modifyInputFrmForBoard;<% 
	if (isTitle) {
		out.write(CommonStaticFinalVars.NEWLINE);
		out.write(CommonStaticFinalVars.NEWLINE);
		out.write("		");
		out.write("if ('' == f.subject.value) {");
		out.write(CommonStaticFinalVars.NEWLINE);
		out.write("			");
		out.write("alert(\"제목을 넣어 주세요\");");
		out.write(CommonStaticFinalVars.NEWLINE);
		out.write("			");
		out.write("f.subject.focus();");
		out.write(CommonStaticFinalVars.NEWLINE);
		out.write("			");
		out.write("return;");
		out.write("		");
		out.write("}");
	} 
%>

		if ('' == f.contents.value) {
			alert("내용을 넣어 주세요.");
			f.contents.focus();
			return;
		}
		
		if (isBoardPassword) {
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

		var newFileListDivNode = document.getElementById('newFileListDivInModifyPartForBoard');		
		var oldFileListDivNode = document.getElementById('oldFileListDivInModifyPartForBoard');
		
		var uploadFileCnt = oldFileListDivNode.childNodes.length + newFileListDivNode.childNodes.length;
			
		if (uploadFileCnt > uploadFileMaxCnt) {
			alert("업로드 할 수 있는 파일 갯수는 최대["+uploadFileMaxCnt+"] 까지 입니다.");
			return;
		}
		
		var g = document.modofyProcessFrmForBoard;
		
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
			= CryptoJS.enc.Base64.stringify(iv);<%
	
	if (isTitle) {
		out.write(CommonStaticFinalVars.NEWLINE);
		out.write(CommonStaticFinalVars.NEWLINE);
		out.write("		");
		out.write("g.subject.value = f.subject.value;");
	} 
%>				
		g.contents.value = f.contents.value;

		if (isBoardPassword) {
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
		
	function showReplyPart() {
		var newFileListDivNode = document.getElementById('newFileListDivInReplyPartForBoard');		
		
		/** remove all child nodes of newFileListDivNode node */
		while(newFileListDivNode.hasChildNodes()) {
			newFileListDivNode.removeChild(newFileListDivNode.firstChild);
		}
		
		// $("#replyInputModal").modal();
			
		var replyPartForBoardObj = document.getElementById('replyPartForBoard');		
		replyPartForBoardObj.style.display = "block";
		
		hideModifyPart();
	}
	
	function hideReplyPart() {
		var replyPartForBoardObj = document.getElementById('replyPartForBoard');
		replyPartForBoardObj.style.display = "none";
	}
	
	function addNewAttachFileForDetailBoardReply() {			
		var prefixOfNewChildDiv = 'replyInputAttachedFileRowDiv';		

		var newFileListDiv = document.getElementById('newFileListDivInReplyPartForBoard');	
		
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
		fileInput.setAttribute("name", "newAttachedFile");
		
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
		var d = document.getElementById('newFileListDivInReplyPartForBoard');		
		var deleteTagetDiv = document.getElementById(divIDName);
		d.removeChild(deleteTagetDiv);
	}
	
	function reply() {
		var f = document.replyInputFrmForBoard;<%
		
	if (isTitle) {
		out.write(CommonStaticFinalVars.NEWLINE);
		out.write(CommonStaticFinalVars.NEWLINE);
		out.write("		");
		out.write("if ('' == f.subject.value) {");
		out.write(CommonStaticFinalVars.NEWLINE);
		out.write("			");
		out.write("alert(\"제목을 넣어 주세요\");");
		out.write(CommonStaticFinalVars.NEWLINE);
		out.write("			");
		out.write("f.subject.focus();");
		out.write(CommonStaticFinalVars.NEWLINE);
		out.write("			");
		out.write("return;");
		out.write("		");
		out.write("}");
	} 
%>

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
				
		var newFileListDivNode = document.getElementById('newFileListDivInReplyPartForBoard');		
		var uploadFileCnt = newFileListDivNode.childNodes.length;	
		
		if (uploadFileCnt > uploadFileMaxCnt) {
			alert("업로드 할 수 있는 파일 갯수는 최대["+uploadFileMaxCnt+"] 까지 입니다.");
			return;
		}
		
		var g = document.replyProcessFrmForBoard;
		
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
		g.<%= WebCommonStaticFinalVars.PARAMETER_KEY_NAME_OF_SESSION_KEY_IV %>.value = CryptoJS.enc.Base64.stringify(iv);<%

	if (isTitle) {
		out.write(CommonStaticFinalVars.NEWLINE);
		out.write(CommonStaticFinalVars.NEWLINE);
		out.write("		");
		out.write("g.subject.value = f.subject.value;"); 
	}
%>
		g.contents.value = f.contents.value;<%

	if (! isUserLoginedIn) {
		out.write(CommonStaticFinalVars.NEWLINE);
		out.write("		");
		out.write("var symmetricKeyObj = CryptoJS.");
		out.write(WebCommonStaticFinalVars.WEBSITE_JAVASCRIPT_SYMMETRIC_KEY_ALGORITHM_NAME);
		out.write(CommonStaticFinalVars.NEWLINE);
		out.write("		");
		out.write("g.pwd.value = symmetricKeyObj.encrypt(f.pwd.value, privateKey, { mode: CryptoJS.mode.CBC, padding: CryptoJS.pad.Pkcs7, iv: iv });");
	}
%>
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
	
	function makeDetailAttachedFileList() {		
		var detailAttachedFileListDivNode = document.getElementById('detailAttachedFileListDiv');		
		
		/** remove all child nodes of oldFileListDivInModifyPartForBoard node */
		while(detailAttachedFileListDivNode.hasChildNodes()) {
			detailAttachedFileListDivNode.removeChild(detailAttachedFileListDivNode.firstChild);
		}
		
		for (var i=0; i < detailAttachedFileListJsonObj.length; i++) {
			var oldAttachedFileRowDivNode = document.createElement("div");
			oldAttachedFileRowDivNode.setAttribute("class", "row");
			
			var oldAttachedFileColDivNode = document.createElement("div");
			oldAttachedFileColDivNode.setAttribute("class", "col-*-*");
						
			var fileNameTextNode = document.createTextNode(detailAttachedFileListJsonObj[i].attachedFileName+" ");
						
			var downloadButtonNode = document.createElement("INPUT");
			downloadButtonNode.setAttribute("type", "button");
			downloadButtonNode.setAttribute("value", "다운로드");
			downloadButtonNode.setAttribute("title", "download file(attachedFileSeq:"+detailAttachedFileListJsonObj[i].attachedFileSeq+", fileName:"+ detailAttachedFileListJsonObj[i].attachedFileName + ")");
			downloadButtonNode.setAttribute("onclick", "downloadFile("+detailAttachedFileListJsonObj[i].attachedFileSeq+")");			
						
			oldAttachedFileColDivNode.appendChild(fileNameTextNode);
			oldAttachedFileColDivNode.appendChild(downloadButtonNode);
			
			oldAttachedFileRowDivNode.appendChild(oldAttachedFileColDivNode);
			
			detailAttachedFileListDivNode.appendChild(oldAttachedFileRowDivNode)
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
		    top.location.href = "/";
		}
		
		rsa.setPublic("<%= getModulusHexString(request) %>", "10001");
		
		makeDetailAttachedFileList();
		restoreAttachedFileListForBoardModify();
		
		expandTextarea('contentsInModifyPartForBoard');
		expandTextarea('contentsInReplyPartForBoard');
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
		out.write("<button type=\"button\" class=\"btn btn-primary btn-sm\" onClick=\"showReplyPart()\">댓글</button>");				
	}
	
	if (boardDetailRes.getFirstWriterID().equals(getLoginedUserIDFromHttpSession(request))) {
		out.write(CommonStaticFinalVars.NEWLINE);
		out.write("					");
		out.write("<button type=\"button\" class=\"btn btn-primary btn-sm\" onClick=\"showModifyPart()\">수정</button>");
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
				<div id="viewPartForBoard">
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
						<div class="col-sm-4" class="col-sm-11" id="subjectDivInViewPartForBoard"><p><%=StringEscapeActorUtil.replace(boardDetailRes.getSubject(), 
								STRING_REPLACEMENT_ACTOR_TYPE.ESCAPEHTML4)%></p></div>
						<div class="col-sm-1" class="col-sm-1" style="background-color:lavender;">조회수</div>
						<div class="col-sm-1" id="voteTxt"><%=boardDetailRes.getViewCount()%></div>
					</div>
					<div class="row">
						<div class="col-sm-1" style="background-color:lavender;">내용</div>
						<div class="col-sm-6" id="cotentDivInViewPartForBoard"><p><%=StringEscapeActorUtil.replace(boardDetailRes.getContents(), 
								STRING_REPLACEMENT_ACTOR_TYPE.ESCAPEHTML4, STRING_REPLACEMENT_ACTOR_TYPE.LINE2BR)%></p></div>
					</div>
					<div class="row">
						<div class="col-sm-1" style="background-color:lavender;">점부파일</div>
						<div class="col-sm-8" id="detailAttachedFileListDiv"></div>
					</div>
				</div>
			
				<div id="replyPartForBoard" style="display:none">
					<h4>댓글 입력 화면</h4>
					<form name="replyInputFrmForBoard" method="post" onSubmit="return false;">							
						<div class="form-group"><%
	if (isTitle) { 
%>
							<label for="subjectInReplyPartForBoard">제목</label>
							<input type="text" id="subjectInReplyPartForBoard" name="subject" class="form-control" placeholder="Enter subject" /><%
		}
%>
					
							<label for="contentsInReplyPartForBoard">내용</label>
							<textarea name="contents" id="contentsInReplyPartForBoard" class="form-control" placeholder="Enter content" rows="5"></textarea><%
	if (! isUserLoginedIn) {
%>
							<label for="pwdInReplyPartForBoard">게시글 비밀번호</label>
							<input type="password" id="pwdInReplyPartForBoard" class="form-control" placeholder="Enter password" name="pwd" /><%		
	}
%>
						</div>
					</form>						
					<div class="btn-group">
						<button type="button" class="btn btn-default" onClick="reply();">저장</button>
						<input type="button" class="btn btn-default" onClick="addNewAttachFileForDetailBoardReply();" value="첨부 파일 추가" />
						<input type="button" class="btn btn-default" onClick="hideReplyPart();" value="닫기" />			
					</div>				
						
					<form name="replyProcessFrmForBoard" target="hiddenFrame" method="post" action="/servlet/BoardReplyProcess" enctype="multipart/form-data">
						<input type="hidden" name="boardID" value="<%=boardDetailRes.getBoardID()%>" />
						<input type="hidden" name="parentBoardNo" value="<%=boardDetailRes.getBoardNo()%>" /><%

	if (! isUserLoginedIn) {
		out.write(CommonStaticFinalVars.NEWLINE);
		out.write("						");
		out.write("<input type=\"hidden\" name=\"pwd\" />");
	}
%>
						<input type="hidden" name="subject" />
						<input type="hidden" name="contents" />
						<input type="hidden" name="<%= WebCommonStaticFinalVars.PARAMETER_KEY_NAME_OF_SESSION_KEY %>" /> 
						<input type="hidden" name="<%= WebCommonStaticFinalVars.PARAMETER_KEY_NAME_OF_SESSION_KEY_IV %>" />
						<!-- 주의점 div 시작 태그와 종료 태그 사이에는 공백을 포함한 어떠한 것도 넣지 말것, 자식 노드로 인식됨 -->
						<div id="newFileListDivInReplyPartForBoard"></div>
					</form>	
				</div>
				<div id="modifyPartForBoard" style="display:none">
					<h4>게시글 수정 화면</h4>
					<form name="modifyInputFrmForBoard" method="post" onSubmit="return false;">							
						 <div class="form-group"><%
	if (isTitle) {
%>
				    		<label for="subjectInModifyPartForBoard">제목</label>
				    		<input type="text" id="subjectInModifyPartForBoard" name="subject" class="form-control" placeholder="Enter subject" /><%
	}
%>				
				    		<label for="contentsInModifyPartForBoard">내용</label>
							<textarea name="contents" id="contentsInModifyPartForBoard" class="form-control" placeholder="Enter content"></textarea><%
	if (boardDetailRes.getIsBoardPassword()) {
%>
							<label for="pwdInMoidfyPartForBoard">게시글 비밀번호</label>
							<input type="password" id="pwdInMoidfyPartForBoard" class="form-control" placeholder="Enter password" name="pwd" /><%		
	}
%>
				 		</div>
					</form>	
					<div class="btn-group">
						<button type="button" class="btn btn-default" onClick="modify(<%= boardDetailRes.getIsBoardPassword() %>)">저장</button>
						<input type="button" class="btn btn-default" onClick="restoreAttachedFileListForBoardModify()" value="기존 첨부 파일 목록 복구" />	
						<input type="button" class="btn btn-default" onClick="addNewAttachFileForBoardModify()" value="신규 첨부 파일 추가" />
						<input type="button" class="btn btn-default" onClick="hideModifyPart();" value="닫기" />									
					</div>
						
					<form name=modofyProcessFrmForBoard target=hiddenFrame method="post" action="/servlet/BoardModifyProcess" enctype="multipart/form-data">
						<input type="hidden" name="boardID" value="<%=boardDetailRes.getBoardID()%>" />
						<input type="hidden" name="boardNo" value="<%=boardDetailRes.getBoardNo()%>" /><%

	if (boardDetailRes.getIsBoardPassword()) {
		out.write(CommonStaticFinalVars.NEWLINE);
		out.write("			");
		out.write("<input type=\"hidden\" name=\"pwd\" />");
	}
%>
						<input type="hidden" name="nextAttachedFileSeq" value="<%= boardDetailRes.getNextAttachedFileSeq() %>" />
						<input type="hidden" name="subject" />
						<input type="hidden" name="contents" />
						<input type="hidden" name="<%= WebCommonStaticFinalVars.PARAMETER_KEY_NAME_OF_SESSION_KEY %>" />
						<input type="hidden" name="<%= WebCommonStaticFinalVars.PARAMETER_KEY_NAME_OF_SESSION_KEY_IV %>" />
						<!-- 주의점 div 시작 태그와 종료 태그 사이에는 공백을 포함한 어떠한 것도 넣지 말것, 자식 노드로 인식됨 -->
						<div id="oldFileListDivInModifyPartForBoard"></div>
						<div id="newFileListDivInModifyPartForBoard"></div>
					</form>	
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