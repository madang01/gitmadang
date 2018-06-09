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
import kr.pe.codda.client.connection.ClientMessageUtility;
import kr.pe.codda.client.connection.ClientObjectCacheManagerIF;
import kr.pe.codda.client.connection.ConnectionPoolSupporterIF;
import kr.pe.codda.client.connection.asyn.executor.AbstractClientTask;
import kr.pe.codda.client.connection.asyn.mainbox.AsynMessageMailbox;
import kr.pe.codda.client.connection.asyn.mainbox.SyncMessageMailbox;
import kr.pe.codda.common.etc.CommonStaticFinalVars;
import kr.pe.codda.common.exception.BodyFormatException;
import kr.pe.codda.common.exception.DynamicClassCallException;
import kr.pe.codda.common.exception.NoMoreDataPacketBufferException;
import kr.pe.codda.common.exception.NotSupportedException;
import kr.pe.codda.common.exception.ServerTaskException;
import kr.pe.codda.common.exception.ServerTaskPermissionException;
import kr.pe.codda.common.io.DataPacketBufferPoolIF;
import kr.pe.codda.common.io.SocketOutputStream;
import kr.pe.codda.common.io.WrapBuffer;
import kr.pe.codda.common.message.AbstractMessage;
import kr.pe.codda.common.protocol.MessageProtocolIF;
import kr.pe.codda.common.protocol.ReadableMiddleObjectWrapper;
import kr.pe.codda.common.protocol.ReceivedMessageBlockingQueueIF;

public class AsynThreadSafeSingleConnection
		implements AsynConnectionIF, ClientInterestedConnectionIF, ReceivedMessageBlockingQueueIF {
	private InternalLogger log = InternalLoggerFactory.getInstance(AsynThreadSafeSingleConnection.class);

	private String projectName = null;
	private String serverHost = null;
	private int serverPort = 0;
	private long socketTimeout = 0;
	int syncMessageMailboxCountPerAsynShareConnection;
	int clientAsynInputMessageQueueCapacity;
	private SocketOutputStream socketOutputStream = null;
	private MessageProtocolIF messageProtocol = null;
	private ClientObjectCacheManagerIF clientObjectCacheManager = null;
	private DataPacketBufferPoolIF dataPacketBufferPool = null;
	private AsynConnectedConnectionAdderIF asynConnectedConnectionAdder = null;
	private AsynClientIOEventControllerIF asynClientIOEventController = null;
	private ConnectionPoolSupporterIF connectionPoolSupporter = null;

	private SocketChannel clientSC = null;
	private java.util.Date finalReadTime = new java.util.Date();
	private ArrayBlockingQueue<SyncMessageMailbox> syncMessageMailboxQueue = null;
	private ArrayList<SyncMessageMailbox> syncMessageMailboxList = new ArrayList<SyncMessageMailbox>();

	private ArrayDeque<ArrayDeque<WrapBuffer>> inputMessageQueue = new ArrayDeque<ArrayDeque<WrapBuffer>>();

	public AsynThreadSafeSingleConnection(String projectName, String serverHost, int serverPort, long socketTimeout,
			int syncMessageMailboxCountPerAsynShareConnection, int clientAsynInputMessageQueueCapacity,
			SocketOutputStream socketOutputStream, MessageProtocolIF messageProtocol,
			ClientObjectCacheManagerIF clientObjectCacheManager, DataPacketBufferPoolIF dataPacketBufferPool,
			AsynConnectedConnectionAdderIF asynConnectedConnectionAdder,
			AsynClientIOEventControllerIF asynClientIOEventController,
			ConnectionPoolSupporterIF connectionPoolSupporter) throws IOException {
		this.projectName = projectName;
		this.serverHost = serverHost;
		this.serverPort = serverPort;
		this.socketTimeout = socketTimeout;
		this.syncMessageMailboxCountPerAsynShareConnection = syncMessageMailboxCountPerAsynShareConnection;
		this.clientAsynInputMessageQueueCapacity = clientAsynInputMessageQueueCapacity;
		this.socketOutputStream = socketOutputStream;
		this.messageProtocol = messageProtocol;
		this.clientObjectCacheManager = clientObjectCacheManager;
		this.dataPacketBufferPool = dataPacketBufferPool;
		this.asynConnectedConnectionAdder = asynConnectedConnectionAdder;
		this.asynClientIOEventController = asynClientIOEventController;
		this.connectionPoolSupporter = connectionPoolSupporter;

		openSocketChannel();

		syncMessageMailboxQueue = new ArrayBlockingQueue<SyncMessageMailbox>(
				syncMessageMailboxCountPerAsynShareConnection);

		for (int i = 0; i < syncMessageMailboxCountPerAsynShareConnection; i++) {
			SyncMessageMailbox syncMessageMailbox = new SyncMessageMailbox(this, i + 1, socketTimeout);

			syncMessageMailboxQueue.add(syncMessageMailbox);
			syncMessageMailboxList.add(syncMessageMailbox);
		}

		// inputMessageQueue = new
		// ArrayBlockingQueue<ArrayDeque<WrapBuffer>>(this.clientAsynInputMessageQueueCapacity);
	}

	private void openSocketChannel() throws IOException {
		clientSC = SocketChannel.open();
		clientSC.configureBlocking(false);
		clientSC.setOption(StandardSocketOptions.SO_KEEPALIVE, true);
		clientSC.setOption(StandardSocketOptions.TCP_NODELAY, true);
		clientSC.setOption(StandardSocketOptions.SO_LINGER, 0);
		clientSC.setOption(StandardSocketOptions.SO_REUSEADDR, true);
		// setReuseAddress

		// log.info("{} connection[{}] created", projectName, serverSC.hashCode());
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
			log.warn("drop the input message[{}] becase it failed to get a mailbox within socket timeout",
					inputMessage.toString());
			throw new SocketTimeoutException("fail to get a mailbox within socket timeout");
		}

		try {
			ClassLoader classloaderOfInputMessage = inputMessage.getClass().getClassLoader();

			syncMessageMailbox.nextMailID();
			inputMessage.messageHeaderInfo.mailboxID = syncMessageMailbox.getMailboxID();
			inputMessage.messageHeaderInfo.mailID = syncMessageMailbox.getMailID();

			ArrayDeque<WrapBuffer> inputMessageWrapBufferQueue = ClientMessageUtility.buildReadableWrapBufferList(
					messageProtocol, clientObjectCacheManager, classloaderOfInputMessage, inputMessage);

			addInputMessage(inputMessage, inputMessageWrapBufferQueue);

			ReadableMiddleObjectWrapper outputMessageWrapReadableMiddleObject = syncMessageMailbox
					.getSyncOutputMessage();

			AbstractMessage outputMessage = ClientMessageUtility.buildOutputMessage(messageProtocol,
					clientObjectCacheManager, classloaderOfInputMessage, outputMessageWrapReadableMiddleObject);

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

		ArrayDeque<WrapBuffer> inputMessageWrapBufferQueue = ClientMessageUtility.buildReadableWrapBufferList(
				messageProtocol, clientObjectCacheManager, inputMessage.getClass().getClassLoader(), inputMessage);

		addInputMessage(inputMessage, inputMessageWrapBufferQueue);

	}

	private void addInputMessage(AbstractMessage inputMessage, ArrayDeque<WrapBuffer> inputMessageWrapBufferQueue)
			throws InterruptedException, SocketTimeoutException {

		synchronized (inputMessageQueue) {
			if (inputMessageQueue.size() >= clientAsynInputMessageQueueCapacity) {
				inputMessageQueue.wait(socketTimeout);

				if (inputMessageQueue.size() >= clientAsynInputMessageQueueCapacity) {
					String errorMessage = new StringBuilder().append("fail to inserts the specified element[")
							.append(inputMessage.toString()).append("] into the input message queue while socket[")
							.append(hashCode()).append("timeout").toString();

					while (!inputMessageWrapBufferQueue.isEmpty()) {
						WrapBuffer wrapBuffer = inputMessageWrapBufferQueue.pollFirst();
						dataPacketBufferPool.putDataPacketBuffer(wrapBuffer);
					}

					throw new SocketTimeoutException(errorMessage);
				}
			}

			inputMessageQueue.add(inputMessageWrapBufferQueue);
			asynClientIOEventController.startWrite(this);
		}
	}

	@Override
	public void close() {
		try {
			clientSC.close();
		} catch (IOException e) {
			log.warn("fail to close the socket channel[{}], errmsg={}", clientSC.hashCode(), e.getMessage());
		}

		releaseResources();
	}

	private void releaseResources() {
		socketOutputStream.close();
		if (!inputMessageQueue.isEmpty()) {
			log.info("the var inputMessageStreamQueue is not a empty");

			do {
				ArrayDeque<WrapBuffer> inputMessageWrapBufferQueue = inputMessageQueue.poll();

				while (!inputMessageWrapBufferQueue.isEmpty()) {
					WrapBuffer wrapBuffer = inputMessageWrapBufferQueue.pollFirst();
					dataPacketBufferPool.putDataPacketBuffer(wrapBuffer);
				}
			} while (!inputMessageQueue.isEmpty());
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
			} catch (IOException e) {
				log.warn("fail to finish a connection[{}] becase io exception has been occurred, errmsg={}", hashCode(),
						e.getMessage());

				close();
				asynClientIOEventController.cancel(selectedKey);
				asynConnectedConnectionAdder.removeInterestedConnection(this);
				connectionPoolSupporter.notice("fail to finish a connection becase of io error");
				return;
			} catch (Exception e) {
				log.warn("fail to finish a connection[{}] becase unknown error has been occurred, errmsg={}",
						hashCode(), e.getMessage());

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
				} catch (Exception e) {
					log.warn(
							"fail to set this key's interest set to OP_READ becase unknown error has been occurred, errmsg={}",
							hashCode(), e.getMessage());

					close();

					asynClientIOEventController.cancel(selectedKey);
					return;
				}

				// try {
				asynConnectedConnectionAdder.addConnectedConnection(this);
				/*
				 * } catch (ConnectionPoolException e) { log.warn(e.getMessage(), e);
				 * asynConnectedConnectionAdder.removeInterestedConnection(this); close();
				 * 
				 * asynClientIOEventController.cancel(selectedKey); return; }
				 */
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
				String errorMessage = new StringBuilder("this socket channel[").append(clientSC.hashCode())
						.append("] has reached end-of-stream").toString();

				log.warn(errorMessage);
				close();
				asynClientIOEventController.cancel(selectedKey);
				return;
			}

			setFinalReadTime();

			messageProtocol.S2MList(socketOutputStream, this);

		} catch (NoMoreDataPacketBufferException e) {
			String errorMessage = new StringBuilder()
					.append("the no more data packet buffer error occurred while reading the socket[")
					.append(clientSC.hashCode()).append("], errmsg=").append(e.getMessage()).toString();
			log.warn(errorMessage, e);
			close();

			asynClientIOEventController.cancel(selectedKey);
			return;
		} catch (IOException e) {
			String errorMessage = new StringBuilder().append("the io error occurred while reading the socket[")
					.append(clientSC.hashCode()).append("], errmsg=").append(e.getMessage()).toString();
			log.warn(errorMessage, e);
			close();
			asynClientIOEventController.cancel(selectedKey);
			return;
		} catch (Exception e) {
			String errorMessage = new StringBuilder().append("the unknown error occurred while reading the socket[")
					.append(clientSC.hashCode()).append("], errmsg=").append(e.getMessage()).toString();
			log.warn(errorMessage, e);
			close();
			asynClientIOEventController.cancel(selectedKey);
			return;
		}

	}

	@Override
	public void onWrite(SelectionKey selectedKey) throws InterruptedException {

		ArrayDeque<WrapBuffer> inputMessageWrapBufferQueue = inputMessageQueue.peek();

		WrapBuffer currentWorkingWrapBuffer = inputMessageWrapBufferQueue.peek();
		ByteBuffer currentWorkingByteBuffer = currentWorkingWrapBuffer.getByteBuffer();

		int numberOfBytesWritten = 0;
		try {
			numberOfBytesWritten = clientSC.write(currentWorkingByteBuffer);
		} catch (IOException e) {
			String errorMessage = new StringBuilder().append("fail to write a sequence of bytes to this channel[")
					.append(clientSC.hashCode()).append("] because error occured, errmsg=").append(e.getMessage())
					.toString();
			log.warn(errorMessage, e);
			close();
			asynClientIOEventController.cancel(selectedKey);
			return;
		}

		if (numberOfBytesWritten > 0) {
			if (!currentWorkingByteBuffer.hasRemaining()) {
				inputMessageWrapBufferQueue.removeFirst();
				dataPacketBufferPool.putDataPacketBuffer(currentWorkingWrapBuffer);

				if (inputMessageWrapBufferQueue.isEmpty()) {
					synchronized (inputMessageQueue) {
						inputMessageQueue.poll();

						if (inputMessageQueue.isEmpty()) {
							selectedKey.interestOps(selectedKey.interestOps() & ~SelectionKey.OP_WRITE);
						}
						inputMessageQueue.notify();
					}
					return;
				}
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
	public void putReceivedMessage(ReadableMiddleObjectWrapper readableMiddleObjectWrapper)
			throws InterruptedException {
		int mailboxID = readableMiddleObjectWrapper.getMailboxID();
		if (CommonStaticFinalVars.ASYN_MAILBOX_ID == mailboxID) {
			try {
				String messageID = readableMiddleObjectWrapper.getMessageID();

				AbstractClientTask clientTask = null;
				try {
					clientTask = clientObjectCacheManager.getClientTask(messageID);
				} catch (DynamicClassCallException e) {
					log.warn(e.getMessage());
					return;
				} catch (Exception | Error e) {
					log.warn("unknwon error", e);
					return;
				}

				try {
					clientTask.execute(hashCode(), projectName, this, readableMiddleObjectWrapper,
							messageProtocol, clientObjectCacheManager);
				} catch (InterruptedException e) {
					throw e;
				} catch (Exception | Error e) {
					log.warn("unknwon error::fail to execute a output message client task", e);
					return;
				}

			} finally {
				readableMiddleObjectWrapper.closeReadableMiddleObject();
			}

		} else {
			SyncMessageMailbox syncMessageMailbox = null;
			try {
				syncMessageMailbox = syncMessageMailboxList.get(mailboxID - 1);
			} catch (IndexOutOfBoundsException e) {
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
