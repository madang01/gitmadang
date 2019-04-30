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
import kr.pe.codda.common.classloader.MessageCodecMangerIF;
import kr.pe.codda.common.etc.CommonStaticFinalVars;
import kr.pe.codda.common.exception.BodyFormatException;
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

public final class SyncThreadSafeSingleConnection implements SyncConnectionIF {
	private InternalLogger log = InternalLoggerFactory.getInstance(SyncThreadSafeSingleConnection.class);

	private ReceivedDataStream receivedDataOnlyStream = null;
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
	private SyncOutputMessageReceiver syncOutputMessageReceiver = null;

	public SyncThreadSafeSingleConnection(String serverHost, int serverPort, long socketTimeout,
			int clientDataPacketBufferSize, ReceivedDataStream receivedDataOnlyStream, MessageProtocolIF messageProtocol,
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
		syncOutputMessageReceiver = new SyncOutputMessageReceiver(messageProtocol);

		openSocketChannel();

		connectAndBuildIOStream();
	}

	private void connectAndBuildIOStream() throws IOException {
		SocketAddress serverAddress = new InetSocketAddress(serverHost, serverPort);

		clientSocket = clientSC.socket();
		try {
			clientSocket.setSoTimeout((int) socketTimeout);
			clientSocket.connect(serverAddress, (int) socketTimeout);
			clientInputStream = clientSocket.getInputStream();
			clientOutputStream = clientSocket.getOutputStream();
		} catch (IOException e) {
			clientSC.close();

			String errorMessage = new StringBuilder().append("an io error occurred when connecting to the server[host=")
					.append(serverHost).append(", serverPort=").append(serverPort)
					.append("] or building the I / O stream, errmsg=").append(e.getMessage()).toString();
			log.warn(errorMessage, e);

			throw e;
		} catch (Exception e) {

			clientSC.close();

			String errorMessage = new StringBuilder()
					.append("an unknown error occurred when connecting to the server[host=").append(serverHost)
					.append(", serverPort=").append(serverPort).append("] or building the I / O stream, errmsg=")
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

	private void setFinalReadTime() {
		finalReadTime = new java.util.Date();
	}

	private void releaseResources() {
		receivedDataOnlyStream.close();

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
			clientSC.close();
		} catch (Exception e) {
			String errorMessage = new StringBuilder().append("fail to close the socket channel[")
					.append(clientSC.hashCode()).append("], errmsg=").append(e.getMessage()).toString();

			log.warn(errorMessage);
		}

		log.info("this connection[{}]'s resources has been released", clientSC.hashCode());
	}

	@Override
	synchronized public AbstractMessage sendSyncInputMessage(MessageCodecMangerIF messageCodecManger, AbstractMessage inputMessage)
			throws InterruptedException, IOException, NoMoreDataPacketBufferException, DynamicClassCallException,
			BodyFormatException, ServerTaskException, ServerTaskPermissionException {

		// ClassLoader classloaderOfInputMessage = inputMessage.getClass().getClassLoader();

		

		if (Integer.MAX_VALUE == mailID) {
			mailID = Integer.MIN_VALUE;
		} else {
			mailID++;
		}

		inputMessage.messageHeaderInfo.mailboxID = mailboxID;
		inputMessage.messageHeaderInfo.mailID = mailID;

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

		while (! wrapBufferList.isEmpty()) {
			WrapBuffer inputMessageWrapBuffer = wrapBufferList.pollFirst();
			try {
				ByteBuffer inputMessageByteBuffer = inputMessageWrapBuffer.getByteBuffer();
				int len = inputMessageByteBuffer.remaining();
				inputMessageByteBuffer.get(socketBuffer, 0, len);
				clientOutputStream.write(socketBuffer, 0, len);

				dataPacketBufferPool.putDataPacketBuffer(inputMessageWrapBuffer);
			} catch (IOException e) {
				dataPacketBufferPool.putDataPacketBuffer(inputMessageWrapBuffer);
				while (! wrapBufferList.isEmpty()) {
					inputMessageWrapBuffer = wrapBufferList.pollFirst();
					dataPacketBufferPool.putDataPacketBuffer(inputMessageWrapBuffer);
				}

				String errorMessage = new StringBuilder().append("fail to write a sequence of bytes to this channel[")
						.append(clientSC.hashCode()).append("] because io error occured, errmsg=")
						.append(e.getMessage()).toString();
				log.warn(errorMessage, e);

				close();
			} catch (Exception e) {
				dataPacketBufferPool.putDataPacketBuffer(inputMessageWrapBuffer);
				while (! wrapBufferList.isEmpty()) {
					inputMessageWrapBuffer = wrapBufferList.pollFirst();
					dataPacketBufferPool.putDataPacketBuffer(inputMessageWrapBuffer);
				}

				String errorMessage = new StringBuilder().append("fail to write a sequence of bytes to this channel[")
						.append(clientSC.hashCode()).append("] because unknown error occured, errmsg=")
						.append(e.getMessage()).toString();
				log.warn(errorMessage, e);
				close();
			}
		}

		syncOutputMessageReceiver.ready(messageCodecManger);
		try {
			do {
				int numberOfReadBytes = 0;

				numberOfReadBytes = receivedDataOnlyStream.read(clientInputStream, socketBuffer);

				if (numberOfReadBytes == -1) {
					String errorMessage = new StringBuilder("this socket channel[").append(clientSC.hashCode())
							.append("] has reached end-of-stream").toString();

					log.warn(errorMessage);
					close();
					throw new IOException(errorMessage);
				}

				setFinalReadTime();

				messageProtocol.S2MList(receivedDataOnlyStream, syncOutputMessageReceiver);

				// log.info("numberOfReadBytes={}, readableMiddleObjectWrapperQueue.isEmpty={}",
				// numberOfReadBytes, readableMiddleObjectWrapperQueue.isEmpty());
			} while (! syncOutputMessageReceiver.isReceivedMessage());

		} catch (NoMoreDataPacketBufferException e) {
			String errorMessage = new StringBuilder()
					.append("the no more data packet buffer error occurred while reading the socket[")
					.append(clientSC.hashCode()).append("], errmsg=").append(e.getMessage()).toString();
			log.warn(errorMessage, e);
			close();
			throw e;
		} catch (IOException e) {
			String errorMessage = new StringBuilder().append("the io error occurred while reading the socket[")
					.append(clientSC.hashCode()).append("], errmsg=").append(e.getMessage()).toString();
			log.warn(errorMessage, e);

			close();
			throw e;
		} catch (Exception e) {
			String errorMessage = new StringBuilder().append("the unknown error occurred while reading the socket[")
					.append(clientSC.hashCode()).append("], errmsg=").append(e.getMessage()).toString();
			log.warn(errorMessage, e);

			close();
			throw new IOException(errorMessage);
		} finally {
			Arrays.fill(socketBuffer, CommonStaticFinalVars.ZERO_BYTE);
		}

		if (syncOutputMessageReceiver.isError()) {
			String errorMessage = "there are one or more recevied messages";
			close();
			throw new IOException(errorMessage);
		}

		AbstractMessage outputMessage = syncOutputMessageReceiver.getReceiveMessage();

		return outputMessage;
	}

	@Override
	public void sendAsynInputMessage(MessageCodecMangerIF messageCodecManger, AbstractMessage inputMessage) throws InterruptedException, NotSupportedException,
			IOException, NoMoreDataPacketBufferException, DynamicClassCallException, BodyFormatException {
		throw new NotSupportedException(
				"this connection doesn't support this method because it is a blocking mode connection and no sharing connection between threads");
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

	@Override
	public boolean isConnected() {
		return clientSocket.isConnected();
	}

	public java.util.Date getFinalReadTime() {
		return finalReadTime;
	}
}
