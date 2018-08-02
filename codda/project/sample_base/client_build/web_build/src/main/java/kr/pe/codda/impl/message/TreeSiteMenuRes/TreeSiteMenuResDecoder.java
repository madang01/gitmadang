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

import kr.pe.codda.common.exception.BodyFormatException;
import kr.pe.codda.common.message.AbstractMessage;
import kr.pe.codda.common.message.codec.AbstractMessageDecoder;
import kr.pe.codda.common.protocol.SingleItemDecoderIF;

/**
 * TreeSiteMenuRes message decoder
 * @author Won Jonghoon
 *
 */
public final class TreeSiteMenuResDecoder extends AbstractMessageDecoder {
	
	private TreeSiteMenuRes.Menu decodeBody(TreeSiteMenuRes.Menu menu, java.util.LinkedList<String> pathStack, SingleItemDecoderIF singleItemDecoder, Object  menuMiddleReadableObject) throws BodyFormatException {
		// TreeSiteMenuRes.Menu menu = new TreeSiteMenuRes.Menu();
		
		menu.setMenuNo((Long)
				singleItemDecoder.getValueFromReadableMiddleObject(pathStack.peek()
					, "menuNo" // itemName
					, kr.pe.codda.common.type.SingleItemType.UNSIGNED_INTEGER // itemType
					, -1 // itemSize
					, null // nativeItemCharset
					, menuMiddleReadableObject));

		menu.setParentNo((Long)
				singleItemDecoder.getValueFromReadableMiddleObject(pathStack.peek()
					, "parentNo" // itemName
					, kr.pe.codda.common.type.SingleItemType.UNSIGNED_INTEGER // itemType
					, -1 // itemSize
					, null // nativeItemCharset
					, menuMiddleReadableObject));

		menu.setDepth((Short)
				singleItemDecoder.getValueFromReadableMiddleObject(pathStack.peek()
					, "depth" // itemName
					, kr.pe.codda.common.type.SingleItemType.UNSIGNED_BYTE // itemType
					, -1 // itemSize
					, null // nativeItemCharset
					, menuMiddleReadableObject));

		menu.setOrderSeq((Short)
				singleItemDecoder.getValueFromReadableMiddleObject(pathStack.peek()
					, "orderSeq" // itemName
					, kr.pe.codda.common.type.SingleItemType.UNSIGNED_BYTE // itemType
					, -1 // itemSize
					, null // nativeItemCharset
					, menuMiddleReadableObject));

		menu.setMenuName((String)
				singleItemDecoder.getValueFromReadableMiddleObject(pathStack.peek()
					, "menuName" // itemName
					, kr.pe.codda.common.type.SingleItemType.UB_PASCAL_STRING // itemType
					, -1 // itemSize
					, null // nativeItemCharset
					, menuMiddleReadableObject));

		menu.setLinkURL((String)
				singleItemDecoder.getValueFromReadableMiddleObject(pathStack.peek()
					, "linkURL" // itemName
					, kr.pe.codda.common.type.SingleItemType.UB_PASCAL_STRING // itemType
					, -1 // itemSize
					, null // nativeItemCharset
					, menuMiddleReadableObject));

		menu.setChildMenuListSize((Integer)
				singleItemDecoder.getValueFromReadableMiddleObject(pathStack.peek()
					, "childMenuListSize" // itemName
					, kr.pe.codda.common.type.SingleItemType.INTEGER // itemType
					, -1 // itemSize
					, null // nativeItemCharset
					, menuMiddleReadableObject));
		
		int childMenuListSize = menu.getChildMenuListSize();
		Object childMenuArrayMiddleObject = singleItemDecoder.getArrayMiddleObjectFromReadableMiddleObject(pathStack.peek(), "menu", childMenuListSize, menuMiddleReadableObject);
		java.util.List<TreeSiteMenuRes.Menu> childMenuList = new java.util.ArrayList<TreeSiteMenuRes.Menu>();
		for (int i=0; i < childMenuListSize; i++) {
			pathStack.push(new StringBuilder(pathStack.peek()).append(".").append("ChildMenu").append("[").append(i).append("]").toString());
			Object childMenuMiddleWritableObject= singleItemDecoder.getReadableMiddleObjFromArrayMiddleObject(pathStack.peek(), childMenuArrayMiddleObject, i);
			TreeSiteMenuRes.Menu childMenu = decodeBody(new TreeSiteMenuRes.Menu(), pathStack, singleItemDecoder,  childMenuMiddleWritableObject);
			childMenuList.add(childMenu);			

			pathStack.pop();
		}

		menu.setChildMenuList(childMenuList);
				
		return menu;
	}

	@Override
	protected AbstractMessage decodeBody(SingleItemDecoderIF singleItemDecoder, Object  middleReadableObject) throws BodyFormatException {
		TreeSiteMenuRes treeSiteMenuRes = new TreeSiteMenuRes();
		java.util.LinkedList<String> pathStack = new java.util.LinkedList<String>();
		pathStack.push("TreeSiteMenuRes");

		treeSiteMenuRes.setRootMenuListSize((Integer)
		singleItemDecoder.getValueFromReadableMiddleObject(pathStack.peek()
			, "rootMenuListSize" // itemName
			, kr.pe.codda.common.type.SingleItemType.INTEGER // itemType
			, -1 // itemSize
			, null // nativeItemCharset
			, middleReadableObject));

		int rootMenuListSize = treeSiteMenuRes.getRootMenuListSize();
		Object rootMenuArrayMiddleObject = singleItemDecoder.getArrayMiddleObjectFromReadableMiddleObject(pathStack.peek(), "menu", rootMenuListSize, middleReadableObject);
		java.util.List<TreeSiteMenuRes.Menu> rootMenuList = new java.util.ArrayList<TreeSiteMenuRes.Menu>();
		for (int i=0; i < rootMenuListSize; i++) {
			pathStack.push(new StringBuilder(pathStack.peek()).append(".").append("RootMenu").append("[").append(i).append("]").toString());
			Object rootMenuMiddleWritableObject= singleItemDecoder.getReadableMiddleObjFromArrayMiddleObject(pathStack.peek(), rootMenuArrayMiddleObject, i);
			TreeSiteMenuRes.Menu rootMenu = decodeBody(new TreeSiteMenuRes.Menu(), pathStack, singleItemDecoder,  rootMenuMiddleWritableObject);
			rootMenuList.add(rootMenu);			

			pathStack.pop();
		}

		treeSiteMenuRes.setRootMenuList(rootMenuList);

		pathStack.pop();

		return treeSiteMenuRes;
	}
}