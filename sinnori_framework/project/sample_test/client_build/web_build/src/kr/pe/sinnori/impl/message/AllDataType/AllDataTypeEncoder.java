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
import java.util.LinkedList;
import kr.pe.sinnori.common.message.AbstractMessage;
import kr.pe.sinnori.common.message.codec.MessageEncoder;
import kr.pe.sinnori.common.protocol.SingleItemEncoderIF;

/**
 * AllDataType 메시지 인코더
 * @author Won Jonghoon
 *
 */
public final class AllDataTypeEncoder extends MessageEncoder {
	@Override
	public void encode(AbstractMessage messageObj, SingleItemEncoderIF singleItemEncoder, Charset charsetOfProject, Object middleWriteObj)
			throws Exception {
		if (!(messageObj instanceof AllDataType)) {
			String errorMessage = String.format("메시지 객체 타입[%s]이 AllDataType 이(가) 아닙니다.", messageObj.getClass().getCanonicalName());
			throw new IllegalArgumentException(errorMessage);
		}
		
		AllDataType allDataType = (AllDataType) messageObj;
		encodeBody(allDataType, singleItemEncoder, charsetOfProject, middleWriteObj);
	}

	/**
	 * <pre>
	 * AllDataType 입력 메시지의 내용을 "단일항목 인코더"를 이용하여 "중간 다리 역활 쓰기 객체"에 저장한다.
	 * </pre>
	 * @param allDataType AllDataType 입력 메시지
	 * @param singleItemEncoder 단일항목 인코더
	 * @param charsetOfProject 프로젝트 문자셋
	 * @param middleWriteObj 중간 다리 역활 쓰기 객체
	 * @throws Exception "입력/출력 메시지"의 내용을 "단일항목 인코더"를 이용하여 "중간 다리 역활 쓰기 객체"에 저장할때 에러 발생시 던지는 예외
	 */
	private void encodeBody(AllDataType allDataType, SingleItemEncoderIF singleItemEncoder, Charset charsetOfProject, Object middleWriteObj) throws Exception {
		String allDataTypeSingleItemPath = "AllDataType";
		LinkedList<String> singleItemPathStatck = new LinkedList<String>();
		singleItemPathStatck.push(allDataTypeSingleItemPath);

		singleItemEncoder.putValueToMiddleWriteObj(allDataTypeSingleItemPath, "byteVar1"
					, 0 // itemTypeID
					, "byte" // itemTypeName
					, allDataType.getByteVar1() // itemValue
					, -1 // itemSizeForLang
					, null // itemCharset,
					, charsetOfProject
					, middleWriteObj);
		singleItemEncoder.putValueToMiddleWriteObj(allDataTypeSingleItemPath, "byteVar2"
					, 0 // itemTypeID
					, "byte" // itemTypeName
					, allDataType.getByteVar2() // itemValue
					, -1 // itemSizeForLang
					, null // itemCharset,
					, charsetOfProject
					, middleWriteObj);
		singleItemEncoder.putValueToMiddleWriteObj(allDataTypeSingleItemPath, "byteVar3"
					, 0 // itemTypeID
					, "byte" // itemTypeName
					, allDataType.getByteVar3() // itemValue
					, -1 // itemSizeForLang
					, null // itemCharset,
					, charsetOfProject
					, middleWriteObj);
		singleItemEncoder.putValueToMiddleWriteObj(allDataTypeSingleItemPath, "unsignedByteVar1"
					, 1 // itemTypeID
					, "unsigned byte" // itemTypeName
					, allDataType.getUnsignedByteVar1() // itemValue
					, -1 // itemSizeForLang
					, null // itemCharset,
					, charsetOfProject
					, middleWriteObj);
		singleItemEncoder.putValueToMiddleWriteObj(allDataTypeSingleItemPath, "unsignedByteVar2"
					, 1 // itemTypeID
					, "unsigned byte" // itemTypeName
					, allDataType.getUnsignedByteVar2() // itemValue
					, -1 // itemSizeForLang
					, null // itemCharset,
					, charsetOfProject
					, middleWriteObj);
		singleItemEncoder.putValueToMiddleWriteObj(allDataTypeSingleItemPath, "unsignedByteVar3"
					, 1 // itemTypeID
					, "unsigned byte" // itemTypeName
					, allDataType.getUnsignedByteVar3() // itemValue
					, -1 // itemSizeForLang
					, null // itemCharset,
					, charsetOfProject
					, middleWriteObj);
		singleItemEncoder.putValueToMiddleWriteObj(allDataTypeSingleItemPath, "shortVar1"
					, 2 // itemTypeID
					, "short" // itemTypeName
					, allDataType.getShortVar1() // itemValue
					, -1 // itemSizeForLang
					, null // itemCharset,
					, charsetOfProject
					, middleWriteObj);
		singleItemEncoder.putValueToMiddleWriteObj(allDataTypeSingleItemPath, "shortVar2"
					, 2 // itemTypeID
					, "short" // itemTypeName
					, allDataType.getShortVar2() // itemValue
					, -1 // itemSizeForLang
					, null // itemCharset,
					, charsetOfProject
					, middleWriteObj);
		singleItemEncoder.putValueToMiddleWriteObj(allDataTypeSingleItemPath, "shortVar3"
					, 2 // itemTypeID
					, "short" // itemTypeName
					, allDataType.getShortVar3() // itemValue
					, -1 // itemSizeForLang
					, null // itemCharset,
					, charsetOfProject
					, middleWriteObj);
		singleItemEncoder.putValueToMiddleWriteObj(allDataTypeSingleItemPath, "unsignedShortVar1"
					, 3 // itemTypeID
					, "unsigned short" // itemTypeName
					, allDataType.getUnsignedShortVar1() // itemValue
					, -1 // itemSizeForLang
					, null // itemCharset,
					, charsetOfProject
					, middleWriteObj);
		singleItemEncoder.putValueToMiddleWriteObj(allDataTypeSingleItemPath, "unsignedShortVar2"
					, 3 // itemTypeID
					, "unsigned short" // itemTypeName
					, allDataType.getUnsignedShortVar2() // itemValue
					, -1 // itemSizeForLang
					, null // itemCharset,
					, charsetOfProject
					, middleWriteObj);
		singleItemEncoder.putValueToMiddleWriteObj(allDataTypeSingleItemPath, "unsignedShortVar3"
					, 3 // itemTypeID
					, "unsigned short" // itemTypeName
					, allDataType.getUnsignedShortVar3() // itemValue
					, -1 // itemSizeForLang
					, null // itemCharset,
					, charsetOfProject
					, middleWriteObj);
		singleItemEncoder.putValueToMiddleWriteObj(allDataTypeSingleItemPath, "intVar1"
					, 4 // itemTypeID
					, "integer" // itemTypeName
					, allDataType.getIntVar1() // itemValue
					, -1 // itemSizeForLang
					, null // itemCharset,
					, charsetOfProject
					, middleWriteObj);
		singleItemEncoder.putValueToMiddleWriteObj(allDataTypeSingleItemPath, "intVar2"
					, 4 // itemTypeID
					, "integer" // itemTypeName
					, allDataType.getIntVar2() // itemValue
					, -1 // itemSizeForLang
					, null // itemCharset,
					, charsetOfProject
					, middleWriteObj);
		singleItemEncoder.putValueToMiddleWriteObj(allDataTypeSingleItemPath, "intVar3"
					, 4 // itemTypeID
					, "integer" // itemTypeName
					, allDataType.getIntVar3() // itemValue
					, -1 // itemSizeForLang
					, null // itemCharset,
					, charsetOfProject
					, middleWriteObj);
		singleItemEncoder.putValueToMiddleWriteObj(allDataTypeSingleItemPath, "unsignedIntVar1"
					, 5 // itemTypeID
					, "unsigned integer" // itemTypeName
					, allDataType.getUnsignedIntVar1() // itemValue
					, -1 // itemSizeForLang
					, null // itemCharset,
					, charsetOfProject
					, middleWriteObj);
		singleItemEncoder.putValueToMiddleWriteObj(allDataTypeSingleItemPath, "unsignedIntVar2"
					, 5 // itemTypeID
					, "unsigned integer" // itemTypeName
					, allDataType.getUnsignedIntVar2() // itemValue
					, -1 // itemSizeForLang
					, null // itemCharset,
					, charsetOfProject
					, middleWriteObj);
		singleItemEncoder.putValueToMiddleWriteObj(allDataTypeSingleItemPath, "unsignedIntVar3"
					, 5 // itemTypeID
					, "unsigned integer" // itemTypeName
					, allDataType.getUnsignedIntVar3() // itemValue
					, -1 // itemSizeForLang
					, null // itemCharset,
					, charsetOfProject
					, middleWriteObj);
		singleItemEncoder.putValueToMiddleWriteObj(allDataTypeSingleItemPath, "longVar1"
					, 6 // itemTypeID
					, "long" // itemTypeName
					, allDataType.getLongVar1() // itemValue
					, -1 // itemSizeForLang
					, null // itemCharset,
					, charsetOfProject
					, middleWriteObj);
		singleItemEncoder.putValueToMiddleWriteObj(allDataTypeSingleItemPath, "longVar2"
					, 6 // itemTypeID
					, "long" // itemTypeName
					, allDataType.getLongVar2() // itemValue
					, -1 // itemSizeForLang
					, null // itemCharset,
					, charsetOfProject
					, middleWriteObj);
		singleItemEncoder.putValueToMiddleWriteObj(allDataTypeSingleItemPath, "longVar3"
					, 6 // itemTypeID
					, "long" // itemTypeName
					, allDataType.getLongVar3() // itemValue
					, -1 // itemSizeForLang
					, null // itemCharset,
					, charsetOfProject
					, middleWriteObj);
		singleItemEncoder.putValueToMiddleWriteObj(allDataTypeSingleItemPath, "strVar1"
					, 7 // itemTypeID
					, "ub pascal string" // itemTypeName
					, allDataType.getStrVar1() // itemValue
					, -1 // itemSizeForLang
					, null // itemCharset,
					, charsetOfProject
					, middleWriteObj);
		singleItemEncoder.putValueToMiddleWriteObj(allDataTypeSingleItemPath, "strVar2"
					, 8 // itemTypeID
					, "us pascal string" // itemTypeName
					, allDataType.getStrVar2() // itemValue
					, -1 // itemSizeForLang
					, null // itemCharset,
					, charsetOfProject
					, middleWriteObj);
		singleItemEncoder.putValueToMiddleWriteObj(allDataTypeSingleItemPath, "strVar3"
					, 9 // itemTypeID
					, "si pascal string" // itemTypeName
					, allDataType.getStrVar3() // itemValue
					, -1 // itemSizeForLang
					, null // itemCharset,
					, charsetOfProject
					, middleWriteObj);
		singleItemEncoder.putValueToMiddleWriteObj(allDataTypeSingleItemPath, "bytesVar1"
					, 14 // itemTypeID
					, "fixed length byte[]" // itemTypeName
					, allDataType.getBytesVar1() // itemValue
					, 7 // itemSizeForLang
					, null // itemCharset,
					, charsetOfProject
					, middleWriteObj);
		singleItemEncoder.putValueToMiddleWriteObj(allDataTypeSingleItemPath, "bytesVar2"
					, 13 // itemTypeID
					, "si variable length byte[]" // itemTypeName
					, allDataType.getBytesVar2() // itemValue
					, -1 // itemSizeForLang
					, null // itemCharset,
					, charsetOfProject
					, middleWriteObj);
		singleItemEncoder.putValueToMiddleWriteObj(allDataTypeSingleItemPath, "sqldate"
					, 15 // itemTypeID
					, "java sql date" // itemTypeName
					, allDataType.getSqldate() // itemValue
					, -1 // itemSizeForLang
					, null // itemCharset,
					, charsetOfProject
					, middleWriteObj);
		singleItemEncoder.putValueToMiddleWriteObj(allDataTypeSingleItemPath, "sqltimestamp"
					, 16 // itemTypeID
					, "java sql timestamp" // itemTypeName
					, allDataType.getSqltimestamp() // itemValue
					, -1 // itemSizeForLang
					, null // itemCharset,
					, charsetOfProject
					, middleWriteObj);
		singleItemEncoder.putValueToMiddleWriteObj(allDataTypeSingleItemPath, "cnt"
					, 4 // itemTypeID
					, "integer" // itemTypeName
					, allDataType.getCnt() // itemValue
					, -1 // itemSizeForLang
					, null // itemCharset,
					, charsetOfProject
					, middleWriteObj);

		java.util.List<AllDataType.Member> memberList = allDataType.getMemberList();

		/** 배열 정보와 배열 크기 일치 검사 */
		if (null == memberList) {
			/** 배열 크기 지정 방식이 간접일 경우 참조하는 변수값이 0 일 경우만 배열 값으로 null 을 허용한다. */
			if (0 != allDataType.getCnt()) {
				String errorMessage = new StringBuilder("간접 참조 회수[")
				.append(allDataType.getCnt())
				.append("] is not zero but ")
				.append(allDataTypeSingleItemPath)
				.append(".")
				.append("memberList")
				.append("is null").toString();
				throw new kr.pe.sinnori.common.exception.BodyFormatException(errorMessage);
			}
		} else {
			int memberListSize = memberList.size();
			/** 배열 값이 null 이 아닐때에는 배열 크기가 배열 정보에서 지정된 크기와 같은지 검사 */
			if (memberListSize != allDataType.getCnt()) {
				String errorMessage = new StringBuilder(allDataTypeSingleItemPath)
				.append(".")
				.append("memberList.length[")
				.append(memberListSize)
				.append("] is not same to ")
				.append(allDataTypeSingleItemPath)
				.append(".")
				.append("cnt[")
				.append(allDataType.getCnt())
				.append("]").toString();
				throw new kr.pe.sinnori.common.exception.BodyFormatException(errorMessage);
			}

			Object memberMiddleWriteArray = singleItemEncoder.getArrayObjFromMiddleWriteObj(allDataTypeSingleItemPath, "member", memberListSize, middleWriteObj);
			for (int i=0; i < memberListSize; i++) {
				singleItemPathStatck.push(new StringBuilder(singleItemPathStatck.getLast()).append(".").append("Member").append("[").append(i).append("]").toString());
				String memberSingleItemPath = singleItemPathStatck.getLast();
				Object memberMiddleWriteObj = singleItemEncoder.getMiddleWriteObjFromArrayObj(memberSingleItemPath, memberMiddleWriteArray, i);
				AllDataType.Member member = memberList.get(i);
				singleItemEncoder.putValueToMiddleWriteObj(memberSingleItemPath, "memberID"
							, 10 // itemTypeID
							, "fixed length string" // itemTypeName
							, member.getMemberID() // itemValue
							, 30 // itemSizeForLang
							, null // itemCharset,
							, charsetOfProject
							, memberMiddleWriteObj);
				singleItemEncoder.putValueToMiddleWriteObj(memberSingleItemPath, "memberName"
							, 10 // itemTypeID
							, "fixed length string" // itemTypeName
							, member.getMemberName() // itemValue
							, 30 // itemSizeForLang
							, null // itemCharset,
							, charsetOfProject
							, memberMiddleWriteObj);
				singleItemEncoder.putValueToMiddleWriteObj(memberSingleItemPath, "cnt"
							, 4 // itemTypeID
							, "integer" // itemTypeName
							, member.getCnt() // itemValue
							, -1 // itemSizeForLang
							, null // itemCharset,
							, charsetOfProject
							, memberMiddleWriteObj);

				java.util.List<AllDataType.Member.Item> itemList = member.getItemList();

				/** 배열 정보와 배열 크기 일치 검사 */
				if (null == itemList) {
					/** 배열 크기 지정 방식이 간접일 경우 참조하는 변수값이 0 일 경우만 배열 값으로 null 을 허용한다. */
					if (0 != member.getCnt()) {
						String errorMessage = new StringBuilder("간접 참조 회수[")
				.append(member.getCnt())
				.append("] is not zero but ")
				.append(memberSingleItemPath)
						.append(".")
						.append("itemList")
						.append("is null").toString();
						throw new kr.pe.sinnori.common.exception.BodyFormatException(errorMessage);
					}
				} else {
					int itemListSize = itemList.size();
					/** 배열 값이 null 이 아닐때에는 배열 크기가 배열 정보에서 지정된 크기와 같은지 검사 */
					if (itemListSize != member.getCnt()) {
						String errorMessage = new StringBuilder(memberSingleItemPath)
						.append(".")
						.append("itemList.length[")
						.append(itemListSize)
						.append("] is not same to ")
						.append(memberSingleItemPath)
						.append(".")
						.append("cnt[")
						.append(member.getCnt())
						.append("]").toString();
						throw new kr.pe.sinnori.common.exception.BodyFormatException(errorMessage);
					}

					Object itemMiddleWriteArray = singleItemEncoder.getArrayObjFromMiddleWriteObj(memberSingleItemPath, "item", itemListSize, memberMiddleWriteObj);
					for (int ii=0; ii < itemListSize; ii++) {
						singleItemPathStatck.push(new StringBuilder(singleItemPathStatck.getLast()).append(".").append("Item").append("[").append(ii).append("]").toString());
						String itemSingleItemPath = singleItemPathStatck.getLast();
						Object itemMiddleWriteObj = singleItemEncoder.getMiddleWriteObjFromArrayObj(itemSingleItemPath, itemMiddleWriteArray, ii);
						AllDataType.Member.Item item = itemList.get(ii);
						singleItemEncoder.putValueToMiddleWriteObj(itemSingleItemPath, "itemID"
									, 10 // itemTypeID
									, "fixed length string" // itemTypeName
									, item.getItemID() // itemValue
									, 30 // itemSizeForLang
									, null // itemCharset,
									, charsetOfProject
									, itemMiddleWriteObj);
						singleItemEncoder.putValueToMiddleWriteObj(itemSingleItemPath, "itemName"
									, 10 // itemTypeID
									, "fixed length string" // itemTypeName
									, item.getItemName() // itemValue
									, 30 // itemSizeForLang
									, null // itemCharset,
									, charsetOfProject
									, itemMiddleWriteObj);
						singleItemEncoder.putValueToMiddleWriteObj(itemSingleItemPath, "itemCnt"
									, 4 // itemTypeID
									, "integer" // itemTypeName
									, item.getItemCnt() // itemValue
									, -1 // itemSizeForLang
									, null // itemCharset,
									, charsetOfProject
									, itemMiddleWriteObj);
						singleItemPathStatck.pop();
					}
				}
				singleItemPathStatck.pop();
			}
		}
	}
}