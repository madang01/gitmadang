package kr.pe.codda.server;

import java.nio.channels.SocketChannel;
import java.util.concurrent.ConcurrentHashMap;

import kr.pe.codda.common.exception.NoMoreDataPacketBufferException;
import kr.pe.codda.common.io.DataPacketBufferPoolIF;
import kr.pe.codda.common.io.SocketOutputStream;
import kr.pe.codda.common.io.SocketOutputStreamFactoryIF;
import kr.pe.codda.common.protocol.MessageProtocolIF;
import kr.pe.codda.server.threadpool.executor.ServerExecutorIF;
import kr.pe.codda.server.threadpool.executor.ServerExecutorPoolIF;

public class SocketResourceManager implements SocketResourceManagerIF {
	// private Logger log = LoggerFactory.getLogger(SocketResourceManager.class);

	// private final Object monitor = new Object();
	private long socketTimeOut=5000;
	private int outputMessageQueueSize=5;
	private SocketOutputStreamFactoryIF socketOutputStreamFactory = null;
	private ServerExecutorPoolIF serverExecutorPool = null;	
	private MessageProtocolIF messageProtocol = null;
	private DataPacketBufferPoolIF dataPacketBufferPool = null;
	private ServerIOEvenetControllerIF serverIOEvenetController = null;

	private ConcurrentHashMap<SocketChannel, SocketResource> socketChannel2SocketResourceHash = new ConcurrentHashMap<SocketChannel, SocketResource>();

	private ProjectLoginManagerIF projectLoginManager = new ProjectLoginManager();;
	
	// private ArrayDeque<WrapBuffer> outputMessageWrapBufferQueue = null;

	public SocketResourceManager(long socketTimeOut,
			int outputMessageQueueSize,
			SocketOutputStreamFactoryIF socketOutputStreamFactory,			
			MessageProtocolIF messageProtocol,
			DataPacketBufferPoolIF dataPacketBufferPool,
			ServerIOEvenetControllerIF serverIOEvenetController) {
		if (socketTimeOut < 0) {
			throw new IllegalArgumentException("the parameter socketTimeOut is less than zero");
		}
		
		if (outputMessageQueueSize <= 0) {
			throw new IllegalArgumentException("the parameter outputMessageQueueSize is less than or equal to zero");
		}
		
		if (null == socketOutputStreamFactory) {
			throw new IllegalArgumentException("the parameter socketOutputStreamFactory is null");
		}
		
		if (null == messageProtocol) {
			throw new IllegalArgumentException("the parameter messageProtocol is null");
		}
		
		if (null == dataPacketBufferPool) {
			throw new IllegalArgumentException("the parameter dataPacketBufferPool is null");
		}
		
		if (null == serverIOEvenetController) {
			throw new IllegalArgumentException("the parameter serverIOEvenetController is null");
		}

		this.socketTimeOut = socketTimeOut;
		this.outputMessageQueueSize = outputMessageQueueSize;
		this.socketOutputStreamFactory = socketOutputStreamFactory;
		this.messageProtocol = messageProtocol;
		this.dataPacketBufferPool = dataPacketBufferPool;
		this.serverIOEvenetController = serverIOEvenetController;
		
		serverIOEvenetController.setSocketResourceManager(this);
	}
	
	public void setServerExecutorPool(ServerExecutorPoolIF serverExecutorPool) {
		this.serverExecutorPool = serverExecutorPool;
	}
	

	@Override
	public void addNewAcceptedSocketChannel(SocketChannel newAcceptedSC)
			throws NoMoreDataPacketBufferException, InterruptedException {
		if (null == newAcceptedSC) {
			throw new IllegalArgumentException("the parameter newAcceptedSC is null");
		}		

		SocketOutputStream socketOutputStreamOfOwnerSC = socketOutputStreamFactory.newInstance();

		PersonalLoginManager personalLoginManagerOfOwnerSC = new PersonalLoginManager(newAcceptedSC,
				projectLoginManager);
		
		ServerExecutorIF executorOfOwnerSC = serverExecutorPool.getExecutorWithMinimumNumberOfSockets();

		SocketResource socketResourceOfOwnerSC = new SocketResource(newAcceptedSC,
				socketTimeOut,
				outputMessageQueueSize,
				messageProtocol,
				executorOfOwnerSC, socketOutputStreamOfOwnerSC,
				personalLoginManagerOfOwnerSC,
				dataPacketBufferPool, serverIOEvenetController);

		/** 소켓 자원 등록 작업 */
		socketChannel2SocketResourceHash.put(newAcceptedSC, socketResourceOfOwnerSC);
		
		executorOfOwnerSC.addNewSocket(newAcceptedSC);
	}

	@Override
	public void remove(SocketChannel ownerSC) {
		if (null == ownerSC) {
			throw new IllegalArgumentException("the parameter ownerSC is null");
		}

		socketChannel2SocketResourceHash.remove(ownerSC);

	}

	@Override
	public SocketResource getSocketResource(SocketChannel ownerSC) {
		if (null == ownerSC) {
			throw new IllegalArgumentException("the ownerSC ownerSC is null");
		}

		SocketResource socketResource = socketChannel2SocketResourceHash.get(ownerSC);

		return socketResource;
	}

	@Override
	public int getNumberOfSocketResources() {
		return socketChannel2SocketResourceHash.size();
	}

}
