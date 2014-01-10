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

import java.nio.BufferOverflowException;
import java.nio.BufferUnderflowException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;

import kr.pe.sinnori.common.exception.BodyFormatException;
import kr.pe.sinnori.common.exception.MessageItemException;
import kr.pe.sinnori.common.exception.NoMoreDataPacketBufferException;
import kr.pe.sinnori.common.exception.SinnoriCharsetCodingException;
import kr.pe.sinnori.common.io.InputStreamIF;
import kr.pe.sinnori.common.io.OutputStreamIF;
import kr.pe.sinnori.common.io.dhb.DHBSingleItem2StreamIF;
import kr.pe.sinnori.common.io.dhb.header.DHBMessageHeader;
import kr.pe.sinnori.common.io.djson.DJSONSingleItem2JSON;
import kr.pe.sinnori.common.lib.CommonRootIF;
import kr.pe.sinnori.common.lib.CommonType;
import kr.pe.sinnori.common.util.HexUtil;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

/**
 * 메시지 구현 추상화 클래스.
 * 
 * <pre class="inheritance">
 * 메시지 정보와 메시지 내용(=데이터)의 결합으로 메시지를 구현한다.
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
public abstract class AbstractItemGroupDataOfMessage implements ItemGroupDataIF, CommonRootIF {
	/** 메시지 식별자 */
	protected String messageID;

	/**
	 * HashMap<String, Object> itemValueHash
	 * ------------------------------------------ 키 : 항목명, 값 : 항목값 항목값은 2가지로
	 * 구성된다. (1) 단일 항목 : (unsigned) byte, (unsigned) short, (unsigned) integer,
	 * long, String, byte[] (2) 배열 : 정규식으로 표현하면 [단일 항목 | 배열]*
	 */
	private HashMap<String, Object> itemValueHash = null;
	

	/** 항목 순서별 항목 이름을 갖는 목록. 항목은 순서를 갖기에 꼭 필요한 자료 구조. */
	private ArrayList<AbstractItemInfo> itemInfoList = null;

	/** 메시지 정보 */
	private MessageInfo messageInfo = null;

	/**
	 * 생성자
	 * @param messageID 메시지 생성을 원하는 메시지 식별자
	 * @param messageInfo 메시지 식별자에 대응하는 메시지 정보
	 */
	public AbstractItemGroupDataOfMessage(String messageID, MessageInfo messageInfo) {
		if (null == messageID) {
			String errorMessage = String.format("파라미터 메시지 식별자가 null 입니다.");
			IllegalArgumentException e = new IllegalArgumentException(
					errorMessage);
			log.warn("IllegalArgumentException", e);
			throw e;
		}

		if (!DHBMessageHeader.IsValidMessageID(messageID)) {
			String errorMessage = String.format("메시지 식별자[%s]가 잘못되었습니다.",
					messageID);
			IllegalArgumentException e = new IllegalArgumentException(
					errorMessage);
			log.warn("IllegalArgumentException", e);
			throw e;
		}
		
		if (null == messageInfo) {
			String errorMessage = String.format("파라미터 메시지 정보가 null 입니다.");
			IllegalArgumentException e = new IllegalArgumentException(
					errorMessage);
			log.warn("IllegalArgumentException", e);
			throw e;
		}

		int itemInfoListSize = messageInfo.getItemInfoList().size();
		
		// FIXME!
		// log.info(String.format("itemInfoListSize=[%d]", itemInfoListSize));
		
		itemValueHash = new HashMap<String, Object>(itemInfoListSize);
		
		this.messageID = messageID;

	
		this.messageInfo =  messageInfo;
		this.itemInfoList = messageInfo.getItemInfoList();
		
		// FIXME!
		// log.info(itemInfoList.toString());
	}

	/**
	 * 어떤 메시지인지 구별하기 위한 메시지 식별자를 반환한다.
	 * 
	 * @return 메시지 식별자
	 */
	public String getMessageID() {
		return messageID;
	}

	
	@Override
	public void M2O(OutputStreamIF sw, DHBSingleItem2StreamIF sisc) throws BodyFormatException, NoMoreDataPacketBufferException {
		// log.info("call M2BDHB");	
		/** 바디 저장, 클라이언트 (1)항, 서버 (1)항인 경우 이곳 로직을 수행한다. */
		int itemInfoListSize = itemInfoList.size();
		for (int i = 0; i < itemInfoListSize; i++) {
		
		// Iterator<AbstractItemInfo> itemInfoList = messageInfo.getItemInfoList();
		// while (itemInfoList.hasNext()) {
			// String key = itemInfoList.get(i).getItemName();
			// AbstractItemInfo itemInfo = messageInfo.getItemInfo(key);
			AbstractItemInfo itemInfo = itemInfoList.get(i);
			// AbstractItemInfo itemInfo = itemInfoList.next();
			
			
			String key = itemInfo.getItemName();
			
			CommonType.LOGICAL_ITEM_GUBUN logicalItemGubun = itemInfo
					.getLogicalItemGubun();
			if (CommonType.LOGICAL_ITEM_GUBUN.SINGLE_ITEM == logicalItemGubun) {
				Object value = itemValueHash.get(key);
				
				SingleItemInfo singleItemInfo = (SingleItemInfo) itemInfo;
				int itemTypeID = singleItemInfo.getItemTypeID();
				int itemSizeForLang = singleItemInfo.getItemSizeForLang();
				Charset itemCharsetForLang = singleItemInfo.getItemCharsetForLang();

				try {
					sisc.I2S(key, itemTypeID, value, itemSizeForLang, itemCharsetForLang, sw);
				} catch (OutOfMemoryError e) {
					String errorMessage = String.format("OutOfMemoryError::%s.%s value=[%s], itemSizeForLang=[%d], itemCharset=[%s]", messageID, 
							key, value, itemSizeForLang, itemCharsetForLang);
					log.warn(errorMessage, e);
					throw new BodyFormatException(errorMessage);
				} catch (IllegalArgumentException e) {
					String errorMessage = String.format("IllegalArgumentException::%s.%s value=[%s], itemSizeForLang=[%d], itemCharset=[%s], message=[%s]", 
							messageID, key, value, itemSizeForLang, itemCharsetForLang, e.getMessage());
					log.warn(errorMessage, e);
					throw new BodyFormatException(errorMessage);
				} catch (BufferOverflowException e) {
					String errorMessage = String.format("BufferOverflowException::%s.%s value=[%s], itemSizeForLang=[%d], itemCharset=[%s]", messageID, 
							key, value, itemSizeForLang, itemCharsetForLang);
					log.warn(errorMessage, e);
					throw new BodyFormatException(errorMessage);
				}
			} else {
				
				
				ArrayData arrayData = (ArrayData) itemValueHash.get(key);
				
				if (null == arrayData) {
					arrayData = new ArrayData(messageID, this,
							(ArrayInfo) itemInfo);
					itemValueHash.put(key, arrayData);
				}
				
				sisc.writeGroupHead(key, (ArrayInfo) itemInfo, sw);
				
				int arrayDataSize = arrayData.size();
				for (int j = 0; j < arrayDataSize; j++) {
					ItemGroupDataIF itemGroupData = arrayData.get(j);
					itemGroupData.M2O(sw, sisc);
				}
				
				sisc.writeGroupTail(key, (ArrayInfo) itemInfo, sw);
	
			}
		}
	}

	@Override
	public void O2M(InputStreamIF sr, DHBSingleItem2StreamIF sisc) throws BodyFormatException {
		// log.info("call B2MDHB");
		int itemInfoListSize = itemInfoList.size();
		for (int i = 0; i < itemInfoListSize; i++) {
		// Iterator<AbstractItemInfo> itemInfoList = messageInfo.getItemInfoList();
		// while (itemInfoList.hasNext()) {
			// String key = keyList.get(i);
			// AbstractItemInfo itemInfo = messageInfo.getItemInfo(key);
			AbstractItemInfo itemInfo = itemInfoList.get(i);
			// AbstractItemInfo itemInfo = itemInfoList.next();
			
			
			String key = itemInfo.getItemName();
			CommonType.LOGICAL_ITEM_GUBUN logicalItemGubun = itemInfo
					.getLogicalItemGubun();
			if (CommonType.LOGICAL_ITEM_GUBUN.SINGLE_ITEM == logicalItemGubun) {
				SingleItemInfo singleItemInfo = (SingleItemInfo) itemInfo;
				int itemTypeID = singleItemInfo.getItemTypeID();
				int itemSizeForLang = singleItemInfo.getItemSizeForLang();
				Charset itemCharsetForLang = singleItemInfo.getItemCharsetForLang();
				
				// FIXMME!
				// log.info("%s sr.getPosition[%d]", key, sr.getPosition());
				try {
					sisc.S2I(key, itemTypeID, itemSizeForLang, itemCharsetForLang, itemValueHash, sr);
				} catch (OutOfMemoryError e) {
					String errorMessage = String.format("OutOfMemoryError::%s.%s itemSizeForLang=[%d], itemCharset=[%s]", messageID, 
							key, itemSizeForLang, itemCharsetForLang);
					log.warn(errorMessage, e);
					throw new BodyFormatException(errorMessage);
				} catch (SinnoriCharsetCodingException e) {
					String errorMessage = 
							String.format("SinnoriCharsetCodingException::%s.%s itemSizeForLang=[%d], itemCharset=[%s], message=[%s]", 
									messageID, key, itemSizeForLang, itemCharsetForLang, e.getMessage());
					log.warn(errorMessage, e);
					throw new BodyFormatException(errorMessage);
				} catch (IllegalArgumentException e) {
					String errorMessage = 
							String.format("IllegalArgumentException::%s.%s itemSizeForLang=[%d], itemCharset=[%s], message=[%s]", 
									messageID, key, itemSizeForLang, itemCharsetForLang, e.getMessage());
					log.warn(errorMessage, e);
					throw new BodyFormatException(errorMessage);
				} catch (BufferUnderflowException e) {
					String errorMessage = String.format("BufferUnderflowException::%s.%s itemSizeForLang=[%d], itemCharset=[%s]",
							messageID, key, itemSizeForLang, itemCharsetForLang);
					log.warn(errorMessage, e);
					throw new BodyFormatException(errorMessage);
				}
			} else {
				/*
				ArrayData arrayData = (ArrayData) itemValueHash
						.get(key);
				if (null == arrayData) {
					arrayData = new ArrayData(messageID, this,
							(ArrayInfo) itemInfo);
					itemValueHash.put(key, arrayData);
	
				}
				*/
				ArrayData arrayData = new ArrayData(messageID, this,
						(ArrayInfo) itemInfo);
				itemValueHash.put(key, arrayData);
				
				
				
				sisc.readGroupHead(key, (ArrayInfo) itemInfo, sr);
	
				int arrayDataSize = arrayData.size();
				for (int j = 0; j < arrayDataSize; j++) {
					ItemGroupDataIF itemGroupData = arrayData
							.get(j);
					itemGroupData.O2M(sr, sisc);
				}
				
				sisc.readGroupTail(key, (ArrayInfo) itemInfo, sr);
			}
		}
		
		long remainingBytes = sr.remaining();
		if (0 != remainingBytes) {
			log.info(String.format("메시시[%s] 항목을 모두 읽은후에도 남은 바이트[%d]가 존재합니다.", messageID, remainingBytes));
		}

	}
	
	public void O2M(JSONObject jsonObj, DJSONSingleItem2JSON djsonSingleItemConverter) throws BodyFormatException {
		int itemInfoListSize = itemInfoList.size();
		for (int i = 0; i < itemInfoListSize; i++) {
			AbstractItemInfo itemInfo = itemInfoList.get(i);
			String key = itemInfo.getItemName();
			CommonType.LOGICAL_ITEM_GUBUN logicalItemGubun = itemInfo.getLogicalItemGubun();
			if (CommonType.LOGICAL_ITEM_GUBUN.SINGLE_ITEM == logicalItemGubun) {
				SingleItemInfo singleItemInfo = (SingleItemInfo) itemInfo;
				int itemTypeID = singleItemInfo.getItemTypeID();
				int itemSizeForLang = singleItemInfo.getItemSizeForLang();
				Charset itemCharsetForLang = singleItemInfo.getItemCharsetForLang();
				
				// FIXMME!
				// log.info("%s sr.getPosition[%d]", key, sr.getPosition());
				try {
					djsonSingleItemConverter.S2I(key, itemTypeID, itemSizeForLang, itemCharsetForLang, itemValueHash, jsonObj);
				} catch (OutOfMemoryError e) {
					String errorMessage = String.format("OutOfMemoryError::%s.%s itemSizeForLang=[%d], itemCharset=[%s]", messageID, 
							key, itemSizeForLang, itemCharsetForLang);
					log.warn(errorMessage, e);
					throw new BodyFormatException(errorMessage);
				} catch (IllegalArgumentException e) {
					String errorMessage = 
							String.format("IllegalArgumentException::%s.%s itemSizeForLang=[%d], itemCharset=[%s], message=[%s]", 
									messageID, key, itemSizeForLang, itemCharsetForLang, e.getMessage());
					log.warn(errorMessage, e);
					throw new BodyFormatException(errorMessage);
				}
			} else {
				ArrayData arrayData = (ArrayData) itemValueHash
						.get(key);
				if (null == arrayData) {
					try {
						arrayData = new ArrayData(messageID, this, (ArrayInfo) itemInfo);
					} catch(ClassCastException e) {
						String errorMessage = 
								String.format("ClassCastException::%s.%s::%s", 
										messageID, key, e.getMessage());
						log.warn(errorMessage, e);
						throw new BodyFormatException(errorMessage);
					}
					itemValueHash.put(key, arrayData);
	
				}
				
				int arrayDataSize = arrayData.size();
				
				JSONArray jsonArrayData = (JSONArray)jsonObj.get(key);
				if (null == jsonArrayData) {
					String errorMessage = 
							String.format("메시지[%s] 배열 항목[%s]에 대응하는 json 배열 항목이 존재하지 않습니다.::%s", 
									messageID, key, jsonObj.toJSONString());
					throw new BodyFormatException(errorMessage);
				}
				int jsonArrayDataSize = jsonArrayData.size();
				if (arrayDataSize != jsonArrayDataSize) {
					String errorMessage = 
							String.format("메시지[%s] 배열 항목[%s]의 크기[%d]가 json 배열 항목의 크기[%d] 가 다릅니다.", 
									messageID, key, arrayDataSize, jsonArrayDataSize);
					throw new BodyFormatException(errorMessage);
				}
				
				for (int j = 0; j < arrayDataSize; j++) {
					JSONObject jsonObjOfArray = (JSONObject)jsonArrayData.get(j);
					ItemGroupDataIF itemGroupData = arrayData.get(j);
					itemGroupData.O2M(jsonObjOfArray, djsonSingleItemConverter);
				}
				
			}
		}
	}
	
	public void M2O(JSONObject jsonObj, DJSONSingleItem2JSON djsonSingleItemConverter) throws BodyFormatException {
		int itemInfoListSize = itemInfoList.size();
		for (int i = 0; i < itemInfoListSize; i++) {
		
		// Iterator<AbstractItemInfo> itemInfoList = arrayInfo.getItemInfoList();
		// while (itemInfoList.hasNext()) {
			
			AbstractItemInfo itemInfo = itemInfoList.get(i);
			// AbstractItemInfo itemInfo = itemInfoList.next();
			
			String key = itemInfo.getItemName();

			CommonType.LOGICAL_ITEM_GUBUN logicalItemGubun = itemInfo
					.getLogicalItemGubun();
			if (CommonType.LOGICAL_ITEM_GUBUN.SINGLE_ITEM == logicalItemGubun) {
				Object value = itemValueHash.get(key);
				
				SingleItemInfo singleItemInfo = (SingleItemInfo) itemInfo;
				int itemTypeID = singleItemInfo.getItemTypeID();
				int itemSizeForLang = singleItemInfo.getItemSizeForLang();
				Charset itemCharsetForLang = singleItemInfo.getItemCharsetForLang();
				
				try {
					djsonSingleItemConverter.I2S(key, itemTypeID, value, itemSizeForLang, itemCharsetForLang, jsonObj);
				} catch (OutOfMemoryError e) {
					String errorMessage = String.format("OutOfMemoryError::%s.%s value=[%s], itemSizeForLang=[%d], itemCharset=[%s]", messageID, 
							key, value, itemSizeForLang, itemCharsetForLang);
					log.warn(errorMessage, e);
					throw new BodyFormatException(errorMessage);
				} catch (IllegalArgumentException e) {
					String errorMessage = 
							String.format("IllegalArgumentException::%s.%s value=[%s], itemSizeForLang=[%d], itemCharset=[%s], message=[%s]", 
									messageID, key, value, itemSizeForLang, itemCharsetForLang, e.getMessage());
					log.warn(errorMessage, e);
					throw new BodyFormatException(errorMessage);
				}
			} else {
				ArrayData arrayData = (ArrayData) itemValueHash.get(key);
				int arrayDataSize = arrayData.size();
				
				JSONArray jsonArrayData = (JSONArray)jsonObj.get(key);
				if (null == jsonArrayData) {
					String errorMessage = 
							String.format("메시지[%s] 배열 항목[%s]에 대응하는 json 배열 항목이 존재하지 않습니다.::%s", 
									messageID, key, jsonObj.toJSONString());
					throw new BodyFormatException(errorMessage);
				}
				int jsonArrayDataSize = jsonArrayData.size();
				if (arrayDataSize != jsonArrayDataSize) {
					String errorMessage = 
							String.format("메시지[%s] 배열 항목[%s]의 크기[%d]가 json 배열 항목의 크기[%d] 가 다릅니다.", 
									messageID, key, arrayDataSize, jsonArrayDataSize);
					throw new BodyFormatException(errorMessage);
				}
				
				for (int j = 0; j < arrayDataSize; j++) {
					JSONObject jsonObjOfArray = (JSONObject)jsonArrayData.get(j);
					ItemGroupDataIF itemGroupData = arrayData.get(j);
					itemGroupData.M2O(jsonObjOfArray, djsonSingleItemConverter);
				}				
			}
		}
	}

	@Override
	public Object getAttribute(String key) throws MessageItemException {
		if (null == key) {
			throw new MessageItemException("파라미터 항목 이름이 null 입니다.");
		}
		
		AbstractItemInfo itemInfo = messageInfo.getItemInfo(key);
		if (null == itemInfo) {
			throw new MessageItemException(String.format(
					"메시지[%s]에 존재하지 않는 항목[%s]입니다.", messageID, key));
		}

		if (CommonType.LOGICAL_ITEM_GUBUN.ARRAY == itemInfo
				.getLogicalItemGubun()) {
			ArrayData arrayData = (ArrayData) itemValueHash.get(key);

			if (null == arrayData) {
				// FIXME!
				// log.info(String.format("key=[%s]", key));
				
				arrayData = new ArrayData(messageID, this,
						(ArrayInfo) itemInfo);

				itemValueHash.put(key, arrayData);
			}

			return arrayData;
		}
		
		Object retObj = itemValueHash.get(key);
		
		
		// FIXME!
		/*
		if (null == retObj) {
			log.info(String.format("key[%s] retObj is null, itemValueHash=[%s]", key, itemValueHash.toString()));
		} else {
			log.info(String.format("key[%s] retObj is not null, itemValueHash=[%s]", key, itemValueHash.toString()));
		}
		*/
				
		if (null ==  retObj) {
			SingleItemInfo singleItemInfo = (SingleItemInfo)itemInfo;
			Object defaultValueForLang = singleItemInfo.getItemDefaultValueForLang();
			if (null != defaultValueForLang) {
				itemValueHash.put(key, defaultValueForLang);
				retObj = defaultValueForLang;
			}
		}
		
		return retObj;

	}

	@Override
	public void setAttribute(String key, Object itemValue)
			throws MessageItemException {
		if (null == key) {
			throw new MessageItemException("파라미터 항목 이름이 null 입니다.");
		}

		if (null == itemValue) {
			throw new MessageItemException("파라미터 항목 값이 null 입니다.");
		}
		
		// FIXME!
		//log.info(String.format("1.key=[%s], itemValue=[%s]", key, itemValue.toString()));

		AbstractItemInfo itemInfo = messageInfo.getItemInfo(key);

		if (null == itemInfo) {
			throw new MessageItemException(String.format(
					"메시지[%s]에 존재하지 않는 항목[%s]입니다.", messageID, key));
		}

		CommonType.LOGICAL_ITEM_GUBUN logicalItemGubun = itemInfo
				.getLogicalItemGubun();
		if (CommonType.LOGICAL_ITEM_GUBUN.ARRAY == logicalItemGubun) {
			throw new MessageItemException(String.format(
					"이 메소드는 단일 항목만 허용합니다. 메시지[%s] 항목[%s]", messageID, key));
		}
		
		SingleItemInfo singleItemInfo = (SingleItemInfo) itemInfo;
		String itemType = singleItemInfo.getItemType();
		Class<?> valueTypeForLang = singleItemInfo.getItemTypeForLang();
		
		if (!valueTypeForLang.isInstance(itemValue)) {
			throw new MessageItemException(String.format(
					"%s 항목[%s]의 타입[%s::%s] 과 값의 타입[%s]이 일치하지 않습니다.",
					messageID, key, itemType, valueTypeForLang.getName(),
					itemValue.getClass().getName()));
		}
		
		
		int itemTypeID = singleItemInfo.getItemTypeID();
		Charset itemCharsetForLang = singleItemInfo.getItemCharsetForLang();
		int itemSizeForLang = singleItemInfo.getItemSizeForLang();
		itemTypeManger.checkValue(itemTypeID, itemCharsetForLang, itemSizeForLang, itemValue);
		
		itemValueHash.put(key, itemValue);
		
		// FIXME!
		//log.info(String.format("2.key=[%s], itemValue=[%s]", key, itemValueHash.get(key).toString()));
		//log.info(String.format("3.key=[%s], itemValue=[%s]", key, itemValueHash.get(key).toString()));
	}
	

	public String toJSONString() {
		StringBuilder strBuilder = new StringBuilder();

		int itemInfoListSize = itemInfoList.size();
		for (int i = 0; i < itemInfoListSize; i++) {
		// Iterator<AbstractItemInfo> itemInfoList = messageInfo.getItemInfoList();
		// for (int i = 0; itemInfoList.hasNext(); i++) {
			AbstractItemInfo itemInfo = itemInfoList.get(i);
			// AbstractItemInfo itemInfo = itemInfoList.next();
			
			String key = itemInfo.getItemName();
			
			
			
			if (i > 0) strBuilder.append(", ");
			strBuilder.append("\"");
			strBuilder.append(JSONObject.escape(key));
			strBuilder.append("\":");

			CommonType.LOGICAL_ITEM_GUBUN logicalItemGubun = itemInfo
					.getLogicalItemGubun();
			if (CommonType.LOGICAL_ITEM_GUBUN.SINGLE_ITEM == logicalItemGubun) {
				Object value = itemValueHash.get(key);
				if (value instanceof String) {
					strBuilder.append("\"");
					strBuilder.append(JSONObject.escape((String)value));
					strBuilder.append("\"");
				} else if (value instanceof byte[]) {
					strBuilder.append("\"");
					strBuilder.append(JSONObject.escape(HexUtil.byteArrayAllToHex((byte[])value)));
					strBuilder.append("\"");
				} else {
					strBuilder.append(value);
				}
			} else {
				ArrayData arrayData = (ArrayData) itemValueHash.get(key);
								
				if (null == arrayData) {
					strBuilder.append("null");
					return strBuilder.toString();
				}
				
	
				strBuilder.append("[ ");
				
				int arrayDataSize = arrayData.size();
				for (int j = 0; j < arrayDataSize; j++) {
					ItemGroupDataIF itemGroupData = arrayData.get(j);
					
					if (j > 0) strBuilder.append(", ");
					strBuilder.append("{ ");
					strBuilder.append(itemGroupData.toJSONString());
					strBuilder.append(" }");
				}
				
				strBuilder.append(" ]");
			}
		}
		
		
		return strBuilder.toString();
	}
	
	@Override
	public String toString() {

		StringBuilder strBuilder = new StringBuilder();
		// strBuff.append(this.getClass().getSimpleName());
		// strBuff.append(" ");
		strBuilder.append(messageID);
		strBuilder.append("={");

		int itemInfoListSize = itemInfoList.size();
		for (int i = 0; i < itemInfoListSize; i++) {
		
		// Iterator<AbstractItemInfo> itemInfoList = messageInfo.getItemInfoList();
		// for (int i = 0; itemInfoList.hasNext(); i++) {
			if (i > 0) {
				strBuilder.append(",");
				strBuilder.append("\n");
			}
			// String key = itemInfoSize.get(i);
			
			AbstractItemInfo itemInfo = itemInfoList.get(i);
			// AbstractItemInfo itemInfo = itemInfoList.next();
			
			
			String key = itemInfo.getItemName();

			CommonType.LOGICAL_ITEM_GUBUN logicalItemGubun = itemInfo
					.getLogicalItemGubun();

			// strBuff.append(messageID);
			// strBuff.append(".");
			strBuilder.append(key);
			strBuilder.append("=");

			if (CommonType.LOGICAL_ITEM_GUBUN.SINGLE_ITEM == logicalItemGubun) {
				Object value = itemValueHash.get(key);

				if (value == null) {
					strBuilder.append("null");
					continue;
				}

				if (value instanceof byte[]) {
					byte arrayValue[] = (byte[]) value;
					
					strBuilder.append("[");
					strBuilder.append(arrayValue.length);
					strBuilder.append("] {");
					
					for (int k = 0; k < arrayValue.length && k < 10; k++) {
						if (k > 0)
							strBuilder.append(",");
						strBuilder.append(arrayValue[k]);
					}
					if (arrayValue.length >= 10) {
						strBuilder.append("...");
					}
					strBuilder.append("}");
				} else {
					strBuilder.append(value);
				}
			} else {
				/**
				 * 배열 데이터(=ArrayData) 는 항목 그룹(=ItemGroupDataIF)을 원소로 가지는 목록을 관리하는 클래스이다.
				 * 배열 데이터의 원소인 항목 그룹에 toString 을 위임하지 않고 
				 * 직접적으로 toString 구현한 이유는 
				 * 항목 그룹은 항목 그룹 목록안에 몇번째에 속한지를 알 수 없기 때문에
				 * 항목 그룹에 속한 항목마다 접두어로 <배열명>[index] 를 넣어줄 수 없다.
				 * 오직 항목 그룹을 관리하는 배열 데이터만이 항목 그룹이 몇번째 인지를 알 수 있으므로,
				 * 배열 데이터(=ArrayData) 에서 toString 을 구현한다.
				 */
				ArrayData arrayData = (ArrayData) itemValueHash.get(key);
				if (null != arrayData) {
					strBuilder.append(arrayData.toString());
				} else {
					strBuilder.append("null");
				}
			}
		}

		strBuilder.append("}");

		return strBuilder.toString();
	}
}
