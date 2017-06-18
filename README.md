  신놀이 프레임워크는 아래와 같은 서버 구조를 갖고 
  
입력 메시지에 대한 비지니스 로직들로 구성되는 서비스를 개발하는 개발 프레임워크입니다.
  
>> 서버 구조 목표
(1) 클라 <---tcp/ip---> 인증서버 <---udp----> 비니지스 로직 서버<--> mysql db
(2) 브라우저 <---tcp/ip---> WAS(=Tomcat) <---udp----> 비니지스 로직 서버<--> mysql db

>> 서비스 개발 목표
(1) 메뉴와 사용자 관리를 지원하는 정도의 간단한 CMS
(2) 부하 테스트및 개발 인프라 확장을 위한 비동기와 동기 방식의 파일 송수신 개발

>> 개발 방향
(1) 난해한 사용법및 개념이 없는 프레임워크, 현실문제 해결은 오직 ER 모델을 통한 해결을 지향한다.
(2) 개발자가 기대한 대로 동작하는 프레임워크, 즉 side effect 없거나 오픈하여 적극적으로 알려주는 프레임워크
(3) 개발 프레임워크를 사용자인 개발자 관점에서 필요한것이 무엇인가? 생각해 보는 개발 프레임워크

>>  라이센스
신놀이 프레임워크는 아파치 라이센스 2로 배포됩니다.

>> 역사
신놀이 프레임워크 코어 라이브리의 서버는 
한빛미디어 "자바 I/O & NIO 네트워크 프로그래밍" 
AdvancedChatServer 를 기반으로 작성되었습니다.

>> core library
파일 : commons-codec-1.9.jar, commons-collections4-4.0.jar  commons-dbcp2-2.0.1.jar commons-io-2.4.jar commons-pool2-2.2.jar
주소 : http://commons.apache.org/
라이센스 : Apache License 2.0

파일 : json-simple-1.1.1.jar
주소 : http://code.google.com/p/json-simple/
라이센스 : Apache License 2.0

파일 : logback-classic-1.1.2.jar logback-core-1.1.2.jar
주소 : https://logback.qos.ch/
라이센스 :  the Eclipse Public License v1.0 or the GNU Lesser General Public License version 2.1


파일 : slf4j-api-1.7.7.jar jcl-over-slf4j-1.7.7.jar
주소 : https://www.slf4j.org/
라이센스 : MIT License

파일 : mysql-connector-java-5.1.37-bin.jar
주소 : https://downloads.mysql.com/archives/c-j/
주소 : GPL


>> swing application library
파일 : jgoodies-common.jar jgoodies-forms.jar
주소 : http://www.jgoodies.com/
라이센스 : BSD Open Source License


>> servlet/jsp library
샘플 웹 클라이언트는 자바 서비스넷의 이원형씨의 JDF 를 기반으로 작성되었습니다.
자바 서비스넷 주소 : http://javaservice.net/

>> web 암호화 관련 javascript library
샘플 웹 클라이언트의 필요한 외부 라이브러리는 아래와 같습니다.

jsbn javascript library(jsbn.js, jsbn2.js, prng4.js, rng.js, rsa.js, rsa2.js)
주소 : http://www-cs-students.stanford.edu/~tjw/jsbn/
라이센스 : BSD License

crtypeJS javascript library(CryptoJS v3.1.2.zip)
주소 : http://code.google.com/p/crypto-js/
라이센스 : New BSD License(=The BSD 3-Clause License)

