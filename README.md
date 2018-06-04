>> Introduction

(1) English version

The name Codda means the program that coders make.
I think of myself as a coder.

Codda is a collection of three things: Netty + Google protocol buffers + WAS.
(1) Defining a message information file, which is an xml file, creates sources that can be serialized / deserialized from the information file.

(2) I created a server using the NIO selector and created a server connection API for the client.

(3) Perform a server task that matches 1: 1 with the 'input message identifier' through the dynamic class loader.

That's why I defined Codda as an 'RPC development framework'.


(2) Korea version

코다(Codda) 라는 이름은 코더들이 만들어가는 프로그램이라는 뜻입니다.
저는 제 스스로를 코더라고 생각합니다.

Codda 는 Netty + Google protocol buffers + WAS 이렇게 3가지를 모아 놓은것입니다.

(1) xml 파일인 메시지 정보 파일을 정의하면 그 정보 파일로 부터 직렬화/역직렬화 할 수 있는 소스들이 만들어 집니다.

(2) NIO selector 를 이용한 서버와 클라이언트용 서버 접속 API 를 만들었습니다.

(3) 동적 클래스 로더를 통해서 '입력 메시지 식별자' 와 1:1 매칭되는 server task 를 수행합니다.

이것이 제가 코다(Codda) 를  'RPC 개발 프레임워크' 로 정의한 이유입니다.

>>  라이센스
아파치 라이센스 2

