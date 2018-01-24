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
package kr.pe.sinnori.common.protocol;

import java.util.ArrayList;
import java.util.List;

import kr.pe.sinnori.common.exception.BodyFormatException;
import kr.pe.sinnori.common.exception.HeaderFormatException;
import kr.pe.sinnori.common.exception.NoMoreDataPacketBufferException;
import kr.pe.sinnori.common.io.SocketOutputStream;
import kr.pe.sinnori.common.io.WrapBuffer;
import kr.pe.sinnori.common.message.AbstractMessage;
import kr.pe.sinnori.common.message.codec.AbstractMessageEncoder;

/**
 * 메시지 프로토콜 인터페이스.
 * 
 * @author Won Jonghoon
 *
 */
public interface MessageProtocolIF {
	
	/**
	 * 메시지를 데이터 패킷 버퍼로 구현한 스트림으로 변환한다.
	 * @param messageObj 스트림으로 변환을 원하는 메시지
	 * @param clientCharset 문자셋
	 * @return 메시지 내용이 담긴 데이터 패킷 버퍼 목록
	 * @throws NoMoreDataPacketBufferException 데이터 패킷 버퍼가 없을때 던지는 예외
	 * @throws BodyFormatException 바디 구성할때 에러 발생시 던지는 예외
	 */
	public List<WrapBuffer> M2S(AbstractMessage messageObj, AbstractMessageEncoder messageEncoder) 
			throws NoMoreDataPacketBufferException, BodyFormatException, HeaderFormatException;
	
	
	/**
	 * <pre>
	 *  읽기 전용 버퍼 목록으로 부터 지정된 메시지 클래스를 추출하여 목록을 구성 후 반환한다.
	 * 역자주) 속도가 떨어지지만 코드 중복을 없애기 위해서 하나로 통합함.
	 * </pre>
	 * 
	 * @param serverClientGubun 서버 클라이언트 구분
	 * @param clientCharset 문자셋
	 * @param socketOutputStream 메시지 입력 스트림 자원
	 * @return 읽기 전용 버퍼 목록으로 부터 추출한 메시지 목록, IOMode 가 true이면 입력 메시지 목록, false 이면 출력 메시지 목록이 된다.
	 * @throws HeaderFormatException 헤더 포맷 구성시 에러 발생시 던지는 예외
	 * @throws NoMoreDataPacketBufferException 데이터 패킷 버퍼가 없을때 던지는 예외
	 */
	public ArrayList<WrapReadableMiddleObject> S2MList(SocketOutputStream socketOutputStream) 
					throws HeaderFormatException, NoMoreDataPacketBufferException;
	
	public SingleItemDecoderIF getSingleItemDecoder();
	
	public int getMessageHeaderSize();
}

