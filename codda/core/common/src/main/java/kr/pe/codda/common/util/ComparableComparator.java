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
package kr.pe.codda.common.util;

import java.io.Serializable;
import java.util.Comparator;

/**
 * 이 소스는 아파치 commons-collections 에서 ComparableComparator.java 를 패키지명만 빼고 그대로 가져온 소스입니다.
 * 아파치 commons-collections 라는 외부 라이브러리에 대한 존경심을 표현하기 위해서 그대로 사용해야 하는것이 맞지만
 * 클라이언트에서 사용할 코어 라이브러리는 외부 라이브러리 의존성 없는 독립성을 가져야
 * Tomcat 같은 곳에서 라이브러리 충돌 없이 무리없이 사용할 수 있기때문에 취한 부득이한 조취입니다.
 * 
 * 원본 참고 주소 : https://commons.apache.org/proper/commons-collections/apidocs/src-html/org/apache/commons/collections4/comparators/ComparableComparator.html
 */
public class ComparableComparator<E extends Comparable<? super E>> implements
		Comparator<E>, Serializable {

	private static final long serialVersionUID = 3857323409902893938L;

	public ComparableComparator() {
		super();
	}

	/** The singleton instance. */
	@SuppressWarnings("rawtypes")
	public static final ComparableComparator INSTANCE = new ComparableComparator();

	@SuppressWarnings("unchecked")
	public static <E extends Comparable<? super E>> ComparableComparator<E> comparableComparator() {
		return INSTANCE;
	}

	@Override
	public int compare(final E o1, final E o2) {
		return o1.compareTo(o2);
	}
}
