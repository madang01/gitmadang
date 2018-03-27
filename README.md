>> Introduction

(1) English version

 Sinnori framework is a message driven development framework with a simple structure of server / client based input -> processing -> output.

Message driven development is a development method in which roles and responsibilities are separated between the server and the client 

so that the server and the client can work independently, 

although the disadvantage is that the maintenance increases exponentially as the number of messages to manage increases.

For example, a client can perform tasks necessary for a client as a virtual output message autonomously generated independently of the server.

The server can also perform logic validation with a virtual input message created by itself, independent of the client.

Finally, the message-driven development method has the disadvantage of overhead due to multiple invocations, but it is easy to assemble and use business logic as a component.



(2) Korea version
 신놀이 프레임워크는 서버/클라이언트 기반의 입력 -> 처리 -> 출력이라는 단순한 구조를 갖는 메시지 주도 개발 프레임워크입니다.
 
메시지 주도 개발 방법은 비록 관리해야할 메시지가 증가할수록 유지 보수가 기하 급수적으로 어려워지는 단점이 있지만 

서버와 클라이언트 각자 독립적으로 작업할 수 있도록 서버와 클라이언트 간의 롤과 책임이 분리되어 있는 개발 방법입니다.

예를 들어 클라이언트는 서버와 독립적으로 자체적으로 생성 된 가상 출력 메시지로 클라이언트에 필요한 작업을 수행 할 수 있습니다.

서버 또한 클라이언트와 별개로 자체적으로 만든 가상의 입력 메시지로 로직 검증을 할 수 있습니다.



>> 앞으로의 목표
신놀이 프레임워크로를 이용한 다양한 프로그램을 개발을 해 보고싶습니다.

그 첫번째 목표는 간이 기능(ex 메뉴구성, 유저추가, 권한설정) 정도만 지원해 주는 웹사이트 구축을 도와와주는 CMS 프로그램니다.

두번째 목표는 파일 송수신 프로그램입니다.

세번째 목표는 네트워크 게임입니다.

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

