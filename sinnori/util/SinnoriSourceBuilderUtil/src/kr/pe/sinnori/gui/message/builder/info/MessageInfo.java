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

package kr.pe.sinnori.gui.message.builder.info;


import java.util.ArrayList;
import java.util.HashMap;

import kr.pe.sinnori.common.lib.CommonType;



/**
 * 메시지 정보 클래스.<br/>
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
 * @author Won Jonghoon
 * 
 */
public class MessageInfo implements ItemGroupInfoIF {
	private String messageID = null;
	private java.util.Date lastModfied;
	private String firstUpperMessageID = null;
	private CommonType.MESSAGE_TRANSFER_DIRECTION direction = null;
	
	private ArrayList<AbstractItemInfo> itemInfoList = new ArrayList<AbstractItemInfo>();
	private HashMap<String, AbstractItemInfo> itemInfoHash = new HashMap<String, AbstractItemInfo>();
		

	public MessageInfo(String messageID, long lastModfied) {		
		this.messageID = messageID;
		this.lastModfied = new java.util.Date(lastModfied);
		this.firstUpperMessageID = messageID.substring(0,1)+messageID.substring(1);
	}

	public String getMessageID() {
		return messageID;
	}
	
	public java.util.Date getLastModified() {
		return lastModfied;
	}
	
	public void setDirection(CommonType.MESSAGE_TRANSFER_DIRECTION direction) {
		this.direction = direction;
	}
	
	public CommonType.MESSAGE_TRANSFER_DIRECTION getDirection() {
		return direction;
	}
	

	/******************* ItemGroupInfoIF start ***********************/
	@Override
	public ArrayList<AbstractItemInfo> getItemInfoList() {
		return itemInfoList;
	}

	@Override
	public void addItemInfo(AbstractItemInfo itemInfo) {
		itemInfoList.add(itemInfo);
		itemInfoHash.put(itemInfo.getItemName(), itemInfo);
	}

	@Override
	public AbstractItemInfo getItemInfo(String itemName) {

		return itemInfoHash.get(itemName);
	}

	/******************* ItemGroupInfoIF end ***********************/

	public String toString() {
		StringBuffer strBuff = new StringBuffer();
		strBuff.append("{ messageID=");
		strBuff.append(messageID);
		strBuff.append(", direction=");
		strBuff.append(direction.toString());
		strBuff.append(", {");

		// Iterator<AbstractItemInfo> itemInfoIter = getItemInfoList();
		int itemInfoSize = itemInfoList.size();
		for (int i = 0; i < itemInfoSize; i++) {
		// for (int i = 0; itemInfoIter.hasNext(); i++) {
			if (i > 0) {
				strBuff.append(",");
				strBuff.append("\n");
			}
			strBuff.append("순서[");
			strBuff.append(i);
			strBuff.append("]=");

			AbstractItemInfo itemInfo = itemInfoList.get(i);
			// AbstractItemInfo itemInfo = itemInfoIter.next();
			
			CommonType.LOGICAL_ITEM_GUBUN logicalItemGubun = itemInfo
					.getLogicalItemGubun();
			if (CommonType.LOGICAL_ITEM_GUBUN.SINGLE_ITEM == logicalItemGubun) {
				SingleItemInfo singleItemInfo = (SingleItemInfo) itemInfo;
				strBuff.append(singleItemInfo.toString());
			} else {
				ArrayInfo arrayInfo = (ArrayInfo) itemInfo;
				strBuff.append(arrayInfo.toString());
			}
		}

		strBuff.append("}}");

		return strBuff.toString();
	}

	@Override
	public String getFirstUpperItemName() {
		return firstUpperMessageID;
	}
}
