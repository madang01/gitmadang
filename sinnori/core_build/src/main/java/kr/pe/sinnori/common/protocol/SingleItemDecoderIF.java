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

import java.nio.charset.Charset;

import kr.pe.sinnori.common.exception.BodyFormatException;
import kr.pe.sinnori.common.message.builder.info.SingleItemType;

/**
 * 단일 항목 디코더 인터페이스. 프로토콜별로 구현된다.
 * @author "Won Jonghoon"
 *
 */
public interface SingleItemDecoderIF {
	
	public Object getValueFromReadableMiddleObject(String path, String itemName, SingleItemType singleItemType, 
			int itemSize, String nativeItemCharset, 
			Charset streamCharset,
			Object middleReadObj)
			throws BodyFormatException;
	
	/**
	 * <pre>
	 * "중간 다리 역활 읽기 객체" 로 부터 배열 정보를 가지고 배열 객체를 얻는다.
	 * 
	 * 참고) "배열 크기를 지정하는 방식"은 2가지가 있으며 첫번째 직접(direct)은 "배열 크기 값" 에 배열 크기를 정하는 방식이며 
	 * 마지막 두번째 참조(reference)은  "배열 크기 값"에 간접 참조하는 변수명의 값으로 배열 크기를 지정하는 방식이다.
	 * </pre>  
	 * @param path 메시지 항목의 경로, ex) AllDataType.memberList[1]
	 * @param arrayName 배열 이름 
	 * @param arrayCntValue 배열 크기 값
	 * @param readableMiddleObject 중간 다리 역활 읽기 객체
	 * @return  "중간 다리 역활 읽기 객체" 로 부터 배열 정보를 가지고 배열 객체
	 * @throws Exception  "중간 다리 역활 읽기 객체" 로 부터 배열 정보를 가지고 배열 객체를 얻을때 에러 발생시 던지는 예외
	 */
	public Object getArrayObjectFromReadableMiddleObject(String path, String arrayName, int arrayCntValue, Object readableMiddleObject)
			throws BodyFormatException;
	
	/**
	 * "배열 객체" 로 부터 지정된 인덱스에 있는 객체를 반환한다.
	 * @param path 메시지 항목의 경로, ex) AllDataType.memberList[1]
	 * @param arrayObj 배열 객체
	 * @param inx 배열 인덱스
	 * @return "배열 객체" 로 부터 지정된 인덱스에 있는 객체
	 * @throws Exception "배열 객체" 로 부터 지정된 인덱스에 있는 객체를 반환할때 에러 발생시 던지는 예외
	 */
	public Object getReadableMiddleObjFromArrayObject(String path, Object arrayObj, int inx) throws BodyFormatException;
	
	/**
	 * <pre>
	 * MiddleReadableObject 가 가진 자원 반환을 하는 장소는  2군데이다.
	 * 첫번째 장소는 메시지 추출 후 쓰임이 다해서 호출하는 AbstractMessageDecoder#decode 이며
	 * 두번째 장소는 2번 연속 호출해도 무방하기때문에 안전하게 자원 반환을 보장하기위한 Executor#run 이다.
	 * </pre>
	 */
	public void closeReadableMiddleObjectWithValidCheck(Object readableMiddleObject) throws BodyFormatException;
}



