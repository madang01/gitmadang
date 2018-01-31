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
import kr.pe.sinnori.common.exception.BodyFormatException;
import kr.pe.sinnori.common.message.AbstractMessage;
import kr.pe.sinnori.common.message.builder.info.SingleItemType;
import kr.pe.sinnori.common.message.codec.AbstractMessageDecoder;
import kr.pe.sinnori.common.protocol.SingleItemDecoderIF;
/**
 * AllItemTypeReq 메시지 디코더
 * @author Won Jonghoon
 *
 */
public final class AllItemTypeReqDecoder extends AbstractMessageDecoder {

	@Override
	protected AbstractMessage decodeBody(SingleItemDecoderIF singleItemDecoder, Object  middleReadableObject) throws OutOfMemoryError, BodyFormatException {
		AllItemTypeReq allItemTypeReq = new AllItemTypeReq();
		LinkedList<String> pathStack = new LinkedList<String>();
		pathStack.push("AllItemTypeReq");

		allItemTypeReq.setByteVar1((Byte)
		singleItemDecoder.getValueFromReadableMiddleObject(pathStack.peek()
			, "byteVar1" // itemName
			, SingleItemType.BYTE // itemType
			, -1 // itemSize
			, null // nativeItemCharset
			, middleReadableObject));

		allItemTypeReq.setByteVar2((Byte)
		singleItemDecoder.getValueFromReadableMiddleObject(pathStack.peek()
			, "byteVar2" // itemName
			, SingleItemType.BYTE // itemType
			, -1 // itemSize
			, null // nativeItemCharset
			, middleReadableObject));

		allItemTypeReq.setByteVar3((Byte)
		singleItemDecoder.getValueFromReadableMiddleObject(pathStack.peek()
			, "byteVar3" // itemName
			, SingleItemType.BYTE // itemType
			, -1 // itemSize
			, null // nativeItemCharset
			, middleReadableObject));

		allItemTypeReq.setUnsignedByteVar1((Short)
		singleItemDecoder.getValueFromReadableMiddleObject(pathStack.peek()
			, "unsignedByteVar1" // itemName
			, SingleItemType.UNSIGNED_BYTE // itemType
			, -1 // itemSize
			, null // nativeItemCharset
			, middleReadableObject));

		allItemTypeReq.setUnsignedByteVar2((Short)
		singleItemDecoder.getValueFromReadableMiddleObject(pathStack.peek()
			, "unsignedByteVar2" // itemName
			, SingleItemType.UNSIGNED_BYTE // itemType
			, -1 // itemSize
			, null // nativeItemCharset
			, middleReadableObject));

		allItemTypeReq.setUnsignedByteVar3((Short)
		singleItemDecoder.getValueFromReadableMiddleObject(pathStack.peek()
			, "unsignedByteVar3" // itemName
			, SingleItemType.UNSIGNED_BYTE // itemType
			, -1 // itemSize
			, null // nativeItemCharset
			, middleReadableObject));

		allItemTypeReq.setShortVar1((Short)
		singleItemDecoder.getValueFromReadableMiddleObject(pathStack.peek()
			, "shortVar1" // itemName
			, SingleItemType.SHORT // itemType
			, -1 // itemSize
			, null // nativeItemCharset
			, middleReadableObject));

		allItemTypeReq.setShortVar2((Short)
		singleItemDecoder.getValueFromReadableMiddleObject(pathStack.peek()
			, "shortVar2" // itemName
			, SingleItemType.SHORT // itemType
			, -1 // itemSize
			, null // nativeItemCharset
			, middleReadableObject));

		allItemTypeReq.setShortVar3((Short)
		singleItemDecoder.getValueFromReadableMiddleObject(pathStack.peek()
			, "shortVar3" // itemName
			, SingleItemType.SHORT // itemType
			, -1 // itemSize
			, null // nativeItemCharset
			, middleReadableObject));

		allItemTypeReq.setUnsignedShortVar1((Integer)
		singleItemDecoder.getValueFromReadableMiddleObject(pathStack.peek()
			, "unsignedShortVar1" // itemName
			, SingleItemType.UNSIGNED_SHORT // itemType
			, -1 // itemSize
			, null // nativeItemCharset
			, middleReadableObject));

		allItemTypeReq.setUnsignedShortVar2((Integer)
		singleItemDecoder.getValueFromReadableMiddleObject(pathStack.peek()
			, "unsignedShortVar2" // itemName
			, SingleItemType.UNSIGNED_SHORT // itemType
			, -1 // itemSize
			, null // nativeItemCharset
			, middleReadableObject));

		allItemTypeReq.setUnsignedShortVar3((Integer)
		singleItemDecoder.getValueFromReadableMiddleObject(pathStack.peek()
			, "unsignedShortVar3" // itemName
			, SingleItemType.UNSIGNED_SHORT // itemType
			, -1 // itemSize
			, null // nativeItemCharset
			, middleReadableObject));

		allItemTypeReq.setIntVar1((Integer)
		singleItemDecoder.getValueFromReadableMiddleObject(pathStack.peek()
			, "intVar1" // itemName
			, SingleItemType.INTEGER // itemType
			, -1 // itemSize
			, null // nativeItemCharset
			, middleReadableObject));

		allItemTypeReq.setIntVar2((Integer)
		singleItemDecoder.getValueFromReadableMiddleObject(pathStack.peek()
			, "intVar2" // itemName
			, SingleItemType.INTEGER // itemType
			, -1 // itemSize
			, null // nativeItemCharset
			, middleReadableObject));

		allItemTypeReq.setIntVar3((Integer)
		singleItemDecoder.getValueFromReadableMiddleObject(pathStack.peek()
			, "intVar3" // itemName
			, SingleItemType.INTEGER // itemType
			, -1 // itemSize
			, null // nativeItemCharset
			, middleReadableObject));

		allItemTypeReq.setUnsignedIntVar1((Long)
		singleItemDecoder.getValueFromReadableMiddleObject(pathStack.peek()
			, "unsignedIntVar1" // itemName
			, SingleItemType.UNSIGNED_INTEGER // itemType
			, -1 // itemSize
			, null // nativeItemCharset
			, middleReadableObject));

		allItemTypeReq.setUnsignedIntVar2((Long)
		singleItemDecoder.getValueFromReadableMiddleObject(pathStack.peek()
			, "unsignedIntVar2" // itemName
			, SingleItemType.UNSIGNED_INTEGER // itemType
			, -1 // itemSize
			, null // nativeItemCharset
			, middleReadableObject));

		allItemTypeReq.setUnsignedIntVar3((Long)
		singleItemDecoder.getValueFromReadableMiddleObject(pathStack.peek()
			, "unsignedIntVar3" // itemName
			, SingleItemType.UNSIGNED_INTEGER // itemType
			, -1 // itemSize
			, null // nativeItemCharset
			, middleReadableObject));

		allItemTypeReq.setLongVar1((Long)
		singleItemDecoder.getValueFromReadableMiddleObject(pathStack.peek()
			, "longVar1" // itemName
			, SingleItemType.LONG // itemType
			, -1 // itemSize
			, null // nativeItemCharset
			, middleReadableObject));

		allItemTypeReq.setLongVar2((Long)
		singleItemDecoder.getValueFromReadableMiddleObject(pathStack.peek()
			, "longVar2" // itemName
			, SingleItemType.LONG // itemType
			, -1 // itemSize
			, null // nativeItemCharset
			, middleReadableObject));

		allItemTypeReq.setLongVar3((Long)
		singleItemDecoder.getValueFromReadableMiddleObject(pathStack.peek()
			, "longVar3" // itemName
			, SingleItemType.LONG // itemType
			, -1 // itemSize
			, null // nativeItemCharset
			, middleReadableObject));

		allItemTypeReq.setStrVar1((String)
		singleItemDecoder.getValueFromReadableMiddleObject(pathStack.peek()
			, "strVar1" // itemName
			, SingleItemType.UB_PASCAL_STRING // itemType
			, -1 // itemSize
			, null // nativeItemCharset
			, middleReadableObject));

		allItemTypeReq.setStrVar2((String)
		singleItemDecoder.getValueFromReadableMiddleObject(pathStack.peek()
			, "strVar2" // itemName
			, SingleItemType.US_PASCAL_STRING // itemType
			, -1 // itemSize
			, null // nativeItemCharset
			, middleReadableObject));

		allItemTypeReq.setStrVar3((String)
		singleItemDecoder.getValueFromReadableMiddleObject(pathStack.peek()
			, "strVar3" // itemName
			, SingleItemType.SI_PASCAL_STRING // itemType
			, -1 // itemSize
			, null // nativeItemCharset
			, middleReadableObject));

		allItemTypeReq.setBytesVar1((byte[])
		singleItemDecoder.getValueFromReadableMiddleObject(pathStack.peek()
			, "bytesVar1" // itemName
			, SingleItemType.FIXED_LENGTH_BYTES // itemType
			, 7 // itemSize
			, null // nativeItemCharset
			, middleReadableObject));

		allItemTypeReq.setBytesVar2((byte[])
		singleItemDecoder.getValueFromReadableMiddleObject(pathStack.peek()
			, "bytesVar2" // itemName
			, SingleItemType.SI_VARIABLE_LENGTH_BYTES // itemType
			, -1 // itemSize
			, null // nativeItemCharset
			, middleReadableObject));

		allItemTypeReq.setSqldate((java.sql.Date)
		singleItemDecoder.getValueFromReadableMiddleObject(pathStack.peek()
			, "sqldate" // itemName
			, SingleItemType.JAVA_SQL_DATE // itemType
			, -1 // itemSize
			, null // nativeItemCharset
			, middleReadableObject));

		allItemTypeReq.setSqltimestamp((java.lang.Boolean)
		singleItemDecoder.getValueFromReadableMiddleObject(pathStack.peek()
			, "sqltimestamp" // itemName
			, SingleItemType.JAVA_SQL_TIMESTAMP // itemType
			, -1 // itemSize
			, null // nativeItemCharset
			, middleReadableObject));

		allItemTypeReq.setCnt((Integer)
		singleItemDecoder.getValueFromReadableMiddleObject(pathStack.peek()
			, "cnt" // itemName
			, SingleItemType.INTEGER // itemType
			, -1 // itemSize
			, null // nativeItemCharset
			, middleReadableObject));

		int member$2ListSize = allItemTypeReq.getCnt();
		Object member$2ArrayMiddleObject = singleItemDecoder.getArrayMiddleObjectFromReadableMiddleObject(pathStack.peek(), "member", member$2ListSize, middleReadableObject);
		java.util.List<AllItemTypeReq.Member> member$2List = new java.util.ArrayList<AllItemTypeReq.Member>();
		for (int i2=0; i2 < member$2ListSize; i2++) {
			pathStack.push(new StringBuilder(pathStack.peek()).append(".").append("Member").append("[").append(i2).append("]").toString());
			Object member$2MiddleWritableObject= singleItemDecoder.getReadableMiddleObjFromArrayMiddleObject(pathStack.peek(), member$2ArrayMiddleObject, i2);
			AllItemTypeReq.Member member$2 = new AllItemTypeReq.Member();
			member$2List.add(member$2);

			member$2.setMemberID((String)
			singleItemDecoder.getValueFromReadableMiddleObject(pathStack.peek()
				, "memberID" // itemName
				, SingleItemType.FIXED_LENGTH_STRING // itemType
				, 30 // itemSize
				, null // nativeItemCharset
				, member$2MiddleWritableObject));

			member$2.setMemberName((String)
			singleItemDecoder.getValueFromReadableMiddleObject(pathStack.peek()
				, "memberName" // itemName
				, SingleItemType.FIXED_LENGTH_STRING // itemType
				, 30 // itemSize
				, null // nativeItemCharset
				, member$2MiddleWritableObject));

			member$2.setCnt((Integer)
			singleItemDecoder.getValueFromReadableMiddleObject(pathStack.peek()
				, "cnt" // itemName
				, SingleItemType.INTEGER // itemType
				, -1 // itemSize
				, null // nativeItemCharset
				, member$2MiddleWritableObject));

			int item$3ListSize = 3;
			Object item$3ArrayMiddleObject = singleItemDecoder.getArrayMiddleObjectFromReadableMiddleObject(pathStack.peek(), "item", item$3ListSize, member$2MiddleWritableObject);
			java.util.List<AllItemTypeReq.Member.Item> item$3List = new java.util.ArrayList<AllItemTypeReq.Member.Item>();
			for (int i3=0; i3 < item$3ListSize; i3++) {
				pathStack.push(new StringBuilder(pathStack.peek()).append(".").append("Item").append("[").append(i3).append("]").toString());
				Object item$3MiddleWritableObject= singleItemDecoder.getReadableMiddleObjFromArrayMiddleObject(pathStack.peek(), item$3ArrayMiddleObject, i3);
				AllItemTypeReq.Member.Item item$3 = new AllItemTypeReq.Member.Item();
				item$3List.add(item$3);

				item$3.setItemID((String)
				singleItemDecoder.getValueFromReadableMiddleObject(pathStack.peek()
					, "itemID" // itemName
					, SingleItemType.FIXED_LENGTH_STRING // itemType
					, 30 // itemSize
					, null // nativeItemCharset
					, item$3MiddleWritableObject));

				item$3.setItemName((String)
				singleItemDecoder.getValueFromReadableMiddleObject(pathStack.peek()
					, "itemName" // itemName
					, SingleItemType.FIXED_LENGTH_STRING // itemType
					, 30 // itemSize
					, null // nativeItemCharset
					, item$3MiddleWritableObject));

				item$3.setItemCnt((Integer)
				singleItemDecoder.getValueFromReadableMiddleObject(pathStack.peek()
					, "itemCnt" // itemName
					, SingleItemType.INTEGER // itemType
					, -1 // itemSize
					, null // nativeItemCharset
					, item$3MiddleWritableObject));

				int subItem$4ListSize = item$3.getItemCnt();
				Object subItem$4ArrayMiddleObject = singleItemDecoder.getArrayMiddleObjectFromReadableMiddleObject(pathStack.peek(), "subItem", subItem$4ListSize, item$3MiddleWritableObject);
				java.util.List<AllItemTypeReq.Member.Item.SubItem> subItem$4List = new java.util.ArrayList<AllItemTypeReq.Member.Item.SubItem>();
				for (int i4=0; i4 < subItem$4ListSize; i4++) {
					pathStack.push(new StringBuilder(pathStack.peek()).append(".").append("SubItem").append("[").append(i4).append("]").toString());
					Object subItem$4MiddleWritableObject= singleItemDecoder.getReadableMiddleObjFromArrayMiddleObject(pathStack.peek(), subItem$4ArrayMiddleObject, i4);
					AllItemTypeReq.Member.Item.SubItem subItem$4 = new AllItemTypeReq.Member.Item.SubItem();
					subItem$4List.add(subItem$4);

					subItem$4.setSubItemID((String)
					singleItemDecoder.getValueFromReadableMiddleObject(pathStack.peek()
						, "subItemID" // itemName
						, SingleItemType.UB_PASCAL_STRING // itemType
						, -1 // itemSize
						, null // nativeItemCharset
						, subItem$4MiddleWritableObject));

					subItem$4.setSubItemName((String)
					singleItemDecoder.getValueFromReadableMiddleObject(pathStack.peek()
						, "subItemName" // itemName
						, SingleItemType.US_PASCAL_STRING // itemType
						, -1 // itemSize
						, null // nativeItemCharset
						, subItem$4MiddleWritableObject));

					subItem$4.setItemCnt((Integer)
					singleItemDecoder.getValueFromReadableMiddleObject(pathStack.peek()
						, "itemCnt" // itemName
						, SingleItemType.INTEGER // itemType
						, -1 // itemSize
						, null // nativeItemCharset
						, subItem$4MiddleWritableObject));

					pathStack.pop();
				}

				item$3.setSubItemList(subItem$4List);

				pathStack.pop();
			}

			member$2.setItemList(item$3List);

			pathStack.pop();
		}

		allItemTypeReq.setMemberList(member$2List);

		allItemTypeReq.setLongVar4((Long)
		singleItemDecoder.getValueFromReadableMiddleObject(pathStack.peek()
			, "longVar4" // itemName
			, SingleItemType.LONG // itemType
			, -1 // itemSize
			, null // nativeItemCharset
			, middleReadableObject));

		pathStack.pop();

		return allItemTypeReq;
	}
}