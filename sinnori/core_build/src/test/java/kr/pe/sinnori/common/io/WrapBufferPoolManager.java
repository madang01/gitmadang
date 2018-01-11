package kr.pe.sinnori.common.io;

import java.nio.ByteOrder;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.LinkedBlockingQueue;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import kr.pe.sinnori.common.exception.NoMoreDataPacketBufferException;
import kr.pe.sinnori.common.project.DataPacketBufferPoolManagerIF;

public class WrapBufferPoolManager implements DataPacketBufferPoolManagerIF {
	private Logger log = LoggerFactory.getLogger(WrapBufferPoolManager.class);
	
	/** 모니터 객체 */
	private final Object dataPacketBufferQueueMonitor = new Object();
	
	
	private LinkedBlockingQueue<WrapBuffer> wrapBufferQueue = null;
	private int dataPacketBufferMaxCntPerMessage;
	private ByteOrder byteOrderOfProject = null;
	private int dataPacketBufferSize;
	private Set<Integer> queueOutWrapBufferSet = new HashSet<Integer>();
	private int dataPacketBufferCnt;
	
	/**
	 * 동기화 쓰지 않고 싱글턴 구현을 위한 비공개 클래스
	 */
	private static final class WrapBufferPoolManagerHolder {
		static final WrapBufferPoolManager singleton = new WrapBufferPoolManager();
	}

	/**
	 * 동기화 쓰지 않는 싱글턴 구현 메소드
	 * 
	 * @return 싱글턴 객체
	 */
	public static WrapBufferPoolManager getInstance() {
		return WrapBufferPoolManagerHolder.singleton;
	}

	/**
	 * 동기화 쓰지 않고 싱글턴 구현을 위한 생성자
	 */
	private WrapBufferPoolManager() {
		byteOrderOfProject = ByteOrder.LITTLE_ENDIAN;
		dataPacketBufferSize = 4096;
		dataPacketBufferCnt = 20;
		
		try {
			for (int i = 0; i < dataPacketBufferCnt; i++) {
				WrapBuffer buffer = new WrapBuffer(dataPacketBufferSize, byteOrderOfProject);
				wrapBufferQueue.add(buffer);				
			}
		} catch (OutOfMemoryError e) {
			String errorMessage = "OutOfMemoryError";
			log.error(errorMessage, e);
			System.exit(1);
		}
	}
	
	@Override
	public WrapBuffer pollDataPacketBuffer()
			throws NoMoreDataPacketBufferException {
		synchronized (dataPacketBufferQueueMonitor) {
			WrapBuffer wrapBuffer = wrapBufferQueue.poll();
			if (null == wrapBuffer) {
				String errorMessage = "no more wrap Buffer in queue";
				throw new NoMoreDataPacketBufferException(errorMessage);
			}

			wrapBuffer.queueOut();
			
			queueOutWrapBufferSet.add(wrapBuffer.hashCode());
			
			
			return wrapBuffer;
		}
	}

	@Override
	public void putDataPacketBuffer(WrapBuffer wrapBuffer) {
		if (null == wrapBuffer)
			return;

		int wrapBufferHashcode = wrapBuffer.hashCode();
		
		/**
		 * 2번 연속 반환 막기
		 */
		synchronized (dataPacketBufferQueueMonitor) {
			if (! queueOutWrapBufferSet.contains(wrapBufferHashcode)) {
				String errorMessage = String.format("the parameter wrapBuffer[%d] is not registed in a queue-out set", wrapBufferHashcode);
				log.warn(errorMessage, new Throwable());
				throw new IllegalArgumentException(errorMessage);
			}
			if (wrapBuffer.isInQueue()) {
				String errorMessage = String.format("the parameter wrapBuffer[%d] is already in queue", wrapBufferHashcode);
				log.warn(errorMessage, new Throwable());
				throw new IllegalArgumentException(errorMessage);
			}
			wrapBuffer.queueIn();
			queueOutWrapBufferSet.remove(wrapBufferHashcode);
		}

		wrapBufferQueue.add(wrapBuffer);
	}

	@Override
	public final int getDataPacketBufferMaxCntPerMessage() {
		return dataPacketBufferMaxCntPerMessage;
	}

	@Override
	public int getDataPacketBufferSize() {
		return dataPacketBufferSize;
	}

	@Override
	public String getQueueState() {
		StringBuilder strBuilder = new StringBuilder();
		strBuilder.append("DataPacketBufferQueue size=[");
		strBuilder.append(wrapBufferQueue.size());
		strBuilder.append("]");
		return strBuilder.toString();
	}

	@Override
	public final ByteOrder getByteOrder() {
		return byteOrderOfProject;
	}
}
