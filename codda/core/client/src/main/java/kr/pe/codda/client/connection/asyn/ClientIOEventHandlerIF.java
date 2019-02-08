package kr.pe.codda.client.connection.asyn;

import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;

public interface ClientIOEventHandlerIF {
	/**
	 * 셀렉터에 지정한 관심 이벤트를 갖는 소켓을 등록하여 셀렉터 키를 받아 반환한다
	 * @param ioEventSelector The selector with which this channel is to be registered
	 * @param wantedInterestOps The interest set for the resulting key
	 * @return A key representing the registration of this channel with the given selector
	 * @throws Exception
	 */
	public SelectionKey register(Selector ioEventSelector, int wantedInterestOps) throws Exception;
	
	/**
	 * 연결을 수행하여 연결 확립 여부를 반환한다, 외부에서 에러 발생시 부가적인 작업 처리가 필요하여 에러 발생시 자체 처리하지 않고 예외로 던진다
	 * @return 연결 확립 여부
	 * @throws Exception 연결 과정에서 발생하는 에러
	 */
	public boolean doConnect() throws Exception;
	
	/**
	 * 파라미터러로 넘어온 셀렉션 키를 등록하고 연결 확립이 완료되었으므로 '연결 추가자'에 추가한다
	 * @param selectedKey 연결 확립 여부에 따라 적절하게 섹렉터에 등록하여 얻은 셀렉션 키
	 */
	public void doFinishConnect(SelectionKey selectedKey);
	
	/**
	 * <pre>
	 * '미 등록된 연결'이 연결 확립을 못해 폐기되는 후속 조취로 
	 * 이 '미 등록된 연결'의 연결 확립을 부탁한 주체자의 '미 등록된 연결 갯수'를 하나 줄인다
	 * </pre>
	 */
	public void doSubtractOneFromNumberOfUnregisteredConnections();
	
	/**
	 * 연결 확립을 마무리한다, 참고) 에러 통제
	 * @param selectedKey 섹렉터에 등록된 키로 OP_CONNECT 이벤트가 발생된 키이다
	 */
	public void onConnect(SelectionKey selectedKey) throws Exception;

	/**
	 * <pre>
	 * 소켓 읽기를 수행하여 메시지를 추출하여 
	 * 추출된 메시지가 동기식인 경우에는 큐에 전달하고 
	 * 비동기 방식의 경우에는 클라이언트 비지니스 로직을 수행한다, 
	 * 참고) 에러 통제
	 * </pre>
	 * 
	 * @param selectedKey 섹렉터에 등록된 키로 OP_READ 이벤트가 발생된 키이다
	 * @throws InterruptedException 추출된 메시지를 큐에 넣는 과정에서 인터럽트 발생시 발생
	 * @throws Exception 
	 */
	public void onRead(SelectionKey selectedKey) throws InterruptedException, Exception;

	/**
	 * 
	 * @param selectedKey 섹렉터에 등록된 키로 OP_WRITE 이벤트가 발생된 키이다
	 */
	public void onWrite(SelectionKey selectedKey) throws Exception;
	
	/**
	 * 자원 반환을 포함한 소켓 연결 닫기
	 */
	public void close();
}
