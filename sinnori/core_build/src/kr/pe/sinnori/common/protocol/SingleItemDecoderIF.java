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

/**
 * 단일 항목 디코더 인터페이스. 프로토콜별로 구현된다.
 * @author "Won Jonghoon"
 *
 */
public interface SingleItemDecoderIF {
	/**
	 * 항목 정보를 바탕으로 "중간 다리 역활 읽기 객체" 로 부터 얻은 값을 반환한다. 즉 이진 스트림을 통해 전달받은 항목의 값을 반환한다.
	 * @param path 메시지 항목의 경로, ex) AllDataType.memberList[1].itemList[1]
	 * @param itemName 항목 이름
	 * @param itemTypeID 항목 타입 식별자
	 * @param itemTypeName 항목 타입명
	 * @param itemSizeForLang 항목 부가정보 - 데이터 크기
	 * @param itemCharset 항목 부가정보 - 문자셋
	 * @param charsetOfProject 프로젝트 문자셋
	 * @param middleReadObj 중간 다리 역활 읽기 객체
	 * @return 항목 정보를 바탕으로 "중간 다리 역활 읽기 객체" 로 부터 얻은 값
	 * @throws Exception 항목 정보를 바탕으로 "중간 다리 역활 읽기 객체" 로 부터 값을 얻을때 에러 발생시 던지는 예외
	 */
	public Object getValueFromMiddleReadObj(String path, String itemName, int itemTypeID, String itemTypeName, 
			int itemSizeForLang, String itemCharset, 
			Charset charsetOfProject,
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
	 * @param middleReadObj 중간 다리 역활 읽기 객체
	 * @return  "중간 다리 역활 읽기 객체" 로 부터 배열 정보를 가지고 배열 객체
	 * @throws Exception  "중간 다리 역활 읽기 객체" 로 부터 배열 정보를 가지고 배열 객체를 얻을때 에러 발생시 던지는 예외
	 */
	public Object getArrayObjFromMiddleReadObj(String path, String arrayName, int arrayCntValue, Object middleReadObj)
			throws BodyFormatException;
	
	/**
	 * "배열 객체" 로 부터 지정된 인덱스에 있는 객체를 반환한다.
	 * @param path 메시지 항목의 경로, ex) AllDataType.memberList[1]
	 * @param arrayObj 배열 객체
	 * @param inx 배열 인덱스
	 * @return "배열 객체" 로 부터 지정된 인덱스에 있는 객체
	 * @throws Exception "배열 객체" 로 부터 지정된 인덱스에 있는 객체를 반환할때 에러 발생시 던지는 예외
	 */
	public Object getMiddleReadObjFromArrayObj(String path, Object arrayObj, int inx) throws BodyFormatException;
	
	public void finish(Object middleReadObj) throws BodyFormatException;
}



