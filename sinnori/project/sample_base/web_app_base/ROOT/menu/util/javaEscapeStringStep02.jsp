<%@ page language="java" session="true" autoFlush="true" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %><%
%><%@ page import="kr.pe.sinnori.weblib.common.WebCommonStaticFinalVars" %><%!
	public void errorPrint(javax.servlet.jsp.JspWriter out, String errorMessage) throws java.io.IOException {
		out.println("<!DOCTYPE html>");
		out.println("<html>");
		out.println("<head>");
		out.println("<meta http-equiv=\"Content-Type\" content=\"text/html;charset=utf-8\" />");
		out.println("</head>");
		out.println("<body>");
		out.println(org.apache.commons.lang3.StringEscapeUtils.escapeHtml4(errorMessage));
		out.println("</body>");
		out.print("</html>");
		out.flush();
	}
%><%
	/** 한글 처리를 위해서 request 문자셋을 utf-8 로 맞춤 */
	request.setCharacterEncoding("utf-8");

	
	String parmCreatingTableSql = request.getParameter("parmCreatingTableSql");
	
	
	String headerString = null;
	String footerString = null;
	String talbeName = null;
	

	StringBuffer bodyStringBuffer = new StringBuffer();
	

	if ( null != parmCreatingTableSql) {
		parmCreatingTableSql = parmCreatingTableSql.trim();
		
		String upperCaseSql = parmCreatingTableSql.toUpperCase();

		int inx = upperCaseSql.indexOf("CREATE TABLE ");
		if (inx != 0) {
			String errorMessage = new StringBuffer("잘못된 테이블 생성 sql 문장입니다. CREATE TABLE 미 검출. parmCreatingTableSql=[")
				.append(parmCreatingTableSql).append("]").toString();
			errorPrint(out, errorMessage);
			return;
		} else {
			/** 테이블명 축출 */
			int minLength = "CREATE TABLE ".length();
			int sqlLength = parmCreatingTableSql.length();

			if (sqlLength <= minLength) {
				String errorMessage = new StringBuffer("잘못된 테이블 생성 sql 문장입니다. 테이블 생성 sql 문장 길이[")
					.append(sqlLength).append("]가 문자열 'CREATE TABLE ' 길이=[").append(minLength).append("] 보다 작거나 같습니다.").toString();
				errorPrint(out, errorMessage);
				return;
			}

			inx = parmCreatingTableSql.indexOf("(", "CREATE TABLE ".length());

			if (inx <= 0) {
				String errorMessage = new StringBuffer("잘못된 테이블 생성 sql 문장입니다. 항목 구성의 시작을 알리는 중괄호가 없습니다.").toString();
				errorPrint(out, errorMessage);
				return;
			}

			int lastInx = parmCreatingTableSql.lastIndexOf("`", inx);
			if (lastInx < 0) {
				String errorMessage = new StringBuffer("잘못된 테이블 생성 sql 문장입니다. 테이블명 감싸는 끝 문자 ` 미존재").toString();
				errorPrint(out, errorMessage);
				return;
			}

			int startInx = parmCreatingTableSql.lastIndexOf("`", lastInx-1);
			if (startInx < 0) {
				String errorMessage = new StringBuffer("잘못된 테이블 생성 sql 문장입니다. 테이블명 감싸는 시작 문자 ` 미존재").toString();
				errorPrint(out, errorMessage);
				return;
			}

			if (startInx+1 >= lastInx) {
				String errorMessage = new StringBuffer("잘못된 테이블 생성 sql 문장입니다. 중괄호 좌측 옆에 ` 문자로 감싼 테이블명 검출 실패. startInx=[")
					.append(startInx).append("], lastInx=[").append(lastInx).append("], parmCreatingTableSql=[")
					.append(parmCreatingTableSql).append("]").toString();
				errorPrint(out, errorMessage);
				return;
			}
			talbeName = parmCreatingTableSql.substring(startInx+1, lastInx);

			/** 상단 구성 */
			headerString = new StringBuilder("public String getCreatingTableSqlFor")
				.append(talbeName).append("() {")
				.append(kr.pe.sinnori.common.etc.CommonStaticFinalVars.NEWLINE)
				.append("	StringBuffer stringBuilder = new StringBuffer();").toString();
			// bodyStringBuffer.append(headerString);			
			
			/** 본문 구성 */
			String lines[] = parmCreatingTableSql.split("\\u000D\\u000A|[\\u000D\\u000A]");
			for (int i=0; i < lines.length; i++) {
				String escapeString = lines[i];
				// String escapeString = org.apache.commons.lang3.StringEscapeUtils.escapeJava(lines[i]);


				bodyStringBuffer.append(kr.pe.sinnori.common.etc.CommonStaticFinalVars.NEWLINE);
				bodyStringBuffer.append("stringBuilder.append(\"");
				bodyStringBuffer.append(escapeString);
				bodyStringBuffer.append("\");");
				bodyStringBuffer.append(kr.pe.sinnori.common.etc.CommonStaticFinalVars.NEWLINE);
				bodyStringBuffer.append("stringBuilder.append(");
				bodyStringBuffer.append("System.getProperty(\"line.separator\")");
				bodyStringBuffer.append(");");
			
			}

			/** 하단 구성 */
			footerString = new StringBuilder("	return stringBuilder.toString();")
				.append(kr.pe.sinnori.common.etc.CommonStaticFinalVars.NEWLINE)
				.append("}").toString();
			// bodyStringBuffer.append(footerString);
		}
		


		
		
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
    <h1>'자바 테이블 생성문 반환 메소드 자동 생성' 2 단계</h1>

<form name="frm" action="javaEscapeStringStep03.jsp" method="post" onsubmit="return goForm(this);">	
	<label class="AXInputLabel">
        <span>테이블명</span>
        <input type="text" name="parmTableName" value="<%=talbeName%>" class="AXInput" readonly="readonly" />
    </label>
<div class="AXHspace20"></div>
<table cellpadding="0" cellspacing="0" class="AXGridTable">
	<colgroup>
		<col width="60" /><col />
	</colgroup>
	<thead>
		<tr align="center">
			<td>메소드<br/>구성부</td>
			<td align="center">내 용</td>
		</tr>
	</thead>	
	<tbody>
	<tr>
		<td align="center">상단</td>
		<td><textarea name="parmHeader" class="AXTextarea" style="width:95%;" rows="3"><%=org.apache.commons.lang3.StringEscapeUtils.escapeHtml4(headerString)%></textarea></td>
	</tr>
	<tr>
		<td align="center">본문</td>
		<td><textarea name="parmBody" class="AXTextarea" style="width:95%;" rows="20"><%=org.apache.commons.lang3.StringEscapeUtils.escapeHtml4(bodyStringBuffer.toString())%></textarea></td>
	</tr>
	<tr>
		<td align="center">하단</td>
		<td><textarea name="parmFooter" class="AXTextarea" style="width:95%;" rows="3"><%=org.apache.commons.lang3.StringEscapeUtils.escapeHtml4(footerString)%></textarea></td>
	</tr>
	<tr><td colspan="2" align="center"><button type="submit" class="AXButton Classic"><i class="axi axi-hand-o-up"></i> 다음</button></td></tr>
	</tbody>
</table>
</form>	
</div>
	
</body>
</html>
