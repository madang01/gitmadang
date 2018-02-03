package kr.pe.sinnori.common.io;

import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.channels.SocketChannel;
import java.nio.charset.CharsetDecoder;
import java.util.LinkedList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import kr.pe.sinnori.common.etc.WrapBufferUtil;
import kr.pe.sinnori.common.exception.NoMoreDataPacketBufferException;

public class SocketOutputStream {
	private Logger log = LoggerFactory.getLogger(SocketOutputStream.class);	
	
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
	
	public SocketOutputStream(CharsetDecoder streamCharsetDecoder, 
			int dataPacketBufferMaxCntPerMessage, 
			DataPacketBufferPoolManagerIF dataPacketBufferPoolManager) throws NoMoreDataPacketBufferException {
		
		// this.ownerSocketChannel =ownerSocketChannel;
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
	
	
	public SocketOutputStream(List<WrapBuffer> writtenWrapBufferList, CharsetDecoder streamCharsetDecoder, 
			int dataPacketBufferMaxCntPerMessage, 
			DataPacketBufferPoolManagerIF dataPacketBufferPoolManager) throws NoMoreDataPacketBufferException {
		
		this.streamCharsetDecoder = streamCharsetDecoder;
		this.dataPacketBufferPoolManager = dataPacketBufferPoolManager;
		this.dataPacketBufferMaxCntPerMessage = dataPacketBufferMaxCntPerMessage;
		this.streamByteOrder = dataPacketBufferPoolManager.getByteOrder();
		this.dataPacketBufferSize = dataPacketBufferPoolManager.getDataPacketBufferSize();		
		this.numberOfWrittenBytes = 0;

		for (WrapBuffer writtenWrapBuffer : writtenWrapBufferList) {
			this.numberOfWrittenBytes += writtenWrapBuffer.getByteBuffer().duplicate().flip().remaining();
			this.socketOutputStreamWrapBufferList.add(writtenWrapBuffer);
		}
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

	public int read(SocketChannel readableSocketChannel) throws IOException, NoMoreDataPacketBufferException {
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
			
			numRead = readableSocketChannel.read(lastByteBuffer);
			
			
			if (numRead <= 0) {
				break;
			}
			
			numberOfWrittenBytes += numRead;
			
			// Thread.sleep(retryTimeGap);
			
		} while(true);
		
		return numRead;
	}
	
	public int read(Socket readableSocket) throws IOException, NoMoreDataPacketBufferException {
		int numRead = 0;
		
		InputStream inputStream = readableSocket.getInputStream();
		byte recvBytes[] = new byte[dataPacketBufferSize];	
		
		WrapBuffer lastWrapBuffer = socketOutputStreamWrapBufferList.getLast();
		
		if (null == lastWrapBuffer) {
			lastWrapBuffer = addNewSocketOutputStreamWrapBuffer();
		}
		
		ByteBuffer lastByteBuffer = lastWrapBuffer.getByteBuffer();
		

		if (! lastByteBuffer.hasRemaining()) {
			lastWrapBuffer = addNewSocketOutputStreamWrapBuffer();
			lastByteBuffer = lastWrapBuffer.getByteBuffer();
		}
		
		numRead = inputStream.read(recvBytes,
				0,
				lastByteBuffer.remaining());			
		
		if (numRead > 0) {
			lastByteBuffer.put(recvBytes, 0, numRead);
			
			numberOfWrittenBytes += numRead;
		}
		
		return numRead;
	}
	
	
	public SocketInputStream createNewSocketInputStream() {
		List<WrapBuffer> duplicatedAndReadableWrapBufferList = WrapBufferUtil.getDuplicatedAndReadableWrapBufferList(socketOutputStreamWrapBufferList);
		return new SocketInputStream(dataPacketBufferMaxCntPerMessage, duplicatedAndReadableWrapBufferList, streamCharsetDecoder, dataPacketBufferPoolManager);
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
		LinkedList<WrapBuffer> messageInputStreamWrapBufferList = new LinkedList<WrapBuffer>();
		do {
			WrapBuffer outputStreamWrapBuffer = socketOutputStreamWrapBufferList.removeFirst();
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
	 * {@link #size()} 검증을 위한 메소드 
	 * @return 버퍼의 유효 바이트의 합 즉 쓰여진 바이트 수를 반환한다.
	 */
	public long getNumberOfWrittenBytesUsingList() {
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
	
	public ByteOrder getStreamByteOrder() {
		return streamByteOrder;
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
