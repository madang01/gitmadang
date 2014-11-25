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


/**
 * <pre>
 * 읽기 작업 결과 클래스.  
 * 파일 수정 전후 따질 수 있는 "읽기 작업 마지막 시간"과
 * 파일 수정후 작업을 좀더 편하게 해 줄 수 있는 "읽기 작업 결과로 얻은 인스턴스 객체"를 가지는 클래스이다.
 * "읽기 작업 결과로 얻은 인스턴스 객체"는 현재 기준 2가지가 있다.
 * (1) XML 로 작성되는 메시지 식별자 정보 파일을 다룰때에는 파일 엔트리 클래스(File) 객체
 * (2) 서버 비지니스 로직 클래스 파일을 다룰때에는 서비 비지니스 로직 클래스의 인스턴스 객체
 * </pre> 
 * @author Won Jonghoon
 * 
 */
public class ReadFileInfo {
	/** 읽기 작업 마지막 시간 */
	public Long lastModified;
	
	/** 읽기 작업 결과로 얻은 인스턴스 객체 */
	public Object resultObject = null;

	/**
	 * 생성자
	 * @param resultObject 읽기 작업 결과로 얻은 인스턴스 객체
	 * @param lastModified 읽기 작업 마지막 시간
	 */
	public ReadFileInfo(Object resultObject, long lastModified) {
		this.resultObject = resultObject;
		this.lastModified = lastModified;
	}	
	
	/**
	 * 파일 수정에 따른 재 읽기 작업 결과를 반영한다.
	 * @param newLoadedObject 새로 읽은 작업 결과로 얻은 인스턴스 객체
	 * @param newLastModified 읽기 작업 마지막 시간
	 */
	public void chanageNewClassObject(Object newLoadedObject, long newLastModified) {
		this.resultObject = newLoadedObject;
		this.lastModified = newLastModified;
	}
}
