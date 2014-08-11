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
	 * @param itemCharsetForLang
	 * @param charsetOfProject
	 * @param middleWriteObj
	 * @throws Exception
	 */
	public void putValueToMiddleWriteObj(String path, String itemName, int itemTypeID, String itemTypeName, 
			Object itemValue, int itemSizeForLang, Charset itemCharsetForLang,  Charset charsetOfProject, Object middleWriteObj)
			throws Exception;
	
	/**
	 * 
	 * @param path
	 * @param arrayName
	 * @param arrayCntType
	 * @param arrayCntValue
	 * @param middleWriteObj
	 * @return
	 * @throws Exception
	 */
	public Object getArrayObjFromMiddleWriteObj(String path, String arrayName, String arrayCntType, String arrayCntValue, Object middleWriteObj)
			throws Exception;
	
	/**
	 * 
	 * @param path
	 * @param arrayObj
	 * @return
	 * @throws Exception
	 */
	public Object addMiddleWriteObjToArrayObj(String path, Object arrayObj) throws Exception;
}



