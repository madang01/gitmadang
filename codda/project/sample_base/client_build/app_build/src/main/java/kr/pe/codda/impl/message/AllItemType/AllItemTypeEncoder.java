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

package kr.pe.codda.impl.message.AllItemType;

import kr.pe.codda.common.message.AbstractMessage;
import kr.pe.codda.common.message.codec.AbstractMessageEncoder;
import kr.pe.codda.common.protocol.SingleItemEncoderIF;

/**
 * AllItemType message encoder
 * @author Won Jonghoon
 *
 */
public final class AllItemTypeEncoder extends AbstractMessageEncoder {
	@Override
	public void encode(AbstractMessage messageObj, SingleItemEncoderIF singleItemEncoder, Object writableMiddleObject) throws Exception {
		AllItemType allItemType = (AllItemType)messageObj;
		encodeBody(allItemType, singleItemEncoder, writableMiddleObject);
	}


	private void encodeBody(AllItemType allItemType, SingleItemEncoderIF singleItemEncoder, Object middleWritableObject) throws Exception {
		java.util.LinkedList<String> pathStack = new java.util.LinkedList<String>();
		pathStack.push("AllItemType");


		singleItemEncoder.putValueToWritableMiddleObject(pathStack.peek(), "byteVar1"
			, kr.pe.codda.common.type.SingleItemType.BYTE // itemType
			, allItemType.getByteVar1() // itemValue
			, -1 // itemSize
			, null // nativeItemCharset
			, middleWritableObject);

		singleItemEncoder.putValueToWritableMiddleObject(pathStack.peek(), "byteVar2"
			, kr.pe.codda.common.type.SingleItemType.BYTE // itemType
			, allItemType.getByteVar2() // itemValue
			, -1 // itemSize
			, null // nativeItemCharset
			, middleWritableObject);

		singleItemEncoder.putValueToWritableMiddleObject(pathStack.peek(), "byteVar3"
			, kr.pe.codda.common.type.SingleItemType.BYTE // itemType
			, allItemType.getByteVar3() // itemValue
			, -1 // itemSize
			, null // nativeItemCharset
			, middleWritableObject);

		singleItemEncoder.putValueToWritableMiddleObject(pathStack.peek(), "unsignedByteVar1"
			, kr.pe.codda.common.type.SingleItemType.UNSIGNED_BYTE // itemType
			, allItemType.getUnsignedByteVar1() // itemValue
			, -1 // itemSize
			, null // nativeItemCharset
			, middleWritableObject);

		singleItemEncoder.putValueToWritableMiddleObject(pathStack.peek(), "unsignedByteVar2"
			, kr.pe.codda.common.type.SingleItemType.UNSIGNED_BYTE // itemType
			, allItemType.getUnsignedByteVar2() // itemValue
			, -1 // itemSize
			, null // nativeItemCharset
			, middleWritableObject);

		singleItemEncoder.putValueToWritableMiddleObject(pathStack.peek(), "unsignedByteVar3"
			, kr.pe.codda.common.type.SingleItemType.UNSIGNED_BYTE // itemType
			, allItemType.getUnsignedByteVar3() // itemValue
			, -1 // itemSize
			, null // nativeItemCharset
			, middleWritableObject);

		singleItemEncoder.putValueToWritableMiddleObject(pathStack.peek(), "shortVar1"
			, kr.pe.codda.common.type.SingleItemType.SHORT // itemType
			, allItemType.getShortVar1() // itemValue
			, -1 // itemSize
			, null // nativeItemCharset
			, middleWritableObject);

		singleItemEncoder.putValueToWritableMiddleObject(pathStack.peek(), "shortVar2"
			, kr.pe.codda.common.type.SingleItemType.SHORT // itemType
			, allItemType.getShortVar2() // itemValue
			, -1 // itemSize
			, null // nativeItemCharset
			, middleWritableObject);

		singleItemEncoder.putValueToWritableMiddleObject(pathStack.peek(), "shortVar3"
			, kr.pe.codda.common.type.SingleItemType.SHORT // itemType
			, allItemType.getShortVar3() // itemValue
			, -1 // itemSize
			, null // nativeItemCharset
			, middleWritableObject);

		singleItemEncoder.putValueToWritableMiddleObject(pathStack.peek(), "unsignedShortVar1"
			, kr.pe.codda.common.type.SingleItemType.UNSIGNED_SHORT // itemType
			, allItemType.getUnsignedShortVar1() // itemValue
			, -1 // itemSize
			, null // nativeItemCharset
			, middleWritableObject);

		singleItemEncoder.putValueToWritableMiddleObject(pathStack.peek(), "unsignedShortVar2"
			, kr.pe.codda.common.type.SingleItemType.UNSIGNED_SHORT // itemType
			, allItemType.getUnsignedShortVar2() // itemValue
			, -1 // itemSize
			, null // nativeItemCharset
			, middleWritableObject);

		singleItemEncoder.putValueToWritableMiddleObject(pathStack.peek(), "unsignedShortVar3"
			, kr.pe.codda.common.type.SingleItemType.UNSIGNED_SHORT // itemType
			, allItemType.getUnsignedShortVar3() // itemValue
			, -1 // itemSize
			, null // nativeItemCharset
			, middleWritableObject);

		singleItemEncoder.putValueToWritableMiddleObject(pathStack.peek(), "intVar1"
			, kr.pe.codda.common.type.SingleItemType.INTEGER // itemType
			, allItemType.getIntVar1() // itemValue
			, -1 // itemSize
			, null // nativeItemCharset
			, middleWritableObject);

		singleItemEncoder.putValueToWritableMiddleObject(pathStack.peek(), "intVar2"
			, kr.pe.codda.common.type.SingleItemType.INTEGER // itemType
			, allItemType.getIntVar2() // itemValue
			, -1 // itemSize
			, null // nativeItemCharset
			, middleWritableObject);

		singleItemEncoder.putValueToWritableMiddleObject(pathStack.peek(), "intVar3"
			, kr.pe.codda.common.type.SingleItemType.INTEGER // itemType
			, allItemType.getIntVar3() // itemValue
			, -1 // itemSize
			, null // nativeItemCharset
			, middleWritableObject);

		singleItemEncoder.putValueToWritableMiddleObject(pathStack.peek(), "unsignedIntVar1"
			, kr.pe.codda.common.type.SingleItemType.UNSIGNED_INTEGER // itemType
			, allItemType.getUnsignedIntVar1() // itemValue
			, -1 // itemSize
			, null // nativeItemCharset
			, middleWritableObject);

		singleItemEncoder.putValueToWritableMiddleObject(pathStack.peek(), "unsignedIntVar2"
			, kr.pe.codda.common.type.SingleItemType.UNSIGNED_INTEGER // itemType
			, allItemType.getUnsignedIntVar2() // itemValue
			, -1 // itemSize
			, null // nativeItemCharset
			, middleWritableObject);

		singleItemEncoder.putValueToWritableMiddleObject(pathStack.peek(), "unsignedIntVar3"
			, kr.pe.codda.common.type.SingleItemType.UNSIGNED_INTEGER // itemType
			, allItemType.getUnsignedIntVar3() // itemValue
			, -1 // itemSize
			, null // nativeItemCharset
			, middleWritableObject);

		singleItemEncoder.putValueToWritableMiddleObject(pathStack.peek(), "longVar1"
			, kr.pe.codda.common.type.SingleItemType.LONG // itemType
			, allItemType.getLongVar1() // itemValue
			, -1 // itemSize
			, null // nativeItemCharset
			, middleWritableObject);

		singleItemEncoder.putValueToWritableMiddleObject(pathStack.peek(), "longVar2"
			, kr.pe.codda.common.type.SingleItemType.LONG // itemType
			, allItemType.getLongVar2() // itemValue
			, -1 // itemSize
			, null // nativeItemCharset
			, middleWritableObject);

		singleItemEncoder.putValueToWritableMiddleObject(pathStack.peek(), "longVar3"
			, kr.pe.codda.common.type.SingleItemType.LONG // itemType
			, allItemType.getLongVar3() // itemValue
			, -1 // itemSize
			, null // nativeItemCharset
			, middleWritableObject);

		singleItemEncoder.putValueToWritableMiddleObject(pathStack.peek(), "strVar1"
			, kr.pe.codda.common.type.SingleItemType.UB_PASCAL_STRING // itemType
			, allItemType.getStrVar1() // itemValue
			, -1 // itemSize
			, null // nativeItemCharset
			, middleWritableObject);

		singleItemEncoder.putValueToWritableMiddleObject(pathStack.peek(), "strVar2"
			, kr.pe.codda.common.type.SingleItemType.US_PASCAL_STRING // itemType
			, allItemType.getStrVar2() // itemValue
			, -1 // itemSize
			, null // nativeItemCharset
			, middleWritableObject);

		singleItemEncoder.putValueToWritableMiddleObject(pathStack.peek(), "strVar3"
			, kr.pe.codda.common.type.SingleItemType.SI_PASCAL_STRING // itemType
			, allItemType.getStrVar3() // itemValue
			, -1 // itemSize
			, null // nativeItemCharset
			, middleWritableObject);

		singleItemEncoder.putValueToWritableMiddleObject(pathStack.peek(), "bytesVar1"
			, kr.pe.codda.common.type.SingleItemType.FIXED_LENGTH_BYTES // itemType
			, allItemType.getBytesVar1() // itemValue
			, 7 // itemSize
			, null // nativeItemCharset
			, middleWritableObject);

		singleItemEncoder.putValueToWritableMiddleObject(pathStack.peek(), "bytesVar2"
			, kr.pe.codda.common.type.SingleItemType.SI_VARIABLE_LENGTH_BYTES // itemType
			, allItemType.getBytesVar2() // itemValue
			, -1 // itemSize
			, null // nativeItemCharset
			, middleWritableObject);

		singleItemEncoder.putValueToWritableMiddleObject(pathStack.peek(), "sqldate"
			, kr.pe.codda.common.type.SingleItemType.JAVA_SQL_DATE // itemType
			, allItemType.getSqldate() // itemValue
			, -1 // itemSize
			, null // nativeItemCharset
			, middleWritableObject);

		singleItemEncoder.putValueToWritableMiddleObject(pathStack.peek(), "sqltimestamp"
			, kr.pe.codda.common.type.SingleItemType.JAVA_SQL_TIMESTAMP // itemType
			, allItemType.getSqltimestamp() // itemValue
			, -1 // itemSize
			, null // nativeItemCharset
			, middleWritableObject);

		singleItemEncoder.putValueToWritableMiddleObject(pathStack.peek(), "cnt"
			, kr.pe.codda.common.type.SingleItemType.INTEGER // itemType
			, allItemType.getCnt() // itemValue
			, -1 // itemSize
			, null // nativeItemCharset
			, middleWritableObject);

		java.util.List<AllItemType.Member> member$2List = allItemType.getMemberList();

		/** 배열 정보와 배열 크기 일치 검사 */
		if (null == member$2List) {
			/** 배열 크기 지정 방식이 간접일 경우 참조하는 변수값이 0 일 경우만 배열 값으로 null 을 허용한다. */
			if (0 != allItemType.getCnt()) {
				String errorMessage = new StringBuilder("the var member$2List is null but the value referenced by the array size[allItemType.getCnt()][").append(allItemType.getCnt()).append("] is not zero").toString();
				throw new kr.pe.codda.common.exception.BodyFormatException(errorMessage);
			}
		} else {
			int member$2ListSize = member$2List.size();
			/** 배열 값이 null 이 아닐때에는 배열 크기가 배열 정보에서 지정된 크기와 같은지 검사 */
			if (allItemType.getCnt() != member$2ListSize) {
				String errorMessage = new StringBuilder("the var member$2ListSize[").append(member$2ListSize).append("] is not same to the value referenced by the array size[allItemType.getCnt()][").append(allItemType.getCnt()).append("]").toString();
				throw new kr.pe.codda.common.exception.BodyFormatException(errorMessage);
			}

			Object member$2ArrayMiddleObject = singleItemEncoder.getArrayMiddleObjectFromWritableMiddleObject(pathStack.peek(), "member", member$2ListSize, middleWritableObject);
			for (int i2=0; i2 < member$2ListSize; i2++) {
				pathStack.push(new StringBuilder(pathStack.peek()).append(".").append("Member").append("[").append(i2).append("]").toString());
				Object member$2MiddleWritableObject = singleItemEncoder.getWritableMiddleObjectjFromArrayMiddleObject(pathStack.peek(), member$2ArrayMiddleObject, i2);
				AllItemType.Member member$2 = member$2List.get(i2);

				singleItemEncoder.putValueToWritableMiddleObject(pathStack.peek(), "memberID"
					, kr.pe.codda.common.type.SingleItemType.FIXED_LENGTH_STRING // itemType
					, member$2.getMemberID() // itemValue
					, 30 // itemSize
					, null // nativeItemCharset
					, member$2MiddleWritableObject);

				singleItemEncoder.putValueToWritableMiddleObject(pathStack.peek(), "memberName"
					, kr.pe.codda.common.type.SingleItemType.FIXED_LENGTH_STRING // itemType
					, member$2.getMemberName() // itemValue
					, 30 // itemSize
					, null // nativeItemCharset
					, member$2MiddleWritableObject);

				singleItemEncoder.putValueToWritableMiddleObject(pathStack.peek(), "cnt"
					, kr.pe.codda.common.type.SingleItemType.INTEGER // itemType
					, member$2.getCnt() // itemValue
					, -1 // itemSize
					, null // nativeItemCharset
					, member$2MiddleWritableObject);

				java.util.List<AllItemType.Member.Item> item$4List = member$2.getItemList();

				/** 배열 정보와 배열 크기 일치 검사 */
				if (null == item$4List) {
					/** 배열 크기 지정 방식이 간접일 경우 참조하는 변수값이 0 일 경우만 배열 값으로 null 을 허용한다. */
					if (0 != member$2.getCnt()) {
						String errorMessage = new StringBuilder("the var item$4List is null but the value referenced by the array size[member$2.getCnt()][").append(member$2.getCnt()).append("] is not zero").toString();
						throw new kr.pe.codda.common.exception.BodyFormatException(errorMessage);
					}
				} else {
					int item$4ListSize = item$4List.size();
					/** 배열 값이 null 이 아닐때에는 배열 크기가 배열 정보에서 지정된 크기와 같은지 검사 */
					if (member$2.getCnt() != item$4ListSize) {
						String errorMessage = new StringBuilder("the var item$4ListSize[").append(item$4ListSize).append("] is not same to the value referenced by the array size[member$2.getCnt()][").append(member$2.getCnt()).append("]").toString();
						throw new kr.pe.codda.common.exception.BodyFormatException(errorMessage);
					}

					Object item$4ArrayMiddleObject = singleItemEncoder.getArrayMiddleObjectFromWritableMiddleObject(pathStack.peek(), "item", item$4ListSize, member$2MiddleWritableObject);
					for (int i4=0; i4 < item$4ListSize; i4++) {
						pathStack.push(new StringBuilder(pathStack.peek()).append(".").append("Item").append("[").append(i4).append("]").toString());
						Object item$4MiddleWritableObject = singleItemEncoder.getWritableMiddleObjectjFromArrayMiddleObject(pathStack.peek(), item$4ArrayMiddleObject, i4);
						AllItemType.Member.Item item$4 = item$4List.get(i4);

						singleItemEncoder.putValueToWritableMiddleObject(pathStack.peek(), "itemID"
							, kr.pe.codda.common.type.SingleItemType.FIXED_LENGTH_STRING // itemType
							, item$4.getItemID() // itemValue
							, 30 // itemSize
							, null // nativeItemCharset
							, item$4MiddleWritableObject);

						singleItemEncoder.putValueToWritableMiddleObject(pathStack.peek(), "itemName"
							, kr.pe.codda.common.type.SingleItemType.FIXED_LENGTH_STRING // itemType
							, item$4.getItemName() // itemValue
							, 30 // itemSize
							, null // nativeItemCharset
							, item$4MiddleWritableObject);

						singleItemEncoder.putValueToWritableMiddleObject(pathStack.peek(), "itemCnt"
							, kr.pe.codda.common.type.SingleItemType.INTEGER // itemType
							, item$4.getItemCnt() // itemValue
							, -1 // itemSize
							, null // nativeItemCharset
							, item$4MiddleWritableObject);

						java.util.List<AllItemType.Member.Item.SubItem> subItem$6List = item$4.getSubItemList();

						/** 배열 정보와 배열 크기 일치 검사 */
						if (null == subItem$6List) {
							/** 배열 크기 지정 방식이 간접일 경우 참조하는 변수값이 0 일 경우만 배열 값으로 null 을 허용한다. */
							if (0 != item$4.getItemCnt()) {
								String errorMessage = new StringBuilder("the var subItem$6List is null but the value referenced by the array size[item$4.getItemCnt()][").append(item$4.getItemCnt()).append("] is not zero").toString();
								throw new kr.pe.codda.common.exception.BodyFormatException(errorMessage);
							}
						} else {
							int subItem$6ListSize = subItem$6List.size();
							/** 배열 값이 null 이 아닐때에는 배열 크기가 배열 정보에서 지정된 크기와 같은지 검사 */
							if (item$4.getItemCnt() != subItem$6ListSize) {
								String errorMessage = new StringBuilder("the var subItem$6ListSize[").append(subItem$6ListSize).append("] is not same to the value referenced by the array size[item$4.getItemCnt()][").append(item$4.getItemCnt()).append("]").toString();
								throw new kr.pe.codda.common.exception.BodyFormatException(errorMessage);
							}

							Object subItem$6ArrayMiddleObject = singleItemEncoder.getArrayMiddleObjectFromWritableMiddleObject(pathStack.peek(), "subItem", subItem$6ListSize, item$4MiddleWritableObject);
							for (int i6=0; i6 < subItem$6ListSize; i6++) {
								pathStack.push(new StringBuilder(pathStack.peek()).append(".").append("SubItem").append("[").append(i6).append("]").toString());
								Object subItem$6MiddleWritableObject = singleItemEncoder.getWritableMiddleObjectjFromArrayMiddleObject(pathStack.peek(), subItem$6ArrayMiddleObject, i6);
								AllItemType.Member.Item.SubItem subItem$6 = subItem$6List.get(i6);

								singleItemEncoder.putValueToWritableMiddleObject(pathStack.peek(), "subItemID"
									, kr.pe.codda.common.type.SingleItemType.UB_PASCAL_STRING // itemType
									, subItem$6.getSubItemID() // itemValue
									, -1 // itemSize
									, null // nativeItemCharset
									, subItem$6MiddleWritableObject);

								singleItemEncoder.putValueToWritableMiddleObject(pathStack.peek(), "subItemName"
									, kr.pe.codda.common.type.SingleItemType.US_PASCAL_STRING // itemType
									, subItem$6.getSubItemName() // itemValue
									, -1 // itemSize
									, null // nativeItemCharset
									, subItem$6MiddleWritableObject);

								singleItemEncoder.putValueToWritableMiddleObject(pathStack.peek(), "itemCnt"
									, kr.pe.codda.common.type.SingleItemType.INTEGER // itemType
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
			, kr.pe.codda.common.type.SingleItemType.LONG // itemType
			, allItemType.getLongVar4() // itemValue
			, -1 // itemSize
			, null // nativeItemCharset
			, middleWritableObject);

		pathStack.pop();
	}
}