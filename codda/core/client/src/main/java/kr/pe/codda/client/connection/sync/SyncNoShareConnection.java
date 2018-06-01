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
import kr.pe.codda.client.connection.ClientMessageUtilityIF;
import kr.pe.codda.common.etc.CommonStaticFinalVars;
import kr.pe.codda.common.exception.BodyFormatException;
import kr.pe.codda.common.exception.DynamicClassCallException;
import kr.pe.codda.common.exception.NoMoreDataPacketBufferException;
import kr.pe.codda.common.exception.NotSupportedException;
import kr.pe.codda.common.exception.ServerTaskException;
import kr.pe.codda.common.exception.ServerTaskPermissionException;
import kr.pe.codda.common.io.SocketOutputStream;
import kr.pe.codda.common.io.WrapBuffer;
import kr.pe.codda.common.message.AbstractMessage;

/**
 * Note that this implementation is not synchronized.
 * 
 * @author Won jonghoon
 *
 */
public final class SyncNoShareConnection implements SyncConnectionIF {
	private InternalLogger log = InternalLoggerFactory.getInstance(SyncNoShareConnection.class);
	
	private SocketOutputStream socketOutputStream = null;
	private ClientMessageUtilityIF clientMessageUtility = null;
	
	private String serverHost = null;
	private int serverPort  = 0;
	private long socketTimeout=0;
	private int clientDataPacketBufferSize=0;
	
	private SocketChannel clientSC = null;
	private Socket clientSocket = null;
	private InputStream clientInputStream = null;
	private OutputStream clientOutputStream = null;	
	private final int mailboxID = 1;	
	private transient int mailID = Integer.MIN_VALUE;
	private transient java.util.Date finalReadTime = new java.util.Date();
	private boolean isQueueIn = true;
	
	public SyncNoShareConnection(String serverHost, 
			int serverPort,
			long socketTimeout,  
			int clientDataPacketBufferSize,
			SocketOutputStream socketOutputStream,
			ClientMessageUtilityIF clientMessageUtility) throws IOException {		
		this.serverHost = serverHost;
		this.serverPort = serverPort;
		this.socketTimeout = socketTimeout;
		this.clientDataPacketBufferSize = clientDataPacketBufferSize;		
		this.socketOutputStream = socketOutputStream;
		this.clientMessageUtility = clientMessageUtility;		
		
		openSocketChannel();
		
		connectAndBuildIOStream();
	}

	private void connectAndBuildIOStream() throws IOException {
		SocketAddress serverAddress = new InetSocketAddress(serverHost, serverPort);
		// clientSC.connect(serverAddress);
		
		clientSocket = clientSC.socket();
		try {
			clientSocket.setSoTimeout((int)socketTimeout);
			clientSocket.connect(serverAddress, (int)socketTimeout);
			clientInputStream = clientSocket.getInputStream();
			clientOutputStream = clientSocket.getOutputStream();
		} catch(IOException e) {
			close();
			
			String errorMessage = new StringBuilder()
					.append("an io error occurred when connecting to the server or building the I / O stream, errmsg=")
					.append(e.getMessage()).toString();
			log.warn(errorMessage, e);
			
			throw e;
		} catch(Exception e) {
			close();
			
			String errorMessage = new StringBuilder()
					.append("an unknown error occurred when connecting to the server or building the I / O stream, errmsg=")
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
	public AbstractMessage sendSyncInputMessage(AbstractMessage inputMessage)
			throws InterruptedException, IOException, NoMoreDataPacketBufferException, DynamicClassCallException,
			BodyFormatException, ServerTaskException, ServerTaskPermissionException {		
		
		ClassLoader classloaderOfInputMessage = inputMessage.getClass().getClassLoader();
		
		byte[] socketBuffer = new byte[clientDataPacketBufferSize];
		
		if (Integer.MAX_VALUE == mailID) {
			mailID = Integer.MIN_VALUE;
		} else {
			mailID++;
		}
		
		inputMessage.messageHeaderInfo.mailboxID = mailboxID;	
		inputMessage.messageHeaderInfo.mailID = mailID;
				
		
		ArrayDeque<WrapBuffer> inputMessageWrapBufferQueue = clientMessageUtility.buildReadableWrapBufferList(classloaderOfInputMessage, inputMessage);
		
		while (! inputMessageWrapBufferQueue.isEmpty()) {
			WrapBuffer inputMessageWrapBuffer = inputMessageWrapBufferQueue.pollFirst();
			try {
				ByteBuffer inputMessageByteBuffer = inputMessageWrapBuffer.getByteBuffer();
				int len = inputMessageByteBuffer.remaining();
				inputMessageByteBuffer.get(socketBuffer, 0, len);				
				clientOutputStream.write(socketBuffer, 0, len);
				
				clientMessageUtility.releaseWrapBuffer(inputMessageWrapBuffer);				
			} catch(IOException e) {
				clientMessageUtility.releaseWrapBuffer(inputMessageWrapBuffer);
				while (! inputMessageWrapBufferQueue.isEmpty()) {
					inputMessageWrapBuffer = inputMessageWrapBufferQueue.pollFirst();
					clientMessageUtility.releaseWrapBuffer(inputMessageWrapBuffer);
				}
				
				String errorMessage = new StringBuilder()
						.append("fail to write a sequence of bytes to this channel[")
						.append(clientSC.hashCode())
						.append("] because io error occured, errmsg=")
						.append(e.getMessage()).toString();
				log.warn(errorMessage, e);
				close();
			} catch(Exception e) {
				clientMessageUtility.releaseWrapBuffer(inputMessageWrapBuffer);
				while (! inputMessageWrapBufferQueue.isEmpty()) {
					inputMessageWrapBuffer = inputMessageWrapBufferQueue.pollFirst();
					clientMessageUtility.releaseWrapBuffer(inputMessageWrapBuffer);
				}
				
				String errorMessage = new StringBuilder()
						.append("fail to write a sequence of bytes to this channel[")
						.append(clientSC.hashCode())
						.append("] because unknown error occured, errmsg=")
						.append(e.getMessage()).toString();
				log.warn(errorMessage, e);
				close();
			}	
		}		
		
		SyncReceivedMessageBlockingQueue syncReceivedMessageBlockingQueue = new SyncReceivedMessageBlockingQueue();
		try {			
			do {
				int numberOfReadBytes = 0;			
				
				numberOfReadBytes = socketOutputStream.read(clientInputStream, socketBuffer);				
	
				if (numberOfReadBytes == -1) {
					String errorMessage = new StringBuilder("this socket channel[")
							.append(clientSC.hashCode())
							.append("] has reached end-of-stream").toString();
	
					log.warn(errorMessage);
					close();
					throw new IOException(errorMessage);
				}
	
				setFinalReadTime();
	
				clientMessageUtility.S2MList(clientSC, socketOutputStream, syncReceivedMessageBlockingQueue);
				
				// log.info("numberOfReadBytes={}, readableMiddleObjectWrapperQueue.isEmpty={}", numberOfReadBytes, readableMiddleObjectWrapperQueue.isEmpty());
			} while(! syncReceivedMessageBlockingQueue.isReceivedMessage());			
				
		} catch (NoMoreDataPacketBufferException e) {
			String errorMessage = new StringBuilder()
					.append("the no more data packet buffer error occurred while reading the socket[")
					.append(clientSC.hashCode())
					.append("], errmsg=")
					.append(e.getMessage()).toString();
			log.warn(errorMessage, e);
			close();
			
			throw e;
		} catch (IOException e) {
			String errorMessage = new StringBuilder()
					.append("the io error occurred while reading the socket[")
					.append(clientSC.hashCode())
					.append("], errmsg=")
					.append(e.getMessage()).toString();
			log.warn(errorMessage, e);
			close();
			throw e;
		} catch (Exception e) {
			String errorMessage = new StringBuilder()
					.append("the unknown error occurred while reading the socket[")
					.append(clientSC.hashCode())
					.append("], errmsg=")
					.append(e.getMessage()).toString();
			log.warn(errorMessage, e);
			close();
			throw new IOException(errorMessage);
		} finally {
			Arrays.fill(socketBuffer, CommonStaticFinalVars.ZERO_BYTE);
		}
		
		AbstractMessage outputMessage = clientMessageUtility.buildOutputMessage(classloaderOfInputMessage, 
				syncReceivedMessageBlockingQueue.getReadableMiddleObjectWrapper());
		
		return outputMessage;
	}

	@Override
	public void sendAsynInputMessage(AbstractMessage inputMessage) throws InterruptedException, NotSupportedException,
			IOException, NoMoreDataPacketBufferException, DynamicClassCallException, BodyFormatException {
		throw new NotSupportedException("this connection doesn't support this method because it is a blocking mode connection and no sharing connection between threads");
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
		socketOutputStream.close();
		
		if (null != clientInputStream) {
			try {
				clientInputStream.close();
			} catch(Exception e) {
				String errorMessage = new StringBuilder()
						.append("fail to close the input stream of socket channel[")
						.append(clientSC.hashCode())
						.append("], errmsg=")
						.append(e.getMessage()).toString();
				log.warn(errorMessage);
			}
		}
		
		if (null != clientOutputStream) {
			try {
				clientOutputStream.close();
			} catch(Exception e) {
				String errorMessage = new StringBuilder()
						.append("fail to close the output stream of socket channel[")
						.append(clientSC.hashCode())
						.append("], errmsg=")
						.append(e.getMessage()).toString();
				log.warn(errorMessage);
			}
		} 
		
		log.info("this connection[{}]'s resources has been released", clientSC.hashCode());
	}
	
	private void setFinalReadTime() {
		finalReadTime = new java.util.Date();
	}
	
	public java.util.Date getFinalReadTime() {
		return finalReadTime;
	}
}
