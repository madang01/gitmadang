package kr.pe.codda.client.connection.asyn;

import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;

public interface ClientIOEventHandlerIF {
	public SelectionKey register(Selector ioEventSelector, int wantedInterestOps) throws Exception;
	
	/**
	 * 연결을 수행하여 연결 확립 여부를 반환한다
	 * @return 연결 확립 여부
	 * @throws Exception 에러
	 */
	public boolean doConnect() throws Exception;
	
	/**
	 * 파라미터러로 넘어온 셀렉션 키를 등록하고 연결 확립이 완료되었으므로 '연결 추가자'에 추가한다
	 * @param selectedKey 연결 확립 여부에 따라 적절하게 섹렉터에 등록하여 얻은 셀렉션 키
	 */
	public void doFinishConnect(SelectionKey selectedKey);
	
	public void doRemoveUnregisteredConnection();
	
	/**
	 * 연결 확립을 마무리한다
	 * @param selectedKey 섹렉터에 등록된 키로 OP_CONNECT 이벤트가 발생된 키이다
	 */
	public void onConnect(SelectionKey selectedKey);
	public void onRead(SelectionKey selectedKey) throws InterruptedException;
	public void onWrite(SelectionKey selectedKey);
	public void close();
}
