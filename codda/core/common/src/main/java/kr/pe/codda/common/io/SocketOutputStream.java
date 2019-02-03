package kr.pe.codda.common.io;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.channels.SocketChannel;
import java.nio.charset.CharsetDecoder;
import java.util.ArrayDeque;
import java.util.LinkedList;

import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;
import kr.pe.codda.common.exception.NoMoreDataPacketBufferException;

public class SocketOutputStream {
	private InternalLogger log = InternalLoggerFactory.getInstance(SocketOutputStream.class);	
	
	private CharsetDecoder streamCharsetDecoder = null;
	private DataPacketBufferPoolIF dataPacketBufferPool = null;
	private int dataPacketBufferMaxCntPerMessage = -1;
	
	private ByteOrder streamByteOrder = null;	
	private long numberOfWrittenBytes = 0;
	
	/** 소켓 채널 전용 출력 메시지 읽기 전용 버퍼 목록, 참고) 언제나 읽기가 가능 하도록 최소 크기가 1이다. */
	private ArrayDeque<WrapBuffer> socketOutputStreamWrapBufferQueue = new  ArrayDeque<WrapBuffer>();
	/** 메시지를 추출시 생기는 부가 정보를  */
	private Object userDefObject = null;
	private boolean isClosed = false;
	
	public SocketOutputStream(CharsetDecoder streamCharsetDecoder, 
			int dataPacketBufferMaxCntPerMessage, 
			DataPacketBufferPoolIF dataPacketBufferPool) throws NoMoreDataPacketBufferException {
		this.streamCharsetDecoder = streamCharsetDecoder;
		this.dataPacketBufferPool = dataPacketBufferPool;
		this.dataPacketBufferMaxCntPerMessage = dataPacketBufferMaxCntPerMessage;
		
		streamByteOrder = dataPacketBufferPool.getByteOrder();		
		numberOfWrittenBytes = 0;
		
		// addNewSocketOutputStreamWrapBuffer();
	}
	
	
	public SocketOutputStream(ArrayDeque<WrapBuffer> writtenWrapBufferList, CharsetDecoder streamCharsetDecoder, 
			int dataPacketBufferMaxCntPerMessage, 
			DataPacketBufferPoolIF dataPacketBufferPoolManager) throws NoMoreDataPacketBufferException {
		
		this.streamCharsetDecoder = streamCharsetDecoder;
		this.dataPacketBufferPool = dataPacketBufferPoolManager;
		this.dataPacketBufferMaxCntPerMessage = dataPacketBufferMaxCntPerMessage;
		this.streamByteOrder = dataPacketBufferPoolManager.getByteOrder();		
		this.numberOfWrittenBytes = 0;

		for (WrapBuffer writtenWrapBuffer : writtenWrapBufferList) {
			this.numberOfWrittenBytes += writtenWrapBuffer.getByteBuffer().duplicate().flip().remaining();
			this.socketOutputStreamWrapBufferQueue.add(writtenWrapBuffer);
		}
	}
		
	private boolean isFull() {
		return (dataPacketBufferMaxCntPerMessage != socketOutputStreamWrapBufferQueue.size());
	}	

	private WrapBuffer addNewSocketOutputStreamWrapBuffer() throws NoMoreDataPacketBufferException {
		// dataPacketBufferMaxCntPerMessage != socketOutputStreamWrapBufferList.size()
		if (! isFull()) {
			String errorMessage = String
					.format("this stream wrap buffer list size[%d] has researched the maximum number[%d] of data packt buffers per message message",
							socketOutputStreamWrapBufferQueue.size(), dataPacketBufferMaxCntPerMessage);
			throw new NoMoreDataPacketBufferException(errorMessage);
		}
		
		WrapBuffer newSocketOutputStreamWrapBuffer = dataPacketBufferPool.pollDataPacketBuffer();
		
		
		// log.info("add the new socketOutputStreamWrapBuffer[hashcode={}] from the data packet buffer pool to socketOutputStreamWrapBufferList", newSocketOutputStreamWrapBuffer.hashCode());
		
		socketOutputStreamWrapBufferQueue.add(newSocketOutputStreamWrapBuffer);
		return newSocketOutputStreamWrapBuffer;
	}

	private void compackOutputStreamWrapBufferList(ByteBuffer byteBufferHavingRemainingDataAfterCuttingMessageInputStream)
			throws NoMoreDataPacketBufferException {
		LinkedList<WrapBuffer> oldOutputStreamWrapBufferList = new LinkedList<WrapBuffer>();
		while (! socketOutputStreamWrapBufferQueue.isEmpty()) {
			/** move socket output stream wrap buffer list to old output stream wrap buffer list */
			WrapBuffer outputStreamWrapBuffer = socketOutputStreamWrapBufferQueue.removeFirst();				
			oldOutputStreamWrapBufferList.add(outputStreamWrapBuffer);
		}
		
		WrapBuffer outputStreamFirstWrapBuffer = dataPacketBufferPool.pollDataPacketBuffer(); 
		outputStreamFirstWrapBuffer.getByteBuffer().put(byteBufferHavingRemainingDataAfterCuttingMessageInputStream);
		
		/** renew socketOutputStreamWrapBufferList */
		// log.info("add the new outputStreamFirstWrapBuffer[hashcode={}] from the data packet buffer pool to socketOutputStreamWrapBufferList", outputStreamFirstWrapBuffer.hashCode());
		socketOutputStreamWrapBufferQueue.add(outputStreamFirstWrapBuffer);
		
		//log.info("socketOutputStreamWrapBufferList={}", socketOutputStreamWrapBufferList.toString());
		
		while (! oldOutputStreamWrapBufferList.isEmpty()) {
			WrapBuffer oldOutputStreamFirstWrapBuffer = oldOutputStreamWrapBufferList.removeFirst();
			
			ByteBuffer oldOutputStreamFistByteBuffer = oldOutputStreamFirstWrapBuffer.getByteBuffer();
			oldOutputStreamFistByteBuffer.flip();
			
			ByteBuffer outputStreamLastByteBuffer = socketOutputStreamWrapBufferQueue.getLast().getByteBuffer();				
			
			
			while (outputStreamLastByteBuffer.hasRemaining()) {
				if (! oldOutputStreamFistByteBuffer.hasRemaining()) {
					break; 
				}
				outputStreamLastByteBuffer.put(oldOutputStreamFistByteBuffer.get());
			}
			
			if (oldOutputStreamFistByteBuffer.hasRemaining()) {
				oldOutputStreamFistByteBuffer.compact();
				socketOutputStreamWrapBufferQueue.add(oldOutputStreamFirstWrapBuffer);
			} else {				
				if (oldOutputStreamWrapBufferList.size() > 0) {
					log.error("dead code::the var socketOutputStreamWrapBufferList is not a stream. so nobody knows if a side effect bug will occur");
					System.exit(1);
				}
				
				// log.info("return the old socketOutputStreamWrapBuffer[hashcode={}] to the data packet buffer pool", oldOutputStreamFirstWrapBuffer.hashCode());
				
				dataPacketBufferPool.putDataPacketBuffer(oldOutputStreamFirstWrapBuffer);
				
			}
		}
	}	

	public int read(SocketChannel readableSocketChannel) throws IOException, NoMoreDataPacketBufferException {
		int numRead = 0;
		
		ByteBuffer lastByteBuffer = null;
		WrapBuffer lastWrapBuffer = null;
		
		if (socketOutputStreamWrapBufferQueue.isEmpty()) {
			lastWrapBuffer = addNewSocketOutputStreamWrapBuffer();
		} else {
			lastWrapBuffer = socketOutputStreamWrapBufferQueue.peekLast();
		}
		
		lastByteBuffer = lastWrapBuffer.getByteBuffer();
		
		do {
			if (! lastByteBuffer.hasRemaining()) {
				lastWrapBuffer = addNewSocketOutputStreamWrapBuffer();
				lastByteBuffer = lastWrapBuffer.getByteBuffer();
			}
			
			numRead = readableSocketChannel.read(lastByteBuffer);			
			
			if (numRead <= 0) {
				break;
			}
			
			numberOfWrittenBytes += numRead;						
			
		} while(true);
		
		return numRead;
	}
	
	public int read(InputStream is, byte[] buffer) throws IOException, NoMoreDataPacketBufferException {
		int numRead = 0;
		
		ByteBuffer lastByteBuffer = null;
		WrapBuffer lastWrapBuffer = null;
		
		if (socketOutputStreamWrapBufferQueue.isEmpty()) {
			lastWrapBuffer = addNewSocketOutputStreamWrapBuffer();
		} else {
			lastWrapBuffer = socketOutputStreamWrapBufferQueue.peekLast();
		}
		
		lastByteBuffer = lastWrapBuffer.getByteBuffer();
		
		
		if (! lastByteBuffer.hasRemaining()) {
			lastWrapBuffer = addNewSocketOutputStreamWrapBuffer();
			lastByteBuffer = lastWrapBuffer.getByteBuffer();
		}
		
		numRead = is.read(buffer, 0, lastByteBuffer.remaining());
		
		// log.info("numRead={}, buffer={}", numRead, HexUtil.getHexStringFromByteArray(buffer, 0, numRead));
		if (numRead <= 0) {
			return numRead;
		}
		
		lastByteBuffer.put(buffer, 0, numRead);			
		
		numberOfWrittenBytes += numRead;		
		
		return numRead;
	}
	
	
	public SocketInputStream createNewSocketInputStream() throws NoMoreDataPacketBufferException {
		changeReadableWrapBufferList();		
		return new SocketInputStream(dataPacketBufferMaxCntPerMessage, socketOutputStreamWrapBufferQueue, streamCharsetDecoder, dataPacketBufferPool);
	}
	
	private void changeReadableWrapBufferList() throws NoMoreDataPacketBufferException {
		for (WrapBuffer sourceWrapBuffer : socketOutputStreamWrapBufferQueue) {
			ByteBuffer sourceByteBuffer = sourceWrapBuffer.getByteBuffer();
			sourceByteBuffer.flip();
		}
	}
	
	
	public FreeSizeInputStream cutMessageInputStreamFromStartingPosition (long size) throws NoMoreDataPacketBufferException {
		if (size <= 0) {
			throw new IllegalArgumentException(String.format("the parameter size[%d] is less than or equal to zero", size));
		}		
		
		if (size > numberOfWrittenBytes) {
			throw new IllegalArgumentException(String.format("the parameter size[%d] is greater than the number of written bytes[%d]", size, numberOfWrittenBytes));
		}
		
		numberOfWrittenBytes -= size;
		
		//log.info("1. socketOutputStreamWrapBufferList={}", socketOutputStreamWrapBufferList.toString());
		
		int lastPositionOfMessageInputStreamLastByteBuffer = -1;
		ArrayDeque<WrapBuffer> messageInputStreamWrapBufferList = new ArrayDeque<WrapBuffer>();
		do {
			WrapBuffer outputStreamWrapBuffer = socketOutputStreamWrapBufferQueue.removeFirst();
			ByteBuffer outputStreamByteBuffer = outputStreamWrapBuffer.getByteBuffer();
			int remaining = outputStreamByteBuffer.flip().remaining();
			
			if ((size - remaining) > 0L) {
				size -= remaining;
			} else {
				lastPositionOfMessageInputStreamLastByteBuffer = (int)size;
				outputStreamByteBuffer.position(outputStreamByteBuffer.position()+lastPositionOfMessageInputStreamLastByteBuffer);				
				size=0;
			}
			messageInputStreamWrapBufferList.add(outputStreamWrapBuffer);
		} while (size > 0);
		 
		//log.info("2. messageInputStreamWrapBufferList={}", messageInputStreamWrapBufferList.toString());
		
		ByteBuffer messageInputStreamLastByteBuffer = messageInputStreamWrapBufferList.getLast().getByteBuffer();
		
		//log.info("3. messageInputStreamLastByteBuffer={}", messageInputStreamLastByteBuffer.toString());
		
		if (messageInputStreamLastByteBuffer.hasRemaining()) {
			/** 잔존 데이터가 존재하면 데이터들을 앞으로 꽉 채워야 한다 */
			compackOutputStreamWrapBufferList(messageInputStreamLastByteBuffer);
		}
		
		//log.info("4. messageInputStreamLastByteBuffer={}", messageInputStreamLastByteBuffer.toString());
		
		/** 버퍼에 담긴 잔존 데이터 처리후 버퍼 속성 position 와 limit 값 복귀 */
		messageInputStreamLastByteBuffer.position(0);
		messageInputStreamLastByteBuffer.limit(lastPositionOfMessageInputStreamLastByteBuffer);		
		
		
		//log.info("5. messageInputStreamLastByteBuffer={}", messageInputStreamLastByteBuffer.toString());
		//log.info("6. socketOutputStreamWrapBufferList={}", socketOutputStreamWrapBufferList.toString());
		//log.info("7. numberOfWrittenBytes={}", numberOfWrittenBytes);
		
		return new FreeSizeInputStream(dataPacketBufferMaxCntPerMessage, messageInputStreamWrapBufferList, streamCharsetDecoder, dataPacketBufferPool);
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

	public long size() {		
		return numberOfWrittenBytes;
	}

	/**
	 * {@link #size()} 검증을 위한 메소드 
	 * @return 버퍼의 유효 바이트의 합 즉 쓰여진 바이트 수를 반환한다.
	 */
	public long getNumberOfWrittenBytesUsingList() {
		long numberOfWrittenBytes = 0;
	
		for (WrapBuffer socketOutputStreamWrapBuffer : socketOutputStreamWrapBufferQueue) {
			ByteBuffer dupByteBuffer = socketOutputStreamWrapBuffer.getByteBuffer().duplicate();
			dupByteBuffer.flip();			
			numberOfWrittenBytes += dupByteBuffer.remaining();
		}
	
		return numberOfWrittenBytes;
	}
	
	public ArrayDeque<WrapBuffer> getSocketOutputStreamWrapBufferList() {
		return socketOutputStreamWrapBufferQueue;
	}
	
	public ByteOrder getStreamByteOrder() {
		return streamByteOrder;
	}
	
	
	public void close() {
		// log.info("call close");
		
		while (! socketOutputStreamWrapBufferQueue.isEmpty()) {
			WrapBuffer socketOutputStreamWrapBuffer = socketOutputStreamWrapBufferQueue.remove();
			
			// log.info("return the socketOutputStreamWrapBuffer[hashcode={}] to the data packet buffer pool", socketOutputStreamWrapBuffer.hashCode());
			
			dataPacketBufferPool.putDataPacketBuffer(socketOutputStreamWrapBuffer);
		}
		
		numberOfWrittenBytes = 0;
		userDefObject = null;
		isClosed = true;
	}
	
	public boolean isClosed() {
		return isClosed;
	}
	
}
