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
package kr.pe.sinnori.impl.message.AllItemTypeReq;

import java.util.LinkedList;
import kr.pe.sinnori.common.message.AbstractMessage;
import kr.pe.sinnori.common.message.builder.info.SingleItemType;
import kr.pe.sinnori.common.message.codec.AbstractMessageEncoder;
import kr.pe.sinnori.common.protocol.SingleItemEncoderIF;

/**
 * AllItemTypeReq 메시지 인코더
 * @author Won Jonghoon
 *
 */
public final class AllItemTypeReqEncoder extends AbstractMessageEncoder {
	@Override
	public void encode(AbstractMessage messageObj, SingleItemEncoderIF singleItemEncoder, Object writableMiddleObject) throws Exception {
		AllItemTypeReq allItemTypeReq = (AllItemTypeReq)messageObj;
		encodeBody(allItemTypeReq, singleItemEncoder, writableMiddleObject);
	}


	private void encodeBody(AllItemTypeReq allItemTypeReq, SingleItemEncoderIF singleItemEncoder, Object middleWritableObject) throws Exception {
		LinkedList<String> pathStack = new LinkedList<String>();
		pathStack.push("AllItemTypeReq");


		singleItemEncoder.putValueToWritableMiddleObject(pathStack.peek(), "byteVar1"
			, SingleItemType.BYTE // itemType
			, allItemTypeReq.getByteVar1() // itemValue
			, -1 // itemSize
			, null // nativeItemCharset
			, middleWritableObject);

		singleItemEncoder.putValueToWritableMiddleObject(pathStack.peek(), "byteVar2"
			, SingleItemType.BYTE // itemType
			, allItemTypeReq.getByteVar2() // itemValue
			, -1 // itemSize
			, null // nativeItemCharset
			, middleWritableObject);

		singleItemEncoder.putValueToWritableMiddleObject(pathStack.peek(), "byteVar3"
			, SingleItemType.BYTE // itemType
			, allItemTypeReq.getByteVar3() // itemValue
			, -1 // itemSize
			, null // nativeItemCharset
			, middleWritableObject);

		singleItemEncoder.putValueToWritableMiddleObject(pathStack.peek(), "unsignedByteVar1"
			, SingleItemType.UNSIGNED_BYTE // itemType
			, allItemTypeReq.getUnsignedByteVar1() // itemValue
			, -1 // itemSize
			, null // nativeItemCharset
			, middleWritableObject);

		singleItemEncoder.putValueToWritableMiddleObject(pathStack.peek(), "unsignedByteVar2"
			, SingleItemType.UNSIGNED_BYTE // itemType
			, allItemTypeReq.getUnsignedByteVar2() // itemValue
			, -1 // itemSize
			, null // nativeItemCharset
			, middleWritableObject);

		singleItemEncoder.putValueToWritableMiddleObject(pathStack.peek(), "unsignedByteVar3"
			, SingleItemType.UNSIGNED_BYTE // itemType
			, allItemTypeReq.getUnsignedByteVar3() // itemValue
			, -1 // itemSize
			, null // nativeItemCharset
			, middleWritableObject);

		singleItemEncoder.putValueToWritableMiddleObject(pathStack.peek(), "shortVar1"
			, SingleItemType.SHORT // itemType
			, allItemTypeReq.getShortVar1() // itemValue
			, -1 // itemSize
			, null // nativeItemCharset
			, middleWritableObject);

		singleItemEncoder.putValueToWritableMiddleObject(pathStack.peek(), "shortVar2"
			, SingleItemType.SHORT // itemType
			, allItemTypeReq.getShortVar2() // itemValue
			, -1 // itemSize
			, null // nativeItemCharset
			, middleWritableObject);

		singleItemEncoder.putValueToWritableMiddleObject(pathStack.peek(), "shortVar3"
			, SingleItemType.SHORT // itemType
			, allItemTypeReq.getShortVar3() // itemValue
			, -1 // itemSize
			, null // nativeItemCharset
			, middleWritableObject);

		singleItemEncoder.putValueToWritableMiddleObject(pathStack.peek(), "unsignedShortVar1"
			, SingleItemType.UNSIGNED_SHORT // itemType
			, allItemTypeReq.getUnsignedShortVar1() // itemValue
			, -1 // itemSize
			, null // nativeItemCharset
			, middleWritableObject);

		singleItemEncoder.putValueToWritableMiddleObject(pathStack.peek(), "unsignedShortVar2"
			, SingleItemType.UNSIGNED_SHORT // itemType
			, allItemTypeReq.getUnsignedShortVar2() // itemValue
			, -1 // itemSize
			, null // nativeItemCharset
			, middleWritableObject);

		singleItemEncoder.putValueToWritableMiddleObject(pathStack.peek(), "unsignedShortVar3"
			, SingleItemType.UNSIGNED_SHORT // itemType
			, allItemTypeReq.getUnsignedShortVar3() // itemValue
			, -1 // itemSize
			, null // nativeItemCharset
			, middleWritableObject);

		singleItemEncoder.putValueToWritableMiddleObject(pathStack.peek(), "intVar1"
			, SingleItemType.INTEGER // itemType
			, allItemTypeReq.getIntVar1() // itemValue
			, -1 // itemSize
			, null // nativeItemCharset
			, middleWritableObject);

		singleItemEncoder.putValueToWritableMiddleObject(pathStack.peek(), "intVar2"
			, SingleItemType.INTEGER // itemType
			, allItemTypeReq.getIntVar2() // itemValue
			, -1 // itemSize
			, null // nativeItemCharset
			, middleWritableObject);

		singleItemEncoder.putValueToWritableMiddleObject(pathStack.peek(), "intVar3"
			, SingleItemType.INTEGER // itemType
			, allItemTypeReq.getIntVar3() // itemValue
			, -1 // itemSize
			, null // nativeItemCharset
			, middleWritableObject);

		singleItemEncoder.putValueToWritableMiddleObject(pathStack.peek(), "unsignedIntVar1"
			, SingleItemType.UNSIGNED_INTEGER // itemType
			, allItemTypeReq.getUnsignedIntVar1() // itemValue
			, -1 // itemSize
			, null // nativeItemCharset
			, middleWritableObject);

		singleItemEncoder.putValueToWritableMiddleObject(pathStack.peek(), "unsignedIntVar2"
			, SingleItemType.UNSIGNED_INTEGER // itemType
			, allItemTypeReq.getUnsignedIntVar2() // itemValue
			, -1 // itemSize
			, null // nativeItemCharset
			, middleWritableObject);

		singleItemEncoder.putValueToWritableMiddleObject(pathStack.peek(), "unsignedIntVar3"
			, SingleItemType.UNSIGNED_INTEGER // itemType
			, allItemTypeReq.getUnsignedIntVar3() // itemValue
			, -1 // itemSize
			, null // nativeItemCharset
			, middleWritableObject);

		singleItemEncoder.putValueToWritableMiddleObject(pathStack.peek(), "longVar1"
			, SingleItemType.LONG // itemType
			, allItemTypeReq.getLongVar1() // itemValue
			, -1 // itemSize
			, null // nativeItemCharset
			, middleWritableObject);

		singleItemEncoder.putValueToWritableMiddleObject(pathStack.peek(), "longVar2"
			, SingleItemType.LONG // itemType
			, allItemTypeReq.getLongVar2() // itemValue
			, -1 // itemSize
			, null // nativeItemCharset
			, middleWritableObject);

		singleItemEncoder.putValueToWritableMiddleObject(pathStack.peek(), "longVar3"
			, SingleItemType.LONG // itemType
			, allItemTypeReq.getLongVar3() // itemValue
			, -1 // itemSize
			, null // nativeItemCharset
			, middleWritableObject);

		singleItemEncoder.putValueToWritableMiddleObject(pathStack.peek(), "strVar1"
			, SingleItemType.UB_PASCAL_STRING // itemType
			, allItemTypeReq.getStrVar1() // itemValue
			, -1 // itemSize
			, null // nativeItemCharset
			, middleWritableObject);

		singleItemEncoder.putValueToWritableMiddleObject(pathStack.peek(), "strVar2"
			, SingleItemType.US_PASCAL_STRING // itemType
			, allItemTypeReq.getStrVar2() // itemValue
			, -1 // itemSize
			, null // nativeItemCharset
			, middleWritableObject);

		singleItemEncoder.putValueToWritableMiddleObject(pathStack.peek(), "strVar3"
			, SingleItemType.SI_PASCAL_STRING // itemType
			, allItemTypeReq.getStrVar3() // itemValue
			, -1 // itemSize
			, null // nativeItemCharset
			, middleWritableObject);

		singleItemEncoder.putValueToWritableMiddleObject(pathStack.peek(), "bytesVar1"
			, SingleItemType.FIXED_LENGTH_BYTES // itemType
			, allItemTypeReq.getBytesVar1() // itemValue
			, 7 // itemSize
			, null // nativeItemCharset
			, middleWritableObject);

		singleItemEncoder.putValueToWritableMiddleObject(pathStack.peek(), "bytesVar2"
			, SingleItemType.SI_VARIABLE_LENGTH_BYTES // itemType
			, allItemTypeReq.getBytesVar2() // itemValue
			, -1 // itemSize
			, null // nativeItemCharset
			, middleWritableObject);

		singleItemEncoder.putValueToWritableMiddleObject(pathStack.peek(), "sqldate"
			, SingleItemType.JAVA_SQL_DATE // itemType
			, allItemTypeReq.getSqldate() // itemValue
			, -1 // itemSize
			, null // nativeItemCharset
			, middleWritableObject);

		singleItemEncoder.putValueToWritableMiddleObject(pathStack.peek(), "sqltimestamp"
			, SingleItemType.JAVA_SQL_TIMESTAMP // itemType
			, allItemTypeReq.getSqltimestamp() // itemValue
			, -1 // itemSize
			, null // nativeItemCharset
			, middleWritableObject);

		singleItemEncoder.putValueToWritableMiddleObject(pathStack.peek(), "cnt"
			, SingleItemType.INTEGER // itemType
			, allItemTypeReq.getCnt() // itemValue
			, -1 // itemSize
			, null // nativeItemCharset
			, middleWritableObject);

		java.util.List<AllItemTypeReq.Member> member$2List = allItemTypeReq.getMemberList();

		/** 배열 정보와 배열 크기 일치 검사 */
		if (null == member$2List) {
			/** 배열 크기 지정 방식이 간접일 경우 참조하는 변수값이 0 일 경우만 배열 값으로 null 을 허용한다. */
			if (0 != allItemTypeReq.getCnt()) {
				String errorMessage = new StringBuilder("the var member$2List is null but the value referenced by the array size[allItemTypeReq.getCnt()][").append(allItemTypeReq.getCnt()).append("] is not zero").toString();
				throw new kr.pe.sinnori.common.exception.BodyFormatException(errorMessage);
			}
		} else {
			int member$2ListSize = member$2List.size();
			/** 배열 값이 null 이 아닐때에는 배열 크기가 배열 정보에서 지정된 크기와 같은지 검사 */
			if (allItemTypeReq.getCnt() != member$2ListSize) {
				String errorMessage = new StringBuilder("the var member$2ListSize[").append(member$2ListSize).append("] is not same to the value referenced by the array size[allItemTypeReq.getCnt()][").append(allItemTypeReq.getCnt()).append("]").toString();
				throw new kr.pe.sinnori.common.exception.BodyFormatException(errorMessage);
			}

			Object member$2ArrayMiddleObject = singleItemEncoder.getArrayMiddleObjectFromWritableMiddleObject(pathStack.peek(), "member", member$2ListSize, middleWritableObject);
			for (int i2=0; i2 < member$2ListSize; i2++) {
				pathStack.push(new StringBuilder(pathStack.peek()).append(".").append("Member").append("[").append(i2).append("]").toString());
				Object member$2MiddleWritableObject = singleItemEncoder.getWritableMiddleObjectjFromArrayMiddleObject(pathStack.peek(), member$2ArrayMiddleObject, i2);
				AllItemTypeReq.Member member$2 = member$2List.get(i2);

				singleItemEncoder.putValueToWritableMiddleObject(pathStack.peek(), "memberID"
					, SingleItemType.FIXED_LENGTH_STRING // itemType
					, member$2.getMemberID() // itemValue
					, 30 // itemSize
					, null // nativeItemCharset
					, member$2MiddleWritableObject);

				singleItemEncoder.putValueToWritableMiddleObject(pathStack.peek(), "memberName"
					, SingleItemType.FIXED_LENGTH_STRING // itemType
					, member$2.getMemberName() // itemValue
					, 30 // itemSize
					, null // nativeItemCharset
					, member$2MiddleWritableObject);

				singleItemEncoder.putValueToWritableMiddleObject(pathStack.peek(), "cnt"
					, SingleItemType.INTEGER // itemType
					, member$2.getCnt() // itemValue
					, -1 // itemSize
					, null // nativeItemCharset
					, member$2MiddleWritableObject);

				java.util.List<AllItemTypeReq.Member.Item> item$4List = member$2.getItemList();

				/** 배열 정보와 배열 크기 일치 검사 */
				if (null == item$4List) {
					/** 배열 크기 지정 방식이 간접일 경우 참조하는 변수값이 0 일 경우만 배열 값으로 null 을 허용한다. */
					if (0 != 3) {
						String errorMessage = new StringBuilder("the var item$4List is null but the value defined by array size[3] is not zero").toString();
						throw new kr.pe.sinnori.common.exception.BodyFormatException(errorMessage);
					}
				} else {
					int item$4ListSize = item$4List.size();
					/** 배열 값이 null 이 아닐때에는 배열 크기가 배열 정보에서 지정된 크기와 같은지 검사 */
					if (3 != item$4ListSize) {
						String errorMessage = new StringBuilder("the var item$4ListSize[").append(item$4ListSize).append("] is not same to the value defined by array size[3]").toString();
						throw new kr.pe.sinnori.common.exception.BodyFormatException(errorMessage);
					}

					Object item$4ArrayMiddleObject = singleItemEncoder.getArrayMiddleObjectFromWritableMiddleObject(pathStack.peek(), "item", item$4ListSize, member$2MiddleWritableObject);
					for (int i4=0; i4 < item$4ListSize; i4++) {
						pathStack.push(new StringBuilder(pathStack.peek()).append(".").append("Item").append("[").append(i4).append("]").toString());
						Object item$4MiddleWritableObject = singleItemEncoder.getWritableMiddleObjectjFromArrayMiddleObject(pathStack.peek(), item$4ArrayMiddleObject, i4);
						AllItemTypeReq.Member.Item item$4 = item$4List.get(i4);

						singleItemEncoder.putValueToWritableMiddleObject(pathStack.peek(), "itemID"
							, SingleItemType.FIXED_LENGTH_STRING // itemType
							, item$4.getItemID() // itemValue
							, 30 // itemSize
							, null // nativeItemCharset
							, item$4MiddleWritableObject);

						singleItemEncoder.putValueToWritableMiddleObject(pathStack.peek(), "itemName"
							, SingleItemType.FIXED_LENGTH_STRING // itemType
							, item$4.getItemName() // itemValue
							, 30 // itemSize
							, null // nativeItemCharset
							, item$4MiddleWritableObject);

						singleItemEncoder.putValueToWritableMiddleObject(pathStack.peek(), "itemCnt"
							, SingleItemType.INTEGER // itemType
							, item$4.getItemCnt() // itemValue
							, -1 // itemSize
							, null // nativeItemCharset
							, item$4MiddleWritableObject);

						java.util.List<AllItemTypeReq.Member.Item.SubItem> subItem$6List = item$4.getSubItemList();

						/** 배열 정보와 배열 크기 일치 검사 */
						if (null == subItem$6List) {
							/** 배열 크기 지정 방식이 간접일 경우 참조하는 변수값이 0 일 경우만 배열 값으로 null 을 허용한다. */
							if (0 != item$4.getItemCnt()) {
								String errorMessage = new StringBuilder("the var subItem$6List is null but the value referenced by the array size[item$4.getItemCnt()][").append(item$4.getItemCnt()).append("] is not zero").toString();
								throw new kr.pe.sinnori.common.exception.BodyFormatException(errorMessage);
							}
						} else {
							int subItem$6ListSize = subItem$6List.size();
							/** 배열 값이 null 이 아닐때에는 배열 크기가 배열 정보에서 지정된 크기와 같은지 검사 */
							if (item$4.getItemCnt() != subItem$6ListSize) {
								String errorMessage = new StringBuilder("the var subItem$6ListSize[").append(subItem$6ListSize).append("] is not same to the value referenced by the array size[item$4.getItemCnt()][").append(item$4.getItemCnt()).append("]").toString();
								throw new kr.pe.sinnori.common.exception.BodyFormatException(errorMessage);
							}

							Object subItem$6ArrayMiddleObject = singleItemEncoder.getArrayMiddleObjectFromWritableMiddleObject(pathStack.peek(), "subItem", subItem$6ListSize, item$4MiddleWritableObject);
							for (int i6=0; i6 < subItem$6ListSize; i6++) {
								pathStack.push(new StringBuilder(pathStack.peek()).append(".").append("SubItem").append("[").append(i6).append("]").toString());
								Object subItem$6MiddleWritableObject = singleItemEncoder.getWritableMiddleObjectjFromArrayMiddleObject(pathStack.peek(), subItem$6ArrayMiddleObject, i6);
								AllItemTypeReq.Member.Item.SubItem subItem$6 = subItem$6List.get(i6);

								singleItemEncoder.putValueToWritableMiddleObject(pathStack.peek(), "subItemID"
									, SingleItemType.UB_PASCAL_STRING // itemType
									, subItem$6.getSubItemID() // itemValue
									, -1 // itemSize
									, null // nativeItemCharset
									, subItem$6MiddleWritableObject);

								singleItemEncoder.putValueToWritableMiddleObject(pathStack.peek(), "subItemName"
									, SingleItemType.US_PASCAL_STRING // itemType
									, subItem$6.getSubItemName() // itemValue
									, -1 // itemSize
									, null // nativeItemCharset
									, subItem$6MiddleWritableObject);

								singleItemEncoder.putValueToWritableMiddleObject(pathStack.peek(), "itemCnt"
									, SingleItemType.INTEGER // itemType
									, subItem$6.getItemCnt() // itemValue
									, -1 // itemSize
									, null // nativeItemCharset
									, subItem$6MiddleWritableObject);

								pathStack.pop();
							}
						}

						pathStack.pop();
					}
				}

				pathStack.pop();
			}
		}

		singleItemEncoder.putValueToWritableMiddleObject(pathStack.peek(), "longVar4"
			, SingleItemType.LONG // itemType
			, allItemTypeReq.getLongVar4() // itemValue
			, -1 // itemSize
			, null // nativeItemCharset
			, middleWritableObject);

		pathStack.pop();
	}
}