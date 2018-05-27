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
import kr.pe.codda.client.connection.asyn.mainbox.SyncMessageMailbox;
import kr.pe.codda.client.connection.asyn.noshare.AsynNoShareConnection;
import kr.pe.codda.common.config.subset.ProjectPartConfiguration;
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
import kr.pe.codda.common.protocol.ReceivedMessageBlockingQueueIF;
import kr.pe.codda.common.protocol.ReadableMiddleObjectWrapper;

public final class SyncNoShareConnection implements SyncConnectionIF, ReceivedMessageBlockingQueueIF {
	private InternalLogger log = InternalLoggerFactory.getInstance(AsynNoShareConnection.class);
	
	private SocketOutputStream socketOutputStream = null;
	private ClientMessageUtilityIF clientMessageUtility = null;
	
	private String serverHost = null;
	private int serverPort  = 0;
	private long socketTimeout=0;
	private int clientDataPacketBufferSize=0;
	
	private SocketChannel clientSC = null;
	private Socket clientSocket = null;
	private InputStream clinetInputStream = null;
	private OutputStream clinetOutputStream = null;	
	private SyncMessageMailbox syncMessageMailbox = null;
	private java.util.Date finalReadTime = new java.util.Date();
	private boolean isQueueIn = true;
	
	private ReadableMiddleObjectWrapper outputMessageWrapReadableMiddleObject = null;
	private byte[] socketBuffer = null;
	
	public SyncNoShareConnection(ProjectPartConfiguration projectPartConfiguration, 
			SocketOutputStream socketOutputStream,
			ClientMessageUtilityIF clientMessageUtility) throws IOException {
		
		this.socketOutputStream = socketOutputStream;
		this.clientMessageUtility = clientMessageUtility;
		
		serverHost = projectPartConfiguration.getServerHost();
		serverPort = projectPartConfiguration.getServerPort();
		socketTimeout = projectPartConfiguration.getClientSocketTimeout();
		clientDataPacketBufferSize = projectPartConfiguration.getClientDataPacketBufferSize();
		
		openSocketChannel();
		
		SocketAddress serverAddress = new InetSocketAddress(serverHost, serverPort);
		// clientSC.connect(serverAddress);
		
		clientSocket = clientSC.socket();
		try {
			clientSocket.setSoTimeout((int)socketTimeout);
			clientSocket.connect(serverAddress, (int)socketTimeout);
			clinetInputStream = clientSocket.getInputStream();
			clinetOutputStream = clientSocket.getOutputStream();
		} catch(Exception e) {
			try {
				clientSC.close();
			} catch (IOException e1) {
			}
			throw e;
		}
		
		
		final int mailboxID = 1;
		syncMessageMailbox = new SyncMessageMailbox(this, mailboxID, socketTimeout);
		
		
		socketBuffer = new byte[clientDataPacketBufferSize];
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
		
		inputMessage.messageHeaderInfo.mailboxID = syncMessageMailbox.getMailboxID();
		inputMessage.messageHeaderInfo.mailID = syncMessageMailbox.getMailID();
		
		ArrayDeque<WrapBuffer> inputMessageWrapBufferQueue = clientMessageUtility.buildReadableWrapBufferList(classloaderOfInputMessage, inputMessage);
		
		while (! inputMessageWrapBufferQueue.isEmpty()) {
			WrapBuffer inputMessageWrapBuffer = inputMessageWrapBufferQueue.pollFirst();
			try {
				ByteBuffer inputMessageByteBuffer = inputMessageWrapBuffer.getByteBuffer();
				int len = inputMessageByteBuffer.remaining();
				inputMessageByteBuffer.get(socketBuffer, 0, len);				
				clinetOutputStream.write(socketBuffer, 0, len);
				
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
				try {
					close();
				} catch (IOException e1) {
					log.warn("fail to close the socket channel[{}], errmsg={}", 
							hashCode(), e1.getMessage());
				}
				
				releaseResources();
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
				try {
					close();
				} catch (IOException e1) {
					log.warn("fail to close the socket channel[{}], errmsg={}", 
							hashCode(), e1.getMessage());
				}
				
				releaseResources();
			}	
		}		
		
		outputMessageWrapReadableMiddleObject = null;
		try {			
			do {
				int numberOfReadBytes = 0;			
				
				numberOfReadBytes = socketOutputStream.read(clinetInputStream, socketBuffer);
				
	
				if (numberOfReadBytes == -1) {
					String errorMessage = new StringBuilder("this socket channel[")
							.append(clientSC.hashCode())
							.append("] has reached end-of-stream").toString();
	
					log.warn(errorMessage);
					close();
					releaseResources();
					throw new IOException(errorMessage);
				}
	
				setFinalReadTime();
	
				clientMessageUtility.S2MList(clientSC, socketOutputStream, this);
				
				// log.info("numberOfReadBytes={}, readableMiddleObjectWrapperQueue.isEmpty={}", numberOfReadBytes, readableMiddleObjectWrapperQueue.isEmpty());
			} while(null == outputMessageWrapReadableMiddleObject);			
				
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
			
			throw e;
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
			throw e;
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
			throw e;
		} finally {
			Arrays.fill(socketBuffer, CommonStaticFinalVars.ZERO_BYTE);
		}
		
		
		AbstractMessage outputMessage = clientMessageUtility.buildOutputMessage(classloaderOfInputMessage, outputMessageWrapReadableMiddleObject);
		
		return outputMessage;
	}

	@Override
	public void sendAsynInputMessage(AbstractMessage inputMessage) throws InterruptedException, NotSupportedException,
			IOException, NoMoreDataPacketBufferException, DynamicClassCallException, BodyFormatException {
		throw new NotSupportedException("this connection doesn't support this method because it is a blocking mode connection and no sharing connection between threads");
	}

	@Override
	public void close() throws IOException {
		clientSocket.close();
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

	public void releaseResources() {
		socketOutputStream.close();
		
		if (null != clinetInputStream) {
			try {
				clinetInputStream.close();
			} catch(Exception e) {
				log.warn("fail to close a input stream, errmsg={}", e.getMessage());
			}
		}
		
		/*if (null != clinetOutputStream) {
			try {
				clinetOutputStream.close();
			} catch(Exception e) {
				log.warn("fail to close a output stream, errmsg={}", e.getMessage());
			}
		} */
		
		log.info("this connection[{}]'s resources has been released", clientSC.hashCode());
	}
	
	private void setFinalReadTime() {
		finalReadTime = new java.util.Date();
	}
	
	public java.util.Date getFinalReadTime() {
		return finalReadTime;
	}

	@Override
	public void putReceivedMessage(ReadableMiddleObjectWrapper readableMiddleObjectWrapper) throws InterruptedException {
		outputMessageWrapReadableMiddleObject = readableMiddleObjectWrapper;
	}
}
