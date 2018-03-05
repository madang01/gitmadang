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
package kr.pe.sinnori.common.protocol.djson;

import java.nio.charset.Charset;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import kr.pe.sinnori.common.exception.BodyFormatException;
import kr.pe.sinnori.common.protocol.SingleItemEncoderIF;
import kr.pe.sinnori.common.type.SingleItemType;

/**
 * DJSON 단일 항목 인코더
 * 
 * @author "Won Jonghoon"
 *
 */
public class DJSONSingleItemEncoder implements SingleItemEncoderIF {
	private Logger log = LoggerFactory.getLogger(DJSONSingleItemEncoder.class);
	
	private DJSONSingleItemEncoderMatcherIF djsonSingleItemEncoderMatcher = null;
	
	public DJSONSingleItemEncoder(DJSONSingleItemEncoderMatcherIF djsonSingleItemEncoderMatcher) {
		if (null == djsonSingleItemEncoderMatcher) {
			throw new IllegalArgumentException("the parameter djsonSingleItemEncoderMatcher is null");
		}
		this.djsonSingleItemEncoderMatcher = djsonSingleItemEncoderMatcher;
	}
	

	@Override
	public void putValueToWritableMiddleObject(String path, String itemName,
			SingleItemType singleItemType, Object itemValue,
			int itemSize, String nativeItemCharset, Object writableMiddleObject) throws Exception {
		if (null == singleItemType) {
			throw new IllegalArgumentException("the parameter singleItemType is null");
		}
		
		if (null == itemValue) {
			throw new IllegalArgumentException("the parameter itemValue is null");
		}
		
		if (!(writableMiddleObject instanceof JSONObject)) {
			String errorMessage = String.format(
					"파라미터 middleWriteObj[%s]의 데이터 타입이 JSONObject 이 아닙니다.",
					writableMiddleObject.getClass().getCanonicalName());
			throw new IllegalArgumentException(errorMessage);
		}
		
		int itemTypeID = singleItemType.getItemTypeID();
		String itemTypeName = singleItemType.getItemTypeName();
		
		Charset itemCharset = null;
		
		if (null != nativeItemCharset) {			
			try {
				itemCharset = Charset.forName(nativeItemCharset);
			} catch(Exception e) {
				log.warn(String.format("the parameter nativeItemCharset[%s] is not a bad charset name", nativeItemCharset), e);
			}
		}	
		
		JSONObject jsonObjForOutputStream = (JSONObject)writableMiddleObject;
		try {
			AbstractDJSONSingleItemEncoder djsonSingleItemEncoder = djsonSingleItemEncoderMatcher.get(itemTypeID);
			
			djsonSingleItemEncoder.putValue(itemName, itemValue, itemSize, itemCharset, jsonObjForOutputStream);		
		} catch(Exception | OutOfMemoryError e) {
			StringBuffer errorMessageBuilder = new StringBuffer("unknown error::");
			errorMessageBuilder.append(path);
			errorMessageBuilder.append("={itemName=[");
			errorMessageBuilder.append("], itemName=[");
			errorMessageBuilder.append(itemName);
			errorMessageBuilder.append("], itemType=[");
			errorMessageBuilder.append(itemTypeName);
			errorMessageBuilder.append("], itemValue=[");
			errorMessageBuilder.append(itemValue);
			errorMessageBuilder.append("], itemSize=[");
			errorMessageBuilder.append(itemSize);
			errorMessageBuilder.append("], itemCharset=[");
			errorMessageBuilder.append(nativeItemCharset);
			errorMessageBuilder.append("] }, errmsg=[");
			errorMessageBuilder.append(e.getMessage());
			errorMessageBuilder.append("]");
			
			String errorMessage = errorMessageBuilder.toString();
			log.warn(errorMessage, e);
			throw new BodyFormatException(errorMessage);
		}
	}
	
	@Override
	public Object getWritableMiddleObjectjFromArrayMiddleObject(String path, Object arrayObj, int inx) throws BodyFormatException {
		if (null == path) {
			String errorMessage = String.format(
					"%s 경로에 대응하는 존슨 객체에서 존슨 배열의 항목 값을 얻어오는 과정에서 파라미터 path 의 값[%d] 이 null 입니다.",
					path, inx);
			throw new BodyFormatException(errorMessage);
		}
		
		if (inx < 0) {
			String errorMessage = String.format(
					"%s 경로에 대응하는 존슨 객체에서 존슨 배열의 항목 값을 얻어오는 과정에서 파라미터 inx 의 값[%d] 이 음수입니다.",
					path, inx);
			throw new BodyFormatException(errorMessage);
		}
		
		if (!(arrayObj instanceof JSONArray)) {
			String errorMessage = String.format(
					"%s 경로에 대응하는 존슨 객체에서 존슨 배열의 항목 값을 얻는 과정에서 파라미터 arrayObj[%s]의 데이터 타입이 JSONArray 이 아닙니다.",
					path, arrayObj.getClass().getCanonicalName());
			throw new BodyFormatException(errorMessage);
		}
		
		JSONArray jsonArray = (JSONArray)arrayObj;
		Object writableMiddleObjectOfArray = null;
		try {
			writableMiddleObjectOfArray = jsonArray.get(inx);
		} catch(IndexOutOfBoundsException  e) {
			String errorMessage = String.format(
					"%s 경로에 대응하는 존슨 객체에서 존슨 배열의 항목 값은 배열 크기를 벗어난 요청입니다.", path);
			throw new BodyFormatException(errorMessage);
		}		
		
		if (!(writableMiddleObjectOfArray instanceof JSONObject)) {
			String errorMessage = String.format(
					"%s 경로에 대응하는 존슨 객체에서 존슨 배열의 항목의 값의 타입[%s]이 JSONObject 이 아닙니다.",
					path, writableMiddleObjectOfArray.getClass().getCanonicalName());
			throw new BodyFormatException(errorMessage);
		}
		
		return writableMiddleObjectOfArray;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public Object getArrayMiddleObjectFromWritableMiddleObject(String path, String arrayName,
			int arrayCntValue, Object writableMiddleObject)
			throws BodyFormatException {
		if (!(writableMiddleObject instanceof JSONObject)) {
			String errorMessage = String.format(
					"%s 경로에 대응하는 존슨 객체에서 존슨 배열[%s]를 얻는 과정에서  파라미터 middleWriteObj[%s]의 데이터 타입이 JSONObject 이 아닙니다.",
					path, arrayName, writableMiddleObject.getClass().getCanonicalName());
			throw new BodyFormatException(errorMessage);
		}
		
		if (arrayCntValue < 0) {
			String errorMessage = String.format(
					"%s 경로에 대응하는 존슨 객체에서 존슨 배열[%s] 생성후 얻기::parameter arrayCntValue is less than zero",
					path, arrayName);
			throw new BodyFormatException(errorMessage);
		}
		
		JSONObject jsonReadObj = (JSONObject)writableMiddleObject;
		Object valueObj = jsonReadObj.get(arrayName);

		if (null == valueObj) {
			/*String errorMessage = String.format(
					"%s 경로에 대응하는 존슨 객체에서 존슨 배열[%s]이 존재하지 않습니다.",
					path, arrayName);
			throw new BodyFormatException(errorMessage);*/
			JSONArray jsonArray = new JSONArray();
			for (int i=0; i < arrayCntValue; i++) {
				jsonArray.add(new JSONObject());
			}
			jsonReadObj.put(arrayName, jsonArray);
			return jsonArray;
		}

		if (!(valueObj instanceof JSONArray)) {
			String errorMessage = String.format(
					"%s 경로에 대응하는 존슨 객체에서 존슨 배열[%s]의 값의 타입[%s]이 JSONArray 이 아닙니다.",
					path, arrayName, valueObj.getClass().getCanonicalName());
			throw new BodyFormatException(errorMessage);
		}
		
		
		JSONArray jsonArray = (JSONArray)valueObj;
		
		if (jsonArray.size() !=  arrayCntValue) {
			String errorMessage = String.format(
					"%s 경로에 대응하는 존슨 객체에서 존슨 배열[%s]의 크기[%s]가 파라미터 arrayCntValue[%d]와 다릅니다.",
					path, arrayName, jsonArray.size(), arrayCntValue);
			throw new BodyFormatException(errorMessage);
		}
		
		return jsonArray;
	}
	
	
	@SuppressWarnings("unchecked")
	@Override
	public Object getGroupMiddleObjectFromWritableMiddleObject(String path, String groupName, Object writableMiddleObject)
			throws BodyFormatException {
		if (!(writableMiddleObject instanceof JSONObject)) {
			String errorMessage = String.format(
					"%s 경로에 대응하는 존슨 객체에서 존슨 그룹[%s]을 얻는 과정에서  파라미터 middleWriteObj[%s]의 데이터 타입이 JSONObject 이 아닙니다.",
					path, groupName, writableMiddleObject.getClass().getCanonicalName());
			throw new BodyFormatException(errorMessage);
		}
		
		JSONObject jsonReadObj = (JSONObject)writableMiddleObject;
		Object valueObj = jsonReadObj.get(groupName);

		if (null == valueObj) {
			JSONObject jsonGroup = new JSONObject();
			
			jsonReadObj.put(groupName, jsonGroup);
			return jsonGroup;
		}

		if (!(valueObj instanceof JSONObject)) {
			String errorMessage = String.format(
					"%s 경로에 대응하는 존슨 객체에서 존슨 그룹[%s]의 값의 타입[%s]이 JSONObject 이 아닙니다.",
					path, groupName, valueObj.getClass().getCanonicalName());
			throw new BodyFormatException(errorMessage);
		}
		
		return valueObj;
	}
}
