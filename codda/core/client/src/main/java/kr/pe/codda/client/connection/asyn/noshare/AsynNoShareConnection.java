package kr.pe.codda.client.connection.asyn.noshare;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.net.SocketTimeoutException;
import java.net.StandardSocketOptions;
import java.nio.ByteBuffer;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.SelectableChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.ArrayDeque;

import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;
import kr.pe.codda.client.connection.ClientMessageUtilityIF;
import kr.pe.codda.client.connection.asyn.AsynConnectedConnectionAdderIF;
import kr.pe.codda.client.connection.asyn.AsynConnectionIF;
import kr.pe.codda.client.connection.asyn.AsynSelectorMangerIF;
import kr.pe.codda.client.connection.asyn.InterestedAsynConnectionIF;
import kr.pe.codda.client.connection.asyn.executor.ClientExecutorIF;
import kr.pe.codda.client.connection.asyn.mainbox.SyncMailboxForAsynNoShare;
import kr.pe.codda.common.config.subset.ProjectPartConfiguration;
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
import kr.pe.codda.common.protocol.ReceivedMessageBlockingQueueIF;
import kr.pe.codda.common.protocol.WrapReadableMiddleObject;

public class AsynNoShareConnection implements AsynConnectionIF, InterestedAsynConnectionIF, ReceivedMessageBlockingQueueIF {
	protected InternalLogger log = InternalLoggerFactory.getInstance(AsynNoShareConnection.class);
	
	protected ProjectPartConfiguration projectPartConfiguration = null;
	protected SocketOutputStream socketOutputStream = null;
	protected ClientMessageUtilityIF clientMessageUtility = null;
	protected AsynConnectedConnectionAdderIF asynConnectedConnectionAdder = null;
	protected AsynSelectorMangerIF asynSelectorManger = null;
	
	protected SocketChannel clientSC = null;
	protected java.util.Date finalReadTime = new java.util.Date();
	protected SelectableChannel serverSelectableChannel = null;	
	protected SyncMailboxForAsynNoShare syncMailboxForAsynNoShare = null;
	
	protected ArrayDeque<ArrayDeque<WrapBuffer>> inputMessageQueue = null;	
	
	protected boolean isQueueIn = true;	
	
	protected ClientExecutorIF clientExecutor = null;
	
	public AsynNoShareConnection(ProjectPartConfiguration projectPartConfiguration, 
			SocketOutputStream socketOutputStream,
			ClientMessageUtilityIF clientMessageUtility,
			AsynConnectedConnectionAdderIF asynConnectedConnectionAdder,
			ClientExecutorIF clientExecutor,
			AsynSelectorMangerIF asynSelectorManger) throws IOException {
		this.projectPartConfiguration = projectPartConfiguration;
		this.socketOutputStream = socketOutputStream;
		this.clientMessageUtility = clientMessageUtility;
		this.asynConnectedConnectionAdder = asynConnectedConnectionAdder;
		this.clientExecutor = clientExecutor;
		this.asynSelectorManger = asynSelectorManger;
		
		openSocketChannel();
		
		final int mailboxID = 1;
		syncMailboxForAsynNoShare = new SyncMailboxForAsynNoShare(this, mailboxID, projectPartConfiguration.getClientSocketTimeout());
				
		inputMessageQueue = new ArrayDeque<ArrayDeque<WrapBuffer>>(projectPartConfiguration.getClientAsynInputMessageQueueSize());
	}
	
	private void openSocketChannel() throws IOException {
		clientSC = SocketChannel.open();
		serverSelectableChannel = clientSC.configureBlocking(false);
		clientSC.setOption(StandardSocketOptions.SO_KEEPALIVE, true);
		clientSC.setOption(StandardSocketOptions.TCP_NODELAY, true);
		clientSC.setOption(StandardSocketOptions.SO_LINGER, 0);
		clientSC.setOption(StandardSocketOptions.SO_REUSEADDR, true);		
		//setReuseAddress
		
		//log.info("{} connection[{}] created", projectName, serverSC.hashCode());
	}

	private void setFinalReadTime() {
		finalReadTime = new java.util.Date();
	}
	
	@Override
	public AbstractMessage sendSyncInputMessage(AbstractMessage inputMessage)
			throws InterruptedException, IOException, NoMoreDataPacketBufferException, DynamicClassCallException,
			BodyFormatException, ServerTaskException, ServerTaskPermissionException {
		
		inputMessage.messageHeaderInfo.mailboxID = syncMailboxForAsynNoShare.getMailboxID();
		inputMessage.messageHeaderInfo.mailID = syncMailboxForAsynNoShare.getMailID();
		
		ArrayDeque<WrapBuffer> inputMessageWrapBufferQueue = clientMessageUtility.buildReadableWrapBufferList(this.getClass().getClassLoader(), inputMessage);
		
	
		synchronized (inputMessageQueue) {
			if (inputMessageQueue.size() == projectPartConfiguration.getClientAsynInputMessageQueueSize()) {
				inputMessageQueue.wait(projectPartConfiguration.getClientSocketTimeout());
				
				if (inputMessageQueue.size() == projectPartConfiguration.getClientAsynInputMessageQueueSize()) {						
					log.warn("socket[{}] timeout occurs when entering an input message[{}]", 
							clientSC.hashCode(), inputMessage.toString());						
					throw new SocketTimeoutException("socket timeout occurs when entering an input message");
				}
			}
			
			inputMessageQueue.addLast(inputMessageWrapBufferQueue);			
			asynSelectorManger.startWrite(this);
		}
		
		WrapReadableMiddleObject outputMessageWrapReadableMiddleObject = null;
		try {
			outputMessageWrapReadableMiddleObject = syncMailboxForAsynNoShare.getSyncOutputMessage();
		} catch(SocketTimeoutException e) {
			if (! isConnected()) {
				throw new IOException("this socket is disconnected");
			}
			throw e;
		}
		
		AbstractMessage outputMessage = clientMessageUtility.buildOutputMessage(getClass().getClassLoader(), outputMessageWrapReadableMiddleObject);
		
		
		return outputMessage;
	}

	@Override
	public void sendAsynInputMessage(AbstractMessage inputMessage) throws InterruptedException, NotSupportedException,
			IOException, NoMoreDataPacketBufferException, DynamicClassCallException, BodyFormatException {
		inputMessage.messageHeaderInfo.mailboxID = syncMailboxForAsynNoShare.getMailboxID();
		inputMessage.messageHeaderInfo.mailID = syncMailboxForAsynNoShare.getMailID();
		
		ArrayDeque<WrapBuffer> inputMessageWrapBufferQueue = clientMessageUtility.buildReadableWrapBufferList(this.getClass().getClassLoader(), inputMessage);
		
		synchronized (inputMessageQueue) {
			if (inputMessageQueue.size() == projectPartConfiguration.getClientAsynInputMessageQueueSize()) {
				inputMessageQueue.wait(projectPartConfiguration.getClientSocketTimeout());
				
				if (inputMessageQueue.size() == projectPartConfiguration.getClientAsynInputMessageQueueSize()) {						
					log.warn("socket[{}] timeout occurs when entering an input message[{}]", 
							clientSC.hashCode(), inputMessage.toString());						
					throw new SocketTimeoutException("socket timeout occurs when entering an input message");
				}
			}
			
			inputMessageQueue.addLast(inputMessageWrapBufferQueue);			
			asynSelectorManger.startWrite(this);
		}
	}

	@Override
	public void close() throws IOException {
		clientExecutor.removeAsynConnection(this);
		clientSC.close();
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
			
			try {
				close();
			} catch (IOException e1) {
				log.warn("fail to close the socket channel[{}] becase of io error, errmsg={}", 
						hashCode(), e1.getMessage());
			}
			releaseResources();
			
			selectedKey.channel();			
			return;
		} catch (Exception e) {
			log.warn("fail to finish a connection[{}] becase unknown error has been occurred, errmsg={}", hashCode(), e.getMessage());
			
			try {
				close();
			} catch (IOException e1) {
				log.warn("fail to close the socket channel[{}] becase of io error, errmsg={}", 
						hashCode(), e1.getMessage());
			}
			releaseResources();
			
			selectedKey.channel();
			return;
		}
			
		if (isSuccess) {
			log.info("the interested connection[{}] finished", hashCode());
			try {
				selectedKey.interestOps(SelectionKey.OP_READ);
			} catch(Exception e) {
				log.warn("fail to set this key's interest set to OP_READ becase unknown error has been occurred, errmsg={}", hashCode(), e.getMessage());
				
				try {
					close();
				} catch (IOException e1) {
					log.warn("fail to close the socket channel[{}] becase of io error, errmsg={}", 
							hashCode(), e1.getMessage());
				}
				releaseResources();
				
				selectedKey.channel();
				return;
			}
			
			try {
				asynConnectedConnectionAdder.addConnectedConnection(this);
			} catch (ConnectionPoolException e) {
				log.warn(e.getMessage(), e);			
				asynConnectedConnectionAdder.removeInterestedConnection(this);
				try {
					close();
				} catch (IOException e1) {
					log.warn("fail to close the socket channel[{}] becase of io error, errmsg={}", 
							hashCode(), e1.getMessage());
				}
				releaseResources();
				
				selectedKey.channel();
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
				releaseResources();
				selectedKey.channel();
				return;
			}

			setFinalReadTime();

			clientMessageUtility.S2MList(clientSC, socketOutputStream, this);		
				
		} catch (NoMoreDataPacketBufferException e) {
			String errorMessage = new StringBuilder()
					.append("소켓[")
					.append(clientSC.hashCode())
					.append("] 읽기 작업중 데이터 패킷 버퍼 부족 에러 발생").toString();
			log.warn(errorMessage, e);
			try {
				close();
			} catch (IOException e1) {
				log.warn("fail to close the socket channel[{}] becase of io error, errmsg={}", 
						hashCode(), e1.getMessage());
			}
			releaseResources();
			
			selectedKey.channel();
			return;
		} catch (IOException e) {
			String errorMessage = new StringBuilder()
					.append("소켓[")
					.append(clientSC.hashCode())
					.append("] 읽기 작업중 IO 에러 발생, 상세사유=")
					.append(e.getMessage()).toString();
			log.warn(errorMessage, e);
			try {
				close();
			} catch (IOException e1) {
				log.warn("fail to close the socket channel[{}] becase of io error, errmsg={}", 
						hashCode(), e1.getMessage());
			}
			releaseResources();
			selectedKey.channel();
			return;
		}
		
	}

	@Override
	public void onWrite(SelectionKey selectedKey) throws InterruptedException {
		ArrayDeque<WrapBuffer> inputMessageWrapBufferQueue = inputMessageQueue.peek();
		WrapBuffer currentWorkingWrapBuffer = inputMessageWrapBufferQueue.peek();
		ByteBuffer currentWorkingByteBuffer = currentWorkingWrapBuffer.getByteBuffer();
		boolean loop = true;
		while (loop) {
			int numberOfBytesWritten  = 0;
			try {
				numberOfBytesWritten = clientSC.write(currentWorkingByteBuffer);
			} catch(IOException e) {
				String errorMessage = new StringBuilder()
						.append("소켓[")
						.append(clientSC.hashCode())
						.append("] 쓰기 작업중 IO 에러 발생, 상세사유=")
						.append(e.getMessage()).toString();
				log.warn(errorMessage, e);
				try {
					close();
				} catch (IOException e1) {
					log.warn("fail to close the socket channel[{}] becase of io error, errmsg={}", 
							hashCode(), e1.getMessage());
				}
				
				releaseResources();
				selectedKey.channel();
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
					synchronized (inputMessageQueue) {
						inputMessageQueue.removeFirst();
						try {
							if (inputMessageQueue.isEmpty()) {
								asynSelectorManger.endWrite(this);
								loop = false;
								return;
							}
						} finally {
							inputMessageQueue.notify();
						}
					}
					
					inputMessageWrapBufferQueue = inputMessageQueue.peek();
				}
				
				currentWorkingWrapBuffer = inputMessageWrapBufferQueue.peek();
				currentWorkingByteBuffer = currentWorkingWrapBuffer.getByteBuffer();
			}
		}
	}

	@Override
	public void releaseResources() {
		socketOutputStream.close();
	}

	@Override
	public SelectionKey register(Selector ioEventSelector, int wantedInterestOps) throws ClosedChannelException {
		SelectionKey registedKey = clientSC.register(ioEventSelector, wantedInterestOps);		
		return registedKey;
	}
	
	@Override
	public boolean doConect() throws IOException {
		SocketAddress serverAddress = new InetSocketAddress(projectPartConfiguration.getServerHost(), projectPartConfiguration.getServerPort());
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
	public void putReceivedMessage(WrapReadableMiddleObject wrapReadableMiddleObject) throws InterruptedException {
		int mailboxID = wrapReadableMiddleObject.getMailboxID();
		if (CommonStaticFinalVars.ASYN_MAILBOX_ID == mailboxID) {
			clientExecutor.putAsynOutputMessage(wrapReadableMiddleObject);
		} else {
			syncMailboxForAsynNoShare.putSyncOutputMessage(wrapReadableMiddleObject);
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
