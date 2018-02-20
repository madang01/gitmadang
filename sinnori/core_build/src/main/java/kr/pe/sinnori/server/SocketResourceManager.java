package kr.pe.sinnori.server;

import java.nio.channels.SocketChannel;
import java.nio.charset.CharsetDecoder;
import java.util.HashMap;

import kr.pe.sinnori.common.exception.NoMoreDataPacketBufferException;
import kr.pe.sinnori.common.io.DataPacketBufferPoolIF;
import kr.pe.sinnori.common.io.SocketOutputStream;
import kr.pe.sinnori.server.threadpool.IEOThreadPoolSetManagerIF;
import kr.pe.sinnori.server.threadpool.executor.handler.ServerExecutorIF;
import kr.pe.sinnori.server.threadpool.inputmessage.handler.InputMessageReaderIF;
import kr.pe.sinnori.server.threadpool.outputmessage.handler.OutputMessageWriterIF;

public class SocketResourceManager implements SocketResourceManagerIF {
	private final Object monitor = new Object();	
	
	private CharsetDecoder streamCharsetDecoder = null;
	private int dataPacketBufferMaxCntPerMessage = 0;
	private DataPacketBufferPoolIF dataPacketBufferPoolManager = null;
	private ProjectLoginManagerIF projectLoginManager = null;
	private IEOThreadPoolSetManagerIF ieoThreadPoolManager = null;
	
	private HashMap<SocketChannel, SocketResource> socketChannel2SocketResourceHash 
		= new HashMap<SocketChannel, SocketResource>(); 
	
	public SocketResourceManager( 
			CharsetDecoder streamCharsetDecoder,
			int dataPacketBufferMaxCntPerMessage,
			DataPacketBufferPoolIF dataPacketBufferPoolManager,
			ProjectLoginManagerIF projectLoginManager,
			IEOThreadPoolSetManagerIF ieoThreadPoolManager) {
		if (null == streamCharsetDecoder) {
			throw new IllegalArgumentException("the parameter streamCharsetDecoder is null");
		}
		
		if (dataPacketBufferMaxCntPerMessage <= 0) {
			String errorMessage = String.format("the parameter dataPacketBufferMaxCntPerMessage is less than or equal to zero", dataPacketBufferMaxCntPerMessage);
			throw new IllegalArgumentException(errorMessage);
		}
		
		if (null == dataPacketBufferPoolManager) {
			throw new IllegalArgumentException("the parameter dataPacketBufferPoolManager is null");
		}
		if (null == ieoThreadPoolManager) {
			throw new IllegalArgumentException("the parameter ieoThreadPoolManager is null");
		}
		
		this.streamCharsetDecoder = streamCharsetDecoder;
		this.dataPacketBufferMaxCntPerMessage = dataPacketBufferMaxCntPerMessage;
		this.dataPacketBufferPoolManager = dataPacketBufferPoolManager;
		this.projectLoginManager = projectLoginManager;
		this.ieoThreadPoolManager = ieoThreadPoolManager;
	}
	

	@Override
	public void addNewSocketChannel(SocketChannel newSC) throws NoMoreDataPacketBufferException {
		if (null == newSC) {
			throw new IllegalArgumentException("the parameter newSC is null");
		}		
		
		InputMessageReaderIF inputMessageReaderOfOwnerSC = 
				ieoThreadPoolManager.getInputMessageReaderWithMinimumMumberOfSockets();		
		
		ServerExecutorIF executorOfOwnerSC = 
				ieoThreadPoolManager.getExecutorWithMinimumMumberOfSockets();
		
		OutputMessageWriterIF outputMessageWriterOfOwnerSC = 
				ieoThreadPoolManager.getOutputMessageWriterWithMinimumMumberOfSockets();
		
		SocketOutputStream socketOutputStreamOfOwnerSC = 
				new SocketOutputStream(streamCharsetDecoder, 
						dataPacketBufferMaxCntPerMessage, 
						dataPacketBufferPoolManager);
		
		PersonalLoginManager personalLoginManagerOfOwnerSC = 
				new PersonalLoginManager(newSC, projectLoginManager);
		
		SocketResource socketResource = new SocketResource(
				newSC, 
				inputMessageReaderOfOwnerSC,
				executorOfOwnerSC,
				outputMessageWriterOfOwnerSC,
				socketOutputStreamOfOwnerSC, 
				personalLoginManagerOfOwnerSC);
		
		synchronized (monitor) {
			/** 소켓 자원 등록 작업 */
			socketChannel2SocketResourceHash.put(newSC, socketResource);
			
			/**
			 * <pre>
			 * Warning! 반듯이 신규 소켓을 '입력 메시지 담당 쓰레드'(InputMessageReader)에 등록 하기 앞서 소켓에 1:1로 할당되는 자원 등록 작업이 선행되어야 한다.
			 * 왜냐하면 '입력 메시지 담당 쓰레드' 에 신규 소켓 등록시 소켓에 1:1 로 할당된 자원중 하나인 출력 스트림을 이용한 입력 메시지 추출 작업이 진행되기때문이다.
			 * 
			 * ps : '입력 메시지 담당 쓰레드'(InputMessageReader) 에 신규 소켓 등록 작업은 
			 * '출력 메시지 담당 쓰레드'(OutputMessageWriter) 와 '입력 메시지 처리 담당 쓰레드'(Executor) 보다 늦게 등록하도록 한다.
			 * 왜냐하면 '입력 메시지 담당 쓰레드'는  '출력 메시지 담당 쓰레드'와 '입력 메시지 처리 담당 쓰레드'와 달리 
			 * 읽기 전용 selector 등록이라는 부가 작업이 더 있기 때문이다.
			 * </pre>
			 */
			outputMessageWriterOfOwnerSC.addNewSocket(newSC);
			executorOfOwnerSC.addNewSocket(newSC);
			inputMessageReaderOfOwnerSC.addNewSocket(newSC);
		}
	}

	@Override
	public void remove(SocketChannel ownerSC) {
		if (null == ownerSC) {
			throw new IllegalArgumentException("the parameter ownerSC is null");
		}
		
		boolean isRegisted = false;
		SocketResource socketResource = null;
		synchronized (monitor) {
			socketResource = socketChannel2SocketResourceHash.get(ownerSC);
			isRegisted = (null != socketResource);
			
			if (isRegisted) {
				socketChannel2SocketResourceHash.remove(ownerSC);
			}
			
		}
		
		if (isRegisted) {
			socketResource.close();
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
