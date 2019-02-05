package kr.pe.codda.server;

import java.nio.channels.SelectionKey;

public interface ServerIOEventHandlerIF {
	/**
	 * 연결 객체의 OP_READ 이벤트 발생시 호출되는 메소드, 예외를 던지지 않도록 구현되었지만 예외를 멋어난 예외는 발생할 수 있다
	 * @param selectedKey selector 에서 OP_READ 이벤트를 발생한 키
	 * @throws Exception 예상 못한 예외
	 */
	public void onRead(SelectionKey selectedKey) throws Exception;
	
	/**
	 * 연결 객체의 OP_WRITE 이벤트 발생시 호출되는 메소드, 예외를 던지지 않도록 구현되었지만 예외를 멋어난 예외는 발생할 수 있다
	 * @param selectedKey selectedKey selector 에서 OP_WRITE 이벤트를 발생한 키
	 * @throws Exception 예상 못한 예외
	 */
	public void onWrite(SelectionKey selectedKey) throws Exception;
}
