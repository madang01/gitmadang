package kr.pe.sinnori.common.io;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.channels.SocketChannel;
import java.nio.charset.CharsetDecoder;
import java.util.ArrayList;
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
		
		/*WrapBuffer lastWrapBuffer = dataPacketBufferPoolManager.pollDataPacketBuffer();
		
		log.info("In Construct, lastWrapBuffer hashcode={}", lastWrapBuffer.hashCode());
		
		socketOutputStreamWrapBufferList.add(lastWrapBuffer);*/
		
		streamByteOrder = dataPacketBufferPoolManager.getByteOrder();
		dataPacketBufferSize = dataPacketBufferPoolManager.getDataPacketBufferSize();
		
		numberOfWrittenBytes = 0;
		
		addNewSocketOutputStreamWrapBuffer();
	}
	

	private boolean isFull() {
		return (dataPacketBufferMaxCntPerMessage != socketOutputStreamWrapBufferList.size());
	}

	private WrapBuffer addNewSocketOutputStreamWrapBuffer() throws NoMoreDataPacketBufferException {
		// dataPacketBufferMaxCntPerMessage != socketOutputStreamWrapBufferList.size()
		if (! isFull()) {
			String errorMessage = String
					.format("this stream wrap buffer list size[%d] has researched the maximum number[%d] of data packt buffers per message message",
							socketOutputStreamWrapBufferList.size(), dataPacketBufferMaxCntPerMessage);
			throw new NoMoreDataPacketBufferException(errorMessage);
		}
		
		WrapBuffer newSocketOutputStreamWrapBuffer = dataPacketBufferPoolManager.pollDataPacketBuffer();
		
		
		// log.info("add the new socketOutputStreamWrapBuffer[hashcode={}] from the data packet buffer pool to socketOutputStreamWrapBufferList", newSocketOutputStreamWrapBuffer.hashCode());
		
		socketOutputStreamWrapBufferList.add(newSocketOutputStreamWrapBuffer);
		return newSocketOutputStreamWrapBuffer;
	}

	private void compackOutputStreamWrapBufferList(ByteBuffer byteBufferHavingRemainingDataAfterCuttingMessageInputStream)
			throws NoMoreDataPacketBufferException {
		LinkedList<WrapBuffer> oldOutputStreamWrapBufferList = new LinkedList<WrapBuffer>();
		while (! socketOutputStreamWrapBufferList.isEmpty()) {
			/** move socket output stream wrap buffer list to old output stream wrap buffer list */
			WrapBuffer outputStreamWrapBuffer = socketOutputStreamWrapBufferList.removeFirst();				
			oldOutputStreamWrapBufferList.add(outputStreamWrapBuffer);
		}
		
		WrapBuffer outputStreamFirstWrapBuffer = dataPacketBufferPoolManager.pollDataPacketBuffer(); 
		outputStreamFirstWrapBuffer.getByteBuffer().put(byteBufferHavingRemainingDataAfterCuttingMessageInputStream);
		
		/** renew socketOutputStreamWrapBufferList */
		// log.info("add the new outputStreamFirstWrapBuffer[hashcode={}] from the data packet buffer pool to socketOutputStreamWrapBufferList", outputStreamFirstWrapBuffer.hashCode());
		socketOutputStreamWrapBufferList.add(outputStreamFirstWrapBuffer);
		
		//log.info("socketOutputStreamWrapBufferList={}", socketOutputStreamWrapBufferList.toString());
		
		while (! oldOutputStreamWrapBufferList.isEmpty()) {
			WrapBuffer oldOutputStreamFirstWrapBuffer = oldOutputStreamWrapBufferList.removeFirst();
			
			ByteBuffer oldOutputStreamFistByteBuffer = oldOutputStreamFirstWrapBuffer.getByteBuffer();
			oldOutputStreamFistByteBuffer.flip();
			
			ByteBuffer outputStreamLastByteBuffer = socketOutputStreamWrapBufferList.getLast().getByteBuffer();				
			
			
			while (outputStreamLastByteBuffer.hasRemaining()) {
				if (! oldOutputStreamFistByteBuffer.hasRemaining()) {
					break; 
				}
				outputStreamLastByteBuffer.put(oldOutputStreamFistByteBuffer.get());
			}
			
			if (oldOutputStreamFistByteBuffer.hasRemaining()) {
				oldOutputStreamFistByteBuffer.compact();
				socketOutputStreamWrapBufferList.add(oldOutputStreamFirstWrapBuffer);
			} else {				
				if (oldOutputStreamWrapBufferList.size() > 0) {
					log.error("dead code::the var socketOutputStreamWrapBufferList is not a stream. so nobody knows if a side effect bug will occur");
					System.exit(1);
				}
				
				// log.info("return the old socketOutputStreamWrapBuffer[hashcode={}] to the data packet buffer pool", oldOutputStreamFirstWrapBuffer.hashCode());
				
				dataPacketBufferPoolManager.putDataPacketBuffer(oldOutputStreamFirstWrapBuffer);
				
			}
		}
	}

	/**
	 * FIXME! 테스트를 위해서 지정한 크기를 갖는 빈 소켓 출력 스트림을 만든다
	 * @param size
	 * @throws NoMoreDataPacketBufferException
	 */
	/*public void makeEmptySocketOutputStream(long size) throws NoMoreDataPacketBufferException {
		if (size <= 0) {
			throw new IllegalArgumentException(String.format("the parameter size[%d] is less than or equal to zero", size));
		}
		
		close();
		
		int numberOfWrapBuffersRequiredForSize = (int)((size-1+dataPacketBufferSize) / dataPacketBufferSize);
		int lastIndex = numberOfWrapBuffersRequiredForSize - 1;
		int lastPosition = (int)(size - lastIndex * dataPacketBufferSize);
		
		int lastIndex;
		int lastPosition;
		
		long quotient = size / dataPacketBufferSize;
		long remainder = size % dataPacketBufferSize;
		
		if (0 == remainder && quotient > 1) {
			lastIndex = (int)(quotient - 1);
			lastPosition = dataPacketBufferSize;
		} else {
			lastIndex = (int)quotient;
			lastPosition = (int)remainder;
		}
		
		log.info("In makeEmptySocketOutputStream, size={}, dataPacketBufferSize={}, quotient={}, remainder={}", size, dataPacketBufferSize, quotient, remainder);
		
		log.info("In makeEmptySocketOutputStream, lastIndex={}, lastPosition={}", lastIndex, lastPosition);
		
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
	}*/
	
	// FIXME! 
	public void rebuildSocketOutputStream(ArrayList<WrapBuffer> newSocketOutputStreamWrapBufferList) {
		close();
		
		for (WrapBuffer newSocketOutputStreamWrapBuffer : newSocketOutputStreamWrapBufferList) {
			numberOfWrittenBytes += newSocketOutputStreamWrapBuffer.getByteBuffer().duplicate().flip().remaining();
			socketOutputStreamWrapBufferList.add(newSocketOutputStreamWrapBuffer);
			
			// log.info("add the new socketOutputStreamWrapBuffer[hashcode={}] from the data packet buffer pool to socketOutputStreamWrapBufferList", newSocketOutputStreamWrapBuffer.hashCode());
		}
	}

	public int read() throws IOException, NoMoreDataPacketBufferException, InterruptedException {
		int numRead = 0;
		
		WrapBuffer lastWrapBuffer = socketOutputStreamWrapBufferList.getLast();
		
		if (null == lastWrapBuffer) {
			lastWrapBuffer = addNewSocketOutputStreamWrapBuffer();
		}
		
		ByteBuffer lastByteBuffer = lastWrapBuffer.getByteBuffer();
		
		do {
			if (! lastByteBuffer.hasRemaining()) {
				lastWrapBuffer = addNewSocketOutputStreamWrapBuffer();
				lastByteBuffer = lastWrapBuffer.getByteBuffer();
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
	
	

	// Extract 
	public FreeSizeInputStream cutMessageInputStreamFromStartingPosition (long size) throws NoMoreDataPacketBufferException {
		if (size <= 0) {
			throw new IllegalArgumentException(String.format("the parameter size[%d] is less than or equal to zero", size));
		}		
		
		if (size > numberOfWrittenBytes) {
			throw new IllegalArgumentException(String.format("the parameter size[%d] is greater than the number of written bytes[%d]", size, numberOfWrittenBytes));
		}
		
		// Quotient and remainder
		
		/*int numberOfWrapBuffersRequiredForSize = (int)((size-1+dataPacketBufferSize) / dataPacketBufferSize);
		int lastIndex = numberOfWrapBuffersRequiredForSize - 1;
		int lastPosition = (int)(size - lastIndex * dataPacketBufferSize); */
		
		long quotient = size / dataPacketBufferSize;
		long remainder = size % dataPacketBufferSize;
		
		int lastIndex;
		int lastPosition;
		
		if (0 == remainder && quotient > 1) {			
			lastIndex = (int)(quotient - 1);
			lastPosition = dataPacketBufferSize;
		} else {
			lastIndex = (int)quotient;
			lastPosition = (int)remainder;
		}
		
		//log.info("In cutMessageInputStreamFromStartingPosition, size={}, dataPacketBufferSize={}, quotient={}, remainder={}", size, dataPacketBufferSize, quotient, remainder);
		//log.info("In cutMessageInputStreamFromStartingPosition, lastIndex={}, lastPosition={}", lastIndex, lastPosition);
		
		
		LinkedList<WrapBuffer> messageInputStreamWrapBufferList = new LinkedList<WrapBuffer>(); 
		for (int i=0; i <= lastIndex; i++) {
			WrapBuffer outputStreamWrapBuffer = socketOutputStreamWrapBufferList.removeFirst();
			outputStreamWrapBuffer.getByteBuffer().flip();
			messageInputStreamWrapBufferList.add(outputStreamWrapBuffer);
			
			// log.info("additional work done::messageInputStreamWrapBufferList[{}]'s wrapBuffer[{}]", i, outputStreamWrapBuffer.toString());
		}
		
		//log.info("1. messageInputStreamWrapBufferList={}", messageInputStreamWrapBufferList.toString());
		
		ByteBuffer messageInputStreamLastByteBuffer = messageInputStreamWrapBufferList.getLast().getByteBuffer();
		
		
		messageInputStreamLastByteBuffer.position(lastPosition);		
		
		//log.info("2. messageInputStreamLastByteBuffer={}", messageInputStreamLastByteBuffer.toString());
		
		if (messageInputStreamLastByteBuffer.hasRemaining()) {
			/** FIXME! 잔존 데이터가 존재하면 데이터들을 앞으로 꽉 채워야 한다 */
			compackOutputStreamWrapBufferList(messageInputStreamLastByteBuffer);
			
			// FIXME!
			/*int socketOutputStreamWrapBufferListSize = socketOutputStreamWrapBufferList.size();
			for (int i=0; i < socketOutputStreamWrapBufferListSize; i++) {
				WrapBuffer outputStreamWrapBuffer = socketOutputStreamWrapBufferList.get(i);				
				log.info("socketOutputStreamWrapBufferList[{}]={}", i, outputStreamWrapBuffer.toString());
			}*/
		}
		
		/** 버퍼에 담긴 잔존 데이터 처리후 버퍼 속성 position 와 limit 값 복귀 */
		messageInputStreamLastByteBuffer.position(0);
		messageInputStreamLastByteBuffer.limit(lastPosition);
		
		numberOfWrittenBytes -= size;
		
		// log.info("3. messageInputStreamWrapBufferList={}", messageInputStreamWrapBufferList.toString());
		// log.info("4. socketOutputStreamWrapBufferList={}", socketOutputStreamWrapBufferList.toString());
		// log.info("4. numberOfWrittenBytes={}", numberOfWrittenBytes);
		
		return new FreeSizeInputStream(dataPacketBufferMaxCntPerMessage, messageInputStreamWrapBufferList, streamCharsetDecoder, dataPacketBufferPoolManager);
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
	 * FIXME! {{@link #size()} 검증을 위한 메소드 
	 * @return 버퍼의 유효 바이트의 합 즉 쓰여진 바이트 수를 반환한다.
	 */
	public long getNumberOfWrittenBytes() {
		long numberOfWrittenBytes = 0;
	
		for (WrapBuffer socketOutputStreamWrapBuffer : socketOutputStreamWrapBufferList) {
			ByteBuffer dupByteBuffer = socketOutputStreamWrapBuffer.getByteBuffer().duplicate();
			dupByteBuffer.flip();			
			numberOfWrittenBytes += dupByteBuffer.remaining();
		}
	
		return numberOfWrittenBytes;
	}
	
	public LinkedList<WrapBuffer> getSocketOutputStreamWrapBufferList() {
		return socketOutputStreamWrapBufferList;
	}
 
	public void close() {
		// log.info("call close");
		
		while (! socketOutputStreamWrapBufferList.isEmpty()) {
			WrapBuffer socketOutputStreamWrapBuffer = socketOutputStreamWrapBufferList.remove();
			
			// log.info("return the socketOutputStreamWrapBuffer[hashcode={}] to the data packet buffer pool", socketOutputStreamWrapBuffer.hashCode());
			
			dataPacketBufferPoolManager.putDataPacketBuffer(socketOutputStreamWrapBuffer);
		}
		
		numberOfWrittenBytes = 0;
	}
	
}
