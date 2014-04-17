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


package kr.pe.sinnori.common.io.dhb;

import java.nio.BufferOverflowException;
import java.nio.charset.Charset;
import java.util.HashMap;

import kr.pe.sinnori.common.exception.BodyFormatException;
import kr.pe.sinnori.common.exception.NoMoreDataPacketBufferException;
import kr.pe.sinnori.common.exception.SinnoriBufferUnderflowException;
import kr.pe.sinnori.common.exception.SinnoriCharsetCodingException;
import kr.pe.sinnori.common.io.InputStreamIF;
import kr.pe.sinnori.common.io.OutputStreamIF;
import kr.pe.sinnori.common.message.ArrayInfo;
import kr.pe.sinnori.common.message.ItemTypeManger;

/**
 * 단일 항목 스트림 변환기 인터페이스<br/>
 * 이 인터페이스는 프로토콜 별로 구현된다.
 * @author Jonghoon Won
 *
 */
public interface DHBSingleItem2StreamIF {
	
	/**
	 * <pre>
	 * 항목 정보와 항목의 값을 스트림에 저장한다. 
	 * 주) 메시지  값을 저장할때 값의 타입 정보와 항목의 타입 정보의 일치성 여부를 따져서 저장한다.
	 *     따라서 이곳에서는 값과 항목 타입의 일치성 여부를 검사하지 않는다.
	 * </pre>
	 * 
	 * @param itemName 항목 이름
	 * @param itemTypeID 항목 타입 식별자, 항목 타입 관리자{@link ItemTypeManger} 를 통해 관리 된다.
	 * @param itemValue 항목 값
	 * @param itemSizeForLang 언어에 특화된 부가 정보중 하나인 항목 크기
	 * @param itemCharsetForLang 언어에 특화된 부가 정보중 하나인 문자셋
	 * @param sw 출력 스트림
	 * @throws BodyFormatException 항목 쓰기 실패시 던지는 예외
	 * @throws IllegalArgumentException 잘못된 파라미터 넣었을 경우 던지는 예외
	 * @throws BufferOverflowException 버퍼 크기를 넘어선 쓰기 시도시 던지는 예외
	 * @throws NoMoreDataPacketBufferException 데이터 패킷 버퍼 확보 실패시 던지는 예외
	 */
	public void I2S(String itemName, int itemTypeID, 
			Object itemValue, int itemSizeForLang, Charset itemCharsetForLang, OutputStreamIF sw)
			throws BodyFormatException, IllegalArgumentException, BufferOverflowException, NoMoreDataPacketBufferException;
	
	/**
	 * 항목정보를 바탕으로 스트림에서 항목의 값을 읽어 항목값 해쉬에 저장한다. 항목값 해쉬의 키는 항목명, 값은 항목의 값이다. 
	 * @param itemName 항목 이름
	 * @param itemTypeID 항목 타입 식별자, 항목 타입 관리자{@link ItemTypeManger} 를 통해 관리 된다.
	 * @param itemSizeForLang 언어에 특화된 부가 정보중 하나인 항목 크기
	 * @param itemCharsetForLang 언어에 특화된 부가 정보중 하나인 문자셋
	 * @param itemValueHash 항목명 해쉬, 키는 항목명, 값은 항목의 값이다.
	 * @param sr 입력 스트림
	 * @throws SinnoriCharsetCodingException 문자셋 디코딩시 에러 발생시 던지는 예외
	 * @throws SinnoriBufferUnderflowException 버퍼 크기를 넘어선 읽기 시도시 던지는 예외
	 * @throws IllegalArgumentException 잘못된 파라미터 넣었을 경우 던지는 예외
	 */
	public void S2I(String itemName, int itemTypeID, 
			int itemSizeForLang, Charset itemCharsetForLang, 
			HashMap<String, Object> itemValueHash, InputStreamIF sr)
			throws SinnoriCharsetCodingException, SinnoriBufferUnderflowException, IllegalArgumentException, BodyFormatException;
	
	/**
	 * 그룹 시작을 알리는 내용을 출력 스트림에 저장한다. 참고) DHB는 아무 일도 안하고, DXML 에서는 그룹이름을 시작 태그로 출력한다.
	 * @param groupName 그룹명
	 * @param sw 출력 스트림
	 * @throws BodyFormatException 그룹의 시작을 알리는 내용을 저장하는 과정에서 에러 발생시 던지는 예외
	 * @throws NoMoreDataPacketBufferException 데이터 패킷 버퍼를 확보하지 못했을때 던지는 예외
	 */
	public void writeGroupHead(String groupName, ArrayInfo arrayInfo, OutputStreamIF sw) throws BodyFormatException, NoMoreDataPacketBufferException;
	
	/**
	 * 그룹의 시작을 알리는 내용을 읽어 온다. 단 그룹의 시작을 알리는 내용이 저장한 내용과 일치하지 않으면 바디 포맷 예외를 던진다.
	 * @param groupName 그룹명
	 * @param sr 입력 BodyFormatException
	 * @throws BodyFormatException 그룹의 시작을 알리는 내용이 저장한 내용과 일치하지 않을때 던지는 예외
	 */
	public void readGroupHead(String groupName, ArrayInfo arrayInfo, InputStreamIF sr) throws BodyFormatException;
	
	/**
	 * 그룹 종료를 알리는 내용을 출력 스트림에 저장한다. 참고) DHB는 아무 일도 안하고, DXML 에서는 그룹이름을 종료 태그로 출력한다.
	 * @param groupName
	 * @param sw 출력 스트림
	 * @throws BodyFormatException 그룹의 종료를 알리는 내용을 저장하는 과정에서 에러 발생시 던지는 예외
	 * @throws NoMoreDataPacketBufferException 데이터 패킷 버퍼를 확보하지 못했을때 던지는 예외
	 */
	public void writeGroupTail(String groupName, ArrayInfo arrayInfo, OutputStreamIF sw) throws BodyFormatException, NoMoreDataPacketBufferException;
	
	/**
	 * 그룹의 종료를 알리는 내용을 읽어 온다. 단 그룹의 종료를 알리는 내용이 저장한 내용과 일치하지 않으면 바디 포맷 예외를 던진다.
	 * @param groupName 그룹명
	 * @param sr 입력 스트림
	 * @throws BodyFormatException 그룹의 종료를 알리는 내용이 저장한 내용과 일치하지 않을때 던지는 예외
	 */
	public void readGroupTail(String groupName, ArrayInfo arrayInfo, InputStreamIF sr) throws BodyFormatException;
}
