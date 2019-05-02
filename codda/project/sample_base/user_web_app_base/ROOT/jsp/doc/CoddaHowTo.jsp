<%@ page import="kr.pe.codda.weblib.common.WebCommonStaticFinalVars" %><%
%><%@ page extends="kr.pe.codda.weblib.jdf.AbstractUserJSP" language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%><%
	request.setAttribute(WebCommonStaticFinalVars.REQUEST_KEY_NAME_OF_MENU_GROUP_URL, "/jsp/doc/CoddaHowTo.jsp");
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
<body>
	<div class=header>
		<div class="container">
		<%=getMenuNavbarString(request)%>
		</div>
	</div>
	<div class="content">
		<div class="container">
			<div class="panel panel-default">
				<div class="panel-heading"><h4>Codda HowTo</h4></div>
				<div class="panel-body">
					<article style="white-space:pre-line;">
제목 : 데비안에서 codda 개발 환경 구축하기
작성일 : 2019년 5월 1일
작성자 : Won Jonghoon


(0) 시작하기 앞서 알아 두어야할 사항
첫번째 보안을 위해서 git 에 DB 계정 혹은 개인 메일 계정의 아이디와 비밀번호를 올리지 않도록 주의하시기 바랍니다.

두번째 코다 개발 환경을 구축할 PC 는 OS 로 데비안 9.9 가 설치되었으며 IP 주소는 172.30.1.15 를 갖습니다.
자신의 PC 환경에 맞게 IP 와 와 DB 계정 비밀번호를 바꾸어 주시면 됩니다.
DB 계정 비밀번호가 test1234 라면 'root비밀번호' 로 표시된 부분에서 작은 따옴표 안에 자신의 비밀번호를 넣어 'test1234' 로 수정하시면 됩니다.

세번째 아파치 & tomcat 은 계속 진화중인지라 각 버전마다 설장 방법이 조금씩 다를 수 있습니다.

네번째 sample_base' 라는 프로젝트는 코다 코어 라이브러리를 활용하여 코다 커뮤니트 사이트를 구축할 목적으로 만든 프로젝트입니다.
코다 커뮤니트 사이트에서 비밀번호 찾기 메뉴에서 메일 서버가 필요한데 git 에 올려진 메일 서버 설정(=> gitmadang/codda/project/sample_base/resources/email.properties)은 가짜 메일 서버에 맞춘것입니다.
메일 서버 설치및 운영은 매우 복잡하기때문에 네이버, 다음, 구글등 외부 메일 서버를 이용하는것을 권장합니다.
외부 메일 서버 설정은 <a href="http://blog.naver.com/jmkoo02/20199969614">"Java Mail API 활용:주요 포털 웹메일 IMAP/SMTP 설정 정보"</a> 블로그를 참고해 주시기 바랍니다.


------------ 필요한 파일 설치 파트 시작 ------------
(1) apt-get install git
(2) apt-get install default-jdk
(3) apt-get install eclipse
(4) apt-get install mariadb-server
(5) apt-get install apache2
(6) apt-get install tomcat8
(7) apt-get install libapache2-mod-jk
------------ 필요한 파일 설치 파트 종료 ------------

------------ 기타 유틸 설치 파트 시작 ------------
(1) apt-get install net-tools
------------ 기타 유틸 설치 파트 종료 ------------



------------ codda 소스 다운로드및 개발 환경 구축하기 시작 ------------
(1) git clone https://github.com/madang01/gitmadang.git gitmadang
(2) cd gitmadang
(3) cd codda    
(4) chmod u+x Helper.sh
(5) 코다 도우미 유틸을 통해 설치 경로 적용
(5-1) ./Helper.sh
(5-2) 코다 도우미 첫화면에서 "All Main Project Manger" 버튼 클릭
<img src="/images/howto/codda_helper_screenshot01.png" class="img-rounded" />
(5-3) 설치 경로 지정 화면에서 설치 경로를 입력후 "Next" 버튼 클릭
<img src="/images/howto/codda_helper_screenshot02.png" class="img-rounded"  />
(5-4) "All Main Project Manger" 화면에서 "apply Codda installed path to all project" 버튼 클릭
<img src="/images/howto/codda_helper_screenshot03.png" class="img-rounded"  />
(5-5) 창종료
(6)
------------ codda 소스 다운로드및 개발 환경 구축하기 종료 ------------


------------ mariadb db 서버 설치및 환경 설정 시작  ------------
(1) mariadb root 비밀번호 설정

	참고 주소 : https://sarc.io/index.php/mariadb/931-mysql-mariadb-update-root-password

(1-1) mysql -u root
(1-2) DB root 계정 비밀번호 설정

	MariaDB [(none)]> use mysql;
	Reading table information for completion of table and column names
	You can turn off this feature to get a quicker startup with -A

	Database changed
	MariaDB [mysql]> update user set password=password('root비밀번호') where user='root';
	Query OK, 1 row affected (0.00 sec)
	Rows matched: 1  Changed: 1  Warnings: 0

	MariaDB [mysql]> flush pribvileges;
	
(2) mariadb 원격 접속 허용 하기 설정
(2-1) /etc/mysql/mariadb.conf/50-server.cnf 파일에서 bind-address 앞에 # 을 붙여 주석 처리하기
(2-2) mariadb 리스타트 ==> /etc/init.d/mysql restart
(2-3) root 계정 원격에서 접속 허용하기

	MariaDB [(none)]> GRANT ALL PRIVILEGES ON *.* TO 'root'@'172.30.1.15' IDENTIFIED BY 'root비밀번호' WITH GRANT OPTION;
	Query OK, 0 rows affected (0.00 sec)


	참고 주소 : https://mariadb.com/kb/en/library/configuring-mariadb-for-remote-client-access/
(3) workbench 설정
(3-1) workbench 초기 화면
<img src="/images/howto/workbench_screenshot01.png" class="img-rounded" style="width:100%" />
(3-2) 기본적으로 있는 "Local instance 3306" 에 마우스 우클릭으로 펼쳐진 메뉴에서 "Edit Connection..." 선택
<img src="/images/howto/workbench_screenshot02.png" class="img-rounded" style="width:100%" />
(3-3) "Connection" tab -> "Parameters" tab 에서 Hostname 항목에 172.30.1.15 입력후 "Store in Keychain ..." 버튼 클릭하여 비밀번호 설정후 "Test Connection" 버튼 클릭하여 연결 테스트하여 정상 연결되면 "close" 버튼 클릭
<img src="/images/howto/workbench_screenshot03.png" class="img-rounded" style="width:100%" />
(3-4) "Local instance 3306" 버튼 클릭후 "Connection Warning" 경고창에서 "Continue Anyway" 버튼 클릭
<img src="/images/howto/workbench_screenshot04.png" class="img-rounded" style="width:100%" />
(3-5) workbench 정상 접속 화면
<img src="/images/howto/workbench_screenshot05.png" class="img-rounded" style="width:100%" />
(3-6) madangshoe01  계정을 만들고 "sample_base_db_erd.mwb" 파일을 열어 Databse -> Synchronize Model 메뉴을 선택하여 'sb_db', 'gt_sb_db', 'lt_sb_db' 각 스키마 생성
------------ db 서버 설치및 환경 설정 종료  ------------


------------ 아파치 & tomcat 설정 시작 ------------
(10) 아파치 & tomcat 설정



------------ 아파치 & tomcat 설정 종료 ------------
</article>

				</div>
			</div>
		</div>
	</div>
</body>
</html>