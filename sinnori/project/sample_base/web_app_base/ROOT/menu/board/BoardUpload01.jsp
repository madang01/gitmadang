<%@ page extends="kr.pe.sinnori.common.weblib.AbstractJSP" language="java" session="true" autoFlush="true" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %><%
%><%@ page import="kr.pe.sinnori.common.weblib.WebCommonStaticFinalVars" %><%
%><jsp:useBean id="topmenu" class="java.lang.String" scope="request" /><%
%><jsp:useBean id="leftmenu" class="java.lang.String" scope="request" /><%
%><jsp:useBean id="errorMessage" class="java.lang.String" scope="request" /><%
%><jsp:useBean id="boardUploadFileOutDTO" class="kr.pe.sinnori.impl.message.BoardUploadFileOutDTO.BoardUploadFileOutDTO" scope="request" />
<h1>자유 게시판 - 업로드 파일 결과</h1><br/>
<script type="text/javascript">
<!--
	function init() {<%
	if (null != errorMessage && !errorMessage.equals("")) {
%>
		var boardUploadFileOutDTO = {isError:true, errorMessage:"<%=escapeScript(errorMessage)%>"};<%
	} else {
%>
		var boardUploadFileOutDTO = {isError:false, attachId:<%= boardUploadFileOutDTO.getAttachId() %>, oldAttachFileList : [<%
	java.util.List<kr.pe.sinnori.impl.message.BoardUploadFileOutDTO.BoardUploadFileOutDTO.AttachFile> attachFileList = boardUploadFileOutDTO.getAttachFileList();
	for (kr.pe.sinnori.impl.message.BoardUploadFileOutDTO.BoardUploadFileOutDTO.AttachFile attachFile : attachFileList) {
		out.print("{");
		out.print("attachSeq:");
		out.print(attachFile.getAttachSeq());
		out.print(", attachFileName:");
		out.print("\"");
		out.print(escapeScript(attachFile.getAttachFileName()));
		out.print("\"");
		out.print("}, ");
	}	
%>]};<%
	}
%>
		parent.callbackUpload(boardUploadFileOutDTO);
	}
	window.onload = init;	
//-->
</script>
