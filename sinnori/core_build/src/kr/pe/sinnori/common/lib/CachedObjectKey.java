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


public class CachedObjectKey implements CommonRootIF {
	public int classLoaderHashCode = 0;
	public String classFullName = null;
	
	
	/**
	 * 캐쉬된 객체
	 * @param classLoader 클래스 로더
	 * @param classFullName 클래스 이름
	 */
	public CachedObjectKey(int classLoaderHashCode, String classFullName) {
		this.classLoaderHashCode = classLoaderHashCode;
		this.classFullName = classFullName;
	}
		
	@Override
	/**
	 * 주의) 삭제하지 말것.  Hashmap 의 키로 동작 시키기 위해  {@link #equals} 와 {@link #hashCode} 이 필요하다. 
	 */
	public boolean equals(Object compObj) {
		if (null == compObj) return false;
		
		if (!(compObj instanceof CachedObjectKey)) {
			return false;
		}
		
		if(this == compObj) {
			/*Throwable t = new Throwable();
			log.info("same object", t);*/
			return true;
		}
		
		/*Throwable t = new Throwable();
		log.info("not same object", t);*/
	
		CachedObjectKey dstObj = (CachedObjectKey) compObj;
		return (dstObj.classLoaderHashCode == this.classLoaderHashCode && dstObj.classFullName.equals(classFullName));
	}
	
	@Override
	/**
	 * 주의) 삭제하지 말것.  Hashmap 의 키로 동작 시키기 위해  {@link #equals} 와 {@link #hashCode} 이 필요하다.
	 */
	public int hashCode() {
		return (classLoaderHashCode | classFullName.hashCode());
		//return (classFullName.hashCode());
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("KeyObject [classLoader hashCode=");
		builder.append(classLoaderHashCode);
		builder.append(", classFullName=");
		builder.append(classFullName);
		builder.append("]");
		return builder.toString();
	}
}
