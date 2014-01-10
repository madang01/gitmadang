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

package kr.pe.sinnori.common.message;

import kr.pe.sinnori.common.exception.BodyFormatException;
import kr.pe.sinnori.common.exception.MessageItemException;
import kr.pe.sinnori.common.exception.NoMoreDataPacketBufferException;
import kr.pe.sinnori.common.io.InputStreamIF;
import kr.pe.sinnori.common.io.OutputStreamIF;
import kr.pe.sinnori.common.io.dhb.DHBSingleItem2StreamIF;
import kr.pe.sinnori.common.io.djson.DJSONSingleItem2JSON;

import org.json.simple.JSONObject;

/**
 * 메시지 항목 그룹 내용 접근 메소드 정의 인터페이스.
 * 
 * <pre>
 * 항목 그룹에 속한 단일 항목 혹은 배열의 값에 대한 접근 메소드와
 * 항목 그룹의 DHB 방식의 이진 스트림 변환 관련 메소드를 제공한다.
 * 주) 단일 항목의 경우 직접적인 값을 바로 지정할 수 있고 얻을 수 있지만,
 * 배열은 직접적으로 값을 저장할 수 없고 오직 {@link #getAttribute(String)} 를 통해서만 접근 가능하다.
 * 이때 중요한 점이 배열을 얻는 시점에서 반듯이 배열의 크기는 정해져야 한다.
 * 즉, 배열 크기가 참조 방식의 경우 참조하는 변수의 값이 미리 정해 져야 함을 말한다.
 * 메소드 {@link #getAttribute(String)} 는 항목이 배열일 경우 {@link ArrayData} 객체를 반환한다.
 * 
 * 참고) 메시지 표현 정규식
 * 메시지 = 메시지 식별자, 항목 그룹
 * 항목 그룹 = (항목)*
 * 항목 = (단일 항목 | 배열)
 * 단일 항목 = 이름, 타입, 타입 부가 정보{0..1}, 값
 * 타입 부가 정보 = 크기 | 문자셋
 * 배열 = 이름, 반복 횟수, (항목 그룹)
 * </pre>
 * 
 * @author Jonghoon Won
 * 
 */
public interface ItemGroupDataIF {
	public static final ItemTypeManger itemTypeManger = ItemTypeManger.getInstance();
	
	/**
	 * 항목 그룹에 속한 항목 이름을 갖는 항목의 값을 얻어온다.
	 * 
	 * <pre>
	 * 주) 배열은 반듯이 이 메소드를 통해서 얻어진다. 이때 주의할 점이 내부적으로 배열을 다루는 메모리 확보를 이루어 진다. 
	 *     따라서 배열 크기 지정 방식이 참조일 경우 미리 참조 변수의 값이 정해져야 한다.
	 *     이렇게 정해진 참조 변수값으로 배열 크기가 정해진다.
	 * </pre>
	 * 
	 * @param key
	 *            항목명
	 * @return 항목값
	 */
	public Object getAttribute(String key) throws MessageItemException;

	/**
	 * 항목명에 항목값을 저장한다. 단 배열을 직접적으로 저장할 수 없다.
	 * 
	 * @param key
	 *            항목명
	 * @param value
	 *            항목값
	 */
	public void setAttribute(String key, Object value)
			throws MessageItemException;

	/**
	 * DHB 방식으로 이진 스트림에서 항목 그룹 내용을 읽어와 저장한다.
	 * 
	 * @param sr
	 *            읽기 기능을 제공하는 이진 스트림
	 * @throws BodyFormatException
	 *             이진 스트림에서 데이터를 읽어오는 과정에서 내부 오류가 발생시 던지는 예외
	 */
	public void O2M(InputStreamIF sr, DHBSingleItem2StreamIF sisc) throws BodyFormatException;

	/**
	 * 항목 그룹 내용을 DHB 방식으로 이진 스트림에 저장한다.
	 * 
	 * @param sw
	 *            쓰기 기능을 제공하는 이진 스트림
	 * @throws BodyFormatException
	 *             이진 스트림에 데이터를 쓰는 과정에서 내부 오류가 발생시 던지는 예외
	 * @throws NoMoreDataPacketBufferException
	 *             이진 스트림에 데이터를 쓰는 과정에서 바디 버퍼 확보에 실패시 던지는 예외
	 */
	public void M2O(OutputStreamIF sw, DHBSingleItem2StreamIF sisc) throws BodyFormatException,
	NoMoreDataPacketBufferException;
	
	
	public void O2M(JSONObject jsonObj, DJSONSingleItem2JSON djsonSingleItemConverter) throws BodyFormatException;
	public void M2O(JSONObject jsonObj, DJSONSingleItem2JSON djsonSingleItemConverter) throws BodyFormatException;
	
	public String toJSONString();
}
