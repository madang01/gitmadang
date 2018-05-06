<%@ page language="java" session="true" autoFlush="true" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %><%
%><%@page import="kr.pe.sinnori.weblib.htmlstring.HtmlStringUtil"%><%
%><%@ page import="kr.pe.sinnori.weblib.common.WebCommonStaticFinalVars" %><%!
	public void errorPrint(javax.servlet.jsp.JspWriter out, String errorMessage) throws java.io.IOException {
		out.println("<!DOCTYPE html>");
		out.println("<html>");
		out.println("<head>");
		out.println("<meta http-equiv=\"Content-Type\" content=\"text/html;charset=utf-8\" />");
		out.println("</head>");
		out.println("<body>");
		out.println(HtmlStringUtil.toHtml4String(errorMessage));
		out.println("</body>");
		out.print("</html>");
		out.flush();
	}
%><%
	/** 한글 처리를 위해서 request 문자셋을 utf-8 로 맞춤 */
	request.setCharacterEncoding("utf-8");


	String parmTableName = request.getParameter("parmTableName");
	String parmHeader = request.getParameter("parmHeader");
	String parmBody = request.getParameter("parmBody");
	String parmFooter = request.getParameter("parmFooter");

	if (null == parmTableName) {
			String errorMessage = "parameter parmTableName missing";
			errorPrint(out, errorMessage);
			return;
	}
	if (null == parmHeader) {
			String errorMessage = "parameter parmHeader missing";
			errorPrint(out, errorMessage);
			return;
	}
	if (null == parmBody) {
			String errorMessage = "parameter parmBody missing";
			errorPrint(out, errorMessage);
			return;
	}
	if (null == parmFooter) {
			String errorMessage = "parameter parmFooter missing";
			errorPrint(out, errorMessage);
			return;
	}
	
%>
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
		<script type="text/javascript">
		function goForm(f) {
			return true;
		}
	</script>
</head>
<body>
<div id="AXGridTarget">
    <h1>'자바 테이블 생성문 반환 메소드 자동 생성' 3 단계(=마지막 단계)</h1>

<table cellpadding="0" cellspacing="0" class="AXGridTable">
	<colgroup>
		<col width="60" /><col />
	</colgroup>
	<thead>
		<tr align="center">
			<td>제 목</td>
			<td>내 용</td>
		</tr>
	</thead>
<form name="frm" action="javaEscapeStringStep01.jsp" method="post" onsubmit="return goForm(this);">	
	<tbody>
	<tr>
		<td align="center">테이블명</td>
		<td><input type="text" name="parmTableName" value="<%=parmTableName%>" class="AXInput" readonly="readonly" /></td>
	</tr>
	<tr>
		<td align="center">최 종<br/>결 과</td>
		<td><textarea name="parmResult" class="AXTextarea" style="width:95%;" rows="20"><%=HtmlStringUtil.toHtml4String(parmHeader)%>
<%=	HtmlStringUtil.toHtml4String(parmBody)%>
<%=	HtmlStringUtil.toHtml4String(parmFooter)%></textarea></td>
	</tr>
	<tr><td colspan="2" align="center"><button type="submit" class="AXButton Classic"><i class="axi axi-check-circle"></i> 확인</button></td></tr>
	</tbody>
</form>	
</table>
</div>
	
</body>
</html>
