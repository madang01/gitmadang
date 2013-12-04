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

import kr.pe.sinnori.common.exception.MessageItemException;
import kr.pe.sinnori.common.lib.CommonRootIF;

/**
 * 메시지 배열 구현 클래스.
 * 
 * <pre>
 * 배열 정보를 이용하여 배열에 속한 항목 그룹의 목록 생성하며 지정된 위치의 항목 그룹 반환을 주 목적으로 한다.
 * 
 * 참고) 메시지 표현 정규식
 * 메시지 = 메시지 식별자, 항목 그룹
 * 항목 그룹 = (항목)*
 * 항목 = (단일 항목 | 배열)
 * 단일 항목 = 이름, 타입, 타입 부가 정보{0..1}, 값
 * 타입 부가 정보 = 크기 | 문자셋
 * 배열 = 이름, 반복 횟수, (항목 그룹)
 * </pre>
 * 
 * @author Jonghoon Won
 * 
 */
public class ArrayData implements CommonRootIF {
	private ArrayList<ItemGroupDataIF> itemGroupData = new ArrayList<ItemGroupDataIF>();
	private ArrayList<AbstractItemInfo> itemGroupInfo = null;

	private ArrayInfo arrayInfo = null;

	private String parentPath = null;

	private ItemGroupDataIF parent = null;

	private String arrayName = null;
	private String arrayCntType = null;
	private String arrayCntValue = null;
	private int arraySize = 0;

	/**
	 * 배열을 구현하는 클래스 생성자. 배열명 배열
	 * 
	 * @param parentPath
	 *            부모가 속한 계층 구조를 표현하는 문자열
	 * @param parent
	 *            부모인 항목 그룹 객체. 배열의 크기가 부모가 가지고 있는 항목의 값을 참조 할 경우 사용된다.
	 * @param arrayInfo
	 *            배열 정보. 배열에 속한 항목 그룹에 대한 정보를 바탕으로 항목 그룹에 대한 접근을
	 */
	public ArrayData(String parentPath, ItemGroupDataIF parent,
			ArrayInfo arrayInfo) {
		// System.out.printf("1.path=[%s], arrayName=[%s]\n", path,
		// arrayInfo.getArrayName());
		this.parentPath = parentPath;
		this.arrayInfo = arrayInfo;
		this.parent = parent;

		arrayName = arrayInfo.getArrayName();
		arrayCntType = arrayInfo.getArrayCntType();
		arrayCntValue = arrayInfo.getArrayCntValue();
		if (arrayCntType.equals("direct")) {
			try {
				arraySize = Integer.parseInt(arrayCntValue);

				if (arraySize < 0) {
					String errorMessage = String.format(
							"%s 배열[%s] 에서 직접 입력된 배열 크기[%s] 가 0 보다 작습니다.",
							parentPath, arrayName, arrayCntValue);
					throw new RuntimeException(errorMessage);
				}
			} catch (NumberFormatException num_e) {
				String errorMessage = String.format(
						"%s 배열[%s] 에서 직접 입력된 배열 크기[%s] 가 숫자가 아닙니다.",
						parentPath, arrayName, arrayCntValue);
				throw new RuntimeException(errorMessage);
			}

		} else {
			try {
				arraySize = (Integer) this.parent.getAttribute(arrayCntValue);
			} catch (MessageItemException e) {
				log.fatal(e.getMessage(), e);
				System.exit(1);
			}
			if (arraySize < 0) {
				String errorMessage = String.format(
						"%s 배열[%s] 에서  참조 변수로 지정된 배열 크기[%d] 가 0 보다 작습니다.",
						parentPath, arrayName, arraySize);
				throw new RuntimeException(errorMessage);
			}
		}

		for (int i = 0; i < arraySize; i++) {
			ItemGroupDataOfArray itemGroupDataOfArray = new ItemGroupDataOfArray(
					parentPath, itemGroupData.size(), arrayInfo);
			itemGroupData.add(itemGroupDataOfArray);
		}
		
		itemGroupInfo = this.arrayInfo.getItemInfoList();
	}

	/**
	 * 배열 크기 반환
	 * 
	 * @return 배열 크기
	 */
	public int size() {
		return arraySize;
	}

	/**
	 * 항목 그룹 내용 목록에서 지정된 위치의 항목 그룹 내용을 반환한다.
	 * 
	 * @param index
	 *            항목 그룹 내용 목록에서 원하는 항목 그룹 내용의 위치
	 * @return 지정된 위치의 항목 그룹 내용
	 * @throws IndexOutOfBoundsException
	 *             지정된 위치가 항목 그룹 내용 목록의 범위를 벗어날 경우 발생.
	 */
	public ItemGroupDataIF get(int index)
			throws IndexOutOfBoundsException {
		return itemGroupData.get(index);
	}

	/**
	 * 부모 경로를 반환한다.
	 * 
	 * @return 부모 경로
	 */
	public String getParentPath() {
		return parentPath;
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
	public String toString() {
		int itemGroupDataListSize = itemGroupData.size();

		StringBuilder strBuilder = new StringBuilder();

		if (0 == itemGroupDataListSize) {
			strBuilder.append(parentPath);
			strBuilder.append(".");
			strBuilder.append(arrayName);
			strBuilder.append("=<empty>");
			return strBuilder.toString();
		}

		// ArrayList<String> keyList = arrayInfo.getKeyList();
		// int keySize = keyList.size();

		
		
		int itemGroupSize = itemGroupInfo.size();
		for (int i = 0; i < itemGroupDataListSize; i++) {
			if (i > 0) {
				strBuilder.append(", ");
			}

			strBuilder.append("{\n");

			ItemGroupDataIF itemGroupDataOfArray = itemGroupData.get(i);

			for (int j = 0; j < itemGroupSize; j++) {
			// for (int j = 0; itemGroupInfo.hasNext(); j++) {
				if (j > 0) {
					strBuilder.append(",\n");
				}

				strBuilder.append(arrayName);
				strBuilder.append("[");
				strBuilder.append(i);
				strBuilder.append("].");

				// String key = keyList.get(j);
				String key = itemGroupInfo.get(j).getItemName();
				// String key = itemGroupInfo.next().getItemName();

				strBuilder.append(key);
				strBuilder.append("=");

				Object value = null;
				try {
					value = itemGroupDataOfArray.getAttribute(key);
				} catch (MessageItemException e) {
					log.fatal(e.getMessage(), e);
					System.exit(1);
				}

				if (value == null) {
					strBuilder.append("null");
					continue;
				}

				if (value instanceof ArrayData) {
					/**
					 * 배열 데이터(=ArrayData) 는 항목 그룹(=ItemGroupDataIF)을 원소로 가지는 목록을 관리하는 클래스이다.
					 * 배열 데이터의 원소인 항목 그룹에 toString 을 위임하지 않고 
					 * 직접적으로 toString 구현한 이유는 
					 * 항목 그룹은 항목 그룹 목록안에 몇번째에 속한지를 알 수 없기 때문에
					 * 항목 그룹에 속한 항목마다 접두어로 <배열명>[index] 를 넣어줄 수 없다.
					 * 오직 항목 그룹을 관리하는 배열 데이터만이 항목 그룹이 몇번째 인지를 알 수 있으므로,
					 * 배열 데이터(=ArrayData) 에서 toString 을 구현한다.
					 */
					ArrayData arrayData = (ArrayData) value;
					strBuilder.append(arrayData.toString());
				} else if (value instanceof byte[]) {

					strBuilder.append("{");
					byte arrayValue[] = (byte[]) value;
					for (int k = 0; k < arrayValue.length; k++) {
						if (k > 0)
							strBuilder.append(",");
						strBuilder.append(arrayValue[k]);
					}
					strBuilder.append("}");
				} else {
					strBuilder.append(value);
				}
			}

			strBuilder.append("}");
		}
		return strBuilder.toString();
	}
}
