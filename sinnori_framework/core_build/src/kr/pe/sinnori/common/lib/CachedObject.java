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
package kr.pe.sinnori.common.lib;


public class CachedObject implements CommonRootIF {
	public ClassLoader classLoader = null;
	public String classFullName = null;
	public int seq;
	public Object cachedObj = null;
	public long createDate;
	public long updateDate;
	
	/**
	 * 캐쉬된 객체
	 * @param classLoader 클래스 로더
	 * @param classFullName 클래스 이름
	 * @param seq 사용된 시간 개념의 순번
	 * @param cachedObj 지정된 클래스 로더에서 지정된 클래스 이름으로 생성된 객체
	 */
	public CachedObject(ClassLoader classLoader, String classFullName, int seq, Object cachedObj) {
		this.classLoader = classLoader;
		this.classFullName = classFullName;
		this.seq = seq;
		this.cachedObj = cachedObj;
		this.createDate = this.updateDate = new java.util.Date().getTime();
	}
	
	public void updateSeq(int newSeq) {
		this.seq = newSeq;
		this.updateDate = new java.util.Date().getTime();
	}
	
	@Override
	/**
	 * 주의) 삭제하지 말것.  Hashmap 의 키로 동작 시키기 위해  {@link #equals} 와 {@link #hashCode} 이 필요하다. 
	 */
	public boolean equals(Object compObj) {
		if (null == compObj) return false;
		
		if (!(compObj instanceof CachedObject)) {
			return false;
		}
		
		if(this == compObj) {
			/*Throwable t = new Throwable();
			log.info("same object", t);*/
			return true;
		}
		
		/*Throwable t = new Throwable();
		log.info("not same object", t);*/
	
		CachedObject dstObj = (CachedObject) compObj;
		return (dstObj.classLoader.equals(classLoader) && dstObj.classFullName.equals(classFullName));
	}
	
	@Override
	/**
	 * 주의) 삭제하지 말것.  Hashmap 의 키로 동작 시키기 위해  {@link #equals} 와 {@link #hashCode} 이 필요하다.
	 */
	public int hashCode() {
		return (classLoader.hashCode() | classFullName.hashCode());
		//return (classFullName.hashCode());
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("KeyObject [classLoader hashCode=");
		builder.append(classLoader.hashCode());
		builder.append(", classFullName=");
		builder.append(classFullName);
		builder.append(", seq=");
		builder.append(seq);
		builder.append(", cachedObj hashCode=");
		builder.append(cachedObj.hashCode());
		builder.append("]");
		return builder.toString();
	}
}
