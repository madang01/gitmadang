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
package kr.pe.sinnori.impl.message.AllDataType;

import java.nio.charset.Charset;
import kr.pe.sinnori.common.exception.BodyFormatException;
import kr.pe.sinnori.common.message.AbstractMessage;
import kr.pe.sinnori.common.message.codec.MessageDecoder;
import kr.pe.sinnori.common.protocol.SingleItemDecoderIF;

/**
 * AllDataType 메시지 디코더
 * @author Jonghoon won
 *
 */
public final class AllDataTypeDecoder extends MessageDecoder {

	/**
	 * <pre>
	 *  "단일항목 디코더"를 이용하여 "중간 다리 역활 읽기 객체" 에서 추출된 AllDataType 메시지를 반환한다.
	 * </pre>
	 * @param singleItemDecoder 단일항목 디코더
	 * @param charsetOfProject 프로젝트 문자셋
	 * @param middleReadObj 중간 다리 역활 읽기 객체
	 * @return "단일항목 디코더"를 이용하여 "중간 다리 역활 읽기 객체" 에서 추출된 AllDataType 메시지
	 * @throws OutOfMemoryError 메모리 확보 실패시 던지는 예외
	 * @throws BodyFormatException 바디 디코딩 실패시 던지는 예외
	 */
	@Override
	protected AbstractMessage decodeBody(SingleItemDecoderIF singleItemDecoder, Charset charsetOfProject, Object  middleReadObj) throws OutOfMemoryError, BodyFormatException {
		AllDataType allDataType = new AllDataType();
		String sigleItemPath0 = "AllDataType";

		allDataType.setByteVar1((Byte)
		singleItemDecoder.getValueFromMiddleReadObj(sigleItemPath0
		, "byteVar1" // itemName
		, 0 // itemTypeID
		, "byte" // itemTypeName
		, -1 // itemSizeForLang
		, null // itemCharset,
		, charsetOfProject
		, middleReadObj));

		allDataType.setByteVar2((Byte)
		singleItemDecoder.getValueFromMiddleReadObj(sigleItemPath0
		, "byteVar2" // itemName
		, 0 // itemTypeID
		, "byte" // itemTypeName
		, -1 // itemSizeForLang
		, null // itemCharset,
		, charsetOfProject
		, middleReadObj));

		allDataType.setByteVar3((Byte)
		singleItemDecoder.getValueFromMiddleReadObj(sigleItemPath0
		, "byteVar3" // itemName
		, 0 // itemTypeID
		, "byte" // itemTypeName
		, -1 // itemSizeForLang
		, null // itemCharset,
		, charsetOfProject
		, middleReadObj));

		allDataType.setUnsignedByteVar1((Short)
		singleItemDecoder.getValueFromMiddleReadObj(sigleItemPath0
		, "unsignedByteVar1" // itemName
		, 1 // itemTypeID
		, "unsigned byte" // itemTypeName
		, -1 // itemSizeForLang
		, null // itemCharset,
		, charsetOfProject
		, middleReadObj));

		allDataType.setUnsignedByteVar2((Short)
		singleItemDecoder.getValueFromMiddleReadObj(sigleItemPath0
		, "unsignedByteVar2" // itemName
		, 1 // itemTypeID
		, "unsigned byte" // itemTypeName
		, -1 // itemSizeForLang
		, null // itemCharset,
		, charsetOfProject
		, middleReadObj));

		allDataType.setUnsignedByteVar3((Short)
		singleItemDecoder.getValueFromMiddleReadObj(sigleItemPath0
		, "unsignedByteVar3" // itemName
		, 1 // itemTypeID
		, "unsigned byte" // itemTypeName
		, -1 // itemSizeForLang
		, null // itemCharset,
		, charsetOfProject
		, middleReadObj));

		allDataType.setShortVar1((Short)
		singleItemDecoder.getValueFromMiddleReadObj(sigleItemPath0
		, "shortVar1" // itemName
		, 2 // itemTypeID
		, "short" // itemTypeName
		, -1 // itemSizeForLang
		, null // itemCharset,
		, charsetOfProject
		, middleReadObj));

		allDataType.setShortVar2((Short)
		singleItemDecoder.getValueFromMiddleReadObj(sigleItemPath0
		, "shortVar2" // itemName
		, 2 // itemTypeID
		, "short" // itemTypeName
		, -1 // itemSizeForLang
		, null // itemCharset,
		, charsetOfProject
		, middleReadObj));

		allDataType.setShortVar3((Short)
		singleItemDecoder.getValueFromMiddleReadObj(sigleItemPath0
		, "shortVar3" // itemName
		, 2 // itemTypeID
		, "short" // itemTypeName
		, -1 // itemSizeForLang
		, null // itemCharset,
		, charsetOfProject
		, middleReadObj));

		allDataType.setUnsignedShortVar1((Integer)
		singleItemDecoder.getValueFromMiddleReadObj(sigleItemPath0
		, "unsignedShortVar1" // itemName
		, 3 // itemTypeID
		, "unsigned short" // itemTypeName
		, -1 // itemSizeForLang
		, null // itemCharset,
		, charsetOfProject
		, middleReadObj));

		allDataType.setUnsignedShortVar2((Integer)
		singleItemDecoder.getValueFromMiddleReadObj(sigleItemPath0
		, "unsignedShortVar2" // itemName
		, 3 // itemTypeID
		, "unsigned short" // itemTypeName
		, -1 // itemSizeForLang
		, null // itemCharset,
		, charsetOfProject
		, middleReadObj));

		allDataType.setUnsignedShortVar3((Integer)
		singleItemDecoder.getValueFromMiddleReadObj(sigleItemPath0
		, "unsignedShortVar3" // itemName
		, 3 // itemTypeID
		, "unsigned short" // itemTypeName
		, -1 // itemSizeForLang
		, null // itemCharset,
		, charsetOfProject
		, middleReadObj));

		allDataType.setIntVar1((Integer)
		singleItemDecoder.getValueFromMiddleReadObj(sigleItemPath0
		, "intVar1" // itemName
		, 4 // itemTypeID
		, "integer" // itemTypeName
		, -1 // itemSizeForLang
		, null // itemCharset,
		, charsetOfProject
		, middleReadObj));

		allDataType.setIntVar2((Integer)
		singleItemDecoder.getValueFromMiddleReadObj(sigleItemPath0
		, "intVar2" // itemName
		, 4 // itemTypeID
		, "integer" // itemTypeName
		, -1 // itemSizeForLang
		, null // itemCharset,
		, charsetOfProject
		, middleReadObj));

		allDataType.setIntVar3((Integer)
		singleItemDecoder.getValueFromMiddleReadObj(sigleItemPath0
		, "intVar3" // itemName
		, 4 // itemTypeID
		, "integer" // itemTypeName
		, -1 // itemSizeForLang
		, null // itemCharset,
		, charsetOfProject
		, middleReadObj));

		allDataType.setUnsignedIntVar1((Long)
		singleItemDecoder.getValueFromMiddleReadObj(sigleItemPath0
		, "unsignedIntVar1" // itemName
		, 5 // itemTypeID
		, "unsigned integer" // itemTypeName
		, -1 // itemSizeForLang
		, null // itemCharset,
		, charsetOfProject
		, middleReadObj));

		allDataType.setUnsignedIntVar2((Long)
		singleItemDecoder.getValueFromMiddleReadObj(sigleItemPath0
		, "unsignedIntVar2" // itemName
		, 5 // itemTypeID
		, "unsigned integer" // itemTypeName
		, -1 // itemSizeForLang
		, null // itemCharset,
		, charsetOfProject
		, middleReadObj));

		allDataType.setUnsignedIntVar3((Long)
		singleItemDecoder.getValueFromMiddleReadObj(sigleItemPath0
		, "unsignedIntVar3" // itemName
		, 5 // itemTypeID
		, "unsigned integer" // itemTypeName
		, -1 // itemSizeForLang
		, null // itemCharset,
		, charsetOfProject
		, middleReadObj));

		allDataType.setLongVar1((Long)
		singleItemDecoder.getValueFromMiddleReadObj(sigleItemPath0
		, "longVar1" // itemName
		, 6 // itemTypeID
		, "long" // itemTypeName
		, -1 // itemSizeForLang
		, null // itemCharset,
		, charsetOfProject
		, middleReadObj));

		allDataType.setLongVar2((Long)
		singleItemDecoder.getValueFromMiddleReadObj(sigleItemPath0
		, "longVar2" // itemName
		, 6 // itemTypeID
		, "long" // itemTypeName
		, -1 // itemSizeForLang
		, null // itemCharset,
		, charsetOfProject
		, middleReadObj));

		allDataType.setLongVar3((Long)
		singleItemDecoder.getValueFromMiddleReadObj(sigleItemPath0
		, "longVar3" // itemName
		, 6 // itemTypeID
		, "long" // itemTypeName
		, -1 // itemSizeForLang
		, null // itemCharset,
		, charsetOfProject
		, middleReadObj));

		allDataType.setStrVar1((String)
		singleItemDecoder.getValueFromMiddleReadObj(sigleItemPath0
		, "strVar1" // itemName
		, 7 // itemTypeID
		, "ub pascal string" // itemTypeName
		, -1 // itemSizeForLang
		, null // itemCharset,
		, charsetOfProject
		, middleReadObj));

		allDataType.setStrVar2((String)
		singleItemDecoder.getValueFromMiddleReadObj(sigleItemPath0
		, "strVar2" // itemName
		, 8 // itemTypeID
		, "us pascal string" // itemTypeName
		, -1 // itemSizeForLang
		, null // itemCharset,
		, charsetOfProject
		, middleReadObj));

		allDataType.setStrVar3((String)
		singleItemDecoder.getValueFromMiddleReadObj(sigleItemPath0
		, "strVar3" // itemName
		, 9 // itemTypeID
		, "si pascal string" // itemTypeName
		, -1 // itemSizeForLang
		, null // itemCharset,
		, charsetOfProject
		, middleReadObj));

		allDataType.setBytesVar1((byte[])
		singleItemDecoder.getValueFromMiddleReadObj(sigleItemPath0
		, "bytesVar1" // itemName
		, 14 // itemTypeID
		, "fixed length byte[]" // itemTypeName
		, 7 // itemSizeForLang
		, null // itemCharset,
		, charsetOfProject
		, middleReadObj));

		allDataType.setBytesVar2((byte[])
		singleItemDecoder.getValueFromMiddleReadObj(sigleItemPath0
		, "bytesVar2" // itemName
		, 13 // itemTypeID
		, "si variable length byte[]" // itemTypeName
		, -1 // itemSizeForLang
		, null // itemCharset,
		, charsetOfProject
		, middleReadObj));

		allDataType.setCnt((Integer)
		singleItemDecoder.getValueFromMiddleReadObj(sigleItemPath0
		, "cnt" // itemName
		, 4 // itemTypeID
		, "integer" // itemTypeName
		, -1 // itemSizeForLang
		, null // itemCharset,
		, charsetOfProject
		, middleReadObj));

		Object memberMiddleReadArray = singleItemDecoder.getArrayObjFromMiddleReadObj(sigleItemPath0, "member", allDataType.getCnt(), middleReadObj);
		AllDataType.Member[] memberList = new AllDataType.Member[allDataType.getCnt()];
		for (int i=0; i < memberList.length; i++) {
			String sigleItemPath1 = new StringBuilder(sigleItemPath0).append(".").append("Member[").append(i).append("]").toString();
			Object memberMiddleReadObj = singleItemDecoder.getMiddleReadObjFromArrayObj(sigleItemPath1, memberMiddleReadArray, i);
			memberList[i] = allDataType. new Member();

			memberList[i].setMemberID((String)
			singleItemDecoder.getValueFromMiddleReadObj(sigleItemPath1
			, "memberID" // itemName
			, 10 // itemTypeID
			, "fixed length string" // itemTypeName
			, 30 // itemSizeForLang
			, null // itemCharset,
			, charsetOfProject
			, memberMiddleReadObj));

			memberList[i].setMemberName((String)
			singleItemDecoder.getValueFromMiddleReadObj(sigleItemPath1
			, "memberName" // itemName
			, 10 // itemTypeID
			, "fixed length string" // itemTypeName
			, 30 // itemSizeForLang
			, null // itemCharset,
			, charsetOfProject
			, memberMiddleReadObj));

			memberList[i].setCnt((Integer)
			singleItemDecoder.getValueFromMiddleReadObj(sigleItemPath1
			, "cnt" // itemName
			, 4 // itemTypeID
			, "integer" // itemTypeName
			, -1 // itemSizeForLang
			, null // itemCharset,
			, charsetOfProject
			, memberMiddleReadObj));

			Object itemMiddleReadArray = singleItemDecoder.getArrayObjFromMiddleReadObj(sigleItemPath1, "item", memberList[i].getCnt(), memberMiddleReadObj);
			AllDataType.Member.Item[] itemList = new AllDataType.Member.Item[memberList[i].getCnt()];
			for (int ii=0; ii < itemList.length; ii++) {
				String sigleItemPath2 = new StringBuilder(sigleItemPath1).append(".").append("Item[").append(ii).append("]").toString();
				Object itemMiddleReadObj = singleItemDecoder.getMiddleReadObjFromArrayObj(sigleItemPath2, itemMiddleReadArray, ii);
				itemList[ii] = memberList[i]. new Item();

				itemList[ii].setItemID((String)
				singleItemDecoder.getValueFromMiddleReadObj(sigleItemPath2
				, "itemID" // itemName
				, 10 // itemTypeID
				, "fixed length string" // itemTypeName
				, 30 // itemSizeForLang
				, null // itemCharset,
				, charsetOfProject
				, itemMiddleReadObj));

				itemList[ii].setItemName((String)
				singleItemDecoder.getValueFromMiddleReadObj(sigleItemPath2
				, "itemName" // itemName
				, 10 // itemTypeID
				, "fixed length string" // itemTypeName
				, 30 // itemSizeForLang
				, null // itemCharset,
				, charsetOfProject
				, itemMiddleReadObj));

				itemList[ii].setItemCnt((Integer)
				singleItemDecoder.getValueFromMiddleReadObj(sigleItemPath2
				, "itemCnt" // itemName
				, 4 // itemTypeID
				, "integer" // itemTypeName
				, -1 // itemSizeForLang
				, null // itemCharset,
				, charsetOfProject
				, itemMiddleReadObj));
	memberList[i].setItemList(itemList);
			}
allDataType.setMemberList(memberList);
		}
		return allDataType;
	}
}