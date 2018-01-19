package kr.pe.sinnori.common.io;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.channels.SocketChannel;
import java.nio.charset.CharsetDecoder;
import java.util.LinkedList;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import kr.pe.sinnori.common.exception.NoMoreDataPacketBufferException;

public class NewSocketOutputStream {
	private Logger log = LoggerFactory.getLogger(NewSocketOutputStream.class);
	
	
	private SocketChannel ownerSocketChannel = null;
	
	/** 소켓 채널 전용 출력 메시지 읽기 전용 버퍼 목록, 참고) 언제나 읽기가 가능 하도록 최소 크기가 1이다. */
	private LinkedList<WrapBuffer> socketOutputStreamWrapBufferList = new  LinkedList<WrapBuffer>();
	/** 메시지를 추출시 생기는 부가 정보를  */
	private Object userDefObject = null;
	
	private CharsetDecoder streamCharsetDecoder = null;
	private DataPacketBufferPoolManagerIF dataPacketBufferPoolManager = null;
	private ByteOrder streamByteOrder = null;
	private int dataPacketBufferSize = -1;
	
	private long numberOfWrittenBytes = 0;
	
	// private SocketChannel clientSC = null;
	private int dataPacketBufferMaxCntPerMessage = -1;
	public NewSocketOutputStream(SocketChannel ownerSocketChannel,
			CharsetDecoder streamCharsetDecoder, 
			int dataPacketBufferMaxCntPerMessage, 
			DataPacketBufferPoolManagerIF dataPacketBufferPoolManager) throws NoMoreDataPacketBufferException {
		
		this.ownerSocketChannel =ownerSocketChannel;
		this.streamCharsetDecoder = streamCharsetDecoder;
		this.dataPacketBufferPoolManager = dataPacketBufferPoolManager;
		this.dataPacketBufferMaxCntPerMessage = dataPacketBufferMaxCntPerMessage;
		
		WrapBuffer lastWrapBuffer = dataPacketBufferPoolManager.pollDataPacketBuffer();
		
		log.info("In Construct, lastWrapBuffer hashcode={}", lastWrapBuffer.hashCode());
		
		socketOutputStreamWrapBufferList.add(lastWrapBuffer);
		
		streamByteOrder = dataPacketBufferPoolManager.getByteOrder();
		dataPacketBufferSize = dataPacketBufferPoolManager.getDataPacketBufferSize();
		
		numberOfWrittenBytes = 0;
	}
	
	private ByteBuffer getLastDataPacketByteBuffer() {
		return socketOutputStreamWrapBufferList.getLast().getByteBuffer();
	}

	private boolean isFull() {
		return (dataPacketBufferMaxCntPerMessage != socketOutputStreamWrapBufferList.size());
	}

	/**
	 * @return 사용자 정의 객체
	 */
	public Object getUserDefObject() {
		return userDefObject;
	}
	
	/**
	 * 새로운 사용자 정의 객체를 저장한다.
	 * @param newUserDefObject 새로운 사용자 정의 객체
	 */
	public void setUserDefObject(Object newUserDefObject) {
		this.userDefObject = newUserDefObject;
	}
	
	public void close() {
		log.info("call close");
		
		while (! socketOutputStreamWrapBufferList.isEmpty()) {
			WrapBuffer outputMessageWrapBuffer = socketOutputStreamWrapBufferList.remove();			
			dataPacketBufferPoolManager.putDataPacketBuffer(outputMessageWrapBuffer);
		}
		
		numberOfWrittenBytes = 0;
	}
	
	public ByteBuffer addDataPacketWrapBuffer() throws NoMoreDataPacketBufferException {
		// dataPacketBufferMaxCntPerMessage != socketOutputStreamWrapBufferList.size()
		if (! isFull()) {
			String errorMessage = String
					.format("this stream wrap buffer list size[%d] has researched the maximum number[%d] of data packt buffers per message message",
							socketOutputStreamWrapBufferList.size(), dataPacketBufferMaxCntPerMessage);
			throw new NoMoreDataPacketBufferException(errorMessage);
		}
		
		WrapBuffer lastWrapBuffer = dataPacketBufferPoolManager.pollDataPacketBuffer();
		
		log.info("In addDataPacketWrapBuffer, lastWrapBuffer hashcode={}", lastWrapBuffer.hashCode());
		
		// log.info("In addDataPacketWrapBuffer, lastWrapBuffer={}", lastWrapBuffer.toString());
		
		socketOutputStreamWrapBufferList.add(lastWrapBuffer);
		return lastWrapBuffer.getByteBuffer();
	}
	
	public int read() throws IOException, NoMoreDataPacketBufferException, InterruptedException {
		int numRead = 0;
		ByteBuffer lastByteBuffer = getLastDataPacketByteBuffer();
		
		do {
			if (! lastByteBuffer.hasRemaining()) {				
				lastByteBuffer = addDataPacketWrapBuffer();
			}
			
			numRead = ownerSocketChannel.read(lastByteBuffer);
			
			
			if (numRead <= 0) {
				break;
			}
			
			numberOfWrittenBytes += numRead;
			
			// Thread.sleep(retryTimeGap);
			
		} while(true);
		
		return numRead;
	}
	
	
	public SocketInputStream getSocketInputStream() {		
		LinkedList<WrapBuffer> dupFlippedDataPacketBufferList = new LinkedList<WrapBuffer>(); 
		
		for (WrapBuffer dataPacketWrapBuffer : socketOutputStreamWrapBufferList) {
			ByteBuffer dupDataPacketByteBuffer = dataPacketWrapBuffer.getByteBuffer().duplicate();
			dupDataPacketByteBuffer.order(streamByteOrder);
			dupDataPacketByteBuffer.flip();
			dupFlippedDataPacketBufferList.add(new WrapBuffer(dupDataPacketByteBuffer));
		}
		
		return new SocketInputStream(dataPacketBufferMaxCntPerMessage, dupFlippedDataPacketBufferList, streamCharsetDecoder, dataPacketBufferPoolManager);
	}
	
	

	/**
	 * FIXME! 테스트를 위해서 지정한 크기를 갖는 빈 소켓 출력 스트림을 만든다
	 * @param size
	 * @throws NoMoreDataPacketBufferException
	 */
	public void makeEmptySocketOutputStream(long size) throws NoMoreDataPacketBufferException {
		if (size <= 0) {
			throw new IllegalArgumentException(String.format("the parameter size[%d] is less than or equal to zero", size));
		}
		
		close();
		
		int numberOfWrapBuffersRequiredForSize = (int)((size-1+dataPacketBufferSize) / dataPacketBufferSize);
		int lastIndex = numberOfWrapBuffersRequiredForSize - 1;
		int lastPosition = (int)(size - lastIndex * dataPacketBufferSize);
		
		
		log.info("In makeEmptySocketOutputStream, size={}, lastIndex={}, lastPosition={}", size, lastIndex, lastPosition);
		
		// log.info("socketOutputStreamWrapBufferList={}", socketOutputStreamWrapBufferList.toString());
		
		// dataPacketBufferSize
		ByteBuffer lastByteBuffer = null;
		for (int i=0; i < lastIndex; i++) {
			lastByteBuffer = addDataPacketWrapBuffer();
			lastByteBuffer.limit(lastByteBuffer.capacity());
			lastByteBuffer.position(lastByteBuffer.capacity());
		}
		
		lastByteBuffer = addDataPacketWrapBuffer();
		lastByteBuffer.limit(lastByteBuffer.capacity());
		lastByteBuffer.position(lastPosition);
		
		numberOfWrittenBytes = size;
		
		// log.info("In makeEmptySocketOutputStream, socketOutputStreamWrapBufferList={}", socketOutputStreamWrapBufferList.toString());
	}
	
	public long getNumberOfWrittenBytes() {
		long numberOfWrittenBytes = 0;

		for (WrapBuffer socketOutputStreamWrapBuffer : socketOutputStreamWrapBufferList) {
			ByteBuffer dupByteBuffer = socketOutputStreamWrapBuffer.getByteBuffer().duplicate();
			dupByteBuffer.flip();			
			numberOfWrittenBytes += dupByteBuffer.remaining();
		}

		return numberOfWrittenBytes;
	}
	
	public long size() {
		
		return numberOfWrittenBytes;
	}
	
	
	// Extract 
	public FreeSizeInputStream cutMessageInputStreamFromStartingPosition (long size) throws NoMoreDataPacketBufferException {
		if (size <= 0) {
			throw new IllegalArgumentException(String.format("the parameter size[%d] is less than or equal to zero", size));
		}		
		
		if (size > numberOfWrittenBytes) {
			throw new IllegalArgumentException(String.format("the parameter size[%d] is greater than the number of written bytes[%d]", size, numberOfWrittenBytes));
		}
		
		// Quotient and remainder
		
		int numberOfWrapBuffersRequiredForSize = (int)((size-1+dataPacketBufferSize) / dataPacketBufferSize);
		int lastIndex = numberOfWrapBuffersRequiredForSize - 1;
		int lastPosition = (int)(size - lastIndex * dataPacketBufferSize); 
		
		
		log.info("in cutMessageInputStreamFromStartingPosition, size={}, lastIndex={}, lastPosition={}", size, lastIndex, lastPosition);
		
		LinkedList<WrapBuffer> messageInputStreamWrapBufferList = new LinkedList<WrapBuffer>(); 
		for (int i=0; i <= lastIndex; i++) {
			WrapBuffer outputStreamWrapBuffer = socketOutputStreamWrapBufferList.removeFirst();
			outputStreamWrapBuffer.getByteBuffer().flip();
			messageInputStreamWrapBufferList.add(outputStreamWrapBuffer);
			
			// log.info("additional work done::messageInputStreamWrapBufferList[{}]'s wrapBuffer[{}]", i, outputStreamWrapBuffer.toString());
		}
		
		log.info("1. messageInputStreamWrapBufferList={}", messageInputStreamWrapBufferList.toString());
		
		ByteBuffer messageInputStreamLastByteBuffer = messageInputStreamWrapBufferList.getLast().getByteBuffer();
		
		// log.info("2. messageInputStreamLastByteBuffer={}", messageInputStreamLastByteBuffer.toString());
		
		messageInputStreamLastByteBuffer.position(lastPosition);		
		
		log.info("2. messageInputStreamLastByteBuffer={}", messageInputStreamLastByteBuffer.toString());
		
		if (messageInputStreamLastByteBuffer.hasRemaining()) {
			// FIXME! 잔존 데이터가 존재하면 추가후 스트림을 앞으로 꽉 채워야 한다
			WrapBuffer outputStreamFirstWrapBuffer = dataPacketBufferPoolManager.pollDataPacketBuffer(); 
			outputStreamFirstWrapBuffer.getByteBuffer().put(messageInputStreamLastByteBuffer);
						
			
			/** pack socket output stream buffer */
			LinkedList<WrapBuffer> subOutputStreamWrapBufferList = new LinkedList<WrapBuffer>();
			
			while (! socketOutputStreamWrapBufferList.isEmpty()) {
				/** move socket output stream wrap buffer list to sub output stream wrap buffer list */
				
				WrapBuffer outputStreamWrapBuffer = socketOutputStreamWrapBufferList.removeFirst();				
				subOutputStreamWrapBufferList.add(outputStreamWrapBuffer);
			}			
			
			
			//log.info("socketOutputStreamWrapBufferList isEmpty={}", socketOutputStreamWrapBufferList.isEmpty());
			//log.info("subOutputStreamWrapBufferList={}", subOutputStreamWrapBufferList.toString());			
			
			/** renew socket output stream wrap buffer list */
			socketOutputStreamWrapBufferList.add(outputStreamFirstWrapBuffer);
			
			//log.info("socketOutputStreamWrapBufferList={}", socketOutputStreamWrapBufferList.toString());
			
			
			
			while (! subOutputStreamWrapBufferList.isEmpty()) {
				WrapBuffer subOutputStreamFirstWrapBuffer = subOutputStreamWrapBufferList.removeFirst();
				
				ByteBuffer subOutputStreamFistByteBuffer = subOutputStreamFirstWrapBuffer.getByteBuffer();
				subOutputStreamFistByteBuffer.flip();
				
				ByteBuffer outputStreamLastByteBuffer = socketOutputStreamWrapBufferList.getLast().getByteBuffer();				
				
				
				while (outputStreamLastByteBuffer.hasRemaining()) {
					if (! subOutputStreamFistByteBuffer.hasRemaining()) {
						break; 
					}
					outputStreamLastByteBuffer.put(subOutputStreamFistByteBuffer.get());
				}
				
				if (subOutputStreamFistByteBuffer.hasRemaining()) {
					subOutputStreamFistByteBuffer.compact();
					socketOutputStreamWrapBufferList.add(subOutputStreamFirstWrapBuffer);
				} else {
					dataPacketBufferPoolManager.putDataPacketBuffer(subOutputStreamFirstWrapBuffer);
				}
			}
			
			// FIXME!
			/*int socketOutputStreamWrapBufferListSize = socketOutputStreamWrapBufferList.size();
			for (int i=0; i < socketOutputStreamWrapBufferListSize; i++) {
				WrapBuffer outputStreamWrapBuffer = socketOutputStreamWrapBufferList.get(i);				
				log.info("socketOutputStreamWrapBufferList[{}]={}", i, outputStreamWrapBuffer.toString());
			}*/
		}
		
		/** 잔존 데이터 복사 완료후 메시지 입력 스트림에 맞게 마지막 랩 버퍼 읽기 가능 상태로 만들기 위한 보정 작업 */
		messageInputStreamLastByteBuffer.position(0);
		messageInputStreamLastByteBuffer.limit(lastPosition);
		
		numberOfWrittenBytes -= size;
		
		log.info("3. messageInputStreamWrapBufferList={}", messageInputStreamWrapBufferList.toString());
		log.info("4. socketOutputStreamWrapBufferList={}", socketOutputStreamWrapBufferList.toString());
		log.info("4. numberOfWrittenBytes={}", numberOfWrittenBytes);
		
		return new FreeSizeInputStream(dataPacketBufferMaxCntPerMessage, messageInputStreamWrapBufferList, streamCharsetDecoder, dataPacketBufferPoolManager);
	}
	
}
