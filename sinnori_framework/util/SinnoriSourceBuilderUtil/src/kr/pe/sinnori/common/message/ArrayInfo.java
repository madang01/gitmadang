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

import java.util.ArrayList;
import java.util.HashMap;

import kr.pe.sinnori.common.lib.CommonType;


/**
 * 배열 정보 클래스. 배열 이름, 배열 크기, 배열에 속한 항목 그룹 정보를 가지고 있다.
 * 
 * @author Won Jonghoon
 * 
 */
public class ArrayInfo extends AbstractItemInfo implements
		ItemGroupInfoIF {
	private ArrayList<AbstractItemInfo> itemGroupInfoOfArray = new ArrayList<AbstractItemInfo>();
	private HashMap<String, AbstractItemInfo> itemInfoHash = new HashMap<String, AbstractItemInfo>();

	private String arrayName = null;
	
	private String arrayFirstUpperName = null;
	/**
	 * 배열의 반복 횟수 지정 방식(cnttype)은 2가지가 있다.<br/>
	 * (1) 직접(direct) : 고정 크기 지정방식으로 배열 반복 횟수에는 배열의 반복 횟수 값이 저장되며,<br/>
	 * (2) 참조(reference) : 가변 크기 지정방식으로 배열 반복 횟수는 참조하는 항목의 값이다.
	 */
	private String arrayCntType = null;
	/**
	 * 배열의 반복 횟수(cntvalue) "배열의 반복 횟수 지정 방식"이 직접(direct) 이면 배열 반복 횟수를 반환하며,<br/>
	 * 참조(reference)일 경우에는 참조하는 항목 이름을 반환한다.<br/>
	 * 참조하는 항목은 숫자형으로 배열과 같은 단계로 반듯이 앞에 나와야 한다.<br/>
	 * 이렇게 앞에 나와야 하는 이유는 배열 정보를 읽어와서 <br/>
	 * 배열 정보를 저장하기 전에 참조 변수가 같은 레벨에서 존재하며 숫자형인지 판단을 하기 위해서이다.<br/>
	 * 메시지 정보 파일을 순차적으로 읽기 때문에 배열 뒤에 위치하면 알 수가 없다
	 */
	private String arrayCntValue = null;

	/**
	 * 생성자
	 * 
	 * @param arrayName
	 *            배열 이름
	 * @param arrayCntType
	 *            배열의 반복 횟수 지정 방식
	 * @param arrayCntValue
	 *            배열 반복 횟수
	 */
	public ArrayInfo(String arrayName, String arrayCntType, String arrayCntValue) {
		this.arrayName = arrayName;
		this.arrayFirstUpperName = arrayName.substring(0, 1).toUpperCase() + arrayName.substring(1);
		this.arrayCntType = arrayCntType;
		this.arrayCntValue = arrayCntValue;
	}

	/**
	 * 배열의 반복 횟수 지정 방식을 반환한다.
	 * 
	 * @return 배열의 반복 횟수 지정 방식
	 */
	public String getArrayCntType() {
		return arrayCntType;
	}

	/**
	 * 배열 반복 횟수를 반환한다.
	 * 
	 * @return 배열 반복 횟수
	 */
	public String getArrayCntValue() {
		return arrayCntValue;
	}

	/**
	 * 배열 이름을 반환한다.
	 * 
	 * @return 배열 이름
	 */
	public String getArrayName() {
		return arrayName;
	}
	
	
	@Override
	public String getFirstUpperItemName() {
		return arrayFirstUpperName;
	}
	

	@Override
	public String toString() {
		StringBuffer strBuff = new StringBuffer();
		strBuff.append("{ arrayName=[");
		strBuff.append(arrayName);
		strBuff.append("], arrayCntType=[");
		strBuff.append(arrayCntType);
		strBuff.append("], arrayCntValue=[");
		strBuff.append(arrayCntValue);
		strBuff.append("], {\n");

		// Iterator<AbstractItemInfo> itemInfoIter = getItemInfoList();
		
		int itemInfoSize = itemGroupInfoOfArray.size();
		for (int i = 0; i < itemInfoSize; i++) {
		// for (int i = 0; itemInfoIter.hasNext(); i++) {
			if (i > 0) {
				strBuff.append(",");
				strBuff.append("\n");
			}
			// arrayName
			strBuff.append(arrayName);
			strBuff.append("[");
			strBuff.append(i);
			strBuff.append("]=");
			
			AbstractItemInfo itemInfo = itemGroupInfoOfArray.get(i);
			// AbstractItemInfo itemInfo = itemInfoIter.next();
			CommonType.LOGICAL_ITEM_GUBUN logicalItemGubun = itemInfo.getLogicalItemGubun();
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

	/******************* ItemGroupInfoIF start ***********************/
	@Override
	public ArrayList<AbstractItemInfo> getItemInfoList() {
		// return itemInfoHash.values().iterator();
		// Throwable t = new Throwable();
		// Iterator<AbstractItemInfo> retIter = itemInfoHash.values().iterator();
		//log.info(String.format("itemInfoHash size=[%d], hasNext=[%s]", itemInfoHash.values().size(), retIter.hasNext()), t);
		
		// return retIter;
		
		return itemGroupInfoOfArray;
	}

	@Override
	public void addItemInfo(AbstractItemInfo itemInfo) {
		itemGroupInfoOfArray.add(itemInfo);
		itemInfoHash.put(itemInfo.getItemName(), itemInfo);
	}

	@Override
	public AbstractItemInfo getItemInfo(String itemName) {
		return itemInfoHash.get(itemName);
	}

	/******************* ItemGroupInfoIF end ***********************/

	/******************* AbstractItemInfo start ***********************/
	@Override
	public String getItemName() {
		// TODO Auto-generated method stub
		return arrayName;
	}

	@Override
	public CommonType.LOGICAL_ITEM_GUBUN getLogicalItemGubun() {
		// TODO Auto-generated method stub
		return CommonType.LOGICAL_ITEM_GUBUN.ARRAY_ITEM;
	}
	/******************* AbstractItemInfo end ***********************/

	

}
