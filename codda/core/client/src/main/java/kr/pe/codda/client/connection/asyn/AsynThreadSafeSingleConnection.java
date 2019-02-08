package kr.pe.codda.client.connection.asyn;

import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.net.SocketTimeoutException;
import java.net.StandardSocketOptions;
import java.nio.ByteBuffer;
import java.nio.channels.CancelledKeyException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.TimeUnit;

import kr.pe.codda.client.classloader.ClientTaskMangerIF;
import kr.pe.codda.client.connection.ClientMessageUtility;
import kr.pe.codda.client.connection.asyn.mainbox.AsynMessageMailbox;
import kr.pe.codda.client.connection.asyn.mainbox.SyncMessageMailbox;
import kr.pe.codda.client.task.AbstractClientTask;
import kr.pe.codda.common.classloader.MessageCodecMangerIF;
import kr.pe.codda.common.etc.CommonStaticFinalVars;
import kr.pe.codda.common.exception.BodyFormatException;
import kr.pe.codda.common.exception.ConnectionPoolTimeoutException;
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

public class AsynThreadSafeSingleConnection implements AsynConnectionIF,
		ClientIOEventHandlerIF, ReceivedMessageBlockingQueueIF {
	private InternalLogger log = InternalLoggerFactory
			.getInstance(AsynThreadSafeSingleConnection.class);

	private final Object writeMonitor = new Object();
	private final Object readMonitor = new Object();
	private String projectName = null;
	private String serverHost = null;
	private int serverPort = 0;
	private long socketTimeout = 0;
	int syncMessageMailboxCountPerAsynShareConnection;
	int clientAsynInputMessageQueueCapacity;
	private SocketOutputStream socketOutputStream = null;
	private MessageProtocolIF messageProtocol = null;
	private DataPacketBufferPoolIF dataPacketBufferPool = null;
	private ClientTaskMangerIF clientTaskManger = null;
	private AsynConnectedConnectionAdderIF asynConnectedConnectionAdder = null;
	private ClientIOEventControllerIF asynClientIOEventController = null;

	private SocketChannel clientSC = null;
	private SelectionKey personalSelectionKey = null;
	private java.util.Date finalReadTime = new java.util.Date();
	private ArrayBlockingQueue<SyncMessageMailbox> syncMessageMailboxQueue = null;
	private ArrayList<SyncMessageMailbox> syncMessageMailboxList = new ArrayList<SyncMessageMailbox>();

	private ArrayDeque<ArrayDeque<WrapBuffer>> inputMessageQueue = new ArrayDeque<ArrayDeque<WrapBuffer>>();

	public AsynThreadSafeSingleConnection(String projectName,
			String serverHost, int serverPort, long socketTimeout,
			int syncMessageMailboxCountPerAsynShareConnection,
			int clientAsynInputMessageQueueCapacity,
			SocketOutputStream socketOutputStream,
			MessageProtocolIF messageProtocol,
			DataPacketBufferPoolIF dataPacketBufferPool,
			ClientTaskMangerIF clientTaskManger,
			AsynConnectedConnectionAdderIF asynConnectedConnectionAdder,
			ClientIOEventControllerIF asynClientIOEventController)
			throws IOException {
		this.projectName = projectName;
		this.serverHost = serverHost;
		this.serverPort = serverPort;
		this.socketTimeout = socketTimeout;
		this.syncMessageMailboxCountPerAsynShareConnection = syncMessageMailboxCountPerAsynShareConnection;
		this.clientAsynInputMessageQueueCapacity = clientAsynInputMessageQueueCapacity;
		this.socketOutputStream = socketOutputStream;
		this.messageProtocol = messageProtocol;
		this.dataPacketBufferPool = dataPacketBufferPool;
		this.clientTaskManger = clientTaskManger;
		this.asynConnectedConnectionAdder = asynConnectedConnectionAdder;
		this.asynClientIOEventController = asynClientIOEventController;

		openSocketChannel();

		syncMessageMailboxQueue = new ArrayBlockingQueue<SyncMessageMailbox>(
				syncMessageMailboxCountPerAsynShareConnection);

		for (int i = 0; i < syncMessageMailboxCountPerAsynShareConnection; i++) {
			SyncMessageMailbox syncMessageMailbox = new SyncMessageMailbox(
					this, i + 1, socketTimeout);

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

		// log.info("{} connection[{}] created", projectName,
		// serverSC.hashCode());
	}

	private void setFinalReadTime() {
		finalReadTime = new java.util.Date();
	}

	public java.util.Date getFinalReadTime() {
		return finalReadTime;
	}

	@Override
	public AbstractMessage sendSyncInputMessage(
			MessageCodecMangerIF messageCodecManger,
			AbstractMessage inputMessage) throws InterruptedException,
			IOException, NoMoreDataPacketBufferException,
			DynamicClassCallException, BodyFormatException,
			ServerTaskException, ServerTaskPermissionException {

		SyncMessageMailbox syncMessageMailbox = syncMessageMailboxQueue.poll(
				socketTimeout, TimeUnit.MILLISECONDS);
		if (null == syncMessageMailbox) {
			log.warn(
					"drop the input message[{}] becase it failed to get a mailbox within socket timeout",
					inputMessage.toString());
			throw new SocketTimeoutException(
					"fail to get a mailbox within socket timeout");
		}

		try {
			// ClassLoader classloaderOfInputMessage =
			// inputMessage.getClass().getClassLoader();

			syncMessageMailbox.nextMailID();
			inputMessage.messageHeaderInfo.mailboxID = syncMessageMailbox
					.getMailboxID();
			inputMessage.messageHeaderInfo.mailID = syncMessageMailbox
					.getMailID();

			ArrayDeque<WrapBuffer> inputMessageWrapBufferQueue = ClientMessageUtility
					.buildReadableWrapBufferList(messageCodecManger,
							messageProtocol, inputMessage);

			addInputMessage(inputMessage, inputMessageWrapBufferQueue);

			ReadableMiddleObjectWrapper outputMessageWrapReadableMiddleObject = syncMessageMailbox
					.getSyncOutputMessage();

			AbstractMessage outputMessage = ClientMessageUtility
					.buildOutputMessage(messageCodecManger, messageProtocol,
							outputMessageWrapReadableMiddleObject);

			return outputMessage;
		} finally {
			syncMessageMailboxQueue.offer(syncMessageMailbox);
		}
	}

	@Override
	public void sendAsynInputMessage(MessageCodecMangerIF messageCodecManger,
			AbstractMessage inputMessage) throws InterruptedException,
			NotSupportedException, IOException,
			NoMoreDataPacketBufferException, DynamicClassCallException,
			BodyFormatException {
		inputMessage.messageHeaderInfo.mailboxID = AsynMessageMailbox
				.getMailboxID();
		inputMessage.messageHeaderInfo.mailID = AsynMessageMailbox
				.getNextMailID();

		ArrayDeque<WrapBuffer> inputMessageWrapBufferQueue = ClientMessageUtility
				.buildReadableWrapBufferList(messageCodecManger,
						messageProtocol, inputMessage);

		addInputMessage(inputMessage, inputMessageWrapBufferQueue);
	}

	@Override
	public void close() {
		log.info("socket[{}] closed", clientSC.hashCode());

		try {
			clientSC.close();
		} catch (IOException e) {
			log.warn("fail to close the socket channel[{}], errmsg={}",
					clientSC.hashCode(), e.getMessage());
		}

		asynClientIOEventController.cancel(personalSelectionKey);

		releaseResources();

	}

	private void releaseResources() {
		synchronized (readMonitor) {
			socketOutputStream.close();
		}

		synchronized (writeMonitor) {
			if (!inputMessageQueue.isEmpty()) {
				log.info("the var inputMessageStreamQueue is not a empty");

				while (!inputMessageQueue.isEmpty()) {
					ArrayDeque<WrapBuffer> inputMessageWrapBufferQueue = inputMessageQueue
							.poll();

					while (!inputMessageWrapBufferQueue.isEmpty()) {
						WrapBuffer wrapBuffer = inputMessageWrapBufferQueue
								.removeFirst();
						dataPacketBufferPool.putDataPacketBuffer(wrapBuffer);
					}
				}
			}
		}

		log.info("this connection[{}]'s resources has been released",
				clientSC.hashCode());
	}

	@Override
	public SelectionKey register(Selector ioEventSelector, int wantedInterestOps)
			throws Exception {
		SelectionKey registeredSelectionKey = clientSC.register(
				ioEventSelector, wantedInterestOps);
		return registeredSelectionKey;
	}

	@Override
	public boolean doConnect() throws Exception {
		SocketAddress serverAddress = new InetSocketAddress(serverHost,
				serverPort);
		boolean isSuceess = clientSC.connect(serverAddress);

		if (isSuceess) {
			isSuceess = clientSC.finishConnect();
		}

		return isSuceess;
	}

	public void doFinishConnect(SelectionKey selectedKey) {
		personalSelectionKey = selectedKey;
		asynConnectedConnectionAdder.addConnectedConnection(this);
	}

	public void doSubtractOneFromNumberOfUnregisteredConnections() {
		asynConnectedConnectionAdder
				.subtractOneFromNumberOfUnregisteredConnections(this);
	}

	@Override
	public void onConnect(SelectionKey selectedKey) throws Exception {
		boolean isSuccess = clientSC.finishConnect();

		if (!isSuccess) {
			String errorMessage = new StringBuilder()
					.append("fail to finish connection[").append(hashCode())
					.append("] becase the returned value is false").toString();
			throw new IOException(errorMessage);
		}

		selectedKey.interestOps(SelectionKey.OP_READ);

		doFinishConnect(selectedKey);		
	}

	@Override
	public void putReceivedMessage(
			ReadableMiddleObjectWrapper readableMiddleObjectWrapper)
			throws InterruptedException {
		int mailboxID = readableMiddleObjectWrapper.getMailboxID();
		if (CommonStaticFinalVars.ASYN_MAILBOX_ID == mailboxID) {
			try {
				String messageID = readableMiddleObjectWrapper.getMessageID();

				AbstractClientTask clientTask = null;
				try {
					clientTask = clientTaskManger.getClientTask(messageID);
				} catch (DynamicClassCallException e) {
					log.warn(e.getMessage());
					return;
				} catch (Exception | Error e) {
					log.warn("unknwon error", e);
					return;
				}

				try {
					clientTask.execute(hashCode(), projectName, this,
							readableMiddleObjectWrapper, messageProtocol);
				} catch (InterruptedException e) {
					throw e;
				} catch (Exception | Error e) {
					log.warn(
							"unknwon error::fail to execute a output message client task",
							e);
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
				log.warn(
						"fail to match a mailbox of the received message[{}], errmsg={}",
						readableMiddleObjectWrapper.toSimpleInformation(),
						e.getMessage());
				return;
			}
			syncMessageMailbox
					.putSyncOutputMessage(readableMiddleObjectWrapper);
		}
	}

	@Override
	public void onRead(SelectionKey selectedKey) throws Exception {
		synchronized (readMonitor) {
			int numberOfReadBytes = socketOutputStream.read(clientSC);

			if (-1 == numberOfReadBytes) {
				String errorMessage = new StringBuilder("this socket channel[")
						.append(clientSC.hashCode())
						.append("] has reached end-of-stream").toString();

				log.warn(errorMessage);
				close();
				return;
			}

			setFinalReadTime();
			messageProtocol.S2MList(socketOutputStream, this);
		}
	}

	private void addInputMessage(AbstractMessage inputMessage,
			ArrayDeque<WrapBuffer> inputMessageWrapBufferQueue)
			throws InterruptedException, IOException {

		// long startTime = System.nanoTime();
		synchronized (writeMonitor) {

			if (inputMessageQueue.size() >= clientAsynInputMessageQueueCapacity) {
				writeMonitor.wait(socketTimeout);

				if (inputMessageQueue.size() >= clientAsynInputMessageQueueCapacity) {
					String errorMessage = new StringBuilder()
							.append("fail to inserts the specified element[")
							.append(inputMessage.toString())
							.append("] into the input message queue while socket[")
							.append(hashCode()).append("] timeout").toString();

					log.warn(errorMessage);

					/** 입력 메시지 스트림을 담은 버퍼 리스트는 별도 해제 */
					while (!inputMessageWrapBufferQueue.isEmpty()) {
						WrapBuffer wrapBuffer = inputMessageWrapBufferQueue
								.removeFirst();
						dataPacketBufferPool.putDataPacketBuffer(wrapBuffer);
					}

					throw new ConnectionPoolTimeoutException(errorMessage);
				}
			}

			inputMessageQueue.add(inputMessageWrapBufferQueue);

			try {
				personalSelectionKey.interestOps(personalSelectionKey
						.interestOps() | SelectionKey.OP_WRITE);
				asynClientIOEventController.wakeup();
			} catch (CancelledKeyException e) {
				String errorMessage = new StringBuilder()
						.append("fail to set this selector[socket channel=")
						.append(clientSC.hashCode())
						.append("] key's interest set to the given value 'OP_WRITE' becase of CancelledKeyException")
						.toString();

				log.warn(errorMessage);
				close();
				throw new IOException(errorMessage);
			} catch (Exception e) {
				String errorMessage = new StringBuilder()
						.append("fail to set this selector[socket channel=")
						.append(clientSC.hashCode())
						.append("] key's interest set to the given value 'OP_WRITE' becase of unknown error")
						.toString();

				log.warn(errorMessage, e);
				close();
				throw new IOException(errorMessage);
			}
		}

		/*
		 * long endTime = System.nanoTime();
		 * log.info("addInputMessage elasped {} microseconds",
		 * TimeUnit.MICROSECONDS.convert((endTime - startTime),
		 * TimeUnit.NANOSECONDS));
		 */
	}

	@Override
	public void onWrite(SelectionKey selectedKey) throws Exception {

		synchronized (writeMonitor) {
			ArrayDeque<WrapBuffer> inputMessageWrapBufferQueue = inputMessageQueue
					.peek();
			if (null == inputMessageWrapBufferQueue) {
				return;
			}

			WrapBuffer currentWorkingWrapBuffer = inputMessageWrapBufferQueue
					.peek();
			ByteBuffer currentWorkingByteBuffer = currentWorkingWrapBuffer
					.getByteBuffer();

			int numberOfBytesWritten = clientSC.write(currentWorkingByteBuffer);

			if (numberOfBytesWritten > 0) {
				if (!currentWorkingByteBuffer.hasRemaining()) {
					inputMessageWrapBufferQueue.removeFirst();
					dataPacketBufferPool
							.putDataPacketBuffer(currentWorkingWrapBuffer);

					if (inputMessageWrapBufferQueue.isEmpty()) {
						try {
							inputMessageQueue.poll();
							if (inputMessageQueue.isEmpty()) {
								selectedKey
										.interestOps(selectedKey.interestOps()
												& ~SelectionKey.OP_WRITE);
							}
						} finally {
							writeMonitor.notify();
						}
						return;
					}
				}
			}
		}
	}

	public boolean isConnected() {
		return clientSC.isConnected();
	}

	public int hashCode() {
		return clientSC.hashCode();
	}

	@Override
	protected void finalize() {
		close();
	}
}
