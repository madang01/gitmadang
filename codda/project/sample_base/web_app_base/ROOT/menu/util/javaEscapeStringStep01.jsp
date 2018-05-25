<%@ page language="java" session="true" autoFlush="true" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %><%
%><%@ page import="kr.pe.codda.weblib.common.WebCommonStaticFinalVars" %>
<!DOCTYPE html>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html;charset=utf-8" />
<meta name="viewport" content="width=device-width,initial-scale=1.0,user-scalable=no" />
<title><%=WebCommonStaticFinalVars.WEBSITE_TITLE%></title>
    <!-- 공통요소 -->
	<link rel="stylesheet" type="text/css" href="/axisj/axicon/axicon.min.css" />
    <link rel="stylesheet" type="text/css" href="/axisj/ui/arongi/AXJ.css" />
    <script type="text/javascript" src="/axisj/jquery/jquery.min.js"></script>
    <script type="text/javascript" src="/axisj/lib/AXJ.js"></script>

    <!-- 추가하는 UI 요소 -->
    <link rel="stylesheet" type="text/css" href="/axisj/ui/arongi/AXInput.css" />
	 <link rel="stylesheet" type="text/css" href="/axisj/ui/arongi/AXButton.css" />
    <link rel="stylesheet" type="text/css" href="/axisj/ui/arongi/AXSelect.css" />
    <link rel="stylesheet" type="text/css" href="/axisj/ui/arongi/AXGrid.css" />
    <script type="text/javascript" src="/axisj/lib/AXInput.js"></script>
    <script type="text/javascript" src="/axisj/lib/AXSelect.js"></script>
    <script type="text/javascript" src="/axisj/lib/AXGrid.js"></script>

	<script type="text/javascript">
		function goForm(f) {
			var parmCreatingTableSql = f.parmCreatingTableSql.value;
			if (parmCreatingTableSql == "") {
				alert("body 에 DB 테이블 생성 쿼리문을 넣어주세요.");
				f.parmCreatingTableSql.focus();
				return false;
			}


			return true;
		}
	</script>
</head>
<body>
<div id="AXGridTarget">
    <h1>'자바 테이블 생성문 반환 메소드 자동 생성' 1 단계</h1>

<table cellpadding="0" cellspacing="0" class="AXGridTable">
	<colgroup>
		<col />
	</colgroup>
	<thead>
		<tr>
			<td>테이블 생성 sql 문장 입력부</td>
		</tr>
	</thead>
	<tbody>
<form name="frm" action="javaEscapeStringStep02.jsp" method="post" onsubmit="return goForm(this);">	
	<tr>
		<td><textarea name="parmCreatingTableSql" class="AXTextarea" style="width:95%;" rows="20"></textarea></td>
	</tr>
	<tr><td align="center"><button type="submit" class="AXButton Classic"><i class="axi axi-hand-o-up"></i> 다음</button></td></tr>
	</tbody>
</form>	
</table>
</div>
</body>
</html>
