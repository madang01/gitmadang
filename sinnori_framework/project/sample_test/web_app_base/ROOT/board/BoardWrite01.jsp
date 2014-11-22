<%@ page extends="kr.pe.sinnori.common.weblib.AbstractJSP" language="java" session="true" autoFlush="true" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %><%
%><%@ page import="kr.pe.sinnori.common.weblib.WebCommonStaticFinalVars" %><%
%><jsp:useBean id="topmenu" class="java.lang.String" scope="request" /><%
%><jsp:useBean id="leftmenu" class="java.lang.String" scope="request" /><%
%><jsp:useBean id="errorMessage" class="java.lang.String" scope="request" /><%
%><jsp:useBean id="parmBoardId" class="java.lang.String" scope="request" />
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
				<dd><%=escapeHtml(errorMessage, WebCommonStaticFinalVars.LINE2BR_STRING_REPLACER)%></dd>
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

		// FIXME!
		alert(document.attachForm.attachId.value);


		var g = document.gofrm;
		g.attachId.value = document.attachForm.attachId.value;
		g.subject.value = f.subject.value;
		g.content.value = f.content.value;
		g.sessionkeyBase64.value = sessionStorage.getItem('<%=WebCommonStaticFinalVars.SESSIONSTORAGE_SESSIONKEY_NAME%>');
		var iv = CryptoJS.lib.WordArray.random(<%=WebCommonStaticFinalVars.WEBSITE_IV_SIZE%>);
		g.ivBase64.value = CryptoJS.enc.Base64.stringify(iv);
		g.submit();
	}

	function goList() {
		var g = document.listForm;
		g.submit();
	}

	function addNewAttachFile() {		
		var uploadFileMaxCnt = <%=WebCommonStaticFinalVars.WEBSITE_FILEUPLOAD_MAX_COUNT%>;
		var prefixOfChildDiv = 'newChildDiv';
		var maxIndex = -1;		
		var uploadFileCnt = 0;

		var newFileListDiv = document.getElementById('newFileListDiv');
		for (var i=0;i < newFileListDiv.childNodes.length; i++) {
			var childNode = newFileListDiv.childNodes[i];
			
			if (childNode.id.indexOf(prefixOfChildDiv) == 0) {
				uploadFileCnt++;
				var numStr = childNode.id.substring(prefixOfChildDiv.length);

				var num = parseInt(numStr);

				if (maxIndex < num) {
					maxIndex = num;
				}	
			}						
		}
	
		if (uploadFileCnt >= uploadFileMaxCnt) {
			alert("업로드 할 수 있는 파일 갯수는 최대["+uploadFileMaxCnt+"] 까지 입니다.");
			return;
		}
		

		var inx = maxIndex+1;

		var newChilddiv = document.createElement('div');

		var divIdName = prefixOfChildDiv+inx;		

		newChilddiv.setAttribute('id',divIdName);

		
		newChilddiv.innerHTML = "<input type=\"file\" name=\"newAttachFile\" id=\"newAttachFile"+inx+"\" size=\"70\" />&nbsp;<a href=\'#\' id=newAttachFileDeleteLink"+inx+" style=\"visibility:hidden\" onclick=\'removeNewAttachFile(\""+divIdName+"\")\'>삭제</a>";


		newFileListDiv.appendChild(newChilddiv);

		fixVisibleOfNewAttachFileDeleteLinks();
	}

	function removeAllNewAttachFiles() {
		var d = document.getElementById('newFileListDiv');
		for(var i=0; i < d.childNodes.length; i++) {
			d.removeChild(d.childNodes[i]);
		}
	}

	function removeNewAttachFile(divIdName) {
		var d = document.getElementById('newFileListDiv');		
		var olddiv = document.getElementById(divIdName);
		d.removeChild(olddiv);
	}

	function fixVisibleOfNewAttachFileDeleteLinks() {
		var f = document.attachForm;

		if (f.newAttachFile.length != undefined) {
			for (var i=0;i < f.newAttachFile.length; i++) {
				var newAttachFile = f.newAttachFile[i];
				var inx = newAttachFile.id.substring("newAttachFile".length);				

				var newAttachFileDeleteLink = document.getElementById('newAttachFileDeleteLink'+inx);

				if (i+1 < f.newAttachFile.length ) newAttachFileDeleteLink.style.visibility = "visible";
				else newAttachFileDeleteLink.style.visibility = "hidden";				
			}
		}
	}

	function checkForm() {
		var f = document.attachForm;

		alert(f.newAttachFile.length);
	}

	var boardUploadFileOutDTO;

	function restoreOldFiles() {
		var f = document.attachForm;		

		f.attachId.value = boardUploadFileOutDTO.attachId;		

		var prefixOfChildDiv = 'oldChildDiv';
		var oldFileListDiv = document.getElementById('oldFileListDiv');

		for (var i=0; i < boardUploadFileOutDTO.oldAttachFileList.length; i++) {
			var divIdName = prefixOfChildDiv+i;
			
			removeOldAttachFile(divIdName);

			var oldChilddiv = document.createElement('div');			

			oldChilddiv.setAttribute('id',divIdName);		

			oldChilddiv.innerHTML = "<input type=\"hidden\" name=\"oldAttachSeq\" value=\""+boardUploadFileOutDTO.oldAttachFileList[i].attachSeq+"\" /><input type=\"text\" name=\"oldAttachFileName\" disabled=\"disabled\" size=\"70\" value=\""+boardUploadFileOutDTO.oldAttachFileList[i].attachFileName+"\" />&nbsp;<a href=\'#\' onclick=\'removeOldAttachFile(\""+divIdName+"\")\'>삭제</a>";

			oldFileListDiv.appendChild(oldChilddiv);
			
		}
	}

	function callbackUpload(parmBoardUploadFileOutDTO) {
		init();

		if (parmBoardUploadFileOutDTO.isError) {
			alert(parmBoardUploadFileOutDTO.errorMessage);
		} else {
			boardUploadFileOutDTO = parmBoardUploadFileOutDTO;

			var d = document.getElementById('oldFileMenuDiv');
			d.style.visibility = "visible";

			restoreOldFiles();
		}		
	}

	function removeAllOldAttachFiles() {
		var d = document.getElementById('oldFileListDiv');		
		for(var i=0; i < d.childNodes.length; i++) {
			d.removeChild(d.childNodes[i]);
		}	
	}

	function removeOldAttachFile(divIdName) {
		var d = document.getElementById('oldFileListDiv');		
		var olddiv = document.getElementById(divIdName);
		if (olddiv != undefined) {
			d.removeChild(olddiv);
		}		
	}

	function init() {
		removeAllOldAttachFiles();
		removeAllNewAttachFiles();
		addNewAttachFile();
	}

	window.onload=init;
</script>
<form name=gofrm method="post" action="/servlet/BoardWrite">
<input type="hidden" name="topmenu" value="<%=topmenu%>" />
<input type="hidden" name="pageMode" value="proc" />
<input type="hidden" name="boardId" value="<%=parmBoardId%>" />
<input type="hidden" name="subject" />
<input type="hidden" name="content" />
<input type="hidden" name="attachId" value="0" />
<input type="hidden" name="sessionkeyBase64" />
<input type="hidden" name="ivBase64" />
</form>

<form name=listForm method="post" action="/servlet/BoardList">
<input type="hidden" name="topmenu" value="<%=topmenu%>" />
<input type="hidden" name="boardId" value="<%=parmBoardId%>" />
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
				<dd><input type="button" onClick="save()" value="저장" /> <input type="button" onClick="goList()" value="취소" /></dd>
			</dl>
		</li>
		</ul>	
	</div>
</form>
<form name="attachForm" target="uploadResultFrame" action="/servlet/BoardUpload" method="post" enctype="multipart/form-data">
	<input type="hidden" name="attachId" value="0" />

	<!-- input type="hidden" name="attachSeq" value="0" />
	<input type="hidden" name="attachSeq" value="1" / -->
	
	<div id="oldFileMenuDiv" style="visibility:hidden"><p><a href="#" onclick="restoreOldFiles();">첨부한 파일 복원</a></p></div>
	<div id="oldFileListDiv"></div><br/>
	
	<p><a href="#" onclick="addNewAttachFile();">첨부 파일 추가</a></p>
	<!-- 주의점 myDiv 시작 태그와 종료 태그 사이에는 공백을 포함한 어떠한 것도 넣지 말것, 자식 노드로 인식됨 -->
	<div id="newFileListDiv"></div><br/>

	<input type="submit" value="파일 올리기..." />
	<a href="#" onClick="checkForm()">폼 자식 객체로 들어가있는지 검사</a>
</form>

<iframe name="uploadResultFrame" width="400" height="300" >
</iframe><%
	}
%>
