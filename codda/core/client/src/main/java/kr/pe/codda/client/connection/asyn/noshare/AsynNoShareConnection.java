package kr.pe.codda.client.connection.asyn.noshare;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.net.StandardSocketOptions;
import java.nio.ByteBuffer;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.ArrayDeque;

import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;
import kr.pe.codda.client.connection.ClientMessageUtilityIF;
import kr.pe.codda.client.connection.ConnectionPoolSupporterIF;
import kr.pe.codda.client.connection.asyn.AsynClientIOEventControllerIF;
import kr.pe.codda.client.connection.asyn.AsynConnectedConnectionAdderIF;
import kr.pe.codda.client.connection.asyn.AsynConnectionIF;
import kr.pe.codda.client.connection.asyn.ClientInterestedConnectionIF;
import kr.pe.codda.client.connection.asyn.executor.ClientExecutorIF;
import kr.pe.codda.client.connection.asyn.mainbox.AsynMessageMailbox;
import kr.pe.codda.client.connection.asyn.mainbox.SyncMessageMailbox;
import kr.pe.codda.common.etc.CommonStaticFinalVars;
import kr.pe.codda.common.exception.BodyFormatException;
import kr.pe.codda.common.exception.ConnectionPoolException;
import kr.pe.codda.common.exception.DynamicClassCallException;
import kr.pe.codda.common.exception.NoMoreDataPacketBufferException;
import kr.pe.codda.common.exception.NotSupportedException;
import kr.pe.codda.common.exception.ServerTaskException;
import kr.pe.codda.common.exception.ServerTaskPermissionException;
import kr.pe.codda.common.io.SocketOutputStream;
import kr.pe.codda.common.io.WrapBuffer;
import kr.pe.codda.common.message.AbstractMessage;
import kr.pe.codda.common.protocol.ReadableMiddleObjectWrapper;
import kr.pe.codda.common.protocol.ReceivedMessageBlockingQueueIF;

/**
 * <pre>
 * Note that this implementation is not synchronized.
 * </pre>
 * 
 * @author Won Jonghoon
 *
 */
public final class AsynNoShareConnection implements AsynConnectionIF, ClientInterestedConnectionIF, ReceivedMessageBlockingQueueIF {
	private InternalLogger log = InternalLoggerFactory.getInstance(AsynNoShareConnection.class);
	
	// private ProjectPartConfiguration projectPartConfiguration = null;
	private String serverHost = null;
	private int serverPort  = 0;
	private long socketTimeout=0;
	
	private SocketOutputStream socketOutputStream = null;
	private ClientMessageUtilityIF clientMessageUtility = null;
	private AsynConnectedConnectionAdderIF asynConnectedConnectionAdder = null;
	private AsynClientIOEventControllerIF asynSelectorManger = null;
	private ConnectionPoolSupporterIF connectionPoolSupporter = null;
	
	private SocketChannel clientSC = null;
	private java.util.Date finalReadTime = new java.util.Date();
	private SyncMessageMailbox syncMessageMailbox = null;
	
	private ArrayDeque<WrapBuffer> inputMessageWrapBufferQueue = null;
	
	private boolean isQueueIn = true;	
	
	private ClientExecutorIF clientExecutor = null;
	
	public AsynNoShareConnection(String serverHost, 
			int serverPort,
			long socketTimeout, 
			SocketOutputStream socketOutputStream,
			ClientMessageUtilityIF clientMessageUtility,
			AsynConnectedConnectionAdderIF asynConnectedConnectionAdder,
			ClientExecutorIF clientExecutor,
			AsynClientIOEventControllerIF asynSelectorManger,
			ConnectionPoolSupporterIF connectionPoolSupporter) throws IOException {
		this.serverHost = serverHost;
		this.serverPort = serverPort;
		this.socketTimeout = socketTimeout;		
		this.socketOutputStream = socketOutputStream;
		this.clientMessageUtility = clientMessageUtility;
		this.asynConnectedConnectionAdder = asynConnectedConnectionAdder;
		this.clientExecutor = clientExecutor;
		this.asynSelectorManger = asynSelectorManger;
		this.connectionPoolSupporter = connectionPoolSupporter;
		
		openSocketChannel();
		
		final int mailboxID = 1;
		syncMessageMailbox = new SyncMessageMailbox(this, mailboxID, this.socketTimeout);
	}
	
	private void openSocketChannel() throws IOException {
		clientSC = SocketChannel.open();
		clientSC.configureBlocking(false);
		clientSC.setOption(StandardSocketOptions.SO_KEEPALIVE, true);
		clientSC.setOption(StandardSocketOptions.TCP_NODELAY, true);
		clientSC.setOption(StandardSocketOptions.SO_LINGER, 0);
		clientSC.setOption(StandardSocketOptions.SO_REUSEADDR, true);
		//log.info("{} connection[{}] created", projectName, serverSC.hashCode());
	}

	private void setFinalReadTime() {
		finalReadTime = new java.util.Date();
	}
	
	

	public java.util.Date getFinalReadTime() {
		return finalReadTime;
	}
	
	@Override
	public AbstractMessage sendSyncInputMessage(AbstractMessage inputMessage)
			throws InterruptedException, IOException, NoMoreDataPacketBufferException, DynamicClassCallException,
			BodyFormatException, ServerTaskException, ServerTaskPermissionException {
		
		ClassLoader classloaderOfInputMessage = inputMessage.getClass().getClassLoader();		
		
		syncMessageMailbox.nextMailID();
		inputMessage.messageHeaderInfo.mailboxID = syncMessageMailbox.getMailboxID();
		inputMessage.messageHeaderInfo.mailID = syncMessageMailbox.getMailID();
		
		inputMessageWrapBufferQueue = clientMessageUtility.buildReadableWrapBufferList(classloaderOfInputMessage, inputMessage);
					
		asynSelectorManger.startWrite(this);
		
		ReadableMiddleObjectWrapper outputMessageWrapReadableMiddleObject = syncMessageMailbox.getSyncOutputMessage();
		
		
		AbstractMessage outputMessage = clientMessageUtility.buildOutputMessage(classloaderOfInputMessage, outputMessageWrapReadableMiddleObject);
		
		
		return outputMessage;
	}

	@Override
	public void sendAsynInputMessage(AbstractMessage inputMessage) throws InterruptedException, NotSupportedException,
			IOException, NoMoreDataPacketBufferException, DynamicClassCallException, BodyFormatException {
		ClassLoader classloaderOfInputMessage = inputMessage.getClass().getClassLoader();
		
		inputMessage.messageHeaderInfo.mailboxID = AsynMessageMailbox.getMailboxID();
		inputMessage.messageHeaderInfo.mailID = AsynMessageMailbox.getNextMailID();
		
		inputMessageWrapBufferQueue = clientMessageUtility.buildReadableWrapBufferList(classloaderOfInputMessage, inputMessage);
					
		asynSelectorManger.startWrite(this);
	}

	@Override
	public void close() {
		
		
		try {
			clientSC.close();
		} catch(IOException e) {
			log.warn("fail to close the socket channel[{}], errmsg={}", 
					clientSC.hashCode(), e.getMessage());
		}
		
		releaseResources();
	}
	
	private void releaseResources() {
		socketOutputStream.close();
		clientExecutor.removeAsynConnection(this);
		
		if (null != inputMessageWrapBufferQueue) {
			if (! inputMessageWrapBufferQueue.isEmpty()) {
				log.info("the var inputMessageWrapBufferQueue is not a empty");
				
				do {
					WrapBuffer wrapBuffer = inputMessageWrapBufferQueue.pollFirst();
					clientMessageUtility.releaseWrapBuffer(wrapBuffer);
				} while (inputMessageWrapBufferQueue.isEmpty());
			}
			
		}
		
		log.info("this connection[{}]'s resources has been released", clientSC.hashCode());
	}

	public int hashCode() {
		return clientSC.hashCode();
	}

	@Override
	public void onConnect(SelectionKey selectedKey) {
		
		boolean isSuccess = false;
		try {
			isSuccess = clientSC.finishConnect();
		} catch(IOException e) {
			log.warn("fail to finish a connection[{}] becase io exception has been occurred, errmsg={}", hashCode(), e.getMessage());
			
			close();		
			asynSelectorManger.cancel(selectedKey);
			asynConnectedConnectionAdder.removeInterestedConnection(this);
			connectionPoolSupporter.notice("fail to finish a connection becase of io error");
			
			return;
		} catch (Exception e) {
			log.warn("fail to finish a connection[{}] becase unknown error has been occurred, errmsg={}", hashCode(), e.getMessage());
			
			close();				
			asynSelectorManger.cancel(selectedKey);
			asynConnectedConnectionAdder.removeInterestedConnection(this);
			connectionPoolSupporter.notice("fail to finish a connection becase of unknown error");
			return;
		}
			
		if (isSuccess) {
			// log.info("the interested connection[{}] finished", hashCode());
			try {
				selectedKey.interestOps(SelectionKey.OP_READ);
			} catch(Exception e) {
				log.warn("fail to set this key's interest set to OP_READ becase unknown error has been occurred, errmsg={}", hashCode(), e.getMessage());
				
				close();
				
				asynSelectorManger.cancel(selectedKey);
				return;
			}
			
			try {
				asynConnectedConnectionAdder.addConnectedConnection(this);
			} catch (ConnectionPoolException e) {
				log.warn(e.getMessage(), e);			
				asynConnectedConnectionAdder.removeInterestedConnection(this);
				close();
				
				asynSelectorManger.cancel(selectedKey);
				return;
			}			
		} else {
			log.debug("the interested connection[{}] unfinshed", hashCode());
		}	
		
	}

	@Override
	public void onRead(SelectionKey selectedKey) throws InterruptedException {
		try {
			int numberOfReadBytes = 0;			
			do {
				numberOfReadBytes = socketOutputStream.read(clientSC);
			} while (numberOfReadBytes > 0);

			if (numberOfReadBytes == -1) {
				String errorMessage = new StringBuilder("this socket channel[")
						.append(clientSC.hashCode())
						.append("] has reached end-of-stream").toString();

				log.warn(errorMessage);
				close();
				asynSelectorManger.cancel(selectedKey);
				return;
			}

			setFinalReadTime();

			clientMessageUtility.S2MList(clientSC, socketOutputStream, this);		
				
		} catch (NoMoreDataPacketBufferException e) {
			String errorMessage = new StringBuilder()
					.append("the no more data packet buffer error occurred while reading the socket[")
					.append(clientSC.hashCode())
					.append("], errmsg=")
					.append(e.getMessage()).toString();
			log.warn(errorMessage, e);
			close();
			
			asynSelectorManger.cancel(selectedKey);
			return;
		} catch (IOException e) {
			String errorMessage = new StringBuilder()
					.append("the io error occurred while reading the socket[")
					.append(clientSC.hashCode())
					.append("], errmsg=")
					.append(e.getMessage()).toString();
			log.warn(errorMessage, e);
			close();
			asynSelectorManger.cancel(selectedKey);
			return;
		} catch (Exception e) {
			String errorMessage = new StringBuilder()
					.append("the unknown error occurred while reading the socket[")
					.append(clientSC.hashCode())
					.append("], errmsg=")
					.append(e.getMessage()).toString();
			log.warn(errorMessage, e);
			close();
			asynSelectorManger.cancel(selectedKey);
			return;
		}
		
	}

	@Override
	public void onWrite(SelectionKey selectedKey) throws InterruptedException {
		WrapBuffer currentWorkingWrapBuffer = inputMessageWrapBufferQueue.peek();
		ByteBuffer currentWorkingByteBuffer = currentWorkingWrapBuffer.getByteBuffer();
		boolean loop = true;
		while (loop) {
			int numberOfBytesWritten  = 0;
			try {
				numberOfBytesWritten = clientSC.write(currentWorkingByteBuffer);
			} catch(IOException e) {
				String errorMessage = new StringBuilder()
						.append("fail to write a sequence of bytes to this channel[")
						.append(clientSC.hashCode())
						.append("] because io error occured, errmsg=")
						.append(e.getMessage()).toString();
				log.warn(errorMessage, e);				
				close();
				asynSelectorManger.cancel(selectedKey);
				return;
			}	
			
			
			if (0 == numberOfBytesWritten) {
				loop = false;
				return;
			}
			
			if (! currentWorkingByteBuffer.hasRemaining()) {
				inputMessageWrapBufferQueue.removeFirst();
				clientMessageUtility.releaseWrapBuffer(currentWorkingWrapBuffer);
				
				if (inputMessageWrapBufferQueue.isEmpty()) {					
					asynSelectorManger.endWrite(this);
						
					loop = false;
					return;
				}
				
				currentWorkingWrapBuffer = inputMessageWrapBufferQueue.peek();
				currentWorkingByteBuffer = currentWorkingWrapBuffer.getByteBuffer();
			}
		}
	}

	
	@Override
	public SelectionKey register(Selector ioEventSelector, int wantedInterestOps) throws ClosedChannelException {		
		SelectionKey registedKey = clientSC.register(ioEventSelector, wantedInterestOps);		
		return registedKey;
	}
	
	@Override
	public boolean doConect() throws IOException {
		SocketAddress serverAddress = new InetSocketAddress(serverHost, serverPort);
		return clientSC.connect(serverAddress);
	}

	@Override
	public SelectionKey keyFor(Selector ioEventSelector) {
		return clientSC.keyFor(ioEventSelector);
	}
	
	@Override
	public boolean finishConnect() throws IOException {		
		return clientSC.finishConnect();
	}

	@Override
	public void putReceivedMessage(ReadableMiddleObjectWrapper readableMiddleObjectWrapper) throws InterruptedException {		
		int mailboxID = readableMiddleObjectWrapper.getMailboxID();
		if (CommonStaticFinalVars.ASYN_MAILBOX_ID == mailboxID) {
			clientExecutor.putAsynOutputMessage(readableMiddleObjectWrapper);
		} else {
			syncMessageMailbox.putSyncOutputMessage(readableMiddleObjectWrapper);
		}
	}
	

	public boolean isConnected() {
		return clientSC.isConnected();
	}
	
	
	/**
	 * 큐 속에 들어갈때 상태 변경 메소드
	 */
	protected void queueIn() {
		isQueueIn = true;
		// log.info("NoShareAsynConnection[{}] queue in", this.hashCode());
	}

	/**
	 * 큐 밖으로 나갈때 상태 변경 메소드
	 */
	protected void queueOut() {
		isQueueIn = false;
		// log.info("NoShareAsynConnection[{}] queue out", this.hashCode());
	}
	
	public boolean isInQueue() {
		return isQueueIn;
	}

}
