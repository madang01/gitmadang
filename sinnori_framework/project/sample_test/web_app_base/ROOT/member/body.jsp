<%@ page language="java" contentType="text/html; charset=UTF-8"   pageEncoding="UTF-8"%><%
	String topmenu = request.getParameter("topmenu");
%>
회원 - body part
<script>
goURL("/PageJump.jsp?topmenu=<%=topmenu%>&targeturl=/member/login.jsp");
</script>
