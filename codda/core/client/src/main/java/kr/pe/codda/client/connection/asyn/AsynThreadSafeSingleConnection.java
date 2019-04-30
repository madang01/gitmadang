package kr.pe.codda.client.connection.asyn;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.net.StandardSocketOptions;
import java.nio.ByteBuffer;
import java.nio.channels.CancelledKeyException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.ArrayDeque;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.TimeUnit;

import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;
import kr.pe.codda.client.classloader.ClientTaskMangerIF;
import kr.pe.codda.client.connection.asyn.mainbox.AsynMessageMailbox;
import kr.pe.codda.client.connection.asyn.mainbox.SyncMessageMailbox;
import kr.pe.codda.client.task.AbstractClientTask;
import kr.pe.codda.common.classloader.MessageCodecMangerIF;
import kr.pe.codda.common.etc.CommonStaticFinalVars;
import kr.pe.codda.common.exception.BodyFormatException;
import kr.pe.codda.common.exception.ConnectionPoolTimeoutException;
import kr.pe.codda.common.exception.DynamicClassCallException;
import kr.pe.codda.common.exception.HeaderFormatException;
import kr.pe.codda.common.exception.NoMoreDataPacketBufferException;
import kr.pe.codda.common.exception.NotSupportedException;
import kr.pe.codda.common.exception.ServerTaskException;
import kr.pe.codda.common.exception.ServerTaskPermissionException;
import kr.pe.codda.common.io.DataPacketBufferPoolIF;
import kr.pe.codda.common.io.ReceivedDataStream;
import kr.pe.codda.common.io.WrapBuffer;
import kr.pe.codda.common.message.AbstractMessage;
import kr.pe.codda.common.message.codec.AbstractMessageEncoder;
import kr.pe.codda.common.protocol.MessageProtocolIF;
import kr.pe.codda.common.protocol.ProtocolUtil;
import kr.pe.codda.common.protocol.ReceivedMessageReceiverIF;

public class AsynThreadSafeSingleConnection
		implements AsynConnectionIF, ClientIOEventHandlerIF, ReceivedMessageReceiverIF {
	private InternalLogger log = InternalLoggerFactory.getInstance(AsynThreadSafeSingleConnection.class);

	private final Object writeMonitor = new Object();
	// private final Object readMonitor = new Object();
	private String projectName = null;
	private String serverHost = null;
	private int serverPort = 0;
	private long socketTimeout = 0;
	int syncMessageMailboxCountPerAsynShareConnection;
	int clientAsynInputMessageQueueCapacity;
	private ReceivedDataStream receivedDataOnlyStream = null;
	private MessageProtocolIF messageProtocol = null;
	private DataPacketBufferPoolIF dataPacketBufferPool = null;
	private ClientTaskMangerIF clientTaskManger = null;
	private AsynConnectedConnectionAdderIF asynConnectedConnectionAdder = null;
	private ClientIOEventControllerIF asynClientIOEventController = null;

	private SocketChannel clientSC = null;
	private SelectionKey personalSelectionKey = null;
	private java.util.Date finalReadTime = new java.util.Date();

	private SyncMessageMailbox[] syncMessageMailboxArray = null;
	private ArrayBlockingQueue<SyncMessageMailbox> syncMessageMailboxQueue = null;

	private ArrayDeque<ArrayDeque<WrapBuffer>> inputMessageQueue = new ArrayDeque<ArrayDeque<WrapBuffer>>();

	public AsynThreadSafeSingleConnection(String projectName, String serverHost, int serverPort, long socketTimeout,
			int syncMessageMailboxCountPerAsynShareConnection, int clientAsynInputMessageQueueCapacity,
			ReceivedDataStream receivedDataOnlyStream, MessageProtocolIF messageProtocol,
			DataPacketBufferPoolIF dataPacketBufferPool, ClientTaskMangerIF clientTaskManger,
			AsynConnectedConnectionAdderIF asynConnectedConnectionAdder,
			ClientIOEventControllerIF asynClientIOEventController) throws IOException {
		this.projectName = projectName;
		this.serverHost = serverHost;
		this.serverPort = serverPort;
		this.socketTimeout = socketTimeout;
		this.syncMessageMailboxCountPerAsynShareConnection = syncMessageMailboxCountPerAsynShareConnection;
		this.clientAsynInputMessageQueueCapacity = clientAsynInputMessageQueueCapacity;
		this.receivedDataOnlyStream = receivedDataOnlyStream;
		this.messageProtocol = messageProtocol;
		this.dataPacketBufferPool = dataPacketBufferPool;
		this.clientTaskManger = clientTaskManger;
		this.asynConnectedConnectionAdder = asynConnectedConnectionAdder;
		this.asynClientIOEventController = asynClientIOEventController;

		this.syncMessageMailboxQueue = new ArrayBlockingQueue<SyncMessageMailbox>(
				syncMessageMailboxCountPerAsynShareConnection);
		this.syncMessageMailboxArray = new SyncMessageMailbox[syncMessageMailboxCountPerAsynShareConnection + 1];
		for (int i = 1; i < syncMessageMailboxArray.length; i++) {
			SyncMessageMailbox syncMessageMailbox = new SyncMessageMailbox(this, i, socketTimeout, messageProtocol);
			syncMessageMailboxArray[i] = syncMessageMailbox;
			syncMessageMailboxQueue.offer(syncMessageMailbox);
		}

		// FIXME!
		log.info("syncMessageMailboxQueue.size={}", syncMessageMailboxQueue.size());

		openSocketChannel();
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
	public AbstractMessage sendSyncInputMessage(MessageCodecMangerIF messageCodecManger, AbstractMessage inputMessage)
			throws InterruptedException, IOException, NoMoreDataPacketBufferException, DynamicClassCallException,
			BodyFormatException, ServerTaskException, ServerTaskPermissionException {

		// synchronized (clientSC) {

		SyncMessageMailbox syncMessageMailbox = syncMessageMailboxQueue.poll(socketTimeout, TimeUnit.MILLISECONDS);
		if (null == syncMessageMailbox) {
			/** timeout */
			String errorMessage = "fail to get a SyncMessageMailbox queue element because of timeout";

			throw new ConnectionPoolTimeoutException(errorMessage);
		}

		AbstractMessage outputMessage = null;

		try {
			syncMessageMailbox.setMessageCodecManger(messageCodecManger);
			inputMessage.messageHeaderInfo.mailboxID = syncMessageMailbox.getMailboxID();
			inputMessage.messageHeaderInfo.mailID = syncMessageMailbox.getMailID();

			AbstractMessageEncoder messageEncoder = null;

			try {
				messageEncoder = messageCodecManger.getMessageEncoder(inputMessage.getMessageID());
			} catch (DynamicClassCallException e) {
				throw e;
			} catch (Exception e) {
				String errorMessage = new StringBuilder("unkown error::fail to get a input message encoder::")
						.append(e.getMessage()).toString();
				log.warn(errorMessage, e);
				throw new DynamicClassCallException(errorMessage);
			}

			ArrayDeque<WrapBuffer> wrapBufferList = null;
			try {
				wrapBufferList = messageProtocol.M2S(inputMessage, messageEncoder);
			} catch (NoMoreDataPacketBufferException e) {
				throw e;
			} catch (BodyFormatException e) {
				throw e;
			} catch (HeaderFormatException e) {
				throw e;
			} catch (Exception e) {
				String errorMessage = new StringBuilder("unkown error::fail to get a input message encoder::")
						.append(e.getMessage()).toString();
				log.error(errorMessage, e);
				System.exit(1);
			}

			addInputMessage(inputMessage, wrapBufferList);

			outputMessage = syncMessageMailbox.getSyncOutputMessage();
		} finally {
			syncMessageMailboxQueue.offer(syncMessageMailbox);
		}

		// }

		return outputMessage;

	}

	@Override
	public void sendAsynInputMessage(MessageCodecMangerIF messageCodecManger, AbstractMessage inputMessage)
			throws InterruptedException, NotSupportedException, IOException, NoMoreDataPacketBufferException,
			DynamicClassCallException, BodyFormatException {
		inputMessage.messageHeaderInfo.mailboxID = AsynMessageMailbox.getMailboxID();
		inputMessage.messageHeaderInfo.mailID = AsynMessageMailbox.getNextMailID();

		AbstractMessageEncoder messageEncoder = null;

		try {
			messageEncoder = messageCodecManger.getMessageEncoder(inputMessage.getMessageID());
		} catch (DynamicClassCallException e) {
			throw e;
		} catch (Exception e) {
			String errorMessage = new StringBuilder("unkown error::fail to get a input message encoder::")
					.append(e.getMessage()).toString();
			log.warn(errorMessage, e);
			throw new DynamicClassCallException(errorMessage);
		}

		ArrayDeque<WrapBuffer> wrapBufferList = null;
		try {
			wrapBufferList = messageProtocol.M2S(inputMessage, messageEncoder);
		} catch (NoMoreDataPacketBufferException e) {
			throw e;
		} catch (BodyFormatException e) {
			throw e;
		} catch (HeaderFormatException e) {
			throw e;
		} catch (Exception e) {
			String errorMessage = new StringBuilder("unkown error::fail to get a input message encoder::")
					.append(e.getMessage()).toString();
			log.error(errorMessage, e);
			System.exit(1);
		}

		addInputMessage(inputMessage, wrapBufferList);
	}

	@Override
	public void close() {
		log.info("socket[{}] closed", clientSC.hashCode());

		try {
			clientSC.close();
		} catch (IOException e) {
			log.warn("fail to close the socket channel[{}], errmsg={}", clientSC.hashCode(), e.getMessage());
		}

		asynClientIOEventController.cancel(personalSelectionKey);

		releaseResources();

	}

	private void releaseResources() {
		// synchronized (readMonitor) {
		receivedDataOnlyStream.close();
		// }

		synchronized (writeMonitor) {
			if (!inputMessageQueue.isEmpty()) {
				log.info("the var inputMessageStreamQueue is not a empty");

				while (!inputMessageQueue.isEmpty()) {
					ArrayDeque<WrapBuffer> inputMessageWrapBufferQueue = inputMessageQueue.poll();

					while (!inputMessageWrapBufferQueue.isEmpty()) {
						WrapBuffer wrapBuffer = inputMessageWrapBufferQueue.removeFirst();
						dataPacketBufferPool.putDataPacketBuffer(wrapBuffer);
					}
				}
			}
		}

		log.info("this connection[{}]'s resources has been released", clientSC.hashCode());
	}

	@Override
	public SelectionKey register(Selector ioEventSelector, int wantedInterestOps) throws Exception {
		SelectionKey registeredSelectionKey = clientSC.register(ioEventSelector, wantedInterestOps);
		return registeredSelectionKey;
	}

	@Override
	public boolean doConnect() throws Exception {
		SocketAddress serverAddress = new InetSocketAddress(serverHost, serverPort);
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
		asynConnectedConnectionAdder.subtractOneFromNumberOfUnregisteredConnections(this);
	}

	@Override
	public void onConnect(SelectionKey selectedKey) throws Exception {
		boolean isSuccess = clientSC.finishConnect();

		if (!isSuccess) {
			String errorMessage = new StringBuilder().append("fail to finish connection[").append(hashCode())
					.append("] becase the returned value is false").toString();
			throw new IOException(errorMessage);
		}

		selectedKey.interestOps(SelectionKey.OP_READ);

		doFinishConnect(selectedKey);
	}

	@Override
	public void putReceivedMessage(int mailboxID, int mailID, String messageID, Object readableMiddleObject)
			throws InterruptedException {
		if (CommonStaticFinalVars.ASYN_MAILBOX_ID == mailboxID) {
			try {
				AbstractClientTask clientTask = clientTaskManger.getClientTask(messageID);
				clientTask.execute(hashCode(), projectName, this, mailboxID, mailID, messageID, readableMiddleObject,
						messageProtocol);
			} catch (InterruptedException e) {
				throw e;
			} catch (Exception | Error e) {
				log.warn("unknwon error::fail to execute a output message client task", e);
				return;
			} finally {
				// readableMiddleObjectWrapper.closeReadableMiddleObject();
				ProtocolUtil.closeReadableMiddleObject(mailboxID, mailID, messageID, readableMiddleObject);
			}
		} else {
			if (mailboxID <= 0 || mailboxID > syncMessageMailboxArray.length) {
				String errorMessage = new StringBuilder("The synchronous output message[").append("mailboxID=")
						.append(mailboxID).append(", mailID=").append(mailID).append(", messageID=").append(messageID)
						.append("] was discarded because the paramter mailboxID is not valid").toString();

				log.warn(errorMessage);
				
				ProtocolUtil.closeReadableMiddleObject(mailboxID, mailID, messageID, readableMiddleObject);

				return;
			}

			syncMessageMailboxArray[mailboxID].putSyncOutputMessage(mailboxID, mailID, messageID, readableMiddleObject);
		}
	}

	@Override
	public void onRead(SelectionKey selectedKey) throws Exception {
		// synchronized (readMonitor) {
		int numberOfReadBytes = receivedDataOnlyStream.read(clientSC);

		if (-1 == numberOfReadBytes) {
			String errorMessage = new StringBuilder("this socket channel[").append(clientSC.hashCode())
					.append("] has reached end-of-stream").toString();

			log.warn(errorMessage);
			close();
			return;
		}

		setFinalReadTime();
		messageProtocol.S2MList(receivedDataOnlyStream, this);
		// }
	}

	private void addInputMessage(AbstractMessage inputMessage, ArrayDeque<WrapBuffer> inputMessageWrapBufferQueue)
			throws InterruptedException, IOException {

		// long startTime = System.nanoTime();
		synchronized (writeMonitor) {

			if (!clientSC.isConnected()) {
				throw new IOException("this connection was closed");
			}

			if (inputMessageQueue.size() >= clientAsynInputMessageQueueCapacity) {
				writeMonitor.wait(socketTimeout);

				if (inputMessageQueue.size() >= clientAsynInputMessageQueueCapacity) {
					String errorMessage = new StringBuilder().append("fail to inserts the specified element[")
							.append(inputMessage.toString()).append("] into the input message queue of connection[")
							.append(hashCode()).append("] timeout").toString();

					log.warn(errorMessage);

					/** 입력 메시지 스트림을 담은 버퍼 리스트는 별도 해제 */
					while (!inputMessageWrapBufferQueue.isEmpty()) {
						WrapBuffer wrapBuffer = inputMessageWrapBufferQueue.removeFirst();
						dataPacketBufferPool.putDataPacketBuffer(wrapBuffer);
					}

					throw new ConnectionPoolTimeoutException(errorMessage);
				}
			}

			inputMessageQueue.add(inputMessageWrapBufferQueue);

			try {
				personalSelectionKey.interestOps(personalSelectionKey.interestOps() | SelectionKey.OP_WRITE);
				asynClientIOEventController.wakeup();
			} catch (CancelledKeyException e) {
				String errorMessage = new StringBuilder().append("fail to set this selector[socket channel=")
						.append(clientSC.hashCode())
						.append("] key's interest set to the given value 'OP_WRITE' becase of CancelledKeyException")
						.toString();

				log.warn(errorMessage);
				close();
				throw new IOException(errorMessage);
			} catch (Exception e) {
				String errorMessage = new StringBuilder().append("fail to set this selector[socket channel=")
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
		 * TimeUnit.MICROSECONDS.convert((endTime - startTime), TimeUnit.NANOSECONDS));
		 */
	}

	@Override
	public void onWrite(SelectionKey selectedKey) throws Exception {

		synchronized (writeMonitor) {
			ArrayDeque<WrapBuffer> inputMessageWrapBufferQueue = inputMessageQueue.peek();
			if (null == inputMessageWrapBufferQueue) {
				return;
			}

			WrapBuffer currentWorkingWrapBuffer = inputMessageWrapBufferQueue.peek();
			ByteBuffer currentWorkingByteBuffer = currentWorkingWrapBuffer.getByteBuffer();

			int numberOfBytesWritten = clientSC.write(currentWorkingByteBuffer);

			if (numberOfBytesWritten > 0) {
				if (!currentWorkingByteBuffer.hasRemaining()) {
					inputMessageWrapBufferQueue.removeFirst();
					dataPacketBufferPool.putDataPacketBuffer(currentWorkingWrapBuffer);

					if (inputMessageWrapBufferQueue.isEmpty()) {
						try {
							inputMessageQueue.poll();
							if (inputMessageQueue.isEmpty()) {
								selectedKey.interestOps(selectedKey.interestOps() & ~SelectionKey.OP_WRITE);
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
