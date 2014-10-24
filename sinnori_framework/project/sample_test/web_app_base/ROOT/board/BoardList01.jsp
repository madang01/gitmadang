<%@ page language="java" session="true" autoFlush="true" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %><%
%><%@ page import="org.apache.commons.lang.StringEscapeUtils" %><%
%><%@ page import="kr.pe.sinnori.impl.message.BoardListOutDTO.BoardListOutDTO" %><%
%><jsp:useBean id="topmenu" class="java.lang.String" scope="request" /><%
%><jsp:useBean id="leftmenu" class="java.lang.String" scope="request" /><%
%><jsp:useBean id="boardListOutDTO" class="kr.pe.sinnori.impl.message.BoardListOutDTO.BoardListOutDTO" scope="request" /><%
%><jsp:useBean id="errorMessage" class="java.lang.String" scope="request" />
<style>
<!--
table {
	border:solid 1px;
	border-color:black;
	border-collapse:collapse;
}
thead {
	height : 30px;
}
tbody {
	height : 20px;
	text-align:center;
}
-->
</style>
<h1>자유 게시판</h1>
<br/>
<table border="1">
<thead>
<tr>
	<th width="30">번호</th>
	<th width="200">제목</th>
	<th width="50">작성자</th>
	<th width="50">조회수</th>
	<th width="50">추천수</th>
	<th width="70">최초 등록일</th>
	<th width="70">마지막<br/>수정일</th>
	<th width="70">회원구분</th>
</tr>
</thead>
<tbody><%
	if (null != errorMessage && !errorMessage.equals("")) {
%>
<tr>
	<td colspan="8"><%= errorMessage %></td>
</tr><%
	} else {
		java.util.List<BoardListOutDTO.Board> boardList = boardListOutDTO.getBoardList();
		for (BoardListOutDTO.Board board : boardList) {
%>
<tr>
	<td><%=board.getBoardNo() %></td>
	<td align="left"><%=board.getTitle() %></td>
	<td><%=board.getNickname() %></td>
	<td><%=board.getViewCount() %></td>
	<td><%=board.getVotes() %></td>
	<td><%=board.getRegisterDate().toString() %></td>
	<td><%=board.getModifiedDate().toString() %></td>
	<td><%=board.getMemberGubunName() %></td>
</tr><%
		}
	}
%>
</tbody>
</table>

