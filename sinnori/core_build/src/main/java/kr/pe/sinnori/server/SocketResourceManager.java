package kr.pe.sinnori.server;

import java.nio.channels.SocketChannel;
import java.nio.charset.CharsetDecoder;
import java.util.HashMap;

import kr.pe.sinnori.common.exception.NoMoreDataPacketBufferException;
import kr.pe.sinnori.common.io.DataPacketBufferPoolIF;
import kr.pe.sinnori.common.io.SocketOutputStream;
import kr.pe.sinnori.server.threadpool.ServerThreadPoolManagerIF;
import kr.pe.sinnori.server.threadpool.executor.handler.ExecutorIF;
import kr.pe.sinnori.server.threadpool.inputmessage.handler.InputMessageReaderIF;
import kr.pe.sinnori.server.threadpool.outputmessage.handler.OutputMessageWriterIF;

public class SocketResourceManager implements SocketResourceManagerIF {
	private final Object monitor = new Object();	
	
	private CharsetDecoder streamCharsetDecoder = null;
	private int dataPacketBufferMaxCntPerMessage = 0;
	private DataPacketBufferPoolIF dataPacketBufferPoolManager = null;
	private ProjectLoginManagerIF projectLoginManager = null;
	private ServerThreadPoolManagerIF serverThreadPoolManager = null;
	
	private HashMap<SocketChannel, SocketResource> socketChannel2SocketResourceHash 
		= new HashMap<SocketChannel, SocketResource>(); 
	
	private SocketResourceManager( 
			CharsetDecoder streamCharsetDecoder,
			int dataPacketBufferMaxCntPerMessage,
			DataPacketBufferPoolIF dataPacketBufferPoolManager,
			ProjectLoginManagerIF projectLoginManager,
			ServerThreadPoolManagerIF serverThreadPoolManager) {
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
		if (null == serverThreadPoolManager) {
			throw new IllegalArgumentException("the parameter serverThreadPoolManager is null");
		}
		
		this.streamCharsetDecoder = streamCharsetDecoder;
		this.dataPacketBufferMaxCntPerMessage = dataPacketBufferMaxCntPerMessage;
		this.dataPacketBufferPoolManager = dataPacketBufferPoolManager;
		this.projectLoginManager = projectLoginManager;
		this.serverThreadPoolManager = serverThreadPoolManager;
	}
	
	public static class Builder {
		public static SocketResourceManager build(CharsetDecoder streamCharsetDecoder,
				int dataPacketBufferMaxCntPerMessage,
				DataPacketBufferPoolIF dataPacketBufferPoolManager,
				ProjectLoginManagerIF projectLoginManager,
				ServerThreadPoolManagerIF serverThreadPoolManager) {
            return new SocketResourceManager(
            		streamCharsetDecoder,
            		dataPacketBufferMaxCntPerMessage,
            		dataPacketBufferPoolManager,
            		projectLoginManager, serverThreadPoolManager);
        }
	}

	@Override
	public void addNewSocketChannel(SocketChannel newSC) throws NoMoreDataPacketBufferException {
		if (null == newSC) {
			throw new IllegalArgumentException("the newSC ownerSC is null");
		}
		
		
		InputMessageReaderIF inputMessageReaderWithMinimumMumberOfSockets = null;
		ExecutorIF executorWithMinimumMumberOfSockets = null;
		OutputMessageWriterIF outputMessageWriterWithMinimumMumberOfSockets = null;
		
		SocketOutputStream socketOutputStream = 
				new SocketOutputStream(streamCharsetDecoder, 
						dataPacketBufferMaxCntPerMessage, 
						dataPacketBufferPoolManager);
		
		PersonalLoginManager personalLoginManager = 
				new PersonalLoginManager(newSC, projectLoginManager);
		
		inputMessageReaderWithMinimumMumberOfSockets = serverThreadPoolManager.getInputMessageReaderWithMinimumMumberOfSockets();
		executorWithMinimumMumberOfSockets = serverThreadPoolManager.getExecutorWithMinimumMumberOfSockets();
		outputMessageWriterWithMinimumMumberOfSockets = serverThreadPoolManager.getOutputMessageWriterWithMinimumMumberOfSockets();
		
		SocketResource socketResource = new SocketResource(
				newSC, 
				inputMessageReaderWithMinimumMumberOfSockets,
				executorWithMinimumMumberOfSockets,
				outputMessageWriterWithMinimumMumberOfSockets,
				socketOutputStream, 
				personalLoginManager);
		
		synchronized (monitor) {			
			socketChannel2SocketResourceHash.put(newSC, socketResource);
		}
		
		/**
		 * <pre>
		 * Warning! 첫번째) 반듯이 소켓 리소스 등록 후 해당 메시지 입력/처리/출력 담당 쓰레드에 신규 소켓을 등록 시켜야 한다.
		 * 왜냐하면 '메시지 입력  담당 쓰레드'(=InputMessageReader)에 소켓 신규 등록시 
		 * 읽기 전용 selector 에 등록되어 데이터를 읽어 메시지를 추출하여 
		 * 등록된 소켓 리소스로 부터 얻은 '메시지 처리 담당 쓰레드'(=Executor) 로 넘기기때문이다.
		 * 
		 * 두번째) 각 메시지 입력/처리/출력 담당 쓰레드에 신규  소켓 등록시 '메시지 입력  담당 쓰레드'는 맨 마지막으로 해야 한다.
		 * 왜냐하면  '메시지 처리 담당 쓰레드'와 '메시지 출력 담당 쓰레드'(=OutputMessageWriter) 는 단순 등록일뿐이지만 
		 * '메시지 입력  담당 쓰레드' 는 읽기 전용 selector 에 소켓을 등록해야 하는 부과 작업이 더 있기때문이다.
		 * </pre>
		 */
		outputMessageWriterWithMinimumMumberOfSockets.addNewSocket(newSC);
		executorWithMinimumMumberOfSockets.addNewSocket(newSC);
		inputMessageReaderWithMinimumMumberOfSockets.addNewSocket(newSC);
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
