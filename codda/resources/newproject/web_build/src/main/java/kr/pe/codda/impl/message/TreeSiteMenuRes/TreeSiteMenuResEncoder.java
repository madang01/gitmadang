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
	
	private void encodeBody(TreeSiteMenuRes.Menu menu, java.util.LinkedList<String> pathStack, SingleItemEncoderIF singleItemEncoder, Object menuMiddleWritableObject) throws Exception {
		singleItemEncoder.putValueToWritableMiddleObject(pathStack.peek(), "menuNo"
				, kr.pe.codda.common.type.SingleItemType.UNSIGNED_INTEGER // itemType
				, menu.getMenuNo() // itemValue
				, -1 // itemSize
				, null // nativeItemCharset
				, menuMiddleWritableObject);

			singleItemEncoder.putValueToWritableMiddleObject(pathStack.peek(), "parentNo"
				, kr.pe.codda.common.type.SingleItemType.UNSIGNED_INTEGER // itemType
				, menu.getParentNo() // itemValue
				, -1 // itemSize
				, null // nativeItemCharset
				, menuMiddleWritableObject);

			singleItemEncoder.putValueToWritableMiddleObject(pathStack.peek(), "depth"
				, kr.pe.codda.common.type.SingleItemType.UNSIGNED_BYTE // itemType
				, menu.getDepth() // itemValue
				, -1 // itemSize
				, null // nativeItemCharset
				, menuMiddleWritableObject);

			singleItemEncoder.putValueToWritableMiddleObject(pathStack.peek(), "orderSeq"
				, kr.pe.codda.common.type.SingleItemType.UNSIGNED_BYTE // itemType
				, menu.getOrderSeq() // itemValue
				, -1 // itemSize
				, null // nativeItemCharset
				, menuMiddleWritableObject);

			singleItemEncoder.putValueToWritableMiddleObject(pathStack.peek(), "menuName"
				, kr.pe.codda.common.type.SingleItemType.UB_PASCAL_STRING // itemType
				, menu.getMenuName() // itemValue
				, -1 // itemSize
				, null // nativeItemCharset
				, menuMiddleWritableObject);

			singleItemEncoder.putValueToWritableMiddleObject(pathStack.peek(), "linkURL"
				, kr.pe.codda.common.type.SingleItemType.UB_PASCAL_STRING // itemType
				, menu.getLinkURL() // itemValue
				, -1 // itemSize
				, null // nativeItemCharset
				, menuMiddleWritableObject);

			singleItemEncoder.putValueToWritableMiddleObject(pathStack.peek(), "childMenuListSize"
				, kr.pe.codda.common.type.SingleItemType.INTEGER // itemType
				, menu.getChildMenuListSize() // itemValue
				, -1 // itemSize
				, null // nativeItemCharset
				, menuMiddleWritableObject);
			
			java.util.List<TreeSiteMenuRes.Menu> childMenuList = menu.getChildMenuList();

			/** 배열 정보와 배열 크기 일치 검사 */
			if (null == childMenuList) {
				/** 배열 크기 지정 방식이 간접일 경우 참조하는 변수값이 0 일 경우만 배열 값으로 null 을 허용한다. */
				if (0 != menu.getChildMenuListSize()) {
					String errorMessage = new StringBuilder("the var childMenuList is null but the value referenced by the array size[menu.getChildMenuListSize()][").append(menu.getChildMenuListSize()).append("] is not zero").toString();
					throw new kr.pe.codda.common.exception.BodyFormatException(errorMessage);
				}
			} else {
				int childMenuListSize = childMenuList.size();
				/** 배열 값이 null 이 아닐때에는 배열 크기가 배열 정보에서 지정된 크기와 같은지 검사 */
				if (menu.getChildMenuListSize() != childMenuListSize) {
					String errorMessage = new StringBuilder("the size[").append(childMenuListSize).append("] of the array var childMenuList is not same to the value referenced by the array size[menu.getChildMenuListSize()][").append(menu.getChildMenuListSize()).append("]").toString();
					throw new kr.pe.codda.common.exception.BodyFormatException(errorMessage);
				}
				
				Object childMenuArrayMiddleObject = singleItemEncoder.getArrayMiddleObjectFromWritableMiddleObject(pathStack.peek(), "menu", childMenuListSize, menuMiddleWritableObject);
				for (int i=0; i < childMenuListSize; i++) {
					pathStack.push(new StringBuilder(pathStack.peek()).append(".").append("ChildMenu").append("[").append(i).append("]").toString());
					Object childMenuMiddleWritableObject = singleItemEncoder.getWritableMiddleObjectjFromArrayMiddleObject(pathStack.peek(), childMenuArrayMiddleObject, i);
					TreeSiteMenuRes.Menu childMenu = childMenuList.get(i);

					encodeBody(childMenu, pathStack, singleItemEncoder, childMenuMiddleWritableObject);

					pathStack.pop();
				}
			}
	}

	private void encodeBody(TreeSiteMenuRes treeSiteMenuRes, SingleItemEncoderIF singleItemEncoder, Object middleWritableObject) throws Exception {
		java.util.LinkedList<String> pathStack = new java.util.LinkedList<String>();
		pathStack.push("TreeSiteMenuRes");


		singleItemEncoder.putValueToWritableMiddleObject(pathStack.peek(), "rootMenuListSize"
			, kr.pe.codda.common.type.SingleItemType.INTEGER // itemType
			, treeSiteMenuRes.getRootMenuListSize() // itemValue
			, -1 // itemSize
			, null // nativeItemCharset
			, middleWritableObject);

		java.util.List<TreeSiteMenuRes.Menu> rootMenuList = treeSiteMenuRes.getRootMenuList();

		/** 배열 정보와 배열 크기 일치 검사 */
		if (null == rootMenuList) {
			/** 배열 크기 지정 방식이 간접일 경우 참조하는 변수값이 0 일 경우만 배열 값으로 null 을 허용한다. */
			if (0 != treeSiteMenuRes.getRootMenuListSize()) {
				String errorMessage = new StringBuilder("the var childMenuList is null but the value referenced by the array size[treeSiteMenuRes.getRootMenuListSize()][").append(treeSiteMenuRes.getRootMenuListSize()).append("] is not zero").toString();
				throw new kr.pe.codda.common.exception.BodyFormatException(errorMessage);
			}
		} else {
			int rootMenuListSize = rootMenuList.size();
			/** 배열 값이 null 이 아닐때에는 배열 크기가 배열 정보에서 지정된 크기와 같은지 검사 */
			if (treeSiteMenuRes.getRootMenuListSize() != rootMenuListSize) {
				String errorMessage = new StringBuilder("the size[").append(rootMenuListSize).append("] of the array var rootMenuList is not same to the value referenced by the array size[treeSiteMenuRes.getRootMenuListSize()][").append(treeSiteMenuRes.getRootMenuListSize()).append("]").toString();
				throw new kr.pe.codda.common.exception.BodyFormatException(errorMessage);
			}

			Object rootMenuArrayMiddleObject = singleItemEncoder.getArrayMiddleObjectFromWritableMiddleObject(pathStack.peek(), "menu", rootMenuListSize, middleWritableObject);
			for (int i=0; i < rootMenuListSize; i++) {
				pathStack.push(new StringBuilder(pathStack.peek()).append(".").append("RootMenu").append("[").append(i).append("]").toString());
				Object rootMenuMiddleWritableObject = singleItemEncoder.getWritableMiddleObjectjFromArrayMiddleObject(pathStack.peek(), rootMenuArrayMiddleObject, i);
				TreeSiteMenuRes.Menu rootMenu = rootMenuList.get(i);

				encodeBody(rootMenu, pathStack, singleItemEncoder, rootMenuMiddleWritableObject);

				pathStack.pop();
			}
		}

		pathStack.pop();
	}
}