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
import kr.pe.sinnori.common.io.MessageProtocolIF;

/**
 * <pre>
 * 소켓에 1:1로 할당되는 메시지 입력 스트림 자원 클래스.
 * 자원은 2가지이다.
 * 첫번째 소켓 읽기를 할때 필요한  메시지 내용을 담을 데이터 패킷 버퍼 목록
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
public class MessageInputStreamResourcePerSocket implements CommonRootIF {
	/** 소켓 채널 전용 출력 메시지 읽기 전용 버퍼 목록, 참고) 언제나 읽기가 가능 하도록 최소 크기가 1이다. */
	private ArrayList<WrapBuffer> dataPacketBufferList = new  ArrayList<WrapBuffer>();
	/** 메시지를 추출시 생기는 부가 정보를  */
	private Object userDefObject = null;
	
	private DataPacketBufferQueueManagerIF dataPacketBufferQueueManager = null;
	private ByteOrder byteOrderOfProject = null;
	
	private int dataPacketBufferMaxCntPerMessage = -1;
	
	private ByteBuffer lastByteBuffer = null;
	private ByteBuffer firstByteBuffer = null;
	
	/**
	 * 생성자
	 * @param dataPacketBufferQueueManager 데이터 패킷 버퍼 큐 관리자
	 * @throws NoMoreDataPacketBufferException 데이터 패킷 버퍼 확보 실패시 던지는 예외
	 */
	public MessageInputStreamResourcePerSocket(DataPacketBufferQueueManagerIF dataPacketBufferQueueManager) throws NoMoreDataPacketBufferException {
		
		this.dataPacketBufferQueueManager = dataPacketBufferQueueManager;
		this.byteOrderOfProject = dataPacketBufferQueueManager.getByteOrder();
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
	public MessageInputStreamResourcePerSocket(ArrayList<WrapBuffer> messageReadWrapBufferList,
			DataPacketBufferQueueManagerIF dataPacketBufferQueueManager) throws NoMoreDataPacketBufferException {
		this.dataPacketBufferQueueManager = dataPacketBufferQueueManager;
		this.dataPacketBufferList = messageReadWrapBufferList;
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
	 * @return 메시지 내용을 담고 있는 데이터 패킷 버퍼 큐 목록
	 */
	/*
	public ArrayList<WrapBuffer> getMessageReadWrapBufferList() {
		return wrapBufferList;
	}
	*/
	
	/**
	 * 클라이언트 연결 클래스의 소켓이 닫혔을 경우 호출 되는 초기화 함수. 1개 남은 바이트 버퍼의 속성값은 모두 초기화(=clear) 된다.
	 */
	public void initResource() {
		int dataPacketBufferListSize = dataPacketBufferList.size() - 1;
		for (int i=0; i < dataPacketBufferListSize; i++) {
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
	 * 다음 메시지를 받기 위한 마지막 데이터 패킷 버퍼를 제외한<br/>
	 * 추출된 메시지의 내용을 담은 데이터 패킷 버퍼를 데이터 패킷 버퍼 큐 관리자한테 반환한다.
	 * 
	 * @param endPositionOfMessage 마지막 데이터 패킷 버퍼에서 메시지가 끝나는 지점
	 * @param finalReadPosition 마지막 데이터 패킷 버퍼의 최종 데이터 수신 위치
	 */
	/*public void freeWrapBufferWithoutLastBuffer(int endPositionOfMessage, int finalReadPosition) {
		ByteBuffer lastInputStreamBuffer = getLastDataPacketBuffer();
		
		*//** 추출된 메시지 영역 삭제 - 마지막 출력 메시지 랩 버퍼에 존재하는 잔존 데이터를 처음 위치로 이동 시킨후 다음 데이터 읽기 준비를 한다. *//*
		lastInputStreamBuffer.position(endPositionOfMessage);
		lastInputStreamBuffer.limit(finalReadPosition);
		lastInputStreamBuffer.compact();
		
		int outputMessageWrapBufferListSize = wrapBufferList.size();
		for (int j=1; j < outputMessageWrapBufferListSize ; j++) {
			WrapBuffer outputMessageWrapBuffer = wrapBufferList.get(0);
			wrapBufferList.remove(0);
			dataPacketBufferQueueManager.putDataPacketBuffer(outputMessageWrapBuffer);
		}
		
	}*/
	
	/**
	 * 지정된 인덱스까지 데이터 패킷 랩 버퍼를 삭제하며 <br/>
	 * 동시에 삭제된 데이터 패킷 랩 버퍼를 데이터 패킷 랩 버퍼 관리자에 반환한다.<br/> 
	 * 예를 들면 1이라고 하면 인덱스 0과 인덱스 1 이렇게 2개가 삭제되며 <br/>
	 * 동시에 삭제된 버퍼는 데이터 패킷 랩 버퍼 관리자에 반환된다.<br/>
	 * 
	 * @param startIndex 메시지 삭제후 최종 시작 버퍼의 인덱스
	 * @param startPostion 메시지 삭제후 최종 시작 버퍼의 위치
	 */
	public void truncate(int startIndex, int startPostion) {
		if (startIndex < 0) {
			String errorMessage = String.format("parameter startIndex[%d] less than zero", startIndex);
			throw new IllegalArgumentException(errorMessage);
		}
		
		if (startIndex >= dataPacketBufferList.size()) {
			String errorMessage = String.format("parameter startIndex[%d] equal to or greater than WrapBufferList'size[%d]", startIndex, dataPacketBufferList.size());
			throw new IllegalArgumentException(errorMessage);
		}
		
		if (startPostion < 0) {
			String errorMessage = String.format("parameter startPostion[%d] less than zero", startPostion);
			throw new IllegalArgumentException(errorMessage);
		}
		
		firstByteBuffer = dataPacketBufferList.get(startIndex).getByteBuffer();
		firstByteBuffer.position(startPostion);
		firstByteBuffer.compact();
		/** 첫번째 버퍼의 남은 공간을 채우기 위한 버퍼 속성 조정 */
		firstByteBuffer.position(firstByteBuffer.limit());
		firstByteBuffer.limit(firstByteBuffer.capacity());
		
		for (int i=0; i < startIndex; i++) {
			WrapBuffer workWrapBuffer = dataPacketBufferList.remove(0);
			dataPacketBufferQueueManager.putDataPacketBuffer(workWrapBuffer);
		}
		
		int flipBufferListSize = dataPacketBufferList.size() - 1;
		for (int i=0; i < flipBufferListSize ; i++) {
			ByteBuffer currentByteBuffer = dataPacketBufferList.get(i).getByteBuffer();
			ByteBuffer nextByteBuffer = dataPacketBufferList.get(i+1).getByteBuffer();
			nextByteBuffer.flip();
			nextByteBuffer.put(currentByteBuffer);
		}
		
		lastByteBuffer = dataPacketBufferList.get(flipBufferListSize).getByteBuffer();
		lastByteBuffer.compact();
		/** 마지막 버퍼의 남은 공간을 채우기 위한 버퍼 속성 조정 */
		lastByteBuffer.position(lastByteBuffer.limit());
		lastByteBuffer.limit(lastByteBuffer.capacity());
	}
	
	/**
	 * @return 데이터 패킷 버퍼 추가 가능 여부
	 */
	public boolean canNextDataPacketBuffer() {
		return (dataPacketBufferMaxCntPerMessage != dataPacketBufferList.size());
	}
	
	/**
	 * 
	 * @param index 얻고자 하는 데이터 패킷 버퍼 목록 상의 위치
	 * @return 패킷 버퍼목록에서 지정된 위치를 갖는 데이터 패킷 버퍼
	 */
	public ByteBuffer getDataPacketBuffer(int index) {		
		if (index < 0) {
			String errorMessage = String.format("parameter index[%d] less than zero", index);
			throw new IllegalArgumentException(errorMessage);
		}
		
		if (index >= dataPacketBufferList.size()) {
			String errorMessage = String.format("parameter index[%d] equal to or greater than WrapBufferList'size[%d]", index, dataPacketBufferList.size());
			throw new IllegalArgumentException(errorMessage);
		}
		
		int dataPacketBufferListSize = dataPacketBufferList.size();
		
		if (index >= dataPacketBufferListSize) {
			String errorMessage = String.format("parameter index[%d] over than the WrapBufferList' size[%d]", index, dataPacketBufferListSize);
			throw new IllegalArgumentException(errorMessage);
		}
		
		
		return dataPacketBufferList.get(index).getByteBuffer();
	}
	
	public ByteBuffer getFirstDataPacketBuffer() {
		return dataPacketBufferList.get(0).getByteBuffer();
	}
	
	public ByteBuffer getLastDataPacketBuffer() {
		return dataPacketBufferList.get(dataPacketBufferList.size()-1).getByteBuffer();
	}
	
	public ByteBuffer nextDataPacketBuffer() throws NoMoreDataPacketBufferException {
		/*
		if (!canNextDataPacketBuffer()) {
			String errorMessage = String
					.format("메시지당 최대 데이터 패킷 갯수[%d]을 넘을 수 없습니다.",
							dataPacketBufferMaxCntPerMessage);
			throw new NoMoreDataPacketBufferException(
					errorMessage);
		}
		*/
		WrapBuffer lastWrapBuffer = dataPacketBufferQueueManager.pollDataPacketBuffer(); 
		dataPacketBufferList.add(lastWrapBuffer);
		lastByteBuffer = lastWrapBuffer.getByteBuffer();		
		return lastByteBuffer;
	}
	
	public int getDataPacketBufferListSize() {
		return dataPacketBufferList.size();
	}
	
	public FreeSizeInputStream getFreeSizeInputStream(CharsetDecoder charsetOfProjectDecoder) {
		int dataPacketBufferListSize = dataPacketBufferList.size();
		
		ArrayList<ByteBuffer> streamBufferList = new ArrayList<ByteBuffer>(dataPacketBufferListSize);
		for (int i=0; i < dataPacketBufferListSize; i++) {
			ByteBuffer dupPacketBuffer = dataPacketBufferList.get(i).getByteBuffer().duplicate();
			dupPacketBuffer.order(byteOrderOfProject);
			dupPacketBuffer.flip();
			streamBufferList.add(dupPacketBuffer);
		}
		
		FreeSizeInputStream freeSizeInputStream = 
				new FreeSizeInputStream(streamBufferList, charsetOfProjectDecoder, dataPacketBufferMaxCntPerMessage);		
		return freeSizeInputStream;
	}
	
	public void flipAllBuffer() {
		int dataPacketBufferListSize = dataPacketBufferList.size();
		for (int i=0; i < dataPacketBufferListSize; i++) {
			dataPacketBufferList.get(i).getByteBuffer().flip();
		}
	}
}
