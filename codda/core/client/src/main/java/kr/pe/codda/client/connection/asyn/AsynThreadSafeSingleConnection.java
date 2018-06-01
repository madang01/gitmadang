package kr.pe.codda.client.connection.asyn;

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
import java.util.ArrayList;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.TimeUnit;

import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;
import kr.pe.codda.client.connection.ClientMessageUtilityIF;
import kr.pe.codda.client.connection.ConnectionPoolSupporterIF;
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

public class AsynThreadSafeSingleConnection implements AsynConnectionIF, ClientInterestedConnectionIF, ReceivedMessageBlockingQueueIF {
	private InternalLogger log = InternalLoggerFactory.getInstance(AsynThreadSafeSingleConnection.class);
	
	private String serverHost = null;
	private int serverPort  = 0;
	private long socketTimeout=0;
	int syncMessageMailboxCountPerAsynShareConnection;
	int inputMessageQueueSize;	
	private SocketOutputStream socketOutputStream = null;
	private ClientMessageUtilityIF clientMessageUtility = null;	
	private AsynConnectedConnectionAdderIF asynConnectedConnectionAdder = null;
	private ClientExecutorIF clientExecutor = null;
	private AsynClientIOEventControllerIF asynClientIOEventController = null;
	private ConnectionPoolSupporterIF connectionPoolSupporter = null;
	
	private SocketChannel clientSC = null;
	private java.util.Date finalReadTime = new java.util.Date();	
	private ArrayBlockingQueue<SyncMessageMailbox> syncMessageMailboxQueue = null;
	private ArrayList<SyncMessageMailbox> syncMessageMailboxList = new ArrayList<SyncMessageMailbox>();
	
	private ArrayDeque<ArrayDeque<WrapBuffer>> inputMessageStreamQueue = null;
	
	public AsynThreadSafeSingleConnection(String serverHost, 
			int serverPort,
			long socketTimeout, 
			int syncMessageMailboxCountPerAsynShareConnection, 
			int inputMessageQueueSize,
			SocketOutputStream socketOutputStream,
			ClientMessageUtilityIF clientMessageUtility,
			AsynConnectedConnectionAdderIF asynConnectedConnectionAdder,
			ClientExecutorIF clientExecutor,
			AsynClientIOEventControllerIF asynClientIOEventController,
			ConnectionPoolSupporterIF connectionPoolSupporter) throws IOException {
		this.serverHost = serverHost;
		this.serverPort = serverPort;
		this.socketTimeout = socketTimeout;
		this.syncMessageMailboxCountPerAsynShareConnection = syncMessageMailboxCountPerAsynShareConnection;
		this.inputMessageQueueSize = inputMessageQueueSize;		
		this.socketOutputStream = socketOutputStream;
		this.clientMessageUtility = clientMessageUtility;
		this.asynConnectedConnectionAdder = asynConnectedConnectionAdder;
		this.clientExecutor = clientExecutor;
		this.asynClientIOEventController = asynClientIOEventController;
		this.connectionPoolSupporter = connectionPoolSupporter;
		
		openSocketChannel();
		
		syncMessageMailboxQueue = new ArrayBlockingQueue<SyncMessageMailbox>(syncMessageMailboxCountPerAsynShareConnection);
		
		
		for (int i=0; i < syncMessageMailboxCountPerAsynShareConnection; i++) {
			SyncMessageMailbox syncMessageMailbox = new SyncMessageMailbox(this, i+1, socketTimeout);
			
			syncMessageMailboxQueue.add(syncMessageMailbox);
			syncMessageMailboxList.add(syncMessageMailbox);
		}
				
		inputMessageStreamQueue = new ArrayDeque<ArrayDeque<WrapBuffer>>(inputMessageQueueSize);
	}
	
	
	private void openSocketChannel() throws IOException {
		clientSC = SocketChannel.open();
		clientSC.configureBlocking(false);
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
	
	


	public java.util.Date getFinalReadTime() {
		return finalReadTime;
	}
	
	@Override
	public AbstractMessage sendSyncInputMessage(AbstractMessage inputMessage)
			throws InterruptedException, IOException, NoMoreDataPacketBufferException, DynamicClassCallException,
			BodyFormatException, ServerTaskException, ServerTaskPermissionException {		
		
		SyncMessageMailbox syncMessageMailbox = syncMessageMailboxQueue.poll(socketTimeout, TimeUnit.MILLISECONDS);
		if (null == syncMessageMailbox) {
			log.warn("drop the input message[{}] becase it failed to get a mailbox within socket timeout", inputMessage.toString());
			throw new SocketTimeoutException("fail to get a mailbox within socket timeout");
		}
		
		try {
			ClassLoader classloaderOfInputMessage = inputMessage.getClass().getClassLoader();
			
			syncMessageMailbox.nextMailID();
			inputMessage.messageHeaderInfo.mailboxID = syncMessageMailbox.getMailboxID();
			inputMessage.messageHeaderInfo.mailID = syncMessageMailbox.getMailID();
			
			ArrayDeque<WrapBuffer> inputMessageWrapBufferQueue = clientMessageUtility.buildReadableWrapBufferList(classloaderOfInputMessage, inputMessage);

			synchronized (inputMessageStreamQueue) {
				if (inputMessageStreamQueue.size() == inputMessageQueueSize) {
					inputMessageStreamQueue.wait(socketTimeout);
					
					if (inputMessageStreamQueue.size() == inputMessageQueueSize) {						
						log.warn("socket[{}] timeout occurs when entering an input message[{}]", 
								clientSC.hashCode(), inputMessage.toString());						
						throw new SocketTimeoutException("socket timeout occurs when entering an input message");
					}
				}
				
				inputMessageStreamQueue.addLast(inputMessageWrapBufferQueue);			
				asynClientIOEventController.startWrite(this);
			}
			
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
		} finally {
			syncMessageMailboxQueue.offer(syncMessageMailbox);
		}		
	}

	@Override
	public void sendAsynInputMessage(AbstractMessage inputMessage) throws InterruptedException, NotSupportedException,
			IOException, NoMoreDataPacketBufferException, DynamicClassCallException, BodyFormatException {
		
		
		inputMessage.messageHeaderInfo.mailboxID = AsynMessageMailbox.getMailboxID();
		inputMessage.messageHeaderInfo.mailID = AsynMessageMailbox.getNextMailID();
		
		ArrayDeque<WrapBuffer> inputMessageWrapBufferQueue = clientMessageUtility.buildReadableWrapBufferList(inputMessage.getClass().getClassLoader(), inputMessage);
		
		synchronized (inputMessageStreamQueue) {
			if (inputMessageStreamQueue.size() == inputMessageQueueSize) {
				inputMessageStreamQueue.wait(socketTimeout);
				
				if (inputMessageStreamQueue.size() == inputMessageQueueSize) {						
					log.warn("socket[{}] timeout occurs when entering an input message[{}]", 
							clientSC.hashCode(), inputMessage.toString());						
					throw new SocketTimeoutException("socket timeout occurs when entering an input message");
				}
			}
			
			inputMessageStreamQueue.addLast(inputMessageWrapBufferQueue);			
			asynClientIOEventController.startWrite(this);
		}
	}

	@Override
	public void close() {		
		try {
			clientSC.close();
		} catch (IOException e) {
			log.warn("fail to close the socket channel[{}], errmsg={}", 
					clientSC.hashCode(), e.getMessage());
		}
				
		releaseResources();
	}
	
	private void releaseResources() {
		socketOutputStream.close();
		clientExecutor.removeAsynConnection(this);
		
		if (! inputMessageStreamQueue.isEmpty()) {
			log.info("the var inputMessageStreamQueue is not a empty");
			
			do {
				ArrayDeque<WrapBuffer> inputMessageWrapBufferQueue  = inputMessageStreamQueue.pollFirst();
				
				while (! inputMessageWrapBufferQueue.isEmpty()) {
					WrapBuffer wrapBuffer = inputMessageWrapBufferQueue.pollFirst();
					clientMessageUtility.releaseWrapBuffer(wrapBuffer);
				}
			} while (! inputMessageStreamQueue.isEmpty());
		}
		
		
		log.info("this connection[{}]'s resources has been released", clientSC.hashCode());
	}

	public int hashCode() {
		return clientSC.hashCode();
	}

	@Override
	public void onConnect(SelectionKey selectedKey) {
		if (clientSC.isConnectionPending()) {
			boolean isSuccess = false;
			try {
				isSuccess = clientSC.finishConnect();
			} catch(IOException e) {
				log.warn("fail to finish a connection[{}] becase io exception has been occurred, errmsg={}", hashCode(), e.getMessage());
				
				close();				
				asynClientIOEventController.cancel(selectedKey);
				asynConnectedConnectionAdder.removeInterestedConnection(this);
				connectionPoolSupporter.notice("fail to finish a connection becase of io error");
				return;
			} catch (Exception e) {
				log.warn("fail to finish a connection[{}] becase unknown error has been occurred, errmsg={}", hashCode(), e.getMessage());
				
				close();				
				asynClientIOEventController.cancel(selectedKey);
				asynConnectedConnectionAdder.removeInterestedConnection(this);
				connectionPoolSupporter.notice("fail to finish a connection becase of unknow error");
				return;
			}
				
			if (isSuccess) {
				// log.info("the interested connection[{}] finished", hashCode());
				try {
					selectedKey.interestOps(SelectionKey.OP_READ);
				} catch(Exception e) {
					log.warn("fail to set this key's interest set to OP_READ becase unknown error has been occurred, errmsg={}", hashCode(), e.getMessage());
					
					close();
					
					asynClientIOEventController.cancel(selectedKey);
					return;
				}
				
				try {
					asynConnectedConnectionAdder.addConnectedConnection(this);
				} catch (ConnectionPoolException e) {
					log.warn(e.getMessage(), e);			
					asynConnectedConnectionAdder.removeInterestedConnection(this);
					close();
					
					asynClientIOEventController.cancel(selectedKey);
					return;
				}			
			} else {
				log.debug("the interested connection[{}] unfinshed", hashCode());
			}	
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
				asynClientIOEventController.cancel(selectedKey);
				return;
			}

			setFinalReadTime();

			clientMessageUtility.S2MList(this, socketOutputStream, this);		
				
		} catch (NoMoreDataPacketBufferException e) {
			String errorMessage = new StringBuilder()
					.append("the no more data packet buffer error occurred while reading the socket[")
					.append(clientSC.hashCode())
					.append("], errmsg=")
					.append(e.getMessage()).toString();
			log.warn(errorMessage, e);
			close();
			
			asynClientIOEventController.cancel(selectedKey);
			return;
		} catch (IOException e) {
			String errorMessage = new StringBuilder()
					.append("the io error occurred while reading the socket[")
					.append(clientSC.hashCode())
					.append("], errmsg=")
					.append(e.getMessage()).toString();
			log.warn(errorMessage, e);
			close();
			asynClientIOEventController.cancel(selectedKey);
			return;
		} catch (Exception e) {
			String errorMessage = new StringBuilder()
					.append("the unknown error occurred while reading the socket[")
					.append(clientSC.hashCode())
					.append("], errmsg=")
					.append(e.getMessage()).toString();
			log.warn(errorMessage, e);
			close();
			asynClientIOEventController.cancel(selectedKey);
			return;
		}
		
	}

	@Override
	public void onWrite(SelectionKey selectedKey) throws InterruptedException {				
		ArrayDeque<WrapBuffer> inputMessageWrapBufferQueue = inputMessageStreamQueue.peek();
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
						.append("] because error occured, errmsg=")
						.append(e.getMessage()).toString();
				log.warn(errorMessage, e);
				close();
				asynClientIOEventController.cancel(selectedKey);
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
					synchronized (inputMessageStreamQueue) {
						inputMessageStreamQueue.removeFirst();
						try {
							if (inputMessageStreamQueue.isEmpty()) {
								asynClientIOEventController.endWrite(this);
								loop = false;
								return;
							}
						} finally {
							inputMessageStreamQueue.notify();
						}
					}
					
					
					inputMessageWrapBufferQueue = inputMessageStreamQueue.peek();
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
			SyncMessageMailbox syncMessageMailbox = null;
			try {
				syncMessageMailbox = syncMessageMailboxList.get(mailboxID - 1);
			} catch(IndexOutOfBoundsException e) {
				log.warn("fail to match a mailbox of the received message[{}], errmsg={}", 
						readableMiddleObjectWrapper.toSimpleInformation(), e.getMessage());
				return;
			}			
			syncMessageMailbox.putSyncOutputMessage(readableMiddleObjectWrapper);
		}
	}
	

	public boolean isConnected() {
		return clientSC.isConnected();
	}	
}
