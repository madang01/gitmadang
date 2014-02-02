<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%><%
    String topmenu = request.getParameter("topmenu");
    if (null == topmenu) topmenu="5";
    String leftmenu = request.getParameter("leftmenu");
    if (null == leftmenu) leftmenu="000000";

    final String arryLeftTopMenuPage[][] =      {
                { "JDF 테스트", "/servlet/JDFNotLoginTest", "00"},
                { "RSA 기반 섹션키 테스트", "/PageJump.jsp?topmenu=topmenu&targeturl=/testcode/BlowfishMember01.jsp", "00"},
                { "동적 클래스 호출 실패 테스트", "/servlet/TestServerInMsgFail", "00"},
                { "네트워크 기능테스트", "/download/main.jsp", null},
    };


%>
<ol>
    <li>JDF 테스트
		<table border="1" style="text-align:center;">
		    <tr>
		        <td colspan="2">로그인 여부</td>
		    </tr>
		<tr>
			<td>비 로그인</td><td><a href="#" onClick="goURL('/servlet/JDFNotLoginTest')">예제</a> <a href="#">소스</a></td>
		</tr>
		<tr>
			<td>로그인</td><td><a href="#" onClick="goURL('/servlet/JDFLoginTest')">예제</a> <a href="#">소스</a></td>
		</tr>
		</table><br/>
    </li>
    
    
    <li>RSA 기반 섹션키 테스트
		<table border="1" style="text-align:center;">
		<tr>
			<td>모듈명</td>
			<td colspan="2">기능</td>
		</tr>
		<tr>
			<td>JSBN</td><td>RSA 암/복호화 테스트</td><td><a href="#" onclick="goURL('/servlet/JSBNTest')">예제</a> <a href="#">소스</a></td>
		</tr>
		<tr>
			<td rowspan="2">CryptoJS</td><td>해시 알고리즘<br/>(=메세지 다이제스트) 테스트</td><td><a href="#" onclick="goURL('/servlet/CryptoJSMDTest')">예제</a> <a href="#">소스</a></td>
		</tr>
		<tr>
			<td>대칭키 테스트</td><td><a href="#" onclick="goURL('/servlet/CryptoJSSKTest')">예제</a> <a href="#">소스</a></td>
		</tr>
		<tr>
			<td>AbstractSessionKeyServlet</td><td>세션키 요구 서블릿 테스트</td><td><a href="#" onclick="goURL('/servlet/SessionKeyTest')">예제</a></td>
		</tr>
		</table><br/>
	</li>
	

    
    <li>동적 클래스 호출 실패 테스트
		<table border="1" style="text-align:center;">
		<tr>
			<td>서버/클라이언트 종류</td><td colspan="2">메세직 식별자 적용 대상</td>
		</tr>	
		<tr>
			<td>서버</td><td>실행기</td><td><a href="#">예제</a> <a href="#">소스</a></td>
		</tr>	
		</table><br/>	    
    </li>
    
    <li>클라이언트와 서버 응답 timeout 검증<a href="#" onclick="goURL('/servlet/EchoTest')">예제</a> <a href="#">소스</a></li>
    
    <li>모든 데이터 타입 검증<a href="#" onclick="goURL('/servlet/AllDataTypeTest')">예제</a> <a href="#">소스</a></li>
</ol>
