  코다는 아파치2 라이센스를 따르는 오픈 소스 프로그램으로 개발중인 RPC 개발 프레임워크입니다.

코다에서 말하는 RPC 개발 프레임워크란 (1) 입력 메시지를 작성하여  (2) 입력 메시지와 1:1 대응하는 원격 비지니스 로직을 호출하여 

(3) 출력 메시지(들)를 얻어 응용 어플을 개발하는 틀입니다.

코다는 (1) 서버와 (2) 서버 접속 API 라는 2가지 구성되어 있습니다.

(1) 코다 서버로 자바 NIO selector 를 기반으로 하는 싱글 쓰레드 서버입니다.

(2) 코다 서버 접속 API 는 DBCP 와 같은 연결 폴로써 

(2-1) 입력 메시지를 보낸후 출력 메시지를 지정 시간 동안 대기하여 받는 동기 메시지 방식과 

(2-2) 입력 메시지를 보낸후 출력 메시지를 기다리지 않고 다른 처리를 할수 있는 비동기 메시지 방식

이렇게 2가지를 지원하는 연결 폴로 구성되어 있습니다.

참고 : 코다는 구 신놀이(Sinnori)를 계승 발전한 프레임워크입니다.

보다 자세한 사항은 메뉴 "기술문서" 에 있는 내용들을 참고해 주시기 바랍니다.

