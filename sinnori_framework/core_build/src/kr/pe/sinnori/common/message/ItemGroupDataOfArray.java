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
import kr.pe.sinnori.common.io.SingleItemSConverterIF;
import kr.pe.sinnori.common.io.djson.DJSONSingleItemConverter;
import kr.pe.sinnori.common.lib.CommonRootIF;
import kr.pe.sinnori.common.lib.CommonType;
import kr.pe.sinnori.common.util.HexUtil;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

/**
 * 메시지 배열에 속한 항목 그룹 구현 클래스.
 * 
 * <pre>
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
public class ItemGroupDataOfArray implements ItemGroupDataIF,
		CommonRootIF {
	private String parentPath = null;
	private int inx = 0;
	private ArrayInfo arrayInfo = null;
	
	private ArrayList<AbstractItemInfo> itemInfoList = null;

	private HashMap<String, Object> itemValueHash = null;
	
	

	// private AbstractMultiItemData parent = null;
	private String arrayName = null;

	private String currentPath = null;

	public ItemGroupDataOfArray(String parentPath, int inx, ArrayInfo arrayInfo) {
		// System.out.printf("2.path=[%s], inx=[%d], arrayName=[%s]\n", path,
		// inx, arrayName);
		int itemInfoListSize = arrayInfo.getItemInfoList().size();
		// FIXME!
		// log.info(String.format("itemInfoListSize=[%d]", itemInfoListSize));

		itemValueHash = new HashMap<String, Object>(itemInfoListSize);
		
		
		this.parentPath = parentPath;
		this.inx = inx;
		this.arrayInfo = arrayInfo;
		this.arrayName = arrayInfo.getArrayName();
		// this.parent = parent;
		// this.keyList = arrayInfo.getKeyList();
		this.itemInfoList = arrayInfo.getItemInfoList();
		

		StringBuffer currentPathBuff = new StringBuffer(parentPath);
		currentPathBuff.append(".");
		currentPathBuff.append(arrayName);
		currentPathBuff.append("[");
		currentPathBuff.append(inx);
		currentPathBuff.append("]");
		this.currentPath = currentPathBuff.toString();
	}

	public String getParentPath() {
		return parentPath;
	}

	public int getIndex() {
		return inx;
	}

	public ArrayInfo getArrayInfo() {
		return arrayInfo;
	}

	@Override
	public void M2S(OutputStreamIF sw, SingleItemSConverterIF sisc) throws BodyFormatException, NoMoreDataPacketBufferException {
		// int keySize = keyList.size();

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
					sisc.I2S(key, itemTypeID, value, itemSizeForLang, itemCharsetForLang, sw);
				} catch (OutOfMemoryError e) {
					String errorMessage = String.format("OutOfMemoryError::%s.%s value=[%s], itemSizeForLang=[%d], itemCharset=[%s]", parentPath, 
							key, value, itemSizeForLang, itemCharsetForLang);
					log.warn(errorMessage, e);
					throw new BodyFormatException(errorMessage);
				} catch (IllegalArgumentException e) {
					String errorMessage = 
							String.format("IllegalArgumentException::%s.%s value=[%s], itemSizeForLang=[%d], itemCharset=[%s], message=[%s]", 
									parentPath, key, value, itemSizeForLang, itemCharsetForLang, e.getMessage());
					log.warn(errorMessage, e);
					throw new BodyFormatException(errorMessage);
				} catch (BufferOverflowException e) {
					String errorMessage = String.format("BufferOverflowException::%s.%s value=[%s], itemSizeForLang=[%d], itemCharset=[%s]", parentPath, 
							key, value, itemSizeForLang, itemCharsetForLang);
					log.warn(errorMessage, e);
					throw new BodyFormatException(errorMessage);
				}
			} else {
				sisc.writeGroupHead(key, sw);
				
				
				ArrayData arrayData = (ArrayData) itemValueHash.get(key);
				if (null == arrayData) {
					arrayData = new ArrayData(parentPath, this,
							(ArrayInfo) itemInfo);
					itemValueHash.put(key, arrayData);
				}
				
				int arrayDataSize = arrayData.size();
				for (int j = 0; j < arrayDataSize; j++) {
					ItemGroupDataIF itemGroupData = arrayData.get(j);
					itemGroupData.M2S(sw, sisc);
				}

				sisc.writeGroupTail(key, sw);
			}
		}
	}

	@Override
	public void S2M(InputStreamIF sr, SingleItemSConverterIF sisc) throws BodyFormatException {
		// int keySize = keyList.size();

		int itemInfoListSize = itemInfoList.size();
		for (int i = 0; i < itemInfoListSize; i++) {
		// Iterator<AbstractItemInfo> itemInfoList = arrayInfo.getItemInfoList();
		// while (itemInfoList.hasNext()) {
			// String key = keyList.get(i);
			// AbstractItemInfo itemInfo = arrayInfo.getItemInfo(key);
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
				
				try {
					sisc.S2I(key, itemTypeID, itemSizeForLang, itemCharsetForLang, itemValueHash, sr);
				} catch (OutOfMemoryError e) {
					String errorMessage = String.format("OutOfMemoryError::%s.%s itemSizeForLang=[%d], itemCharset=[%s]", parentPath, 
							key, itemSizeForLang, itemCharsetForLang);
					log.warn(errorMessage, e);
					throw new BodyFormatException(errorMessage);
				} catch (SinnoriCharsetCodingException e) {
					String errorMessage = 
							String.format("SinnoriCharsetCodingException::%s.%s itemSizeForLang=[%d], itemCharset=[%s], message=[%s]", 
									parentPath, key, itemSizeForLang, itemCharsetForLang, e.getMessage());
					log.warn(errorMessage, e);
					throw new BodyFormatException(errorMessage);
				} catch (IllegalArgumentException e) {
					String errorMessage = 
							String.format("IllegalArgumentException::%s.%s itemSizeForLang=[%d], itemCharset=[%s], message=[%s]", 
									parentPath, key, itemSizeForLang, itemCharsetForLang, e.getMessage());
					log.warn(errorMessage, e);
					throw new BodyFormatException(errorMessage);
				} catch (BufferUnderflowException e) {
					String errorMessage = String.format("BufferUnderflowException::%s.%s itemSizeForLang=[%d], itemCharset=[%s]", parentPath, 
							key, itemSizeForLang, itemCharsetForLang);
					log.warn(errorMessage, e);
					throw new BodyFormatException(errorMessage);
				}
			} else {
				ArrayData arrayData = (ArrayData) itemValueHash.get(key);
				if (null == arrayData) {

					arrayData = new ArrayData(currentPath, this,
							(ArrayInfo) itemInfo);
					itemValueHash.put(key, arrayData);
				}
				
				sisc.readGroupHead(key, sr);

				int arrayDataSize = arrayData.size();
				for (int j = 0; j < arrayDataSize; j++) {
					ItemGroupDataIF itemGroupData = arrayData.get(j);
					itemGroupData.S2M(sr, sisc);
				}
				
				sisc.readGroupTail(key, sr);
			}
		}

	}
	
	public void JSON2M(JSONObject jsonObj, DJSONSingleItemConverter djsonSingleItemConverter) throws BodyFormatException {
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
					String errorMessage = String.format("OutOfMemoryError::%s.%s itemSizeForLang=[%d], itemCharset=[%s]", parentPath, 
							key, itemSizeForLang, itemCharsetForLang);
					log.warn(errorMessage, e);
					throw new BodyFormatException(errorMessage);
				} catch (IllegalArgumentException e) {
					String errorMessage = 
							String.format("IllegalArgumentException::%s.%s itemSizeForLang=[%d], itemCharset=[%s], message=[%s]", 
									parentPath, key, itemSizeForLang, itemCharsetForLang, e.getMessage());
					log.warn(errorMessage, e);
					throw new BodyFormatException(errorMessage);
				}
			} else {
				ArrayData arrayData = (ArrayData) itemValueHash
						.get(key);
				if (null == arrayData) {
					arrayData = new ArrayData(currentPath, this,
							(ArrayInfo) itemInfo);
					itemValueHash.put(key, arrayData);
	
				}
				int arrayDataSize = arrayData.size();
				
				JSONArray jsonArrayData = (JSONArray)jsonObj.get(key);
				if (null == jsonArrayData) {
					String errorMessage = 
							String.format("%s 배열 항목[%s]에 대응하는 json 배열 항목이 존재하지 않습니다.::%s", 
									parentPath, key, jsonObj.toJSONString());
					throw new BodyFormatException(errorMessage);
				}
				int jsonArrayDataSize = jsonArrayData.size();
				if (arrayDataSize != jsonArrayDataSize) {
					String errorMessage = 
							String.format("%s 배열 항목[%s]의 크기[%d]가 json 배열 항목의 크기[%d] 가 다릅니다.", 
									parentPath, key, arrayDataSize, jsonArrayDataSize);
					throw new BodyFormatException(errorMessage);
				}
				
				for (int j = 0; j < arrayDataSize; j++) {
					JSONObject jsonObjOfArray = (JSONObject)jsonArrayData.get(j);
					ItemGroupDataIF itemGroupData = arrayData.get(j);
					itemGroupData.JSON2M(jsonObjOfArray, djsonSingleItemConverter);
				}
				
			}
		}
	}
	
	public void M2JSON(JSONObject jsonObj, DJSONSingleItemConverter djsonSingleItemConverter) throws BodyFormatException {
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
					String errorMessage = String.format("OutOfMemoryError::%s.%s value=[%s], itemSizeForLang=[%d], itemCharset=[%s]", parentPath, 
							key, value, itemSizeForLang, itemCharsetForLang);
					log.warn(errorMessage, e);
					throw new BodyFormatException(errorMessage);
				} catch (IllegalArgumentException e) {
					String errorMessage = 
							String.format("IllegalArgumentException::%s.%s value=[%s], itemSizeForLang=[%d], itemCharset=[%s], message=[%s]", 
									parentPath, key, value, itemSizeForLang, itemCharsetForLang, e.getMessage());
					log.warn(errorMessage, e);
					throw new BodyFormatException(errorMessage);
				}
			} else {
				ArrayData arrayData = (ArrayData) itemValueHash.get(key);
				int arrayDataSize = arrayData.size();
				
				JSONArray jsonArrayData = (JSONArray)jsonObj.get(key);
				if (null == jsonArrayData) {
					String errorMessage = 
							String.format("%s 배열 항목[%s]에 대응하는 json 배열 항목이 존재하지 않습니다.::%s", 
									parentPath, key, jsonObj.toJSONString());
					throw new BodyFormatException(errorMessage);
				}
				int jsonArrayDataSize = jsonArrayData.size();
				if (arrayDataSize != jsonArrayDataSize) {
					String errorMessage = 
							String.format("%s 배열 항목[%s]의 크기[%d]가 json 배열 항목의 크기[%d] 가 다릅니다.", 
									parentPath, key, arrayDataSize, jsonArrayDataSize);
					throw new BodyFormatException(errorMessage);
				}
				
				for (int j = 0; j < arrayDataSize; j++) {
					JSONObject jsonObjOfArray = (JSONObject)jsonArrayData.get(j);
					ItemGroupDataIF itemGroupData = arrayData.get(j);
					itemGroupData.M2JSON(jsonObjOfArray, djsonSingleItemConverter);
				}				
			}
		}
	}

	@Override
	public Object getAttribute(String key) throws MessageItemException {
		if (null == key) {
			throw new MessageItemException("파라미터 항목 이름이 null 입니다.");
		}
		
		AbstractItemInfo itemInfo = arrayInfo.getItemInfo(key);
		if (null == itemInfo) {
			throw new MessageItemException(String.format(
					"%s[%d] 배열[%s]에 존재하지 않는 항목[%s]입니다.", parentPath, inx,
					arrayName, key));
		}

		if (CommonType.LOGICAL_ITEM_GUBUN.ARRAY == itemInfo
				.getLogicalItemGubun()) {
			// SinnoriArrayInfo arrayInfo = (SinnoriArrayInfo)itemInfo;
			ArrayData arrayData = (ArrayData) itemValueHash.get(key);

			if (null == arrayData) {
				arrayData = new ArrayData(currentPath, this,
						(ArrayInfo) itemInfo);

				itemValueHash.put(key, arrayData);
			}

			return arrayData;
		}
		
		Object retObj = itemValueHash.get(key);
		if (null ==  retObj) {
			SingleItemInfo singleItemInfo = (SingleItemInfo)itemInfo;
			Object defaultValueForLang = singleItemInfo.getItemDefaultValueForLang();
			// itemInfo.g
			if (null != defaultValueForLang) {
				itemValueHash.put(key, defaultValueForLang);
				retObj = defaultValueForLang;
			}
		}

		return retObj;

	}

	@Override
	public void setAttribute(String key, Object itemValue) throws MessageItemException {
		if (null == key) {
			throw new MessageItemException("파라미터 항목 이름이 null 입니다.");
		}
		
		if (null == itemValue) {
			throw new MessageItemException("파라미터 값이 null 입니다.");
		}
		
		// FIXME!
		// log.info(String.format("key=[%s]", key));

		AbstractItemInfo itemInfo = arrayInfo.getItemInfo(key);

		if (null == itemInfo) {
			throw new MessageItemException(String.format(
					"%s[%d] 배열[%s]에 존재하지 않는 항목[%s]입니다.", parentPath, inx,
					arrayName, key));
		}

		CommonType.LOGICAL_ITEM_GUBUN logicalItemGubun = itemInfo
				.getLogicalItemGubun();
		if (CommonType.LOGICAL_ITEM_GUBUN.ARRAY == logicalItemGubun) {
			throw new MessageItemException(String.format(
					"이 메소드는 단일 항목만 허용합니다. %s[%d] 배열[%s] 항목[%s]", parentPath,
					inx, arrayName, key));
		}
		
		SingleItemInfo singleItemInfo = (SingleItemInfo) itemInfo;
		String itemType = singleItemInfo.getItemType();
		Class<?> valueTypeForLang = singleItemInfo.getItemTypeForLang();
		
		if (!valueTypeForLang.isInstance(itemValue)) {
			throw new MessageItemException(String.format(
					"%s[%d] 항목[%s]의 타입[%s::%s] 과 값의 타입[%s]이 일치하지 않습니다.",
					parentPath, inx, key, itemType, valueTypeForLang.getName(),
					itemValue.getClass().getName()));
		}
		
		int itemTypeID = singleItemInfo.getItemTypeID();
		Charset itemCharsetForLang = singleItemInfo.getItemCharsetForLang();
		int itemSizeForLang = singleItemInfo.getItemSizeForLang();
		itemTypeManger.checkValue(itemTypeID, itemCharsetForLang, itemSizeForLang, itemValue);

		itemValueHash.put(key, itemValue);
	}

	
	public String toJSONString() {
		StringBuilder strBuilder = new StringBuilder();
		
		int itemInfoListSize = itemInfoList.size();
		for (int i = 0; i < itemInfoListSize; i++) {
		// Iterator<AbstractItemInfo> itemInfoList = arrayInfo.getItemInfoList();
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
	
}
