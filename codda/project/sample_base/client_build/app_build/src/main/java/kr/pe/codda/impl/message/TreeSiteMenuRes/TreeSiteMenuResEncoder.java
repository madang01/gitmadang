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

package kr.pe.codda.impl.message.TreeSiteMenuRes;

import kr.pe.codda.common.message.AbstractMessage;
import kr.pe.codda.common.message.codec.AbstractMessageEncoder;
import kr.pe.codda.common.protocol.SingleItemEncoderIF;

/**
 * TreeSiteMenuRes message encoder
 * @author Won Jonghoon
 *
 */
public final class TreeSiteMenuResEncoder extends AbstractMessageEncoder {
	@Override
	public void encode(AbstractMessage messageObj, SingleItemEncoderIF singleItemEncoder, Object writableMiddleObject) throws Exception {
		TreeSiteMenuRes treeSiteMenuRes = (TreeSiteMenuRes)messageObj;
		encodeBody(treeSiteMenuRes, singleItemEncoder, writableMiddleObject);
	}


	private void encodeBody(TreeSiteMenuRes treeSiteMenuRes, SingleItemEncoderIF singleItemEncoder, Object middleWritableObject) throws Exception {
		java.util.LinkedList<String> pathStack = new java.util.LinkedList<String>();
		pathStack.push("TreeSiteMenuRes");


		singleItemEncoder.putValueToWritableMiddleObject(pathStack.peek(), "cnt"
			, kr.pe.codda.common.type.SingleItemType.INTEGER // itemType
			, treeSiteMenuRes.getCnt() // itemValue
			, -1 // itemSize
			, null // nativeItemCharset
			, middleWritableObject);

		java.util.List<TreeSiteMenuRes.Menu> menu$2List = treeSiteMenuRes.getMenuList();

		/** 배열 정보와 배열 크기 일치 검사 */
		if (null == menu$2List) {
			/** 배열 크기 지정 방식이 간접일 경우 참조하는 변수값이 0 일 경우만 배열 값으로 null 을 허용한다. */
			if (0 != treeSiteMenuRes.getCnt()) {
				String errorMessage = new StringBuilder("the var menu$2List is null but the value referenced by the array size[treeSiteMenuRes.getCnt()][").append(treeSiteMenuRes.getCnt()).append("] is not zero").toString();
				throw new kr.pe.codda.common.exception.BodyFormatException(errorMessage);
			}
		} else {
			int menu$2ListSize = menu$2List.size();
			/** 배열 값이 null 이 아닐때에는 배열 크기가 배열 정보에서 지정된 크기와 같은지 검사 */
			if (treeSiteMenuRes.getCnt() != menu$2ListSize) {
				String errorMessage = new StringBuilder("the var menu$2ListSize[").append(menu$2ListSize).append("] is not same to the value referenced by the array size[treeSiteMenuRes.getCnt()][").append(treeSiteMenuRes.getCnt()).append("]").toString();
				throw new kr.pe.codda.common.exception.BodyFormatException(errorMessage);
			}

			Object menu$2ArrayMiddleObject = singleItemEncoder.getArrayMiddleObjectFromWritableMiddleObject(pathStack.peek(), "menu", menu$2ListSize, middleWritableObject);
			for (int i2=0; i2 < menu$2ListSize; i2++) {
				pathStack.push(new StringBuilder(pathStack.peek()).append(".").append("Menu").append("[").append(i2).append("]").toString());
				Object menu$2MiddleWritableObject = singleItemEncoder.getWritableMiddleObjectjFromArrayMiddleObject(pathStack.peek(), menu$2ArrayMiddleObject, i2);
				TreeSiteMenuRes.Menu menu$2 = menu$2List.get(i2);

				singleItemEncoder.putValueToWritableMiddleObject(pathStack.peek(), "menuNo"
					, kr.pe.codda.common.type.SingleItemType.UNSIGNED_INTEGER // itemType
					, menu$2.getMenuNo() // itemValue
					, -1 // itemSize
					, null // nativeItemCharset
					, menu$2MiddleWritableObject);

				singleItemEncoder.putValueToWritableMiddleObject(pathStack.peek(), "parentNo"
					, kr.pe.codda.common.type.SingleItemType.UNSIGNED_INTEGER // itemType
					, menu$2.getParentNo() // itemValue
					, -1 // itemSize
					, null // nativeItemCharset
					, menu$2MiddleWritableObject);

				singleItemEncoder.putValueToWritableMiddleObject(pathStack.peek(), "depth"
					, kr.pe.codda.common.type.SingleItemType.UNSIGNED_BYTE // itemType
					, menu$2.getDepth() // itemValue
					, -1 // itemSize
					, null // nativeItemCharset
					, menu$2MiddleWritableObject);

				singleItemEncoder.putValueToWritableMiddleObject(pathStack.peek(), "orderSeq"
					, kr.pe.codda.common.type.SingleItemType.UNSIGNED_BYTE // itemType
					, menu$2.getOrderSeq() // itemValue
					, -1 // itemSize
					, null // nativeItemCharset
					, menu$2MiddleWritableObject);

				singleItemEncoder.putValueToWritableMiddleObject(pathStack.peek(), "menuName"
					, kr.pe.codda.common.type.SingleItemType.UB_PASCAL_STRING // itemType
					, menu$2.getMenuName() // itemValue
					, -1 // itemSize
					, null // nativeItemCharset
					, menu$2MiddleWritableObject);

				singleItemEncoder.putValueToWritableMiddleObject(pathStack.peek(), "linkURL"
					, kr.pe.codda.common.type.SingleItemType.UB_PASCAL_STRING // itemType
					, menu$2.getLinkURL() // itemValue
					, -1 // itemSize
					, null // nativeItemCharset
					, menu$2MiddleWritableObject);

				singleItemEncoder.putValueToWritableMiddleObject(pathStack.peek(), "cnt"
					, kr.pe.codda.common.type.SingleItemType.INTEGER // itemType
					, menu$2.getCnt() // itemValue
					, -1 // itemSize
					, null // nativeItemCharset
					, menu$2MiddleWritableObject);

				pathStack.pop();
			}
		}

		pathStack.pop();
	}
}