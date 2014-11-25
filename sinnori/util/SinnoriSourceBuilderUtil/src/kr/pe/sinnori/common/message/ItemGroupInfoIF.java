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

/**
 * 메시지 항목 그룹 정보가 가져야할 메소드들을 정의하는 인터페이스
 * 
 * @author Won Jonghoon
 * 
 */
public interface ItemGroupInfoIF {
	/**
	 * 순서를 가지는 항목 그룹 정보를 반환한다.
	 * 
	 * @return 순서를 가지는 항목 그룹 정보
	 */
	public ArrayList<AbstractItemInfo> getItemInfoList();

	/**
	 * 항목 그룹에 신규 항목을 추가한다.
	 * 
	 * @param itemInfo
	 *            항목 그룹에 추가되는 신규 항목
	 */
	public void addItemInfo(AbstractItemInfo itemInfo);
	 

	/**
	 * 항목 이름과 1:1 대응되는 항목 정보를 반환한다.
	 * 
	 * @param itemName
	 *            원하는 항목정보의 항목 이름
	 * @return 항목 이름과 1:1 대응되는 항목 정보
	 */
	public AbstractItemInfo getItemInfo(String itemName);
	
	public String getFirstUpperItemName();
	
}
