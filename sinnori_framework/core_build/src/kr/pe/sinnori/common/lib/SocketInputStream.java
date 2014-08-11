/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package kr.pe.sinnori.common.lib;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.CharsetDecoder;
import java.util.ArrayList;

import kr.pe.sinnori.common.exception.NoMoreDataPacketBufferException;
import kr.pe.sinnori.common.io.FreeSizeInputStream;
import kr.pe.sinnori.common.protocol.MessageProtocolIF;

/**
 * <pre>
 * 소켓에 1:1로 할당되는 메시지 입력 스트림 클래스.
 * 주요 자료 구조로 2가지를 갖는다.
 * 첫번째 소켓 읽기를 할때 가상의 입력 스트림을 구현하여 필요한  메시지 내용을 담을 데이터 패킷 버퍼 목록
 * 두번째 메시지 처리 중 필요한 상태 정보를 갖는 부과 정보. 
 *       예) DHB 프로토콜의 경우 DHB 헤더 정보. 
 * 
 * 상태 정보를 갖는 부과 정보를 두는 이유는 메시지를 완성 시킬때까지 유지할 상태 값이 필요하기때문이다. 
 * 예를 들면 DHB 프로토콜의 경우 부과 정보에 저장된 DHB 헤더가 없을 경우 
 * DHB 헤더를 작성하여 부과 정보에 저장한다.
 * 이후 데이터를 받을때 마다 부과 정보에 있는 DHB 헤더 정보를 바탕으로
 * 메시지를 다 받았는지 여부를 판단하여 메시지를 추출한다.
 *  
 * 참고) 소켓에 1:1로 할당되는 메시지 입력 스트림 자원 클래스가 가지고 있는 2개 자원의 실질적인 운영은 
 *       메시지 교환 프로토콜을 구현하는 스트림 변환기 {@link MessageProtocolIF} 에서 한다. 
 * </pre>
 * 
 * @author Jonghoon Won
 *
 */
public class SocketInputStream implements CommonRootIF {
	/** 소켓 채널 전용 출력 메시지 읽기 전용 버퍼 목록, 참고) 언제나 읽기가 가능 하도록 최소 크기가 1이다. */
	private ArrayList<WrapBuffer> dataPacketBufferList = new  ArrayList<WrapBuffer>();
	/** 메시지를 추출시 생기는 부가 정보를  */
	private Object userDefObject = null;
	
	private DataPacketBufferQueueManagerIF dataPacketBufferQueueManager = null;
	//private ByteOrder byteOrderOfProject = null;
	
	private int dataPacketBufferMaxCntPerMessage = -1;
	
	private ByteBuffer lastByteBuffer = null;
	private ByteBuffer firstByteBuffer = null;
	
	// FIXME! S2MList 걸린 시간 로그 남기기
	//private long startTime = 0L;
	
	/**
	 * 생성자
	 * @param dataPacketBufferQueueManager 데이터 패킷 버퍼 큐 관리자
	 * @throws NoMoreDataPacketBufferException 데이터 패킷 버퍼 확보 실패시 던지는 예외
	 */
	public SocketInputStream(DataPacketBufferQueueManagerIF dataPacketBufferQueueManager) throws NoMoreDataPacketBufferException {
		
		this.dataPacketBufferQueueManager = dataPacketBufferQueueManager;
		//this.byteOrderOfProject = dataPacketBufferQueueManager.getByteOrder();
		this.dataPacketBufferMaxCntPerMessage = dataPacketBufferQueueManager.getDataPacketBufferMaxCntPerMessage();
		
		WrapBuffer lastWrapBuffer = dataPacketBufferQueueManager.pollDataPacketBuffer();
		dataPacketBufferList.add(lastWrapBuffer);
		
		lastByteBuffer = firstByteBuffer = lastWrapBuffer.getByteBuffer();
	}
	
	
	/**
	 * 가상 IO 테스트를 지원하기 위한 생성자. 실제로 데이터를 받는것이 아닌 미리 받은 메시지 랩버퍼 목록으로 부터 
	 * @param messageReadWrapBufferList
	 * @param dataPacketBufferQueueManager
	 * @throws NoMoreDataPacketBufferException
	 */
	public SocketInputStream(ArrayList<WrapBuffer> messageReadWrapBufferList,
			DataPacketBufferQueueManagerIF dataPacketBufferQueueManager) throws NoMoreDataPacketBufferException {
		this.dataPacketBufferQueueManager = dataPacketBufferQueueManager;
		// this.byteOrderOfProject = dataPacketBufferQueueManager.getByteOrder();
		this.dataPacketBufferMaxCntPerMessage = dataPacketBufferQueueManager.getDataPacketBufferMaxCntPerMessage();
		this.dataPacketBufferList = messageReadWrapBufferList;
		
		firstByteBuffer = dataPacketBufferList.get(0).getByteBuffer();
		lastByteBuffer = dataPacketBufferList.get(dataPacketBufferList.size()-1).getByteBuffer();	
	}
	
	
	public long position() {
		long lastIndexOfDataPacketBufferList = dataPacketBufferList.size() - 1;
		return lastIndexOfDataPacketBufferList *  lastByteBuffer.capacity() + lastByteBuffer.position();
	}
	
	
	public ByteOrder getByteOrder() {
		return dataPacketBufferQueueManager.getByteOrder();
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
	
	
	/**
	 * 클라이언트 연결 클래스의 소켓이 닫혔을 경우 호출 되는 초기화 함수. 1개 남은 바이트 버퍼의 속성값은 모두 초기화(=clear) 된다.
	 */
	public void initResource() {
		int dataPacketBufferListSizeWithoutLastBuffer = dataPacketBufferList.size() - 1;
		for (int i=0; i < dataPacketBufferListSizeWithoutLastBuffer; i++) {
			WrapBuffer workWrapBuffer = dataPacketBufferList.remove(0);
			dataPacketBufferQueueManager.putDataPacketBuffer(workWrapBuffer);
		}
		
		lastByteBuffer = firstByteBuffer = dataPacketBufferList.get(0).getByteBuffer();
		
		userDefObject = null;
	}
	
	/**
	 * <pre>
	 * 모든 읽기 전용 버퍼를 삭제한다. 
	 * 읽기 전용 버퍼는 최소 1개를 갖는것을 전제로 로직이 세워져서
	 * 이 메소드 호출 후 메시지 입력 스트림 자원을 운영하면 에러가 발생할 것이다.
	 * 오직 더 이상 읽기 전용 버퍼를 운영할 수 없을때만 호출해야 한다.
	 * 더 이상 읽기 전용 버퍼를 운영할 수 없는 경우를 예를 들면 
	 * 클라이언트 연결 클래스라 정상적으로 폴에 반환되지 않아 가비지로 빠진 경우 혹은
	 * 서버에서 소켓이 끊어져 자원 회수를 하는 경우가 있다.
	 * </pre>   
	 */
	public void destory() {
		// log.info("call destory");
		
		int outputMessageWrapBufferListSize = dataPacketBufferList.size();
		for (int j=0; j < outputMessageWrapBufferListSize ; j++) {
			WrapBuffer outputMessageWrapBuffer = dataPacketBufferList.get(0);
			dataPacketBufferList.remove(0);
			dataPacketBufferQueueManager.putDataPacketBuffer(outputMessageWrapBuffer);
		}
		firstByteBuffer = null;
		lastByteBuffer = null;
	}
	
	/** 
	 * @return 메시지 데이터 수신중 여부, true 이면 메시지 데이터 수신중, false 이면 메시지 데이터 수신 대기중
	 */
	public boolean isReading() {
		if (null == userDefObject && 0 == firstByteBuffer.position()) return false;
		
		return true;
	}
	
	
	/**
	 * 지정된 인덱스까지 데이터 패킷 랩 버퍼를 삭제하며 <br/>
	 * 동시에 삭제된 데이터 패킷 랩 버퍼를 데이터 패킷 랩 버퍼 관리자에 반환한다.<br/> 
	 * 예를 들면 1이라고 하면 인덱스 0과 인덱스 1 이렇게 2개가 삭제되며 <br/>
	 * 동시에 삭제된 버퍼는 데이터 패킷 랩 버퍼 관리자에 반환된다.<br/>
	 * 
	 * @param startIndex 메시지 삭제후 최종 시작 버퍼의 인덱스
	 * @param startPosition 메시지 삭제후 최종 시작 버퍼의 위치
	 */
	public void truncate(int startIndex, int startPosition) {
		int dataPacketBufferListSize = dataPacketBufferList.size();
		
		if (startIndex < 0) {
			String errorMessage = String.format("parameter startIndex[%d] less than zero", startIndex);
			throw new IllegalArgumentException(errorMessage);
		}
		
		if (startIndex >= dataPacketBufferListSize) {
			String errorMessage = String.format("parameter startIndex[%d] equal to or greater than WrapBufferList'size[%d]", startIndex, dataPacketBufferListSize);
			throw new IllegalArgumentException(errorMessage);
		}
		
		if (startPosition < 0) {
			String errorMessage = String.format("parameter startPosition[%d] less than zero", startPosition);
			throw new IllegalArgumentException(errorMessage);
		}
		
		/** 첫번째 버퍼를 startIndex 로 변경 */
		firstByteBuffer = dataPacketBufferList.get(startIndex).getByteBuffer();
		//log.info(String.format("1.in truncate, firstByteBuffer=[%s]", firstByteBuffer.toString()));
		
		/** 첫번째 버퍼의 읽어온 데이터의 크기 */
		int endPosition = firstByteBuffer.position();
		
		if (startPosition > endPosition) {
			String errorMessage = String.format("parameter startPosition[%d] greater than endPosition[%d]", startPosition, endPosition);
			throw new IllegalArgumentException(errorMessage);
		}
		
		
		/** 첫번째 버퍼의 읽어온 데이터를 첫 시작 위치로 밀어 올린다. */
		firstByteBuffer.limit(endPosition);
		firstByteBuffer.position(startPosition);
		firstByteBuffer.compact();
		//log.info(String.format("2.in truncate, firstByteBuffer=[%s]", firstByteBuffer.toString()));
		
		/** startIndex 이전 버퍼 삭제(=반환) */
		for (int i=0; i < startIndex; i++) {
			WrapBuffer workWrapBuffer = dataPacketBufferList.remove(0);
			dataPacketBufferQueueManager.putDataPacketBuffer(workWrapBuffer);
			dataPacketBufferListSize--;
		}
		
		int flipBufferListSize = dataPacketBufferListSize - 1;
		for (int i=0; i < flipBufferListSize ; i++) {
			ByteBuffer currentByteBuffer = dataPacketBufferList.get(i).getByteBuffer();
			ByteBuffer nextByteBuffer = dataPacketBufferList.get(i+1).getByteBuffer();
			nextByteBuffer.flip();
			
			int currentRemainingBytes = currentByteBuffer.remaining();
			int nextRemainingBytes = nextByteBuffer.remaining();
			if (currentRemainingBytes < nextRemainingBytes) {
				/** truncate 한후, flip를 하는 과정에서 마지막 버퍼의 내용이 남은 채로 이전 버퍼로 이동 **/
				
				// log.info(String.format("1.dataPacketBufferList[%d] currentRemainingBytes=[%d], nextRemainingBytes=[%d]", i, currentRemainingBytes, nextRemainingBytes));
				
				int backupNextLimit = nextByteBuffer.limit();
				nextByteBuffer.limit(nextByteBuffer.position()+currentRemainingBytes);
				currentByteBuffer.put(nextByteBuffer);				
				nextByteBuffer.limit(backupNextLimit);
				nextByteBuffer.compact();
			} else {
				/** truncate 한후, flip를 하는 과정에서 마지막 버퍼의 내용이 모두 이전 버퍼로 이동 **/
				
				// log.info(String.format("2.dataPacketBufferList[%d] currentRemainingBytes=[%d], nextRemainingBytes=[%d]", i, currentRemainingBytes, nextRemainingBytes));
				
				// FIXME!
				/*if (i+1 != flipBufferListSize) {
					log.warn(String.format("다음 버퍼의 모든것이 이동될때에는 오직 다음 버퍼가 마지막 버퍼일때뿐이다. 마지막 버퍼 인덱스[%d]와 다음 버퍼 인덱스[%d] 불일치"
							, flipBufferListSize, i+1));
				}*/
				
				currentByteBuffer.put(nextByteBuffer);
				
				/** 마지막 랩 버퍼를 현재 버퍼로 변경 */
				lastByteBuffer = currentByteBuffer;
				
				/** 마지막 랩 버퍼 반환 */
				WrapBuffer lastWrapBuffer = dataPacketBufferList.get(flipBufferListSize);
				dataPacketBufferQueueManager.putDataPacketBuffer(lastWrapBuffer);
				dataPacketBufferList.remove(i+1);
				dataPacketBufferListSize--;
			}
		}
		
		// FIXME! 지정된 구간 삭제후 남은 데이터 패킷 목록 점검을 위한 디버깅 코드
		/*int size = dataPacketBufferList.size();
		if (dataPacketBufferListSize != size) {
			log.warn(String.format("different dataPacketBufferListSize=[%d] to dataPacketBufferList.size[%d]", dataPacketBufferListSize, size));
		}		
		for (int i=0; i < dataPacketBufferListSize; i++) {
			WrapBuffer workWrapBuffer = dataPacketBufferList.get(i);
			ByteBuffer workByteBuffer = workWrapBuffer.getByteBuffer();
			if (workByteBuffer.position() != workByteBuffer.capacity()) {
				log.warn(String.format("workByteBuffer[%d][%s]", i, workByteBuffer.toString()));
			}
		}*/
		
		// FIXME!
		/*long endTime = System.currentTimeMillis();		
		log.info(String.format("elapsed time=[%s]", endTime - startTime));
		startTime = endTime;*/
	}
	
	public ByteBuffer getLastDataPacketBuffer() {
		// return dataPacketBufferList.get(dataPacketBufferList.size()-1).getByteBuffer();
		return lastByteBuffer;
	}
	
	/**
	 * @return 데이터 패킷 버퍼 추가 가능 여부
	 */
	public boolean canNextDataPacketBuffer() {
		return (dataPacketBufferMaxCntPerMessage != dataPacketBufferList.size());
	}
	
	public ByteBuffer nextDataPacketBuffer() throws NoMoreDataPacketBufferException {
		if (!canNextDataPacketBuffer()) {
			String errorMessage = String
					.format("메시지당 최대 데이터 패킷 갯수[%d]을 넘을 수 없습니다.",
							dataPacketBufferMaxCntPerMessage);
			throw new NoMoreDataPacketBufferException(
					errorMessage);
		}
		
		WrapBuffer lastWrapBuffer = dataPacketBufferQueueManager.pollDataPacketBuffer(); 
		dataPacketBufferList.add(lastWrapBuffer);
		lastByteBuffer = lastWrapBuffer.getByteBuffer();
		return lastByteBuffer;
	}
	
	public int getDataPacketBufferListSize() {
		return dataPacketBufferList.size();
	}
	
	/**
	 * 호출되는 시점 기준으로 소켓 종속 읽기 전용 스트림을 반환한다.
	 * 
	 * 주의점) 소켓 종속 읽기 전용 스트림은 데이터 패킷 버퍼 목록으로 구현한다.
	 * 이 메소드는 내부적으로 데이터 패킷 버퍼 목록을 읽기 가능상태로 복사하여 
	 * 가변 스트림을 구성한것이기때문에 스트림의 조작및 스트림이 가진 데이터 패킷 버퍼 목록의 조작은 
	 * 원본 데이터 패킷 버퍼 목록에 아무런 영향을 주지 않는다.
	 * 
	 * @param charsetDecoderOfProject 프로젝트의 문자셋을 갖는 디코더
	 * @return 호출되는 시점 기준으로 소켓 종속 읽기 전용 스트림
	 */
	public FreeSizeInputStream getFreeSizeInputStream(CharsetDecoder charsetDecoderOfProject) {
		
		/*int dataPacketBufferListSize = dataPacketBufferList.size();
		
		ArrayList<ByteBuffer> streamBufferList = new ArrayList<ByteBuffer>(dataPacketBufferListSize);
		
		for (int i=0; i < dataPacketBufferListSize; i++) {
			ByteBuffer dupPacketBuffer = dataPacketBufferList.get(i).getByteBuffer().duplicate();
			dupPacketBuffer.order(byteOrderOfProject);
			dupPacketBuffer.flip();
			streamBufferList.add(dupPacketBuffer);
		}*/
		
		FreeSizeInputStream freeSizeInputStream = 
				new FreeSizeInputStream(dataPacketBufferList, CommonType.WRAPBUFFER_RECALL_GUBUN.WRAPBUFFER_RECALL_NO, charsetDecoderOfProject, dataPacketBufferQueueManager);
		
		return freeSizeInputStream;
	}
}
