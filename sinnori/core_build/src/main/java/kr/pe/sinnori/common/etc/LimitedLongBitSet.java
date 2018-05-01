package kr.pe.sinnori.common.etc;

import java.util.LinkedList;

import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;


/**
 * <pre>
 * 이 클래스는 너무 큰 용량을 가져야 하는 성공/실패 정보를 담는 비트 집합을 
 * 어디까지 확인했으며 그중 실패한 결과는 무엇이다 라는 정보로 대체하여 용량을 줄였지만 아래와 같은 제약 사항을 갖는 LongBitset 구현 클래스이다.
 * 
 * - 제약 사항 -
 * LongBitset 에 비해서 저장 용량이 작지만 (1) 실패가 너무 많은 경우, 즉 {@link #failedIndexQueue} 크기 제약에 걸린 경우 
 * (2) 이빨이 빠진채로 작업이 너무 많이 진행될 경우, 즉 {@link #reservedIndexList} 크기 제약이 걸린 경우
 * FullListException 예외를 던진다.
 * 
 * </pre>
 * 
 * @author Won Jonghoon
 *
 */
public class LimitedLongBitSet {
	private InternalLogger log = InternalLoggerFactory.getInstance(LimitedLongBitSet.class);
	
	private final Object monitor = new Object();
	private final int maxSizeOfFailedIndexList = 3;
	
	private long maxBitNumber = -1L;	
	private long lastCheckedIndex = -1L;
	
	private long workingLastCheckedIndex = -1L;	
	private LinkedList<Long> failedIndexQueue = new LinkedList<Long>();

	public LimitedLongBitSet(long maxBitNumber) {
		if (maxBitNumber <= 0) {
			String errorMessage = String.format("the parameter maxBitNumber[%d] is less than or equal to zero", maxBitNumber);
			throw new IllegalArgumentException(errorMessage);
		}		
		
		this.maxBitNumber = maxBitNumber;
		this.lastCheckedIndex = this.workingLastCheckedIndex = -1L;
		
		log.info("maxBitNumber[{}], lastCheckedIndex=workingLastCheckedIndex=[{}]", maxBitNumber, lastCheckedIndex);
	}
	
	public LimitedLongBitSet(long maxBitNumber, long lastCheckedIndex) {
		if (maxBitNumber <= 0) {
			String errorMessage = String.format("the parameter maxBitNumber[%d] is less than or equal to zero", maxBitNumber);
			throw new IllegalArgumentException(errorMessage);
		}	
		
		if (lastCheckedIndex < 0) {
			String errorMessage = String.format("the parameter lastCheckedIndex[%d] is less than zero", lastCheckedIndex);
			
			throw new IllegalArgumentException(errorMessage);
		}
		if (lastCheckedIndex >= maxBitNumber) {
			String errorMessage = String.format("the parameter lastCheckedIndex[%d] is greater than or equal to the maxBitNumber[%d]", lastCheckedIndex, maxBitNumber);
			
			throw new IllegalArgumentException(errorMessage);
		}
		
		this.maxBitNumber = maxBitNumber;
		
		this.workingLastCheckedIndex = this.lastCheckedIndex = lastCheckedIndex;
		
		log.info("maxBitNumber[{}], lastCheckedIndex=workingLastCheckedIndex=[{}]", maxBitNumber, lastCheckedIndex);
	}
	
	
	private boolean isDuplicated(long bitIndex) {
		boolean isDuplecated = (bitIndex <= workingLastCheckedIndex);
		
		return isDuplecated;
	}


	private void throwExceptionIfIndexOutOfBound(Long bitIndex) throws IndexOutOfBoundsException {
		if (bitIndex < 0) {
			String errorMessage = String.format("the parameter bitIndex[%d] is less than zero", bitIndex);
			throw new IndexOutOfBoundsException(errorMessage);
		}
		
		if (bitIndex >= maxBitNumber) {
			String errorMessage = String.format("the parameter bitIndex[%d] is greater than or equal to the maxBitNumber[%d]", bitIndex, maxBitNumber);
			throw new IndexOutOfBoundsException(errorMessage);
		}
		
		if (bitIndex <= lastCheckedIndex) {
			String errorMessage = String.format("the parameter bitIndex[%d] is less than or equal to the lastCheckedIndex[%d]", bitIndex, lastCheckedIndex);
			throw new IndexOutOfBoundsException(errorMessage);
		}
	}


	/**
	 * if the parameter bitIndex is not a next bit set index, then throw BadBitSetIndexException 
	 * @param bitIndex
	 * @throws BadBitSetIndexException
	 */
	private void throwExceptionIfNotNextBitSetIndex(long bitIndex) throws BadBitSetIndexException {
		if ((workingLastCheckedIndex+1) != bitIndex) {
			String errorMessge = String.format("the parameter bitIndex[%d] is not a next bit index[%d]", bitIndex, workingLastCheckedIndex+1);
			throw new BadBitSetIndexException(errorMessge);
		}
	}


	@SuppressWarnings("serial")
	public class FailedListFullException extends Exception {
		public FailedListFullException(String errorMessage) {
			super(errorMessage);
		}
	}
	
	
	
	@SuppressWarnings("serial")
	public class BadBitSetIndexException extends Exception {
		public BadBitSetIndexException(String errorMessage) {
			super(errorMessage);
		}
	}
	
	
	/**
	 * Sets the bit at the specified index to true.
	 */
	public void set(long bitIndex) throws IndexOutOfBoundsException, BadBitSetIndexException {		
		throwExceptionIfIndexOutOfBound(bitIndex);
		
		
		synchronized (monitor) {
			/**
			 * 작업 결과가 에러시 재 시도후 다시 성공으로 변경될 경우 처리
			 */
		
			if (isDuplicated(bitIndex)) {
				
					if (! failedIndexQueue.contains(bitIndex)) {				
						String errorMessge = String.format("the parameter bitIndex[%d] is an already processed bit index, it is less than or equalst to workingLastCheckedIndex[%d]", bitIndex, workingLastCheckedIndex);
						throw new BadBitSetIndexException(errorMessge);
					}			
					
					failedIndexQueue.remove(bitIndex);
				
				log.info("the paramter bitIndex[{}] is removed at failedIndexList because of success", bitIndex);
			} else {
				throwExceptionIfNotNextBitSetIndex(bitIndex);
				workingLastCheckedIndex = bitIndex;
				// rebuildLastCheckedIndexAndWaitingIndexList(bitIndex);
			}
		}
		
		
	}

	/**
	 * Sets the bit specified by the index to false.
	 */
	public void clear(long bitIndex) throws IndexOutOfBoundsException, FailedListFullException, BadBitSetIndexException {
		throwExceptionIfIndexOutOfBound(bitIndex);		
		
		synchronized (monitor) {
			throwExceptionIfNotNextBitSetIndex(bitIndex);
			
			int failedIndexListSize = failedIndexQueue.size();
			if (failedIndexListSize >= maxSizeOfFailedIndexList) {
				String errorMessage = String.format("error::when the bitIndex[%d] is processed, the size of failedIndexList[%d] has rearched its maximum value[%d]", bitIndex, failedIndexListSize, maxSizeOfFailedIndexList);
				throw new FailedListFullException(errorMessage);
			}
			
			
			workingLastCheckedIndex = bitIndex;				
			
			
			failedIndexQueue.add(bitIndex);
		}
		
		log.info("add bitIndex[{}] to failedIndexList", bitIndex);
		
	}
	
	public boolean get(long bitIndex) throws IndexOutOfBoundsException {
		throwExceptionIfIndexOutOfBound(bitIndex);
		
		synchronized (monitor) {
			boolean isSuccess = ((bitIndex <= workingLastCheckedIndex) && !failedIndexQueue.contains(bitIndex));
			/*
			if (bitIndex <= workingLastCheckedIndex) {
				isSuccess  = !failedIndexList.contains(bitIndex);
			} else {
				isSuccess = false;
			}*/	
			
			return isSuccess;
		}		
	}

		

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (!(o instanceof LimitedLongBitSet)) {
			return false;
		}
		LimitedLongBitSet other = (LimitedLongBitSet) o;
		if (maxBitNumber != other.maxBitNumber) {
			return false;
		}
		if (lastCheckedIndex != other.lastCheckedIndex) {
			return false;
		}
		
		if (workingLastCheckedIndex != other.workingLastCheckedIndex) {
			return false;
		}
		
		if (failedIndexQueue.equals(other.failedIndexQueue)) {
			return false;
		}
				
		return true;
	}
	
	public int hashCode() {
		return monitor.hashCode();
	}
	
	public long cardinality() {
		long checkedIndexCount = workingLastCheckedIndex + 1;
		
		
		return checkedIndexCount - failedIndexQueue.size();
	}
	
	public final long getLastCheckedIndex() {
		return lastCheckedIndex;
	}
	
	public final long getWorkingLastCheckedIndex() {
		return workingLastCheckedIndex;
	}
	
	public final long getMaxBitNumber() {
		
		return maxBitNumber;
	}
	
	
}
