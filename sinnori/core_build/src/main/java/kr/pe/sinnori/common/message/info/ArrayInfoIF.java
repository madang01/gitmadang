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

package kr.pe.sinnori.common.message.info;

/**
 * 메시지 정보중 배열 정보가 가져야할 메소드들을 정의한 인터페이스
 * 
 * @author Won Jonghoon
 * 
 */
public interface ArrayInfoIF {
	/**
	 * <pre>
	 * 배열의 반복 횟수 지정 방식(cnttype)을 반환한다.
	 * 배열의 반복 횟수 지정 방식은 2가지가 있다.
	 * (1) 직접(direct) : "배열 반복 횟수 값"에는 배열의 반복 횟수 값이 저장되며,
	 * (2) 참조(reference) : "배열 반복 횟수 값"에는 참조하는 항목 이름이 저장된다.
	 * 참고) XML로 작성되는 메시지 정보 파일의 구조를 정의하는 XSD 파일에 정의되어 있다.
	 * </pre>
	 * 
	 * @return 배열의 반복 횟수 지정 방식
	 */
	public String getArrayCntType();

	/**
	 * <pre>
	 * 배열의 반복 횟수 값(cntvalue)을 반환한다.
	 * 배열의 반복 횟수 지정 방식(cnttype)이 직접(direct) 이면 배열 반복 횟수를 반환하며,
	 * 참조(reference)일 경우에는 참조하는 항목 이름을 반환한다.
	 * 참조하는 항목은 숫자형으로 배열과 같은 단계로 반듯이 앞에 나와야 한다.
	 * 이렇게 앞에 나와야 하는 이유는 배열 정보를 읽어와서 배열 정보를 저장하기 전에
	 * 참조 변수가 같은 레벨에서 존재하며 숫자형인지 판단을 하기 위해서이다.
	 * 메시지 정보 파일을 순차적으로 읽기 때문에 배열 뒤에 위치하면 알 수가 없다.
	 * </pre>
	 * 
	 * @return 배열의 반복 횟수 값을 반환한다.
	 */
	public String getArrayCntValue();

	/**
	 * 배열 이름을 반환한다.
	 * 
	 * @return 배열 이름
	 */
	public String getArrayName();
}
