<%@ page extends="kr.pe.sinnori.weblib.jdf.AbstractJSP" language="java" contentType="text/html; charset=UTF-8"   pageEncoding="UTF-8"%><%
%><%@ page import="kr.pe.sinnori.weblib.common.WebCommonStaticFinalVars" %><%
	
%>
<script type="text/javascript">
	function goList() {
		var g = document.goListForm;		
		g.submit();
	}

	window.onload=goList;
</script>
<!-- topmenu 는 하드 코딩 -->
<form name="goListForm" method="post" action="/servlet/BoardList">
<input type="hidden" name="topmenu" value="<%=getCurrentTopMenuIndex(request)%>" />
<input type="hidden" name="boardId" value="2" />
</form>
<session>
사랑방 - body part
</session>
