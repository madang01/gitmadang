package kr.pe.codda.client.connection.asyn;

public interface AsynConnectedConnectionAdderIF {
	/**
	 * 메시지 송수신이 가능한 연결 확립된 연결을 파라미티러 받아 등록 시킨다
	 * 
	 * @param connectedAsynConnection 연결 확립된 연결
	 */
	public void addConnectedConnection(AsynConnectionIF connectedAsynConnection);
	
	/**
	 * <pre>
	 * '미 등록된 연결'이 연결 확립을 못해 폐기되는 후속 조취로 
	 * 이 '미 등록된 연결'의 연결 확립을 부탁한 주체자의 '미 등록된 연결 갯수'를 하나 줄인다
	 * </pre>
	 * 
	 * @param unregisteredAsynConnection 연결 확립을 못해 폐기된 '미 등록된 연결'
	 */
	public void subtractOneFromNumberOfUnregisteredConnections(AsynConnectionIF unregisteredAsynConnection);
}
