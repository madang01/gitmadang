package kr.pe.codda.common.io;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.CharBuffer;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.NoSuchElementException;

import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;
import kr.pe.codda.common.etc.CommonStaticFinalVars;
import kr.pe.codda.common.exception.BufferUnderflowExceptionWithMessage;
import kr.pe.codda.common.exception.CharsetDecoderException;
import kr.pe.codda.common.exception.NoMoreDataPacketBufferException;
import kr.pe.codda.common.util.HexUtil;

public final class ReceivedDataStream implements BinaryInputStreamIF {
	private InternalLogger log = InternalLoggerFactory.getInstance(ReceivedDataStream.class);

	private CharsetDecoder streamCharsetDecoder = null;
	private DataPacketBufferPoolIF dataPacketBufferPool = null;
	private int dataPacketBufferMaxCntPerMessage = -1;

	private ByteOrder streamByteOrder = null;
	private long numberOfReceivedBytes = 0;

	/** 메시지를 추출시 생기는 부가 정보를 */
	private Object userDefObject = null;

	private final int dataPacketBufferSize;
	private int front = 0;
	private int rear = 0;
	private final int circleQueueArraySize;
	private final WrapBuffer receivedStreamWrapBufferArray[];
	private final ByteBuffer readableByteBufferArray[];
	private long numberOfReadBytes = 0;
	private long mark = -1;
	
	private final ArrayDeque<WrapBuffer> backupStreamWrapBufferQueue = new ArrayDeque<WrapBuffer>();

	public ReceivedDataStream(CharsetDecoder streamCharsetDecoder, int dataPacketBufferMaxCntPerMessage,
			DataPacketBufferPoolIF dataPacketBufferPool) throws NoMoreDataPacketBufferException {
		this.streamCharsetDecoder = streamCharsetDecoder;
		this.dataPacketBufferPool = dataPacketBufferPool;
		this.dataPacketBufferMaxCntPerMessage = dataPacketBufferMaxCntPerMessage;

		streamByteOrder = dataPacketBufferPool.getByteOrder();
		numberOfReceivedBytes = 0;

		circleQueueArraySize = dataPacketBufferMaxCntPerMessage + 1;
		receivedStreamWrapBufferArray = new WrapBuffer[circleQueueArraySize];
		readableByteBufferArray = new ByteBuffer[circleQueueArraySize];
		dataPacketBufferSize = dataPacketBufferPool.getDataPacketBufferSize();
	}

	public ReceivedDataStream(ArrayDeque<WrapBuffer> writtenWrapBufferList, CharsetDecoder streamCharsetDecoder,
			int dataPacketBufferMaxCntPerMessage, DataPacketBufferPoolIF dataPacketBufferPoolManager)
			throws NoMoreDataPacketBufferException {

		this.streamCharsetDecoder = streamCharsetDecoder;
		this.dataPacketBufferPool = dataPacketBufferPoolManager;
		this.dataPacketBufferMaxCntPerMessage = dataPacketBufferMaxCntPerMessage;
		this.streamByteOrder = dataPacketBufferPoolManager.getByteOrder();
		this.numberOfReceivedBytes = 0;

		circleQueueArraySize = dataPacketBufferMaxCntPerMessage + 1;
		receivedStreamWrapBufferArray = new WrapBuffer[circleQueueArraySize];
		readableByteBufferArray = new ByteBuffer[circleQueueArraySize];
		dataPacketBufferSize = dataPacketBufferPool.getDataPacketBufferSize();
		
		ArrayDeque<WrapBuffer> compackArrayDeque = new ArrayDeque<WrapBuffer>();

		for (WrapBuffer writtenWrapBuffer : writtenWrapBufferList) {
			ByteBuffer writtenByteBuffer = writtenWrapBuffer.getByteBuffer();
			writtenByteBuffer.flip();
			
			if (writtenByteBuffer.remaining() == 0) {
				dataPacketBufferPoolManager.putDataPacketBuffer(writtenWrapBuffer);
				continue;
			}
			
			this.numberOfReceivedBytes += writtenByteBuffer.remaining();
			
			if (compackArrayDeque.isEmpty()) {
				writtenByteBuffer.compact();
				compackArrayDeque.addLast(writtenWrapBuffer);
			} else {
				WrapBuffer lastCompackWrapBuffer = compackArrayDeque.peekLast();
				ByteBuffer lastCompackByteBuffer = lastCompackWrapBuffer.getByteBuffer();
				
				if (! lastCompackByteBuffer.hasRemaining()) {
					writtenByteBuffer.compact();					
					compackArrayDeque.addLast(writtenWrapBuffer);
				} else {
					while (writtenByteBuffer.hasRemaining() && lastCompackByteBuffer.hasRemaining()) {
						lastCompackByteBuffer.put(writtenByteBuffer.get()); 
					}

					if (writtenByteBuffer.hasRemaining()) {
						writtenByteBuffer.compact();
						compackArrayDeque.addLast(writtenWrapBuffer);
					} else {
						dataPacketBufferPoolManager.putDataPacketBuffer(writtenWrapBuffer);
					}
				}
			}
		}
		
		for (WrapBuffer compackWrapBuffer : compackArrayDeque) {
			addLastToInputStreamQueue(compackWrapBuffer);
		}
	}

	private boolean isOutputStreamQueueFull() {
		return (dataPacketBufferMaxCntPerMessage == getCircleQueueSize());
	}

	private WrapBuffer addNewWrapBufferToStreamWrapBufferQueue() throws NoMoreDataPacketBufferException {		
		if (isOutputStreamQueueFull()) {
			String errorMessage = String.format(
					"this stream wrap buffer list size[%d] has researched the maximum number[%d] of data packt buffers per message message",
					getCircleQueueSize(), dataPacketBufferMaxCntPerMessage);
			throw new NoMoreDataPacketBufferException(errorMessage);
		}

		WrapBuffer newWrapBuffer = dataPacketBufferPool.pollDataPacketBuffer();
		addLastToInputStreamQueue(newWrapBuffer);

		return newWrapBuffer;
	}

	/**
	 * '수신한 데이터 전담 스트림' 에서 메시지로를 추출하여 남은 데이터를 앞쪽으로 꽉 채워주는 메소드이다
	 * 
	 * @param lastByteBufferOfReceviedMessageStream 버퍼 목록으로 표현된 메시지 스트림의 마지막 버퍼로 추출된
	 *                                              메시지외 잔존 데이터를 갖고 있다, 이 메소드 종료후
	 *                                              추출된 메시지 스트림에 맞쳐 버퍼 속성은 재 조정된다
	 * @throws NoMoreDataPacketBufferException
	 */
	private void compackStreamWrapBufferQueue(ByteBuffer lastByteBufferOfReceviedMessageStream)
			throws NoMoreDataPacketBufferException {
		/**
		 * '수신한 데이터 전담 스트림' 에서 메시지 추출후 남은 스트림을 다른 큐에 백업한다
		 */
		
		backupStreamWrapBufferQueue.clear();
		while (!isInputStreamQueueEmpty()) {
			WrapBuffer outputStreamWrapBuffer = removeFirstFromInputStreamQueue();
			backupStreamWrapBufferQueue.add(outputStreamWrapBuffer);
		}

		/** 잔존 데이터를 '수신한 데이터 전담 스트림' 의 첫번째 버퍼에 넣는다 */
		WrapBuffer newFirstWrapBuffer = dataPacketBufferPool.pollDataPacketBuffer();
		newFirstWrapBuffer.getByteBuffer().put(lastByteBufferOfReceviedMessageStream);
		addLastToInputStreamQueue(newFirstWrapBuffer);

		/** 메시지 추출후 남은 스트림을 '수신한 데이터 전담 스트림' 에 이어 붙인다 */
		while (! backupStreamWrapBufferQueue.isEmpty()) {
			WrapBuffer srcWrapBuffer = backupStreamWrapBufferQueue.removeFirst();

			ByteBuffer srcByteBuffer = srcWrapBuffer.getByteBuffer();
			srcByteBuffer.flip();

			ByteBuffer dstByteBuffer = peekLastFromInputStreamQueue().getByteBuffer();
			
			/**
			 * 잔존데이터 복사
			 */
			int minRemaining = Math.min(dstByteBuffer.remaining(), srcByteBuffer.remaining());
			srcByteBuffer.limit(srcByteBuffer.position()+minRemaining);
			dstByteBuffer.put(srcByteBuffer);
			srcByteBuffer.limit(srcByteBuffer.capacity());
			

			if (srcByteBuffer.hasRemaining()) {
				srcByteBuffer.compact();
				addLastToInputStreamQueue(srcWrapBuffer);
			} else {
				if (! backupStreamWrapBufferQueue.isEmpty()) {
					log.error(
							"dead code::if stream then backupStreamWrapBufferQueue's size is zero, so nobody knows if a side effect bug will occur");
					System.exit(1);
				}

				dataPacketBufferPool.putDataPacketBuffer(srcWrapBuffer);
			}
		}
	}

	public int read(SocketChannel readableSocketChannel) throws IOException, NoMoreDataPacketBufferException {
		int numRead = 0;

		ByteBuffer lastByteBuffer = null;
		WrapBuffer lastWrapBuffer = null;

		if (isInputStreamQueueEmpty()) {
			lastWrapBuffer = addNewWrapBufferToStreamWrapBufferQueue();
		} else {
			lastWrapBuffer = peekLastFromInputStreamQueue();
		}

		lastByteBuffer = lastWrapBuffer.getByteBuffer();

		do {
			if (!lastByteBuffer.hasRemaining()) {
				lastWrapBuffer = addNewWrapBufferToStreamWrapBufferQueue();
				lastByteBuffer = lastWrapBuffer.getByteBuffer();
			}

			numRead = readableSocketChannel.read(lastByteBuffer);

			if (numRead <= 0) {
				break;
			}

			numberOfReceivedBytes += numRead;

		} while (true);

		return numRead;
	}

	/**
	 * 
	 * @param is     JDK 1.2 소켓의 입력 스트림
	 * @param buffer 미리 준비해둔 JDK 1.2 소켓의 입력 스트림의 데이터를 받는 버퍼
	 * @return 읽을 바이트 수
	 * @throws IOException
	 * @throws NoMoreDataPacketBufferException
	 */
	public int read(InputStream is, byte[] buffer) throws IOException, NoMoreDataPacketBufferException {
		int numRead = 0;

		ByteBuffer lastByteBuffer = null;
		WrapBuffer lastWrapBuffer = null;

		if (isInputStreamQueueEmpty()) {
			lastWrapBuffer = addNewWrapBufferToStreamWrapBufferQueue();
		} else {
			lastWrapBuffer = peekLastFromInputStreamQueue();
		}

		lastByteBuffer = lastWrapBuffer.getByteBuffer();

		if (!lastByteBuffer.hasRemaining()) {
			lastWrapBuffer = addNewWrapBufferToStreamWrapBufferQueue();
			lastByteBuffer = lastWrapBuffer.getByteBuffer();
		}

		numRead = is.read(buffer, 0, lastByteBuffer.remaining());

		// log.info("numRead={}, buffer={}", numRead,
		// HexUtil.getHexStringFromByteArray(buffer, 0, numRead));
		if (numRead <= 0) {
			return numRead;
		}

		lastByteBuffer.put(buffer, 0, numRead);

		numberOfReceivedBytes += numRead;

		return numRead;
	}

	public FreeSizeInputStream cutReceivedDataStream(final long size)
			throws NoMoreDataPacketBufferException {
		if (size <= 0) {
			String errorMessage = new StringBuilder().append("the parameter size[").append(size)
					.append("] is less than or equal to zero").toString();

			throw new IllegalArgumentException(errorMessage);
		}

		if (size > numberOfReceivedBytes) {
			String errorMessage = new StringBuilder().append("the parameter size[").append(size)
					.append("] is greater than the number of socket read bytes[").append(numberOfReceivedBytes)
					.append("]").toString();

			throw new IllegalArgumentException(errorMessage);
		}
		
		long remaing = size;

		numberOfReceivedBytes -= size;
		
		int lastPositionOfMessageStreamLastByteBuffer = -1;
		ArrayDeque<WrapBuffer> messageStreamWrapBufferQueue = new ArrayDeque<WrapBuffer>();
		do {
			WrapBuffer firstWrapBufferOfReceviedDataOnlyStream = removeFirstFromInputStreamQueue();
			ByteBuffer firstByteBufferOfReceviedDataOnlyStream = firstWrapBufferOfReceviedDataOnlyStream
					.getByteBuffer();
			int remaining = firstByteBufferOfReceviedDataOnlyStream.flip().remaining();

			if ((remaing - remaining) > 0L) {
				remaing -= remaining;
			} else {
				lastPositionOfMessageStreamLastByteBuffer = (int) remaing;
				firstByteBufferOfReceviedDataOnlyStream.position(
						firstByteBufferOfReceviedDataOnlyStream.position() + lastPositionOfMessageStreamLastByteBuffer);
				remaing = 0;
			}
			messageStreamWrapBufferQueue.add(firstWrapBufferOfReceviedDataOnlyStream);
		} while (remaing > 0);

		// log.info("2. messageInputStreamWrapBufferQueue={}",
		// messageInputStreamWrapBufferQueue.toString());

		ByteBuffer lastByteBufferOfMessageStream = messageStreamWrapBufferQueue.getLast().getByteBuffer();

		// log.info("3. messageInputStreamLastByteBuffer={}",
		// messageInputStreamLastByteBuffer.toString());

		if (lastByteBufferOfMessageStream.hasRemaining()) {
			/** 잔존 데이터가 존재하면 데이터들을 앞으로 꽉 채워야 한다 */
			compackStreamWrapBufferQueue(lastByteBufferOfMessageStream);
		}

		// log.info("4. messageInputStreamLastByteBuffer={}",
		// messageInputStreamLastByteBuffer.toString());

		/** 버퍼에 담긴 잔존 데이터 처리후 버퍼 속성 position 와 limit 값 복귀 */
		lastByteBufferOfMessageStream.position(0);
		lastByteBufferOfMessageStream.limit(lastPositionOfMessageStreamLastByteBuffer);		
		
		/** 읽기 상태 초기화 */
		if (numberOfReadBytes > size) {
			remaing = numberOfReadBytes - size;
			
						
			final int circleQueueSize = getCircleQueueSize();
			
			// FIXME!
			// log.info("numberOfReadBytes={}, size={}, circleQueueSize={}", numberOfReadBytes, size, circleQueueSize);

			
			for (int i=0; i < circleQueueSize; i++) {
				final int workingIndex = (front + 1 + i) % circleQueueArraySize;
				
				ByteBuffer readableByteBuffer = readableByteBufferArray[workingIndex];
				
				log.info("readableByteBuffer={}", readableByteBuffer.toString());
				
				if (remaing > dataPacketBufferSize) {
					readableByteBuffer.limit(dataPacketBufferSize);
					readableByteBuffer.position(dataPacketBufferSize);
					
					remaing -= dataPacketBufferSize;
				} else if (remaing > 0) {
					readableByteBuffer.position((int)remaing);
					readableByteBuffer.limit(dataPacketBufferSize);
					remaing = 0;
				} else {
					readableByteBuffer.clear();
				}
			}
		}
		
		numberOfReadBytes = 0;
		
		

		return new FreeSizeInputStream(dataPacketBufferMaxCntPerMessage, messageStreamWrapBufferQueue,
				streamCharsetDecoder, dataPacketBufferPool);
	}

	/**
	 * 새로운 사용자 정의 객체를 저장한다.
	 * 
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

	public long getReceviedBytes() {
		return numberOfReceivedBytes;
	}

	/**
	 * {@link #getReceviedBytes()} 검증을 위한 메소드
	 * 
	 * @return 버퍼의 유효 바이트의 합 즉 쓰여진 바이트 수를 반환한다.
	 */
	public long getSreamSizeUsingStreamWrapBufferQueue() {
		long numberOfSocketReadBytes = 0;
		int size = getCircleQueueSize();

		for (int i = 0; i < size; i++) {
			WrapBuffer itemWrapBuffer = receivedStreamWrapBufferArray[(front + i + 1) % circleQueueArraySize];
			ByteBuffer itemByteBuffer = itemWrapBuffer.getByteBuffer();

			ByteBuffer dupByteBuffer = itemByteBuffer.duplicate();
			dupByteBuffer.flip();
			numberOfSocketReadBytes += dupByteBuffer.remaining();
		}

		return numberOfSocketReadBytes;
	}

	

	public ByteOrder getStreamByteOrder() {
		return streamByteOrder;
	}

	public void close() {
		// log.info("call close");
		numberOfReceivedBytes = 0;
		numberOfReadBytes = 0;
		userDefObject = null;

		while (! isInputStreamQueueEmpty()) {
			WrapBuffer streamWrapBuffer = removeFirstFromInputStreamQueue();
			dataPacketBufferPool.putDataPacketBuffer(streamWrapBuffer);
		}
	}

	/*
	public boolean isClosed() {
		return isClosed;
	}
	*/

	/************************/
	public boolean isInputStreamQueueEmpty() {
		return (rear == front);
	}

	public boolean isInputStreamQueueFull() {
		return ((rear + 1) % circleQueueArraySize == front);
	}

	public void addLastToInputStreamQueue(WrapBuffer itemWrapBuffer) throws IllegalStateException {
		if (isInputStreamQueueFull()) {
			dataPacketBufferPool.putDataPacketBuffer(itemWrapBuffer);
			throw new IllegalStateException("this wrap buffer queue is full");
		}

		rear = (rear + 1) % circleQueueArraySize;
		receivedStreamWrapBufferArray[rear] = itemWrapBuffer;

		ByteBuffer itemByteBuffer = itemWrapBuffer.getByteBuffer();
		ByteBuffer dupByteBuffer = itemByteBuffer.duplicate();
		dupByteBuffer.order(itemByteBuffer.order());
		dupByteBuffer.clear();
		readableByteBufferArray[rear] = dupByteBuffer;
	}

	public WrapBuffer peekFirstFromInputStreamQueue() {
		if (isInputStreamQueueEmpty()) {
			throw new NoSuchElementException("this wrap buffer queue is empty");
		}
		
		return receivedStreamWrapBufferArray[(front + 1) % circleQueueArraySize];
	}

	public WrapBuffer removeFirstFromInputStreamQueue() {
		if (isInputStreamQueueEmpty()) {
			throw new NoSuchElementException("this wrap buffer queue is empty");
		}
		// readableByteBufferDeque.removeFirst();

		front = (front + 1) % circleQueueArraySize;
		return receivedStreamWrapBufferArray[front];		
	}

	private WrapBuffer peekLastFromInputStreamQueue() throws NoSuchElementException {
		if (isInputStreamQueueEmpty()) {
			throw new NoSuchElementException("this wrap buffer queue is empty");
		}
		
		return receivedStreamWrapBufferArray[rear];
	}

	public int getCircleQueueSize() {		
		return (front <= rear) ? (rear - front) :  (circleQueueArraySize + rear - front);
	}

	public void mark() {
		mark = numberOfReadBytes;
	}

	public void reset() {
		if (-1 == mark) {
			return;
		}
		
		if (mark == numberOfReadBytes) {
			mark = -1;
			return;
		} /*
			 * else if (mark > numberOfReadBytes) {
			 * log.error("the var mark[{}] is greater than the var numberOfReadBytes[{}]",
			 * mark, numberOfReadBytes); System.exit(1); }
			 */
		numberOfReadBytes = mark;
		
		final int circleQueueSize = (front <= rear) ? (rear - front) :  (circleQueueArraySize + rear - front);
		for (int i=0; i < circleQueueSize; i++) {
			final int workingIndex = (front + 1 + i) % circleQueueArraySize;
			
			ByteBuffer readableByteBuffer = readableByteBufferArray[workingIndex];
			
			if (mark > dataPacketBufferSize) {
				mark -= dataPacketBufferSize;
				readableByteBuffer.position(dataPacketBufferSize);
			} else if (mark > 0) {
				readableByteBuffer.position((int)mark);
				mark = -1;
			} else {
				readableByteBuffer.position(0);
			}
		}
	}

	private void throwExceptionIfNumberOfBytesRemainingIsLessThanNumberOfBytesRequired(long numberOfBytesRequired)
			throws BufferUnderflowExceptionWithMessage {
		/** available() -> (numberOfReceivedBytes - numberOfReadBytes) */
		if ((numberOfReceivedBytes - numberOfReadBytes) < numberOfBytesRequired) {
			String errorMessage = new StringBuilder()
					.append("the number[").append((numberOfReceivedBytes - numberOfReadBytes))
					.append("] of bytes remaining in this input stream is less than [").append(numberOfBytesRequired)
					.append("] byte(s) that is required").toString();

			throw new BufferUnderflowExceptionWithMessage(errorMessage);
		}
	}

	private void doGetBytes(byte[] dstBytes, int offset, int len) throws BufferUnderflowExceptionWithMessage {
		int relativeIndex = (int) (numberOfReadBytes / dataPacketBufferSize);
		int workingIndex = (front + 1 + relativeIndex) % circleQueueArraySize;

		ByteBuffer workingByteBuffer = readableByteBufferArray[workingIndex];

		int remainingBytesOfWorkBuffer = workingByteBuffer.remaining();

		if (0 == remainingBytesOfWorkBuffer) {
			workingIndex = (workingIndex + 1) % circleQueueArraySize;
			workingByteBuffer = readableByteBufferArray[workingIndex];

			remainingBytesOfWorkBuffer = workingByteBuffer.remaining();
		}

		do {
			if (remainingBytesOfWorkBuffer >= len) {
				workingByteBuffer.get(dstBytes, offset, len);
				numberOfReadBytes += len;
				// len = 0;
				break;
			}

			workingByteBuffer.get(dstBytes, offset, remainingBytesOfWorkBuffer);
			numberOfReadBytes += remainingBytesOfWorkBuffer;

			offset += remainingBytesOfWorkBuffer;
			len -= remainingBytesOfWorkBuffer;

			workingIndex = (workingIndex + 1) % circleQueueArraySize;
			workingByteBuffer = readableByteBufferArray[workingIndex];
			remainingBytesOfWorkBuffer = workingByteBuffer.remaining();

		} while (0 != len);

	}

	private String doGetString(int length, Charset stringCharset)
			throws BufferUnderflowExceptionWithMessage, CharsetDecoderException {
		byte dstBytes[] = new byte[length];
		doGetBytes(dstBytes, 0, dstBytes.length);

		String dst = null;

		try {
			dst = new String(dstBytes, stringCharset);
		} catch (Exception e) {
			String errorMessage = String.format("fail to get a new String. read data hex[%s], charset[%s]",
					HexUtil.getHexStringFromByteArray(dstBytes), stringCharset.name());
			// log.warn(errorMessage, e);
			throw new CharsetDecoderException(errorMessage);
		}

		return dst;
	}

	@Override
	public byte getByte() throws BufferUnderflowExceptionWithMessage {
		throwExceptionIfNumberOfBytesRemainingIsLessThanNumberOfBytesRequired(1);

		int relativeIndex = (int) (numberOfReadBytes / dataPacketBufferSize);
		int workingIndex = (front + 1 + relativeIndex) % circleQueueArraySize;

		// int startOffset = (int)(workingOffset %
		// dataPacketBufferPool.getDataPacketBufferSize());

		ByteBuffer workingByteBuffer = readableByteBufferArray[workingIndex];
		byte retValue = workingByteBuffer.get();

		numberOfReadBytes++;

		return retValue;
	}

	@Override
	public short getUnsignedByte() throws BufferUnderflowExceptionWithMessage {
		throwExceptionIfNumberOfBytesRemainingIsLessThanNumberOfBytesRequired(1);

		int relativeIndex = (int) (numberOfReadBytes / dataPacketBufferSize);
		int workingIndex = (front + 1 + relativeIndex) % circleQueueArraySize;

		ByteBuffer workingByteBuffer = readableByteBufferArray[workingIndex];
		short retValue = (short) (workingByteBuffer.get() & 0xff);

		numberOfReadBytes++;

		return retValue;
	}

	@Override
	public short getShort() throws BufferUnderflowExceptionWithMessage {
		throwExceptionIfNumberOfBytesRemainingIsLessThanNumberOfBytesRequired(2);

		int relativeIndex = (int) (numberOfReadBytes / dataPacketBufferSize);
		int workingIndex = (front + 1 + relativeIndex) % circleQueueArraySize;

		ByteBuffer workingByteBuffer = readableByteBufferArray[workingIndex];

		short retValue = 0;

		byte t1 = workingByteBuffer.get();
		numberOfReadBytes++;

		if (! workingByteBuffer.hasRemaining()) {
			workingIndex = (workingIndex + 1) % circleQueueArraySize;
			workingByteBuffer = readableByteBufferArray[workingIndex];
		}

		byte t2 = workingByteBuffer.get();
		numberOfReadBytes++;

		if (ByteOrder.BIG_ENDIAN.equals(streamByteOrder)) {
			retValue = (short) (((t1 & 0xff) << 8) | (t2 & 0xff));
		} else {
			retValue = (short) (((t2 & 0xff) << 8) | (t1 & 0xff));
		}

		return retValue;
	}

	@Override
	public int getUnsignedShort() throws BufferUnderflowExceptionWithMessage {
		throwExceptionIfNumberOfBytesRemainingIsLessThanNumberOfBytesRequired(2);

		int relativeIndex = (int) (numberOfReadBytes / dataPacketBufferSize);
		int workingIndex = (front + 1 + relativeIndex) % circleQueueArraySize;

		ByteBuffer workingByteBuffer = readableByteBufferArray[workingIndex];

		int retValue = 0;

		byte t1 = workingByteBuffer.get();
		numberOfReadBytes++;

		if (!workingByteBuffer.hasRemaining()) {
			workingIndex = (workingIndex + 1) % circleQueueArraySize;
			workingByteBuffer = readableByteBufferArray[workingIndex];
		}

		byte t2 = workingByteBuffer.get();
		numberOfReadBytes++;

		if (ByteOrder.BIG_ENDIAN.equals(streamByteOrder)) {
			retValue = (((t1 & 0xff) << 8) | (t2 & 0xff));
		} else {
			retValue = (((t2 & 0xff) << 8) | (t1 & 0xff));
		}

		return retValue;
	}

	@Override
	public int getInt() throws BufferUnderflowExceptionWithMessage {
		throwExceptionIfNumberOfBytesRemainingIsLessThanNumberOfBytesRequired(4);

		int relativeIndex = (int) (numberOfReadBytes / dataPacketBufferSize);
		int workingIndex = (front + 1 + relativeIndex) % circleQueueArraySize;

		ByteBuffer workingByteBuffer = readableByteBufferArray[workingIndex];

		int retValue = 0;

		byte t1 = workingByteBuffer.get();
		numberOfReadBytes++;

		if (!workingByteBuffer.hasRemaining()) {
			workingIndex = (workingIndex + 1) % circleQueueArraySize;
			workingByteBuffer = readableByteBufferArray[workingIndex];
		}

		byte t2 = workingByteBuffer.get();
		numberOfReadBytes++;

		if (!workingByteBuffer.hasRemaining()) {
			workingIndex = (workingIndex + 1) % circleQueueArraySize;
			workingByteBuffer = readableByteBufferArray[workingIndex];
		}

		byte t3 = workingByteBuffer.get();
		numberOfReadBytes++;

		if (!workingByteBuffer.hasRemaining()) {
			workingIndex = (workingIndex + 1) % circleQueueArraySize;
			workingByteBuffer = readableByteBufferArray[workingIndex];
		}

		byte t4 = workingByteBuffer.get();
		numberOfReadBytes++;

		if (ByteOrder.BIG_ENDIAN.equals(streamByteOrder)) {
			retValue = (((t1 & 0xff) << 24) | ((t2 & 0xff) << 16) | ((t3 & 0xff) << 8) | (t4 & 0xff));
		} else {
			retValue = (((t4 & 0xff) << 24) | ((t3 & 0xff) << 16) | ((t2 & 0xff) << 8) | (t1 & 0xff));
		}

		return retValue;
	}

	@Override
	public long getUnsignedInt() throws BufferUnderflowExceptionWithMessage {
		throwExceptionIfNumberOfBytesRemainingIsLessThanNumberOfBytesRequired(4);

		int relativeIndex = (int) (numberOfReadBytes / dataPacketBufferSize);
		int workingIndex = (front + 1 + relativeIndex) % circleQueueArraySize;

		ByteBuffer workingByteBuffer = readableByteBufferArray[workingIndex];

		long retValue = 0;

		byte t1 = workingByteBuffer.get();
		numberOfReadBytes++;

		if (!workingByteBuffer.hasRemaining()) {
			workingIndex = (workingIndex + 1) % circleQueueArraySize;
			workingByteBuffer = readableByteBufferArray[workingIndex];
		}

		byte t2 = workingByteBuffer.get();
		numberOfReadBytes++;

		if (!workingByteBuffer.hasRemaining()) {
			workingIndex = (workingIndex + 1) % circleQueueArraySize;
			workingByteBuffer = readableByteBufferArray[workingIndex];
		}

		byte t3 = workingByteBuffer.get();
		numberOfReadBytes++;

		if (!workingByteBuffer.hasRemaining()) {
			workingIndex = (workingIndex + 1) % circleQueueArraySize;
			workingByteBuffer = readableByteBufferArray[workingIndex];
		}

		byte t4 = workingByteBuffer.get();
		numberOfReadBytes++;

		if (ByteOrder.BIG_ENDIAN.equals(streamByteOrder)) {
			retValue = (((t1 & 0xffL) << 24) | ((t2 & 0xffL) << 16) | ((t3 & 0xffL) << 8) | (t4 & 0xffL));
		} else {
			retValue = (((t4 & 0xffL) << 24) | ((t3 & 0xffL) << 16) | ((t2 & 0xffL) << 8) | (t1 & 0xffL));
		}

		return retValue;
	}

	@Override
	public long getLong() throws BufferUnderflowExceptionWithMessage {
		throwExceptionIfNumberOfBytesRemainingIsLessThanNumberOfBytesRequired(8);

		int relativeIndex = (int) (numberOfReadBytes / dataPacketBufferSize);
		int workingIndex = (front + 1 + relativeIndex) % circleQueueArraySize;

		ByteBuffer workingByteBuffer = readableByteBufferArray[workingIndex];

		long retValue = 0;

		byte t1 = workingByteBuffer.get();
		numberOfReadBytes++;

		if (!workingByteBuffer.hasRemaining()) {
			workingIndex = (workingIndex + 1) % circleQueueArraySize;
			workingByteBuffer = readableByteBufferArray[workingIndex];
		}

		byte t2 = workingByteBuffer.get();
		numberOfReadBytes++;

		if (!workingByteBuffer.hasRemaining()) {
			workingIndex = (workingIndex + 1) % circleQueueArraySize;
			workingByteBuffer = readableByteBufferArray[workingIndex];
		}

		byte t3 = workingByteBuffer.get();
		numberOfReadBytes++;

		if (!workingByteBuffer.hasRemaining()) {
			workingIndex = (workingIndex + 1) % circleQueueArraySize;
			workingByteBuffer = readableByteBufferArray[workingIndex];
		}

		byte t4 = workingByteBuffer.get();
		numberOfReadBytes++;

		if (!workingByteBuffer.hasRemaining()) {
			workingIndex = (workingIndex + 1) % circleQueueArraySize;
			workingByteBuffer = readableByteBufferArray[workingIndex];
		}

		byte t5 = workingByteBuffer.get();
		numberOfReadBytes++;

		if (!workingByteBuffer.hasRemaining()) {
			workingIndex = (workingIndex + 1) % circleQueueArraySize;
			workingByteBuffer = readableByteBufferArray[workingIndex];
		}

		byte t6 = workingByteBuffer.get();
		numberOfReadBytes++;

		if (!workingByteBuffer.hasRemaining()) {
			workingIndex = (workingIndex + 1) % circleQueueArraySize;
			workingByteBuffer = readableByteBufferArray[workingIndex];
		}

		byte t7 = workingByteBuffer.get();
		numberOfReadBytes++;

		if (!workingByteBuffer.hasRemaining()) {
			workingIndex = (workingIndex + 1) % circleQueueArraySize;
			workingByteBuffer = readableByteBufferArray[workingIndex];
		}

		byte t8 = workingByteBuffer.get();
		numberOfReadBytes++;

		if (ByteOrder.BIG_ENDIAN.equals(streamByteOrder)) {
			retValue = (((t1 & 0xffL) << 56) | ((t2 & 0xffL) << 48) | ((t3 & 0xffL) << 40) | ((t4 & 0xffL) << 32)
					| ((t5 & 0xffL) << 24) | ((t6 & 0xffL) << 16) | ((t7 & 0xffL) << 8) | (t8 & 0xffL));
		} else {
			retValue = (((t8 & 0xffL) << 56) | ((t7 & 0xffL) << 48) | ((t6 & 0xffL) << 40) | ((t5 & 0xffL) << 32)
					| ((t4 & 0xffL) << 24) | ((t3 & 0xffL) << 16) | ((t2 & 0xffL) << 8) | (t1 & 0xffL));
		}

		return retValue;
	}

	@Override
	public String getFixedLengthString(int fixedLength, CharsetDecoder wantedCharsetDecoder)
			throws BufferUnderflowExceptionWithMessage, IllegalArgumentException, CharsetDecoderException {
		if (fixedLength < 0) {
			throw new IllegalArgumentException(
					String.format("the parameter fixedLength[%d] is less than zero", fixedLength));
		}

		if (0 == fixedLength) {
			return "";
		}

		throwExceptionIfNumberOfBytesRemainingIsLessThanNumberOfBytesRequired(fixedLength);

		byte dstBytes[] = new byte[fixedLength];
		ByteBuffer dstByteBuffer = ByteBuffer.wrap(dstBytes);
		doGetBytes(dstBytes, 0, dstBytes.length);
		
		CharBuffer dstCharBuffer = null;

		try {
			dstCharBuffer = wantedCharsetDecoder.decode(dstByteBuffer);
		} catch (Exception e) {
			String errorMessage = String.format("fail to get a new string, read data hex[%s], charset[%s]",
					HexUtil.getHexStringFromByteArray(dstBytes), wantedCharsetDecoder.charset().name());
			// log.warn(errorMessage, e);
			throw new CharsetDecoderException(errorMessage);
		}

		return dstCharBuffer.toString();
	}

	@Override
	public String getFixedLengthString(int fixedLength)
			throws BufferUnderflowExceptionWithMessage, IllegalArgumentException, CharsetDecoderException {
		return getFixedLengthString(fixedLength, streamCharsetDecoder);
	}

	@Override
	public String getStringAll(Charset wantedCharset)
			throws BufferUnderflowExceptionWithMessage, IllegalArgumentException, CharsetDecoderException {
		if (null == wantedCharset) {
			throw new IllegalArgumentException("the parameter wantedCharset is null");
		}

		long numberOfBytesRemaining = numberOfReceivedBytes - numberOfReadBytes;

		if (0 == numberOfBytesRemaining) {
			return "";
		}

		if (numberOfBytesRemaining > Integer.MAX_VALUE) {
			/**
			 * 자바 문자열에 입력 가능한 바이트 배열의 크기는 Integer.MAX_VALUE 이다.
			 */
			throw new BufferUnderflowExceptionWithMessage(String.format(
					"the number[%d] of bytes remaing in this input stream is greater than the maximum value[%d] of integer",
					numberOfBytesRemaining, Integer.MAX_VALUE));
		}

		int length = (int) numberOfBytesRemaining;

		throwExceptionIfNumberOfBytesRemainingIsLessThanNumberOfBytesRequired(length);

		return doGetString(length, wantedCharset);
	}

	@Override
	public String getStringAll()
			throws BufferUnderflowExceptionWithMessage, IllegalArgumentException, CharsetDecoderException {
		return getStringAll(streamCharsetDecoder.charset());
	}

	@Override
	public String getPascalString()
			throws BufferUnderflowExceptionWithMessage, IllegalArgumentException, CharsetDecoderException {
		return getUBPascalString(streamCharsetDecoder.charset());
	}

	@Override
	public String getPascalString(Charset wantedCharset)
			throws BufferUnderflowExceptionWithMessage, IllegalArgumentException, CharsetDecoderException {
		return getUBPascalString(wantedCharset);
	}

	@Override
	public String getSIPascalString()
			throws BufferUnderflowExceptionWithMessage, IllegalArgumentException, CharsetDecoderException {
		return getSIPascalString(streamCharsetDecoder.charset());
	}

	@Override
	public String getSIPascalString(Charset wantedCharset)
			throws BufferUnderflowExceptionWithMessage, IllegalArgumentException, CharsetDecoderException {
		if (null == wantedCharset) {
			throw new IllegalArgumentException("the parameter wantedCharset is null");
		}

		throwExceptionIfNumberOfBytesRemainingIsLessThanNumberOfBytesRequired(4);

		int length = getInt();
		if (length < 0)
			throw new IllegalArgumentException(
					String.format("the pascal string length[%d] whose type is integer is less than zero", length));

		if (0 == length) {
			return "";
		}

		throwExceptionIfNumberOfBytesRemainingIsLessThanNumberOfBytesRequired(length);

		return doGetString(length, wantedCharset);
	}

	@Override
	public String getUSPascalString()
			throws BufferUnderflowExceptionWithMessage, IllegalArgumentException, CharsetDecoderException {
		return getUSPascalString(streamCharsetDecoder.charset());
	}

	@Override
	public String getUSPascalString(Charset wantedCharset)
			throws BufferUnderflowExceptionWithMessage, IllegalArgumentException, CharsetDecoderException {
		if (null == wantedCharset) {
			throw new IllegalArgumentException("the parameter wantedCharset is null");
		}

		throwExceptionIfNumberOfBytesRemainingIsLessThanNumberOfBytesRequired(2);

		int length = getUnsignedShort();

		if (0 == length) {
			return "";
		}

		throwExceptionIfNumberOfBytesRemainingIsLessThanNumberOfBytesRequired(length);

		return doGetString(length, wantedCharset);
	}

	@Override
	public String getUBPascalString()
			throws BufferUnderflowExceptionWithMessage, IllegalArgumentException, CharsetDecoderException {
		return getUBPascalString(streamCharsetDecoder.charset());
	}

	@Override
	public String getUBPascalString(Charset wantedCharset)
			throws BufferUnderflowExceptionWithMessage, IllegalArgumentException, CharsetDecoderException {
		if (null == wantedCharset) {
			throw new IllegalArgumentException("the parameter wantedCharset is null");
		}

		throwExceptionIfNumberOfBytesRemainingIsLessThanNumberOfBytesRequired(1);

		int length = getUnsignedByte();
		if (0 == length) {
			return "";
		}

		throwExceptionIfNumberOfBytesRemainingIsLessThanNumberOfBytesRequired(length);

		return doGetString(length, wantedCharset);
	}

	@Override
	public void getBytes(byte[] dst, int offset, int length)
			throws BufferUnderflowExceptionWithMessage, IllegalArgumentException {
		if (null == dst) {
			throw new IllegalArgumentException("the parameter dst is null");
		}

		if (offset < 0) {
			throw new IllegalArgumentException(String.format("the parameter offset[%d] less than zero", offset));
		}

		if (offset >= dst.length) {
			throw new IllegalArgumentException(
					String.format("the parameter offset[%d] greater than or equal to the dest buffer's length[%d]",
							offset, dst.length));
		}

		if (length < 0) {
			throw new IllegalArgumentException(String.format("the parameter length[%d] less than zero", length));
		}

		long sumOfOffsetAndLength = ((long) offset + length);
		if (sumOfOffsetAndLength > dst.length) {
			throw new IllegalArgumentException(String.format(
					"the sum[%d] of the parameter offset[%d] and the parameter length[%d] is greater than array.length[%d]",
					sumOfOffsetAndLength, offset, length, dst.length));
		}

		if (0 == length) {
			return;
		}

		throwExceptionIfNumberOfBytesRemainingIsLessThanNumberOfBytesRequired(length);

		// log.info(String.format("limitedRemainingBytes=[%d]", limitedRemainingBytes));

		doGetBytes(dst, offset, length);
	}

	@Override
	public void getBytes(byte[] dstBytes) throws BufferUnderflowExceptionWithMessage, IllegalArgumentException {
		if (null == dstBytes) {
			throw new IllegalArgumentException("paramerter dstBytes is null");
		}

		if (0 == dstBytes.length) {
			return;
		}

		throwExceptionIfNumberOfBytesRemainingIsLessThanNumberOfBytesRequired(dstBytes.length);

		// log.info(String.format("limitedRemainingBytes=[%d]", limitedRemainingBytes));

		doGetBytes(dstBytes, 0, dstBytes.length);

	}

	@Override
	public byte[] getBytes(int len) throws BufferUnderflowExceptionWithMessage, IllegalArgumentException {
		if (len < 0) {
			throw new IllegalArgumentException(String.format("parameter len[%d] less than zero", len));
		}

		if (0 == len) {
			return new byte[0];
		}

		throwExceptionIfNumberOfBytesRemainingIsLessThanNumberOfBytesRequired(len);

		// log.info(String.format("limitedRemainingBytes=[%d]", limitedRemainingBytes));

		/*
		 * long remainingBytes = remaining(); if (len > remainingBytes) { throw new
		 * IllegalArgumentException(String.format(
		 * "지정된 bye 크기[%d]는  남아 있은 버퍼 크기[%d] 보다 작거나 같아야 합니다.", len, remainingBytes)); }
		 */

		byte srcBytes[] = null;
		try {
			srcBytes = new byte[len];
		} catch (OutOfMemoryError e) {
			log.warn("OutOfMemoryError", e);
			throw e;
		}
		doGetBytes(srcBytes, 0, srcBytes.length);
		return srcBytes;
	}

	@Override
	public void skip(int n) throws BufferUnderflowExceptionWithMessage, IllegalArgumentException {
		skip((long) n);
	}

	public void skip(long n) throws BufferUnderflowExceptionWithMessage, IllegalArgumentException {
		if (0 == n) {
			return;
		}

		if (n < 0) {
			throw new IllegalArgumentException(String.format("the parameter n[%d] less than zero", n));
		}

		throwExceptionIfNumberOfBytesRemainingIsLessThanNumberOfBytesRequired(n);

		int relativeIndex = (int) (numberOfReadBytes / dataPacketBufferSize);
		int workingIndex = (front + 1 + relativeIndex) % circleQueueArraySize;


		ByteBuffer workingByteBuffer = readableByteBufferArray[workingIndex];

		// log.info("workingByteBuffer={}", workingByteBuffer);

		int remainingBytesOfWorkBuffer = workingByteBuffer.remaining();

		if (0 == remainingBytesOfWorkBuffer) {
			workingIndex = (workingIndex + 1) % circleQueueArraySize;
			workingByteBuffer = readableByteBufferArray[workingIndex];

			remainingBytesOfWorkBuffer = workingByteBuffer.remaining();
		}

		do {
			if (n <= remainingBytesOfWorkBuffer) {
				workingByteBuffer.position(workingByteBuffer.position() + (int) n);
				numberOfReadBytes += n;
				break;
			}

			workingByteBuffer.position(workingByteBuffer.limit());

			n -= remainingBytesOfWorkBuffer;
			numberOfReadBytes += remainingBytesOfWorkBuffer;

			workingIndex = (workingIndex + 1) % circleQueueArraySize;
			workingByteBuffer = readableByteBufferArray[workingIndex];
			remainingBytesOfWorkBuffer = workingByteBuffer.remaining();
		} while (n != 0);
	}

	@Override
	public Charset getCharset() {
		return streamCharsetDecoder.charset();
	}

	@Override
	public ByteOrder getByteOrder() {
		return dataPacketBufferPool.getByteOrder();
	}

	@Override
	public long available() {
		long numberOfBytesRemaining = numberOfReceivedBytes - numberOfReadBytes;
		return numberOfBytesRemaining;
	}

	public byte[] getMD5WithoutChange(long size) throws IllegalArgumentException, BufferUnderflowExceptionWithMessage {

		if (size < 0) {
			String errorMessage = new StringBuilder("the parameter size[").append(size).append("] is less than zero")
					.toString();
			log.warn(errorMessage);
			throw new IllegalArgumentException(errorMessage);
		}

		if (size > available()) {
			String errorMessage = String.format(
					"the parameter size[%d] is greater than the numbfer of bytes[%d] remaing in this input stream",
					size, available());
			log.info(errorMessage);
			throw new BufferUnderflowExceptionWithMessage(errorMessage);
		}

		if (0 == size) {
			byte md5Bytes[] = new byte[CommonStaticFinalVars.MD5_BYTESIZE];
			Arrays.fill(md5Bytes, CommonStaticFinalVars.ZERO_BYTE);
			return md5Bytes;
		}

		java.security.MessageDigest md5 = null;
		try {
			md5 = MessageDigest.getInstance("MD5");
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
			System.exit(1);
		}


		int relativeIndex = (int) (numberOfReadBytes / dataPacketBufferSize);
		int workingIndex = (front + 1 + relativeIndex) % circleQueueArraySize;

		while (size > 0) {
			ByteBuffer byteBuffer = readableByteBufferArray[workingIndex];
			
			/** MD5 이전 버퍼 상태 저장 */
			int backupPostion = byteBuffer.position();

			int remaing = byteBuffer.remaining();
			
			if (size < remaing) {
				byteBuffer.limit(byteBuffer.position() + (int) size);
				md5.update(byteBuffer);
				
				/** MD5 이전 버퍼 상태로 복구 */
				byteBuffer.limit(dataPacketBufferSize);
				byteBuffer.position(backupPostion);				
				break;
			}

			md5.update(byteBuffer);
			
			/** MD5 이전 버퍼 상태로 복구 */
			byteBuffer.position(backupPostion);
			
			size -= remaing;
			workingIndex = (workingIndex + 1) % circleQueueArraySize;
		}

		byte[] md5Bytes = md5.digest();
		return md5Bytes;
	}
	
	public byte[] getMD5(long size) throws IllegalArgumentException, BufferUnderflowExceptionWithMessage {

		if (size < 0) {
			String errorMessage = new StringBuilder("the parameter size[").append(size).append("] is less than zero")
					.toString();
			log.warn(errorMessage);
			throw new IllegalArgumentException(errorMessage);
		}

		if (0 == size) {
			byte md5Bytes[] = new byte[CommonStaticFinalVars.MD5_BYTESIZE];
			Arrays.fill(md5Bytes, CommonStaticFinalVars.ZERO_BYTE);
			return md5Bytes;
		}
		
		if (size > available()) {
			String errorMessage = String.format(
					"the parameter size[%d] is greater than the numbfer of bytes[%d] remaing in this input stream",
					size, available());
			log.info(errorMessage);
			throw new BufferUnderflowExceptionWithMessage(errorMessage);
		}		

		java.security.MessageDigest md5 = null;
		try {
			md5 = MessageDigest.getInstance("MD5");
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
			System.exit(1);
		}

		int relativeIndex = (int) (numberOfReadBytes / dataPacketBufferSize);
		int workingIndex = (front + 1 + relativeIndex) % circleQueueArraySize;
		
		while (size > 0) {
			ByteBuffer byteBuffer = readableByteBufferArray[workingIndex];

			int remaing = byteBuffer.remaining();

			if (size < remaing) {
				byteBuffer.limit(byteBuffer.position() + (int) size);
				md5.update(byteBuffer);
				byteBuffer.limit(dataPacketBufferSize);
				
				numberOfReadBytes += size;
				break;
			}

			md5.update(byteBuffer);
			size -= remaing;
			
			numberOfReadBytes += remaing;
			
			workingIndex = (workingIndex + 1) % circleQueueArraySize;
		}

		byte[] md5Bytes = md5.digest();
		return md5Bytes;
	}

}
