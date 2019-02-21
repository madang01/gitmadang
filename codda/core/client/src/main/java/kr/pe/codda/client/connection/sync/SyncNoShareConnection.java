package kr.pe.codda.client.connection.sync;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.StandardSocketOptions;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.ArrayDeque;
import java.util.Arrays;

import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;
import kr.pe.codda.client.connection.ClientMessageUtility;
import kr.pe.codda.common.classloader.MessageCodecMangerIF;
import kr.pe.codda.common.etc.CommonStaticFinalVars;
import kr.pe.codda.common.exception.BodyFormatException;
import kr.pe.codda.common.exception.DynamicClassCallException;
import kr.pe.codda.common.exception.NoMoreDataPacketBufferException;
import kr.pe.codda.common.exception.NotSupportedException;
import kr.pe.codda.common.exception.ServerTaskException;
import kr.pe.codda.common.exception.ServerTaskPermissionException;
import kr.pe.codda.common.io.DataPacketBufferPoolIF;
import kr.pe.codda.common.io.ReceivedDataOnlyStream;
import kr.pe.codda.common.io.WrapBuffer;
import kr.pe.codda.common.message.AbstractMessage;
import kr.pe.codda.common.protocol.MessageProtocolIF;

/**
 * Note that this implementation is not synchronized.
 * 
 * @author Won jonghoon
 *
 */
public final class SyncNoShareConnection implements SyncConnectionIF {
	private InternalLogger log = InternalLoggerFactory.getInstance(SyncNoShareConnection.class);

	private ReceivedDataOnlyStream receivedDataOnlyStream = null;
	private MessageProtocolIF messageProtocol = null;
	private DataPacketBufferPoolIF dataPacketBufferPool = null;

	private String serverHost = null;
	private int serverPort = 0;
	private long socketTimeout = 0;
	private int clientDataPacketBufferSize = 0;
	private byte[] socketBuffer = null;

	private SocketChannel clientSC = null;
	private Socket clientSocket = null;
	private InputStream clientInputStream = null;
	private OutputStream clientOutputStream = null;
	private final int mailboxID = 1;
	private transient int mailID = Integer.MIN_VALUE;
	private transient java.util.Date finalReadTime = new java.util.Date();
	private boolean isQueueIn = true;
	private SyncReceivedMessageBlockingQueue syncReceivedMessageBlockingQueue = new SyncReceivedMessageBlockingQueue();

	public SyncNoShareConnection(String serverHost, int serverPort, long socketTimeout, int clientDataPacketBufferSize,
			ReceivedDataOnlyStream receivedDataOnlyStream, MessageProtocolIF messageProtocol,
			DataPacketBufferPoolIF dataPacketBufferPool)
			throws IOException {
		this.serverHost = serverHost;
		this.serverPort = serverPort;
		this.socketTimeout = socketTimeout;
		this.clientDataPacketBufferSize = clientDataPacketBufferSize;
		this.receivedDataOnlyStream = receivedDataOnlyStream;
		this.messageProtocol = messageProtocol;
		this.dataPacketBufferPool = dataPacketBufferPool;

		socketBuffer = new byte[this.clientDataPacketBufferSize];

		openSocketChannel();

		connectAndBuildIOStream();
	}

	private void connectAndBuildIOStream() throws IOException {
		SocketAddress serverAddress = new InetSocketAddress(serverHost, serverPort);
		// clientSC.connect(serverAddress);

		clientSocket = clientSC.socket();
		try {
			clientSocket.setSoTimeout((int) socketTimeout);
			clientSocket.connect(serverAddress, (int) socketTimeout);
			clientInputStream = clientSocket.getInputStream();
			clientOutputStream = clientSocket.getOutputStream();
		} catch (IOException e) {
			close();

			String errorMessage = new StringBuilder()
					.append("an io error occurred when connecting to the server or building the I / O stream, errmsg=")
					.append(e.getMessage()).toString();
			log.warn(errorMessage, e);

			throw e;
		} catch (Exception e) {
			close();

			String errorMessage = new StringBuilder().append(
					"an unknown error occurred when connecting to the server or building the I / O stream, errmsg=")
					.append(e.getMessage()).toString();
			log.warn(errorMessage, e);

			throw new IOException();
		}
	}

	private void openSocketChannel() throws IOException {
		clientSC = SocketChannel.open();
		clientSC.configureBlocking(true);
		clientSC.setOption(StandardSocketOptions.SO_KEEPALIVE, true);
		clientSC.setOption(StandardSocketOptions.TCP_NODELAY, true);
		clientSC.setOption(StandardSocketOptions.SO_LINGER, 0);
		clientSC.setOption(StandardSocketOptions.SO_REUSEADDR, true);
	}

	@Override
	public AbstractMessage sendSyncInputMessage(MessageCodecMangerIF messageCodecManger, AbstractMessage inputMessage)
			throws InterruptedException, IOException, NoMoreDataPacketBufferException, DynamicClassCallException,
			BodyFormatException, ServerTaskException, ServerTaskPermissionException {

		// ClassLoader inputMessageClassLoader =
		// inputMessage.getClass().getClassLoader();

		if (Integer.MAX_VALUE == mailID) {
			mailID = Integer.MIN_VALUE;
		} else {
			mailID++;
		}

		inputMessage.messageHeaderInfo.mailboxID = mailboxID;
		inputMessage.messageHeaderInfo.mailID = mailID;

		ArrayDeque<WrapBuffer> inputMessageWrapBufferQueue = ClientMessageUtility
				.buildReadableWrapBufferList(messageCodecManger, messageProtocol, inputMessage);

		while (!inputMessageWrapBufferQueue.isEmpty()) {
			WrapBuffer inputMessageWrapBuffer = inputMessageWrapBufferQueue.pollFirst();
			try {
				ByteBuffer inputMessageByteBuffer = inputMessageWrapBuffer.getByteBuffer();
				int len = inputMessageByteBuffer.remaining();
				inputMessageByteBuffer.get(socketBuffer, 0, len);
				clientOutputStream.write(socketBuffer, 0, len);

				dataPacketBufferPool.putDataPacketBuffer(inputMessageWrapBuffer);
			} catch (IOException e) {
				dataPacketBufferPool.putDataPacketBuffer(inputMessageWrapBuffer);
				while (!inputMessageWrapBufferQueue.isEmpty()) {
					inputMessageWrapBuffer = inputMessageWrapBufferQueue.pollFirst();
					dataPacketBufferPool.putDataPacketBuffer(inputMessageWrapBuffer);
				}

				String errorMessage = new StringBuilder().append("the io error occurred while writing a input message[")
						.append(inputMessage.toString())
						.append("] to the socket[")
						.append(clientSC.hashCode()).append("], errmsg=")
						.append(e.getMessage()).toString();
				close();
				throw new IOException(errorMessage);
			} catch (Exception e) {
				dataPacketBufferPool.putDataPacketBuffer(inputMessageWrapBuffer);
				while (!inputMessageWrapBufferQueue.isEmpty()) {
					inputMessageWrapBuffer = inputMessageWrapBufferQueue.pollFirst();
					dataPacketBufferPool.putDataPacketBuffer(inputMessageWrapBuffer);
				}
				
				String errorMessage = new StringBuilder().append("the unknown error occurred while writing a input message[")
						.append(inputMessage.toString())
						.append("] to the socket[")
						.append(clientSC.hashCode()).append("], errmsg=")
						.append(e.getMessage()).toString();
				log.warn(errorMessage, e);
				close();
				throw new IOException(errorMessage);
			}
		}

		syncReceivedMessageBlockingQueue.reset();

		try {
			do {
				int numberOfReadBytes = receivedDataOnlyStream.read(clientInputStream, socketBuffer);

				if (numberOfReadBytes == -1) {
					String errorMessage = new StringBuilder("this socket channel[").append(clientSC.hashCode())
							.append("] has reached end-of-stream").toString();

					log.warn(errorMessage);
					close();
					throw new IOException(errorMessage);
				}

				setFinalReadTime();

				messageProtocol.S2MList(receivedDataOnlyStream, syncReceivedMessageBlockingQueue);

				// log.info("numberOfReadBytes={}, readableMiddleObjectWrapperQueue.isEmpty={}",
				// numberOfReadBytes, readableMiddleObjectWrapperQueue.isEmpty());
			} while (!syncReceivedMessageBlockingQueue.isReceivedMessage());

		} catch (NoMoreDataPacketBufferException e) {
			String errorMessage = new StringBuilder()
					.append("the no more data packet buffer error occurred while reading the socket[")
					.append(clientSC.hashCode()).append("], errmsg=").append(e.getMessage()).toString();
			close();

			throw new NoMoreDataPacketBufferException(errorMessage);
		} catch (IOException e) {
			String errorMessage = new StringBuilder().append("the io error occurred while reading the socket[")
					.append(clientSC.hashCode()).append("], errmsg=").append(e.getMessage()).toString();			
			close();
			throw new IOException(errorMessage);
		} catch (Exception e) {
			String errorMessage = new StringBuilder().append("the unknown error occurred while reading the socket[")
					.append(clientSC.hashCode()).append("], errmsg=").append(e.getMessage()).toString();
			log.warn(errorMessage, e);
			close();
			throw new IOException(errorMessage);
		} finally {
			Arrays.fill(socketBuffer, CommonStaticFinalVars.ZERO_BYTE);
		}

		AbstractMessage outputMessage = ClientMessageUtility.buildOutputMessage(messageCodecManger, messageProtocol,
				syncReceivedMessageBlockingQueue.getReadableMiddleObjectWrapper());

		return outputMessage;
	}

	@Override
	public void sendAsynInputMessage(MessageCodecMangerIF messageCodecManger, AbstractMessage inputMessage)
			throws InterruptedException, NotSupportedException, IOException, NoMoreDataPacketBufferException,
			DynamicClassCallException, BodyFormatException {
		throw new NotSupportedException(
				"this connection doesn't support this method because it is a blocking mode connection and no sharing connection between threads");
	}

	@Override
	public void close() {
		if (null != clientInputStream) {
			try {
				clientInputStream.close();
			} catch (Exception e) {
				String errorMessage = new StringBuilder().append("fail to close the input stream of socket channel[")
						.append(clientSC.hashCode()).append("], errmsg=").append(e.getMessage()).toString();
				log.warn(errorMessage);
			}
		}

		if (null != clientOutputStream) {
			try {
				clientOutputStream.close();
			} catch (Exception e) {
				String errorMessage = new StringBuilder().append("fail to close the output stream of socket channel[")
						.append(clientSC.hashCode()).append("], errmsg=").append(e.getMessage()).toString();
				log.warn(errorMessage);
			}
		}

		try {
			clientSocket.close();
		} catch (Exception e) {
			String errorMessage = new StringBuilder().append("fail to close the socket channel[")
					.append(clientSC.hashCode()).append("], errmsg=").append(e.getMessage()).toString();
			log.warn(errorMessage);
		}

		releaseResources();
	}

	@Override
	public boolean isConnected() {
		return clientSocket.isConnected();
	}

	/**
	 * 큐 속에 들어갈때 상태 변경 메소드
	 */
	protected void queueIn() {
		isQueueIn = true;
	}

	/**
	 * 큐 밖으로 나갈때 상태 변경 메소드
	 */
	protected void queueOut() {
		isQueueIn = false;
	}

	public boolean isInQueue() {
		return isQueueIn;
	}

	private void releaseResources() {
		if (!receivedDataOnlyStream.isClosed()) {
			receivedDataOnlyStream.close();

			log.info("this connection[{}]'s resources has been released", clientSC.hashCode());
		}
	}

	private void setFinalReadTime() {
		finalReadTime = new java.util.Date();
	}

	public java.util.Date getFinalReadTime() {
		return finalReadTime;
	}

	@Override
	protected void finalize() {
		log.info("the SyncNoShareConnection[{}] instance call the 'finalize' method", clientSC.hashCode());
		close();
	}
}
