package kr.pe.codda.client.connection.asyn.noshare;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.net.SocketTimeoutException;
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
import kr.pe.codda.client.connection.asyn.AsynClientIOEventControllerIF;
import kr.pe.codda.client.connection.asyn.AsynConnectedConnectionAdderIF;
import kr.pe.codda.client.connection.asyn.AsynConnectionIF;
import kr.pe.codda.client.connection.asyn.ClientInterestedConnectionIF;
import kr.pe.codda.client.connection.asyn.executor.ClientExecutorIF;
import kr.pe.codda.client.connection.asyn.mainbox.AsynMessageMailbox;
import kr.pe.codda.client.connection.asyn.mainbox.SyncMessageMailbox;
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
import kr.pe.codda.common.protocol.ReadableMiddleObjectWrapper;

public final class AsynNoShareConnection implements AsynConnectionIF, ClientInterestedConnectionIF, ReceivedMessageBlockingQueueIF {
	private InternalLogger log = InternalLoggerFactory.getInstance(AsynNoShareConnection.class);
	
	private ProjectPartConfiguration projectPartConfiguration = null;
	private SocketOutputStream socketOutputStream = null;
	private ClientMessageUtilityIF clientMessageUtility = null;
	private AsynConnectedConnectionAdderIF asynConnectedConnectionAdder = null;
	private AsynClientIOEventControllerIF asynSelectorManger = null;
	
	private SocketChannel clientSC = null;
	private java.util.Date finalReadTime = new java.util.Date();
	private SyncMessageMailbox syncMessageMailbox = null;
	
	private ArrayDeque<WrapBuffer> inputMessageWrapBufferQueue = null;
	
	private boolean isQueueIn = true;	
	
	private ClientExecutorIF clientExecutor = null;
	
	public AsynNoShareConnection(ProjectPartConfiguration projectPartConfiguration, 
			SocketOutputStream socketOutputStream,
			ClientMessageUtilityIF clientMessageUtility,
			AsynConnectedConnectionAdderIF asynConnectedConnectionAdder,
			ClientExecutorIF clientExecutor,
			AsynClientIOEventControllerIF asynSelectorManger) throws IOException {
		this.projectPartConfiguration = projectPartConfiguration;
		this.socketOutputStream = socketOutputStream;
		this.clientMessageUtility = clientMessageUtility;
		this.asynConnectedConnectionAdder = asynConnectedConnectionAdder;
		this.clientExecutor = clientExecutor;
		this.asynSelectorManger = asynSelectorManger;
		
		openSocketChannel();
		
		final int mailboxID = 1;
		syncMessageMailbox = new SyncMessageMailbox(this, mailboxID, projectPartConfiguration.getClientSocketTimeout());
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
		
		inputMessage.messageHeaderInfo.mailboxID = syncMessageMailbox.getMailboxID();
		inputMessage.messageHeaderInfo.mailID = syncMessageMailbox.getMailID();
		
		inputMessageWrapBufferQueue = clientMessageUtility.buildReadableWrapBufferList(classloaderOfInputMessage, inputMessage);
					
		asynSelectorManger.startWrite(this);
		
		ReadableMiddleObjectWrapper outputMessageWrapReadableMiddleObject = null;
		try {
			outputMessageWrapReadableMiddleObject = syncMessageMailbox.getSyncOutputMessage();
		} catch(SocketTimeoutException e) {
			if (! isConnected()) {
				throw new IOException("this socket is disconnected");
			}
			throw e;
		}
		
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
			
			asynSelectorManger.cancel(selectedKey);			
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
			
			asynSelectorManger.cancel(selectedKey);
			return;
		}
			
		if (isSuccess) {
			// log.info("the interested connection[{}] finished", hashCode());
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
				
				asynSelectorManger.cancel(selectedKey);
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
				releaseResources();
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
			try {
				close();
			} catch (IOException e1) {
				log.warn("fail to close the socket channel[{}] becase of io error, errmsg={}", 
						hashCode(), e1.getMessage());
			}
			releaseResources();
			
			asynSelectorManger.cancel(selectedKey);
			return;
		} catch (IOException e) {
			String errorMessage = new StringBuilder()
					.append("the io error occurred while reading the socket[")
					.append(clientSC.hashCode())
					.append("], errmsg=")
					.append(e.getMessage()).toString();
			log.warn(errorMessage, e);
			try {
				close();
			} catch (IOException e1) {
				log.warn("fail to close the socket channel[{}] becase of io error, errmsg={}", 
						hashCode(), e1.getMessage());
			}
			releaseResources();
			asynSelectorManger.cancel(selectedKey);
			return;
		} catch (Exception e) {
			String errorMessage = new StringBuilder()
					.append("the unknown error occurred while reading the socket[")
					.append(clientSC.hashCode())
					.append("], errmsg=")
					.append(e.getMessage()).toString();
			log.warn(errorMessage, e);
			try {
				close();
			} catch (IOException e1) {
				log.warn("fail to close the socket channel[{}] becase of io error, errmsg={}", 
						hashCode(), e1.getMessage());
			}
			releaseResources();
			asynSelectorManger.cancel(selectedKey);
			return;
		}
		
	}

	@Override
	public void onWrite(SelectionKey selectedKey) throws InterruptedException {		
		// FIXME!
		// log.info("onWrite::call");
				
		// ArrayDeque<WrapBuffer> inputMessageWrapBufferQueue = this.inputMessageWrapBufferQueue;
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
				try {
					close();
				} catch (IOException e1) {
					log.warn("fail to close the socket channel[{}], errmsg={}", 
							hashCode(), e1.getMessage());
				}
				
				releaseResources();
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
	public void releaseResources() {
		socketOutputStream.close();
		
		log.info("this connection[{}]'s resources has been released", clientSC.hashCode());
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
