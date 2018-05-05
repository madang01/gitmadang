package kr.pe.codda.common.io;

import java.nio.ByteOrder;
import java.util.ArrayDeque;

import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;
import kr.pe.codda.common.exception.NoMoreDataPacketBufferException;

public class DataPacketBufferPool implements DataPacketBufferPoolIF {
	private InternalLogger log = InternalLoggerFactory.getInstance(DataPacketBufferPool.class);

	private final Object monitor = new Object();

	private ArrayDeque<WrapBuffer> dataPacketBufferQueue = null;
	
	private boolean isDirect;
	private ByteOrder dataPacketBufferByteOrder = null;
	private int dataPacketBufferSize;
	private int dataPacketBufferPoolSize;

	public DataPacketBufferPool(boolean isDirect, ByteOrder dataPacketBufferByteOrder, int dataPacketBufferSize,
			int dataPacketBufferPoolSize) {
		if (null == dataPacketBufferByteOrder) {
			throw new IllegalArgumentException("the parameter dataPacketBufferByteOrder is null");
		}

		if (dataPacketBufferSize <= 0) {
			String errorMessage = String.format("the parameter dataPacketBufferSize[%d] is less than or equal to zero",
					dataPacketBufferSize);
			throw new IllegalArgumentException(errorMessage);
		}

		if (dataPacketBufferPoolSize <= 0) {
			String errorMessage = String.format(
					"the parameter dataPacketBufferPoolSize[%d] is less than or equal to zero",
					dataPacketBufferPoolSize);
			throw new IllegalArgumentException(errorMessage);
		}

		this.isDirect = isDirect;
		this.dataPacketBufferByteOrder = dataPacketBufferByteOrder;
		this.dataPacketBufferSize = dataPacketBufferSize;
		this.dataPacketBufferPoolSize = dataPacketBufferPoolSize;

		dataPacketBufferQueue = new ArrayDeque<WrapBuffer>(dataPacketBufferPoolSize);

		try {

			for (int i = 0; i < dataPacketBufferPoolSize; i++) {
				WrapBuffer dataPacketBuffer = new WrapBuffer(isDirect, dataPacketBufferSize, dataPacketBufferByteOrder);
				dataPacketBuffer.setPoolBuffer(true);
				dataPacketBufferQueue.add(dataPacketBuffer);
				// allWrapBufferHashcodeSet.add(dataPacketBuffer.hashCode());
			}

			// log.info("the wrap buffer hashcode set={}",
			// allWrapBufferHashcodeSet.toString());
		} catch (OutOfMemoryError e) {
			String errorMessage = "OutOfMemoryError";
			log.error(errorMessage, e);
			System.exit(1);
		}
	}

	@Override
	public WrapBuffer pollDataPacketBuffer() throws NoMoreDataPacketBufferException {
		WrapBuffer dataPacketBuffer = null;
		synchronized (monitor) {
			dataPacketBuffer = dataPacketBufferQueue.poll();
		}
		if (null == dataPacketBuffer) {
			String errorMessage = "no more wrap buffer in the wrap buffer polling queue";
			throw new NoMoreDataPacketBufferException(errorMessage);
		}

		dataPacketBuffer.queueOut();

		// queueOutWrapBufferHashcodeSet.add(dataPacketBuffer.hashCode());

		// FIXME!, 테스트후 삭제 필요
		/*
		 * { String infoMessage = String.
		 * format("the WrapBuffer[%d] is removed from the wrap buffer polling queue",
		 * dataPacketBuffer.hashCode()); log.info(infoMessage, new
		 * Throwable(infoMessage)); }
		 */

		return dataPacketBuffer;
		
	}

	@Override
	public void putDataPacketBuffer(WrapBuffer dataPacketBuffer) {
		if (null == dataPacketBuffer) {
			return;
		}
		
		if (! dataPacketBuffer.isPoolBuffer()) {
			String errorMessage = String.format("the parameter dataPacketBuffer[%d] is not a pool wrap buffer",
					dataPacketBuffer.hashCode());
			log.warn(errorMessage, new Throwable(errorMessage));
			throw new IllegalArgumentException(errorMessage);
		}

		/**
		 * 2번 연속 반환 막기
		 */
		synchronized (monitor) {
			if (dataPacketBuffer.isInQueue()) {
				String errorMessage = String.format(
						"the parameter dataPacketBuffer[%d] was added to the wrap buffer polling queue",
						dataPacketBuffer.hashCode());
				log.warn(errorMessage, new Throwable(errorMessage));
				throw new IllegalArgumentException(errorMessage);
			}

			dataPacketBuffer.queueIn();
			dataPacketBufferQueue.add(dataPacketBuffer);

			// FIXME!, 테스트후 삭제 필요
			/*
			 * { String infoMessage = String.
			 * format("the parameter dataPacketBuffer[%d] is added to the wrap buffer polling queue"
			 * , dataPacketBuffer.hashCode()); log.info(infoMessage, new
			 * Throwable(infoMessage)); }
			 */
		}

	}

	@Override
	public final int getDataPacketBufferSize() {
		return dataPacketBufferSize;
	}

	public String getQueueState() {
		StringBuilder strBuilder = new StringBuilder();
		strBuilder.append("dataPacketBufferPoolSize=[");
		strBuilder.append(dataPacketBufferPoolSize);
		strBuilder.append("], remaing size=[");
		strBuilder.append(dataPacketBufferQueue.size());
		strBuilder.append("]");
		return strBuilder.toString();
	}

	@Override
	public final ByteOrder getByteOrder() {
		return dataPacketBufferByteOrder;
	}

	public final int getDataPacketBufferPoolSize() {
		return dataPacketBufferPoolSize;
	}

	public boolean isDirect() {
		return isDirect;
	}
	
	public int size() {
		return dataPacketBufferQueue.size();
	}
		
}
