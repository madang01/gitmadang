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
 * 단일 항목 인코더 인터페이스. 프로토콜별로 구현된다.
 * @author "Jonghoon Won"
 *
 */
public interface SingleItemEncoderIF {
	/**
	 * 
	 * @param path
	 * @param itemName
	 * @param itemTypeID
	 * @param itemTypeName
	 * @param itemValue
	 * @param itemSizeForLang
	 * @param itemCharset
	 * @param charsetOfProject
	 * @param middleWriteObj
	 * @throws Exception
	 */
	public void putValueToMiddleWriteObj(String path, String itemName, int itemTypeID, String itemTypeName, 
			Object itemValue, int itemSizeForLang, String itemCharset,  Charset charsetOfProject, Object middleWriteObj)
			throws Exception;
	
	public Object getArrayObjFromMiddleWriteObj(String path, String arrayName,
			int arrayCntValue, Object middleWriteObj)
			throws BodyFormatException;
			
	/**
	 * "배열 객체" 로 부터 지정된 인덱스에 있는 객체를 반환한다.
	 * @param path 메시지 항목의 경로, ex) AllDataType.memberList[1]
	 * @param arrayObj 배열 객체
	 * @param inx 배열 인덱스
	 * @return "배열 객체" 로 부터 지정된 인덱스에 있는 객체
	 * @throws Exception "배열 객체" 로 부터 지정된 인덱스에 있는 객체를 반환할때 에러 발생시 던지는 예외
	 */
	public Object getMiddleWriteObjFromArrayObj(String path, Object arrayObj, int inx) throws BodyFormatException;
}



