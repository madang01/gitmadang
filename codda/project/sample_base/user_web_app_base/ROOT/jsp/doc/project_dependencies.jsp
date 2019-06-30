<%@page import="kr.pe.codda.weblib.common.WebCommonStaticFinalVars" %><%
%><%@page extends="kr.pe.codda.weblib.jdf.AbstractUserJSP" language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %><%
request.setAttribute(WebCommonStaticFinalVars.REQUEST_KEY_NAME_OF_MENU_GROUP_URL, "/jsp/doc/project_dependencies.jsp");
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
<script type='text/javascript'>
	function init() {
	}
	
	window.onload = init;
</script>
</head>
<body>
	<div class=header>
		<div class="container">
<%= getMenuNavbarString(request) %>
		</div>
	</div>
	<div class="content">
		<div class="container">
			<div class="panel panel-default">
				<div class="panel-heading">Project Dependencies</div>
				<div class="panel-body">
					<article style="white-space:pre-wrap;">
1. 코다 로거
1.1 Netty 에서 io.netty.util.internal.logging 패키지(아파치2라이센스, https://github.com/netty/netty/blob/4.1/LICENSE.txt)
1.2 log4j-1.2.17.jar : Apache Log4J(아파치2라이센스)
1.3 log4j-api-2.11.0.jar :Apache Log4J(아파치2라이센스)
1.4 log4j-core-2.11.0.jar : Apache Log4J(아파치2라이센스)
1.5 commons-logging-1.2.jar : Apache Commons Logging(아파치2라이센스)
1.6 logback-classic-1.2.3.jar : Logback(The EPL/LGPL dual-license, EPL v1.0 and the LGPL 2.1, https://logback.qos.ch/license.html)
1.7 logback-core-1.2.3.jar : Logback(The EPL/LGPL dual-license, EPL v1.0 and the LGPL 2.1, https://logback.qos.ch/license.html)
1.8 slf4j-api-1.7.25.jar : SLF4J(MIT 라이센스, https://www.slf4j.org/license.html)

2. 코다 도우미

2.1 메인
2.1.1 logback-classic-1.2.3.jar : Logback(The EPL/LGPL dual-license, EPL v1.0 and the LGPL 2.1, https://logback.qos.ch/license.html)
2.1.2 logback-core-1.2.3.jar : Logback(The EPL/LGPL dual-license, EPL v1.0 and the LGPL 2.1, https://logback.qos.ch/license.html)
2.1.3 slf4j-api-1.7.25.jar : SLF4J(MIT 라이센스, https://www.slf4j.org/license.html)
2.1.4 jcl-over-slf4j-1.7.25.jar : SLF4J(MIT 라이센스, https://www.slf4j.org/license.html)
	dbcp2 depends Apache commons-logging(=jcl-over-slf4j)
2.1.5 commons-cli-1.4.jar : The Apache Commons CLI library(아파치2라이센스)
2.1.6 commons-io-2.6.jar : The Apache Commons IO library(아파치2라이센스)
2.1.7 commons-exec-1.3.jar : The Apache Commons Exec library(아파치2라이센스)
2.1.8 jgoodies-common.jar : : JGoodies Common(BSD 라이센스 : http://www.jgoodies.com/downloads/libraries/)
2.1.9 jgoodies-forms.jar : JGoodies Forms(BSD 라이센스 : http://www.jgoodies.com/downloads/libraries/)

2.2 테스트
2.2.1 byte-buddy-1.7.9.jar : Byte Buddy(아파치2라이센스, https://github.com/raphw/byte-buddy/blob/master/LICENSE)
	Byte Buddy is an open source project distributed under the liberal and business-friendly Apache 2.0 licence. Its source code is freely available on GitHub. Please note that Byte Buddy depends on the ASM library which is distributed under a BSD license. 
2.2.2 byte-buddy-agent-1.7.9.jar : Byte Buddy(아파치2라이센스, https://github.com/raphw/byte-buddy/blob/master/LICENSE)
2.2.3 mockito-core-2.13.4.jar  : mockito(MIT 라이센스,  https://github.com/mockito/mockito/blob/release/2.x/LICENSE)
2.2.4 objenesis-2.6.jar : mockito 의존 라이브러리(아파치2라이센스, http://objenesis.org/license.html)
2.2.5 junit-4.12.jar : JUnit4(Eclipse Public License - v 1.0, https://junit.org/junit4/license.html)
2.2.6 hamcrest-core-1.3.jar : (The 3-Clause BSD License, http://hamcrest.org/JavaHamcrest/)


3. 코다 코어

3.1 메인
3.1.1 commons-dbcp2-2.0.1.jar : DBCP(아파치2 라이센스)
3.1.2 commons-pool2-2.5.0.jar : DBCP 의존 라이브러리(아파치2 라이센스)
3.1.3 logback-classic-1.2.3.jar : Logback(The EPL/LGPL dual-license, EPL v1.0 and the LGPL 2.1, https://logback.qos.ch/license.html)
3.1.4 logback-core-1.2.3.jar : Logback(The EPL/LGPL dual-license, EPL v1.0 and the LGPL 2.1, https://logback.qos.ch/license.html)
3.1.5 slf4j-api-1.7.25.jar : SLF4J(MIT 라이센스, https://www.slf4j.org/license.html)
3.1.6 jcl-over-slf4j-1.7.25.jar : SLF4J(MIT 라이센스, https://www.slf4j.org/license.html)
	dbcp2 depends Apache commons-logging(=jcl-over-slf4j)
3.11 commons-exec-1.3.jar : The Apache Commons Exec library(아파치2라이센스)
3.12 org.apache.commons.collections4.comparators.ComparableComparator.java : : The Apache Commons Collections4 library(아파치2라이센스)

3.2 테스트
3.2.1 byte-buddy-1.7.9.jar : Byte Buddy(아파치2라이센스, https://github.com/raphw/byte-buddy/blob/master/LICENSE)
	Byte Buddy is an open source project distributed under the liberal and business-friendly Apache 2.0 licence. Its source code is freely available on GitHub. Please note that Byte Buddy depends on the ASM library which is distributed under a BSD license. 
3.2.2 byte-buddy-agent-1.7.9.jar : Byte Buddy(아파치2라이센스, https://github.com/raphw/byte-buddy/blob/master/LICENSE)
3.2.3 mockito-core-2.13.4.jar  : mockito(MIT 라이센스,  https://github.com/mockito/mockito/blob/release/2.x/LICENSE)
3.2.4 objenesis-2.6.jar : mockito 의존 라이브러리(아파치2라이센스, http://objenesis.org/license.html)
3.2.5 junit-4.12.jar : JUnit4(Eclipse Public License - v 1.0, https://junit.org/junit4/license.html)
3.2.6 hamcrest-core-1.3.jar : (The 3-Clause BSD License, http://hamcrest.org/JavaHamcrest/)

4. sample_base server

4.1 메인
4.1.1 oracle-mail-1.4.7.jar : 오라클 메일 라이브러리(<a href="/license/oracle-mail-1.4.7.LICENSE.txt">오라클 메일 라이센스</a>)
4.1.2 jooq-3.10.6.jar : jooq(듀얼라이센스,  상용과 아파치2라이센스, https://www.jooq.org/legal/licensing)
4.1.3 jooq-codegen-3.10.6.jar : jooq(듀얼라이센스,  상용과 아파치2라이센스, https://www.jooq.org/legal/licensing)
4.1.4 jooq-meta-3.10.6.jar : jooq(듀얼라이센스,  상용과 아파치2라이센스, https://www.jooq.org/legal/licensing)
4.1.5 logback-classic-1.2.3.jar : Logback(The EPL/LGPL dual-license, EPL v1.0 and the LGPL 2.1, https://logback.qos.ch/license.html)
4.1.6 logback-core-1.2.3.jar : Logback(The EPL/LGPL dual-license, EPL v1.0 and the LGPL 2.1, https://logback.qos.ch/license.html)
4.1.7 slf4j-api-1.7.25.jar : SLF4J(MIT 라이센스, https://www.slf4j.org/license.html)
4.1.8 jcl-over-slf4j-1.7.25.jar : SLF4J(MIT 라이센스, https://www.slf4j.org/license.html)
	dbcp2 depends Apache commons-logging(=jcl-over-slf4j)
4.1.9 gson-2.8.5.jar : 구글 json 라이브러리(아파치2 라이센스, https://github.com/google/gson/blob/master/LICENSE)
4.1.10 commons-dbcp2-2.0.1.jar : DBCP(아파치2 라이센스)
4.1.11 commons-pool2-2.5.0.jar : DBCP 의존 라이브러리(아파치2 라이센스)
4.1.12 mariadb-java-client-2.4.1.jar : mariadb JDBC 드라이버(LGPL 라이센스, https://downloads.mariadb.org/connector-java/2.4.1/)

4.2 테스트
4.2.1 byte-buddy-1.7.9.jar : Byte Buddy(아파치2라이센스, https://github.com/raphw/byte-buddy/blob/master/LICENSE)
	Byte Buddy is an open source project distributed under the liberal and business-friendly Apache 2.0 licence. Its source code is freely available on GitHub. Please note that Byte Buddy depends on the ASM library which is distributed under a BSD license. 
4.2.2 byte-buddy-agent-1.7.9.jar : Byte Buddy(아파치2라이센스, https://github.com/raphw/byte-buddy/blob/master/LICENSE)
4.2.3 mockito-core-2.13.4.jar  : mockito(MIT 라이센스,  https://github.com/mockito/mockito/blob/release/2.x/LICENSE)
4.2.4 objenesis-2.6.jar : mockito 의존 라이브러리(아파치2라이센스, http://objenesis.org/license.html)
4.2.5 commons-exec-1.3.jar : The Apache Commons Exec library(아파치2라이센스)
4.2.6 junit-4.12.jar : JUnit4(Eclipse Public License - v 1.0, https://junit.org/junit4/license.html)
4.2.7 hamcrest-core-1.3.jar : (The 3-Clause BSD License, http://hamcrest.org/JavaHamcrest/)


5. sample_base app-client

5.1 메인
5.1.1 gson-2.8.5.jar : 구글 json 라이브러리(아파치2 라이센스, https://github.com/google/gson/blob/master/LICENSE)

5.2 테스트
5.2.1 byte-buddy-1.7.9.jar : Byte Buddy(아파치2라이센스, https://github.com/raphw/byte-buddy/blob/master/LICENSE)
	Byte Buddy is an open source project distributed under the liberal and business-friendly Apache 2.0 licence. Its source code is freely available on GitHub. Please note that Byte Buddy depends on the ASM library which is distributed under a BSD license. 
5.2.2 byte-buddy-agent-1.7.9.jar : Byte Buddy(아파치2라이센스, https://github.com/raphw/byte-buddy/blob/master/LICENSE)
5.2.3 mockito-core-2.13.4.jar  : mockito(MIT 라이센스,  https://github.com/mockito/mockito/blob/release/2.x/LICENSE)
5.2.4 objenesis-2.6.jar : mockito 의존 라이브러리(아파치2라이센스, http://objenesis.org/license.html)
5.2.5 junit-4.12.jar : JUnit4(Eclipse Public License - v 1.0, https://junit.org/junit4/license.html)
5.2.6 hamcrest-core-1.3.jar : (The 3-Clause BSD License, http://hamcrest.org/JavaHamcrest/)


6. sample_base web-client

6.1 메인
6.1.1 gson-2.8.5.jar : 구글 json 라이브러리(아파치2 라이센스, https://github.com/google/gson/blob/master/LICENSE)
6.1.2 commons-lang3-3.7.jar : The Apache Commons Lang3 (아파치2라이센스)
6.1.3 commons-text-1.3.jar : The Apache Commons Text (아파치2라이센스)
6.1.4 commons-fileupload-1.3.2.jar : The Apache Commons FileUpload (아파치2라이센스)
6.1.5 commons-io-2.6.jar : The Apache Commons IO library(아파치2라이센스)
6.1.6 simplecaptcha-1.2.1.jar : 미상
##### 자바 스크립트 라이브러리 시작 
6.1.7 공개키 라이브러리 JSBN(BSD license, http://www-cs-students.stanford.edu/~tjw/jsbn/LICENSE)
6.1.8 대칭키 라이브러리 cryptoJS(The 3-Clause BSD License, http://code.google.com/p/crypto-js/)
6.1.9 Boostrap 3.3.7(MIT 라이센스, https://getbootstrap.com/docs/3.3/getting-started/#license-faqs)
6.1.10 JQuery 1.8.0(MIT 라이센스, https://jquery.org/license/)
6.1.11 TUI Date Picker(MIT 라이센스, https://github.com/nhn/tui.date-picker)
##### 자바 스크립트 라이브러리 종료

6.2 테스트
6.2.1 byte-buddy-1.7.9.jar : Byte Buddy(아파치2라이센스, https://github.com/raphw/byte-buddy/blob/master/LICENSE)
	Byte Buddy is an open source project distributed under the liberal and business-friendly Apache 2.0 licence. Its source code is freely available on GitHub. Please note that Byte Buddy depends on the ASM library which is distributed under a BSD license. 
6.2.2 byte-buddy-agent-1.7.9.jar : Byte Buddy(아파치2라이센스, https://github.com/raphw/byte-buddy/blob/master/LICENSE)
6.2.3 mockito-core-2.13.4.jar  : mockito(MIT 라이센스,  https://github.com/mockito/mockito/blob/release/2.x/LICENSE)
6.2.4 objenesis-2.6.jar : mockito 의존 라이브러리(아파치2라이센스, http://objenesis.org/license.html)
6.2.5 junit-4.12.jar : JUnit4(Eclipse Public License - v 1.0, https://junit.org/junit4/license.html)
6.2.6 hamcrest-core-1.3.jar : (The 3-Clause BSD License, http://hamcrest.org/JavaHamcrest/)
6.2.7 httpcore-4.4.10.jar : Apache HttpComponents(아파치2라이센스, https://hc.apache.org/index.html)
6.2.8 httpmime-4.5.6.jar : Apache HttpComponents(아파치2라이센스, https://hc.apache.org/index.html)

### 전체 목록 정리 ###					
byte-buddy-1.7.9.jar
byte-buddy-agent-1.7.9.jar
commons-cli-1.4.jar
commons-dbcp2-2.0.1.jar
commons-exec-1.3.jar
commons-fileupload-1.3.2.jar
commons-io-2.6.jar
commons-lang3-3.7.jar
commons-logging-1.2.jar
commons-pool2-2.5.0.jar
commons-text-1.3.jar
gson-2.8.5.jar
hamcrest-core-1.3.jar
httpcore-4.4.10.jar
httpmime-4.5.6.jar
jcl-over-slf4j-1.7.25.jar
jgoodies-common.jar
jgoodies-forms.jar
jooq-3.10.6.jar
jooq-codegen-3.10.6.jar
jooq-meta-3.10.6.jar
junit-4.12.jar
log4j-1.2.17.jar
log4j-api-2.11.0.jar
log4j-core-2.11.0.jar
logback-classic-1.2.3.jar
logback-core-1.2.3.jar
mariadb-java-client-2.4.1.jar
mockito-core-2.13.4.jar
mysql-connector-java-5.1.37-bin.jar
mysql-connector-java-5.1.46.jar
objenesis-2.6.jar
oracle-mail-1.4.7.jar
simplecaptcha-1.2.1.jar
slf4j-api-1.7.25.jar

JSBN
cryptoJS
Boostrap 3.3.7
JQuery 1.8.0

		
					</article>
				</div>
			</div>
		</div>
	</div>
</body>
</html>
