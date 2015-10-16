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

import kr.pe.sinnori.common.etc.CommonType;


/**
 * 메시지 단일 항목과 배열의 부모 추상화 클래스, 단일 항목과 배열을 단일 개념으로 접근하는데 그 목적이 있다.
 * 
 * @author Won Jonghoon
 * 
 */
public abstract class AbstractItemInfo {
	/**
	 * 항목 이름을 반환한다.
	 * 
	 * @return 항목 이름
	 */
	abstract public String getItemName();
	
	abstract public String getFirstUpperItemName();

	/**
	 * <pre>
	 * 논리적인 항목 구분을 반환한다. 논리적인 항목 구분은 2가지로 구분된다.
	 * (1) 단일 항목 : 컴퓨터 언어가 지원하는 변수와 1:1 매치되어 이진 데이터로 변환을 할 수 있는 항목
	 * (2) 배열 : 반복성을 가지는 항목 그룹을 가지는 항목
	 * </pre>
	 * 
	 * @return 논리적인 항목 구분
	 */
	abstract public CommonType.LOGICAL_ITEM_GUBUN getLogicalItemGubun();
}
