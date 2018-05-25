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

public class AcceptedConnectionManager implements AcceptedConnectionManagerIF {
	// private Logger log = LoggerFactory.getLogger(SocketResourceManager.class);

	// private final Object monitor = new Object();
	private long socketTimeOut=5000;
	private int outputMessageQueueSize=5;
	private SocketOutputStreamFactoryIF socketOutputStreamFactory = null;
	private ServerExecutorPoolIF serverExecutorPool = null;	
	private MessageProtocolIF messageProtocol = null;
	private DataPacketBufferPoolIF dataPacketBufferPool = null;
	private ServerIOEvenetControllerIF serverIOEvenetController = null;

	private ConcurrentHashMap<SocketChannel, AcceptedConnection> socketChannel2SocketResourceHash = new ConcurrentHashMap<SocketChannel, AcceptedConnection>();

	private ProjectLoginManagerIF projectLoginManager = null;
	
	// private ArrayDeque<WrapBuffer> outputMessageWrapBufferQueue = null;

	public AcceptedConnectionManager(long socketTimeOut,
			int outputMessageQueueSize,
			ProjectLoginManagerIF projectLoginManager,
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
		
		if (null == projectLoginManager) {
			throw new IllegalArgumentException("the parameter projectLoginManager is null");
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
		this.projectLoginManager = projectLoginManager;
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

		SocketOutputStream socketOutputStreamOfAcceptedSC = socketOutputStreamFactory.newInstance();

		PersonalLoginManager personalLoginManagerOfAcceptedSC = new PersonalLoginManager(newAcceptedSC,
				projectLoginManager);
		
		ServerExecutorIF executorOfAcceptedSC = serverExecutorPool.getExecutorWithMinimumNumberOfSockets();

		AcceptedConnection acceptedConnection = new AcceptedConnection(this, newAcceptedSC,
				socketTimeOut,
				outputMessageQueueSize,
				socketOutputStreamOfAcceptedSC,
				personalLoginManagerOfAcceptedSC,
				executorOfAcceptedSC,
				messageProtocol,				
				dataPacketBufferPool, 
				serverIOEvenetController);

		/** 소켓 자원 등록 작업 */
		socketChannel2SocketResourceHash.put(newAcceptedSC, acceptedConnection);
		
		executorOfAcceptedSC.addNewSocket(newAcceptedSC);
	}

	@Override
	public void remove(SocketChannel ownerSC) {
		if (null == ownerSC) {
			throw new IllegalArgumentException("the parameter ownerSC is null");
		}

		socketChannel2SocketResourceHash.remove(ownerSC);

	}

	@Override
	public AcceptedConnection getAcceptedConnection(SocketChannel ownerSC) {
		if (null == ownerSC) {
			throw new IllegalArgumentException("the ownerSC ownerSC is null");
		}

		AcceptedConnection socketResource = socketChannel2SocketResourceHash.get(ownerSC);

		return socketResource;
	}

	@Override
	public int getNumberOfAcceptedConnection() {
		return socketChannel2SocketResourceHash.size();
	}

}
