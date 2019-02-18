<%@ page import="kr.pe.codda.weblib.htmlstring.HtmlStringUtil"%><%
%><%@ page import="kr.pe.codda.weblib.common.WebCommonStaticFinalVars" %><%
%><%@ page import="kr.pe.codda.weblib.common.BoardType"%><%
%><%@ page extends="kr.pe.codda.weblib.jdf.AbstractUserJSP" language="java" session="true" autoFlush="true" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %><%
	BoardType boardType = (BoardType)request.getAttribute("boardType");
	if (null == boardType) {
		boardType = BoardType.FREE;
	}

%><!DOCTYPE html>
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
<script type="text/javascript">
<!--
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
		
		var rsa = new RSAKey();
		rsa.setPublic("<%=getModulusHexString(request)%>", "10001");
		
		var sessionKeyHex = rsa.encrypt(CryptoJS.enc.Base64.stringify(privateKey));		
		sessionkeyBase64 = CryptoJS.enc.Base64.stringify(CryptoJS.enc.Hex.parse(sessionKeyHex));		
	
		return sessionkeyBase64;
	}
	
	function save() {		
		var f = document.frm;		
		
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
		
		
		var g = document.gofrm;		
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
		
		
		g.subject.value = f.subject.value;
		g.contents.value = f.contents.value;		
		
		
		g.<%= WebCommonStaticFinalVars.PARAMETER_KEY_NAME_OF_SESSION_KEY %>.value 
			= getSessionkeyBase64();
		
		
		var iv = CryptoJS.lib.WordArray.random(<%= WebCommonStaticFinalVars.WEBSITE_IV_SIZE %>);
		g.<%= WebCommonStaticFinalVars.PARAMETER_KEY_NAME_OF_SESSION_KEY_IV %>.value 
			= CryptoJS.enc.Base64.stringify(iv);
				
		g.submit()
	}
	
	function callBackForBoardWriteProcess(boardWriteResObj) {
		alert("게시글 작성이 완료되었습니다");
		
		
		goDetailPage(boardWriteResObj.boardNo);
	}

	function goDetailPage(boardNo) {
		var g = document.goDetailForm;
		g.boardNo.value = boardNo;
		g.<%= WebCommonStaticFinalVars.PARAMETER_KEY_NAME_OF_SESSION_KEY %>.value = getSessionkeyBase64();
		var iv = CryptoJS.lib.WordArray.random(<%= WebCommonStaticFinalVars.WEBSITE_IV_SIZE %>);
		g.<%= WebCommonStaticFinalVars.PARAMETER_KEY_NAME_OF_SESSION_KEY_IV %>.value = CryptoJS.enc.Base64.stringify(iv);		
		g.submit();
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
		
	function removeNewAttachFile(divIdName) {
		var d = document.getElementById('sourceNewFileListDiv');		
		var olddiv = document.getElementById(divIdName);
		d.removeChild(olddiv);
	}
	
	function goList() {
		var g = document.goListForm;
		g.<%= WebCommonStaticFinalVars.PARAMETER_KEY_NAME_OF_SESSION_KEY %>.value = getSessionkeyBase64();
		var iv = CryptoJS.lib.WordArray.random(<%= WebCommonStaticFinalVars.WEBSITE_IV_SIZE %>);
		g.<%= WebCommonStaticFinalVars.PARAMETER_KEY_NAME_OF_SESSION_KEY_IV %>.value = CryptoJS.enc.Base64.stringify(iv);		
		g.submit();		
	}

	
	function init() {
	}

	window.onload=init;
//-->
</script>
</head>
<body>
<%= getWebsiteMenuString(request) %>
	
		
	<form name=goDetailForm method="post" action="/servlet/BoardDetail">
		<input type="hidden" name="boardID" value="<%= boardType.getBoardID() %>" />
		<input type="hidden" name="boardNo" />
		<input type="hidden" name="<%= WebCommonStaticFinalVars.PARAMETER_KEY_NAME_OF_SESSION_KEY %>" />
		<input type="hidden" name="<%= WebCommonStaticFinalVars.PARAMETER_KEY_NAME_OF_SESSION_KEY_IV %>" />	
	</form>
	<form name=goListForm method="post" action="/servlet/BoardList">
		<input type="hidden" name="boardID" value="<%= boardType.getBoardID() %>" />
		<input type="hidden" name="<%= WebCommonStaticFinalVars.PARAMETER_KEY_NAME_OF_SESSION_KEY %>" />
		<input type="hidden" name="<%= WebCommonStaticFinalVars.PARAMETER_KEY_NAME_OF_SESSION_KEY_IV %>" />	
	</form>

	<div class="container-fluid">
		<h3><%= boardType.getName() %> 게시판 - 새글 작성하기</h3>
		<br>
		<form name="frm" enctype="multipart/form-data" method="post" action="/servlet/BoardWriteProcess" onsubmit="return false;">
			<div class="form-group">	
				<label for="subject">제목</label>
				<input type="text" name="subject" id="subject" class="form-control" placeholder="Enter subject" />
				<br>
				<label for="content">내용</label>
				<textarea name="contents" id="contents" class="form-control" placeholder="Enter contents" rows="5"></textarea>
				<br>
				<div class="btn-group">
					<input type="button" class="btn btn-default" onClick="save(); return;" value="저장" />
					<input type="button" class="btn btn-default" onClick="goList()" value="목록으로" />
					<input type="button" class="btn btn-default" onClick="addNewAttachFile()" value="첨부 파일 추가" />			
				</div>
				<br>
				<br>							
			</div>
		</form>
		<form name="gofrm" id="gofrm" enctype="multipart/form-data" method="post" target="hiddenFrame" action="/servlet/BoardWriteProcess">
			<div class="form-group">
				<input type="hidden" name="boardID" value="<%= boardType.getBoardID() %>" />
				<input type="hidden" name="subject" />
				<input type="hidden" name="contents" />
				<input type="hidden" name="<%= WebCommonStaticFinalVars.PARAMETER_KEY_NAME_OF_SESSION_KEY %>" />
				<input type="hidden" name="<%= WebCommonStaticFinalVars.PARAMETER_KEY_NAME_OF_SESSION_KEY_IV %>" />	
				<!-- 주의점 div 시작 태그와 종료 태그 사이에는 공백을 포함한 어떠한 것도 넣지 말것, 자식 노드로 인식됨 -->
				<div id="sourceNewFileListDiv"></div>
			</div>
		</form>
	</div>
	<iframe name="hiddenFrame" style="display:none;"></iframe>
</body>
</html>
