package kr.pe.codda.common.io;

import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.channels.SocketChannel;
import java.nio.charset.CharsetDecoder;
import java.util.ArrayDeque;

import kr.pe.codda.common.exception.NoMoreDataPacketBufferException;

public class ReceivedDataOnlyStream {
	private InternalLogger log = InternalLoggerFactory.getInstance(ReceivedDataOnlyStream.class);	
	
	private CharsetDecoder streamCharsetDecoder = null;
	private DataPacketBufferPoolIF dataPacketBufferPool = null;
	private int dataPacketBufferMaxCntPerMessage = -1;
	
	private ByteOrder streamByteOrder = null;	
	private long numberOfSocketReadBytes = 0;
	
	/** 소켓 채널 전용 출력 메시지 읽기 전용 버퍼 목록 */
	private ArrayDeque<WrapBuffer> streamWrapBufferQueue = new  ArrayDeque<WrapBuffer>();
	/** 메시지를 추출시 생기는 부가 정보를  */
	private Object userDefObject = null;
	private boolean isClosed = false;
	
	
	public ReceivedDataOnlyStream(CharsetDecoder streamCharsetDecoder, 
			int dataPacketBufferMaxCntPerMessage, 
			DataPacketBufferPoolIF dataPacketBufferPool) throws NoMoreDataPacketBufferException {
		this.streamCharsetDecoder = streamCharsetDecoder;
		this.dataPacketBufferPool = dataPacketBufferPool;
		this.dataPacketBufferMaxCntPerMessage = dataPacketBufferMaxCntPerMessage;
		
		streamByteOrder = dataPacketBufferPool.getByteOrder();		
		numberOfSocketReadBytes = 0;
	}
	
	
	public ReceivedDataOnlyStream(ArrayDeque<WrapBuffer> writtenWrapBufferList, CharsetDecoder streamCharsetDecoder, 
			int dataPacketBufferMaxCntPerMessage, 
			DataPacketBufferPoolIF dataPacketBufferPoolManager) throws NoMoreDataPacketBufferException {
		
		this.streamCharsetDecoder = streamCharsetDecoder;
		this.dataPacketBufferPool = dataPacketBufferPoolManager;
		this.dataPacketBufferMaxCntPerMessage = dataPacketBufferMaxCntPerMessage;
		this.streamByteOrder = dataPacketBufferPoolManager.getByteOrder();		
		this.numberOfSocketReadBytes = 0;

		for (WrapBuffer writtenWrapBuffer : writtenWrapBufferList) {
			this.numberOfSocketReadBytes += writtenWrapBuffer.getByteBuffer().duplicate().flip().remaining();
			this.streamWrapBufferQueue.add(writtenWrapBuffer);
		}
		
	}
		
	private boolean isFull() {
		return (dataPacketBufferMaxCntPerMessage != streamWrapBufferQueue.size());
	}	

	private WrapBuffer addNewWrapBufferToStreamWrapBufferQueue() throws NoMoreDataPacketBufferException {
		if (! isFull()) {
			String errorMessage = String
					.format("this stream wrap buffer list size[%d] has researched the maximum number[%d] of data packt buffers per message message",
							streamWrapBufferQueue.size(), dataPacketBufferMaxCntPerMessage);
			throw new NoMoreDataPacketBufferException(errorMessage);
		}
		
		WrapBuffer newWrapBuffer = dataPacketBufferPool.pollDataPacketBuffer();
		
		
		streamWrapBufferQueue.add(newWrapBuffer);
		return newWrapBuffer;
	}

	/**
	 * '수신한 데이터 전담 스트림' 에서 메시지로를 추출하여 남은 데이터를 앞쪽으로 꽉 채워주는 메소드이다
	 *   
	 * @param lastByteBufferOfReceviedMessageStream 버퍼 목록으로 표현된 메시지 스트림의 마지막 버퍼로 추출된 메시지외 잔존 데이터를 갖고 있다, 이 메소드 종료후 추출된 메시지 스트림에 맞쳐 버퍼 속성은 재 조정된다
	 * @throws NoMoreDataPacketBufferException
	 */
	private void compackStreamWrapBufferQueue(ByteBuffer lastByteBufferOfReceviedMessageStream)
			throws NoMoreDataPacketBufferException {
		/**
		 * '수신한 데이터 전담 스트림' 에서 메시지 추출후 남은 스트림을 다른 큐에 백업한다
		 */
		ArrayDeque<WrapBuffer> backupStreamWrapBufferQueue = new ArrayDeque<WrapBuffer>(streamWrapBufferQueue.size());
		while (! streamWrapBufferQueue.isEmpty()) {
			WrapBuffer outputStreamWrapBuffer = streamWrapBufferQueue.removeFirst();				
			backupStreamWrapBufferQueue.add(outputStreamWrapBuffer);
		}
		
		/** 잔존 데이터를 '수신한 데이터 전담 스트림' 의 첫번째 버퍼에 넣는다 */
		WrapBuffer newFirstWrapBuffer = dataPacketBufferPool.pollDataPacketBuffer(); 
		newFirstWrapBuffer.getByteBuffer().put(lastByteBufferOfReceviedMessageStream);		
		streamWrapBufferQueue.add(newFirstWrapBuffer);
		
		/** 메시지 추출후 남은 스트림을 '수신한 데이터 전담 스트림' 에 이어 붙인다 */
		while (! backupStreamWrapBufferQueue.isEmpty()) {
			WrapBuffer firstWrapBufferOfBackup = backupStreamWrapBufferQueue.removeFirst();
			
			ByteBuffer fistByteBufferOfBackup = firstWrapBufferOfBackup.getByteBuffer();
			fistByteBufferOfBackup.flip();
			
			ByteBuffer lastByteBuffer = streamWrapBufferQueue.getLast().getByteBuffer();				

			while (lastByteBuffer.hasRemaining()) {
				if (! fistByteBufferOfBackup.hasRemaining()) {
					break; 
				}
				lastByteBuffer.put(fistByteBufferOfBackup.get());
			}
			
			if (fistByteBufferOfBackup.hasRemaining()) {
				fistByteBufferOfBackup.compact();
				streamWrapBufferQueue.add(firstWrapBufferOfBackup);
			} else {				
				if (backupStreamWrapBufferQueue.size() > 0) {
					log.error("dead code::if stream then backupStreamWrapBufferQueue's size is zero, so nobody knows if a side effect bug will occur");
					System.exit(1);
				}	
				
				dataPacketBufferPool.putDataPacketBuffer(firstWrapBufferOfBackup);				
			}
		}
	}	

	public int read(SocketChannel readableSocketChannel) throws IOException, NoMoreDataPacketBufferException {
		int numRead = 0;
		
		ByteBuffer lastByteBuffer = null;
		WrapBuffer lastWrapBuffer = null;
		
		if (streamWrapBufferQueue.isEmpty()) {
			lastWrapBuffer = addNewWrapBufferToStreamWrapBufferQueue();
		} else {
			lastWrapBuffer = streamWrapBufferQueue.peekLast();
		}
		
		lastByteBuffer = lastWrapBuffer.getByteBuffer();
		
		do {
			if (! lastByteBuffer.hasRemaining()) {
				lastWrapBuffer = addNewWrapBufferToStreamWrapBufferQueue();
				lastByteBuffer = lastWrapBuffer.getByteBuffer();
			}
			
			numRead = readableSocketChannel.read(lastByteBuffer);			
			
			if (numRead <= 0) {
				break;
			}
			
			numberOfSocketReadBytes += numRead;						
			
		} while(true);
		
		return numRead;
	}
	
	public int read(InputStream is, byte[] buffer) throws IOException, NoMoreDataPacketBufferException {
		int numRead = 0;
		
		ByteBuffer lastByteBuffer = null;
		WrapBuffer lastWrapBuffer = null;
		
		if (streamWrapBufferQueue.isEmpty()) {
			lastWrapBuffer = addNewWrapBufferToStreamWrapBufferQueue();
		} else {
			lastWrapBuffer = streamWrapBufferQueue.peekLast();
		}
		
		lastByteBuffer = lastWrapBuffer.getByteBuffer();
		
		
		if (! lastByteBuffer.hasRemaining()) {
			lastWrapBuffer = addNewWrapBufferToStreamWrapBufferQueue();
			lastByteBuffer = lastWrapBuffer.getByteBuffer();
		}
		
		numRead = is.read(buffer, 0, lastByteBuffer.remaining());
		
		// log.info("numRead={}, buffer={}", numRead, HexUtil.getHexStringFromByteArray(buffer, 0, numRead));
		if (numRead <= 0) {
			return numRead;
		}
		
		lastByteBuffer.put(buffer, 0, numRead);			
		
		numberOfSocketReadBytes += numRead;		
		
		return numRead;
	}
		
	public FreeSizeInputStream cutMessageInputStreamFromStartingPosition (long size) throws NoMoreDataPacketBufferException {
		if (size <= 0) {
			String errorMessage = new StringBuilder()
			.append("the parameter size[")
			.append(size)
			.append("] is less than or equal to zero").toString();
			
			throw new IllegalArgumentException(errorMessage);
		}		
		
		if (size > numberOfSocketReadBytes) {
			String errorMessage = new StringBuilder()
			.append("the parameter size[")
			.append(size)
			.append("] is greater than the number of socket read bytes[")
			.append(numberOfSocketReadBytes)
			.append("]").toString();
			
			throw new IllegalArgumentException(errorMessage);
		}
		
		numberOfSocketReadBytes -= size;
		
		int lastPositionOfMessageStreamLastByteBuffer = -1;
		ArrayDeque<WrapBuffer> messageStreamWrapBufferQueue = new ArrayDeque<WrapBuffer>();
		do {
			WrapBuffer firstWrapBufferOfReceviedDataOnlyStream = streamWrapBufferQueue.removeFirst();
			ByteBuffer firstByteBufferOfReceviedDataOnlyStream = firstWrapBufferOfReceviedDataOnlyStream.getByteBuffer();
			int remaining = firstByteBufferOfReceviedDataOnlyStream.flip().remaining();
			
			if ((size - remaining) > 0L) {
				size -= remaining;
			} else {
				lastPositionOfMessageStreamLastByteBuffer = (int)size;
				firstByteBufferOfReceviedDataOnlyStream.position(firstByteBufferOfReceviedDataOnlyStream.position()+lastPositionOfMessageStreamLastByteBuffer);				
				size=0;
			}
			messageStreamWrapBufferQueue.add(firstWrapBufferOfReceviedDataOnlyStream);
		} while (size > 0);
		 
		//log.info("2. messageInputStreamWrapBufferQueue={}", messageInputStreamWrapBufferQueue.toString());
		
		ByteBuffer lastByteBufferOfMessageStream = messageStreamWrapBufferQueue.getLast().getByteBuffer();
		
		//log.info("3. messageInputStreamLastByteBuffer={}", messageInputStreamLastByteBuffer.toString());
		
		if (lastByteBufferOfMessageStream.hasRemaining()) {
			/** 잔존 데이터가 존재하면 데이터들을 앞으로 꽉 채워야 한다 */
			compackStreamWrapBufferQueue(lastByteBufferOfMessageStream);
		}
		
		//log.info("4. messageInputStreamLastByteBuffer={}", messageInputStreamLastByteBuffer.toString());
		
		/** 버퍼에 담긴 잔존 데이터 처리후 버퍼 속성 position 와 limit 값 복귀 */
		lastByteBufferOfMessageStream.position(0);
		lastByteBufferOfMessageStream.limit(lastPositionOfMessageStreamLastByteBuffer);		
		
		
		return new FreeSizeInputStream(dataPacketBufferMaxCntPerMessage, messageStreamWrapBufferQueue, streamCharsetDecoder, dataPacketBufferPool);
	}
	
	/**
	 * 새로운 사용자 정의 객체를 저장한다.
	 * @param newUserDefObject 새로운 사용자 정의 객체
	 */
	public void setUserDefObject(Object newUserDefObject) {
		this.userDefObject = newUserDefObject;
	}

	/**
	 * @return 사용자 정의 객체
	 */
	public Object getUserDefObject() {
		return userDefObject;
	}

	public long getStreamSize() {		
		return numberOfSocketReadBytes;
	}

	/**
	 * {@link #getStreamSize()} 검증을 위한 메소드 
	 * @return 버퍼의 유효 바이트의 합 즉 쓰여진 바이트 수를 반환한다.
	 */
	public long getSreamSizeUsingStreamWrapBufferQueue() {
		long numberOfSocketReadBytes = 0;
	
		for (WrapBuffer streamWrapBuffer : streamWrapBufferQueue) {
			ByteBuffer dupByteBuffer = streamWrapBuffer.getByteBuffer().duplicate();
			dupByteBuffer.flip();			
			numberOfSocketReadBytes += dupByteBuffer.remaining();
		}
	
		return numberOfSocketReadBytes;
	}
	
	public ArrayDeque<WrapBuffer> getStreamWrapBufferQueue() {
		return streamWrapBufferQueue;
	}
	
	public ByteOrder getStreamByteOrder() {
		return streamByteOrder;
	}
	
	
	public void close() {
		// log.info("call close");
		
		while (! streamWrapBufferQueue.isEmpty()) {
			WrapBuffer streamWrapBuffer = streamWrapBufferQueue.remove();			
			dataPacketBufferPool.putDataPacketBuffer(streamWrapBuffer);
		}
		
		numberOfSocketReadBytes = 0;
		userDefObject = null;
		isClosed = true;
	}
	
	public boolean isClosed() {
		return isClosed;
	}
	
}
