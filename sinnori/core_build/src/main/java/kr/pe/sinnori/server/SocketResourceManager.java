package kr.pe.sinnori.server;

import java.nio.channels.SocketChannel;
import java.util.HashMap;

import kr.pe.sinnori.common.exception.NoMoreDataPacketBufferException;
import kr.pe.sinnori.common.io.SocketOutputStream;
import kr.pe.sinnori.common.io.SocketOutputStreamFactoryIF;
import kr.pe.sinnori.server.threadpool.IEOServerThreadPoolSetManagerIF;
import kr.pe.sinnori.server.threadpool.executor.ServerExecutorIF;
import kr.pe.sinnori.server.threadpool.inputmessage.InputMessageReaderIF;
import kr.pe.sinnori.server.threadpool.outputmessage.OutputMessageWriterIF;

public class SocketResourceManager implements SocketResourceManagerIF {
	private final Object monitor = new Object();	
	
	private SocketOutputStreamFactoryIF socketOutputStreamFactory = null;
	private IEOServerThreadPoolSetManagerIF ieoThreadPoolManager = null;
	
	private HashMap<SocketChannel, SocketResource> socketChannel2SocketResourceHash 
		= new HashMap<SocketChannel, SocketResource>(); 
	
	private ProjectLoginManagerIF projectLoginManager =  new ProjectLoginManager();;
	
	public SocketResourceManager( 
			SocketOutputStreamFactoryIF socketOutputStreamFactory,
			IEOServerThreadPoolSetManagerIF ieoThreadPoolManager) {
		if (null == socketOutputStreamFactory) {
			throw new IllegalArgumentException("the parameter socketOutputStreamFactory is null");
		}
		
		if (null == ieoThreadPoolManager) {
			throw new IllegalArgumentException("the parameter ieoThreadPoolManager is null");
		}
		
		this.socketOutputStreamFactory = socketOutputStreamFactory;
		this.ieoThreadPoolManager = ieoThreadPoolManager;
	}
	

	@Override
	public void addNewAcceptedSocketChannel(SocketChannel newAcceptedSC) throws NoMoreDataPacketBufferException, InterruptedException {
		if (null == newAcceptedSC) {
			throw new IllegalArgumentException("the parameter newAcceptedSC is null");
		}		
		
		InputMessageReaderIF inputMessageReaderOfOwnerSC = 
				ieoThreadPoolManager.getInputMessageReaderWithMinimumMumberOfSockets();		
		
		ServerExecutorIF executorOfOwnerSC = 
				ieoThreadPoolManager.getExecutorWithMinimumMumberOfSockets();
		
		OutputMessageWriterIF outputMessageWriterOfOwnerSC = 
				ieoThreadPoolManager.getOutputMessageWriterWithMinimumMumberOfSockets();
		
		SocketOutputStream socketOutputStreamOfOwnerSC = socketOutputStreamFactory.makeNewSocketOutputStream();
		
		PersonalLoginManager personalLoginManagerOfOwnerSC = 
				new PersonalLoginManager(newAcceptedSC, projectLoginManager);
		
		SocketResource socketResource = new SocketResource(
				newAcceptedSC, 
				inputMessageReaderOfOwnerSC,
				executorOfOwnerSC,
				outputMessageWriterOfOwnerSC,
				socketOutputStreamOfOwnerSC, 
				personalLoginManagerOfOwnerSC);
		
		synchronized (monitor) {
			/** 소켓 자원 등록 작업 */
			socketChannel2SocketResourceHash.put(newAcceptedSC, socketResource);
			
			/**
			 * <pre>
			 * Warning! 반듯이 신규 소켓을 '입력 메시지 담당 쓰레드'(InputMessageReader)에 등록 하기 앞서 소켓에 1:1로 할당되는 자원 등록 작업이 선행되어야 한다.
			 * 왜냐하면 '입력 메시지 담당 쓰레드' 에 신규 소켓 등록시 소켓에 1:1 로 할당된 자원중 하나인 출력 스트림을 이용한 입력 메시지 추출 작업이 진행되기때문이다.
			 * 
			 * ps '입력 메시지 담당 쓰레드'(InputMessageReader) 에 신규 소켓 등록 작업은 
			 * '출력 메시지 담당 쓰레드'(OutputMessageWriter) 와 '입력 메시지 처리 담당 쓰레드'(Executor) 보다 늦게 등록하도록 한다.
			 * 왜냐하면 '입력 메시지 담당 쓰레드'는  '출력 메시지 담당 쓰레드'와 '입력 메시지 처리 담당 쓰레드'와 달리 
			 * 읽기 전용 selector 등록이라는 부가 작업이 더 있기 때문이다.
			 * </pre>
			 */
			outputMessageWriterOfOwnerSC.addNewSocket(newAcceptedSC);
			executorOfOwnerSC.addNewSocket(newAcceptedSC);
			inputMessageReaderOfOwnerSC.addNewSocket(newAcceptedSC);
		}
	}

	@Override
	public void remove(SocketChannel ownerSC) {
		if (null == ownerSC) {
			throw new IllegalArgumentException("the parameter ownerSC is null");
		}
		
		synchronized (monitor) {
			socketChannel2SocketResourceHash.remove(ownerSC);
		}
	}

	@Override
	public SocketResource getSocketResource(SocketChannel ownerSC) {
		if (null == ownerSC) {
			throw new IllegalArgumentException("the ownerSC ownerSC is null");
		}
		
		
		SocketResource socketResource = null;
		synchronized (monitor) {
			socketResource = socketChannel2SocketResourceHash.get(ownerSC); 
		}
		return socketResource;
	}

	@Override
	public int getNumberOfSocketResources() {
		return socketChannel2SocketResourceHash.size();
	}
	
}
